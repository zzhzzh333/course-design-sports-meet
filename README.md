# 高校运动会管理系统

基于 Spring Boot + MyBatis + Thymeleaf 的高校运动会管理系统，课程设计项目。系统覆盖比赛项目、运动员、报名、成绩录入、统计报表和基础安全加固等功能。

## 技术栈

- 后端：Spring Boot 3.2.5 + Spring MVC + MyBatis + MySQL 8
- 前端：Thymeleaf 模板 + Thymeleaf Layout Dialect + Bootstrap 5.3 + Font Awesome 6
- 安全：Session 登录拦截 + BCrypt 密码加密 + 参数校验 + POST 安全提交
- 构建：Maven
- JDK：17

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8+
- Maven 3.6+

### 数据库准备

```sql
CREATE DATABASE sports_meet CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'admin'@'localhost' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON sports_meet.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;
```

> 数据库连接信息位于 `src/main/resources/application.yml`，默认数据库为 `sports_meet`，默认用户名/密码为 `admin / 123456`。

### 启动

```bash
mvn spring-boot:run
```

访问：

```text
http://localhost:8080/login
```

如果需要内网访问，项目已配置监听 `0.0.0.0:8080`，同一局域网设备可通过本机 IP 访问：

```text
http://你的本机IP:8080/login
```

Windows 防火墙需要允许 8080 入站。

### 默认账号

| 用户名 | 密码 |
|--------|------|
| admin | 123456 |

## 功能操作手册

### 1. 登录与登出

- 访问 `/login`，输入用户名 `admin`、密码 `123456` 登录
- 登录成功后进入 `/dashboard`
- 点击右上角用户名 → 退出登录
- 未登录访问业务页面会自动跳转到登录页

### 2. 仪表板（/dashboard）

- 展示四张统计卡片：总项目数、总运动员数、总报名人次、已完结项目数
- 展示"即将开始的比赛"列表
- 可作为系统首页检查当前数据概况

### 3. 比赛项目管理（/event/list）

| 操作 | 说明 |
|------|------|
| 搜索 | 顶部按"比赛类别"下拉 + 关键词搜索，表单使用 POST 提交，URL 不显示搜索参数 |
| 新增项目 | 点击"新增项目"按钮，填写项目名称、类别、取前N名、日期、时间、地点 |
| 编辑项目 | 点击"编辑"按钮，可修改项目信息及状态（未开始/进行中/已结束） |
| 结束比赛 | 点击"结束"按钮，确认后项目状态直接变为"已结束" |
| 删除项目 | 点击"删除"按钮，有报名记录的项目不可删除 |
| 查看报名人员 | 点击项目行（非按钮区），下方展开已报名人员表格（学号、姓名、性别、院系、状态），再次点击收起 |

安全说明：

- 删除项目、结束比赛均已改为 POST 表单提交
- 直接访问 `/event/delete/{id}` 或 `/event/finish/{id}` 不会执行操作，只会提示必须通过页面按钮提交
- 项目编号、类别、关键词等参数会进行合法性校验

### 4. 运动员管理（/athlete/list）

| 操作 | 说明 |
|------|------|
| 搜索 | 按院系下拉 + 关键词搜索，POST 提交，URL 不显示 `keyword` 参数 |
| 新增运动员 | 点击"新增运动员"，填写学号、姓名、性别、院系、联系电话 |
| 编辑运动员 | 点击"编辑"按钮修改运动员信息 |
| 删除运动员 | 点击"删除"按钮，有报名记录的运动员不可删除 |

安全说明：

- 删除运动员已改为 POST 表单提交
- 直接访问 `/athlete/delete/{id}` 不会执行删除
- 学号、姓名、手机号、性别、院系编号均会进行基础校验

### 5. 报名管理（/registration/list）

| 操作 | 说明 |
|------|------|
| 搜索 | 按项目下拉 + 关键词搜索，POST 提交，URL 不显示筛选参数 |
| 新增报名 | 点击"新增报名"，选择运动员和比赛项目 |
| 性别限制 | 男子项目仅男运动员可报，女子项目仅女运动员可报，混合项目男女均可 |
| 重复限制 | 同一运动员不能重复报名同一项目 |
| 取消报名 | 仅"已报名"状态可取消，点击"取消报名"按钮 |

安全说明：

- 取消报名已改为 POST 表单提交
- 直接访问 `/registration/cancel/{id}` 不会执行取消
- 报名时会校验运动员编号、项目编号、项目状态和性别匹配规则

### 6. 成绩录入（/score/input）

| 操作 | 说明 |
|------|------|
| 选择项目 | 下拉选择"进行中"或"已结束"的项目，点击"选择项目"进入录入表单 |
| 录入成绩 | 填写每位运动员的名次和成绩，点击"提交"保存 |
| 查看成绩 | 已录入成绩的项目会显示名次、成绩、得分 |

得分规则（取前N名）：

| 名次 | 得分 |
|------|------|
| 第1名 | 7分 |
| 第2名 | 5分 |
| 第3名 | 3分 |
| 第4名 | 2分 |
| 第5名 | 1分 |

安全说明：

