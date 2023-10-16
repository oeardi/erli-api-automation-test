package com.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.Reporter;

import java.util.*;

import static com.common.CacheData.*;
import static com.common.CommonData.Keywork.*;
import static com.common.CommonData.assertAmountKeyword.ASSERT_AMOUNT;
import static com.common.CommonData.assertKeyword.ASSERTS;
import static com.utils.BaseAssertOperateUtils.doAsserts;
import static com.utils.BaseAssertOperateUtils.doAssertsAmount;
import static com.utils.BaseDBOperateUtils.dbOperate;
import static com.utils.HttpRequestUtils.doPost;
import static com.utils.HttpRequestUtils.preDoPost;
import static com.utils.TestUtils.getUniversalAppHeader;

/**
 * AutomationEngineUtils
 *
 * @Author: 冷枫红舞
 */
@Slf4j
public class AutomationEngineUtils {

    private AutomationEngineUtils() {
    }

    /**
     * @param yamlFilePath file_name.yaml and file_path
     * @return String
     */
    public static String apiAuto(String yamlFilePath) {
        log.info("[调试信息] [apiAuto] [==========================================================================================]");
        log.info("[调试信息] [apiAuto] [测试开始] [↓] [↓] [↓]");

        if (StringUtils.isEmpty(yamlFilePath)) {
            log.info("[调试信息] [apiAuto] 传入参数 [yamlFilePath == null]，apiAuto() 方法终止执行。[return null;]");
            return null;
        }

        Map<String, Object> yamlToMap = (Map<String, Object>) new YamlFileUtils().getMapWithYamlFile(yamlFilePath, Map.class);
        title = (String) yamlToMap.get(TITLE);
        log.info("[调试信息] [apiAuto] 测试用例名称：[{}]", title);
        Reporter.log("【调试信息】【apiAuto】 测试用例名称 【" + title + "】");

        description = (String) yamlToMap.get(DESCRIPTION);
        log.info("[调试信息] [apiAuto] 用例描述信息：[{}]", description);
        Reporter.log("【调试信息】【apiAuto】 用例描述信息 【" + description + "】");

        List<String> initDataSqlList = (List<String>) yamlToMap.get(INIT_DATA_SQL);
        if (CollectionUtils.isNotEmpty(initDataSqlList)) {
            for (String sql : initDataSqlList) {
//                log.info("[调试信息] [apiAuto] 打印初始化数据的 SQL 语句：[{}]", sql);
                dbOperate(sql, "init");
            }
        }

        /**
         * [preApi]
         */
        List<String> preApiList = (List<String>) yamlToMap.get(PRE_API);
        Map<String, Object> thisMapBeUsedInTestApiBodyMap = new HashMap<>(16);
        if (CollectionUtils.isEmpty(preApiList)) {
            log.info("[调试信息] [apiAuto] [Case.yaml] 文件中的 [preApi] 为 [null]，当前 [测试用例] 不需要调用前置业务接口。");
        } else {
            Iterator iterator = preApiList.iterator();
            int i = 1, j = preApiList.size();
            while (iterator.hasNext()) {
                LinkedHashMap<String, Object> preApiMap = (LinkedHashMap<String, Object>) iterator.next();
                log.info("[调试信息] [apiAuto] 循环 [{}/{}] 打印 [preApi] 的内容：{} ", i++, j, preApiMap);

                String preUrl = (String) preApiMap.get(PRE_REQUEST_URL);
                if (StringUtils.isEmpty(preUrl)) {
                    log.info("[调试信息] [apiAuto] 请求参数 [preUrl] 为 null，方法终止执行。抛出 IllegalArgumentException 异常。");
                    throw new IllegalArgumentException();
                }
                if (!preUrl.contains("http")) {
                    preUrl = host + preUrl;
                }

                /**
                 * [preApi - preRequestBody]
                 */
                Map<String, Object> preBodyMap = (Map<String, Object>) preApiMap.get(PRE_REQUEST_BODY);
                if (MapUtils.isEmpty(preBodyMap)) {
//                    log.info("[调试信息] [apiAuto] 请求参数 [preBody] 为 null。");
                }

                LinkedHashMap<String, Object> replaceVariateMap = (LinkedHashMap<String, Object>) yamlToMap.get(REPLACE_VARIATE);
                log.info("[调试信息] [apiAuto] [preApi] 打印用于替换 body 的变量：{}", replaceVariateMap);
                if (MapUtils.isNotEmpty(preBodyMap) && MapUtils.isNotEmpty(replaceVariateMap)) {
                    String tempBodyString = JSONObject.toJSONString(preBodyMap);
                    log.info("[调试信息] [apiAuto] [preApi] 打印（替换前）body 值：{}", tempBodyString);
                    for (Map.Entry<String, Object> replaceVariateMapEntry : replaceVariateMap.entrySet()) {
                        String key = replaceVariateMapEntry.getKey();
                        String value = (String) replaceVariateMapEntry.getValue();
                        log.info("[调试信息] [apiAuto] [preApi] 循环打印 yaml 文件中 [replaceVariate] 的内容：[key = {}, value = {}]", key, value);
                        if (tempBodyString.contains(key)) {
                            tempBodyString = tempBodyString.replace(key, value);
                        }
                    }
                    log.info("[调试信息] [apiAuto] [preApi] 打印（替换后）body 值：{}", tempBodyString);
                    preBodyMap = JSONObject.parseObject(tempBodyString, Map.class);
                }


                for (Map.Entry<String, Object> entry : preBodyMap.entrySet()) {
                    String entryGetValue = JSONObject.toJSONString( entry.getValue());
//                    log.info("[调试信息] [apiAuto] 打印 entryGetValue = {}", entryGetValue);
                    if (entryGetValue.startsWith("$")) {
                        String tempValue = JsonPathUtils.getJsonStringValue(httpResponseCache, entryGetValue);
//                        log.info("[调试信息] [apiAuto] 打印 tempValue = {}", tempValue);
                        preBodyMap.put(entry.getKey(), tempValue);
                    }
                }

                Map<String, Object> preApiBaseUniversalHeaderMap = getUniversalAppHeader();
//                log.info("[调试信息] [apiAuto] preApiBaseUniversalHeaderMap = {}", preApiBaseUniversalHeaderMap);
                Map<String, Object> preHeaderMap = (Map<String, Object>) preApiMap.get(PRE_REQUEST_HEADER);
                if (MapUtils.isNotEmpty(preHeaderMap)) {
                    preApiBaseUniversalHeaderMap.putAll(preHeaderMap);
                }
//                preEncryptFlag = (String) preApiMap.get(PRE_ENCRYPT_FLAG);

                Map<String, Object> preDependentParameter = (Map<String, Object>) preApiMap.get(PRE_DEPENDENT_PARAMETER);
                Map<String, Object> tempReturnMap = preDoPost(preUrl, preApiBaseUniversalHeaderMap, preBodyMap, preDependentParameter);
                if (MapUtils.isNotEmpty(tempReturnMap)) {
                    thisMapBeUsedInTestApiBodyMap.putAll(tempReturnMap);
                }
                log.info("[调试信息] [apiAuto] 打印（即将）用于 [testApi] 接口的 body 参数的值：{}", thisMapBeUsedInTestApiBodyMap);
            }
        }

        /**
         * [testApi]
         */
        String httpResponse = null;
        LinkedHashMap<String, Object> testApiMap = (LinkedHashMap<String, Object>) yamlToMap.get(TEST_API);
        if (MapUtils.isEmpty(testApiMap)) {
            log.info("[调试信息] [apiAuto] [Case.yaml] 文件中的 [testApi] 内容为 NULL，apiAuto() 方法终止执行。（抛出 IllegalArgumentException 异常）");
            throw new IllegalArgumentException();
        } else {
            String url = (String) testApiMap.get(REQUEST_URL);
            if (!url.contains("http")) {
                url = host + url;
            }

            Map<String, Object> headerMap = (Map<String, Object>) testApiMap.get(REQUEST_HEADER);

            Map<String, Object> apiBaseUniversalHeaderMap = getUniversalAppHeader();
//            log.info("[调试信息] [apiAuto] apiBaseUniversalHeaderMap = {}", apiBaseUniversalHeaderMap);
            if (MapUtils.isNotEmpty(headerMap)) {
                apiBaseUniversalHeaderMap.putAll(headerMap);
            }
//            encryptFlag = (String) testApiMap.get(ENCRYPT_FLAG);

            /**
             * [testApi -> reqeustBody]
             */
            Map<String, Object> bodyMap = (Map<String, Object>) testApiMap.get(REQUEST_BODY);
//            log.info("[调试信息] [apiAuto] 打印 [bodyMap] 的内容：{}", bodyMap);

            /**
             * [replaceVariate]
             * 如果 [testApi != null] 并且 [bodyMap != null] 并且 [replaceVariate != null]
             */
            LinkedHashMap<String, Object> replaceVariateMap = (LinkedHashMap<String, Object>) yamlToMap.get(REPLACE_VARIATE);
            log.info("[调试信息] [apiAuto] [testApi] 打印用于替换 body 的变量：{}", replaceVariateMap);
            if (MapUtils.isNotEmpty(bodyMap) && MapUtils.isNotEmpty(replaceVariateMap)) {
                String tempBodyString = JSONObject.toJSONString(bodyMap);
                log.info("[调试信息] [apiAuto] [testApi] 打印（替换前）body 值：{}", tempBodyString);
                for (Map.Entry<String, Object> replaceVariateMapEntry : replaceVariateMap.entrySet()) {
                    String key = replaceVariateMapEntry.getKey();
                    String value = (String) replaceVariateMapEntry.getValue();
                    log.info("[调试信息] [apiAuto] [testApi] 循环打印 yaml 文件中 [replaceVariate] 的内容：[key = {}, value = {}]", key, value);
                    if (tempBodyString.contains(key)) {
                        tempBodyString = tempBodyString.replace(key, value);
                    }
                }
                log.info("[调试信息] [apiAuto] [testApi] 打印（替换后）body 值：{}", tempBodyString);
                bodyMap = JSONObject.parseObject(tempBodyString, Map.class);
            }

            /**
             * 将依赖接口获取到的 body 参数，拼装给当前请求的 bodyMap。
             */
            if (MapUtils.isNotEmpty(bodyMap) && MapUtils.isNotEmpty(thisMapBeUsedInTestApiBodyMap)) {
                bodyMap.putAll(thisMapBeUsedInTestApiBodyMap);
            }
            httpResponse = doPost(url, apiBaseUniversalHeaderMap, bodyMap);
        }

        /**
         * [validationDataSql]
         */
        List<String> validationDataSqlList = (List<String>) yamlToMap.get(VALIDATION_DATA_SQL);
        if (CollectionUtils.isNotEmpty(validationDataSqlList)) {
            for (String sql : validationDataSqlList) {
//                log.info("[调试信息] [apiAuto] [validationDataSqlList] 打印 SQL 语句：[{}]", sql);
                dbOperate(sql, "validation");
            }
        }

        /**
         * [asserts]
         */
        List<String> assertsList = (List<String>) yamlToMap.get(ASSERTS);
        if (CollectionUtils.isNotEmpty(assertsList) && StringUtils.isNotEmpty(httpResponse)) {
            doAsserts(assertsList, httpResponse);
        } else {
            log.info("[调试信息] [apiAuto] yaml 文件中 [asserts] 为 null，当前测试没有指定断言。将执行通用断言，判断 [code] 是否为：[00000]。");
            AssertUtils.assertEqualsCode_00000(httpResponse);
        }

        /**
         * [assertAmount]
         */
        List<String> assertAmountList = (List<String>) yamlToMap.get(ASSERT_AMOUNT);
        if (CollectionUtils.isNotEmpty(assertAmountList)) {
            doAssertsAmount(assertAmountList);
        }

        return httpResponse;
    }

}
