import { Box, Flex, IconButton, Button, useToast } from '@chakra-ui/core';
import { Formik, Form } from 'formik';
import React, { useEffect, useState } from 'react';
import { RouteComponentProps } from 'react-router-dom';
import { History } from 'history';
import {
  deletePostById,
  getPostById,
  Post,
  updatePost,
} from '../services/postService';
import { InputField } from './InputField';
import { CreatePostValues } from './pages/CreatePost';
import { Wrapper } from './Wrapper';
import { LoadingProgress } from './LoadingProgress';

interface MatchParams {
  id: string;
  name: string;
}

interface PostUpdateProps extends RouteComponentProps<MatchParams> {
  location: any;
  history: History;
}

export const PostUpdate: React.FC<PostUpdateProps> = ({
  location,
  history,
  ...props
}) => {
  const [isLoading, setIsLoading] = useState(false);
  const [post, setPost] = useState<Post>({} as Post);
  const toast = useToast();

  const postId = props.match.params.id;

  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true);
      const { data } = await getPostById(postId);
      if (data) setPost(data);
      setIsLoading(false);
    };
    fetchData();
  }, [postId]);

  const deletePost = async (postId: number) => {
    setIsLoading(true);
    const isOk = await deletePostById(post.id);
    setIsLoading(false);
    if (isOk) {
      window.location.href = location.state
        ? location.state.from.pathname
        : '/';
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
      {isLoading || !post.user ? (
        <LoadingProgress />
      ) : (
        <Box p={5} shadow="md" borderWidth="1px">
          <Flex>
            <IconButton
              ml="auto"
              size="xs"
              variantColor="red"
              aria-label="Delete post"
              onClick={() => deletePost(post.id)}
              icon="delete"
              isLoading={isLoading}
            />
          </Flex>
          <Box flex={1}>
            <Formik
              initialValues={
                { title: post.title, text: post.text } as CreatePostValues
              }
              onSubmit={async (values, { setErrors }) => {
                const { data, errors } = await updatePost(post.id, values);
                if (data) history.push(`/posts/${data.id}`);
                else if (errors) setErrors(errors);
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
                      placeholder="text"
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
      )}
    </Wrapper>
  );
};
