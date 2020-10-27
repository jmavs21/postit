import { User } from './authService';
import httpService from './httpService';

const followsApi = '/follows';

interface FollowCreateValues {
  fromId: number;
  toId: number;
}

export const createFollow = async (follow: FollowCreateValues) => {
  const response: { data?: string; errors?: any } = {};
  try {
    const { data } = await httpService.post<string>(followsApi, {
      toId: follow.toId,
    });
    response.data = data;
  } catch (ex) {
    response.errors = ex.response.data;
  }
  return response;
};

export const getFollows = async (fromId: number) => {
  const response: {
    data?: User[];
    errors?: any;
  } = {};
  try {
    const { data } = await httpService.get(`${followsApi}/${fromId}`);
    response.data = data;
  } catch (ex) {
    response.errors = ex.response.data;
  }
  return response;
};

export const getFollowers = async (toId: number) => {
  const response: {
    data?: User[];
    errors?: any;
  } = {};
  try {
    const { data } = await httpService.get(`${followsApi}/to/${toId}`);
    response.data = data;
  } catch (ex) {
    response.errors = ex.response.data;
  }
  return response;
};
