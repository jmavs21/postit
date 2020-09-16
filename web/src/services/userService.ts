import http from './httpService';
import { RegisterValues } from '../components/pages/Register';
import { header_token } from '../utils/constants';
import auth from './authService';

interface RegisterResponse {
  id: number;
  email: string;
  password: string;
  name: string;
  createdat: string;
  udpatedat: string;
}

interface Response {
  data?: RegisterResponse;
  errors?: RegisterValues;
}

export const register = async (user: RegisterValues) => {
  const response: Response = {};
  try {
    const axiosResponse = await http.post<RegisterResponse>('/users', user);
    response.data = axiosResponse.data;
    const token = axiosResponse.headers[header_token];
    if (token) auth.storeJwt(token);
  } catch (ex) {
    response.errors = ex.response.data;
  }
  return response;
};
