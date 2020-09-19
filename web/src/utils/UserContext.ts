import { createContext } from 'react';
import { User } from '../services/authService';

export const UserContext = createContext({
  user: null as User | null,
  setUser: null as React.Dispatch<React.SetStateAction<User | null>> | null,
});
