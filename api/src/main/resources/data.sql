INSERT INTO classes (id, class_title, is_premium, user_id, like_count, views, status_value, description) VALUES
                                                                                                             (1, 'Math', true, 1, 100, 1000, 'ACTIVE', 'Basic Math class'),
                                                                                                             (2, 'Science', false, 2, 50, 800, 'ACTIVE', 'Science for beginners'),
                                                                                                             (3, 'History', true, 3, 70, 600, 'INACTIVE', 'World History overview');

-- Step 2: 데이터 삽입
INSERT INTO users (id, class_id, email, is_certified, payment) VALUES
                                                                   (1, 1, 'alice@example.com', 'yes', 'card'),
                                                                   (2, 2, 'bob@example.com', 'no', 'paypal'),
                                                                   (3, 3, 'carol@example.com', 'yes', 'bank transfer'),
                                                                   (4, 1, 'david@example.com', 'no', NULL),
                                                                   (5, 2, 'eve@example.com', 'yes', 'kakao pay');

