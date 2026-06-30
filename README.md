# 高校运动会管理系统

基于 Spring Boot + MyBatis + Thymeleaf 的高校运动会管理系统，课程设计项目。

## 技术栈

- 后端：Spring Boot 3.2.5 + MyBatis + MySQL 8
- 前端：Thymeleaf 模板 + Bootstrap 5.3 + Font Awesome 6
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

### 启动

```bash
mvn spring-boot:run
```

访问 http://localhost:8080/login

### 默认账号

| 用户名 | 密码 |
|--------|------|
| admin | 123456 |

## 功能操作手册

### 1. 登录与登出

- 访问 `/login`，输入用户名 `admin`、密码 `123456` 登录
- 点击右上角用户名 → 退出登录

### 2. 仪表板（/dashboard）

- 展示四张统计卡片：总项目数、总运动员数、总报名人次、已完结项目数
- 展示"即将开始的比赛"列表

### 3. 比赛项目管理（/event/list）

| 操作 | 说明 |
|------|------|
| 搜索 | 顶部按"比赛类别"下拉 + 关键词搜索，点击"搜索"按钮 |
| 新增项目 | 点击"新增项目"按钮，填写项目名称、类别、取前N名、日期、时间、地点 |
| 编辑项目 | 点击"编辑"按钮，可修改项目信息及状态（未开始/进行中/已结束） |
| 结束比赛 | 点击"结束"按钮（黄底），确认后项目状态直接变为"已结束" |
| 删除项目 | 点击"删除"按钮，有报名记录的项目不可删除 |
| 查看报名人员 | 点击项目行（非按钮区），下方展开已报名人员表格（学号、姓名、性别、院系、状态），再次点击收起 |

### 4. 运动员管理（/athlete/list）

| 操作 | 说明 |
|------|------|
| 搜索 | 按院系下拉 + 关键词搜索 |
| 新增运动员 | 点击"新增运动员"，填写学号、姓名、性别、院系、联系电话 |
| 编辑/删除 | 点击对应按钮操作 |

### 5. 报名管理（/registration/list）

| 操作 | 说明 |
|------|------|
| 搜索 | 按项目下拉 + 关键词搜索 |
| 新增报名 | 点击"新增报名"，选择运动员和比赛项目 |
| 性别限制 | 男子项目仅男运动员可报，女子项目仅女运动员可报，混合项目均可 |
| 取消报名 | 仅"已报名"状态可取消，点击"取消"按钮 |

### 6. 成绩录入（/score/input）

| 操作 | 说明 |
|------|------|
| 选择项目 | 下拉选择"进行中"或"已结束"的项目，点击"选择项目"进入录入表单 |
| 录入成绩 | 填写每位运动员的名次和成绩，点击"提交"保存 |
| 查看成绩 | 已录入成绩的项目会显示名次、成绩、得分 |

得分规则（取前N名）：第1名 7分、第2名 5分、第3名 3分、第4名 2分、第5名 1分

### 7. 统计报表（/report）

| 报表 | 路径 | 说明 |
|------|------|------|
| 团体总分 | /report/team | 各院系总分排名，含男/女分项、金/银/铜牌数 |
| 个人成绩 | /report/personal | 按姓名或学号搜索某运动员的所有参赛成绩 |
| 项目成绩 | /report/event | 选择某项目查看该项目的完整成绩排名 |

## 项目结构

```
src/main/java/com/sportsmeet/
├── SportsMeetApplication.java    # 启动类
├── common/                        # 通用工具类
├── config/                        # 配置类（数据初始化、Web配置）
├── controller/                    # 控制器（7个页面控制器 + 1个API控制器）
├── entity/                        # 实体类（6个）
├── interceptor/                   # 拦截器（登录鉴权）
├── mapper/                        # MyBatis Mapper接口（6个）
└── service/                       # 业务层（7个接口 + 7个实现）

src/main/resources/
├── application.yml                # 配置文件
├── schema.sql                     # 建表与初始数据
├── mapper/                        # MyBatis XML映射（6个）
├── static/                        # 静态资源（CSS/JS/图片）
└── templates/                     # Thymeleaf模板（17个）
```

## 初始数据

| 数据 | 数量 |
|------|------|
| 院系 | 6 个 |
| 比赛项目 | 8 个（2 已结束、1 进行中、5 未开始）|
| 运动员 | 20 名 |
| 报名记录 | 15 条 |
| 成绩记录 | 10 条 |

## API 接口（供 JMeter 性能测试）

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/login | POST | 登录（username, password）|
| /api/events | GET | 获取项目列表 |
| /api/report/team | GET | 获取团体总分 |
