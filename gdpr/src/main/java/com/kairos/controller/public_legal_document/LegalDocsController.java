package com.kairos.controller.public_legal_document;

import com.kairos.service.public_legal_document.PublicLegalDocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;

@Controller
@RequestMapping("/public/legal")
public class LegalDocsController {

    @Inject
    private PublicLegalDocumentService publicLegalDocumentService;

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String listLegalDocs(Model model){
        model.addAttribute("legalDocs",publicLegalDocumentService.getAllPublicLegalDocument());
        return "public/legal/legalDocs";
    }
}
