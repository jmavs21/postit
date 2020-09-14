import React from 'react';
import { theme, ThemeProvider, CSSReset } from '@chakra-ui/core';
import { Switch, Route } from 'react-router-dom';
import { Register } from './components/pages/Register';

const App: React.FC = () => {
  return (
    <ThemeProvider theme={theme}>
      <CSSReset />
      <Switch>
        <Route path="/" component={Register} />
      </Switch>
    </ThemeProvider>
  );
};

export default App;
