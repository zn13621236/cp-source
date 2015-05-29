package com.archer.pm.service.s3;

import java.io.File;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;


public interface S3Service {

    void uploadToS3 (File file, String key) throws AmazonServiceException, AmazonClientException, IOException;

    void deleteFromS3 (String key);}
