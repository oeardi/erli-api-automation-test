package com.apis;

import lombok.extern.slf4j.Slf4j;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static com.utils.AutomationEngineUtils.apiAuto;
import static com.utils.TestUtils.getAuthToken;
import static com.utils.TestUtils.setEnvironmentHost;

/**
 * 测试用例
 *
 * @Author: 冷枫红舞
 */
@Slf4j
public class AutomatedTest {

    @Parameters("yamlFilePath")
    @BeforeClass
    public void beforeClass(String yamlFilePath) {
        log.info("[调试信息] [beforeClass] u(҂`･ｪ･´) <,︻╦̵̵̿╤─ ҉ - --");
        log.info("[调试信息] [beforeClass] 打印 yamlFilePath = {}", yamlFilePath);
        setEnvironmentHost();
        getAuthToken(yamlFilePath);

    }

    @Parameters("yamlFilePath")
    @Test
    public void testAutomated(String yamlFilePath) {
        Reporter.log("【调试信息】【testAutomated】 测试用例开始执行：");
        apiAuto(yamlFilePath);
        Reporter.log("【调试信息】【testAutomated】 测试用例执行完毕。");
    }

    @AfterClass
    public void afterClass() {
        log.info("[调试信息] [afterClass] ꒰ᐢ⸝⸝•༝•⸝⸝ᐢ꒱");
    }

}
