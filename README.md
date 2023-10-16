# 【二粒】API 自动化测试框架 1.0

---

## 【设计思路】

【二粒】是由 Java 编写的 API 自动化测试框架。以模块化的形式在 yaml 文件中描述测试用例的各项元素，解析实现其对应的功能。使用者无需编写测试代码，
只需要灵活配置各项 “模块” 内容即可对 API 接口进行测试。【二粒】使用 Maven 结合 TestNg 作为 Case 运行支撑，且能够更好的与多款持续集成平台相结合。

## 【适用范围】

- 研发工程师代码调试与自测；
- 测试工程师主要业务流程的回归性测试；
- 测试环境与生产环境指定功能的定期回归或日常巡检。

## 【功能简介】

- 无前置接口依赖的单一接口测试
- 请求报文体参数替换
- 有前置接口依赖的接口测试（如：接口 D 依赖接口 C）
- 有多个前置接口依赖的接口测试（如：接口 D 依赖接口 A B C）
- 有多个前置接口依赖，且前置接口之间有依赖关系的业务流程测试（如：接口 D 依赖 C，且 C 依赖 B，且 B 依赖 A）
- 响应报文断言
- 初始化数据
- 数据库字段断言 —— 定制功能，当前保本支持交易发生后判断账户余额是否正确。
- 生成测试报告（保存在 test-report-output/ 目录，报告文件名是 xml 中 suiteName 的值。）

---

## 【测试用例编写说明】

在 Case.yaml（测试用例）文件包含的 “模块” 如下：

- title —— 测试用例标题
- description —— 测试用例描述
- initDataSql —— 初始化数据的 SQL 语句
- preApi —— 描述被测接口所依赖的 “前置业务接口”
- replaceVariate —— 被测接口需要被替换的 “请求体” 参数
- testApi —— 描述被测接口各项信息（如：Url、Header、Body）
- asserts —— 断言
- validationDataSql —— 数据断言的对应 SQL 语句
- assertAmount —— 用于验证交易发生后的账户余额

### 【详细的测试用例编写说明参见《测试用例编写说明.yaml》文档。】

---

## 【执行测试用例】

基于 TestNg 提供的 xml 文件方式运行测试用例，在 xml 的 yamlFilePath 字段填写 Case.yaml 文件路径即可。
xml 文件存放在 test_run_xml/ 目录中，建议目录结构与 test_case_yaml/ 目录 结构相同。 使用 “鼠标右键 Run” 方式运行 yaml
测试用例。
也可以将 xml 文件添加在 maven-surefire-plugin 插件中，借助 mvn clean test 命令运行测试用例。

#### 涉及到的一些前置基础知识点，有兴趣的同学可以做些许了解。

- 了解 Yaml 文件的编写格式
- 了解 TestNg 的 xml 文件
- 了解 maven 的 surefire 插件

---

## 【一些初期配置说明】

### 1. 选择测试环境

环境定义在 resources/environment/choose_env.yaml 文件中，根据 env 来指定需要执行测试的环境。
与环境相关配置均在 ${env}.json 中，当前版本只支持 host、privateKey、publicKey 三个通用配置。

### 2. 设置 Header

【二粒】将 “通用” 的 Header.json 文件存放位置定义在 resources/environment/header_info/ 目录下，当我们确定了 test_env 之后，
【二粒】会自动选择对应的 header_info.json 文件，我们需要提前编写好相应的 Header 信息。

PS：如被测接口需要 “特定” 的 Header 信息，则可以直接编写在测试用例 Case.yaml 文件的 requestHeader 部分。

PS 又 PS：这版框架基于 api-user 项目编写，因此 “通用 Header” 均为此项目需要的 Header 信息。

### 3. 获取 Token

【二粒】将用户登录信息定义在了 resources/login_user_info/ 目录下。

这里需要多说一些，因为我们的项目有角色上的区分（如：agent 和 merchant），而不同角色又存在着使用功能差异或接口参数差异。
这部分也需要对项目做定制化处理。

针对 api-user 的处理方式为：通过测试用例来决定使用哪个角色获取 token。在 resources/test_case_yaml/ 测试用例目录下存在 156/ 用例和 256/ 用例两个子目录，
它们分别存放不同角色的测试用例。在运行测试用例时根据 “子目录” 来匹配对应的角色信息。（逻辑处理编写在 TestUtils.getAuthToken 方法中）

---

## 【写在末尾】

因为各个项目的技术特性和实现逻辑不同，会存在接口测试数据在封装处理的差异情况。例如：签名封装规则，token 获取规则，请求报文加密规则等。
所以针对不同的项目也需要对【二粒】进行相应的定制化修改，以满足自身项目测试需要。

PS：为什么起名叫【二粒】呢？
