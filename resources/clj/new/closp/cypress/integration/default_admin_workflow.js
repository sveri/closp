import '../support/user_support'

describe('Spec:', () => {
    function create_user_in_admin_view() {
        cy.apigen.user.login('admin@localhost.de', 'admin', '/admin/users')

        cy.get('#displayname').type("UserOne")
        cy.get('#email').type("aafoo@localhost.de")
        cy.get('#password').type("foobar")
        cy.get('#admin-add-user-form').submit()
    }

    it('create-user', () => {
        create_user_in_admin_view();
        cy.get('body').should("contain.text", "User added")
        cy.get('body').should("contain.text", "aafoo@localhost.de")
    })

    it('cancel-delete-user', () => {
        create_user_in_admin_view()
        cy.get('#admin-users-table .red:first').click()
        cy.get('#really-delete-cancel').click()
        cy.get('body').should("contain.text", "aafoo@localhost.de")
    })

    it('delete-user', () => {
        create_user_in_admin_view()
        cy.get('#admin-users-table .red:first').click()
        cy.get('#really-delete-delete').click()
        cy.get('body').should("contain.text", "User aafoo@localhost.de deleted successfully")
    })

    it('inactive-user-should-not-login', () => {
        create_user_in_admin_view()
        cy.get('.for-active-checkbox:first').click()
        cy.get('[data-test=update-button]:first').click()

        cy.get('nav').get('a.dropdown-trigger').click()
        cy.get('nav #navbar-profile-dropdown a[href*="logout"]').click()

        cy.apigen.user.login('aafoo@localhost.de', 'foobar')
        cy.get('body').should("contain.text", "User inactive. Login forbidden")
    })

})