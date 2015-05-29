package com.archer.pm.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;
import com.archer.pm.domain.constant.Category;
import com.archer.pm.domain.db.Poll;
import com.archer.pm.domain.db.PollComment;
import com.archer.pm.domain.db.User;
import com.archer.pm.domain.model.Option;
import com.archer.pm.domain.model.PollEntity;
import com.archer.pm.domain.repo.PollRepo;
import com.archer.pm.domain.repo.UserRepo;
import com.archer.pm.service.CommonService;
import com.archer.pm.service.FileService;
import com.archer.pm.service.PollService;
import com.archer.pm.service.UserService;

@RequestMapping ("/polls")
@Controller
// @RooWebScaffold (path = "polls", formBackingObject = Poll.class)
public class PollController {

    private static Logger log = Logger.getLogger (PollController.class.getName ());
    @Autowired
    PollService           pollService;
    @Autowired
    CommonService         commonService;
    @Autowired
    UserService           userService;
    @Autowired
    FileService           fileService;
    @Autowired
    PollRepo              pollRepo;
    @Autowired
    UserRepo              userRepo;

    @RequestMapping (value = "text", method = RequestMethod.POST, produces = "text/html")
    public String create (@Valid PollEntity poll, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (poll.getTest ().length () > 0 || bindingResult.hasErrors ()) {
            populateEditFormWithCategory (uiModel);
            populateEditForm (uiModel, poll);
            return "polls/create";
        }
        uiModel.asMap ().clear ();
        User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
        // if user is not logged in, go to sign up page, store the poll to session
        if (user == null) {
            httpServletRequest.getSession ().setAttribute ("pollOnSignUp", poll);
            return "redirect:/users/create?form";
        }
        Poll pollToSave = pollService.createPoll (poll, user);
        userService.reducePollPoints (user, Integer.valueOf (commonService.getTextPollPoints ()));
        return "redirect:/polls/" + encodeUrlPathSegment (pollToSave.getGuid ().toString (), httpServletRequest) + "/show";
    }

    @RequestMapping (value = "noUser", method = RequestMethod.POST, produces = "text/html")
    public String createNoUserPoll (Model uiModel, HttpServletRequest httpServletRequest) {
        uiModel.asMap ().clear ();
        PollEntity poll = ((PollEntity) (httpServletRequest.getSession ().getAttribute ("pollOnSignUp")));
        httpServletRequest.getSession ().removeAttribute ("pollOnSignUp");
        if (poll != null) {
            poll.setInviteOnly (true);
            Poll pollToSave = pollService.createPoll (poll, null);
            return "redirect:/polls/" + encodeUrlPathSegment (pollToSave.getGuid ().toString (), httpServletRequest) + "/ready";
            
        } else {
            uiModel.addAttribute ("message", "no poll created");
            return "redirect:/vote/message";
        }
    }

    @RequestMapping (value = "text", params = "form", produces = "text/html")
    public String createForm (Model uiModel, HttpServletRequest httpServletRequest) {
        populateEditFormWithCategory (uiModel);
        //
        // check user qualified
        User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
        if (user != null && !userService.isUserQualifiedForCreateTextPoll (user)) {
            uiModel.addAttribute ("message", "Sorry, you don't have enough point to create poll.");
            return "redirect:/vote/message";
        }
        populateEditForm (uiModel, new PollEntity ());
        if (user != null) {
            log.info (user.toString ());
            return "polls/create";
        } else {
            return "polls/create-before-login";
        }
        // } else {
        // uiModel.addAttribute ("user", new User ());
        // uiModel.addAttribute ("message", "Please log in to create new polls");
        // return "message-before-login";
        // }
    }

