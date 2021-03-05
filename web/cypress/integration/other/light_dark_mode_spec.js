describe('light and dark mode', () => {
  it('switches mode', () => {
    cy.login();
    cy.get('.dark-mode').then(() => {
      expect(window.localStorage.getItem('darkMode')).to.be.equal('true');
    });
    cy.get('button[aria-label^="change to light mode"]').click();
    cy.get('.light-mode').then(() => {
      expect(window.localStorage.getItem('darkMode')).to.be.equal('false');
    });
    cy.get('button[aria-label^="change to dark mode"]').click();
    cy.get('.dark-mode').then(() => {
      expect(window.localStorage.getItem('darkMode')).to.be.equal('true');
    });
  });
});
