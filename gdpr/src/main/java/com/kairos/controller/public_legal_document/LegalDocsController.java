package com.kairos.controller.public_legal_document;

import com.kairos.response.dto.public_legal_document.PublicLegalDocumentDTO;
import com.kairos.service.public_legal_document.PublicLegalDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.util.List;

@Controller
@RequestMapping("/public/legal")
public class LegalDocsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LegalDocsController.class);

    @Inject
    private PublicLegalDocumentService publicLegalDocumentService;

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String listLegalDocs(Model model){
        List<PublicLegalDocumentDTO> legalDocumentDTOS = publicLegalDocumentService.getAllPublicLegalDocument();
        model.addAttribute("legalDocs",legalDocumentDTOS);
        if(legalDocumentDTOS.size() > 0){
            model.addAttribute("selectedDoc",legalDocumentDTOS.get(0));
        }
        return "public/legal/legalDocs";
    }

    @RequestMapping(value = "/{documentId}/{documentName}",method = RequestMethod.GET)
    public String docDetail(@PathVariable long documentId,@PathVariable String documentName, Model model){
        PublicLegalDocumentDTO legalDocumentDTO = publicLegalDocumentService.getLegalDocumentById(documentId);
        List<PublicLegalDocumentDTO> legalDocumentDTOS = publicLegalDocumentService.getAllPublicLegalDocument();
        model.addAttribute("legalDocs",legalDocumentDTOS);
        if(legalDocumentDTO!=null){
            model.addAttribute("selectedDoc",legalDocumentDTO);
        }
        return "public/legal/legalDocs";
    }
}
