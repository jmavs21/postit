import { Box, Text, Flex, useColorMode, IconButton } from '@chakra-ui/core';
import React, { useContext } from 'react';
import { NavLink } from 'react-router-dom';
import { UserContext } from '../utils/UserContext';

interface NavBarProps {}

export const NavBar: React.FC<NavBarProps> = () => {
  const { colorMode, toggleColorMode } = useColorMode();
  const user = useContext(UserContext);
  console.log('navbar user = ', user);
  return (
    <Flex p={4}>
      <Box ml="auto">
        <Flex>
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
              <Text>
                <NavLink to="/register">Register</NavLink>
              </Text>
            </>
          )}
          {user && (
            <>
              <Text mr={4}>
                <NavLink to="/">{user.name}</NavLink>
              </Text>
              <Text>
                <NavLink to="/logout">Logout</NavLink>
              </Text>
            </>
          )}
        </Flex>
      </Box>
    </Flex>
  );
};
