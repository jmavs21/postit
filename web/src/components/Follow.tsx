import { Button } from '@chakra-ui/core';
import React, { useState } from 'react';
import { User } from '../services/authService';
import { createFollow } from '../services/followService';
import { PostSnippet } from '../services/postService';

interface FollowProps {
  changePostsFollows: (toId: number, isFollow: boolean) => void;
  post: PostSnippet;
  from: User | null;
  toId: number;
}

export const Follow: React.FC<FollowProps> = ({
  changePostsFollows,
  post,
  from,
  toId,
}) => {
  const [isLoading, setIsLoading] = useState(false);

  const callFollow = async () => {
    if (from == null) return;
    setIsLoading(true);
    const { data } = await createFollow({ fromId: from.id, toId });
    if (data != null) {
      if (data === 'Followed') changePostsFollows(toId, true);
      else if (data === 'Unfollowed') changePostsFollows(toId, false);
    }
    setIsLoading(false);
  };

  return from?.id === toId ? null : (
    <Button
      onClick={callFollow}
      mr={4}
      size="xs"
      variantColor="blue"
      variant={post.isFollow ? 'solid' : 'outline'}
      isDisabled={!from}
      isLoading={isLoading}
    >
      {post.isFollow ? 'Following' : 'Follow'}
    </Button>
  );
};
