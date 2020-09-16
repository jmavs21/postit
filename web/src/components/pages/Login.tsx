import React from 'react';
import { Formik, Form } from 'formik';
import { Wrapper } from '../Wrapper';
import { InputField } from '../InputField';
import { Box, Button } from '@chakra-ui/core';
import { login, getLocalUser } from '../../services/authService';
import { Redirect } from 'react-router-dom';

interface LoginProps {
  location: any;
}

export interface LoginValues {
  email: string;
  password: string;
}

export const Login: React.FC<LoginProps> = ({ location }) => {
  if (getLocalUser()) return <Redirect to="/" />;
  return (
    <Wrapper variant="small">
      <Formik
        initialValues={{ email: '', password: '' } as LoginValues}
        onSubmit={async (values, { setErrors }) => {
          const response = await login(values);
          if (response.errors) {
            setErrors(response.errors);
          } else {
            console.log('location=', location.state);
            window.location.href = location.state
              ? location.state.from.pathname
              : '/';
          }
        }}
      >
        {({ isSubmitting }) => (
          <Form>
            <InputField name="email" placeholder="email" label="Email" />
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
              loadingText="Logining"
              variantColor="blue"
            >
              Login
            </Button>
          </Form>
        )}
      </Formik>
    </Wrapper>
  );
};
