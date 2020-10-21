import { Box, Flex, Heading, Link, Stack, Text } from '@chakra-ui/core';
import React, { useContext, useEffect, useState } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { Waypoint } from 'react-waypoint';
import { getPosts, PostSnippet, PostsRes } from '../../services/postService';
import { SEARCH_QUERY } from '../../utils/constants';
import { UserContext } from '../../utils/UserContext';
import { Follow } from '../Follow';
import { LoadingProgress } from '../LoadingProgress';
import { Votes } from '../Votes';
import { Wrapper } from '../Wrapper';

const getSearchQuery = (search: string) => {
  const query = new URLSearchParams(search).get(SEARCH_QUERY);
  return query ? query : '';
};

export const Posts: React.FC = () => {
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
      getLastDate(postsState.posts),
      getSearchQuery(search)
    );
    if (data) {
      const posts = getPostsCopy().posts;
      posts.push(...data.posts);
      setPostsState({
        posts,
        hasMore: data.hasMore,
      });
    }
    setIsLoading(false);
  };

  const getLastDate = (posts: PostSnippet[]) => {
    if (posts.length === 0) return '';
    return posts.reduce((p1, p2) =>
      new Date(p1.createdat) < new Date(p2.createdat) ? p1 : p2
    ).createdat;
  };

  const getPostsCopy = () => {
    return JSON.parse(JSON.stringify(postsState));
  };

  const changePostsFollows = (toId: number, isFollow: boolean) => {
    const posts = getPostsCopy().posts;
    for (const p of posts) {
      if (p.user.id === toId) p.isFollow = isFollow;
    }
    setPostsState({
      posts,
      hasMore: postsState.hasMore,
    });
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
                  <Link as="header">
                    <NavLink to={'/posts/' + p.id}>
                      <Heading fontSize="xl">{p.title}</Heading>
                    </NavLink>
                  </Link>
                  <Flex>
                    <Follow
                      key={p.id}
                      changePostsFollows={changePostsFollows}
                      post={p}
                      from={user}
                      toId={p.user.id}
                    />
                    <Link as="i">
                      <NavLink to={'/posts?search=' + p.user.name}>
                        {p.user.name}
                      </NavLink>
                    </Link>
                  </Flex>
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
