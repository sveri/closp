const {Client} = require('pg')

const client = new Client({
    user: '{{name}}',
    host: 'localhost',
    database: '{{name}}',
    password: '{{name}}',
    port: 5432,
})

async function reset() {
    await client.connect()

    await client.query('DROP TABLE IF EXISTS users')
    await client.query('CREATE TABLE users ( id bigserial NOT NULL PRIMARY KEY, email text UNIQUE,' +
        ' displayname text, role text NOT NULL, last_login time, is_active BOOLEAN DEFAULT FALSE ' +
        'NOT NULL, password text);')
    await client.query("INSERT INTO users (email, displayname, role, is_active, password) VALUES\n" +
        "('admin@localhost.de', 'admin', 'admin', true,\n" +
        "'bcrypt+sha512$d6d175aaa9c525174d817a74$12$24326124313224314d345444356149457a67516150447967517a67472e717a2e777047565a7071495330625441704f46686a556b5535376849743575');")
    await client.end()
}

reset()
