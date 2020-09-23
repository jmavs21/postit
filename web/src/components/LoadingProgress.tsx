import { Flex, CircularProgress } from '@chakra-ui/core';
import React from 'react';

interface LoadingProgressProps {}

export const LoadingProgress: React.FC<LoadingProgressProps> = () => {
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
