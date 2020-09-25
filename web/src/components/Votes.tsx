import { Flex, IconButton, Text } from '@chakra-ui/core';
import React, { useState } from 'react';
import { PostSnippet } from '../services/postService';
import { createVote } from '../services/voteService';

interface VotesProps {
  post: PostSnippet;
  isUser: boolean;
}

type loadingStates = 'no' | 'up' | 'down';

export const Votes: React.FC<VotesProps> = ({ post, isUser }) => {
  const [isVoteLoading, setIsVoteLoading] = useState<loadingStates>('no');
  const callVote = async (postId: number, isUpVote: boolean, load: string) => {
    setIsVoteLoading(load as loadingStates);
    const response = await createVote({ postId, isUpVote });
    if (response.data != null) {
      post.points = response.data;
      post.voteValue = isUpVote ? 1 : -1;
    }
    setIsVoteLoading('no');
  };
  return (
    <Flex direction="column" align="center" justify="center" mr={4}>
      <IconButton
        variantColor={post.voteValue === 1 ? 'green' : undefined}
        isLoading={isVoteLoading === 'up'}
        aria-label="up vote"
        onClick={() => {
          if (post.voteValue === 1) return;
          callVote(post.id, true, 'up');
        }}
        icon="arrow-up"
        isDisabled={!isUser}
      />
      <Text padding={1}>{post.points}</Text>
      <IconButton
        variantColor={post.voteValue === -1 ? 'red' : undefined}
        isLoading={isVoteLoading === 'down'}
        size="md"
        aria-label="down vote"
        onClick={() => {
          if (post.voteValue === -1) return;
          callVote(post.id, false, 'down');
        }}
        icon="arrow-down"
        isDisabled={!isUser}
      />
    </Flex>
  );
};
