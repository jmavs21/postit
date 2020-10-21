import React, { InputHTMLAttributes } from 'react';
import {
  FormControl,
  Input,
  FormErrorMessage,
  InputGroup,
} from '@chakra-ui/core';
import { useField } from 'formik';

type SearchFieldProps = InputHTMLAttributes<HTMLInputElement> & {
  name: string;
  colorMode: 'light' | 'dark';
};

export const SearchField: React.FC<SearchFieldProps> = ({
  colorMode,
  size: _,
  ...props
}) => {
  const [field, { error }] = useField(props);
  return (
    <FormControl isInvalid={!!error}>
      <InputGroup size="sm">
        <Input
          bg={colorMode === 'light' ? '' : '#2c303b'}
          {...field}
          {...props}
          id={field.name}
          placeholder={props.placeholder}
        />
      </InputGroup>
      {error ? <FormErrorMessage>{error}</FormErrorMessage> : null}
    </FormControl>
  );
};
