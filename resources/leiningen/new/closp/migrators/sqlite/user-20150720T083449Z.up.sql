CREATE TABLE user (
id INTEGER CONSTRAINT PK_USERRC PRIMARY KEY AUTOINCREMENT NOT NULL,
first_name VARCHAR(30),
last_name VARCHAR(30),
role VARCHAR(30),
email VARCHAR(30) NOT NULL,
last_login time,
is_active BOOLEAN DEFAULT 0 NOT NULL,
pass VARCHAR(200),
activationid VARCHAR(100),
uuid VARCHAR(43) NOT NULL,
UNIQUE (email),
UNIQUE (activationid));
--;;
INSERT INTO user ("first_name", "last_name", "email", "role", "is_active", "pass", "uuid") VALUES
('admin', 'admin', 'admin@localhost.de', 'admin', 1,
'bcrypt+sha512$d6d175aaa9c525174d817a74$12$24326124313224314d345444356149457a67516150447967517a67472e717a2e777047565a7071495330625441704f46686a556b5535376849743575',
'b4f18236-2a14-49f6-837e-5e23def53124')
