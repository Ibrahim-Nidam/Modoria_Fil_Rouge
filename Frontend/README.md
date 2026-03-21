
# Modoria Frontend — Angular 21 Standalone

The Modoria frontend delivers a luxury e-commerce experience with a modern, high-performance Angular 21 standalone architecture.

## Features

- **Standalone Components**: No NgModules; all features use Angular's latest standalone APIs.
- **Strict Separation**: Logic in `.ts`, templates in `.html`, styles in `.css`.
- **State Management**: Angular Signals for local and UI state; service-based state for app-wide flows.
- **Authentication**: Secure login, registration, and role-based access (ADMIN, CLIENT, AGENT).
- **Admin Dashboard**: Real-time stats, order management, and support ticketing.
- **Support Chat**: Customer support via REST APIs (WebSocket backend available for future real-time updates).
- **Order Flow**: Cart, checkout, and order history with branded email confirmations.
- **Design System**: Minimalist, whitespace-rich, zero-radius luxury UI.

## Project Structure

- `src/app/core/` — Singleton services (auth, HTTP interceptors, guards)
- `src/app/features/` — Feature modules (auth, catalog, cart, admin, chat, stylist)
- `src/app/shared/` — Reusable UI components, directives, and pipes

## Getting Started

### Prerequisites
- Node.js 22+
- npm 10+
- Angular CLI (`npm install -g @angular/cli`)

### Local Development
```bash
npm install
npm start
```

`npm start` runs Angular dev server with `proxy.conf.json`, so relative calls like `/api` and `/storage` are proxied to `http://localhost:8081`.

---
© 2026 Modoria Frontend Team
