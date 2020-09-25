import { Alert, AlertIcon } from '@chakra-ui/core';
import React from 'react';
import { Wrapper } from '../Wrapper';

interface NotFoundProps {}

export const NotFound: React.FC<NotFoundProps> = () => {
  return (
    <Wrapper>
      <Alert justifyContent="center" status="warning">
        <AlertIcon />
        Page not found.
      </Alert>
    </Wrapper>
  );
};
