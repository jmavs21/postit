Cypress.Commands.add('login', () => {
  cy.visit('/', {
    onBeforeLoad(window) {
      window.localStorage.setItem(
        'token',
        'eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiQm9iIiwiaWQiOjEsImVtYWlsIjoiYm9iQGJvYi5jb20iLCJzdWIiOiJib2JAYm9iLmNvbSIsImlhdCI6MTYwMjI3OTE5NywiZXhwIjoxOTE3Njc5MTk3fQ.4sV4C_GE8QcedIZHgllu9s6FTq8xsJIwxqFcg5xfVHU'
      );
    },
  });
});
