package com.archer.pm.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;
import com.archer.pm.cypher.CipherService;
import com.archer.pm.domain.constant.Category;
import com.archer.pm.domain.db.Poll;
import com.archer.pm.domain.db.User;
import com.archer.pm.domain.model.Activity;
import com.archer.pm.domain.model.PollEntity;
import com.archer.pm.domain.model.UpdatePasswordForm;
import com.archer.pm.domain.repo.UserRepo;
import com.archer.pm.service.CommonService;
import com.archer.pm.service.FileService;
import com.archer.pm.service.PollService;
import com.archer.pm.service.UserService;

@RequestMapping ("/users")
@Controller
// @RooWebScaffold (path = "users", formBackingObject = User.class)
public class UserController {

    private static Logger log = Logger.getLogger (UserController.class.getName ());
    @Autowired
    private CipherService cs;
    @Autowired
    private CommonService commonService;
    @Autowired
    UserRepo              userRepo;
    @Autowired
    UserService           userService;
    @Autowired
    FileService           fileService;
    @Autowired
    PollService           pollService;

    @RequestMapping (value = "login", method = RequestMethod.POST, produces = "text/html")
    public String login (@CookieValue (value = "l", required = false) String login, @Valid User user, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest,
            HttpServletResponse response) {
        if (bindingResult.hasErrors ()) {
            // populateEditFormWithCategory (uiModel);
            populateEditForm (uiModel, user);
            return "users/login";
        }
        User u = userRepo.findByUserNameAndStatus (user.getUserName (), "active");
        if (u != null && u.getPassWord ().equalsIgnoreCase (cs.encrypt (user.getPassWord ()))) {
            setCookie (login, httpServletRequest, response, u);
            userService.prepareUser (httpServletRequest, u);
            return "redirect:/vote?random";
        } else {
            // populateEditFormWithCategory (uiModel);
            user.setUserName ("username/password is invalid, try again..");
            populateEditForm (uiModel, user);
            return "users/login";
        }
    }

    /**
     * @param login
     * @param httpServletRequest
     * @param response
     * @param u
     */
    private void setCookie (String login, HttpServletRequest httpServletRequest, HttpServletResponse response, User u) {
        // take care cookie data
        Cookie loginCookie = null;
        if (login == null) {
            loginCookie = new Cookie ("l", u.getGuid ());
            loginCookie.setMaxAge (1 * 60 * 60 * 24 * 7);
            loginCookie.setPath ("cp");
            response.addCookie (loginCookie);
        } else {
            Cookie[] cookies = httpServletRequest.getCookies ();
            for (Cookie c: cookies) {
                if (c.getName ().equalsIgnoreCase ("l")) {
                    c.setValue (u.getGuid ());
                    c.setMaxAge (1 * 60 * 60 * 24 * 7);
                    c.setPath ("cp");
                    response.addCookie (c);
                }
            }
        }
    }

    @RequestMapping (value = "fblogin/{userName}", method = RequestMethod.GET, produces = "text/html")
    public String fbLogin (@PathVariable ("userName") String userName, Model uiModel, HttpServletRequest httpServletRequest) {
        User u = userRepo.findByUserNameAndStatus (userName, "active");
        if (u == null) {
            // if user not exist, create new user..
            userService.create (userName);
        }
        userService.prepareUser (httpServletRequest, u);
        return "redirect:/vote?random";
    }

    @RequestMapping (value = "login", params = "form", produces = "text/html")
    public String loginForm (Model uiModel) {
        // populateEditFormWithCategory (uiModel);
        populateEditForm (uiModel, new User ());
        return "users/login";
    }

