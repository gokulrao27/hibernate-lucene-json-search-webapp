# Dummy Users Frontend

React + Vite + TypeScript frontend for the Dummy Users backend.

Features
- Minimalistic Google-like search bar (debounced, 3+ chars triggers backend)
- Grid of user cards with avatar fallback
- Client-side sorting and filtering
- Infinite scroll / lazy loading
- Responsive UI built with Tailwind CSS
- React Query used for caching and pagination
- Unit tests with React Testing Library + Jest

Setup
1. Install dependencies:
   npm install

2. Configure backend base URL (optional):
   Create a `.env` file in the project root (frontend/) or edit the provided `.env`:
   VITE_API_BASE_URL=http://localhost:8080

3. Start dev server:
   npm run dev

4. Run tests:
   npm test

Notes
- The app expects the backend search endpoint at `/api/users/search` to support pagination params `page` and `size` and optional `query`.
- Logo and favicon are included as static assets in `src/assets`.


