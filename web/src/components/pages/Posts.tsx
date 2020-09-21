import {
  Box,
  Button,
  CircularProgress,
  Flex,
  Heading,
  Stack,
  Text,
} from '@chakra-ui/core';
import React, { useContext, useEffect, useState } from 'react';
import { NavLink } from 'react-router-dom';
import { getPosts, PostsRes } from '../../services/postService';
import { UserContext } from '../../utils/UserContext';
import { Votes } from '../Votes';
import { Wrapper } from '../Wrapper';

interface PostsProps {}

export const Posts: React.FC<PostsProps> = () => {
  const { user } = useContext(UserContext);
  const [postsState, setPostsState] = useState<PostsRes>({
    posts: [],
    hasMore: true,
  });
  const [isLoading, setIsLoading] = useState(false);

  const fetchData = async () => {
    setIsLoading(true);
    const response = await getPosts(
      postsState.posts.length !== 0
        ? postsState.posts[postsState.posts.length - 1].createdat
        : ''
    );
    if (response.data) {
      const postsResCopy = JSON.parse(JSON.stringify(postsState));
      postsResCopy.posts.push(...response.data.posts);
      setPostsState({
        posts: postsResCopy.posts,
        hasMore: response.data.hasMore,
      });
    }
    setIsLoading(false);
  };

  useEffect(() => {
    fetchData();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  return (
    <Wrapper>
      {isLoading && postsState.posts.length === 0 ? (
        <Flex>
          <CircularProgress
            m="auto"
            isIndeterminate
            color="green"
          ></CircularProgress>
        </Flex>
      ) : (
        <>
          <Stack spacing={8}>
            {postsState.posts.map((p) => (
              <Flex key={p.id} p={5} shadow="md" borderWidth="1px">
                <Votes key={p.id} post={p} isUser={user !== null} />
                <Box>
                  <Heading fontSize="xl">
                    <NavLink to={'/posts/' + p.id}>{p.title}</NavLink>
                  </Heading>
                  <Text>{p.user.name}</Text>
                  <Text mt={4}>{p.textSnippet}...</Text>
                </Box>
              </Flex>
            ))}
          </Stack>
          <Flex>
            <Button
              onClick={fetchData}
              isDisabled={!postsState.hasMore}
              m="auto"
              mt={8}
              isLoading={isLoading}
              loadingText="Loading"
            >
              Load more
            </Button>
          </Flex>
        </>
      )}
    </Wrapper>
  );
};
