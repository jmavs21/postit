describe('light and dark mode', () => {
  it('switch mode', () => {
    cy.visit('/');
    cy.get('button[aria-label^="change to"]').click();
    cy.get('button[aria-label^="change to"]').click();
  });
});
