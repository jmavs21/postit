describe('profile', () => {
  it('shows profile', () => {
    cy.login();
    cy.contains('Bob').click();
    cy.url().should('include', 'profile');
    cy.get('#name').should('contains.value', 'Bob');
    cy.contains('Following');
    cy.contains('Followers');
  });

  it('updates profile', () => {
    cy.login();
    cy.contains('Bob').click();
    cy.get('#name').clear().type('Bob2');
    cy.contains('button', 'Update').click();
    cy.contains('Bob2').click();
    cy.get('#name').clear().type('Bob');
    cy.contains('button', 'Update').click();
    cy.contains('Bob');
  });
});
