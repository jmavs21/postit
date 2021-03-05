describe('protected routes', () => {
  it('forwards to login if trying to create new post', () => {
    cy.visit('/');
    cy.get('a[href="/posts/new"]').click();
    cy.url().should('include', 'login');
    cy.contains('Email');
    cy.contains('Password');
  });

  it('forwards to login if trying to enter profile', () => {
    cy.visit('/profile');
    cy.url().should('include', 'login');
    cy.contains('Email');
    cy.contains('Password');
  });
});
