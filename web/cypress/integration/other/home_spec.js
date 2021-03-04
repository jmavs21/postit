describe('home', () => {
  it('goes to posts when clicking on home button', () => {
    cy.visit('/');
    cy.get('div[role="progressbar"]').should('exist');
    cy.contains('h2', 'POSTS').click();
    cy.url().should('include', '/posts');
  });
});
