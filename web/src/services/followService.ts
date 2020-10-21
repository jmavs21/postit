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
