import Chance from 'chance';

const chance = new Chance();

describe('registration', () => {
  it('registers user, logins and logouts', () => {
    cy.visit('/');
    cy.url().should('include', '/posts');
    cy.get('a[href="/register"]').click();
    cy.url().should('include', 'register');
    cy.get('#email').type(chance.email());
    cy.get('#name').type(chance.word());
    cy.get('#password').type(chance.word());
    cy.contains('button', 'Register').click();
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

  it('does not register user with empty email', () => {
    cy.visit('/');
    cy.get('a[href="/register"]').click();
    cy.get('#name').type(chance.word());
    cy.get('#password').type(chance.word());
    cy.contains('button', 'Register').click();
    cy.contains('size must be between 1 and 50');
    cy.url().should('include', 'register');
  });

  it('does not register user with empty name', () => {
    cy.visit('/');
    cy.get('a[href="/register"]').click();
    cy.get('#email').type(chance.email());
    cy.get('#password').type(chance.word());
    cy.contains('button', 'Register').click();
    cy.contains('size must be between 1 and 50');
    cy.url().should('include', 'register');
  });

  it('does not register user with invalid character for name', () => {
    cy.visit('/');
    cy.get('a[href="/register"]').click();
    cy.get('#name').type('|');
    cy.get('#email').type(chance.email());
    cy.get('#password').type(chance.word());
    cy.contains('button', 'Register').click();
    cy.contains('only alphanumeric');
    cy.url().should('include', 'register');
  });

  it('does not register user with empty password', () => {
    cy.visit('/');
    cy.get('a[href="/register"]').click();
    cy.get('#email').type(chance.email());
    cy.get('#name').type(chance.word());
    cy.contains('button', 'Register').click();
    cy.contains('size must be between 1 and 50');
    cy.url().should('include', 'register');
  });
});
