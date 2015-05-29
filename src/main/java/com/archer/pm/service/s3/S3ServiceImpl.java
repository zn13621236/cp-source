package com.archer.pm.service.s3;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3ServiceImpl implements S3Service, InitializingBean, DisposableBean {

    private AmazonS3Client client;
    @Value ("${s3.bucket.name}")
    private String         bucketName;

    @Override
    public void uploadToS3 (File file, String key) throws AmazonServiceException, AmazonClientException, IOException {
        client.putObject (new PutObjectRequest (bucketName, key, file).withCannedAcl (CannedAccessControlList.PublicRead));
    }

    @Override
    public void deleteFromS3 (String key) {
        client.deleteObject (bucketName, key);
    }

    @Override
    public void destroy () throws Exception {
        client.shutdown ();
    }

    @Override
    public void afterPropertiesSet () throws Exception {
        client = new AmazonS3Client (new ClasspathPropertiesFileCredentialsProvider ());
        com.amazonaws.regions.Region usWest2 = Region.getRegion (Regions.US_WEST_2);
        client.setRegion (usWest2);
    }
}
