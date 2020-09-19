import { Box, Button } from '@chakra-ui/core';
import { Formik, Form } from 'formik';
import React from 'react';
import { InputField } from '../InputField';
import { Wrapper } from '../Wrapper';
import { History } from 'history';
import { createPost } from '../../services/postService';

interface PostCreateProps {
  history: History;
}

export interface PostCreateValues {
  title: string;
  text: string;
}

export const PostCreate: React.FC<PostCreateProps> = ({ history }) => {
  return (
    <Wrapper variant="small">
      <Formik
        initialValues={{ title: '', text: '' } as PostCreateValues}
        onSubmit={async (values, { setErrors }) => {
          const response = await createPost(values);
          if (response.errors) {
            setErrors(response.errors);
          } else {
            history.push('/posts');
          }
        }}
      >
        {({ isSubmitting }) => (
          <Form>
            <Box>
              <InputField name="title" placeholder="title" label="Title" />
            </Box>
            <Box mt={4}>
              <InputField
                textarea
                name="text"
                placeholder="text..."
                label="Text"
              />
            </Box>
            <Button
              mt={4}
              type="submit"
              isLoading={isSubmitting}
              loadingText="Creating"
            >
              Create
            </Button>
          </Form>
        )}
      </Formik>
    </Wrapper>
  );
};
