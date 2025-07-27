package com.nntk.m2s.repository;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.PutObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Repository
@Slf4j
public class S3Repository {

    @Value("${bos.ak}")
    private String ak;

    @Value("${bos.sk}")
    private String sk;

    @Value("${bos.endpoint}")
    private String endpoint;

    @Value("${bos.bucketName}")
    private String bucketName;


    public PutObjectResponse uploadFile(File file, String objectKey) {
        BosClientConfiguration config = new BosClientConfiguration();
        config.setMaxConnections(10);
        config.setCredentials(new DefaultBceCredentials(ak, sk));
        config.setEndpoint(endpoint);
        BosClient client = new BosClient(config);
        try {
            return client.putObject(bucketName, objectKey, file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            client.shutdown();
        }
    }


}
