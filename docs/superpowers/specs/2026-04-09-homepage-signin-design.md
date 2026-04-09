# Homepage And Signin Split Design

**Date:** 2026-04-09

## Goal

Turn the current video-driven login page into a public marketing homepage, move descriptive content below the fold, and create a dedicated `/signin` page for account login.

## User Experience

- `/` becomes a public landing page.
- The hero stays video-first, with minimal copy and no lower glass bars blocking the person in the background.
- The left-bottom explanation moves out of the hero and becomes later sections on the page.
- Clicking `登录` or the hero CTA routes to `/signin` instead of opening an inline modal.
- `/signin` becomes a focused black login page inspired by the provided screenshot.
- Protected app routes continue to redirect unauthenticated users to the signin page.

## Information Architecture

### Public routes
- `/`: marketing homepage
- `/signin`: dedicated login page
- `/product`: optional alias/section target resolved inside the homepage flow for navbar consistency

### Protected routes
- `/docs`
- `/chat`
- `/quiz`
- `/review`

## Homepage Sections

1. Fixed floating navbar with left brand, centered links, and right login button.
2. Video hero with minimal copy, prominent CTA, and no decorative glass bars crossing the title.
3. Core features grid.
4. How it works three-step section.
5. Pricing cards.
6. Bottom CTA.
7. Footer.

## Signin Page

- Black-dominant atmosphere with muted video ambience in the background.
- Centered welcome copy and form.
- Real username/password login remains connected to the current auth API.
- The page stays visually separate from the marketing homepage.

## Technical Notes

- Stay in the existing Vue + Vite app.
- Reuse the existing auth API and store.
- Replace modal login behavior on the current page with route navigation to `/signin`.
- Update router guards to redirect unauthenticated traffic to `/signin`.

## Risks And Constraints

- The current frontend stack does not include Tailwind, shadcn/ui, Framer Motion, or Lucide. The requested look will be recreated in Vue with scoped CSS and lightweight DOM/CSS animation instead of introducing a new UI stack.
- Build verification in this environment requires elevated execution because sandboxed esbuild fails with `spawn EPERM`.
