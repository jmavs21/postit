import { Box, Button } from '@chakra-ui/core';
import { Formik, Form } from 'formik';
import React, { useContext } from 'react';
import { updateUser } from '../../services/userService';
import { UserContext } from '../../utils/UserContext';
import { InputField } from '../InputField';
import { Wrapper } from '../Wrapper';

interface ProfileProps {
  location: any;
}

export interface ProfileValues {
  name: string;
}

export const Profile: React.FC<ProfileProps> = ({ location }) => {
  const { user, setUser } = useContext(UserContext);
  if (!user) {
    console.log('Error with user from context');
    return null;
  }
  return (
    <Wrapper variant="small">
      <Formik
        initialValues={{ name: user.name } as ProfileValues}
        onSubmit={async (values, { setErrors }) => {
          const response = await updateUser(values, user.id);
          if (response.errors) {
            setErrors(response.errors);
          } else {
            if (response.data && setUser) setUser(response.data);
            window.location.href = location.state
              ? location.state.from.pathname
              : '/';
          }
        }}
      >
        {({ isSubmitting }) => (
          <Form>
            <Box>
              <InputField name="name" placeholder="name" label="Name" />
            </Box>
            <Button
              mt={4}
              type="submit"
              isLoading={isSubmitting}
              loadingText="Updating"
            >
              Update
            </Button>
          </Form>
        )}
      </Formik>
    </Wrapper>
  );
};
