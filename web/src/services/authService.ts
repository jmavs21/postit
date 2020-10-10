import jwtDecode from 'jwt-decode';
import { LoginValues } from '../components/pages/Login';
import { TOKEN_KEY } from '../utils/constants';
import httpService from './httpService';

httpService.setJwtOnCommonHeaders(getJwt());

interface Response {
  data?: User;
  errors?: LoginValues;
}

export interface User {
  id: number;
  email: string;
  name: string;
}

export const login = async (user: LoginValues) => {
  const response: Response = {};
  try {
    const axiosResponse = await httpService.post<string>('/auth', user);
    if (axiosResponse.data) {
      storeJwt(axiosResponse.data);
      response.data = jwtDecode(axiosResponse.data) as User;
    }
  } catch (ex) {
    response.errors = ex.response.data;
  }
  return response;
};

export const storeJwt = (jwt: string) => {
  localStorage.setItem(TOKEN_KEY, jwt);
};

export const removeJwt = () => {
  localStorage.removeItem(TOKEN_KEY);
};

export const getUserFromJwt = () => {
  try {
    const jwt = getJwt();
    return jwtDecode(jwt) as User;
  } catch (ex) {
    return null;
  }
};

function getJwt() {
  return localStorage.getItem(TOKEN_KEY) ?? '';
}

export default {
  login,
  removeJwt,
  storeJwt,
  getUserFromJwt,
};