    @RequestMapping (value="create",method = RequestMethod.POST, produces = "text/html")
    public String create (@CookieValue (value = "l", required = false) String login, @Valid User user, @RequestParam ("userThumbNail") MultipartFile file, BindingResult bindingResult,
            Model uiModel, HttpServletRequest httpServletRequest, HttpServletResponse response) throws IllegalStateException, IOException {
        populateEditForm (uiModel, user);
        // populateEditFormWithCategory (uiModel);
        populateEditFormWithBirthYears (uiModel);
        if (bindingResult.hasErrors ()) {
            return "users/create";
        } else if (userRepo.findByUserNameAndStatus (user.getUserName (), "active") != null) {
            user.setUserName ("User name is taken, try another one");
            return "users/create";
        } else if (userRepo.findByNickNameAndStatus (user.getNickName (), "active") != null) {
            user.setNickName ("Nick name is taken, try another one");
            return "users/create";
        }   
        else if (!user.getAcceptTerm ()) {
            uiModel.addAttribute ("acceptTermAndCondition", "false");
            return "users/create";
        } else if (file != null && (!file.getContentType ().contains ("image")) && 0 < file.getSize ()) {
            return "users/create";
        }
        if (file != null && (file.getContentType ().contains ("image")) && 0 < file.getSize ()) {
            String userImage = fileService.upload (file, user);
            user.setUserImage (userImage);
        }
        // if come from sign up, then go to poll page..
        PollEntity p = (PollEntity) httpServletRequest.getSession ().getAttribute ("pollOnSignUp");
        httpServletRequest.getSession ().removeAttribute ("pollOnSignUp");
        if (p != null) {
            user = userService.create (user, 5);
            Poll pollToSave = pollService.createPoll (p, user);
            uiModel.asMap ().clear ();
            userService.prepareUser (httpServletRequest, user);
            // take care cookie data
            setCookie (login, httpServletRequest, response, user);
            return "redirect:/polls/" + encodeUrlPathSegment (pollToSave.getGuid ().toString (), httpServletRequest) + "/show";
        } else {
            user = userService.create (user);
            uiModel.asMap ().clear ();
            userService.prepareUser (httpServletRequest, user);
            setCookie (login, httpServletRequest, response, user);
            return "redirect:/vote?random";
        }
    }

    @RequestMapping (value="create",params = "form", produces = "text/html")
    public String createForm (Model uiModel, HttpServletRequest httpServletRequest) {
        PollEntity p = (PollEntity) httpServletRequest.getSession ().getAttribute ("pollOnSignUp");
        if (p != null) {
            uiModel.addAttribute ("pollOnSignUp", true);
        }
        populateEditForm (uiModel, new User ());
        populateEditFormWithCategory (uiModel);
        populateEditFormWithBirthYears (uiModel);
        return "users/create";
    }

    @RequestMapping (value = "signup", produces = "text/html")
    public String signup (Model uiModel, HttpServletRequest httpServletRequest) {
        uiModel.addAttribute ("pollOnSignUp", false);
        populateEditForm (uiModel, new User ());
        populateEditFormWithCategory (uiModel);
        populateEditFormWithBirthYears (uiModel);
        return "users/create";
    }

    @RequestMapping (value = "rank", produces = "text/html")
    public String getRanking (Model uiModel) {
        // populateEditForm (uiModel, new User ());
        // populateEditFormWithCategory (uiModel);
        uiModel.addAttribute ("users", userService.getFansRankingList (10).getContent ());
        return "users/rank";
    }

    // @RequestMapping (value = "/admin/list", produces = "text/html")
    // public String list (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer
    // size, Model uiModel) {
    // // populateEditFormWithCategory (uiModel);
    // if (page != null || size != null) {
    // int sizeNo = size == null ? 10 : size.intValue ();
    // final int firstResult = page == null ? 0 : (page.intValue () - 1) * sizeNo;
    // uiModel.addAttribute ("users", userRepo.findAll (new org.springframework.data.domain.PageRequest (firstResult / sizeNo, sizeNo)).getContent
    // ());
    // float nrOfPages = (float) userRepo.count () / sizeNo;
    // uiModel.addAttribute ("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
    // } else {
    // uiModel.addAttribute ("users", userRepo.findAll ());
    // }
    // return "users/list";
    // }
    @RequestMapping (value = "logout", produces = "text/html")
    public String logout (Model uiModel, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        populateEditFormWithCategory (uiModel);
        httpServletRequest.getSession ().invalidate ();
        // take care of cookie..
        Cookie[] cookies = httpServletRequest.getCookies ();
        if (cookies != null) {
            for (Cookie c: cookies) {
                if (c.getName ().equalsIgnoreCase ("l")) {
                    c.setValue ("false");                    
                    response.addCookie (c);
                }
            }
        }

        Poll p = pollService.getRandomPoll ();
        // httpServletRequest.getSession ().setAttribute ("poll", p);
        populateEditForm (uiModel, p);
        // populateEditForm (uiModel, new User ());
        return "index";
    }

