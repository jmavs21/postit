import React, { useContext } from 'react';
import { Redirect, Route } from 'react-router-dom';
import { UserContext } from '../utils/UserContext';

interface RouteProtectedProps {
  component: any;
  path: string;
}

export const RouteProtected: React.FC<RouteProtectedProps> = ({
  component: Component,
  ...rest
}) => {
  const { user } = useContext(UserContext);
  return (
    <Route
      {...rest}
      render={(props) => {
        if (!user)
          return (
            <Redirect
              to={{
                pathname: '/login',
                state: { from: props.location },
              }}
            />
          );
        return <Component {...props} />;
      }}
    />
  );
};
