CREATE TABLE "users"
("id" VARCHAR(20) AUTO_INCREMENT,
 "first_name" VARCHAR(30),
 "last_name" VARCHAR(30),
 "email" VARCHAR(30) UNIQUE,
 "admin" BOOLEAN,
 "last_login" TIME,
 "is_active" BOOLEAN,
 "pass" VARCHAR(100),
 "activationid" VARCHAR(100) UNIQUE,
 PRIMARY KEY ("id"));
 
INSERT INTO "users" ("first_name", "last_name", "email", "admin", "is_active", "pass") VALUES
('admin', 'admin', 'admin@localhost.de', true, true, 'bcrypt+sha512$d6d175aaa9c525174d817a74$12$24326124313224314d345444356149457a67516150447967517a67472e717a2e777047565a7071495330625441704f46686a556b5535376849743575')
 