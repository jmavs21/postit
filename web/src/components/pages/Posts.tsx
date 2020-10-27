import { Flex, Stack } from '@chakra-ui/core';
import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { Waypoint } from 'react-waypoint';
import { getPosts, Post, PostsRes } from '../../services/postService';
import { SEARCH_QUERY } from '../../utils/constants';
import { LoadingProgress } from '../LoadingProgress';
import { PostCard } from '../PostCard';
import { Wrapper } from '../Wrapper';

const getSearchQuery = (search: string) => {
  const query = new URLSearchParams(search).get(SEARCH_QUERY);
  return query ? query : '';
};

export const Posts: React.FC = () => {
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

  const getLastDate = (posts: Post[]) => {
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
                <PostCard p={p} changePostsFollows={changePostsFollows} />
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
