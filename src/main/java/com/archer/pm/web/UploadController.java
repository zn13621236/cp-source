//package com.archer.pm.web;
//
//import java.io.IOException;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.archer.pm.domain.db.Poll;
//import com.archer.pm.domain.db.User;
//import com.archer.pm.service.FileService;
//import com.archer.pm.service.s3.S3Service;
//
//@RequestMapping ("/upload")
//@Controller
//public class UploadController {
//
//    @Autowired
//    S3Service s3Service;
//    @Autowired
//    FileService fileService;
//
//    @RequestMapping (method = RequestMethod.POST, produces = "text/html")
//    public String handleFormUpload (@RequestParam ("file1") MultipartFile file,@RequestParam ("file2") MultipartFile file2, Model uiModel, HttpServletRequest httpServletRequest) throws IOException {
//        Poll p = (Poll) httpServletRequest.getSession ().getAttribute ("poll");
//        User u = ((User) (httpServletRequest.getSession ().getAttribute ("userId")));
//        fileService.upload (file, p, u);
//        fileService.upload (file2, p, u);
//        return "index";
//    }
//
//    @RequestMapping (produces = "text/html")
//    public String handleUploadForm (Model uiModel, HttpServletRequest httpServletRequest) {
//        return "polls/upload";
//    }
//}
