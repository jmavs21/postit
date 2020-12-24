import { RegisterValues } from '../components/pages/Register';
import { HEADER_TOKEN } from '../utils/constants';
import httpService from './httpService';
import { setJwt } from './authService';
import { ProfileValues } from '../components/pages/Profile';

const usersApi = '/users';

interface UserResponse {
  id: number;
  email: string;
  name: string;
  createdate: string;
  updatedate: string;
}

interface Response {
  data?: UserResponse;
  errors?: RegisterValues;
}

export const registerUser = async (user: RegisterValues) => {
  const response: Response = {};
  try {
    const { data, headers } = await httpService.post<UserResponse>(
      usersApi,
      user
    );
    response.data = data;
    setJwt(headers[HEADER_TOKEN]);
  } catch (ex) {
    response.errors = ex.response.data;
  }
  return response;
};

export const updateUser = async (user: ProfileValues, userId: number) => {
  const response: Response = {};
  try {
    const { data, headers } = await httpService.put<UserResponse>(
      `${usersApi}/${userId}`,
      user
    );
    response.data = data;
    setJwt(headers[HEADER_TOKEN]);
  } catch (ex) {
    response.errors = ex.response.data;
  }
  return response;
};
