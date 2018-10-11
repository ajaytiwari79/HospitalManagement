package com.kairos.service.s3bucket;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;

@Service
public class S3BucketService {


    Logger LOGGER=LoggerFactory.getLogger(S3BucketService.class);
    @Inject
    private AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.bucket.folder}")
    private String bucketFolderName;

    public String uploadImage(File file) {

        String imageUrl = null;
        try {
            ObjectMetadata metaData=new ObjectMetadata();
            //metaData
            PutObjectRequest request = new PutObjectRequest(bucketName, bucketFolderName + "/"+file.getName(), file).withCannedAcl(CannedAccessControlList.PublicRead);
           // request.setMetadata();
            amazonS3Client.putObject(request);
            imageUrl = amazonS3Client.getResourceUrl(bucketName, bucketFolderName + "/"+file.getName());

        } catch (SdkClientException e) {
            LOGGER.warn("{ Sdk Client Exception cause"+e.getCause()+"}");
        }

        return imageUrl;

    }


}
