# Dummy Users — Fullstack Application

This repository contains a Spring Boot backend and a React + Vite frontend that together provide a searchable UI for a sample users dataset.

Contents
- `src/` — backend Spring Boot application
- `frontend/` — React + Vite frontend (TypeScript + Tailwind)
- `src/main/resources/users-payload.json` — sample user data used for seeding/fallback
- `indexes/` — Lucene indexes used by Hibernate Search (if present)

Prerequisites
- Java 17+ and Maven
- Node 18+ and npm
- Git (optional)

Backend (Spring Boot)
1. Build
   - From the project root run:
     mvn clean package

2. Run
   - Run the packaged JAR:
     java -jar target/dummy-0.0.1-SNAPSHOT.jar
   - Or run from Maven for development:
     ./mvnw spring-boot:run    (Windows: mvnw.cmd)

3. Configuration
   - Properties in `src/main/resources/application.yml` or `application.properties`.
   - Important properties:
     - `app.external.users-url` — external API used to fetch users (default in resources)
     - `app.external.fetch-on-startup` — set to `false` to disable automatic fetch on startup
   - CORS: the users controller allows cross-origin requests from the frontend development server.

4. Search indexing
   - Hibernate Search (Lucene) builds indexes at startup (best-effort).
   - Indexes are stored under `./indexes` relative to the working directory when configured.
   - If indexes are missing or stale, delete the `indexes/` folder and restart the app to reindex.

5. API (selected endpoints)
   - GET /api/users?page={n}&size={s}&q={text}
     - Returns an array of matching users (JSON list). The controller hides Page metadata and returns only the list for the frontend.
     - `q` is a free-text search applied to name, ssn, email and username.
   - GET /api/users/{id}
     - Returns full details for a single user.
   - GET /api/users/by-email?email={email}
     - Returns a single user by email (valid email format required).

6. Tests
   - Run backend tests with:
     mvn test

Frontend (React + Vite)
1. Install
   - cd frontend
   - npm install

2. Configure API base URL
   - Edit `frontend/.env` or create it with:
     VITE_API_BASE_URL=http://localhost:8080
   - The frontend uses this env var to call the backend during development.

3. Run dev server
   - cd frontend
   - npm run dev
   - Open the app at http://localhost:5173 (or the port Vite prints)

4. Build for production
   - cd frontend
   - npm run build
   - Preview production build:
     npm run preview

5. Tests
   - Frontend unit tests use Jest + React Testing Library
   - Run:
     npm test

How the frontend works
- Global search bar: type 3+ characters to trigger search (debounced). Results display in a responsive grid of cards.
- Client-side features: sorting by age, filtering by role, country, and age range. Infinite scroll / lazy loading loads more pages from the backend.
- Click a card to view a creative expanded details panel with tabs (Overview, Company, Bank, Raw JSON).

Data and privacy
- The users dataset in `src/main/resources/users-payload.json` is sample/test data included for local development.
- The backend endpoint returns only the user DTO list for search responses. It does not expose internal pageable metadata.

Troubleshooting
- CORS errors in the browser: ensure the backend is running and the controller allows cross-origin requests or use the Vite proxy.
- No results from search: ensure Hibernate Search indexes exist and are up-to-date; delete `indexes/` and restart to force reindex.
- Blank frontend page: check the browser console for errors and ensure `VITE_API_BASE_URL` points to the running backend.

Contributing
- Follow the existing project structure. Run backend and frontend tests after changes. Keep UI and API contract stable.

License and Contact
- This project is maintained locally. For questions about running or modifying it, inspect README and run the test suites to validate changes.
