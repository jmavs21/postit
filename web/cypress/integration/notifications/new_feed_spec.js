describe('New feed notification', () => {
  it('shows notification when a follower makes a new post', () => {
    cy.login();
    cy.contains('button', 'Follow').click().next().contains('Muire');
    cy.request({
      method: 'POST',
      form: true,
      url: 'http://localhost:4000/api/posts',
      headers: {
        'Content-Type': 'application/json',
        'x-auth-token':
          'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6NSwibmFtZSI6Ik11aXJlIiwiZW1haWwiOiJtZWxpczRAdWNzZC5lZHUiLCJzdWIiOiJtZWxpczRAdWNzZC5lZHUiLCJpYXQiOjE2MTQ4OTM2NjgsImV4cCI6MTkzMDI5MzY2OH0.ZbwdLPd90xkn2VNZ8ZZV4SUP1OCmflpe4ei455KG4vg',
      },
      body: {
        title: 'the post title',
        text: 'the post text',
      },
    });
    cy.contains('New post from Muire');
  });
});
