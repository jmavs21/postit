describe('search posts', () => {
  it('searches posts by title', () => {
    cy.login();
    cy.get('#searchText').type('Distributed asymmetric{enter}');
    cy.contains('Distributed asymmetric structure');
  });

  it('searches post by text', () => {
    cy.login();
    cy.get('#searchText').type(
      'Praesent blandit. Nam nulla. Integer pede justo, lacinia eget, tincidunt eget, tempus vel, pede.{enter}'
    );
    cy.contains('Distributed asymmetric structure');
  });

  it('searches post by user name', () => {
    cy.login();
    cy.get('#searchText').type('Xever{enter}');
    cy.contains('Distributed asymmetric structure');
  });

  it('searches post by user name when clicking on posts user name', () => {
    cy.login();
    cy.get('a[href="/posts?search=Muire"]:first').click();
    cy.contains('Muire');
    cy.get('a[href="/posts?search=Michal"]').should('not.exist');
  });
});
