package com.archer.pm.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.archer.pm.domain.db.Poll;
import com.archer.pm.domain.db.User;


public interface FileService {

    String upload (MultipartFile file, Poll poll, User user) throws IllegalStateException, IOException;

    String upload (MultipartFile file, User user) throws IllegalStateException, IOException;

    void deleteFile (String key);}
