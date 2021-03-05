describe('not found page', () => {
  it('forwards to not found page when invalid route', () => {
    cy.visit('/invalid');
    cy.contains('Page not found');
  });
});
