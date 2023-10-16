package com.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.common.CacheData.*;
import static com.utils.JdbcUtils.getConnection;
import static com.utils.JdbcUtils.getStatement;

/**
 * 操作数据库
 * （用于自动化脚本执行之前，初始化一些数据。）
 *
 * @Author: 冷枫红舞
 */
@Slf4j
public class BaseDBOperateUtils {

    public static Statement statement = null;

    static {
        Connection connection = getConnection();
        statement = getStatement(connection);
    }


    /**
     * DB operate
     *
     * @param sql     sql
     * @param element 标识 sql 来自 initDataSql 还是 validationDataSql
     */
    public static void dbOperate(String sql, String element) {
        if (StringUtils.isEmpty(sql) || StringUtils.isEmpty(element)) {
            log.info("[调试信息] [dbOperate] 传入的 [sql | element] 为 null，dbOperate() 方法终止执行。[return;]");
            return;
        }
//        log.info("[调试信息] [dbOperate] 打印 SQL 语句：[{}]", sql);
//        Reporter.log("【调试信息】【dbOperate】 打印 SQL 语句：【" + sql + "】");

        if (sql.startsWith("insert") || sql.startsWith("INSERT")) {
            insert(sql);
//            log.info("[调试信息] [dbOperate][insert] SQL 执行完毕。[{}]", sql);
        } else if (sql.startsWith("delete") || sql.startsWith("DELETE")) {
            delete(sql);
//            log.info("[调试信息] [dbOperate][delete] SQL 执行完毕。[{}]", sql);
        } else if (sql.startsWith("update") || sql.startsWith("UPDATE")) {
            update(sql);
//            log.info("[调试信息] [dbOperate][update] SQL 执行完毕。[{}]", sql);
        } else if (sql.startsWith("select") || sql.startsWith("SELECT")) {
            select(sql, element);
//            log.info("[调试信息] [dbOperate][select] SQL 执行完毕。[{}]", sql);
        } else if (sql.startsWith("$")) {
            selectWithWhere(sql);
        } else if (sql.contains("@")) {
            selectOneField(sql, element);
        } else {
            log.info("[调试信息] [dbOperate] 传入的 SQL 不是 [增、删、改、查] 功能，dbOperate() 方法终止执行。[return;]");
            return;
        }
        log.info("[调试信息] [dbOperate] 方法执行完毕。");
    }

