# PicIt API

API that hadles all the back-end for the PicIt social network

## Environment Variables

- Copy the `src/main/resources/application-env.yml.example` to `src/main/resources/application-env.yml`


- Update the `src/main/resources/application-env.yml` with the following variables:

- - `JWT_SECRET_KEY`: The secret key for the JWT token (256 bits key that can be generated with the command `openssl rand -base64 32`, or use websites)
- - `SPRING_DATA_MONGODB_URI`: The MongoDB URI

## Swagger documentation

- The API documentation is available at [Swagger Doc](http://localhost:8081/swagger-ui/index.html)
