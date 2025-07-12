package com.nntk.m2s.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;

//@Component
public class RestClientBuilderCustomizerImpl implements RestClientBuilderCustomizer {

    @Override
    public void customize(RestClientBuilder builder) {

    }

    /**
     * 在这里加入自定义逻辑，比如跳过SSL的证书检查和hostname检查
     */
    @Override
    public void customize(HttpAsyncClientBuilder builder) {
        SSLContextBuilder sscb = SSLContexts.custom();
        try {
            sscb.loadTrustMaterial((chain, authType) -> {
                // 在这里跳过证书信息校验
                //System.out.println("暂时isTrusted|" + authType + "|" + Arrays.toString(chain));
                return true;
            });
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        try {
            builder.setSSLContext(sscb.build());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 这里跳过主机名称校验
        builder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
    }
}