- 手动修改 `/score/input/{eventId}` 为不存在项目编号时，不会出现 Whitelabel Error Page
- 成绩提交时会校验项目编号、运动员编号、名次范围和成绩文本

### 7. 统计报表（/report）

| 报表 | 路径 | 说明 |
|------|------|------|
| 团体总分 | `/report/team` | 各院系总分排名，含男/女分项、金/银/铜牌数 |
| 个人成绩 | `/report/personal` | 按姓名或学号搜索某运动员的所有参赛成绩，POST 提交 |
| 项目成绩 | `/report/event` | 选择某项目查看该项目的完整成绩排名，POST 提交 |

安全说明：

- 个人成绩和项目成绩查询均使用 POST 表单提交
- URL 不显示 `keyword`、`eventId` 等查询参数
- 查询参数会进行长度、字符和编号合法性校验

## 安全加固说明（v0.4）

当前版本加入轻量级安全加固，不引入 Spring Security，继续保持课程设计项目结构简洁。

| 类型 | 实现 |
|------|------|
| 登录鉴权 | `LoginInterceptor` 对业务页面进行 Session 校验 |
| 密码安全 | 管理员密码使用 BCrypt 加密存储 |
| SQL 注入防护 | MyBatis 使用 `#{}` 参数绑定；搜索关键词进行危险字符过滤 |
| URL 篡改防护 | Controller 对 ID、枚举、状态、成绩等参数进行合法性校验 |
| 危险操作防护 | 删除、结束比赛、取消报名等操作全部改为 POST 表单提交 |
| 友好错误处理 | `GlobalExceptionHandler` 统一处理业务异常和参数异常，避免 Whitelabel 页面 |
| 查询参数隐藏 | 主要搜索/筛选表单改为 POST，URL 不展示 `keyword` 等参数 |

主要新增类：

```text
src/main/java/com/sportsmeet/common/
├── BusinessException.java        # 业务异常
├── GlobalExceptionHandler.java   # 全局异常处理
└── SecurityValidator.java        # 参数校验与关键词过滤
```

## 项目结构

```text
src/main/java/com/sportsmeet/
├── SportsMeetApplication.java    # 启动类
├── common/                        # 通用结果、异常、安全校验、全局异常处理
├── config/                        # 配置类（数据初始化、Web配置）
├── controller/                    # 页面控制器 + API控制器
├── entity/                        # 实体类
├── interceptor/                   # 登录鉴权拦截器
├── mapper/                        # MyBatis Mapper接口
└── service/                       # 业务层接口与实现

src/main/resources/
├── application.yml                # 项目配置、数据库、MyBatis、Thymeleaf
├── schema.sql                     # 建表与初始数据
├── mapper/                        # MyBatis XML映射
├── static/                        # 静态资源（CSS/JS/字体）
└── templates/                     # Thymeleaf页面模板
```

## 初始数据

| 数据 | 数量 |
|------|------|
| 院系 | 6 个 |
| 比赛项目 | 8 个（2 已结束、1 进行中、5 未开始）|
| 运动员 | 20 名 |
| 报名记录 | 15 条 |
| 成绩记录 | 10 条 |

## API 接口说明

API 主要用于接口测试和 JMeter 性能测试。除 `/api/login` 外，其余 API 受登录拦截器保护，需要先登录并携带 Session Cookie。

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

错误示例：

```json
{
  "code": 500,
  "message": "搜索关键词包含非法字符",
  "data": null
}
```

### 1. API 登录

| 项目 | 内容 |
|------|------|
| 接口 | `/api/login` |
| 方法 | POST |
| Content-Type | `application/x-www-form-urlencoded` |
| 是否需要登录 | 否 |

请求参数：

| 参数 | 必填 | 说明 |
|------|------|------|
| username | 是 | 用户名，默认 `admin` |
| password | 是 | 密码，默认 `123456` |

PowerShell 示例：

```powershell
$session = $null
Invoke-WebRequest `
  -Uri "http://localhost:8080/api/login" `
  -Method Post `
  -Body @{username="admin"; password="123456"} `
  -SessionVariable session `
  -UseBasicParsing
```

curl 示例：

```bash
curl -i -c cookie.txt -X POST http://localhost:8080/api/login \
  -d "username=admin" \
  -d "password=123456"
```

成功响应示例：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "系统管理员"
  }
}
```

### 2. 获取比赛项目列表

| 项目 | 内容 |
|------|------|
| 接口 | `/api/events` |
| 方法 | GET |
| 是否需要登录 | 是 |

请求参数：

| 参数 | 必填 | 说明 |
|------|------|------|
| category | 否 | 比赛类别，只允许 `男子`、`女子`、`混合` |
| keyword | 否 | 项目名称关键词，长度不超过 30，过滤危险字符 |

PowerShell 示例：

```powershell
Invoke-WebRequest `
  -Uri "http://localhost:8080/api/events?category=男子&keyword=100" `
  -WebSession $session `
  -UseBasicParsing
