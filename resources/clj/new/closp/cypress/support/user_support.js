if(cy.apigen === undefined) {
    cy.apigen = {}
}

cy.apigen.user = {
    signup: (display_name, email, password) => {
        cy.visit('/user/signup')
        cy.get('#displayname').type(display_name)
        cy.get('#email').type(email)
        cy.get('#password').type(password)
        cy.get('#signup-form').submit()
    },

    login: (email, password, url) => {
        if(url === undefined) {
            url = '/user/login';
        }
        cy.visit(url)
        cy.get('#email').type(email)
        cy.get('#password').type(password)
        cy.get('#login-form').submit()
    }
}