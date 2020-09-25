import React, { InputHTMLAttributes } from 'react';
import {
  FormControl,
  Input,
  FormErrorMessage,
  Icon,
  InputGroup,
  InputLeftElement,
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
        <InputLeftElement children={<Icon name="search" color="gray.300" />} />
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
