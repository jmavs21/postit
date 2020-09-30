import { Box, Flex, Heading, Stack, Text } from '@chakra-ui/core';
import React, { useContext, useEffect, useState } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { Waypoint } from 'react-waypoint';
import { getPosts, PostsRes } from '../../services/postService';
import { SEARCH_QUERY } from '../../utils/constants';
import { UserContext } from '../../utils/UserContext';
import { LoadingProgress } from '../LoadingProgress';
import { Votes } from '../Votes';
import { Wrapper } from '../Wrapper';

interface PostsProps {}

const getSearchQuery = (search: string) => {
  const query = new URLSearchParams(search).get(SEARCH_QUERY);
  return query ? query : '';
};

export const Posts: React.FC<PostsProps> = () => {
  const { user } = useContext(UserContext);
  const [isLoading, setIsLoading] = useState(false);
  const { search } = useLocation();
  const [postsState, setPostsState] = useState<PostsRes>({
    posts: [],
    hasMore: true,
  });

  useEffect(() => {
    (async () => {
      setIsLoading(true);
      const { data } = await getPosts('', getSearchQuery(search));
      if (data) {
        setPostsState({
          posts: data.posts,
          hasMore: data.hasMore,
        });
      } else {
        setPostsState({
          posts: [],
          hasMore: false,
        });
      }
      setIsLoading(false);
    })();
  }, [search]);

  const loadMore = async () => {
    setIsLoading(true);
    const { data } = await getPosts(
      postsState.posts.length !== 0
        ? postsState.posts[postsState.posts.length - 1].createdat
        : '',
      getSearchQuery(search)
    );
    if (data) {
      const postsResCopy = JSON.parse(JSON.stringify(postsState));
      postsResCopy.posts.push(...data.posts);
      setPostsState({
        posts: postsResCopy.posts,
        hasMore: data.hasMore,
      });
    }
    setIsLoading(false);
  };

  return (
    <Wrapper>
      {isLoading && postsState.posts.length === 0 ? (
        <LoadingProgress />
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
                  <Text as="i">{p.user.name}</Text>
                  <Text mt={4}>{p.textSnippet}...</Text>
                </Box>
              </Flex>
            ))}
            <>
              {!isLoading && postsState.hasMore && (
                <Waypoint onEnter={loadMore} />
              )}
              {postsState.hasMore && <LoadingProgress />}
            </>
          </Stack>
        </>
      )}
    </Wrapper>
  );
};
