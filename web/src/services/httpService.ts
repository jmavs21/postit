import axios from 'axios';
import { HEADER_TOKEN, TOKEN_KEY } from '../utils/constants';

axios.defaults.baseURL = process.env.REACT_APP_API_URL;

axios.interceptors.response.use(undefined, (error) => {
  const expectedError =
    error.response &&
    error.response.status >= 400 &&
    error.response.status < 500;
  if (!expectedError) console.log('An unexpected error occurred: ', error);
  if (error.response.status === 401) {
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = '/login';
  }
  return Promise.reject(error);
});

function setJwtOnCommonHeaders(jwt: string) {
  axios.defaults.headers.common[HEADER_TOKEN] = jwt;
}

export default {
  get: axios.get,
  post: axios.post,
  put: axios.put,
  delete: axios.delete,
  setJwtOnCommonHeaders,
};
