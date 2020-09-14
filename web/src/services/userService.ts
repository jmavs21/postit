import http from './httpService';

type User = {
  username: string;
  password: string;
};

export function register(user: User) {
  return http.post('/users', {
    username: user.username,
    password: user.password,
  });
}
