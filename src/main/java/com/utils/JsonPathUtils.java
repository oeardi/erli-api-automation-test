package com.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

/**
 * JsonPathUtils
 *
 * @Author: 冷枫红舞
 */
@Slf4j
public class JsonPathUtils {

    /**
     * 解析 jsonpath
     *
     * @param httpResponse httpResponse
     * @param jsonPath     jsonPath
     * @return String
     */
    public static String getJsonStringValue(String httpResponse, String jsonPath) {
        /**
         * 判断是否为 null
         */
        if (StringUtils.isEmpty(httpResponse) || StringUtils.isEmpty(jsonPath)) {
            log.info("[调试信息] [getJsonStringValue] httpResponse 或 jsonPath 参数为 [null]，当前方法不执行，方法 [return null;]。");
//            Reporter.log("【调试信息】 [getJsonStringValue] httpResponse 或 jsonPath 参数为 null.");
            return null;
        }

        JSONObject jsonObject = JSON.parseObject(httpResponse);
        String value = (String) JSONPath.eval(jsonObject, jsonPath);
        if (StringUtils.isEmpty(value)) {
            log.info("[调试信息] [getJsonStringValue] [{}] 获取到的值为：[null]，方法 [return null;]。", jsonPath);
            return null;
        } else {
            log.info("[调试信息] [getJsonStringValue] [{}] 获取到的值为：[{}]。", jsonPath, value);
//            Reporter.log("【调试信息】 [getJsonStringValue] 获取 jsonPath [" + jsonPath + "] 的值为：[" + value + "]");
            return value;
        }
    }

    @Test
    public void testGetJsonPathValue() {
        String httpResponse = "{\"code\":\"00000\",\"message\":\"SUCCESSFUL\",\"data\":{\"merchantName\":\"Francisca Amaka Onyejekwe\",\"currentMonthGtv\":\"0.00\",\"currentMonthGtvIncreaseRate\":\"0.00\",\"transList\":[{\"dt\":\"2023-09-20\",\"volume\":\"0\",\"transaction\":\"0.00\",\"transactionGapForLastDay\":\"0.00\"},{\"dt\":\"2023-09-19\",\"volume\":\"0\",\"transaction\":\"0.00\",\"transactionGapForLastDay\":\"0.00\"},{\"dt\":\"2023-09-18\",\"volume\":\"0\",\"transaction\":\"0.00\",\"transactionGapForLastDay\":\"0.00\"},{\"dt\":\"2023-09-17\",\"volume\":\"0\",\"transaction\":\"0.00\",\"transactionGapForLastDay\":\"0.00\"},{\"dt\":\"2023-09-16\",\"volume\":\"0\",\"transaction\":\"0.00\",\"transactionGapForLastDay\":\"0.00\"},{\"dt\":\"2023-09-15\",\"volume\":\"0\",\"transaction\":\"0.00\",\"transactionGapForLastDay\":\"0.00\"},{\"dt\":\"2023-09-14\",\"volume\":\"0\",\"transaction\":\"0.00\",\"transactionGapForLastDay\":\"0.00\"}],\"state\":null}}";
        String jsonPath = "$code";
        getJsonStringValue(httpResponse, jsonPath);
    }

}
