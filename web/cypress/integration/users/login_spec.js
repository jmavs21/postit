describe('login', () => {
  it('logins', () => {
    cy.visit('/');
    cy.url().should('include', '/posts');
    cy.get('a[href="/login"]').click();
    cy.url().should('include', 'login');
    cy.get('#email').type('bob@bob.com');
    cy.get('#password').type('bob');
    cy.contains('button', 'Login').click();
    cy.contains('Bob');
    cy.url()
      .should('include', '/posts')
      .then(() => {
        expect(window.localStorage.getItem('token')).to.be.a('string');
      });
    cy.get('a[href="/logout"]')
      .click()
      .then(() => {
        expect(window.localStorage.getItem('token')).to.be.a('null');
      });
    cy.contains('Login');
  });

  it('does not login with empty email', () => {
    cy.visit('/');
    cy.get('a[href="/login"]').click();
    cy.get('#password').type('bob');
    cy.contains('button', 'Login').click();
    cy.contains('size must be between 1 and 50');
    cy.url().should('include', 'login');
  });

  it('does not login with empty password', () => {
    cy.visit('/');
    cy.get('a[href="/login"]').click();
    cy.get('#email').type('bob@bob.com');
    cy.contains('button', 'Login').click();
    cy.contains('size must be between 1 and 50');
    cy.url().should('include', 'login');
  });
});
