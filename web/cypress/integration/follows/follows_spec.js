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
    cy.login();
    cy.request({
      method: 'POST',
      url: Cypress.env('apiUrl') + '/follows',
      headers: {
        'Content-Type': 'application/json',
        'x-auth-token': Cypress.env('muireToken'),
      },
      body: { toId: 1 },
    }).then((response) => {
      expect(response.body).to.be.equal('Followed');
      cy.contains('Bob').click();
      cy.url().should('include', 'profile');
      cy.contains('Followers').next().contains('Muire');
      cy.request({
        method: 'POST',
        url: Cypress.env('apiUrl') + '/follows',
        headers: {
          'Content-Type': 'application/json',
          'x-auth-token': Cypress.env('muireToken'),
        },
        body: { toId: 1 },
      }).then((response) => {
        expect(response.body).to.be.equal('Unfollowed');
        cy.reload();
        cy.url().should('include', 'profile');
        cy.contains('Followers');
        cy.get('a[href="/posts?search=Muire"]').should('not.exist');
      });
    });
  });
});
