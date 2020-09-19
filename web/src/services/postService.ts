import { PostCreateValues } from '../components/pages/PostCreate';
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

interface Response {
  data?: PostsRes;
  errors?: any;
}

export const getPosts = async (cursor: string) => {
  const response: Response = {};
  try {
    const axiosResponse = await httpService.get<PostsRes>(
      `${postsApi}/?cursor=${cursor}`
    );
    response.data = axiosResponse.data;
  } catch (ex) {
    if (ex.response?.data) response.errors = ex.response.data;
    else console.log('Error on fetching posts: ', ex);
  }
  return response;
};

export const createPost = async (post: PostCreateValues) => {
  const response: { data?: any; errors?: any } = {};
  try {
    const axiosReponse = await httpService.post(postsApi, post);
    response.data = axiosReponse.data;
  } catch (ex) {
    response.errors = ex.response.data;
  }
  return response;
};
