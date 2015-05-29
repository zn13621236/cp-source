package com.archer.pm.web;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.archer.pm.cypher.CipherService;
import com.archer.pm.domain.db.User;
import com.archer.pm.domain.repo.UserRepo;
import com.archer.pm.service.email.EmailService;

@RequestMapping ("/users")
@Controller
public class FYPController {

    @Autowired
    UserRepo              userRepo;
    @Autowired
    private CipherService cs;
    @Autowired
    private EmailService  es;

    @RequestMapping (value = "/fyp", method = RequestMethod.POST, produces = "text/html")
    public String fyp (@Valid User user, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors ()) {
            populateEditForm (uiModel, user);
            return "users/fyp";
        }
        User u = userRepo.findByUserNameAndStatus (user.getUserName (),"active");
        if (u != null) {
            // reset pass..
            String newPass = UUID.randomUUID ().toString ().replace ("-", "");
            u.setPassWord (cs.encrypt (newPass));
            userRepo.save (u);
            Map<String,Object> aMap= new HashMap<String,Object>();
            u.setPassWord (newPass);
            aMap.put ("u", u);
            // send new pass to email address...
            es.sendEmail ("2", new String[] { u.getUserName () },aMap);
            return "redirect:/users/login?form";
        }else{
            user.setUserName ("User not found..");
            populateEditForm (uiModel, user);
        return "users/fyp";}
    }

    @RequestMapping (value = "/fyp", params = "form", produces = "text/html")
    public String fypForm (Model uiModel) {
        populateEditForm (uiModel, new User ());
        return "users/fyp";
    }

    void populateEditForm (Model uiModel, User user) {
        uiModel.addAttribute ("user", user);
    }

    String encodeUrlPathSegment (String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding ();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment (pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
}
