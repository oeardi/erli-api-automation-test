package com.common;

import java.util.Map;

/**
 * CacheData 缓存数据类
 *
 * @Author: Casta
 */
public class CacheData {

    /**
     * environment/choose_env.yaml
     */
    public static String env = null;
    public static String host = null;
    public static String privateKey = null;
    public static String publicKey = null;

    public static String title = null;
    public static String description = null;

    public static String tokenCache = null;
    public static String httpResponseCache = null;
    public static String testApiHttpResponseCache = null;
    public static Map<String, Object> dependentParameterCache = null;

    // 是否加密标志位
    public static String encryptToggle = "0";
    public static String preEncryptFlag = null;
    public static String encryptFlag = null;

    /**
     * 缓存 Case.yaml 文件中，[initDataSql] 中 “select” 语句的查询结果。
     * SQL 示例：
     * select balance from table where account_type = '' and merchant_id = '';
     * 注：
     * 如果 [initDataSql] 包含多条 “select” 语句，则会逐条缓存（覆盖上一条查询结果），只保留 “最后一条” 数据。
     */
    public static String cacheInitSelectResult = null;
    /**
     * 缓存 Case.yaml 文件中，[validationDataSql] 中 “select” 语句的查询结果。
     */
    public static String cacheValidationSelectResult = null;


    /**
     * 缓存 Case.yaml 文件中，[initDataSql] 中 “select@” 语句的查询结果。即：field@from 的这种格式。（必须是这种格式）
     * 示例：
     * balance@from table where account_type = '' and merchant_id = '';
     * 注：
     * 如果 [initDataSql] 包含多条 “select@” 语句，则会逐条缓存（覆盖上一条查询结果），只保留 “最后一条” 数据。
     */
    public static String cacheInitData = null;
    public static Long amountCacheInit = 0L;
    public static Long balanceCacheInit = 0L;

    /**
     * 缓存 Case.yaml 文件中，[validationDataSql] 中 “select@” 语句的查询结果。即：field@from 的这种格式。（必须是这种格式）
     */
    public static String cacheValidationData = null;
    public static Long amountCacheValidation = 0L;
    public static Long balanceCacheValidation = 0L;

}
