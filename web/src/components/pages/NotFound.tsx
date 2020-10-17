import { Alert, AlertIcon } from '@chakra-ui/core';
import React from 'react';
import { Wrapper } from '../Wrapper';

export const NotFound: React.FC = () => {
  return (
    <Wrapper>
      <Alert justifyContent="center" status="warning">
        <AlertIcon />
        Page not found.
      </Alert>
    </Wrapper>
  );
};