    @RequestMapping (value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete (@PathVariable ("id") BigInteger id, @RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size,
            Model uiModel) {
        User user = userRepo.findOne (id);
        user.setStatus ("deleted");
        userRepo.save (user);
        uiModel.asMap ().clear ();
        uiModel.addAttribute ("page", (page == null) ? "1" : page.toString ());
        uiModel.addAttribute ("size", (size == null) ? "10" : size.toString ());
        return "redirect:/users";
    }

    @RequestMapping (value = "/{id}", produces = "text/html")
    public String show (@PathVariable ("id") String id, Model uiModel) {
        // populateEditFormWithCategory (uiModel);
        User user = userRepo.findByGuidAndStatus (id, "active");
        uiModel.addAttribute ("user", user);
        uiModel.addAttribute ("itemId", id);
        uiModel.addAttribute ("totalVote", userService.getUserVoteNumber (user));
        uiModel.addAttribute ("totalPoll", userService.getTotalPollNumberForUser (user));
        uiModel.addAttribute ("createDate", commonService.convertTime (user.getCreateDate (), "MM/dd/yyyy"));
        return "users/show";
    }

    @RequestMapping (value = "/show/others/{guid}", produces = "text/html")
    public String showUserDetail (@PathVariable ("guid") String guid, Model uiModel, HttpServletRequest httpServletRequest) {
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            return "index";
        }
        User loggedInUser = (User) httpServletRequest.getSession ().getAttribute ("userId");
        User user = userRepo.findByGuidAndStatus (guid, "active");
        if (userService.isIdol (loggedInUser.getGuid (), user.getGuid ())) {
            uiModel.addAttribute ("watched", true);
        } else {
            uiModel.addAttribute ("watched", false);
        }
        uiModel.addAttribute ("user", user);
        uiModel.addAttribute ("totalVote", userService.getUserVoteNumber (user));
        uiModel.addAttribute ("totalPoll", userService.getTotalPollNumberForUser (user));
        uiModel.addAttribute ("createDate", commonService.convertTime (user.getCreateDate (), "MM/dd/yyyy"));
        return "users/show-others";
    }

