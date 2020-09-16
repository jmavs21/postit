import jwtDecode from 'jwt-decode';
import { LoginValues } from '../components/pages/Login';
import http from './httpService';

const tokenKey = 'token';

http.setJwt(getJwt());

interface Response {
  data?: string;
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
    const axiosResponse = await http.post<string>('/auth', user);
    response.data = axiosResponse.data;
    if (response.data) storeJwt(response.data);
  } catch (ex) {
    response.errors = ex.response.data;
  }
  console.log('response=', response);
  return response;
};

export const storeJwt = (jwt: string) => {
  localStorage.setItem(tokenKey, jwt);
};

export const logout = () => {
  localStorage.removeItem(tokenKey);
};

export const getLocalUser = () => {
  try {
    const jwt = getJwt();
    return jwtDecode(jwt) as User;
  } catch (ex) {
    console.log('error decoding jwt.');
    return null;
  }
};

function getJwt() {
  return localStorage.getItem(tokenKey) ?? '';
}

export default {
  login,
  storeJwt,
  logout,
  getLocalUser,
};
