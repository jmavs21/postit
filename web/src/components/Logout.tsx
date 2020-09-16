import React, { useEffect } from 'react';
import { logout } from '../services/authService';

interface LogoutProps {}

export const Logout: React.FC<LogoutProps> = () => {
  useEffect(() => {
    logout();
    window.location.href = '/';
  }, []);
  return null;
};