    @RequestMapping (value = "/show", produces = "text/html")
    public String show (Model uiModel, HttpServletRequest httpServletRequest) {
        // populateEditFormWithCategory (uiModel);
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            User u = (User) httpServletRequest.getSession ().getAttribute ("userId");
            u = userRepo.findByUserNameAndStatus (u.getUserName (), "active");
            return show (u.getGuid (), uiModel);
        } else {
            return "redirect:/users/login?form";
        }
    }

    @RequestMapping (value = "/{id}", params = "form", produces = "text/html")
    public String updateForm (@PathVariable ("id") String id, Model uiModel, HttpServletRequest httpServletRequest) {
        // populateEditFormWithCategory (uiModel);
        populateEditForm (uiModel, new UpdatePasswordForm (userRepo.findByGuidAndStatus (id, "active")));
        return "users/update";
    }

    @RequestMapping (value = "update", params = "form", produces = "text/html")
    public String updateForm (Model uiModel, HttpServletRequest httpServletRequest) {
        User u = (User) httpServletRequest.getSession ().getAttribute ("userId");
        // populateEditFormWithCategory (uiModel);
        populateEditForm (uiModel, new UpdatePasswordForm (u));
        return "users/update";
    }

    @RequestMapping (method = RequestMethod.PUT, produces = "text/html")
    public String updatePass (@Valid UpdatePasswordForm user, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        User u = (User) (httpServletRequest.getSession ().getAttribute ("userId"));
        if (bindingResult.hasErrors ()) {
            populateEditForm (uiModel, new UpdatePasswordForm (u));
            // populateEditFormWithCategory (uiModel);
            return "users/update";
        }
        uiModel.asMap ().clear ();
        User userToSave = userRepo.findByUserNameAndStatus (((User) (httpServletRequest.getSession ().getAttribute ("userId"))).getUserName (), "active");
        System.out.println ("this is users password:  = " + user.getPassWord ());
        if (cs.encrypt (user.getOldPassWord ()).equalsIgnoreCase (userToSave.getPassWord ())) {
            userToSave.setPassWord (cs.encrypt (user.getNewPassWord ()));
            userRepo.save (userToSave);
            return "redirect:/users/" + encodeUrlPathSegment (userToSave.getGuid (), httpServletRequest);
        } else {
            populateEditForm (uiModel, new UpdatePasswordForm (u));
            // populateEditFormWithCategory (uiModel);
            return "users/update";
        }
    }

    @RequestMapping (value = "update/preference", params = "form", produces = "text/html")
    public String updatePreferenceForm (Model uiModel, HttpServletRequest httpServletRequest) {
        User u = (User) httpServletRequest.getSession ().getAttribute ("userId");
        if (u == null) {
            return "index";
        }
        populateEditFormWithCategory (uiModel);
        populateEditForm (uiModel, new UpdatePasswordForm (u));
        uiModel.addAttribute ("createDate", commonService.convertTime (u.getCreateDate (), "MM/dd/yyyy"));
        return "users/update-preference";
    }

    @RequestMapping (value = "update/preference", method = RequestMethod.POST, produces = "text/html")
    public String update (@Valid UpdatePasswordForm user, @RequestParam ("userThumbNail") MultipartFile file, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest)
            throws IllegalStateException, IOException {
        User u = (User) (httpServletRequest.getSession ().getAttribute ("userId"));
        if (bindingResult.hasErrors () || (file != null && (!file.getContentType ().contains ("image")) && 0 < file.getSize ())) {
            populateEditForm (uiModel, new UpdatePasswordForm (u));
            // populateEditFormWithCategory (uiModel);
            return "users/update-perference";
        }
        uiModel.asMap ().clear ();
        User userToSave = userRepo.findByUserNameAndStatus (((User) (httpServletRequest.getSession ().getAttribute ("userId"))).getUserName (), "active");
        userToSave.setInterestList (user.getInterestList ());
        if (file != null && file.getSize () > 0) {
            // remove older image from s3
            if (userToSave.getUserImage () != null) {
                fileService.deleteFile (userToSave.getUserImage ());
            }
            // store new image..
            String userImage = fileService.upload (file, userToSave);
            userToSave.setUserImage (userImage);
        }
        u = userRepo.save (userToSave);
        // update session..
        httpServletRequest.getSession ().setAttribute ("userId", u);
        return "redirect:/users/" + encodeUrlPathSegment (userToSave.getGuid (), httpServletRequest);
    }

    @RequestMapping (value = "watch/remove/{guid}", method = RequestMethod.POST, produces = "text/html")
    public String removeWatch (@PathVariable ("guid") String guid, Model uiModel, HttpServletRequest httpServletRequest) {
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            User fan = (User) (httpServletRequest.getSession ().getAttribute ("userId"));
            User idol = userRepo.findByGuidAndStatus (guid, "active");
            userService.removeWatcher (fan.getGuid (), idol.getGuid ());
            // uiModel.addAttribute ("poll", httpServletRequest.getSession ().getAttribute ("poll"));
            // populateEditFormWithCategory (uiModel);
            return listMyFan (null, null, uiModel, httpServletRequest);
        } else {
            uiModel.addAttribute ("message", "Please log in to unfan a user.");
            uiModel.addAttribute ("user", new User ());
            // populateEditFormWithCategory (uiModel);
            return "message-before-login";
        }
    }

    @RequestMapping (value = "list/watch", produces = "text/html")
    public String listMyFan (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, Model uiModel,
            HttpServletRequest httpServletRequest) {
        // populateEditFormWithCategory (uiModel);
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            User user = (User) (httpServletRequest.getSession ().getAttribute ("userId"));
            // only log in allowed to see
            List <User> uList = userService.getMyIdols (user.getGuid ());
            if (uList == null) {
                uiModel.addAttribute ("message", "no watch list");
                return "message";
            }
            uiModel.addAttribute ("watchers", uList);
            int sizeNo = size == null ? 15 : size.intValue ();
            final int firstResult = page == null ? 0 : page;
            uiModel.addAttribute ("page", firstResult);
            int left = uList.size () - firstResult * sizeNo;
            if (left > 0) {
                uList = uList.subList (firstResult * sizeNo, (firstResult * sizeNo + sizeNo) < uList.size () ? firstResult * sizeNo + sizeNo : uList.size ());
                if (left <= 15) {
                    uiModel.addAttribute ("more", false);
                }
            } else {
                uList = null;
            }
            return "users/list-watches";
        } else {
            uiModel.addAttribute ("message", "please log in to see user watch list.");
            uiModel.addAttribute ("user", new User ());
            return "message-before-login";
        }
    }

    @RequestMapping (value = "list/recommend/users", produces = "text/html")
    public String listRecommendUser (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            User user = (User) (httpServletRequest.getSession ().getAttribute ("userId"));
            // only log in allowed to see
            int sizeNo = size == null ? 3 : size.intValue ();
            final int firstResult = page == null ? 0 : page;
            log.info ("this is page number: " + firstResult);
            PageRequest pq = new PageRequest (firstResult, sizeNo);
            List <User> uList = userService.getRecommendUser (user, pq);
            uiModel.addAttribute ("recommend_page", firstResult + 1);
            sizeNo = 3;
            int left = uList.size () - firstResult * sizeNo;
            if (left > 0) {
                uList = uList.subList (firstResult * sizeNo, (firstResult * sizeNo + sizeNo) < uList.size () ? firstResult * sizeNo + sizeNo : uList.size ());
                if (left <= 3) {
                    uiModel.addAttribute ("more", false);
                }
            } else {
                uiModel.addAttribute ("recommend_page", 0);
                uList = uList.subList (0, 3);
            }
            httpServletRequest.getSession ().setAttribute ("recommend_users", uList);
            uiModel.addAttribute ("recommend_users", uList);
            return "users/list-recommend";
        } else {
            uiModel.addAttribute ("message", "please log in to see recommend user list.");
            return "redirect:/vote/message-block";
        }
    }

    @RequestMapping (value = "/idol/activity/{guid}", produces = "text/html")
    public String listIdolActivityForm (@PathVariable ("guid") String guid, @RequestParam (value = "page", required = false) Integer page,
            @RequestParam (value = "size", required = false) Integer size, Model uiModel, HttpServletRequest httpServletRequest) {
        // populateEditFormWithCategory (uiModel);
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            uiModel.addAttribute ("message", "please log in.");
            return "message-before-login";
        } else {
            int sizeNo = size == null ? 15 : size.intValue ();
            final int firstResult = page == null ? 0 : page;
            uiModel.addAttribute ("page", firstResult);
            User user = userRepo.findByGuidAndStatus (guid, "active");
            List <Activity> acList = pollService.getUserActivity (user, firstResult, sizeNo);
            if (acList == null) {
                uiModel.addAttribute ("message", "No poll founded..");
                // populateEditFormWithCategory (uiModel);
                return "message";
            }
            uiModel.addAttribute ("activities", acList);
            return "users/activities";
        }
    }

    @RequestMapping (value = "/idol/activity", produces = "text/html")
    public String listIdolsActivityForm (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, Model uiModel,
            HttpServletRequest httpServletRequest) {
        // populateEditFormWithCategory (uiModel);
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            uiModel.addAttribute ("message", "please log in.");
            uiModel.addAttribute ("user", new User ());
            return "message-before-login";
        } else {
            User user = (User) (httpServletRequest.getSession ().getAttribute ("userId"));
            int sizeNo = size == null ? 15 : size.intValue ();
            final int firstResult = page == null ? 0 : page;
            uiModel.addAttribute ("page", firstResult);
            List <Activity> acList = pollService.getIdolsActivity (user, firstResult, sizeNo);
            if (acList == null) {
                uiModel.addAttribute ("message", "No poll founded..");
                // populateEditFormWithCategory (uiModel);
                return "message";
            }
            uiModel.addAttribute ("oldSize", acList.size ());
            uiModel.addAttribute ("activities", acList);
            return "users/activities";
        }
    }

    @RequestMapping (value = "/idol/activity/append", produces = "text/html")
    public String listIdolsActivityAppendForm (@RequestParam (value = "page", required = false) Integer page, @RequestParam (value = "size", required = false) Integer size, Model uiModel,
            HttpServletRequest httpServletRequest) {
        // populateEditFormWithCategory (uiModel);
        if (!commonService.containAttributeInSession (httpServletRequest, "userId")) {
            uiModel.addAttribute ("message", "please log in.");
            uiModel.addAttribute ("user", new User ());
            return "message-before-login";
        } else {
            User user = (User) (httpServletRequest.getSession ().getAttribute ("userId"));
            int sizeNo = size == null ? 15 : size.intValue ();
            final int firstResult = page == null ? 0 : page;
            uiModel.addAttribute ("page", firstResult);
            List <Activity> acList = pollService.getIdolsActivity (user, firstResult, sizeNo);
            if (acList == null) {
                uiModel.addAttribute ("maxPage", true);
            }
            uiModel.addAttribute ("activities", acList);
            return "users/activities-none";
        }
    }

    @RequestMapping (value = "fan/{userGuid}", method = RequestMethod.POST, produces = "text/html")
    public String watchUser (@PathVariable ("userGuid") String userGuid, Model uiModel, HttpServletRequest httpServletRequest) {
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            User fan = (User) (httpServletRequest.getSession ().getAttribute ("userId"));
            User idol = userRepo.findByGuidAndStatus (userGuid, "active");
            userService.watchUser (fan.getGuid (), idol.getGuid ());
            uiModel.addAttribute ("watched", true);
            return showUserDetail (userGuid, uiModel, httpServletRequest);
        } else {
            uiModel.addAttribute ("message", "Please log in to watch a user.");
            // populateEditFormWithCategory (uiModel);
            // uiModel.addAttribute ("user", new User ());
            return "message-before-login";
        }
    }

    @RequestMapping (value = "unFan/{userGuid}", method = RequestMethod.POST, produces = "text/html")
    public String unWatchUser (@PathVariable ("userGuid") String userGuid, Model uiModel, HttpServletRequest httpServletRequest) {
        if (commonService.containAttributeInSession (httpServletRequest, "userId")) {
            User fan = (User) (httpServletRequest.getSession ().getAttribute ("userId"));
            User idol = userRepo.findByGuidAndStatus (userGuid, "active");
            userService.removeWatcher (fan.getGuid (), idol.getGuid ());
            uiModel.addAttribute ("watched", false);
            // populateEditFormWithCategory (uiModel);
            return showUserDetail (userGuid, uiModel, httpServletRequest);
        } else {
            uiModel.addAttribute ("message", "Please log in to watch a user.");
            // populateEditFormWithCategory (uiModel);
            uiModel.addAttribute ("user", new User ());
            return "message-before-login";
        }
    }

    @RequestMapping (value = "userName/{userName}/check", method = RequestMethod.GET, produces = "text/plain")
    public String userNameCheck (@PathVariable ("userName") String userName) {
        if (userService.checkUserName (userName)) {
            return "users/true";
        }
        return "users/false";
    }

    @RequestMapping (value = "nickName/{userName}/check", method = RequestMethod.POST, produces = "text/html")
    public String nickNameCheck (@PathVariable ("nickName") String nickName) {
        if (userService.checkNickName (nickName)) {
            return "users/true";
        }
        return "users/false";
    }

    private void populateEditFormWithBirthYears (Model uiModel) {
        List <Integer> aList = new ArrayList <Integer> ();
        for (int i = 1900; i < 2011; i++) {
            aList.add (i);
        }
        uiModel.addAttribute ("years", aList);
    }

    public void populateEditFormWithCategory (Model uiModel) {
        List <Category> labelValues = new ArrayList <Category> ();
        for (Category c: Category.values ()) {
            labelValues.add (c);
        }
        uiModel.addAttribute ("categoryValues", labelValues);
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

    void populateEditForm (Model uiModel, User user) {
        uiModel.addAttribute ("user", user);
    }

    void populateEditForm (Model uiModel, UpdatePasswordForm user) {
        uiModel.addAttribute ("user", user);
    }

    void populateEditForm (Model uiModel, Poll poll) {
        uiModel.addAttribute ("poll", poll);
    }
}
