import Chance from 'chance';

const chance = new Chance();

describe('votes', () => {
  it('votes on a post', () => {
    cy.login();
    cy.get('a[href="/posts/new"]').click();
    cy.url().should('include', 'new');
    cy.get('#title').type(chance.sentence({ words: 5 }));
    cy.get('#text').type(chance.paragraph({ sentences: 1 }));
    cy.contains('button', 'Create').click();
    cy.contains('0');
    cy.get('button[aria-label="up vote"]').click();
    cy.contains('1');
    cy.get('button[aria-label="down vote"]').click();
    cy.contains('-1');
    cy.get('button[aria-label="Edit post"]').click();
    cy.get('button[aria-label="Delete post"]').click();
  });
});
