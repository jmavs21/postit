describe('load more posts', () => {
  it('loads all 100 posts by 20 at a time', () => {
    cy.visit('/');
    cy.get('div[role="progressbar"]').should('exist');
    cy.get('button[aria-label="up vote"]').should('have.length', 20);
    cy.scrollTo('bottom');
    cy.get('div[role="progressbar"]').should('exist');
    cy.get('button[aria-label="up vote"]').should('have.length', 40);
    cy.scrollTo('bottom');
    cy.get('div[role="progressbar"]').should('exist');
    cy.get('button[aria-label="up vote"]').should('have.length', 60);
    cy.scrollTo('bottom');
    cy.get('div[role="progressbar"]').should('exist');
    cy.get('button[aria-label="up vote"]').should('have.length', 80);
    cy.scrollTo('bottom');
    cy.get('div[role="progressbar"]').should('exist');
    cy.get('button[aria-label="up vote"]').should('have.length', 100);
    cy.scrollTo('bottom');
    cy.get('div[role="progressbar"]').should('not.exist');
  });
});
