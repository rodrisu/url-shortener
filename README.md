
# URL shortener

Shorten URLs.





## Table of contents
* [Features](#features)
* [Tech Stack](#tech-stack)
* [API Reference](#api-reference)
* [Run Locally](#run-locally)
* [Running Tests](#running-tests)
* [Architecture](#architecture)
* [Feedback](#feedback)
* [Badges](#badges)

## Features

- Shorten URL
- Get long URL from the short URL
- Get statistics from the short URL
- Delete short URL
- Get redirected to the original URL from the short one



## Tech Stack

**Server:** Java, Spring Boot, Maven

**Database:** AWS DynamoDB

**Cache:** Redis


## API Reference

#### Shorten url

```http
  POST /urls
```

| Body | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `url` | `string` | **Required**. Complete URL to shorten |

---

#### Get long URL form short URL key

```http
  GET /urls/${shortUrlKey}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `shortUrlKey`      | `string` | **Required**. Key of the short URL to get |

---

#### Get statistics of a short URL

```http
  GET /urls/{shortUrlKey}/statistics
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `shortUrlKey`      | `string` | **Required**. Key of the short URL to get statistics from |

---

#### Get redirected to the original URL

```http
  GET /{shortUrlKey}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `shortUrlKey`      | `string` | **Required**. Key of the short URL to get redirected |

---

#### Delete short URL

```http
  DELETE /urls/{shortUrlKey}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `shortUrlKey`      | `string` | **Required**. Key of the short URL to be deleted |

---
## Run Locally

### Requirements to run locally

- Java 11 installed
- Docker installed
- Maven
- AWS CLI installed and configured

### Steps

1. Clone the project

```bash
  git clone https://github.com/rodrisu/url-shortener.git
```

2. Go to the project directory

```bash
  cd url-shortener
```

4. Install dependencies

```bash
  mvn install
```

5. Start Docker


6. Run Docker Compose to set up DynamoDB and Redis in Docker

```bash
  docker compose up -d
```

7. Create the DynamoDB table running the following bash script. It is mandatory to pass the **table_name**, **endpoint_url** and **region**.

```bash
  ./create_dynamodb_table.sh UrlsTable http://localhost:8000 us-east-1
```

8. Start the server

```bash
  mvn spring-boot:run
```


## Testing

### Running tests

We use `JUnit` for testing our code. To run tests, run the following command

```bash
  mvn test
```


## Architecture

### Ideally would be

![Ideal Architecture](https://i.ibb.co/N2vQJm4/URLShortener-drawio.png)


## Feedback

If you have any feedback, please reach out to me at serron.rodrigo@gmail.com


## Badges

![GitHub last commit](https://img.shields.io/github/last-commit/rodrisu/url-shortener)

![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/rodrisu/url-shortener)

![GitHub watchers](https://img.shields.io/github/watchers/rodrisu/url-shortener)

![GitHub all releases](https://img.shields.io/github/downloads/rodrisu/url-shortener/total)


