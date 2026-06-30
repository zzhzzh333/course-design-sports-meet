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
('男子100米', '男子', 5, '2026-07-15', '09:00', '田径场', 0),
('女子100米', '女子', 5, '2026-07-15', '09:30', '田径场', 0),
('男子跳远', '男子', 5, '2026-07-15', '10:00', '沙坑区', 0),
('女子跳高', '女子', 5, '2026-07-15', '10:30', '田径场', 0),
('男子铅球', '男子', 5, '2026-07-16', '09:00', '投掷区', 0),
('女子铅球', '女子', 5, '2026-07-16', '09:30', '投掷区', 0),
('4×100米混合接力', '混合', 3, '2026-07-16', '10:00', '田径场', 0),
('男子1500米', '男子', 5, '2026-07-16', '10:30', '田径场', 0);

INSERT IGNORE INTO athlete (student_no, name, gender, dept_id, phone) VALUES
('2024001', '张三', '男', 1, '13800000001'),
('2024002', '李四', '男', 1, '13800000002'),
('2024003', '王五', '男', 2, '13800000003'),
('2024004', '赵六', '男', 2, '13800000004'),
('2024005', '陈七', '男', 3, '13800000005'),
('2024006', '刘八', '男', 3, '13800000006'),
('2024007', '周九', '男', 4, '13800000007'),
('2024008', '吴十', '男', 5, '13800000008'),
('2024009', '郑一', '男', 6, '13800000009'),
('2024010', '冯二', '男', 6, '13800000010'),
('2024011', '孙丽', '女', 1, '13800000011'),
('2024012', '钱芳', '女', 1, '13800000012'),
('2024013', '杨雪', '女', 2, '13800000013'),
('2024014', '黄梅', '女', 3, '13800000014'),
('2024015', '韩冰', '女', 4, '13800000015'),
('2024016', '唐雅', '女', 5, '13800000016'),
('2024017', '曹婷', '女', 6, '13800000017'),
('2024018', '魏兰', '女', 2, '13800000018'),
('2024019', '许静', '女', 3, '13800000019'),
('2024020', '何琳', '女', 4, '13800000020');