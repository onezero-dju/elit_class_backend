ALTER TABLE classes ADD FOREIGN KEY (user_id) REFERENCES users(id);
AlTer TABLE users ADD FOREIGN KEY (class_id) REFERENCES classes(id);