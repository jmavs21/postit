import { Box } from '@chakra-ui/core';
import { History } from 'history';
import React, { useEffect, useState } from 'react';
import { RouteComponentProps } from 'react-router-dom';
import { getPostById, Post } from '../../services/postService';
import { LoadingProgress } from '../LoadingProgress';
import { PostCard } from '../PostCard';
import { Wrapper } from '../Wrapper';

interface MatchParams {
  id: string;
  name: string;
}

interface PostViewProps extends RouteComponentProps<MatchParams> {
  history: History;
}

export const PostView: React.FC<PostViewProps> = ({ ...props }) => {
  const [post, setPost] = useState<Post>({} as Post);
  const [isLoading, setIsLoading] = useState(false);

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

  const changePostsFollows = (toId: number, isFollow: boolean) => {
    if (post.user.id === toId) post.isFollow = isFollow;
    setPost({ ...post, isFollow });
  };

  return (
    <Wrapper>
      {isLoading || !post.user ? (
        <LoadingProgress />
      ) : (
        <Box p={5} shadow="md" borderWidth="1px">
          <PostCard
            p={post}
            changePostsFollows={changePostsFollows}
            isView={true}
          />
        </Box>
      )}
    </Wrapper>
  );
};