    @RequestMapping (value = "create", produces = "text/html")
    public String createChooseForm (Model uiModel, HttpServletRequest httpServletRequest) {
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            return "polls/template";
        } else {
            return "polls/template-before-login";
        }
    }

    
    
    @RequestMapping (value = "/{guid}/ready", produces = "text/html")
    public String ready (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, @PathVariable ("guid") String guid,
            Model uiModel, HttpServletRequest httpServletRequest) {
        // populateEditFormWithCategory (uiModel);
        Poll poll = pollRepo.findOne (guid);
        uiModel.addAttribute ("poll", poll);
        return "polls/ready";
    }
    
    @RequestMapping (value = "/{guid}/show", produces = "text/html")
    public String show (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, @PathVariable ("guid") String guid,
            Model uiModel, HttpServletRequest httpServletRequest) {
        // populateEditFormWithCategory (uiModel);
        Poll poll = pollRepo.findOne (guid);
        uiModel.addAttribute ("poll", poll);
        uiModel.addAttribute ("itemId", guid);
        uiModel.addAttribute ("newComment", new PollComment ());
        if (poll.isInviteOnly ()) {
            uiModel.addAttribute ("sharelink", poll.getPrivateLink ());
        } else {
            uiModel.addAttribute ("sharelink", commonService.getPollHost () + "/" + poll.getGuid ());
        }
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            // check if user owns the poll..owner allows to delete poll in the view, and no fan user displayed
            User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            if (user.getGuid ().equalsIgnoreCase (poll.getUserGuid ())) {
                uiModel.addAttribute ("owner", "true");
            }
            // check user is fan or not..
            if (userService.isIdol (user.getGuid (), poll.getUserGuid ())) {
                uiModel.addAttribute ("watched", true);
            }
            // check poll is pinged or not..
            if (pollService.isPined (poll.getGuid (), user.getGuid ())) {
                uiModel.addAttribute ("pined", true);
            }
            // in order to show existing comments...
            int sizeNo = size == null ? 30 : size.intValue ();
            final int firstResult = page == null ? 0 : page;
            uiModel.addAttribute ("page", firstResult);
            PageRequest request = new PageRequest (firstResult, sizeNo, Direction.DESC, "addDate");
            List <PollComment> comments = pollService.getPollComments (guid, request);
            if (comments != null) {
                uiModel.addAttribute ("comments", comments);
            }
            uiModel.addAttribute ("userNickName", user.getNickName ());
            return "polls/show";
        }
