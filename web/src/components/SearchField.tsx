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
};

export const SearchField: React.FC<SearchFieldProps> = ({
  size: _,
  ...props
}) => {
  const [field, { error }] = useField(props);
  return (
    <FormControl isInvalid={!!error}>
      <InputGroup size="sm">
        <Input
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
