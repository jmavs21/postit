import jwtDecode from 'jwt-decode';
import { LoginValues } from '../components/pages/Login';
import { TOKEN_KEY } from '../utils/constants';
import { addJwtOnHeaders } from './httpService';
import httpService from './httpService';

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
    const { data } = await httpService.post<string>('/auth', user);
    if (data) {
      setJwt(data);
      response.data = getUserFromJwt() as User;
    }
  } catch (ex) {
    response.errors = ex.response.data;
  }
  return response;
};

const getJwt = () => {
  return localStorage.getItem(TOKEN_KEY) ?? '';
};

export const setJwt = (jwt: string) => {
  localStorage.setItem(TOKEN_KEY, jwt);
};

export const removeJwt = () => {
  localStorage.removeItem(TOKEN_KEY);
};

export const getUserFromJwt = () => {
  try {
    return jwtDecode(getJwt()) as User;
  } catch (ex) {
    return null;
  }
};

addJwtOnHeaders(getJwt());
