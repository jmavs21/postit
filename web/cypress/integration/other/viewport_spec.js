describe('viewport', () => {
  it('shows app on mobile', () => {
    cy.visit('/');
    cy.viewport('iphone-7', 'landscape');
    cy.get('#searchText').should('be.visible');
    cy.viewport('iphone-7');
    cy.get('#searchText').should('be.visible');
  });
});
