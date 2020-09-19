import httpService from './httpService';

const votesApi = '/votes';

interface VoteCreateValues {
  postId: number;
  isUpVote: boolean;
}

export const createVote = async (vote: VoteCreateValues) => {
  const response: { data?: any; errors?: any } = {};
  try {
    const axiosReponse = await httpService.post(votesApi, vote);
    response.data = axiosReponse.data;
  } catch (ex) {
    response.errors = ex.response.data;
  }
  return response;
};
