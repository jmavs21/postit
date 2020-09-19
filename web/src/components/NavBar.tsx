import {
  Text,
  Flex,
  useColorMode,
  IconButton,
  Button,
  Heading,
} from '@chakra-ui/core';
import React, { useContext } from 'react';
import { NavLink } from 'react-router-dom';
import { UserContext } from '../utils/UserContext';

interface NavBarProps {}

export const NavBar: React.FC<NavBarProps> = () => {
  const { colorMode, toggleColorMode } = useColorMode();
  const { user, setUser } = useContext(UserContext);
  return (
    <Flex
      bg="LightCoral"
      zIndex={1}
      position="sticky"
      top={0}
      alignItems="center"
    >
      <Flex ml={8}>
        <NavLink to="/">
          <Heading>POST IT</Heading>
        </NavLink>
      </Flex>
      <Flex ml={8}>
        <Button size="sm">
          <NavLink to="/posts/new">Create Post</NavLink>
        </Button>
      </Flex>
      <Flex ml="auto">
        <IconButton
          size="xs"
          mr={8}
          aria-label={`change to ${colorMode} mode`}
          onClick={toggleColorMode}
          icon={colorMode === 'light' ? 'sun' : 'moon'}
        />
        {!user && (
          <>
            <Text mr={4}>
              <NavLink to="/login">Login</NavLink>
            </Text>
            <Text mr={8}>
              <NavLink to="/register">Register</NavLink>
            </Text>
          </>
        )}
        {user && (
          <>
            <Text mr={4}>
              <NavLink to="/profile">{user.name}</NavLink>
            </Text>
            <Text mr={8}>
              <NavLink
                to="/logout"
                onClick={() => {
                  if (setUser != null) setUser(null);
                }}
              >
                Logout
              </NavLink>
            </Text>
          </>
        )}
      </Flex>
    </Flex>
  );
};
