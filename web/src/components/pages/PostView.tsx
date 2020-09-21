import {
  Stack,
  Flex,
  Box,
  Heading,
  Text,
  Alert,
  AlertIcon,
  IconButton,
  useToast,
  Button,
} from '@chakra-ui/core';
import { Formik, Form } from 'formik';
import React, { useContext, useEffect, useState } from 'react';
import { RouteComponentProps } from 'react-router-dom';
import {
  getPostById,
  Post,
  deletePostById,
  updatePost,
} from '../../services/postService';
import { UserContext } from '../../utils/UserContext';
import { InputField } from '../InputField';
import { Wrapper } from '../Wrapper';
import { PostCreateValues } from './PostCreate';
import { History } from 'history';

interface MatchParams {
  id: string;
  name: string;
}

interface PostViewProps extends RouteComponentProps<MatchParams> {
  history: History;
}

export const PostView: React.FC<PostViewProps> = ({ history, ...props }) => {
  const { user } = useContext(UserContext);
  const [post, setPost] = useState<Post>({} as Post);
  const toast = useToast();

  const postId = props.match.params.id;

  useEffect(() => {
    const fetchData = async () => {
      const response = await getPostById(postId);
      if (response.data) setPost(response.data);
    };
    fetchData();
  }, [postId]);

  const deletePost = async (postId: number) => {
    const isOk = await deletePostById(post.id);
    if (isOk) {
      window.location.href = '/';
    } else {
      toast({
        title: 'There was an error deleting the post.',
        description: 'Please try again later.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  return (
    <Wrapper>
      {!post.user ? (
        <Alert status="error">
          <AlertIcon />
          There was an error fetching the post.
        </Alert>
      ) : user != null && user.id === post.user.id ? (
        <Box p={5} shadow="md" borderWidth="1px">
          <Flex>
            <IconButton
              ml="auto"
              size="xs"
              variantColor="red"
              aria-label="Delete post"
              onClick={async () => deletePost(post.id)}
              icon="delete"
            />
          </Flex>
          <Box flex={1}>
            <Formik
              initialValues={
                { title: post.title, text: post.text } as PostCreateValues
              }
              onSubmit={async (values, { setErrors }) => {
                const response = await updatePost(post.id, values);
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
                    <InputField
                      name="title"
                      placeholder="title"
                      label="Title"
                    />
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
                    loadingText="Updating"
                  >
                    Update
                  </Button>
                </Form>
              )}
            </Formik>
          </Box>
        </Box>
      ) : (
        <Stack spacing={8}>
          <Flex p={5} shadow="md" borderWidth="1px">
            <Box>
              <Heading fontSize="xl">{post.title}</Heading>
              <Text>{post.user.name}</Text>
              <Text mt={4}>{post.text}</Text>
            </Box>
          </Flex>
        </Stack>
      )}
    </Wrapper>
  );
};
