package com.kairos.service.s3bucket;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.kairos.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class AWSBucketService {


    Logger LOGGER = LoggerFactory.getLogger(AWSBucketService.class);
    @Inject
    private AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.bucket.folder}")
    private String bucketFolderName;

    public String uploadImage(MultipartFile multipartFile) {

        String imageUrl = null;
        try {
            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentType(multipartFile.getContentType());
            String fileName = DateUtils.getDate().getTime() + multipartFile.getOriginalFilename();
            String key = bucketFolderName + File.separator + fileName;
            InputStream inputStream = multipartFile.getInputStream();
            PutObjectRequest request = new PutObjectRequest(bucketName, key, inputStream, metaData).withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3Client.putObject(request);
            imageUrl = amazonS3Client.getResourceUrl(bucketName, key);
            inputStream.close();
        } catch (IOException e) {
            LOGGER.warn("{ IO Exception Exception cause" + e.getCause() + "}");
        } catch (SdkClientException e) {
            LOGGER.warn("{ Sdk Client Exception cause" + e.getCause() + "}");
        }
        return imageUrl;

    }


}
