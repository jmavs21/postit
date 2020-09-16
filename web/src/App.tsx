import React from 'react';
import {
  theme,
  ThemeProvider,
  CSSReset,
  ColorModeProvider,
} from '@chakra-ui/core';
import { Switch, Route, Redirect } from 'react-router-dom';
import { Register } from './components/pages/Register';
import { Posts } from './components/pages/Posts';
import { NotFound } from './components/pages/NotFound';
import { Login } from './components/pages/Login';
import { NavBar } from './components/NavBar';
import { Logout } from './components/Logout';
import { UserContext } from './utils/UserContext';
import { getLocalUser } from './services/authService';

const App: React.FC = () => {
  return (
    <ThemeProvider theme={theme}>
      <ColorModeProvider>
        <CSSReset />
        <UserContext.Provider value={getLocalUser()}>
          <NavBar />
          <Switch>
            <Route path="/register" component={Register} />
            <Route path="/login" component={Login} />
            <Route path="/logout" component={Logout} />
            <Route path="/posts" component={Posts} />
            <Route path="/not-found" component={NotFound} />
            <Redirect from="/" exact to="/posts" />
            <Redirect to="/not-found" />
          </Switch>
        </UserContext.Provider>
      </ColorModeProvider>
    </ThemeProvider>
  );
};

export default App;
