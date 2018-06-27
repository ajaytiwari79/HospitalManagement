package com.kairos.service.jackrabbit_service;


import com.kairos.custom_exception.ClauseNotFoundJackRabbitException;
import com.kairos.custom_exception.JackrabbitNodeNotFoundException;
import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.repository.agreement_template.AgreementSectionMongoRepository;
import com.kairos.persistance.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.persistance.repository.clause.ClauseMongoRepository;
import com.kairos.persistance.repository.clause_tag.ClauseTagMongoRepository;
import com.kairos.response.dto.master_data.AgreementSectionResponseDTO;
import com.kairos.response.dto.clause.ClauseBasicResponseDTO;
import com.kairos.utils.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.jcr.*;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static com.kairos.constants.JackRabbitConstant.CLAUSE_PARENT_NODE;
import static com.kairos.constants.JackRabbitConstant.NODE_TYPE_UNSTRUCTURED;
import static com.kairos.constants.JackRabbitConstant.CLAUSE_CHILD_NODE;
import static com.kairos.constants.JackRabbitConstant.AGREEMENT_TEMPLATE_PARENT_NODE;
import static com.kairos.constants.JackRabbitConstant.AGREEMENT_TEMPLATE_CHILD_NODE;
import static com.kairos.constants.JackRabbitConstant.JCR_CONTENT;
import static com.kairos.constants.JackRabbitConstant.NODE_TYPE_RESOURCE;
import static com.kairos.constants.JackRabbitConstant.JCR_DATA;


@Component
public class JackrabbitService {

    @Inject
    private Repository repository;


    @Inject
    private ClauseMongoRepository clauseMongoRepository;

    @Inject
    private ClauseTagMongoRepository clauseTagMongoRepository;

    @Inject
    private PolicyAgreementTemplateRepository policyAgreementTemplateRepository;

    @Inject
    private AgreementSectionMongoRepository agreementSectionMongoRepository;


    private static final Logger logger = LoggerFactory.getLogger(JackrabbitService.class);

