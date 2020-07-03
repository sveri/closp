import '../support/user_support'

describe('Spec:', () => {
    it('register_forward-after-success_logout_register_expect-email-exists', () => {
        cy.apigen.signup_user('admin2', 'foo2@localhost.de', 'foobar')
        cy.get('nav').should("contain.text", "admin2")
        cy.get('nav').get('a.dropdown-trigger').click()
        cy.get('nav #navbar-profile-dropdown a[href*="logout"]').click()

        cy.apigen.signup_user('admin2', 'foo2@localhost.de', 'foobar')
        cy.get('#signup-form').should('contain.text', 'This email already exists')
    })

    it('registered_user_should_be_able_to_login', () => {
        cy.apigen.signup_user('admin2', 'foo2@localhost.de', 'foobar')
        cy.get('nav').should("contain.text", "admin2")
        cy.get('nav').get('a.dropdown-trigger').click()
        cy.get('nav #navbar-profile-dropdown a[href*="logout"]').click()

        cy.apigen.login_user('foo2@localhost.de', 'foobar')
        cy.get('nav').should("contain.text", "admin2")
    })

    it('invalid_login_email', () => {
        cy.apigen.login_user('foo2@localhost.de', 'foobar')
        cy.get('#login-form').should('contain.text', 'Please provide a correct email')
    })

    it('invalid_login_password', () => {
        cy.apigen.signup_user('admin2', 'foo2@localhost.de', 'foobar')
        cy.get('nav').should("contain.text", "admin2")
        cy.get('nav').get('a.dropdown-trigger').click()
        cy.get('nav #navbar-profile-dropdown a[href*="logout"]').click()

        cy.apigen.login_user('foo2@localhost.de', 'foobarwer')
        cy.get('#login-form').should('contain.text', 'Please provide a correct password')
    })

    it('change-password_and_relogin', () => {
        cy.apigen.signup_user('admin2', 'foo2@localhost.de', 'foobar')

        cy.get('nav').get('a.dropdown-trigger').click()
        cy.get('nav #navbar-profile-dropdown a[href*="change"]').click()

        cy.get('#oldpassword').type('foobar')
        cy.get('#password').type('foobarNew')
        cy.get('#confirm').type('foobarNew')
        cy.get('#changepassword-form').submit()
        cy.get('body').should("contain.text", 'Password changed')

        cy.get('nav').get('a.dropdown-trigger').click()
        cy.get('nav #navbar-profile-dropdown a[href*="logout"]').click({force: true})

        cy.apigen.login_user('foo2@localhost.de', 'foobarNew')
        cy.get('nav').should("contain.text", "admin2")
    })

    it('change-password_dont_match', () => {
        cy.apigen.signup_user('admin2', 'foo2@localhost.de', 'foobar')

        cy.get('nav').get('a.dropdown-trigger').click()
        cy.get('nav #navbar-profile-dropdown a[href*="change"]').click()

        cy.get('#oldpassword').type('foobar')
        cy.get('#password').type('foobarNew')
        cy.get('#confirm').type('foobarNewWrong')
        cy.get('#changepassword-form').submit()
        cy.get('body').should("contain.text", 'Entered passwords do not match')
    })

    it('change-password_wrong-old-password', () => {
        cy.apigen.signup_user('admin2', 'foo2@localhost.de', 'foobar')

        cy.get('nav').get('a.dropdown-trigger').click()
        cy.get('nav #navbar-profile-dropdown a[href*="change"]').click()

        cy.get('#oldpassword').type('foobarOldWrong')
        cy.get('#password').type('foobarNew')
        cy.get('#confirm').type('foobarNewWrong')
        cy.get('#changepassword-form').submit()
        cy.get('body').should("contain.text", 'Current password was incorrect')
    })

    it('login_with-forward', () => {
        cy.apigen.signup_user('admin2', 'foo2@localhost.de', 'foobar')
        cy.get('nav').should("contain.text", "admin2")
        cy.get('nav').get('a.dropdown-trigger').click()
        cy.get('nav #navbar-profile-dropdown a[href*="logout"]').click()

        cy.visit('/user/changepassword')
        cy.get('#email').type('foo2@localhost.de')
        cy.get('#password').type('foobar')
        cy.get('#login-form').submit()
        cy.get('nav').should("contain.text", "Change Password")
    })
})