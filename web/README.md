# Web

[Posts](https://github.com/jmavs21/posts) frontend.

## Technologies used

- TypeScript
- React
- Chakra UI
- React Router DOM
- Formik
- Axios
- JWT decode
- React Waypoint
- Server-Sent Events API
- Cypress
- Docker

## Usage

### Build

```sh
npm i
```

### Run

```sh
npm start
```

### Run E2E tests

- Run first the backend API project by following the instructions in [here](https://github.com/jmavs21/posts/tree/master/api)

```sh
   npm run cypress:open
```

- On Cypress UI click button **Run integration specs**

## Docker usage

### Build

```sh
docker build -t web:latest .
```

### Run

```sh
docker run \
      -it \
      --rm \
      -v {$PWD}:/app \
      -v /app/node_modules \
      -p 3000:3000 \
      -e CHOKIDAR_USEPOLLING=true \
      web:latest
```
