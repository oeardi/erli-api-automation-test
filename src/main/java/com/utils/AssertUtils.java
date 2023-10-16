package com.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.testng.Reporter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AssertUtils
 *
 * @Author: 冷枫红舞
 */
@Slf4j
public class AssertUtils {

    public static final String JSON_PATH_CODE = "$Code";
    public static final String JSON_PATH_CODE_1 = "$code";
    public static final String CODE_00000 = "00000";
    public static final String JSON_PATH_MESSAGE = "$Message";
    public static final String JSON_PATH_MESSAGE_1 = "$message";
    public static final String MESSAGE_SUCCESSFUL = "SUCCESSFUL";
    public static final String JSON_PATH_STATUS = "$Status";
    public static final String JSON_PATH_STATUS_1 = "$status";
    public static final String JSON_PATH_DATA = "$Data";
    public static final String JSON_PATH_DATA_1 = "$data";
    public static final String JSON_PATH_DATA_POS_ID = "$Data.PosID";
    public static final String JSON_PATH_DATA_PTSP_ID = "$Data.PtspID";
    public static final String JSON_PATH_DATA_AUTHORIZATION = "$Data.Authorization";
    public static final String JSON_PATH_DATA_AUTH_TOKEN = "$data.authToken.value";
    public static final String JSON_PATH_DATA_RANDOM_NUMBER = "$data.randomNumber";
    public static final String JSON_PATH_DATA_TOKEN = "$data.token";
    public static final String JSON_PATH_DATA_TRANSACTION_NO = "$data.transactionNo";
    public static final String JSON_PATH_DATA_TRANSACTION_STATUS = "$data.transactionStatus";
    public static final String JSON_PATH_DATA_TRANSACTION_AMOUNT = "$data.transactionAmount";
    public static final String JSON_PATH_DATA_acquirer = "$Data.acquirer";
    public static final String JSON_PATH_DATA_agent_num = "$Data.agent_num";
    public static final String JSON_PATH_DATA_agent_type = "$Data.agent_type";
    public static final String JSON_PATH_DATA_agent_status = "$Data.agent_status";
    public static final String JSON_PATH_DATA_terminalList = "$Data.terminalList";
    public static final String JSON_PATH_DATA_channelId = "$Data.channelId";
    public static final String JSON_PATH_DATA_Reference = "$Data.Reference";
    public static final String JSON_PATH_DATA_orderNo = "$Data.orderNo";
    public static final String JSON_PATH_DATA_transactionTypeList = "$Data.transactionTypeList";
    public static final String JSON_PATH_DATA_orderStatusList = "$Data.orderStatusList";
    public static final String JSON_PATH_DATA_snList = "$Data.snList";

    /**
     * 断言 jsonPath 的取值与 expect 相等
     *
     * @param httpResponse 响应报文
     * @param jsonPath     jsonPath
     * @param expect       预期值
     */
    public static void assertEquals(String httpResponse, String jsonPath, Object expect) {
        /**
         * 判断是否为 null
         */
        if (StringUtils.isEmpty(httpResponse) || StringUtils.isEmpty(jsonPath)) {
            log.info("[调试信息] [ERROR] [assertEquals] httpResponse 或 jsonPath 参数为 null.");
            throw new IllegalArgumentException();
        }

        assertThat(JSONPath.eval(JSONObject.parseObject(httpResponse), jsonPath)).isEqualTo(expect);
        log.info("[调试信息] [assertEquals] [断言通过] [{}] 的取值等于预期的：[{}]", jsonPath, expect);
        Reporter.log("【调试信息】【assertEquals】 【断言通过】 【" + jsonPath + "】 的取值等于预期的：【" + expect + "】");
    }

    /**
     * 金额断言
     *
     * @param before      期初金额。（从数据库获取，然后保存在 “缓存变量” 中。）
     * @param transAmount 交易金额
     * @param transFee    交易手续费
     * @param commission  佣金
     * @param otherFee    其它手续费
     * @param fundFlow    资金方向
     * @param after       期末金额
     */
    public static void assertEquals(long before, long transAmount, long transFee, long commission, long otherFee,
                                    String fundFlow, long after) {
        log.info("[调试信息] [assertEquals] before 期初金额：[{}]", before);
        log.info("[调试信息] [assertEquals] transAmount 交易金额：[{}]", transAmount);
        log.info("[调试信息] [assertEquals] transFee 交易手续费：[{}]", transFee);
        log.info("[调试信息] [assertEquals] commission 佣金：[{}]", commission);
        log.info("[调试信息] [assertEquals] otherFee 其它手续费：[{}]", otherFee);
        log.info("[调试信息] [assertEquals] fundFlow 资金方向：[{}]", fundFlow);
        log.info("[调试信息] [assertEquals] after 期末金额：[{}]", after);

        long sum = 0L;
        if (fundFlow.equals("+")) {
            sum = before + transAmount - transFee - commission - otherFee;
        } else {
            sum = before - transAmount - transFee - commission - otherFee;
        }
        assertThat(sum).isEqualTo(after);
        log.info("[调试信息] [assertEquals] [断言通过] [{} {} {} - {} - {} - {} = {}] 等于预期的：[{}]", before, fundFlow, transAmount,
                transFee, commission, otherFee, sum, after);
    }

