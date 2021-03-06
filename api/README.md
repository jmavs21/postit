# api

[Posts](https://github.com/jmavs21/posts) backend.

## Technologies used

- Kotlin
- Spring Boot
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Spring Validation
- H2 Database
- Embedded Redis
- Java JWT
- Server-Sent Events
- Swagger
- Docker

## Usage

### Build

```sh
./gradlew build
```

### Run

```sh
./gradlew bootRun
```

### Run integration tests

```sh
./gradlew test
```

### API documentation

Via [Swagger](http://localhost:4000/swagger-ui.html#/)

## Docker usage

### Build

```sh
docker build -t api:latest .
```

### Run

```sh
docker run -p 4000:4000 api:latest
```
