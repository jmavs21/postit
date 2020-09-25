import { CreatePostValues } from '../components/pages/CreatePost';
import { User } from './authService';
import httpService from './httpService';

const postsApi = '/posts';

export interface PostsRes {
  posts: PostSnippet[];
  hasMore: boolean;
}

export interface PostSnippet {
  id: number;
  title: string;
  textSnippet: string;
  voteValue: number;
  points: number;
  user: User;
  createdat: string;
  udpatedat: string;
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
}

export const getPosts = async (cursor: string, search: string) => {
  const response: {
    data?: PostsRes;
    errors?: any;
  } = {};
  try {
    const axiosResponse = await httpService.get<PostsRes>(
      `${postsApi}/?cursor=${cursor}&search=${search}`
    );
    response.data = axiosResponse.data;
  } catch (ex) {
    if (ex.response?.data) response.errors = ex.response.data;
    else console.log('Error on fetching posts: ', ex);
  }
  return response;
};

export const createPost = async (post: CreatePostValues) => {
  const response: { data?: any; errors?: any } = {};
  try {
    const axiosReponse = await httpService.post(postsApi, post);
    response.data = axiosReponse.data;
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
    const axiosResponse = await httpService.get<Post>(`${postsApi}/${postId}`);
    response.data = axiosResponse.data;
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
    const axiosReponse = await httpService.put<Post>(
      `${postsApi}/${postId}`,
      post
    );
    response.data = axiosReponse.data;
  } catch (ex) {
    response.errors = ex.response.data;
  }
  return response;
};
