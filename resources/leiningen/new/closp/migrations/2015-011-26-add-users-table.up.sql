CREATE TABLE users
(id VARCHAR(20) AUTO_INCREMENT,
 first_name VARCHAR(30),
 last_name VARCHAR(30),
 email VARCHAR(30),
 admin BOOLEAN,
 last_login TIME,
 is_active BOOLEAN,
 pass VARCHAR(100),
 PRIMARY KEY (ID));
 
INSERT INTO users (first_name, last_name, email, admin, is_active, pass) VALUES
('admin', 'admin', 'admin@localhost.de', true, true, 'admin')
 