import httpService from './httpService';

const votesApi = '/votes';

interface VoteCreateValues {
  postId: number;
  isUpVote: boolean;
}

export const createVote = async (vote: VoteCreateValues) => {
  const response: { data?: any; errors?: any } = {};
  try {
    const { data } = await httpService.post(votesApi, vote);
    response.data = data;
  } catch (ex) {
    response.errors = ex.response.data;
  }
  return response;
};
