CREATE TABLE "user"
("id" INT(11) AUTO_INCREMENT,
 "first_name" VARCHAR(30),
 "last_name" VARCHAR(30),
 "email" VARCHAR(30) UNIQUE,
 "role" VARCHAR(30),
 "last_login" TIME,
 "is_active" BOOLEAN,
 "pass" VARCHAR(200),
 "activationid" VARCHAR(100) UNIQUE,
 "uuid" VARCHAR(43),
 PRIMARY KEY ("id"));
 
INSERT INTO "user" ("first_name", "last_name", "email", "role", "is_active", "pass", "uuid") VALUES
('admin', 'admin', 'admin@localhost.de', 'admin', true, 'bcrypt+sha512$d6d175aaa9c525174d817a74$12$24326124313224314d345444356149457a67516150447967517a67472e717a2e777047565a7071495330625441704f46686a556b5535376849743575', 'b4f18236-2a14-49f6-837e-5e23def53124')
 