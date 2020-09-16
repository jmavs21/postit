import { createContext } from 'react';
import { User } from '../services/authService';

export const UserContext = createContext<User | null>(null);
