package com.archer.pm.web;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;
import com.archer.pm.domain.constant.Category;
import com.archer.pm.domain.db.Poll;
import com.archer.pm.domain.db.User;
import com.archer.pm.domain.repo.PollRepo;
import com.archer.pm.service.CommonService;
import com.archer.pm.service.PollService;
import com.archer.pm.service.UserService;

@RequestMapping ("/vote")
@Controller
// @RooWebScaffold (path = "vote", formBackingObject = Poll.class)
public class VoteController {

    @Autowired
    PollRepo      pollRepo;
    @Autowired
    CommonService cService;
    @Autowired
    PollService   pollService;
    @Autowired
    CommonService commonService;
    @Autowired
    UserService   uService;

    @RequestMapping (value = { "/{guid}/{optionId}", "/vote/{guid}/{optionId}" }, method = RequestMethod.POST, produces = "text/html")
    public String vote (@PathVariable ("guid") String guid, @PathVariable ("optionId") String optionId, Model uiModel, HttpServletRequest httpServletRequest) {
        Poll p = pollRepo.findOne (guid);
        uiModel.asMap ().clear ();
        httpServletRequest.getSession ().removeAttribute ("pollId");
        User u = null;
        // if user login, then reward him poll points...
        if (cService.containAttributeInSession (httpServletRequest, "userId")) {
            // poll points increase
            u = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            if (u.getGuid ().equalsIgnoreCase (p.getUserGuid ())) {
                u = null;
            }
        }
        boolean voteSuccess = pollService.vote (p, optionId, u, httpServletRequest);
        if (voteSuccess) {
            return "redirect:/polls/" + encodeUrlPathSegment (p.getGuid (), httpServletRequest) + "/show";
        } else {
            uiModel.addAttribute ("message", "Oops, it seems you either have voted for this poll before or own this poll. Hence not eligible to vote again.");
            uiModel.addAttribute ("user", new User ());
            return "vote/message";
        }
    }

    @RequestMapping (value = { "/{guid}/{optionId}/multiple", "/vote/{guid}/{optionId}/multiple" }, method = RequestMethod.POST, produces = "text/html")
    public String voteMultiple (@PathVariable ("guid") String guid, @PathVariable ("optionId") String optionId, Model uiModel, HttpServletRequest httpServletRequest) {
        Poll p = pollRepo.findOne (guid);
        uiModel.asMap ().clear ();
        httpServletRequest.getSession ().removeAttribute ("pollId");
        User u = null;
        // if user login, then reward him poll points...
        if (cService.containAttributeInSession (httpServletRequest, "userId")) {
            // poll points increase
            u = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            if (u.getGuid ().equalsIgnoreCase (p.getUserGuid ())) {
                u = null;
            }
        }
        // else{
        // uiModel.addAttribute ("message", "Please log in/sign up to vote popular polls");
        // uiModel.addAttribute("user", new User());
        // return "vote/message-block";
        //
        // }
        boolean voteSuccess = pollService.vote (p, optionId, u, httpServletRequest);
        if (voteSuccess) {
            return "redirect:/polls/" + encodeUrlPathSegment (p.getGuid (), httpServletRequest) + "/multiple";
        } else {
            uiModel.addAttribute ("message", "Oops, it seems you either have voted for this poll before or own this poll. Hence not eligible to vote again.");
            return "vote/message-block";
        }
    }
    
    
    
    
    @RequestMapping (value = { "/{guid}/{optionId}/lightview", "/vote/{guid}/{optionId}/lightview" }, method = RequestMethod.POST, produces = "text/html")
    public String voteLightView (@PathVariable ("guid") String guid, @PathVariable ("optionId") String optionId, Model uiModel, HttpServletRequest httpServletRequest) {
        Poll p = pollRepo.findOne (guid);
        uiModel.asMap ().clear ();
        httpServletRequest.getSession ().removeAttribute ("pollId");
        User u = null;
        // if user login, then reward him poll points...
        if (cService.containAttributeInSession (httpServletRequest, "userId")) {
            // poll points increase
            u = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            if (u.getGuid ().equalsIgnoreCase (p.getUserGuid ())) {
                u = null;
            }
        }
        boolean voteSuccess = pollService.vote (p, optionId, u, httpServletRequest);
        if (voteSuccess) {
            return "redirect:/polls/" + encodeUrlPathSegment (p.getGuid (), httpServletRequest) + "/lightview";
        } else {
            uiModel.addAttribute ("message", "Oops, it seems you either have voted for this poll before or own this poll. Hence not eligible to vote again.");
            return "vote/message-block";
        }
    }
    

