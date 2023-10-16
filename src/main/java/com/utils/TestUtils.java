package com.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.UniversalAppHeaderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.util.Map;

import static com.common.CacheData.*;
import static com.url.OfflinePaymentApiUserUrl.*;
import static com.utils.HttpRequestUtils.doPost;

/**
 * AggregatorTestUtils
 *
 * @Author: 冷枫红舞
 */
@Slf4j
public class TestUtils {

    private TestUtils() {
    }

    /**
     * 获取测试环境 [test, uat, pro]
     */
    public static void setEnvironmentHost() {
//        log.info("[调试信息] [setEnvironmentHost] 开始执行");
        Map<String, Object> yamlToMap = (Map<String, Object>) new YamlFileUtils().getMapWithYamlFile("/environment/choose_env.yaml", Map.class);
        env = (String) yamlToMap.get("env");
        log.info("[调试信息] [setEnvironmentHost] 当前测试环境：[{}]", env);
        if (StringUtils.isEmpty(env)) {
            log.info("[调试信息] [setEnvironmentHost] 文件 [/environment/choose_env.yaml] 为空。方法终止执行，手动抛出 IllegalArgumentException 异常。");
            throw new IllegalArgumentException();
        }

        Map<String, Object> map = null;
        switch (env) {
            case "test":
                map = (Map<String, Object>) new JsonFileUtils().getObjectWithJsonFile("/environment/env_test.json", Map.class);
                break;
            case "uat":
                map = (Map<String, Object>) new JsonFileUtils().getObjectWithJsonFile("/environment/env_uat.json", Map.class);
                break;
            case "pro":
                map = (Map<String, Object>) new JsonFileUtils().getObjectWithJsonFile("/environment/env_pro.json", Map.class);
                break;
            default:
                log.info("[调试信息] [setEnvironmentHost] 没有匹配到 [/environment/env_{}.json] 文件，请确认文件是否存在。", env);
        }
        assert map != null;
        host = (String) map.get("host");
        if (StringUtils.isEmpty(host)) {
            host = HOST_URL;
        }
        privateKey = (String) map.get("privateKey");
        publicKey = (String) map.get("publicKey");
        log.info("[调试信息] [setEnvironmentHost] host = {}", host);
        log.info("[调试信息] [setEnvironmentHost] privateKey = {}", privateKey);
        log.info("[调试信息] [setEnvironmentHost] publicKey = {}", publicKey);
    }


    /**
     * 从 json_file/header_info_app.json 文件中，获取通用的 header 数据。
     *
     * @return Map
     */
    public static Map<String, Object> getUniversalAppHeader() {
//        log.info("[调试信息] [getUniversalAppHeader] 开始获取 [header] 的值... {ฅ՞•ﻌ•՞ฅ}");
        if (StringUtils.isEmpty(env)) {
            log.info("[调试信息] [getUniversalAppHeader] 文件 [/environment/choose_env.yaml] 为空。方法终止执行，手动抛出 IllegalArgumentException 异常。");
            throw new IllegalArgumentException();
        }

        String headerInfoFile = null;
        switch (env) {
            case "test":
                headerInfoFile = "/header_info/test/header_info_app.json";
                break;
            case "uat":
                headerInfoFile = "/header_info/uat/header_info_app.json";
                break;
            case "pro":
                headerInfoFile = "/header_info/pro/header_info_app.json";
                break;
        }

        Map<String, Object> universalAppMap = (Map<String, Object>) new JsonFileUtils().getObjectWithJsonFile(headerInfoFile, Map.class);
        if (MapUtils.isEmpty(universalAppMap)) {
            log.info("[调试信息] [getUniversalAppHeader] 获取 [header_info.json] 内容为 [null]，方法终止执行。抛出 IllegalArgumentException 异常。");
            throw new IllegalArgumentException();
        }

        /**
         * 如果不对请求报文加密，则 Header 的 encryptToggle 值为 0
         * （api-user 逻辑）
         */
        encryptToggle = (String) universalAppMap.get("encryptToggle");

        for (Map.Entry<String, Object> entry : universalAppMap.entrySet()) {
//            log.info("[调试信息] [getUniversalAppHeader] 打印 [header.json] 文件的内容：[{} = {}]", entry.getKey(), entry.getValue());
        }
        return universalAppMap;
    }


