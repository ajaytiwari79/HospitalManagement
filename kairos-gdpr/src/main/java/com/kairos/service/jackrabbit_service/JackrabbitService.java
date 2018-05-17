package com.kairos.service.jackrabbit_service;


import com.kairos.custome_exception.ClauseNotFoundJackRabbitException;
import com.kairos.persistance.model.agreement_template.AgreementTemplate;
import com.kairos.persistance.model.clause.Clause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.jcr.*;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;

import java.math.BigInteger;
import java.util.Calendar;

import static com.kairos.constant.JackRabbitConstant.CLAUSE_PARENT_NODE;
import static com.kairos.constant.JackRabbitConstant.NODE_TYPE_UNSTRUCTURED;
import static com.kairos.constant.JackRabbitConstant.CLAUSE_CHILD_NODE;
import static com.kairos.constant.JackRabbitConstant.AGREEMENT_TEMPLATE_PARENT_NODE;
import static com.kairos.constant.JackRabbitConstant.AGREEMENT_TEMPLATE;

@Component
public class JackrabbitService {


    @Inject
    private Repository repository;


    Logger logger = LoggerFactory.getLogger(JackrabbitService.class);

    public Boolean addClause(Clause clause) throws RepositoryException {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        try {
            Node rootNode = session.getRootNode();
            Node parentClauseNode;
            if (!rootNode.hasNode(CLAUSE_PARENT_NODE)) {
                parentClauseNode = rootNode.addNode(CLAUSE_PARENT_NODE, NODE_TYPE_UNSTRUCTURED);
                parentClauseNode.setProperty("jcr:lastModified", Calendar.getInstance().getTimeInMillis());

            }
            parentClauseNode = rootNode.getNode(CLAUSE_PARENT_NODE);
            Node clauseVersioningNode = parentClauseNode.addNode(CLAUSE_CHILD_NODE + clause.getId(), "nt:unstructured");
            clauseVersioningNode.addMixin("mix:versionable");
            clauseVersioningNode.setProperty("jcr:lastModified", Calendar.getInstance().getTimeInMillis());
            clauseVersioningNode.setProperty("id", String.valueOf(clause.getId()));
            clauseVersioningNode.setProperty("title", clause.getTitle());
            session.save();
            clauseVersioningNode.checkin();
            return true;

        }  catch (PathNotFoundException e) {
            logger.info(" path Not found Exception");
            logger.warn(e.getMessage());
            e.printStackTrace();
        } catch (RepositoryException e) {
            logger.info("repository exception");
            logger.warn(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.info("repository exception");
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            System.err.println("finally++++++++++");
            session.logout();
        }
        return false;

    }


    public Boolean clauseVersioning(BigInteger clauseid, Clause clause) throws RepositoryException {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        try {
            Node rootNode = session.getRootNode();
            Node parentClauseNode;

            Node clauseNode = rootNode.getNode(CLAUSE_PARENT_NODE + "/" + CLAUSE_CHILD_NODE + clauseid);
            logger.info("clausenode.isnode()  " + clauseNode.isNode());
            if (clauseNode.isNode()) {
                clauseNode.checkout();
                clauseNode.setProperty("jcr:lastModified", Calendar.getInstance().getTimeInMillis());
                clauseNode.setProperty("id", clause.getId().toString());
                clauseNode.setProperty("title", clause.getTitle());
                session.save();
                clauseNode.checkin();

                VersionIterator it = clauseNode.getVersionHistory().getAllVersions();
                it.skip(1);
                while (it.hasNext()) {
                    Version version = (Version) it.next();
                    NodeIterator nodeIterator = version.getNodes();
                    while (nodeIterator.hasNext()) {

                        Node currentNode = nodeIterator.nextNode();
                        System.err.println("node id " + version.getName());
                        System.err.println("node id " + currentNode.getProperty("id").getValue());
                        System.err.println("node id " + currentNode.getProperty("title").getString());


                    }

                }


                return true;
            }
            throw new ClauseNotFoundJackRabbitException("clause versioning node not found in jackrbbit for id" + clause.getId());


        } catch (PathNotFoundException e) {
            logger.info(" path Not found Exception");
            logger.warn(e.getMessage());
            e.printStackTrace();
        }  catch (RepositoryException e) {
            logger.info("repository exception");
            logger.warn(e.getMessage());
            e.printStackTrace();
        }
       catch (Exception e) {
            logger.info(" exception");
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            session.logout();
        }
        return false;
    }

    public Boolean addAgreementTemplate(AgreementTemplate agreementTemplate) throws RepositoryException {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        try {
            Node rootNode = session.getRootNode();
            Node parentClauseNode;
            if (!rootNode.hasNode(AGREEMENT_TEMPLATE_PARENT_NODE)) {
                parentClauseNode = rootNode.addNode(AGREEMENT_TEMPLATE_PARENT_NODE, NODE_TYPE_UNSTRUCTURED);
                parentClauseNode.setProperty("jcr:lastModified", Calendar.getInstance().getTimeInMillis());

            }
            parentClauseNode = rootNode.getNode(AGREEMENT_TEMPLATE_PARENT_NODE);
            Node agreementTemplateVersioningNode = parentClauseNode.addNode(AGREEMENT_TEMPLATE + agreementTemplate.getId(), "nt:unstructured");
            agreementTemplateVersioningNode.addMixin("mix:versionable");
            agreementTemplateVersioningNode.setProperty("jcr:lastModified", Calendar.getInstance().getTimeInMillis());
            agreementTemplateVersioningNode.setProperty("id", String.valueOf(agreementTemplate.getId()));
            agreementTemplateVersioningNode.setProperty("name", agreementTemplate.getName());
            session.save();
            agreementTemplateVersioningNode.checkin();
            return true;

        } catch (PathNotFoundException e) {
            logger.info(" path Not found Exception");
            logger.warn(e.getMessage());
            e.printStackTrace();
        }  catch (RepositoryException e) {
            logger.info("repository exception");
            logger.warn(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.info("repository exception");
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            System.err.println("finally++++++++++");
            session.logout();
        }
        return false;

    }


    public Boolean agreementTemplateVersioning(BigInteger agreementTemplateId, AgreementTemplate agreementTemplate) throws RepositoryException {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        try {
            Node rootNode = session.getRootNode();
            Node parentClauseNode;

            Node agreementTemplateVersionNode = rootNode.getNode(AGREEMENT_TEMPLATE_PARENT_NODE + "/" + AGREEMENT_TEMPLATE + agreementTemplateId);
            logger.info("clausenode.isnode()  " + agreementTemplateVersionNode.isNode());
            if (agreementTemplateVersionNode.isNode()) {
                agreementTemplateVersionNode.checkout();
                agreementTemplateVersionNode.setProperty("jcr:lastModified", Calendar.getInstance().getTimeInMillis());
                agreementTemplateVersionNode.setProperty("id", agreementTemplate.getId().toString());
                agreementTemplateVersionNode.setProperty("name", agreementTemplate.getName());
                session.save();
                agreementTemplateVersionNode.checkin();

                VersionIterator it = agreementTemplateVersionNode.getVersionHistory().getAllVersions();
                it.skip(1);
                while (it.hasNext()) {
                    Version version = (Version) it.next();
                    NodeIterator nodeIterator = version.getNodes();
                    while (nodeIterator.hasNext()) {
                        Node currentNode = nodeIterator.nextNode();
                        System.err.println("node id " + version.getName());
                        System.err.println("node id " + currentNode.getProperty("id").getValue());
                        System.err.println("node id " + currentNode.getProperty("title").getString());

                    }

                }


                return true;
            }
            throw new ClauseNotFoundJackRabbitException("clause versioning node not found in jackrbbit for id" + agreementTemplate.getId());


        }  catch (PathNotFoundException e) {
            logger.info(" path Not found Exception");
            logger.warn(e.getMessage());
            e.printStackTrace();
        } catch (RepositoryException e) {
            logger.info("repository exception");
            logger.warn(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.info(" exception");
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            session.logout();
        }
        return false;
    }


}
