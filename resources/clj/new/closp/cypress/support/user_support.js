cy.apigen = {
    signup_user: (display_name, email, password) => {
        cy.visit('/user/signup')
        cy.get('#displayname').type(display_name)
        cy.get('#email').type(email)
        cy.get('#password').type(password)
        cy.get('#signup-form').submit()
    },

    login_user: (email, password) => {
        cy.visit('/user/login')
        cy.get('#email').type(email)
        cy.get('#password').type(password)
        cy.get('#login-form').submit()
    }
}