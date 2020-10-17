import React, { useMemo, useState } from 'react';
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
import { getUserFromJwt } from './services/authService';
import { Profile } from './components/pages/Profile';
import { CreatePost } from './components/pages/CreatePost';
import { ProtectedRoute } from './components/ProtectedRoute';
import { PostView } from './components/pages/PostView';

const App: React.FC = () => {
  const [user, setUser] = useState(() => getUserFromJwt());
  const value = useMemo(() => ({ user, setUser }), [user, setUser]);

  return (
    <ThemeProvider theme={theme}>
      <ColorModeProvider>
        <CSSReset />
        <UserContext.Provider value={value}>
          <NavBar />
          <Switch>
            <Route path="/register" component={Register} />
            <Route path="/login" component={Login} />
            <Route path="/logout" component={Logout} />
            <ProtectedRoute path="/profile" component={Profile} />
            <ProtectedRoute path="/posts/new" component={CreatePost} />
            <Route path="/posts/:id" component={PostView} />
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
