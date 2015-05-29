package com.archer.pm.web;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.archer.pm.domain.db.Report;
import com.archer.pm.domain.db.User;
import com.archer.pm.domain.repo.ReportRepo;
import com.archer.pm.service.CommonService;

@RequestMapping ("/report")
@Controller
//@RooWebScaffold (path = "report", formBackingObject = Report.class)
public class ReportController {

    @Autowired
    CommonService commonService;

    @RequestMapping (value = "/{guid}/do", params = "form", produces = "text/html")
    public String createForm (@PathVariable ("guid") String pollGuid, Model uiModel, HttpServletRequest httpServletRequest) {
        if( commonService.containAttributeInSession (httpServletRequest, "userId")){
        populateEditForm (uiModel, new Report ());
        uiModel.addAttribute ("reportedPollGuid", pollGuid);
        return "report/create";
         }else{
         httpServletRequest.getSession ().setAttribute ("pollGuidToReport", pollGuid);
         return "report/create-before-login";
         }
    }

    @RequestMapping (value = "/{guid}/do", method = RequestMethod.POST, produces = "text/html")
    public String create (@Valid Report report, BindingResult bindingResult, @PathVariable ("guid") String pollGuid, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors ()) {
            populateEditForm (uiModel, report);
            return "report/create";
        }
        uiModel.asMap ().clear ();
        User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
        report.setPollGuid (pollGuid);
        report.setReportTime (System.currentTimeMillis ());
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            report.setReporterGuid (user.getGuid ());
            reportRepo.save (report);
            return "report/success";
        } else {
            reportRepo.save (report);
            return "report/success-before-login";
        }
    }

    @RequestMapping (value = "/{id}", produces = "text/html")
    public String show (@PathVariable ("id") BigInteger id, Model uiModel) {
        uiModel.addAttribute ("report", reportRepo.findOne (id));
        uiModel.addAttribute ("itemId", id);
        return "report/show";
    }

	@Autowired
    ReportRepo reportRepo;

	@RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("reports", reportRepo.findAll(new org.springframework.data.domain.PageRequest(firstResult / sizeNo, sizeNo)).getContent());
            float nrOfPages = (float) reportRepo.count() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("reports", reportRepo.findAll());
        }
        return "report/list";
    }

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid Report report, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, report);
            return "report/update";
        }
        uiModel.asMap().clear();
        reportRepo.save(report);
        return "redirect:/report/" + encodeUrlPathSegment(report.getId().toString(), httpServletRequest);
    }

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") BigInteger id, Model uiModel) {
        populateEditForm(uiModel, reportRepo.findOne(id));
        return "report/update";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") BigInteger id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Report report = reportRepo.findOne(id);
        reportRepo.delete(report);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/report";
    }

	void populateEditForm(Model uiModel, Report report) {
        uiModel.addAttribute("report", report);
    }

	String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
}
