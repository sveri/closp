CREATE TABLE users (
id bigserial NOT NULL PRIMARY KEY,
first_name character varying(30),
last_name character varying(30),
role character varying(30),
email character varying(30) NOT NULL,
last_login time,
is_active BOOLEAN DEFAULT FALSE NOT NULL,
pass character varying(200)
);

ALTER TABLE users OWNER TO getless;

ALTER TABLE ONLY users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);

--;;
INSERT INTO users (first_name, last_name, email, role, is_active, pass) VALUES
('admin', 'admin', 'admin@localhost.de', 'admin', true,
'bcrypt+sha512$d6d175aaa9c525174d817a74$12$24326124313224314d345444356149457a67516150447967517a67472e717a2e777047565a7071495330625441704f46686a556b5535376849743575')
