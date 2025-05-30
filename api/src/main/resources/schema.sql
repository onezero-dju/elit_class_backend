-- 독립 테이블
CREATE TABLE badge_store (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             badge_name VARCHAR(255) NOT NULL
);

CREATE TABLE interest_categories (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     interest_list VARCHAR(100) NOT NULL
);

CREATE TABLE rules (
                       rule_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       rule_name VARCHAR(255) NOT NULL
);

-- 클래스 & 유저 (예약어 → 복수형)
CREATE TABLE classes (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         class_title VARCHAR(50) NOT NULL,
                         is_premium BOOLEAN NOT NULL,
                         user_id BIGINT NOT NULL,
                         like_count BIGINT NOT NULL,
                         views BIGINT NOT NULL,
                         status_value VARCHAR(50) NOT NULL,
                         description VARCHAR(255)
);

CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       class_id BIGINT NOT NULL,
                       email VARCHAR(100) NOT NULL,
                       is_certified VARCHAR(50) NOT NULL,
                       payment VARCHAR(100)

);
-- 강의 & 페이지
CREATE TABLE lectures (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          lecture_title VARCHAR(50) NOT NULL,
                          context VARCHAR(255) NOT NULL,
                          class_id BIGINT NOT NULL,
                          FOREIGN KEY (class_id) REFERENCES classes(id)
);

CREATE TABLE pages (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       is_quiz BOOLEAN NOT NULL,
                       title VARCHAR(50),
                       context VARCHAR(255) NOT NULL,
                       lecture_id BIGINT NOT NULL,
                       page_type VARCHAR(255) NOT NULL,
                       FOREIGN KEY (lecture_id) REFERENCES lectures(id)
);

-- 프로필
CREATE TABLE profiles (
                          user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          nickname VARCHAR(50) NOT NULL UNIQUE,
                          profile_image_url VARCHAR(255) NOT NULL,
                          status_context TEXT,
                          craete_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 리포트
CREATE TABLE user_rules (
                            iru_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            user_id BIGINT NOT NULL,
                            rule_id_ref BIGINT NOT NULL,
                            rule_id BIGINT NOT NULL,
                            FOREIGN KEY (user_id) REFERENCES users(id),
                            FOREIGN KEY (rule_id_ref) REFERENCES rules(rule_id)
);

CREATE TABLE reports (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         class_id BIGINT NOT NULL,
                         user_id BIGINT NOT NULL,
                         reason TEXT NOT NULL,
                         report_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         status_value VARCHAR(50) NOT NULL,
                         admin_comment TEXT,
                         reviewed_at TIMESTAMP,
                         reviewed_user_ruledId BIGINT,
                         FOREIGN KEY (class_id) REFERENCES classes(id),
                         FOREIGN KEY (user_id) REFERENCES users(id),
                         FOREIGN KEY (reviewed_user_ruledId) REFERENCES user_rules(iru_id)
);

-- 뱃지
CREATE TABLE user_badges (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             badge_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             is_view VARCHAR(255) NOT NULL,
                             FOREIGN KEY (badge_id) REFERENCES badge_store(id),
                             FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 관심사
CREATE TABLE user_interests (
                                iru_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                user_id BIGINT NOT NULL,
                                interest_id BIGINT NOT NULL,
                                FOREIGN KEY (user_id) REFERENCES users(id),
                                FOREIGN KEY (interest_id) REFERENCES interest_categories(id)
);

CREATE TABLE user_container (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                user_id BIGINT NOT NULL,
                                container_id VARCHAR(100) NOT NULL,
                                container_name VARCHAR(100),
                                project_name VARCHAR(100),
                                language VARCHAR(100),
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                FOREIGN KEY (user_id) REFERENCES users(id)
);

