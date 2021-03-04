import Chance from 'chance';

const chance = new Chance();
const title = chance.sentence({ words: 5 });
const text = chance.paragraph({ sentences: 1 });

describe('crud post', () => {
  it('creates, reads, updates and deletes a post', () => {
    const titleUpdate = chance.sentence({ words: 5 });
    const textUpdate = chance.paragraph({ sentences: 1 });
    cy.login();
    cy.get('a[href="/posts/new"]').click();
    cy.url().should('include', 'new');
    cy.get('#title').type(title);
    cy.get('#text').type(text);
    cy.contains('button', 'Create').click();
    cy.url().should('include', 'posts/');
    cy.contains(title);
    cy.contains(text);
    cy.contains('a[href="/posts?search=Bob"]', 'Bob');
    cy.get('button[aria-label="Edit post"]').click();
    cy.url().should('include', 'update');
    cy.get('#title').clear().type(titleUpdate);
    cy.get('#text').clear().type(textUpdate);
    cy.contains('button', 'Update').click();
    cy.contains(titleUpdate);
    cy.contains(textUpdate);
    cy.contains('a[href="/posts?search=Bob"]', 'Bob');
    cy.get('button[aria-label="Edit post"]').click();
    cy.get('button[aria-label="Delete post"]').click();
    cy.url().should('include', '/posts');
    cy.get('#title').should('not.exist');
    cy.get('#text').should('not.exist');
  });

  it('does not create or update post with empty title or text', () => {
    cy.login();
    cy.get('a[href="/posts/new"]').click();
    cy.get('#text').type(text);
    cy.contains('button', 'Create').click();
    cy.contains('size must be between 1 and 50');
    cy.url().should('include', 'new');
    cy.get('#text').clear();
    cy.get('#title').type(title);
    cy.contains('button', 'Create').click();
    cy.contains('size must be between 1 and 50');
    cy.url().should('include', 'new');
    cy.get('#text').type(text);
    cy.contains('button', 'Create').click();
    cy.get('button[aria-label="Edit post"]').click();
    cy.get('#title').clear();
    cy.contains('button', 'Update').click();
    cy.contains('size must be between 1 and 50');
    cy.url().should('include', 'update');
    cy.get('#title').type(title);
    cy.get('#text').clear();
    cy.contains('button', 'Update').click();
    cy.contains('size must be between 1 and 50');
    cy.get('button[aria-label="Delete post"]').click();
  });
});
