package com.archer.pm.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.archer.pm.domain.db.Poll;
import com.archer.pm.domain.db.User;
import com.archer.pm.service.s3.S3Service;

@Service
public class FileServiceImpl implements FileService{
    @Value("${image.resize.width}")
    private int IMG_WIDTH;
    @Value("${image.resize.height}")
    private int IMG_HEIGHT;
    @Value("${image.resize.user.width}")
    private int USER_IMG_WIDTH;
    @Value("${image.resize.user.height}")
    private int USER_IMG_HEIGHT;
    @Value("${s3.bucket.name}")
    private String bucketName;
    @Value("${s3.host}")
    private String s3Host;
    @Autowired
    S3Service s3Service;
    

    
    @Override
    public String upload(MultipartFile file,Poll poll,User user) throws IllegalStateException, IOException{
        String fileName = user.getGuid () + "-" + poll.getGuid () +"-"+System.currentTimeMillis () +".png";
        File destination=null;
        try{
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
        int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
        image=resizeImage(image,type,IMG_WIDTH,IMG_HEIGHT);
        destination=new File(fileName);
        ImageIO.write(image, "png", destination); 
        s3Service.uploadToS3 (destination, fileName);}
        finally{
            if(destination.exists ()){
                destination.delete (); 
            } 
        }
        String imageLink=s3Host+bucketName+"/"+fileName;
        return imageLink;
    }
    @Override
    public String upload(MultipartFile file,User user) throws IllegalStateException, IOException{
        String fileName = user.getGuid () + "-"+System.currentTimeMillis () +".png";
        File destination=null;
        try{
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
        int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
        image=resizeImage(image,type,USER_IMG_WIDTH,USER_IMG_HEIGHT);
        destination=new File(fileName);
        ImageIO.write(image, "png", destination); 
        s3Service.uploadToS3 (destination, fileName);}
        finally{
            if(destination.exists ()){
                destination.delete (); 
            } 
        }
        String imageLink=s3Host+bucketName+"/"+fileName;
        return imageLink;
    }
    
    
    @Override
    public void deleteFile(String key){
        s3Service.deleteFromS3 (key);
    }
    private  BufferedImage resizeImage(BufferedImage originalImage, int type,int IMG_WIDTH,int IMG_HEIGHT){
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
        return resizedImage;
        }
    
}
