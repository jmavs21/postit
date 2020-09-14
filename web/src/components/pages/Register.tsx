import React from 'react';
import { Formik, Form } from 'formik';
import { Wrapper } from '../Wrapper';
import { InputField } from '../InputField';
import { Box, Button } from '@chakra-ui/core';

interface RegisterProps {}

export const Register: React.FC<RegisterProps> = () => {
  return (
    <Wrapper variant="small">
      <Formik
        initialValues={{ username: '', password: '' }}
        onSubmit={(values) => {
          console.log(values);
        }}
      >
        {({ isSubmitting }) => (
          <Form>
            <InputField
              name="username"
              placeholder="username"
              label="Username"
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
              loadingText="Registering"
              variantColor="blue"
            >
              Register
            </Button>
          </Form>
        )}
      </Formik>
    </Wrapper>
  );
};
