import { Box, Button, Flex } from '@chakra-ui/core';
import { Formik, Form } from 'formik';
import React from 'react';
import { History } from 'history';
import { InputField } from '../InputField';
import { Wrapper } from '../Wrapper';
import { createPost } from '../../services/postService';

interface CreatePostProps {
  history: History;
}

export interface CreatePostValues {
  title: string;
  text: string;
}

export const CreatePost: React.FC<CreatePostProps> = ({ history }) => {
  return (
    <Wrapper>
      <Flex p={5} shadow="md" borderWidth="1px">
        <Box flex={1}>
          <Formik
            initialValues={{ title: '', text: '' } as CreatePostValues}
            onSubmit={async (values, { setErrors }) => {
              const { data, errors } = await createPost(values);
              if (data) history.push(`/posts/${data.id}`);
              else if (errors) setErrors(errors);
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
                    placeholder="text"
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
        </Box>
      </Flex>
    </Wrapper>
  );
};
