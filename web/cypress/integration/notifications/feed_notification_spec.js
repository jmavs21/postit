describe('feed notification', () => {
  it('shows notification when a follower makes a new post', () => {
    cy.login();
    cy.contains('button', 'Follow').click().next().contains('Muire');
    cy.fixture('post.json').then((post) => {
      cy.request({
        method: 'POST',
        url: Cypress.env('apiUrl') + '/posts',
        headers: {
          'Content-Type': 'application/json',
          'x-auth-token': Cypress.env('muireToken'),
        },
        body: post,
      }).then((response) => {
        cy.contains('New post from Muire');
        cy.request({
          method: 'DELETE',
          url: Cypress.env('apiUrl') + `/posts/${response.body.id}`,
          headers: {
            'Content-Type': 'application/json',
            'x-auth-token': Cypress.env('muireToken'),
          },
        });
      });
    });
    cy.contains('button', 'Following').click().next().contains('Muire');
  });
});
