import React, { useEffect } from 'react';
import { removeJwt } from '../services/authService';

interface LogoutProps {}

export const Logout: React.FC<LogoutProps> = () => {
  useEffect(() => {
    removeJwt();
    window.location.href = '/';
  }, []);
  return null;
};
