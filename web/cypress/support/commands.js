Cypress.Commands.add('login', () => {
  cy.visit('/', {
    onBeforeLoad(window) {
      window.localStorage.setItem('token', Cypress.env('bobToken'));
      window.localStorage.setItem('darkMode', 'true');
    },
  });
});
