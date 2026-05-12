# Craps

Player vs House casino dice game. Java 21 / Spring Boot 3.3.5 backend, React 18 / TypeScript / Vite frontend. Maven multi-module build.

## Build & run

```bash
# Start both servers (kills existing, streams logs to terminal)
./start.sh

# Or start individually:
cd craps-backend && mvn spring-boot:run   # Backend on :8090
cd craps-frontend && npm run dev           # Frontend on :5183

# Build everything (backend + frontend into single JAR)
./mvnw clean package

# Run tests
./mvnw test
```

- Backend: http://localhost:8090
- Frontend dev: http://localhost:5183 (proxied to backend)
- Ctrl+C stops both when using start.sh

## Project structure

- `craps-backend/` — Spring Boot REST API, game logic, bet resolution engine
- `craps-frontend/` — React SPA, bundled into backend's `static/` by Maven

## API

All game endpoints are under `/api/v1/games`.

- `POST /api/v1/games` — create a new game session (bankroll, odds config)
- `GET /api/v1/games/{id}` — get table state
- `POST /api/v1/games/{id}/bets` — place a bet
- `DELETE /api/v1/games/{id}/bets/{betId}` — remove a bet
- `POST /api/v1/games/{id}/bets/{betId}/toggle` — toggle place bet ON/OFF
- `POST /api/v1/games/{id}/roll` — roll dice (resolves all bets in response)
- `POST /api/v1/games/{id}/cashout` — cash out and end game
- `DELETE /api/v1/games/{id}` — abandon game

## Game rules

Craps is a dice game. The player bets on outcomes of two dice rolls. Two phases:
- **Come-Out Roll**: 7/11 = natural (Pass Line wins), 2/3/12 = craps (Pass Line loses), 4-10 = point established
- **Point Phase**: Roll the point again before 7 to win Pass Line; roll 7 = seven-out

Supported bets: Pass Line, Don't Pass, Come, Don't Come, Odds, Place (4/5/6/8/9/10), Field, Any 7, Any Craps, Yo, Aces, Boxcars, Hardways, C&E, Horn.

## Platform integration

This game integrates with the game platform at `/Users/randyn/projects/git/game-platform/`.
See `GAME-BLUEPRINT.md` in that repo for the full contract.

Key integration points:
- `GET /platform/health` — health check for catalog status
- `PlatformAuthFilter` — verifies platform JWT, sets user attributes on request
- `PlatformScoreReporter` — reports game results to platform on cash out / broke
- Frontend uses `@gitgames/platform-sdk` for token handling

### Design rules

1. Single player vs house — no Player enum or AI strategy needed.
2. Bankroll accounting: debit on bet placement, credit on win.
3. Roll resolves all active bets in a single HTTP response.
4. All platform config has empty defaults so the game works standalone without the platform.
