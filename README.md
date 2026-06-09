<div align="center">
  <img src="/public/readme_logo.png" alt="MockBoard.dev Logo" width="150"/>
  <h1>MockBoard</h1>
</div>

<div align="center">
  <strong>Local-first HTTP API mocks for development and testing</strong>
  <br>
  Java 21, Javalin, MapDB, Svelte
</div>
<br>

<div align="center">
  <img src="https://img.shields.io/badge/Java-21-orange" alt="Java 21">
  <img src="https://img.shields.io/badge/Javalin-7.2-blue" alt="Javalin">
  <img src="https://img.shields.io/badge/Svelte-5-ff3e00" alt="Svelte">
  <img src="https://img.shields.io/badge/Status-Beta-red" alt="Status">
  <img src="https://img.shields.io/badge/License-MIT-blue" alt="License">
</div>

## What It Is

MockBoard is a small local app for mocking HTTP APIs.

Run it, create a board, add mock rules, and call the generated `/m/{boardId}` URL from your app, tests, or scripts. Boards and rules are stored locally in MapDB, so there is no external database and no team account layer.

<p align="center">
  <img src="/public/screenshot_1.png" alt="screenshot"/>
</p>

## What It Does

- Creates local mock boards with optional display names.
- Serves mock endpoints from `/m/{boardId}`.
- Supports `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `HEAD`, and `OPTIONS`.
- Matches paths exactly or with single-segment `*` wildcards.
- Returns custom status codes, headers, JSON bodies, and response delays.
- Generates fake response values with `{{template.keys}}`.
- Shows incoming requests live in the UI through SSE.
- Keeps request logs per board while the app is running.

## Usage

Start the app, open the UI, create a board, and add a mock rule.

Example mock rule:

- Method: `GET`
- Path: `/api/users/*`
- Status: `200`
- Body:

```json
{
  "id": "{{system.uuid}}",
  "name": "{{user.fullName}}",
  "email": "{{user.email}}"
}
```

Call it:

```shell
curl http://localhost:8000/m/{boardId}/api/users/123
```

Each call is logged in the board dashboard. If the rule body has templates, they are resolved on every request.

## Dynamic Response Templates

Mock response bodies must be valid JSON. Template values can be placed inside JSON strings:

```json
{
  "id": "{{system.uuid}}",
  "city": "{{address.city}}",
  "bio": "{{content.sentence}}"
}
```

Supported template keys:

| Group | Keys |
| --- | --- |
| `user` | `fullName`, `firstName`, `lastName`, `email`, `username`, `phoneNumber`, `avatar` |
| `address` | `full`, `city`, `street`, `zipCode`, `country`, `countryCode` |
| `content` | `char`, `word`, `sentence`, `paragraph` |
| `system` | `int`, `long`, `double`, `bool`, `uuid` |

Full key examples: `{{user.email}}`, `{{address.city}}`, `{{content.paragraph}}`, `{{system.uuid}}`.

Unknown templates render as `[unknown: key]`. Generated values are escaped for JSON strings.

## Running Locally

Prerequisites:

- Java 21+
- Node 20+ if you are rebuilding the frontend
- Docker if you prefer Docker Compose

Build the frontend:

```shell
dev/scripts/build_frontend.sh
```

Build the Java jar:

```shell
dev/scripts/rebuild_java.sh
```

Build and run the jar:

```shell
dev/scripts/run_jar.sh
```

Manual run after building:

```shell
java -jar target/mockboard-0.4-beta.jar
```

Default URL:

```text
http://localhost:8000
```

Docker:

```shell
docker compose up -d --build
```

Docker URL:

```text
http://localhost:8888
```

Useful environment variables:

| Variable | Default | Purpose |
| --- | --- | --- |
| `PORT` | `8000` | HTTP server port |
| `MBD_STORE_PATH` | `data/mockboard.db` | Local MapDB file path |
| `MBD_MAX_MOCK_RULES` | `1000` | Max mock rules per board |
| `MBD_MAX_WEBHOOKS` | `100` | Max stored request log entries per board |
| `MBD_VALIDATION_MOCK_MAX_ALLOWED_DELAY` | `10000` | Max response delay in milliseconds |
| `MBD_VALIDATION_MOCK_MAX_BODY_LENGTH` | `100000` | Max JSON body length |
| `MBD_VALIDATION_MOCK_MAX_PATH_LENGTH` | `1000` | Max mock path length |

The frontend source lives in `src/frontend`. The production build is served by Javalin from `src/main/resources/static`.

## Contributing

No contributions are expected right now.

This is a personal local tool, not a product roadmap. Issues or notes are still useful if something is broken, but pull requests may sit untouched or be closed if they do not match how the tool is used.