//        uiModel.addAttribute ("user", new User ());
        return "polls/show-before-login";
    }

    @RequestMapping (value = "/{guid}/multiple", produces = "text/html")
    public String showMultiple (@PathVariable ("guid") String guid, Model uiModel, HttpServletRequest httpServletRequest) {
        Poll poll = pollRepo.findOne (guid);
        uiModel.addAttribute ("poll", poll);
        return "polls/show-preview";
    }
    
    @RequestMapping (value = "/{guid}/lightview", produces = "text/html")
    public String showLightView (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, @PathVariable ("guid") String guid,
            Model uiModel, HttpServletRequest httpServletRequest) {

        // populateEditFormWithCategory (uiModel);
        Poll poll = pollRepo.findOne (guid);
        uiModel.addAttribute ("poll", poll);
        uiModel.addAttribute ("itemId", guid);
        uiModel.addAttribute ("newComment", new PollComment ());
        if (poll.isInviteOnly ()) {
            uiModel.addAttribute ("sharelink", poll.getPrivateLink ());
        } else {
            uiModel.addAttribute ("sharelink", commonService.getPollHost () + "/" + poll.getGuid ());
        }
       // if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            // check if user owns the poll..owner allows to delete poll in the view, and no fan user displayed
            User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            if (user.getGuid ().equalsIgnoreCase (poll.getUserGuid ())) {
                uiModel.addAttribute ("owner", "true");
            }
            // check user is fan or not..
            if (userService.isIdol (user.getGuid (), poll.getUserGuid ())) {
                uiModel.addAttribute ("watched", true);
            }
            // check poll is pinged or not..
            if (pollService.isPined (poll.getGuid (), user.getGuid ())) {
                uiModel.addAttribute ("pined", true);
            }
            // in order to show existing comments...
            int sizeNo = size == null ? 30 : size.intValue ();
            final int firstResult = page == null ? 0 : page;
            uiModel.addAttribute ("page", firstResult);
            PageRequest request = new PageRequest (firstResult, sizeNo, Direction.DESC, "addDate");
            List <PollComment> comments = pollService.getPollComments (guid, request);
            if (comments != null) {
                uiModel.addAttribute ("comments", comments);
            }
            uiModel.addAttribute ("userNickName", user.getNickName ());
            return "polls/show-light-view";
   //     }

  //      return "polls/show-before-login";
    }
    

    @RequestMapping (value = "list/mine", produces = "text/html")
    public String list (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, Model uiModel,
            HttpServletRequest httpServletRequest) {
        uiModel.addAttribute ("voted", false);
        populateEditFormWithCategory (uiModel);
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            // uiModel.addAttribute("user",new User());
            return "index";
        }
        User user = (User) (httpServletRequest.getSession ().getAttribute ("userId"));
        int sizeNo = size == null ? 12 : size.intValue ();
        final int firstResult = page == null ? 0 : page;
        uiModel.addAttribute ("page", firstResult);
        log.info ("user guid: ="+ user.getGuid ());
        long maxNumb = pollRepo.countByUserGuidAndStatus (user.getGuid (), "active");
        log.info ("this is count number: = " + maxNumb);
        uiModel.addAttribute ("maxPage", (int) maxNumb % sizeNo != 0 ? Math.floor (maxNumb / sizeNo) + 1 : maxNumb / sizeNo);
        uiModel.addAttribute ("polls",
                pollRepo.findByUserGuidAndStatus (user.getGuid (), "active", new org.springframework.data.domain.PageRequest (firstResult, sizeNo, new Sort (Direction.DESC, "createDate")))
                        .getContent ());
        return "polls/list";
    }

    @RequestMapping (value = "list/mine/append", produces = "text/html")
    public String listAppend (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, Model uiModel,
            HttpServletRequest httpServletRequest) {
        uiModel.addAttribute ("voted", false);
        populateEditFormWithCategory (uiModel);
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            // uiModel.addAttribute("user",new User());
            return "index";
        }
        int sizeNo = size == null ? 12 : size.intValue ();
        final int firstResult = page == null ? 0 : page;
        uiModel.addAttribute ("page", firstResult);
        uiModel.addAttribute (
                "polls",
                pollRepo.findByUserGuidAndStatus (((User) (httpServletRequest.getSession ().getAttribute ("userId"))).getGuid (), "active",
                        new org.springframework.data.domain.PageRequest (firstResult, sizeNo, new Sort (Direction.DESC, "createDate"))).getContent ());
        return "polls/list-none";
    }

    @RequestMapping (value = "/{guid}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete (@PathVariable ("guid") String guid, @RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size,
            Model uiModel, HttpServletRequest httpServletRequest) {
        User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
        pollService.deletePoll (guid, user.getGuid ());
        uiModel.asMap ().clear ();
        uiModel.addAttribute ("message", "Poll has been deleted");
        return "message";
    }

    // create image poll..
    @RequestMapping (value = "/image", method = RequestMethod.POST, produces = "text/html")
    public String createImagePoll (@Valid PollEntity poll, @RequestParam ("file1") MultipartFile file, @RequestParam ("file2") MultipartFile file2, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) throws IllegalStateException, IOException {
        uiModel.asMap ().clear ();
        log.info ("this is content type: = " + file.getContentType ());
        if (poll.getTest ().length () > 0 || bindingResult.hasErrors ()) {
            populateEditForm (uiModel, poll);
            populateEditFormWithCategory (uiModel);
            return "polls/image1";
        } else if (!file.getContentType ().contains ("image") || !file2.getContentType ().contains ("image") || 0 == file.getSize () || file.getSize () > 1024 * 1024 * 3 || 0 == file2.getSize ()
                || file2.getSize () > 1024 * 1024 * 3) {
            populateEditForm (uiModel, poll);
            populateEditFormWithCategory (uiModel);
            uiModel.addAttribute ("errormessage", "File has to be 1. image file 2. less than 3mb");
            return "polls/image1";
        }
        log.info ("this is selected poll: = " + poll.toString ());
        User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
        Poll p = pollService.createPoll (poll, user);
        String option1 = fileService.upload (file, p, user);
        String option2 = fileService.upload (file2, p, user);
        p.getOptions ().add (new Option (option1));
        p.getOptions ().add (new Option (option2));
        p.setImagePoll (true);
        pollRepo.save (p);
        userService.reducePollPoints (user, Integer.valueOf (commonService.getImagePollPoints ()));
        return "redirect:/polls/" + encodeUrlPathSegment (p.getGuid ().toString (), httpServletRequest) + "/show";
    }

    // create image poll
    @RequestMapping (value = "/image", params = "form", produces = "text/html")
    public String createImagePollForm (Model uiModel, HttpServletRequest httpServletRequest) {
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            // check user qualified
            User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            if (user != null && !userService.isUserQualifiedForImagePoll (user)) {
                uiModel.addAttribute ("message", "Sorry, you don't have enough point to create image poll.");
                return "message";
            }
            PollEntity poll = new PollEntity ();
            poll.setQuestion ("which one is better?");
            populateEditForm (uiModel, poll);
            populateEditFormWithCategory (uiModel);
            return "polls/image1";
        } else {
            uiModel.addAttribute ("message", "Please log in to create new polls");
            return "message-before-login";
        }
    }

    // create image poll template 2
    @RequestMapping (value = "/image2", method = RequestMethod.POST, produces = "text/html")
    public String createImagePoll2 (@Valid PollEntity poll, @RequestParam ("file1") MultipartFile file, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest)
            throws IllegalStateException, IOException {
        uiModel.asMap ().clear ();
        if (poll.getTest ().length () > 0 || bindingResult.hasErrors ()) {
            populateEditFormWithCategory (uiModel);
            populateEditForm (uiModel, poll);
            return "polls/image2";
        } else if (!file.getContentType ().contains ("image") || 0 == file.getSize () || file.getSize () > 1024 * 1024 * 3) {
            populateEditForm (uiModel, poll);
            populateEditFormWithCategory (uiModel);
            uiModel.addAttribute ("errormessage", "File has to be 1. image file 2. less than 3mb");
            return "polls/image2";
        }
        log.info ("this is selected poll: = " + poll.toString ());
        User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
        Poll p = pollService.createPoll (poll, user);
        String question = fileService.upload (file, p, user);
        p.setQuestion (question);
        p.setImagePoll (true);
        pollRepo.save (p);
        userService.reducePollPoints (user, Integer.valueOf (commonService.getImagePollPoints ()));
        return "redirect:/polls/" + encodeUrlPathSegment (p.getGuid ().toString (), httpServletRequest) + "/show";
    }

    // create image poll template 2
    @RequestMapping (value = "/image2", params = "form", produces = "text/html")
    public String createImagePollForm2 (Model uiModel, HttpServletRequest httpServletRequest) {
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            // check user qualified
            User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            if (!userService.isUserQualifiedForImagePoll (user)) {
                uiModel.addAttribute ("message", "Sorry, you don't have enough point to create image poll.");
                return "message";
            }
            PollEntity poll = new PollEntity ();
            poll.setQuestion ("which one is better?");
            populateEditForm (uiModel, poll);
            populateEditFormWithCategory (uiModel);
            return "polls/image2";
        } else {
            uiModel.addAttribute ("message", "Please log in to create new polls");
            return "message-before-login";
        }
    }

    // below are inherited from spring roo...
    @RequestMapping (method = RequestMethod.PUT, produces = "text/html")
    public String update (@Valid Poll poll, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors ()) {
            populateEditForm (uiModel, poll);
            return "polls/update";
        }
        uiModel.asMap ().clear ();
        pollRepo.save (poll);
        return "redirect:/polls/" + encodeUrlPathSegment (poll.getGuid ().toString (), httpServletRequest) + "/show";
    }

    @RequestMapping (value = "/{guid}", params = "form", produces = "text/html")
    public String updateForm (@PathVariable ("guid") String guid, Model uiModel) {
        populateEditForm (uiModel, pollRepo.findOne (guid));
        return "polls/update";
    }

    @RequestMapping (value = "list/voted", produces = "text/html")
    public String listVotedPollsForm (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, Model uiModel,
            HttpServletRequest httpServletRequest) {
        populateEditFormWithCategory (uiModel);
        uiModel.addAttribute ("voted", true);
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            uiModel.addAttribute ("message", "please log in to see your voted poll");
            return "message-before-login";
        } else {
            // only log in allowed to see
            User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            int sizeNo = size == null ? 12 : size.intValue ();
            final int firstResult = page == null ? 0 : page;
            uiModel.addAttribute ("page", firstResult);
            List <Poll> pollList = pollService.listVotedPolls (user.getGuid ());
            if (pollList == null) {
                uiModel.addAttribute ("message", "no poll found");
                return "message";
            }
            long maxNumb = pollList.size ();
            log.info ("this is the poll list size: =" + pollList.size ());
            uiModel.addAttribute ("maxPage", (int) maxNumb % sizeNo != 0 ? Math.floor (maxNumb / sizeNo) + 1 : maxNumb / sizeNo);
            int left = pollList.size () - firstResult * sizeNo;
            if (left > 0) {
                pollList = pollList.subList (firstResult * sizeNo, (firstResult * sizeNo + sizeNo) < pollList.size () ? firstResult * sizeNo + sizeNo : pollList.size ());
            } else {
                pollList = null;
            }
            uiModel.addAttribute ("polls", pollList);
            return "polls/list";
        }
    }

    @RequestMapping (value = "list/voted/append", produces = "text/html")
    public String listVotedPollsAppendForm (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, Model uiModel,
            HttpServletRequest httpServletRequest) {
        populateEditFormWithCategory (uiModel);
        uiModel.addAttribute ("voted", true);
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            uiModel.addAttribute ("message", "please log in to see your voted poll");
            return "redirect:/vote/message-before-login";
        } else {
            // only log in allowed to see
            User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            int sizeNo = size == null ? 12 : size.intValue ();
            final int firstResult = page == null ? 0 : page;
            uiModel.addAttribute ("page", firstResult);
            List <Poll> pollList = pollService.listVotedPolls (user.getGuid ());
//            if (pollList == null) {
//                uiModel.addAttribute ("message", "");
//                return "redirect:/vote/message";
//            }
            int left = pollList.size () - firstResult * sizeNo;
            if (left > 0) {
                pollList = pollList.subList (firstResult * sizeNo, (firstResult * sizeNo + sizeNo) < pollList.size () ? firstResult * sizeNo + sizeNo : pollList.size ());
            } else {
                pollList = null;
            }
            uiModel.addAttribute ("polls", pollList);
            return "polls/list-none";
        }
    }

    @RequestMapping (value = "list/pined", produces = "text/html")
    public String listPinedPollsForm (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, Model uiModel,
            HttpServletRequest httpServletRequest) {
        populateEditFormWithCategory (uiModel);
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            uiModel.addAttribute ("message", "please log in to see your voted poll");
            return "message-before-login";
        } else {
            // only log in allowed to see
            User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            uiModel.addAttribute ("pined_voted", true);
            int sizeNo = size == null ? 12 : size.intValue ();
            final int firstResult = page == null ? 0 : page;
            uiModel.addAttribute ("page", firstResult);
            List <Poll> pollList = pollService.getPinedPoll (user.getGuid ());
            if (pollList == null) {
                uiModel.addAttribute ("message", "no poll found");
                return "message";
            }
            log.info ("this is the poll list size: =" + pollList.size ());
            long maxNumb = pollList.size ();
            log.info ("this is the poll list size: =" + pollList.size ());
            uiModel.addAttribute ("maxPage", (int) maxNumb % sizeNo != 0 ? Math.floor (maxNumb / sizeNo) + 1 : maxNumb / sizeNo);
            int left = pollList.size () - firstResult * sizeNo;
            if (left > 0) {
                pollList = pollList.subList (firstResult * sizeNo, (firstResult * sizeNo + sizeNo) < pollList.size () ? firstResult * sizeNo + sizeNo : pollList.size ());
            } else {
                pollList = null;
            }
            uiModel.addAttribute ("polls", pollList);
            return "polls/list";
        }
    }

    @RequestMapping (value = "list/pined/append", produces = "text/html")
    public String listPinedPollsAppendForm (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, Model uiModel,
            HttpServletRequest httpServletRequest) {
        populateEditFormWithCategory (uiModel);
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            uiModel.addAttribute ("message", "please log in to see your voted poll");
            return "redirect:/vote/message-before-login";
        } else {
            // only log in allowed to see
            User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            uiModel.addAttribute ("pined_voted", true);
            int sizeNo = size == null ? 12 : size.intValue ();
            final int firstResult = page == null ? 0 : page;
            uiModel.addAttribute ("page", firstResult);
            List <Poll> pollList = pollService.getPinedPoll (user.getGuid ());
            if (pollList == null) {
                uiModel.addAttribute ("message", "");
                return "redirect:/vote/message";
            }
            int left = pollList.size () - firstResult * sizeNo;
            if (left > 0) {
                pollList = pollList.subList (firstResult * sizeNo, (firstResult * sizeNo + sizeNo) < pollList.size () ? firstResult * sizeNo + sizeNo : pollList.size ());
            } else {
                pollList = null;
            }
            uiModel.addAttribute ("polls", pollList);
            return "polls/list-none";
        }
    }

    @RequestMapping (value = "{guid}/addComment", method = RequestMethod.POST, produces = "text/html")
    public String addComment (@PathVariable ("guid") String guid, @Valid PollComment comment, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors () || comment.getComment () == null) {
            return show (null, null, guid, uiModel, httpServletRequest);
        }
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            uiModel.addAttribute ("message", "please log in to make comment");
            uiModel.addAttribute ("user", new User ());
            // populateEditFormWithCategory (uiModel);
            return "message-before-login";
        } else {
            User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            pollService.addComment (guid, user, comment);
            return show (null, null, guid, uiModel, httpServletRequest);
        }
    }

    @RequestMapping (value = "{pollGuid}/{guid}/removeComment", method = RequestMethod.POST, produces = "text/html")
    public String removeComment (@PathVariable ("pollGuid") String pollGuid, @PathVariable ("guid") String guid, Model uiModel, HttpServletRequest httpServletRequest) {
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            uiModel.addAttribute ("message", "please log in to remove comment");
            return "message-before-login";
        } else {
            pollService.removeComment (guid);
            return show (null, null, pollGuid, uiModel, httpServletRequest);
        }
    }

    @RequestMapping (value = "fan/{guid}/{userGuid}", method = RequestMethod.POST, produces = "text/html")
    public String watchUser (@PathVariable ("guid") String guid, @PathVariable ("userGuid") String userGuid, Model uiModel, HttpServletRequest httpServletRequest) {
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            User fan = (User) (httpServletRequest.getSession ().getAttribute ("userId"));
            User idol = userRepo.findByGuidAndStatus (userGuid, "active");
            userService.watchUser (fan.getGuid (), idol.getGuid ());
            uiModel.addAttribute ("watched", true);
            return show (null, null, guid, uiModel, httpServletRequest);
        } else {
            uiModel.addAttribute ("message", "Please log in to watch a user.");
            // populateEditFormWithCategory (uiModel);
            uiModel.addAttribute ("user", new User ());
            return "message-before-login";
        }
    }

    @RequestMapping (value = "unFan/{guid}/{userGuid}", method = RequestMethod.POST, produces = "text/html")
    public String unWatchUser (@PathVariable ("guid") String guid, @PathVariable ("userGuid") String userGuid, Model uiModel, HttpServletRequest httpServletRequest) {
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            User fan = (User) (httpServletRequest.getSession ().getAttribute ("userId"));
            User idol = userRepo.findByGuidAndStatus (userGuid, "active");
            userService.removeWatcher (fan.getGuid (), idol.getGuid ());
            uiModel.addAttribute ("watched", false);
            // populateEditFormWithCategory (uiModel);
            return show (null, null, guid, uiModel, httpServletRequest);
        } else {
            uiModel.addAttribute ("message", "Please log in to watch a user.");
            // populateEditFormWithCategory (uiModel);
            uiModel.addAttribute ("user", new User ());
            return "message-before-login";
        }
    }

    @RequestMapping (value = "pin/{guid}", method = RequestMethod.POST, produces = "text/html")
    public String pinPoll (@PathVariable ("guid") String guid, Model uiModel, HttpServletRequest httpServletRequest) {
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            User u = (User) (httpServletRequest.getSession ().getAttribute ("userId"));
            pollService.pinPoll (guid, u.getGuid ());
            uiModel.addAttribute ("pined", true);
            return show (null, null, guid, uiModel, httpServletRequest);
        } else {
            uiModel.addAttribute ("message", "Please log in to pin a poll.");
            // populateEditFormWithCategory (uiModel);
            uiModel.addAttribute ("user", new User ());
            return "message-before-login";
        }
    }

    @RequestMapping (value = "unPin/{guid}", method = RequestMethod.POST, produces = "text/html")
    public String unPinPoll (@PathVariable ("guid") String guid, Model uiModel, HttpServletRequest httpServletRequest) {
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            User user = (User) (httpServletRequest.getSession ().getAttribute ("userId"));
            pollService.unPinPoll (guid, user.getGuid ());
            uiModel.addAttribute ("pined", false);
            return show (null, null, guid, uiModel, httpServletRequest);
        } else {
            uiModel.addAttribute ("message", "Please log in to unpin a poll.");
            // populateEditFormWithCategory (uiModel);
            uiModel.addAttribute ("user", new User ());
            return "message-before-login";
        }
    }

    @RequestMapping (value = "list/recommend", produces = "text/html")
    public String listRecommendedPollsForm (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, Model uiModel,
            HttpServletRequest httpServletRequest) {
        uiModel.addAttribute ("popular", false);
        populateEditFormWithCategory (uiModel);
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            uiModel.addAttribute ("message", "please log in to see your voted poll");
            return "message-before-login";
        } else {
            // only log in allowed to see
            User user = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
            int sizeNo = size == null ? 15 : size.intValue ();
            final int firstResult = page == null ? 0 : page;
            uiModel.addAttribute ("page", firstResult);
            PageRequest pq = new PageRequest (firstResult, sizeNo);
            Page <Poll> pollList = pollService.getRecommendPollList (user, pq);
            uiModel.addAttribute ("polls", pollList.getContent ());
            return "vote/list";
        }
    }

    public void populateEditForm (Model uiModel, Poll poll) {
        uiModel.addAttribute ("poll", poll);
        uiModel.addAttribute ("categorys", Arrays.asList (Category.values ()));
    }

    public String encodeUrlPathSegment (String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding ();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment (pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }

    void populateEditForm (Model uiModel, PollEntity poll) {
        uiModel.addAttribute ("poll", poll);
    }

    public void populateEditFormWithCategory (Model uiModel) {
        List <Category> labelValues = new ArrayList <Category> ();
        for (Category c: Category.values ()) {
            labelValues.add (c);
        }
        uiModel.addAttribute ("categoryValues", labelValues);
    }
}
