import {
  Box,
  Button,
  Divider,
  Heading,
  Link,
  List,
  ListItem,
} from '@chakra-ui/core';
import { Formik, Form } from 'formik';
import React, { useContext, useEffect, useState } from 'react';
import { NavLink } from 'react-router-dom';
import { User } from '../../services/authService';
import { getFollowers, getFollows } from '../../services/followService';
import { updateUser } from '../../services/userService';
import { UserContext } from '../../utils/UserContext';
import { InputField } from '../InputField';
import { LoadingProgress } from '../LoadingProgress';
import { Wrapper } from '../Wrapper';

interface ProfileProps {
  location: any;
}

export interface ProfileValues {
  name: string;
}

export const Profile: React.FC<ProfileProps> = ({ location }) => {
  const { user, setUser } = useContext(UserContext);

  const [isLoading, setIsLoading] = useState(false);
  const [follows, setFollows] = useState<User[]>([]);
  const [followers, setFollowers] = useState<User[]>([]);

  useEffect(() => {
    if (!user) return;
    (async () => {
      setIsLoading(true);
      let { data } = await getFollows(user.id);
      if (data) setFollows(data);
      let { data: data2 } = await getFollowers(user.id);
      if (data2) setFollowers(data2);
      setIsLoading(false);
    })();
  }, [user]);

  if (!user) return null;

  return (
    <Wrapper variant="small">
      <Formik
        initialValues={{ name: user.name } as ProfileValues}
        onSubmit={async (values, { setErrors }) => {
          const { data, errors } = await updateUser(values, user.id);
          if (errors) {
            setErrors(errors);
          } else {
            if (data && setUser) setUser(data);
            window.location.href = location.state
              ? location.state.from.pathname
              : '/';
          }
        }}
      >
        {({ isSubmitting }) => (
          <Form>
            <Box>
              <InputField name="name" placeholder="name" label="Name" />
            </Box>
            <Button
              mt={4}
              type="submit"
              isLoading={isSubmitting}
              loadingText="Updating"
            >
              Update
            </Button>
          </Form>
        )}
      </Formik>
      <Divider m={4} />
      {isLoading ? (
        <LoadingProgress />
      ) : (
        <>
          <Heading size="lg">Following</Heading>
          <List styleType="disc" pl={4}>
            {follows.map((u) => (
              <ListItem key={u.id} p={1}>
                <Link as="i">
                  <NavLink to={'/posts?search=' + u.name}>{u.name}</NavLink>
                </Link>
              </ListItem>
            ))}
          </List>
          <Divider m={4} />
          <Heading size="lg">Followers</Heading>
          <List styleType="disc" pl={4}>
            {followers.map((u) => (
              <ListItem key={'to' + u.id} p={1}>
                <Link as="i">
                  <NavLink to={'/posts?search=' + u.name}>{u.name}</NavLink>
                </Link>
              </ListItem>
            ))}
          </List>
        </>
      )}
    </Wrapper>
  );
};
