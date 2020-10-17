import React, { useContext } from 'react';
import { Formik, Form } from 'formik';
import { Wrapper } from '../Wrapper';
import { InputField } from '../InputField';
import { Box, Button } from '@chakra-ui/core';
import { login } from '../../services/authService';
import { Redirect } from 'react-router-dom';
import { UserContext } from '../../utils/UserContext';

interface LoginProps {
  location: any;
}

export interface LoginValues {
  email: string;
  password: string;
}

export const Login: React.FC<LoginProps> = ({ location }) => {
  const { user, setUser } = useContext(UserContext);
  if (user) return <Redirect to="/" />;
  return (
    <Wrapper variant="small">
      <Formik
        initialValues={{ email: '', password: '' } as LoginValues}
        onSubmit={async (values, { setErrors }) => {
          const { data, errors } = await login(values);
          if (errors) {
            setErrors(errors);
          } else {
            if (data && setUser) setUser(data);
            window.location.href = location.state
              ? location.state.from.pathname
              : '/';
          }
        }}
      >
        {({ isSubmitting }) => (
          <Form>
            <InputField
              type="email"
              name="email"
              placeholder="email"
              label="Email"
            />
            <Box mt={4}>
              <InputField
                name="password"
                placeholder="password"
                label="Password"
                type="password"
              />
            </Box>
            <Button
              mt={4}
              type="submit"
              isLoading={isSubmitting}
              loadingText="Login..."
            >
              Login
            </Button>
          </Form>
        )}
      </Formik>
    </Wrapper>
  );
};
