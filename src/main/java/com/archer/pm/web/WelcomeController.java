package com.archer.pm.web;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import com.archer.pm.domain.constant.Category;
import com.archer.pm.domain.db.Poll;
import com.archer.pm.domain.db.User;
import com.archer.pm.service.CommonService;
import com.archer.pm.service.PollService;
import com.archer.pm.service.UserService;

@RequestMapping ("/")
@Controller
public class WelcomeController {

    @Autowired
    CommonService cs;
    @Autowired
    UserService   uService;
    @Autowired
    PollService   pollService;

    @RequestMapping (produces = "text/html")
    public String loginForm (@CookieValue(value="l", required=false) String loggedIn,Model uiModel, HttpServletRequest httpServletRequest) {
//        populateEditFormWithCategory (uiModel);        
        if ((loggedIn!=null&&!loggedIn.equalsIgnoreCase ("false"))||cs.containAttributeInSession (httpServletRequest, "userId")) {
            User u = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            Poll p = pollService.getRandomPoll (u);
//            httpServletRequest.getSession ().setAttribute ("poll", p);
            populateEditForm (uiModel, p);
            return "index-after-login";
        }
     
        Poll p = pollService.getRandomPoll ();
//        httpServletRequest.getSession ().setAttribute ("poll", p);
        populateEditForm (uiModel, p);
        populateEditForm (uiModel, new User ());
        return "index";
    }

    public void populateEditFormWithCategory (Model uiModel) {
        List <Category> labelValues = new ArrayList <Category> ();
        for (Category c: Category.values ()) {
            labelValues.add (c);
        }
        uiModel.addAttribute ("categoryValues", labelValues);
    }

    void populateEditForm (Model uiModel, Poll poll) {
        uiModel.addAttribute ("poll", poll);
    }
    void populateEditForm (Model uiModel, User user) {
        uiModel.addAttribute ("user", user);
    }
}
