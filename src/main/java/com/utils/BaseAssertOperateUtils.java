package com.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.util.*;

import static com.common.CacheData.*;
import static com.common.CommonData.assertAmountKeyword.*;
import static com.common.CommonData.assertKeyword.*;
import static com.utils.AssertUtils.*;
import static com.utils.BaseDBOperateUtils.selectWithWhere;
import static com.utils.JdbcUtils.getConnection;
import static com.utils.JdbcUtils.getStatement;

/**
 * 断言相关操作
 *
 * @Author: 冷枫红舞
 */
@Slf4j
public class BaseAssertOperateUtils {

    private BaseAssertOperateUtils() {
    }

    public static final String IS_EQUALS_TO = "=";
    public static final String NOT_EQUALS_TO = "!=";
    public static final String IS_NULL = "null";
    public static final String NOT_NULL = "!null";
    public static final String CONTAINS = "contains";

    /**
     * 解析并执行 yaml 断言部分
     *
     * @param assertsList  yaml 文件 [asserts] 部分
     * @param httpResponse 响应报文
     */
    public static void doAsserts(List<String> assertsList, String httpResponse) {
//        log.info("[调试信息] [doAssert] 开始执行：");
        if (CollectionUtils.isEmpty(assertsList) || StringUtils.isEmpty(httpResponse)) {
            log.info("[调试信息] [doAssert] 参数 [assertsList == null] 或 [httpResponse == null]，程序不执行 [asserts] 断言。");
            return;
        } else {
            Iterator iterator = assertsList.iterator();
            int i = 1, j = assertsList.size();
            while (iterator.hasNext()) {
                LinkedHashMap<String, Object> assertMap = (LinkedHashMap<String, Object>) iterator.next();
//                log.info("[调试信息] [doAssert] 循环 [{}/{}] 打印 [assertMap] 的内容：{} ", i++, j, assertMap);

                String valuePath = (String) assertMap.get(VALUE_PATH);
                String condition = (String) assertMap.get(CONDITION);
                String expect = (String) assertMap.get(EXPECT);
                if (StringUtils.isEmpty(valuePath) || StringUtils.isEmpty(condition)) {
                    log.info("[调试信息] [doAsserts] 获取到的 [valuePath == null] 或 [condition == null] 跳过本次断言。打印本次断言 assertMap 参数：{}", assertMap);
                } else {
                    switch (condition) {
                        case IS_EQUALS_TO:
                            if (StringUtils.isNotEmpty(expect)) {
                                assertEquals(httpResponse, valuePath, expect);
                            } else {
                                log.info("[调试信息] [doAsserts] 获取到的 [expect == null] 跳过 IS_EQUALS_TO 断言。");
                            }
                            break;
                        case NOT_EQUALS_TO:
                            if (StringUtils.isNotEmpty(expect)) {
                                assertNotEquals(httpResponse, valuePath, expect);
                            } else {
                                log.info("[调试信息] [doAsserts] 获取到的 [expect == null] 跳过 NOT_EQUALS_TO 断言。");
                            }
                            break;
                        case IS_NULL:
                            assertIsNull(httpResponse, valuePath);
                            break;
                        case NOT_NULL:
                            assertNotNull(httpResponse, valuePath);
                            break;
                        case CONTAINS:
                            if (StringUtils.isNotEmpty(expect)) {
                                assertContains(httpResponse, valuePath, expect);
                            } else {
                                log.info("[调试信息] [doAsserts] 获取到的 [expect == null] 跳过 CONTAINS 断言。");
                            }
                            break;
                        default:
                            log.info("[调试信息] [doAsserts] 没有匹配到 [condition = {}] 请确认 [yaml] 文件中的元素填写是否正确。", condition);
                    }
                }
            }
        }
    }

    /**
     * 解析并执行 yaml 断言部分
     *
     * @param assertsList yaml 文件 [assertAmount] 部分
     */
    public static void doAssertsAmount(List<String> assertsList) {
        log.info("[调试信息] [doAssertsAmount] 开始执行：");
        if (CollectionUtils.isEmpty(assertsList)) {
            log.info("[调试信息] [doAssertsAmount] 参数 [assertsList == null]，程序不执行 doAssertsAmount() 方法。[return;]");
            return;
        } else {
            Iterator iterator = assertsList.iterator();
            int i = 1, j = assertsList.size();
            while (iterator.hasNext()) {
                LinkedHashMap<String, Object> assertMap = (LinkedHashMap<String, Object>) iterator.next();
                log.info("[调试信息] [doAssertsAmount] 循环 [{}/{}] 打印 [assertMap] 的内容：{} ", i++, j, assertMap);

                String transAmountSql = (String) assertMap.get(TRANS_AMOUNT);
                log.info("[调试信息] [doAssertsAmount] transAmountSql = {}", transAmountSql);
                Long transAmount = queryAmountOrFee(transAmountSql);
                log.info("[调试信息] [doAssertsAmount] transAmount = {}", transAmount);

                String transFeeSql = (String) assertMap.get(TRANS_FEE);
                Long transFee = queryAmountOrFee(transFeeSql);
                log.info("[调试信息] [doAssertsAmount] transFee = {}", transFee);

                String commissionSql = (String) assertMap.get(COMMISSION);
                Long commission = queryAmountOrFee(commissionSql);
                log.info("[调试信息] [doAssertsAmount] commission = {}", commission);

                String otherFeeSql = (String) assertMap.get(OTHER_FEE);
                Long otherFee = queryAmountOrFee(otherFeeSql);
                log.info("[调试信息] [doAssertsAmount] otherFee = {}", otherFee);

                String keyType = (String) assertMap.get(KEY_TYPE);
                String fundFlow = (String) assertMap.get(FUND_FLOW);
                if (StringUtils.isEmpty(keyType) || StringUtils.isEmpty(fundFlow)) {
                    log.info("[调试信息] [doAssertsAmount] 获取到的 [keyType == null] 或 [fundFlow == null] 跳过本次断言。打印本次断言 assertMap 参数：{}", assertMap);
                } else {
                    switch (keyType) {
                        case "amount":
                            assertEquals(amountCacheInit, transAmount, transFee, commission, otherFee, fundFlow, amountCacheValidation);
                            break;
                        case "balance":
                            assertEquals(balanceCacheInit, transAmount, transFee, commission, otherFee, fundFlow, balanceCacheValidation);
                            break;
                        default:
                            log.info("[调试信息] [doAssertsAmount] 没有匹配到 [keyType = {}] 请确认 [yaml] 文件中的元素填写是否正确。", keyType);
                    }
                }
            }
        }
    }

    /**
     * 根据 [Case.yaml] 文件中 [assertAmount] 对应的 sql 语句，查询交易 amount 或 fee 的值。
     *
     * @param sql sql 语句
     * @return Long
     */
    private static Long queryAmountOrFee(String sql) {
        Long amountOrFee = null;
        if (StringUtils.isEmpty(sql) || sql.startsWith("0") || !sql.startsWith("$") || !sql.contains("@select") || !sql.contains("${}")) {
            amountOrFee = 0L;
        } else {
            amountOrFee = Long.valueOf(Objects.requireNonNull(selectWithWhere(sql)));
        }
//        log.info("[调试信息] [queryAmountOrFee] amountOrFee = {}", amountOrFee);

        return amountOrFee;
    }

}