    @RequestMapping (params = "random", produces = "text/html")
    public String voteRandomForm (Model uiModel, HttpServletRequest httpServletRequest) {
        populateEditFormWithCategory (uiModel);
        if (!cService.containAttributeInSession (httpServletRequest, "userId")) {
            Poll p = pollService.getRandomPoll ();
            // httpServletRequest.getSession ().setAttribute ("poll", p);
            populateEditForm (uiModel, p);
            // populateEditForm (uiModel, new User());
            return "vote/update-before-login";
        } else {
            User u = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            Poll p = pollService.getRandomPoll (u);
            if (p == null) {
                uiModel.addAttribute ("message", "no poll available..");
                return "message";
            }
            // httpServletRequest.getSession ().setAttribute ("poll", p);
            populateEditForm (uiModel, p);
            return "vote/update";
        }
    }

    @RequestMapping (value = "/{guid}/preview", produces = "text/html")
    public String showMultiple (@PathVariable ("guid") String guid, Model uiModel, HttpServletRequest httpServletRequest) {
        // populateEditFormWithCategory (uiModel);
        Poll poll = pollRepo.findOne (guid);
        uiModel.addAttribute ("poll", poll);
        return "vote/vote-block";
    }

    @RequestMapping (value = "/{guid}", params = "direct", produces = "text/html")
    public String voteDirectForm (@PathVariable ("guid") String guid, Model uiModel, HttpServletRequest httpServletRequest) {
        populateEditFormWithCategory (uiModel);
        Poll p = pollRepo.findOne (guid);
        if (p == null||p.isInviteOnly ()) {
            uiModel.addAttribute ("message", "no poll found..");
            return "message";
        }
        // httpServletRequest.getSession ().setAttribute ("poll", p);
        populateEditForm (uiModel, p);
        if (!cService.containAttributeInSession (httpServletRequest, "userId")) {
            return "vote/update-before-login";
        } else {
            User u = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            if (!pollService.isQuailifiedForVote (u, p)) {
                return "redirect:/polls/" + encodeUrlPathSegment (p.getGuid (), httpServletRequest) + "/show";
            } else {
                return "vote/update";
            }
        }
    }
    
    @RequestMapping (value = "/{guid}", params = "lightview", produces = "text/html")
    public String voteFromListLightBoxViewForm (@PathVariable ("guid") String guid, Model uiModel, HttpServletRequest httpServletRequest) {
        populateEditFormWithCategory (uiModel);

        Poll p = pollRepo.findOne (guid);
        if (p == null||p.isInviteOnly ()) {
            uiModel.addAttribute ("message", "no poll found..");
            return "vote/message-block";
        }
        // httpServletRequest.getSession ().setAttribute ("poll", p);
        populateEditForm (uiModel, p);
        if (!cService.containAttributeInSession (httpServletRequest, "userId")) {
            return "vote/vote-light-view";
        } else {
            User u = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            if (!pollService.isQuailifiedForVote (u, p)) {
                return "redirect:/polls/" + encodeUrlPathSegment (p.getGuid (), httpServletRequest) + "/lightview";
            } else {
                return "vote/vote-light-view";
            }
        }
    }

