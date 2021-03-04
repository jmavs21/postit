describe('disabled features without login', () => {
  it('can not up vote, down vote or follow a user', () => {
    cy.visit('/');
    cy.get('button[aria-label="up vote"]:first').should('be.disabled');
    cy.get('button[aria-label="down vote"]:first').should('be.disabled');
    cy.contains('Follow').should('be.disabled');
  });
});
