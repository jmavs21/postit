import axios from 'axios';
import { header_token } from '../utils/constants';

axios.defaults.baseURL = process.env.REACT_APP_API_URL;

axios.interceptors.response.use(undefined, (error) => {
  const expectedError =
    error.response &&
    error.response.status >= 400 &&
    error.response.status < 500;
  if (!expectedError) console.log('An unexpected error occurred: ', error);
  return Promise.reject(error);
});

function setJwt(jwt: string) {
  axios.defaults.headers.common[header_token] = jwt;
}

export default {
  get: axios.get,
  post: axios.post,
  put: axios.put,
  delete: axios.delete,
  setJwt,
};