    @RequestMapping (value = "/{guid}", params = "invite", produces = "text/html")
    public String voteInviteForm (@PathVariable ("guid") String guid, Model uiModel, HttpServletRequest httpServletRequest) {
        populateEditFormWithCategory (uiModel);
        Poll p = pollRepo.findByGuidAndStatus (guid, "active");
        // httpServletRequest.getSession ().setAttribute ("poll", p);
        populateEditForm (uiModel, p);
        if (!cService.containAttributeInSession (httpServletRequest, "userId")) {
            if (p == null) {
                uiModel.addAttribute ("message", "no poll found..");
                return "message--before-login";
            } else if (!pollService.checkVoterIp (p, httpServletRequest)) {
                uiModel.addAttribute ("message", "Voted already..");
                return "message-before-login";
            }
            return "vote/update-before-login";
        } else {
            if (p == null) {
                uiModel.addAttribute ("message", "no poll found..");
                return "message";
            } else if (!pollService.checkVoterIp (p, httpServletRequest)) {
                uiModel.addAttribute ("message", "Voted already..");
                return "message";
            }
            User u = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            if (!pollService.isQuailifiedForVote (u, p)) {
                return "redirect:/polls/" + encodeUrlPathSegment (p.getGuid (), httpServletRequest) + "/show";
            } else {
                return "vote/update";
            }
        }
    }

    // @RequestMapping (value = "/{guid}", params = "popular", produces = "text/html")
    // public String votePopularForm (@PathVariable ("guid") String guid, Model uiModel, HttpServletRequest httpServletRequest) {
    // populateEditFormWithCategory (uiModel);
    // if (!cService.containAttributeInSession (httpServletRequest, "userId")) {
    // uiModel.addAttribute ("message", "Sorry, only logged in user can vote popular polls. Please login.");
    // return "vote/message";
    // }
    // Poll p = pollRepo.findOne (guid);
    // if (!p.getUserGuid ().equalsIgnoreCase (((User) (httpServletRequest.getSession ().getAttribute ("userId"))).getGuid ())) {
    // httpServletRequest.getSession ().setAttribute ("poll", p);
    // populateEditForm (uiModel, p);
    // return "vote/update";
    // } else {
    // uiModel.addAttribute ("message", "Sorry, poll owner is not eligible to vote this poll.");
    // return "vote/message";
    // }
    // }
    @RequestMapping (value = "/{category}", params = "random", produces = "text/html")
    public String voteRandomByCategoryForm (@PathVariable ("category") String category, Model uiModel, HttpServletRequest httpServletRequest) {
        populateEditFormWithCategory (uiModel);
        if (!cService.containAttributeInSession (httpServletRequest, "userId")) {
            uiModel.addAttribute ("watched", false);
            Poll p = pollService.getRandomPoll (category);
            if (p != null) {
                // httpServletRequest.getSession ().setAttribute ("poll", p);
                populateEditForm (uiModel, p);
                populateEditFormWithCategory (uiModel);
                populateEditForm (uiModel, new User ());
                return "vote/update-before-login";
            } else {
                uiModel.addAttribute ("message", "No poll founded..");
                populateEditFormWithCategory (uiModel);
                populateEditForm (uiModel, new User ());
                return "vote/message-before-login";
            }
        } else {
            User u = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            Poll p = pollService.getRandomPoll (category, u);
            if (p != null) {
                if (uService.isIdol (u.getGuid (), p.getUserGuid ())) {
                    uiModel.addAttribute ("watched", true);
                } else {
                    uiModel.addAttribute ("watched", false);
                }
                // httpServletRequest.getSession ().setAttribute ("poll", p);
                populateEditForm (uiModel, p);
                return "vote/update";
            } else {
                populateEditFormWithCategory (uiModel);
                uiModel.addAttribute ("message", "No poll founded..");
                return "vote/message";
            }
        }
    }