    /**
     * @param sql sql
     */
    private static void insert(String sql) {
        if (StringUtils.isEmpty(sql)) {
            log.info("[调试信息] [insert] 传入参数 [sql == null]，insert() 方法终止执行。[return null;]");
            return;
        }

        int result = 0;
        try {
            result = statement.executeUpdate(sql);
            if (result > 0) {
                log.info("[调试信息] [insert] INSERT 执行成功。");
            } else {
                log.info("[调试信息] [insert] INSERT 执行结果 [result <= 0]，数据 [INSERT] 失败。");
            }
            log.info("[调试信息] [insert] INSERT 执行结果：[{}]", result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param sql sql
     */
    private static void delete(String sql) {
        if (StringUtils.isEmpty(sql)) {
            log.info("[调试信息] [delete] 传入参数 [sql == null]，delete() 方法终止执行。[return null;]");
            return;
        }

        int result = 0;
        try {
            result = statement.executeUpdate(sql);
            if (result > 0) {
                log.info("[调试信息] [delete] DELETE 执行成功。");
            } else {
                log.info("[调试信息] [delete] DELETE 执行结果 [result <= 0]，数据 [DELETE] 失败。");
            }
            log.info("[调试信息] [delete] DELETE 执行结果：[{}]", result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param sql sql
     */
    private static void update(String sql) {
        if (StringUtils.isEmpty(sql)) {
            log.info("[调试信息] [update] 传入参数 [sql == null]，update() 方法终止执行。[return null;]");
            return;
        }

        int result = 0;
        try {
            result = statement.executeUpdate(sql);
            if (result > 0) {
                log.info("[调试信息] [update] UPDATE 执行成功。");
            } else {
                log.info("[调试信息] [update] UPDATE 执行结果 [result <= 0]，数据 [UPDATE] 失败。");
            }
            log.info("[调试信息] [update] UPDATE 执行结果：[{}]", result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param sql sql
     */
    private static void select(String sql, String element) {
        if (StringUtils.isEmpty(sql)) {
            log.info("[调试信息] [select] 传入参数 [sql == null]，select() 方法终止执行。[return null;]");
            return;
        }

        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                if (element.equals("init")) {
                    if (null != cacheInitSelectResult) {
                        cacheInitSelectResult = null;
                    }
                    cacheInitSelectResult = resultSet.getString(1);
                    log.info("[调试信息] [select] 输出查询结果 cacheInit：[{}]", cacheInitSelectResult);
                } else {
                    if (null != cacheValidationSelectResult) {
                        cacheValidationSelectResult = null;
                    }
                    cacheValidationSelectResult = resultSet.getString(1);
                    log.info("[调试信息] [select] 输出查询结果 cacheValidation：[{}]", cacheValidationSelectResult);
                }
                log.info("[调试信息] [select] SELECT 执行成功。");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void selectOneField(String sql, String element) {
        if (StringUtils.isEmpty(sql) || StringUtils.isEmpty(element)) {
            log.info("[调试信息] [selectOneField] 传入参数 [sql | element] 为 null，selectOneField() 方法终止执行。[return null;]");
            return;
        }
//        log.info("[调试信息] [selectOneField] [{}] 打印原始 SQL 语句：[{}]", element, sql);

        String[] strings = sql.split("@");
        String part1 = strings[0].trim();
        String part2 = strings[1].trim();
//        log.info("[调试信息] [selectOne] part1：[{}]", part1);
//        log.info("[调试信息] [selectOne] part2：[{}]", part2);
        sql = "select " + part1 + " " + part2;
        log.info("[调试信息] [selectOneField] [{}] 打印格式化后的 SQL 语句：[{}]", element, sql);

        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
//                log.info("[调试信息] [selectOneField] while (resultSet.next())");
                if (element.equals("init")) {
                    if (null != cacheInitData) {
                        cacheInitData = null;
                    }
                    cacheInitData = resultSet.getString(1);
                    log.info("[调试信息] [selectOneField] [init] 输出查询结果：[{} = {}]", part1, cacheInitData);
                    if (part1.contains("amount")) {
                        amountCacheInit = Long.parseLong(cacheInitData);
//                        log.info("[调试信息] [selectOneField] [init] 打印缓存值 CacheData.amountCacheInit = [{}]", amountCacheInit);
                    }
                    if (part1.contains("balance")) {
                        balanceCacheInit = Long.parseLong(cacheInitData);
//                        log.info("[调试信息] [selectOneField] [init] 打印缓存值 CacheData.balanceCacheInit = [{}]", balanceCacheInit);
                    }
                    log.info("[调试信息] [selectOneField] [init] SELECT（查询指定字段）执行成功。");
                }

                if (element.equals("validation")) {
                    if (null != cacheValidationData) {
                        cacheValidationData = null;
                    }
                    cacheValidationData = resultSet.getString(1);
                    log.info("[调试信息] [selectOneField] [validation] 输出查询结果：[{} = {}]", part1, cacheValidationData);
                    if (part1.contains("amount")) {
                        amountCacheValidation = Long.parseLong(cacheValidationData);
//                        log.info("[调试信息] [selectOneField] [validation] 打印缓存值 CacheData.amountCacheValidation = [{}]", amountCacheValidation);
                    }
                    if (part1.contains("balance")) {
                        balanceCacheValidation = Long.parseLong(cacheValidationData);
//                        log.info("[调试信息] [selectOneField] [validation] 打印缓存值 CacheData.balanceCacheValidation = [{}]", balanceCacheValidation);
                    }
                    log.info("[调试信息] [selectOneField] [validation] SELECT（查询指定字段）执行成功。");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 从 “接口响应报文” 中获取字段的值，并以此为查询条件，查看数据是否在数据库中存在。
     *
     * @param sql sql
     * @return value
     */
    public static String selectWithWhere(String sql) {
        if (StringUtils.isEmpty(sql) || sql.startsWith("0") || !sql.startsWith("$") || !sql.contains("@select") || !sql.contains("${}")) {
            log.info("[调试信息] [selectWithWhere] 传入参数 [sql == null]，selectWithWhere() 方法终止执行。[return null;]");
            return null;
        }

        /**
         * SQL 示例：
         * $data.orderNo@select * from ka_settlement_bill_withdraw where order_no like '${}%';
         */
        String[] strings = sql.split("@");
        String jsonPathPart = strings[0].trim();
        String sqlPart = strings[1].trim();
        log.info("[调试信息] [selectWithWhere] jsonPathPart：[{}]", jsonPathPart);  // $data.orderNo
        log.info("[调试信息] [selectWithWhere] sqlPart：[{}]", sqlPart);

        // 从 http response 响应报文中获取到值
        String jsonValue = JsonPathUtils.getJsonStringValue(testApiHttpResponseCache, jsonPathPart);
        log.info("[调试信息] [selectWithWhere] jsonValue：[{}]", jsonValue);

        try {
            /**
             * 因测试环境较慢，等待几秒，确保数据库事务执行完毕。
             */
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String sqlValue = null;
        if (StringUtils.isNotEmpty(jsonValue)) {
            sql = sqlPart.replace("${}", jsonValue);
            log.info("[调试信息] [selectWithWhere] SELECT SQL：[{}]", sql);

            ResultSet resultSet = null;
            try {
                resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    sqlValue = resultSet.getString(1);
                    log.info("[调试信息] [selectWithWhere] 输出查询结果：[{}]", sqlValue);
                    assert sqlValue != null;
                    log.info("[调试信息] [selectWithWhere] SELECT 执行成功。");
                } else {
                    log.info("[调试信息] [selectWithWhere] resultSet 集合为空。（如果手动执行 sql 发现数据存在，说明测试环境响应较慢，则建议增加 sleep 时间。）");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return sqlValue;
    }

}
