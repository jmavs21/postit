import { Flex, Heading, IconButton, Text, useColorMode } from '@chakra-ui/core';
import { Form, Formik } from 'formik';
import React, { useContext } from 'react';
import { NavLink } from 'react-router-dom';
import { SEARCH_QUERY } from '../utils/constants';
import { UserContext } from '../utils/UserContext';
import { SearchField } from './SearchField';

export const NavBar: React.FC = () => {
  const { colorMode, toggleColorMode } = useColorMode();
  const { user, setUser } = useContext(UserContext);
  return (
    <Flex
      bg="LightCoral"
      p={1}
      zIndex={1}
      position="sticky"
      top={0}
      justify="center"
      align="center"
    >
      <Flex width={800} align="center">
        <Flex mr={4}>
          <NavLink to="/">
            <Heading size="lg">POSTS</Heading>
          </NavLink>
          <NavLink to="/posts/new">
            <IconButton
              icon="add"
              size="xs"
              ml={4}
              isRound={true}
              aria-label="Create post"
            />
          </NavLink>
        </Flex>
        <Flex flex={1}>
          <Formik
            initialValues={{ searchText: '' }}
            onSubmit={async (values) => {
              if (values.searchText.length !== 0) {
                window.location.href = `/posts?${SEARCH_QUERY}=${values.searchText}`;
              }
            }}
          >
            {() => (
              <Form style={{ width: '100%' }}>
                <SearchField
                  name="searchText"
                  placeholder="Search"
                  colorMode={colorMode}
                />
              </Form>
            )}
          </Formik>
        </Flex>
        <Flex ml={4} align="center">
          <IconButton
            icon={colorMode === 'light' ? 'sun' : 'moon'}
            size="xs"
            mr={2}
            isRound={true}
            aria-label={`change to ${colorMode} mode`}
            onClick={toggleColorMode}
          />
          {!user && (
            <>
              <Text mr={2}>
                <NavLink to="/login">Login</NavLink>
              </Text>
              <Text>
                <NavLink to="/register">Register</NavLink>
              </Text>
            </>
          )}
          {user && (
            <>
              <Text as="i" mr={2}>
                <NavLink to="/profile">{user.name}</NavLink>
              </Text>

              <NavLink to="/logout">
                <IconButton
                  icon="close"
                  size="xs"
                  ml={2}
                  isRound={true}
                  aria-label="Logout"
                  onClick={() => {
                    if (setUser != null) setUser(null);
                  }}
                />
              </NavLink>
            </>
          )}
        </Flex>
      </Flex>
    </Flex>
  );
};