    // @RequestMapping (value = "multiple", params = "random", produces = "text/html")
    // public String voteRandomMultipleForm (Model uiModel, HttpServletRequest httpServletRequest) {
    // populateEditFormWithCategory (uiModel);
    // if (!cService.containAttributeInSession (httpServletRequest, "userId")) {
    // List <Poll> pList = pollService.getRandomPollList ();
    // if (pList != null && pList.size () > 0) {
    // for (int i = 0; i < pList.size (); i++) {
    // uiModel.addAttribute ("poll" + (i + 1), pList.get (i));
    // httpServletRequest.getSession ().setAttribute ("poll" + (i + 1), pList.get (i));
    // }
    // httpServletRequest.getSession ().setAttribute ("pollList", pList);
    // return "vote/updateMultiple-before-login";
    // } else {
    // uiModel.addAttribute ("message", "No poll founded..");
    // populateEditFormWithCategory (uiModel);
    // return "vote/message-before-login";
    // }
    // } else {
    // User u = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
    // List <Poll> pList = pollService.getRandomPollList (u);
    // if (pList != null && pList.size () > 0) {
    // for (int i = 0; i < pList.size (); i++) {
    // uiModel.addAttribute ("poll" + (i + 1), pList.get (i));
    // // if poll is watched by owner, show watched in UI.
    // if (uService.isIdol (u.getGuid (), pList.get (i).getUserGuid ())) {
    // uiModel.addAttribute ("watched" + i, true);
    // }
    // httpServletRequest.getSession ().setAttribute ("poll" + (i + 1), pList.get (i));
    // }
    // httpServletRequest.getSession ().setAttribute ("pollList", pList);
    // return "vote/updateMultiple";
    // } else {
    // uiModel.addAttribute ("message", "No poll founded..");
    // populateEditFormWithCategory (uiModel);
    // return "vote/message";
    // }
    // }
    // }
    @RequestMapping (value = "{category}/popular", produces = "text/html")
    public String listPopularPollForm (@PathVariable ("category") String category, @RequestParam (value = "page", required = false) Integer page,
            @RequestParam (value = "size", required = false) Integer size, Model uiModel, HttpServletRequest httpServletRequest) {
        uiModel.addAttribute ("popular_category", category);
        populateEditFormWithCategory (uiModel);
        // only log in allowed to see
        int sizeNo = size == null ? 15 : size.intValue ();
        final int firstResult = page == null ? 0 : page;
        uiModel.addAttribute ("page", firstResult);
        PageRequest pq = new PageRequest (firstResult, sizeNo, new Sort (Direction.DESC, "voteNum"));
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            Page <Poll> pp = pollService.getHotPollByCategory (category, pq);
            uiModel.addAttribute ("polls", pp.getContent ());
            return "vote/list-before-login";
        } else {
            User u = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            Page <Poll> pp = pollService.getHotPollByCategory (u.getGuid (), category, pq);
            uiModel.addAttribute ("polls", pp.getContent ());
            return "vote/list";
        }
    }

    @RequestMapping (value = "popular", produces = "text/html")
    public String listPopularPollByCategoryForm (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, Model uiModel,
            HttpServletRequest httpServletRequest) {
        uiModel.addAttribute ("popular", true);
        populateEditFormWithCategory (uiModel);
        // only log in allowed to see
        int sizeNo = size == null ? 15 : size.intValue ();
        final int firstResult = page == null ? 0 : page;
        uiModel.addAttribute ("page", firstResult);
        PageRequest pq = new PageRequest (firstResult, sizeNo, new Sort (Direction.DESC, "voteNum"));
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            Page <Poll> pp = pollService.getHotPoll (pq);
            uiModel.addAttribute ("polls", pp.getContent ());
            return "vote/list-before-login";
        } else {
            User u = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            Page <Poll> pp = pollService.getHotPoll (u.getGuid (), pq);
            uiModel.addAttribute ("polls", pp.getContent ());
            return "vote/list";
        }
    }

    @RequestMapping (value = "/list", params = "random", produces = "text/html")
    public String voteRandomListForm (Model uiModel, HttpServletRequest httpServletRequest) {
        populateEditFormWithCategory (uiModel);
        uiModel.addAttribute ("mode_choose", true);
        if (!cService.containAttributeInSession (httpServletRequest, "userId")) {
            Page <Poll> p = pollService.getRandomPollList ();
            uiModel.addAttribute ("polls", p.getContent ());
            return "vote/list-before-login";
        } else {
            User u = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            Page <Poll> p = pollService.getRandomPollList (u);
            uiModel.addAttribute ("polls", p.getContent ());
            return "vote/list";
        }
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
