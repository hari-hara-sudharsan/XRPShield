# XRPShield — SaaS Design System & UX Guidelines

## 1. Design Principles & Aesthetics
XRPShield follows an enterprise SaaS design philosophy with sleek dark mode aesthetics, glassmorphism, responsive grid architecture, clear visual hierarchy, and accessible micro-interactions.

---

## 2. Color Palette & Tokens

| Token Name | CSS Variable | Hex / HSL | Application |
|---|---|---|---|
| Dark Background | `--bg-dark` | `#090D16` | Main App Canvas |
| Card Glass | `--bg-card` | `rgba(15, 23, 42, 0.75)` | Glassmorphic Cards & Tables |
| Primary Neon Cyan | `--primary-cyan` | `#00F2FE` | Primary CTA & Glow Highlights |
| Primary Deep Blue | `--primary-blue` | `#4FACFE` | Button Gradients |
| Accent Emerald | `--accent-emerald` | `#10B981` | Success Status & Active Probes |
| Accent Amber | `--accent-amber` | `#F59E0B` | Warning Indicators |
| Accent Rose | `--accent-rose` | `#EF4444` | Error Alerts |

---

## 3. UI Component Library
- **Cards (`.card`, `.glass-panel`):** Glassmorphic backdrop blur (16px) with smooth hover transforms (`translateY(-2px)`).
- **Data Tables (`.data-table`):** Structured th/td typography with hover row highlights.
- **Buttons (`.btn-primary`, `.btn-secondary`):** High-contrast primary CTA buttons and subtle secondary controls.
- **Toast Notifications (`.toast`):** Animated sliding notifications for platform feedback (`toast-success`, `toast-warning`, `toast-error`, `toast-info`).
- **Responsive Layout:** Adaptive breakpoints for Desktop (1024px+), Tablet (768px - 1024px), and Mobile (< 768px).
