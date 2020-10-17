import { Flex, CircularProgress } from '@chakra-ui/core';
import React from 'react';

export const LoadingProgress: React.FC = () => {
  return (
    <Flex>
      <CircularProgress
        m="auto"
        isIndeterminate
        color="green"
      ></CircularProgress>
    </Flex>
  );
};