    /**
     * 断言 jsonPath 的取值【不等于】 expect
     *
     * @param httpResponse 响应报文
     * @param jsonPath     jsonPath
     * @param expect       预期值
     */
    public static void assertNotEquals(String httpResponse, String jsonPath, Object expect) {
        /**
         * 判断是否为 null
         */
        if (StringUtils.isEmpty(httpResponse) || StringUtils.isEmpty(jsonPath)) {
            log.info("[调试信息] [ERROR] [assertNotEquals] httpResponse 或 jsonPath 参数为 null.");
            throw new IllegalArgumentException();
        }

        String real = (String) JSONPath.eval(JSONObject.parseObject(httpResponse), jsonPath);
        assertThat(real).isNotEqualTo(expect);
        log.info("[调试信息] [assertNotEquals] [断言通过] [{}] 的取值为 [{}]，不等于预期的：[{}]", jsonPath, real, expect);
        Reporter.log("【调试信息】【assertNotEquals】 【断言通过】 【" + jsonPath + "】 的取值为 【" + real + "】，不等于预期的：【" + expect + "】");
    }

    /**
     * 断言：响应报文 code == 00000
     *
     * @param httpResponse 响应报文
     */
    public static void assertEqualsCode_00000(String httpResponse) {
        assertEquals(httpResponse, JSON_PATH_CODE_1, CODE_00000);
    }

    /**
     * 断言：响应报文 code 与 “指定的 code”（响应码）相等
     *
     * @param httpResponse 响应报文
     * @param codeValue    指定的 code（响应码）
     */
    public static void assertEqualsCode(String httpResponse, String codeValue) {
        assertEquals(httpResponse, JSON_PATH_CODE_1, codeValue);
    }

    /**
     * 断言：响应报文 message == SUCCESSFUL
     *
     * @param httpResponse 响应报文
     */
    public static void assertEquestMessage_SUCCESSFUL(String httpResponse) {
        assertEquals(httpResponse, JSON_PATH_MESSAGE_1, MESSAGE_SUCCESSFUL);
    }

    /**
     * 断言：判断为空
     *
     * @param response 响应报文
     * @param jsonPath jsonPath
     */
    public static void assertIsNull(String response, String jsonPath) {
        if (StringUtils.isEmpty(response) || StringUtils.isEmpty(jsonPath)) {
            log.info("[调试信息] [ERROR] [assertIsNull] 输入参数中的 response 或 jsonPath 为 null.");
            Reporter.log("【调试信息】【ERROR】【assertIsNull】 输入参数 response 或 jsonPath 为 null.");
            throw new IllegalArgumentException();
        }

        Object value = JSONPath.eval(JSONObject.parseObject(response), jsonPath);
        assertThat(value).isNull();
        log.info("[调试信息] [assertIsNull] [断言通过] [{}] 的取值为预期的：[null]", jsonPath);
        Reporter.log("【调试信息】【assertIsNull】 【断言通过】 【" + jsonPath + "】 的取值为预期的：【null】");
    }

    /**
     * 断言：判断不为空
     *
     * @param response 响应报文
     * @param jsonPath jsonPath
     */
    public static void assertNotNull(String response, String jsonPath) {
        if (StringUtils.isEmpty(response) || StringUtils.isEmpty(jsonPath)) {
            log.info("[调试信息] [ERROR] [assertNotNull] 输入参数中的 response 或 jsonPath 为 null.");
            Reporter.log("【调试信息】【ERROR】【assertNotNull】 输入参数 response 或 jsonPath 为 null.");
            throw new IllegalArgumentException();
        }

        Object value = JSONPath.eval(JSONObject.parseObject(response), jsonPath);
        assertThat(value).isNotNull();
        log.info("[调试信息] [assertNotNull] [断言通过] [{}] 的取值为预期的：[not null]", jsonPath);
        Reporter.log("【调试信息】【assertNotNull】 【断言通过】 【" + jsonPath + "】 的取值为预期的：【not null】");
    }

    /**
     * 断言：响应报文 data 不能为 null
     *
     * @param httpResponse 响应报文
     */
    public static void assertNotNull_Data(String httpResponse) {
        assertNotNull(httpResponse, JSON_PATH_DATA_1);
    }

    /**
     * 断言：是否【包含】指定的 “字符串”
     *
     * @param httpResponse  响应报文
     * @param jsonPath      jsonPath
     * @param containString 需要包含的 “字符串”
     */
    public static void assertContains(String httpResponse, String jsonPath, String containString) {
        Object tempString = JSONPath.eval(JSONObject.parseObject(httpResponse), jsonPath);
//        log.info("[调试信息] [assertContains] [httpResponse] ：[{}]", httpResponse);
//        log.info("[调试信息] [assertContains] [jsonPath] ：[{}]", jsonPath);
//        log.info("[调试信息] [assertContains] [containString] ：[{}]", containString);
//        log.info("[调试信息] [assertContains] [tempString] ：[{}]", tempString);
        assertThat(StringUtils.contains(tempString + "", containString)).isEqualTo(true);
        log.info("[调试信息] [assertContains] [断言通过] [{}] 的取值包含预期的：[{}]", jsonPath, containString);
        Reporter.log("【调试信息】【assertContains】 【断言通过】 【" + jsonPath + "】 的取值包含预期的：【" + containString + "】");
    }

}
