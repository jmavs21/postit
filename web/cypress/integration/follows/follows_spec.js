describe('follows', () => {
  it('follows another user', () => {
    cy.login();
    cy.contains('button', 'Follow').click().next().contains('Muire');
    cy.contains('Bob').click();
    cy.url().should('include', 'profile');
    cy.contains('Following').next().contains('Muire');
    cy.go('back');
    cy.contains('button', 'Following').click().next().contains('Muire');
  });

  it('is followed by another user', () => {
    cy.loginAsOther();
    cy.get('#searchText').type('Bob{enter}');
    cy.contains('button', 'Follow').click().next().contains('Bob');
    cy.login();
    cy.contains('Bob').click();
    cy.url().should('include', 'profile');
    cy.contains('Followers').next().contains('Muire');
    cy.loginAsOther();
    cy.get('#searchText').type('Bob{enter}');
    cy.contains('button', 'Following').click().next().contains('Bob');
  });
});
