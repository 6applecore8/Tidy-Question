# Homepage And Signin Split Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Split the current login experience into a public marketing homepage and a dedicated signin page, while preserving existing authenticated app flows.

**Architecture:** Keep the app in Vue Router and add two public pages: a marketing home page at `/` and a dedicated login page at `/signin`. Reuse the existing auth API/store and update route guards so protected pages redirect to the new signin page.

**Tech Stack:** Vue 3, Vue Router, Pinia, Element Plus, Vite, scoped CSS

---

### Task 1: Route Split

**Files:**
- Create: `fronted/src/pages/home/HomePage.vue`
- Create: `fronted/src/pages/signin/SignInPage.vue`
- Modify: `fronted/src/app/router/index.ts`

- [ ] Add the new homepage component route at `/`.
- [ ] Add the dedicated signin route at `/signin`.
- [ ] Update unauthenticated redirects from `/login` to `/signin`.
- [ ] Update authenticated redirects away from `/signin` to `/docs`.
- [ ] Keep `/docs`, `/chat`, `/quiz`, and `/review` behind auth.

### Task 2: Homepage Implementation

**Files:**
- Create: `fronted/src/pages/home/HomePage.vue`

- [ ] Build a fixed floating navbar with a centered pill navigation.
- [ ] Build a video hero with minimal copy and CTA buttons.
- [ ] Move explanatory content into later sections rather than the hero overlay.
- [ ] Add features, how-it-works, pricing, bottom CTA, and footer sections.
- [ ] Route all login CTAs to `/signin`.

### Task 3: Dedicated Signin Page

**Files:**
- Create: `fronted/src/pages/signin/SignInPage.vue`
- Modify: `fronted/src/features/auth/components/LoginCard.vue`

- [ ] Build a centered black login screen matching the reference direction.
- [ ] Keep the login card wired to the existing `submit` event contract.
- [ ] Ensure successful login still redirects to the requested protected route or `/docs`.

### Task 4: Cleanup And Verification

**Files:**
- Modify: `fronted/src/pages/login/LoginPage.vue` if needed for compatibility or deprecation handling

- [ ] Remove obsolete modal-login-only behavior from the old page flow.
- [ ] Run `npm.cmd run lint` in `fronted`.
- [ ] Run `npm.cmd run build` in `fronted` with elevated execution.
- [ ] Summarize any remaining visual follow-up items.
