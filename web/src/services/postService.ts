import _ from 'lodash';
import { CreatePostValues } from '../components/pages/CreatePost';
import { User } from './authService';
import httpService from './httpService';

const postsApi = '/posts';

export interface PostsRes {
  posts: Post[];
  hasMore: boolean;
}

export interface Post {
  id: number;
  title: string;
  text: string;
  voteValue: number;
  points: number;
  user: User;
  createdat: string;
  udpatedat: string;
  isFollow: boolean;
}

export const getPosts = async (cursor: string, search: string) => {
  const response: {
    data?: PostsRes;
    errors?: any;
  } = {};
  try {
    const { data } = await httpService.get<PostsRes>(
      `${postsApi}/?cursor=${cursor}&search=${search}`
    );
    data.posts = _.orderBy(data.posts, [(p) => p.isFollow], ['desc']);
    response.data = data;
  } catch (ex) {
    if (ex.response?.data) response.errors = ex.response.data;
    else console.log('Error on fetching posts: ', ex);
  }
  return response;
};

export const createPost = async (post: CreatePostValues) => {
  const response: { data?: any; errors?: any } = {};
  try {
    const { data } = await httpService.post(postsApi, post);
    response.data = data;
  } catch (ex) {
    response.errors = ex.response.data;
  }
  return response;
};

export const getPostById = async (postId: string) => {
  const response: {
    data?: Post;
    errors?: any;
  } = {};
  try {
    const { data } = await httpService.get<Post>(`${postsApi}/${postId}`);
    response.data = data;
  } catch (ex) {
    if (ex.response?.data) response.errors = ex.response.data;
    else console.log('Error on fetching posts: ', ex);
  }
  return response;
};

export const deletePostById = async (postId: number) => {
  try {
    await httpService.delete(`${postsApi}/${postId}`);
  } catch (ex) {
    console.log('Error on fetching posts: ', ex);
    return false;
  }
  return true;
};

export const updatePost = async (postId: number, post: CreatePostValues) => {
  const response: { data?: any; errors?: any } = {};
  try {
    const { data } = await httpService.put<Post>(`${postsApi}/${postId}`, post);
    response.data = data;
  } catch (ex) {
    response.errors = ex.response.data;
  }
  return response;
};
