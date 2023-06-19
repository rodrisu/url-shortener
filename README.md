
# URL shortener

Shorten URLs.

![GitHub last commit](https://img.shields.io/github/last-commit/rodrisu/url-shortener)

![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/rodrisu/url-shortener)




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

**Some other steps that will be written soon**

Then,
Clone the project

```bash
  git clone https://github.com/rodrisu/url-shortener.git
```

Go to the project directory

```bash
  cd url-shortener
```

Install dependencies

```bash
  mvn install
```

Start the server

```bash
  mvn spring-boot:run
```


## Testing

### Running tests

We use `JUnit` for testing our code. To run tests, run the following command

```bash
  mvn test
```


## Feedback

If you have any feedback, please reach out to me at serron.rodrigo@gmail.com

