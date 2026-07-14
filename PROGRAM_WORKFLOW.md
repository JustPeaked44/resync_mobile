# Resync Mobile App: Program Workflow

This document outlines the high-level program workflow for the Resync Android application, incorporating recent updates to the Authentication UI, the New Scan Wizard, and Style Reference support.

---

## 1. Entry & Onboarding Flow
*   **Splash Screen:** 
    *   Displays an animated Resync sync-ring logo.
    *   Performs an automatic session check via `SessionManager` (Jetpack DataStore).
    *   Navigates to **Onboarding** (first-time users), **Landing/Auth** (signed-out), or **Dashboard** (active session).
*   **Onboarding:** 
    *   A 3-step interactive tutorial showcasing "Align Your Research", "Understand Every Correction", and "Defend with Confidence".
    *   **New Entry Point:** A "Sign In" button in the top-left allows returning users to skip the tutorial and go directly to Auth.
*   **Welcome/Landing Screen:** 
    *   Presents a high-level summary of the app's value proposition.
    *   Provides two primary actions: **Create Free Account** and **Sign In**.

---

## 2. Authentication Flow
*   **Unified Auth Card:** Uses a modern tabbed layout to switch between **Log In** and **Create Account**.
    *   **Registration Logic:** Requires First Name, Last Name, Email, and Password with confirmation.
    *   **Encryption:** All data is submitted over a secure TLS 1.3 encryption standard.
*   **Session Persistence:** On success, a secure token and user profile details (Institution, Role, Bio) are saved locally to enable background synchronization.

---

## 3. Dashboard & History Management
*   **Dashboard Hub:**
    *   **Success State:** Displays the most recent manuscript's **Coherence Score**, issue counts, and citation validation status.
    *   **Empty State:** Shows a custom illustration and a "Scan your first chapter" call-to-action.
    *   **Quick History:** A scrollable list of recent audits with their respective scores.
*   **Manuscript Archive (History):**
    *   Search and filter (All, High Score, Needs Review) functionality.
    *   **Comparison Mode:** Allows selecting exactly two versions of a document to generate a delta report.
*   **Version Comparison Screen:**
    *   Visualizes score evolution over a chronological timeline.
    *   Categorizes inconsistencies into **Resolved**, **Unresolved**, and **New** groups.

---

## 4. New Scan Wizard (Core Engine)
The wizard facilitates document submission through a structured step-by-step process:
*   **Step 1: Upload Type:** Choose between "Per Chapter" (multi-document comparison) or "Whole Manuscript" (single document audit).
*   **Step 2: Selection/Source:**
    *   *Per Chapter:* Select specific chapters (1 through 6) to be included in the set.
    *   *Whole Manuscript:* Choose between a live Google Docs link or a local Word document (demo-restricted).
*   **Step 3: Submit URLs & Metadata:**
    *   **Live URLs:** Input individual Google Docs links for all selected components.
    *   **Style/Format Reference (Optional):** Provide a link to a formatting guide, journal template, or citation rulebook.
    *   **Research Focus:** Define optional themes and choose the Research Type (Quantitative vs. Qualitative).
    *   **Demo Utility:** A "Demo" button in the Top App Bar auto-fills all required fields with valid sample data for testing.

---

## 5. Analysis & Processing
*   **Foreground Processing HUD:** A translucent overlay with a cycling status label ("Analyzing terminology...", "Detecting contradictions...") keeps the user informed during active analysis.
*   **Background ScanWorker:** 
    *   Uses **WorkManager** to handle long-running network requests even if the app is minimized.
    *   Increments scan counts and triggers local notifications upon completion.
*   **Integrations:** 
    *   **OneSignal:** Sends external push notifications for scan completions.
    *   **Deep Linking:** Notifications include a direct URI (`resync://results/...`) to open the specific report.

---

## 6. Comprehensive Result Details
A detailed, multi-tabbed interface for reviewing the AI's audit:
*   **Overview:** Features a "Score Ring" visualization and a high-level **Executive Diagnosis** of document integrity.
*   **Manuscript Preview:** An interactive view of the extracted text.
    *   **Color-Coded Highlights:** Contradictions (Red), Redundancies (Orange), Logic Gaps (Purple), Terminology Clashes (Blue), and **Style Deviations (Indigo)**.
    *   **Jump-to-Card:** Tapping a highlight navigates directly to the detailed explanation in the relevant tab.
*   **Inconsistencies Tab:** Detailed breakdown of every flagged issue with "What was found," "Why it matters," and a "Recommended Correction."
*   **References Tab:** Audits all external links and citations for dead links or formatting errors.
*   **PDF Export:** Utility to generate and save a professional PDF coherence report to the device's storage.
