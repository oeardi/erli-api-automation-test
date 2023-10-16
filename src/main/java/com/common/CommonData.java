package com.common;

/**
 * @Author: 冷枫红舞
 */
public class CommonData {

    public static final String Y = "Y";
    public static final String N = "N";

    public interface UserInfo {
        String LOGIN_NAME = "";
        String MERCHANT_LOGIN_METHOD = "";
        String PASSWORD = "";
        String ROLE = "";
    }

    /**
     * ymal 文件中的元素
     */
    public interface Keywork {
        String TITLE = "title";
        String DESCRIPTION = "description";

        String INIT_DATA_SQL = "initDataSql";

        String PRE_API = "preApi";
        String PRE_REQUEST_URL = "preRequestUrl";
        String PRE_REQUEST_HEADER = "preRequestHeader";
        String PRE_REQUEST_BODY = "preReqeustBody";
        String PRE_ENCRYPT_FLAG = "preEncryptFlag";
        String PRE_DEPENDENT_PARAMETER = "preDependentParameter";
        String REPLACE_VARIATE = "replaceVariate";
        String TEST_API = "testApi";
        String REQUEST_URL = "requestUrl";
        String REQUEST_HEADER = "requestHeader";
        String REQUEST_BODY = "reqeustBody";
        String ENCRYPT_FLAG = "encryptFlag";

        String VALIDATION_DATA_SQL = "validationDataSql";
    }

    public interface assertKeyword {
        String ASSERTS = "asserts";
        String VALUE_PATH = "valuePath";
        String CONDITION = "condition";
        String EXPECT = "expect";

    }

    public interface assertAmountKeyword {
        String ASSERT_AMOUNT = "assertAmount";

        /**
         * [assertAmountKeyword] 说明：
         * transAmount 交易金额（单位：考包）
         * transFee 交易手续费（单位：考包）
         * commission 佣金（单位：考包）
         * otherFee 其它手续费（单位：考包）
         * keyType 断言类型，取值：amount、balance。如果判断交易金额，则 type = amount；如果判断账户余额，则 type = balance。
         * fundFlow 资金方向，断言内部实现 “加、减” 计算。取值：+、-
         */
        String TRANS_AMOUNT = "transAmount";
        String TRANS_FEE = "transFee";
        String COMMISSION = "commission";
        String OTHER_FEE = "otherFee";
        String KEY_TYPE = "keyType";
        String FUND_FLOW = "fundFlow";
    }

}