```

curl 示例：

```bash
curl -b cookie.txt "http://localhost:8080/api/events?category=%E7%94%B7%E5%AD%90&keyword=100"
```

安全测试示例：

```bash
curl -b cookie.txt "http://localhost:8080/api/events?keyword=' OR 1=1 --"
```

预期返回：

```json
{
  "code": 500,
  "message": "搜索关键词包含非法字符",
  "data": null
}
```

### 3. 获取团体总分报表

| 项目 | 内容 |
|------|------|
| 接口 | `/api/report/team` |
| 方法 | GET |
| 是否需要登录 | 是 |

PowerShell 示例：

```powershell
Invoke-WebRequest `
  -Uri "http://localhost:8080/api/report/team" `
  -WebSession $session `
  -UseBasicParsing
```

curl 示例：

```bash
curl -b cookie.txt http://localhost:8080/api/report/team
```

返回数据字段说明：

| 字段 | 说明 |
|------|------|
| deptName | 院系名称 |
| maleScore | 男子项目得分 |
| femaleScore | 女子项目得分 |
| totalScore | 总分 |
| goldCount | 第一名数量 |
| silverCount | 第二名数量 |
| bronzeCount | 第三名数量 |

## 测试指导

### 1. 页面功能测试

建议按以下顺序测试：

| 序号 | 模块 | 测试点 |
|------|------|------|
| 1 | 登录 | admin / 123456 能正常登录 |
| 2 | 仪表板 | 统计卡片和即将开始比赛正常展示 |
| 3 | 比赛项目 | 搜索、新增、编辑、结束比赛、展开报名人员、删除限制 |
| 4 | 运动员 | 搜索、新增、编辑、删除限制 |
| 5 | 报名 | 新增报名、性别限制、重复报名限制、取消报名 |
| 6 | 成绩 | 选择项目、录入成绩、查看成绩 |
| 7 | 报表 | 团体总分、个人成绩、项目成绩 |
| 8 | 安全 | URL 篡改、SQL 注入关键词、非法 ID 均不出现 Whitelabel |

### 2. URL 参数隐藏测试

测试搜索表单是否已改为 POST：

1. 登录系统
2. 进入 `/athlete/list`
3. 搜索框输入 `1`
4. 点击搜索
5. 地址栏应保持：

```text
http://localhost:8080/athlete/list
```

不应出现：

```text
http://localhost:8080/athlete/list?deptId=&keyword=1
```

同理测试：

- `/event/list`
- `/registration/list`
- `/report/personal`
- `/report/event`

### 3. URL 篡改测试

登录后手动访问以下地址：

```text
http://localhost:8080/event/delete/1
http://localhost:8080/event/finish/1
http://localhost:8080/athlete/delete/1
http://localhost:8080/registration/cancel/1
http://localhost:8080/score/input/999999
http://localhost:8080/report/event?eventId=999999
```

预期：

- 不直接执行删除、结束、取消等危险操作
- 不出现 Whitelabel Error Page
- 返回对应列表页或查询页，并显示友好错误提示

### 4. SQL 注入关键词测试

页面搜索框输入：

```text
' OR 1=1 --
```

或通过 API 请求：

```bash
curl -b cookie.txt "http://localhost:8080/api/events?keyword=' OR 1=1 --"
```

预期：

- 不执行异常 SQL
- 页面显示"搜索关键词包含非法字符"
- API 返回错误 JSON

### 5. JMeter 测试建议

#### 线程组建议

| 场景 | 线程数 | Ramp-Up | 循环次数 |
|------|--------|---------|----------|
| 基础功能验证 | 5 | 5秒 | 3 |
| 普通压力测试 | 20 | 10秒 | 5 |
| 接口稳定性测试 | 50 | 20秒 | 5 |

#### JMeter 测试计划结构

```text
Test Plan
└── Thread Group
    ├── HTTP Cookie Manager
    ├── HTTP Request Defaults
    │   ├── Server Name: localhost
    │   └── Port Number: 8080
    ├── POST /api/login
    ├── GET /api/events
    ├── GET /api/report/team
    ├── View Results Tree
    └── Summary Report
```

#### HTTP Cookie Manager

必须添加 `HTTP Cookie Manager`，否则 `/api/events` 和 `/api/report/team` 会因为未登录被拦截。

#### 请求 1：登录

| 配置项 | 值 |
|--------|----|
| Method | POST |
| Path | `/api/login` |
| Body 参数 | `username=admin`，`password=123456` |

#### 请求 2：项目列表

| 配置项 | 值 |
|--------|----|
| Method | GET |
| Path | `/api/events` |
| Parameters | `category=男子`，`keyword=100` |

#### 请求 3：团体总分

| 配置项 | 值 |
|--------|----|
| Method | GET |
| Path | `/api/report/team` |

#### 断言建议

对每个 API 请求添加 `Response Assertion`：

| 接口 | 断言内容 |
|------|----------|
| `/api/login` | `"code":200` 或 `操作成功` |
| `/api/events` | `"code":200` |
| `/api/report/team` | `"code":200` |

#### 性能观察指标

关注 Summary Report 中：

- Average：平均响应时间
- Min / Max：最小/最大响应时间
- Error %：错误率，应尽量保持 0%
- Throughput：吞吐量

## 版本记录

详见 [CHANGELOG.md](CHANGELOG.md)。
