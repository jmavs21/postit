import { Flex, IconButton, Text } from '@chakra-ui/core';
import React, { useState } from 'react';
import { PostSnippet } from '../services/postService';
import { createVote } from '../services/voteService';
import { sleep } from '../utils/sleep';

interface VotesProps {
  post: PostSnippet;
  isUser: boolean;
  updatePostVote: (postId: number, points: number, voteValue: number) => void;
}

type loadingStates = 'no' | 'up' | 'down';

export const Votes: React.FC<VotesProps> = ({
  post,
  isUser,
  updatePostVote,
}) => {
  const [isVoteLoading, setIsVoteLoading] = useState<loadingStates>('no');
  const callVote = async (postId: number, isUpVote: boolean, load: string) => {
    setIsVoteLoading(load as loadingStates);
    await sleep(1000);
    const response = await createVote({ postId, isUpVote });
    if (response.data != null) {
      updatePostVote(post.id, response.data, isUpVote ? 1 : -1);
      // post.points = response.data;
      // post.voteValue = isUpVote ? 1 : -1;
    }
    setIsVoteLoading('no');
  };
  return (
    <Flex direction="column" align="center" justify="center" mr={4}>
      <IconButton
        isLoading={isVoteLoading === 'up'}
        aria-label="up vote"
        onClick={() => {
          callVote(post.id, true, 'up');
        }}
        icon="arrow-up"
        isDisabled={post.voteValue === 1 || !isUser}
      />
      <Text padding={1}>{post.points}</Text>
      <IconButton
        isLoading={isVoteLoading === 'down'}
        size="md"
        aria-label="down vote"
        onClick={() => {
          callVote(post.id, false, 'down');
        }}
        icon="arrow-down"
        isDisabled={post.voteValue === -1 || !isUser}
      />
    </Flex>
  );
};
