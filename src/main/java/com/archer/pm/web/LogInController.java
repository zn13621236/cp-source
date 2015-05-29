//package com.archer.pm.web;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Set;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.CookieValue;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import com.archer.pm.cypher.CipherService;
//import com.archer.pm.domain.constant.Category;
//import com.archer.pm.domain.db.User;
//import com.archer.pm.domain.repo.IdolsRepo;
//import com.archer.pm.domain.repo.UserRepo;
//import com.archer.pm.service.CommonService;
//import com.archer.pm.service.UserService;
//
//@RequestMapping ("/users")
//@Controller
//public class LogInController {
//
//    @Autowired
//    UserRepo              userRepo;
//    @Autowired
//    IdolsRepo             idolRepo;
//    @Autowired
//    UserService           userService;
//    @Autowired
//    private CipherService cs;
//    @Autowired
//    private CommonService commonService;
//
//   
//
//    void populateEditForm (Model uiModel, User user) {
//        uiModel.addAttribute ("user", user);
//    }
//
//    public void populateEditFormWithCategory (Model uiModel) {
//        List <Category> labelValues = new ArrayList <Category> ();
//        for (Category c: Category.values ()) {
//            labelValues.add (c);
//        }
//        uiModel.addAttribute ("categoryValues", labelValues);
//    }
//}
