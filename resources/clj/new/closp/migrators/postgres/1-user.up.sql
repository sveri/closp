CREATE TABLE users (
id bigserial NOT NULL PRIMARY KEY,
email text UNIQUE NOT NULL,
displayname text NOT NULL,
role text NOT NULL,
last_login time,
is_active BOOLEAN DEFAULT FALSE NOT NULL,
password text
);

ALTER TABLE users OWNER TO {{name}};

ALTER TABLE ONLY users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);

--;;
INSERT INTO users (email, displayname, role, is_active, password) VALUES
('admin@localhost.de', 'admin', 'admin', true,
'bcrypt+sha512$d6d175aaa9c525174d817a74$12$24326124313224314d345444356149457a67516150447967517a67472e717a2e777047565a7071495330625441704f46686a556b5535376849743575');
