const goOnline = () => {
  cy.log('**go online**')
    .then(() => {
      return Cypress.automation('remote:debugger:protocol', {
        command: 'Network.emulateNetworkConditions',
        params: {
          offline: false,
          latency: -1,
          downloadThroughput: -1,
          uploadThroughput: -1,
        },
      });
    })
    .then(() => {
      return Cypress.automation('remote:debugger:protocol', {
        command: 'Network.disable',
      });
    });
};

const goOffline = () => {
  cy.log('**go offline**')
    .then(() => {
      return Cypress.automation('remote:debugger:protocol', {
        command: 'Network.enable',
      });
    })
    .then(() => {
      return Cypress.automation('remote:debugger:protocol', {
        command: 'Network.emulateNetworkConditions',
        params: {
          offline: true,
          latency: -1,
          downloadThroughput: -1,
          uploadThroughput: -1,
        },
      });
    });
};

describe('offline mode', { browser: '!firefox' }, () => {
  beforeEach(goOnline);
  afterEach(goOnline);

  it('shows progress bar when selecting a post', () => {
    cy.visit('/');
    goOffline();
    cy.get('a[href="/posts/38"]').click();
    cy.get('div[role="progressbar"]').should('exist');
  });

  it('shows error message when trying to load posts', () => {
    cy.visit('/');
    goOffline();
    cy.contains('h2', 'POSTS').click();
    cy.contains('Please try again later');
  });

  it('can write new post', () => {
    cy.login();
    goOffline();
    cy.get('a[href="/posts/new"]').click();
    cy.url().should('include', 'new');
    cy.contains('Title');
    cy.contains('Text');
  });

  it('can view some information from profile', () => {
    cy.login();
    goOffline();
    cy.contains('Bob').click();
    cy.url().should('include', 'profile');
    cy.get('div[role="progressbar"]').should('exist');
  });
});