    public Boolean addClauseNodeToJackrabbit(BigInteger id, Clause clause) throws RepositoryException {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));

        try {

            String file = clauseFileContent(clause);
            InputStream inputStream = new ByteArrayInputStream(file.getBytes());

            Node rootNode = session.getRootNode();
            Node parentClauseNode;
            if (!rootNode.hasNode(CLAUSE_PARENT_NODE)) {
                parentClauseNode = rootNode.addNode(CLAUSE_PARENT_NODE, NODE_TYPE_UNSTRUCTURED);
                parentClauseNode.setProperty("jcr:lastModified", Calendar.getInstance().getTimeInMillis());
            }
            parentClauseNode = rootNode.getNode(CLAUSE_PARENT_NODE);
            Node clauseVersioningNode = parentClauseNode.addNode(CLAUSE_CHILD_NODE + id, "nt:unstructured");
            clauseVersioningNode.addMixin("mix:versionable");
            clauseVersioningNode.setProperty("jcr:lastModified", Calendar.getInstance().getTimeInMillis());
            clauseVersioningNode.setProperty("title", clause.getTitle());

            Node content = clauseVersioningNode.addNode(JCR_CONTENT, NODE_TYPE_RESOURCE);
            Binary binary = session.getValueFactory().createBinary(inputStream);
            content.setProperty(JCR_DATA, binary);
            session.save();
            clauseVersioningNode.checkin();
            return true;
        } catch (RepositoryException e) {
            clauseMongoRepository.delete(clause);
            clauseTagMongoRepository.deleteAll(clause.getTags());
            logger.warn(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            clauseMongoRepository.delete(clause);
            clauseTagMongoRepository.deleteAll(clause.getTags());
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            session.logout();
        }
        return false;

    }


    public Boolean clauseVersioning(BigInteger clauseid, Clause clause) throws RepositoryException {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        try {


            String file = clauseFileContent(clause);
            InputStream inputStream = new ByteArrayInputStream(file.getBytes());
            Node rootNode = session.getRootNode();
            Node clauseNode = rootNode.getNode(CLAUSE_PARENT_NODE + "/" + CLAUSE_CHILD_NODE + clauseid);
            if (clauseNode.isNode()) {
                clauseNode.checkout();
                clauseNode.setProperty("jcr:lastModified", Calendar.getInstance().getTimeInMillis());
                clauseNode.setProperty("id", clause.getId().toString());
                clauseNode.setProperty("title", clause.getTitle());

                Node contentNode = clauseNode.getNode(JCR_CONTENT);
                if (!contentNode.isNode()) {
                    throw new JackrabbitNodeNotFoundException("clause not found");
                }
                Binary binary = session.getValueFactory().createBinary(inputStream);
                contentNode.setProperty(JCR_DATA, binary);
                session.save();
                clauseNode.checkin();

                return true;
            }
            throw new ClauseNotFoundJackRabbitException("clause versioning node not found in jackrbbit for id" + clause.getId());


        } catch (RepositoryException e) {

            logger.warn(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {

            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            session.logout();
        }
        return false;
    }


    public StringBuffer getClauseVersion(BigInteger id, String version) throws RepositoryException {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        StringBuffer result = new StringBuffer();
        BufferedReader bufferedReader;
        try {
            Node clauseVersinongNode = session.getRootNode().getNode(CLAUSE_PARENT_NODE + "/" + CLAUSE_CHILD_NODE + id);
            Node currentNode;
            Version version1 = clauseVersinongNode.getVersionHistory().getVersion(version);
            NodeIterator nodeIterator = version1.getNodes();
            while (nodeIterator.hasNext()) {
                currentNode = (Node) nodeIterator.next();
                bufferedReader = new BufferedReader(new InputStreamReader(currentNode.getNode(Node.JCR_CONTENT).getProperty(JCR_DATA).getStream()));
                String s = bufferedReader.readLine();

                while (s != null) {

                    result.append(s);
                    s = bufferedReader.readLine();


                }
                currentNode.getNode(Node.JCR_CONTENT).getProperty(JCR_DATA).getBinary().dispose();

            }

        } catch (Exception e) {

            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            session.logout();
        }
        return result;

    }


    public List<String> getClauseVersions(BigInteger id) throws RepositoryException {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        List<String> versionList = new ArrayList<>();
        try {
            Node clauseVersinongNode = session.getRootNode().getNode(CLAUSE_PARENT_NODE + "/" + CLAUSE_CHILD_NODE + id);
            VersionManager versionManager = session.getWorkspace().getVersionManager();
            VersionIterator versions = versionManager.getVersionHistory(clauseVersinongNode.getPath()).getAllVersions();
            versions.skip(1);
            while (versions.hasNext()) {
                versionList.add(versions.nextVersion().getName());
            }
            return versionList;
        } catch (Exception e) {

            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            session.logout();
        }
        return null;

    }


    public Boolean addAgreementTemplateJackrabbit(BigInteger id, PolicyAgreementTemplate agreementTemplate, List<AgreementSectionResponseDTO> sectionResponseDto) throws RepositoryException {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        try {
            String file = policyAgreementTemplateFileContent(sectionResponseDto, agreementTemplate);
            InputStream targetStream = new ByteArrayInputStream(file.getBytes());
            Node rootNode = session.getRootNode();
            Node parentClauseNode;
            if (!rootNode.hasNode(AGREEMENT_TEMPLATE_PARENT_NODE)) {
                parentClauseNode = rootNode.addNode(AGREEMENT_TEMPLATE_PARENT_NODE, NODE_TYPE_UNSTRUCTURED);
                parentClauseNode.setProperty("jcr:lastModified", Calendar.getInstance().getTimeInMillis());

            }
            parentClauseNode = rootNode.getNode(AGREEMENT_TEMPLATE_PARENT_NODE);
            Node agreementTemplateVersioningNode = parentClauseNode.addNode(AGREEMENT_TEMPLATE_CHILD_NODE + id, "nt:unstructured");
            agreementTemplateVersioningNode.addMixin("mix:versionable");
            agreementTemplateVersioningNode.setProperty("jcr:lastModified", Calendar.getInstance().getTimeInMillis());
            agreementTemplateVersioningNode.setProperty("name", agreementTemplate.getName());
            Node content = agreementTemplateVersioningNode.addNode(JCR_CONTENT, NODE_TYPE_RESOURCE);
            Binary binary = session.getValueFactory().createBinary(targetStream);
            content.setProperty(JCR_DATA, binary);
            targetStream.close();
            session.save();
            agreementTemplateVersioningNode.checkin();
            return true;

        } catch (RepositoryException e) {
            agreementSectionMongoRepository.deleteAll(agreementSectionMongoRepository.findAgreementSectionByIds(UserContext.getCountryId(),agreementTemplate.getAgreementSections()));
            policyAgreementTemplateRepository.delete(agreementTemplate);
            logger.warn(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            agreementSectionMongoRepository.deleteAll(agreementSectionMongoRepository.findAgreementSectionByIds(UserContext.getCountryId(),agreementTemplate.getAgreementSections()));
            policyAgreementTemplateRepository.delete(agreementTemplate);
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            session.logout();
        }
        return false;

    }


    public Boolean agreementTemplateVersioning(BigInteger id, PolicyAgreementTemplate agreementTemplate, List<AgreementSectionResponseDTO> sectionResponseDto) throws RepositoryException {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        try {
            String content = policyAgreementTemplateFileContent(sectionResponseDto, agreementTemplate);
            InputStream targetStream = new ByteArrayInputStream(content.getBytes());
            Node rootNode = session.getRootNode();
            Node agreementTemplateVersionNode = rootNode.getNode(AGREEMENT_TEMPLATE_PARENT_NODE + "/" + AGREEMENT_TEMPLATE_CHILD_NODE + id);
            logger.info("clausenode.isnode()  " + agreementTemplateVersionNode.isNode());
            if (agreementTemplateVersionNode.isNode()) {
                agreementTemplateVersionNode.checkout();
                agreementTemplateVersionNode.setProperty("jcr:lastModified", Calendar.getInstance().getTimeInMillis());
                agreementTemplateVersionNode.setProperty("name", agreementTemplate.getName());
                Node contentNode = agreementTemplateVersionNode.getNode(JCR_CONTENT);
                if (!contentNode.isNode()) {
                    throw new JackrabbitNodeNotFoundException("policy document template file Node not found");
                }
                Binary binary = session.getValueFactory().createBinary(targetStream);
                contentNode.setProperty(JCR_DATA, binary);
                session.save();
                agreementTemplateVersionNode.checkin();
                return true;
            }
            throw new ClauseNotFoundJackRabbitException("clause versioning node not found in jackrbbit for id" + agreementTemplate.getId());
        } catch (RepositoryException e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {

            session.logout();
        }
        return false;
    }


    public List<String> getPolicyTemplateVersions(BigInteger id) throws RepositoryException {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        List<String> versionList = new ArrayList<>();
        try {
            Node clauseVersinongNode = session.getRootNode().getNode(AGREEMENT_TEMPLATE_PARENT_NODE + "/" + AGREEMENT_TEMPLATE_CHILD_NODE + id);
            VersionManager versionManager = session.getWorkspace().getVersionManager();
            VersionIterator versions = versionManager.getVersionHistory(clauseVersinongNode.getPath()).getAllVersions();
            versions.skip(1);
            while (versions.hasNext()) {
                versionList.add(versions.nextVersion().getName());
            }
            return versionList;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            session.logout();
        }
        return null;
    }


    public StringBuffer getpolicyTemplateVersion(BigInteger id, String version) throws RepositoryException {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        StringBuffer result = new StringBuffer();
        BufferedReader bufferedReader;
        try {
            Node policyAgreementVersionNode = session.getRootNode().getNode(AGREEMENT_TEMPLATE_PARENT_NODE + "/" + AGREEMENT_TEMPLATE_CHILD_NODE + id);
            Node currentNode;
            Version version1 = policyAgreementVersionNode.getVersionHistory().getVersion(version);
            NodeIterator nodeIterator = version1.getNodes();
            while (nodeIterator.hasNext()) {
                currentNode = (Node) nodeIterator.next();
                bufferedReader = new BufferedReader(new InputStreamReader(currentNode.getNode(Node.JCR_CONTENT).getProperty(JCR_DATA).getStream()));
                String s = bufferedReader.readLine();
                while (s != null) {
                    result.append(s);
                    s = bufferedReader.readLine();
                }
                currentNode.getNode(Node.JCR_CONTENT).getProperty(JCR_DATA).getBinary().dispose();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            session.logout();
        }
        return result;

    }


    public String getpolicyTemplateBaseVersion(BigInteger id) throws RepositoryException {

        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        String result = new String();
        try {
            Node policyAgreementVersionNode = session.getRootNode().getNode(AGREEMENT_TEMPLATE_PARENT_NODE + "/" + AGREEMENT_TEMPLATE_CHILD_NODE + id);
            Node currentNode;
            Version version1 = policyAgreementVersionNode.getBaseVersion();
            NodeIterator nodeIterator = version1.getNodes();
            while (nodeIterator.hasNext()) {
                currentNode = (Node) nodeIterator.next();
                result = new BufferedReader(new InputStreamReader(currentNode.getNode(Node.JCR_CONTENT).getProperty(JCR_DATA).getStream()))
                        .lines().collect(Collectors.joining("\n"));
            }

        } catch (Exception e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            session.logout();
        }

        return result;

    }


    public String policyAgreementTemplateFileContent(List<AgreementSectionResponseDTO> sectionResponseDto, PolicyAgreementTemplate policyAgreementTemplate) {
        StringBuilder context = new StringBuilder();
        context.append("HEADER"+"\n");
        context.append(policyAgreementTemplate.getName()+"\n");
        context.append(policyAgreementTemplate.getDescription()+"\n");
        sectionResponseDto.forEach(section -> {
                    context.append(section.getTitle()+"\n");
                    for (ClauseBasicResponseDTO clause : section.getClauses()) {
                        context.append(clause.getTitle()+"\n");
                        context.append(clause.getDescription()+"\n");

                    }
                }
        );

        return new String(context);
    }


    public String clauseFileContent(Clause clause) {
        StringBuilder context = new StringBuilder();
        context.append("HEADER"+"\n");
        context.append(clause.getTitle()+"\n");
        context.append(clause.getDescription()+"\n");
        return context.toString();
    }


}









