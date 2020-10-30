import { Flex, Box, Link, Heading, Text, IconButton } from '@chakra-ui/core';
import React, { useContext } from 'react';
import { NavLink } from 'react-router-dom';
import { Post } from '../services/postService';
import { UserContext } from '../utils/UserContext';
import { Follow } from './Follow';
import { Votes } from './Votes';

interface PostCardProps {
  p: Post;
  changePostsFollows: (toId: number, isFollow: boolean) => void | null;
  isView?: boolean;
}

export const PostCard: React.FC<PostCardProps> = ({
  p,
  changePostsFollows,
  isView,
}) => {
  const { user } = useContext(UserContext);
  return (
    <Box width="100%">
      <Flex>
        <Votes key={p.id} post={p} isUser={user !== null} />
        <Box mr="auto">
          {isView ? (
            <Heading fontSize="xl">{p.title}</Heading>
          ) : (
            <Link as="header">
              <NavLink to={'/posts/' + p.id}>
                <Heading fontSize="xl">{p.title}</Heading>
              </NavLink>
            </Link>
          )}
          <Flex mt={2}>
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
          <Text mt={4}>{p.text}</Text>
        </Box>
        {user?.id === p.user.id ? (
          <Flex justifyContent="flex-end">
            <NavLink to={'/posts/udpate/' + p.id}>
              <IconButton
                icon="edit"
                size="xs"
                ml="auto"
                aria-label="Edit post"
              />
            </NavLink>
          </Flex>
        ) : null}
      </Flex>
    </Box>
  );
};
