CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    real_name   VARCHAR(50)  NOT NULL,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS department (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS athlete (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_no  VARCHAR(20)  NOT NULL UNIQUE,
    name        VARCHAR(50)  NOT NULL,
    gender      VARCHAR(4)   NOT NULL,
    dept_id     BIGINT       NOT NULL,
    phone       VARCHAR(20),
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dept_id) REFERENCES department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS event (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    category    VARCHAR(10)  NOT NULL,
    top_n       INT          NOT NULL DEFAULT 5,
    event_date  DATE         NOT NULL,
    event_time  VARCHAR(20)  NOT NULL,
    location    VARCHAR(100) NOT NULL,
    status      TINYINT      NOT NULL DEFAULT 0,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS registration (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    athlete_id    BIGINT   NOT NULL,
    event_id      BIGINT   NOT NULL,
    register_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status        TINYINT  NOT NULL DEFAULT 0,
    create_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_athlete_event (athlete_id, event_id),
    FOREIGN KEY (athlete_id) REFERENCES athlete(id),
    FOREIGN KEY (event_id)   REFERENCES event(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS score (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_id BIGINT       NOT NULL,
    athlete_id      BIGINT       NOT NULL,
    event_id        BIGINT       NOT NULL,
    `rank`          INT          NOT NULL,
    `result`        VARCHAR(50),
    points          INT          NOT NULL DEFAULT 0,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES registration(id),
    FOREIGN KEY (athlete_id)      REFERENCES athlete(id),
    FOREIGN KEY (event_id)        REFERENCES event(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO department (name) VALUES
('计算机与信息工程学院'),
('机械工程学院'),
('经济管理学院'),
('外国语学院'),
('艺术设计学院'),
('体育学院');

INSERT IGNORE INTO event (name, category, top_n, event_date, event_time, location, status) VALUES
('男子100米', '男子', 5, '2026-07-15', '09:00', '田径场A', 2),
('女子100米', '女子', 5, '2026-07-15', '09:30', '田径场A', 2),
('男子跳远', '男子', 5, '2026-07-15', '10:00', '沙坑区', 1),
('女子跳高', '女子', 5, '2026-07-15', '14:00', '田径场B', 0),
('男子铅球', '男子', 5, '2026-07-16', '09:00', '投掷区', 0),
('女子铅球', '女子', 5, '2026-07-16', '09:30', '投掷区', 0),
('4×100米混合接力', '混合', 3, '2026-07-16', '14:00', '田径场A', 0),
('男子1500米', '男子', 5, '2026-07-16', '15:00', '田径场A', 0);

INSERT IGNORE INTO athlete (student_no, name, gender, dept_id, phone) VALUES
('2024001', '张三', '男', 1, '13800000001'),
('2024002', '李四', '男', 1, '13800000002'),
('2024003', '王五', '男', 2, '13800000003'),
('2024004', '赵六', '男', 3, '13800000004'),
('2024005', '陈七', '男', 4, '13800000005'),
('2024006', '刘八', '男', 5, '13800000006'),
('2024007', '周九', '男', 6, '13800000007'),
('2024008', '吴十', '男', 2, '13800000008'),
('2024009', '郑一', '男', 6, '13800000009'),
('2024010', '冯二', '男', 3, '13800000010'),
('2024011', '孙丽', '女', 1, '13800000011'),
('2024012', '钱芳', '女', 2, '13800000012'),
('2024013', '杨雪', '女', 3, '13800000013'),
('2024014', '黄梅', '女', 4, '13800000014'),
('2024015', '韩冰', '女', 5, '13800000015'),
('2024016', '唐雅', '女', 6, '13800000016'),
('2024017', '曹婷', '女', 1, '13800000017'),
('2024018', '魏兰', '女', 2, '13800000018'),
('2024019', '许静', '女', 6, '13800000019'),
('2024020', '何琳', '女', 5, '13800000020');

-- 男子100米(已结束)：5人来自不同院系
INSERT IGNORE INTO registration (athlete_id, event_id, status) VALUES
(1, 1, 2), (3, 1, 2), (4, 1, 2), (6, 1, 2), (9, 1, 2),
-- 女子100米(已结束)：5人来自不同院系
(11, 2, 2), (12, 2, 2), (13, 2, 2), (15, 2, 2), (16, 2, 2),
-- 男子跳远(进行中)：5人，部分与100米重叠
(1, 3, 1), (2, 3, 1), (5, 3, 1), (7, 3, 1), (8, 3, 1),
-- 女子跳高(未开始)：4人
(14, 4, 0), (17, 4, 0), (18, 4, 0), (20, 4, 0),
-- 男子铅球(未开始)：3人
(3, 5, 0), (9, 5, 0), (10, 5, 0),
-- 4×100米混合接力(未开始)：4人(2男2女)
(2, 7, 0), (6, 7, 0), (11, 7, 0), (19, 7, 0);

-- 男子100米成绩
INSERT IGNORE INTO score (registration_id, athlete_id, event_id, `rank`, `result`, points) VALUES
(1, 1, 1, 1, '11.20秒', 7),
(2, 3, 1, 2, '11.45秒', 5),
(3, 4, 1, 3, '11.68秒', 3),
(4, 6, 1, 4, '11.82秒', 2),
(5, 9, 1, 5, '12.05秒', 1),
-- 女子100米成绩
(6, 11, 2, 1, '12.35秒', 7),
(7, 12, 2, 2, '12.58秒', 5),
(8, 13, 2, 3, '12.72秒', 3),
(9, 15, 2, 4, '12.91秒', 2),
(10, 16, 2, 5, '13.10秒', 1);