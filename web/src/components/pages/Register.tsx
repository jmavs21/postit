import React, { useContext } from 'react';
import { Formik, Form } from 'formik';
import { Wrapper } from '../Wrapper';
import { InputField } from '../InputField';
import { Box, Button } from '@chakra-ui/core';
import { registerUser } from '../../services/userService';
import { Redirect } from 'react-router-dom';
import { UserContext } from '../../utils/UserContext';

interface RegisterProps {}

export interface RegisterValues {
  email: string;
  password: string;
  name: string;
}

export const Register: React.FC<RegisterProps> = () => {
  const { user } = useContext(UserContext);
  if (user) return <Redirect to="/" />;
  return (
    <Wrapper variant="small">
      <Formik
        initialValues={{ email: '', password: '', name: '' } as RegisterValues}
        onSubmit={async (values, { setErrors }) => {
          const response = await registerUser(values);
          if (response.errors) setErrors(response.errors);
          else window.location.href = '/';
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
              <InputField name="name" placeholder="name" label="Name" />
            </Box>
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
              loadingText="Registering"
            >
              Register
            </Button>
          </Form>
        )}
      </Formik>
    </Wrapper>
  );
};