    /**
     * 获取 token
     *
     * @param yamlFile Case.yaml文件所在路径
     */
    public static void getAuthToken(String yamlFile) {
        log.info("[调试信息] [getAuthToken] 开始执行... {ฅ՞•ﻌ•՞ฅ}");
        if (StringUtils.isEmpty(env)) {
            log.info("[调试信息] [getAuthToken] 文件 [/environment/choose_env.yaml] 为空。方法终止执行，手动抛出 IllegalArgumentException 异常。");
            throw new IllegalArgumentException();
        }
        if (StringUtils.isEmpty(yamlFile)) {
            log.info("[调试信息] [getAuthToken] 参数 [yamlFile] 为空，方法终止执行。请检查 [TestNg.xml] 文件 <parameter> 的值是否正确。");
            throw new IllegalArgumentException();
        }

        /**
         * 根据 Case.yaml 文件所在路径（test_case_yaml/test_senario_156 或 test_case_yaml/test_senario_256），
         * 获取对应的 login_user_info，用于后续获取到 156 或 256 用户的 token 值。
         */
        String loginUserInfoFile = getUserInfoJsonFile(yamlFile, env);
        log.info("[调试信息] [getAuthToken] yamlFile = {}", yamlFile);
        log.info("[调试信息] [getAuthToken] userJsonFile = {}", loginUserInfoFile);
        assert loginUserInfoFile != null;

        if (StringUtils.isEmpty(tokenCache)) {
            Map<String, Object> universalHeaderMap = getUniversalAppHeader();
            log.info("[调试信息] [getAuthToken] universalHeaderMap = {}", universalHeaderMap);
            Map<String, Object> bodyMap = (Map<String, Object>) new JsonFileUtils().getObjectWithJsonFile(loginUserInfoFile, Map.class);
            String httpResponse = doPost(host + beforeLoginSecurityValidationUrl, universalHeaderMap, bodyMap);

            String valChainId = JsonPathUtils.getJsonStringValue(httpResponse, "$data.valChainId");
            if (loginUserInfoFile.contains("merchantNo")) {
                log.info("[调试信息] [getAuthToken] 当使用 [KA] 登录时，[beforeLoginSecurityValidation] 接口返回的 [valChainId] 字段值为：[null]。");
            }

            bodyMap.put("userSignProtocolFlag", "1");
            bodyMap.put("valChainId", valChainId);
            httpResponse = doPost(host + loginSecurityValidationUrl, universalHeaderMap, bodyMap);
            tokenCache = JsonPathUtils.getJsonStringValue(httpResponse, "$data.authToken.value");
            assert tokenCache != null;
        }
        log.info("[调试信息] [getAuthToken] 获取 [authToken] 值为：{}", tokenCache);
    }

    /**
     * 获取 login_user_info/{env}/{role}_login_user_info.json 文件所在路径
     *
     * @param yamlFile Case.yaml 文件所在路径
     * @param env      Case 运行环境
     * @return user_info.json
     */
    private static String getUserInfoJsonFile(String yamlFile, String env) {
        if (StringUtils.isEmpty(yamlFile) || StringUtils.isEmpty(env)) {
            log.info("[调试信息] [getUserInfoJsonFile] 入参为空，方法终止执行，手动抛出 IllegalArgumentException 异常。");
            throw new IllegalArgumentException();
        }
        if (yamlFile.contains("test_senario_156")) {
            return "/login_user_info/" + env + "/agent_login_user_info.json";
        } else if (yamlFile.contains("test_senario_256")) {
            return "/login_user_info/" + env + "/ka_login_user_info.json";
        } else {
            return null;
        }
    }


    @Test
    public void testSetEnvironmentHost() {
        setEnvironmentHost();
    }

    @Test
    public void testGetUniversalAppHeader() {
        setEnvironmentHost();
        UniversalAppHeaderEntity universalAppHeaderEntity = JSONObject.toJavaObject((JSON) getUniversalAppHeader(), UniversalAppHeaderEntity.class);
        log.info("universalAppHeaderEntity = {}", universalAppHeaderEntity);
    }

    @Test
    public static void testGetAuthToken() {
        setEnvironmentHost();
        getAuthToken("/test_case_yaml/test_senario_156/156_transfer_to_256/refactorCreateOrder_955.yaml");
        log.info("[Test Case] 打印 缓存的 [token] 值为：{}", tokenCache);
    }

}
