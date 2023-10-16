package com.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.common.CacheData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.Reporter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.common.CacheData.*;
import static com.common.CommonData.N;
import static org.apache.http.Consts.UTF_8;

/**
 * HttpRequestUtils
 *
 * @Author: 冷枫红舞
 */
@Slf4j
public class HttpRequestUtils {

    private HttpRequestUtils() {
    }

    /**
     * preDoPost
     *
     * @param url       url
     * @param headerMap headerMap
     * @param bodyMap   bodyMap
     * @return Map<String, Object>
     */
    public static Map<String, Object> preDoPost(String url,
                                                Map<String, Object> headerMap,
                                                Map<String, Object> bodyMap,
                                                Map<String, Object> preDependentParameterMap) {
        log.info("[调试信息] [preDoPost] 开始执行：");
        if (StringUtils.isEmpty(url) || MapUtils.isEmpty(bodyMap)) {
            log.info("[调试信息] [preDoPost] 请求参数 [url] 或 [body] 为 null，方法终止执行。抛出 IllegalArgumentException 异常。");
            throw new IllegalArgumentException();
        }

        String httpResponse = doPost(url, headerMap, bodyMap);
        CacheData.httpResponseCache = httpResponse;

//        log.info("[调试信息] [preDoPost] 打印 preDependentParameterMap 的值：{}", preDependentParameterMap);
        if (MapUtils.isNotEmpty(preDependentParameterMap)) {
            preDependentParameterMap.replaceAll((k, v) -> JSONPath.eval(JSONObject.parseObject(httpResponse), (String) v));
            log.info("[调试信息] [preDoPost] 打印从 [前置接口] 获取的依赖参数 [preDependentParameterMap] 的值：{}", preDependentParameterMap);
        }
        return preDependentParameterMap;
    }

    /**
     * doPost
     *
     * @param url       url
     * @param headerMap headerMap
     * @param bodyMap   bodyMap
     * @return String
     */
    public static String doPost(String url,
                                Map<String, Object> headerMap,
                                Map<String, Object> bodyMap) {
        log.info("[调试信息] [doPost] 开始执行：");
        if (StringUtils.isEmpty(url)) {
            log.info("[调试信息] [doPost] 请求参数 [url == null]，方法终止执行。抛出 IllegalArgumentException 异常。");
            throw new IllegalArgumentException();
        } else {
            log.info("[调试信息] [doPost] 打印请求 URL = {}", url);
            Reporter.log("【调试信息】【doPost】 打印请求 URL = " + url);
        }

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        if (MapUtils.isNotEmpty(headerMap)) {
            for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
                httpPost.setHeader(entry.getKey(), (String) entry.getValue());
            }
        }
        if (StringUtils.isNotEmpty(tokenCache)) {
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, tokenCache);
//            log.info("[调试信息] [doPost] httpPost.setHeader(HttpHeaders.AUTHORIZATION, tokenCache)");
        }
        if (MapUtils.isEmpty(bodyMap)) {
            log.info("[调试信息] [doPost] 请求参数 [body == null]，方法终止执行。抛出 IllegalArgumentException 异常。");
            throw new IllegalArgumentException();
        }
        String entityString = getRequestData(bodyMap);

        return httpExecute(httpPost, entityString);
    }

    /**
     * 根据项目实际情况实现具体加密逻辑。
     *
     * @param bodyMap 未加密报文
     * @return 加密后报文
     */
    private static String getRequestData(Map<String, Object> bodyMap) {
        String body = JSONObject.toJSONString(bodyMap);
        log.info("[调试信息] [getRequestData] 打印 [未加密] 的 [body] 数据：{}", bodyMap);

        /**
         * 如果不对请求报文加密，则 Header 的 encryptToggle 值为 0
         * （api-user 逻辑）
         */
        if (encryptToggle.equals("0")) {
            preEncryptFlag = "N";
            encryptFlag = "N";
        } else {
            preEncryptFlag = "Y";
            encryptFlag = "Y";
        }

        if (StringUtils.isEmpty(privateKey) || StringUtils.isEmpty(publicKey)) {
            encryptFlag = N;
//            log.info("[调试信息] [getRequestData] 请求报文 [不加密]，因为 privateKey 或 publicKey [null]，设置 encryptFlag = N。");
        }

        String entityString = null;
        if (StringUtils.isEmpty(encryptFlag) || encryptFlag.equalsIgnoreCase(N)) {
//            log.info("[调试信息] [getRequestData] 请求报文 [不加密]，因为 encryptFlag 加密标志位设置为：[null] 或 [N]。");
            entityString = body;
        } else {
            /**
             * [项目] 加密逻辑
             */
//            String encryptBody = RSAUtil.bigEncryptByPublicKey(body, RSAUtil.rsaPublicKeyToFront);
//            log.info("[调试信息] [getRequestData] 打印 [加密后] 的 [body] 数据：{}", encryptBody);
//            Map<String, Object> dataMap = new HashMap<>();
//            dataMap.put("data", encryptBody);
//            entityString = JSONObject.toJSONString(dataMap);
//            log.info("[调试信息] [getRequestData] 打印组装好的 [data] 报文数据：{}", entityString);
        }

        return entityString;
    }

    /**
     * httpClient.execute()
     *
     * @param httpPost httpPost
     * @return String
     */
    private static String httpExecute(HttpPost httpPost, String entityString) {
//        log.info("[调试信息] [httpExecute] 开始执行：");
        log.info("[调试信息] [httpExecute] 打印请求报文：{}", entityString);
        Reporter.log("【调试信息】【httpExecute】 打印请求报文：" + entityString);
        StringEntity stringEntity = new StringEntity(entityString, UTF_8);
        httpPost.setEntity(stringEntity);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 2023.10.10
        HttpClientContext context = HttpClientContext.create();
        // 2023.10.10

        try {
            CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpPost);

            // 2023.10.10
            HttpHost target = context.getTargetHost();
            List<URI> redirect = context.getRedirectLocations();
            URI location = URIUtils.resolve(httpPost.getURI(), target, redirect);
            log.info("[调试信息] [httpExecute] Final HTTP location：{}", location.toASCIIString());
            // 2023.10.10

            if (closeableHttpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                try {
                    closeableHttpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                log.info("[调试信息] [httpExecute] [ERROR] 打印 [Status Line] = [{}]", closeableHttpResponse.getStatusLine());
                Reporter.log("【调试信息】【httpExecute】【ERROR】 打印 【Status Line】 = 【" + closeableHttpResponse.getStatusLine() + "】");
            }

            String response = EntityUtils.toString(closeableHttpResponse.getEntity(), UTF_8);
            log.info("[调试信息] [httpExecute] 打印响应报文：{}", response);
            Reporter.log("【调试信息】【httpExecute】 打印响应报文：" + response);

            if (StringUtils.isEmpty(response)) {
                log.info("[调试信息] [httpExecute] 响应报文 [response == null]，方法终止执行。抛出 [IllegalArgumentException] 异常。");
                Reporter.log("【调试信息】【httpExecute】 响应报文 【response == null】，方法终止执行。抛出 【IllegalArgumentException】 异常。");
                throw new IllegalArgumentException();
            }

            /**
             * 缓存 [testApi] 的 response
             */
            CacheData.testApiHttpResponseCache = response;
            return response;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
