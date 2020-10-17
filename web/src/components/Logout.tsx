import React, { useEffect } from 'react';
import { removeJwt } from '../services/authService';

export const Logout: React.FC = () => {
  useEffect(() => {
    removeJwt();
    window.location.href = '/';
  }, []);
  return null;
};
