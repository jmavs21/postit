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

Cypress.Commands.add('loginAsOther', () => {
  cy.visit('/', {
    onBeforeLoad(window) {
      window.localStorage.setItem(
        'token',
        'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6NSwibmFtZSI6Ik11aXJlIiwiZW1haWwiOiJtZWxpczRAdWNzZC5lZHUiLCJzdWIiOiJtZWxpczRAdWNzZC5lZHUiLCJpYXQiOjE2MTQ4OTM2NjgsImV4cCI6MTkzMDI5MzY2OH0.ZbwdLPd90xkn2VNZ8ZZV4SUP1OCmflpe4ei455KG4vg'
      );
    },
  });
});
