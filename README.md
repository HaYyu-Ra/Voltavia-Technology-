# Voltavia Client Portal — Android Native Application

**Company:** Voltavia Technologies  
**Lead Developer:** Hayyu Ragea Chewaka  
**Sample Application ID:** `com.voltavia.clientportal`  
**Primary Market:** Ethiopia  
**Default Language:** English (Localized string resources ready for Afan Oromo `res/values-om` and Amharic `res/values-am`)  
**Currency:** ETB (Ethiopian Birr)  
**Target Platform:** Android Phones & Tablets (minSdk 26, targetSdk 36)  

---

## 1. Technical Stack & Pinned Environment Versions

This native Android application is developed following modern Android Jetpack & Material 3 architecture guidelines:

| Component | Pinned Version / Requirement | Notes |
| :--- | :--- | :--- |
| **Language** | Kotlin 2.2.10 | Coroutines 1.10.2 & Flow |
| **UI Framework** | Jetpack Compose (BOM 2024.09.00) | Material Design 3 (M3) |
| **Navigation** | Navigation Compose 2.8.9 | Type-safe route management |
| **Local Persistence** | AndroidX Room 2.7.0 (with KSP 2.3.5) | Fully bundled seed data & migrations |
| **State Management** | AndroidX Lifecycle & ViewModel 2.8.7 | `StateFlow` + `SharingStarted.WhileSubscribed` |
| **Build System** | Android Gradle Plugin (AGP) 9.1.1 | Gradle Kotlin DSL (`build.gradle.kts`) |
| **Java Development Kit** | JDK 17 / 21 | Target bytecode Java 11/17 |
| **Android SDK** | `minSdk 26` (Android 8.0+), `compileSdk 36` | Adaptive phone & tablet layouts |
| **Cloud Backend** | Cloud Firestore & Firebase Auth | Zero-trust security rules (`firestore.rules`) |
| **Testing** | Robolectric 4.16.1 & Compose JUnit4 | Local JVM CUJ testing |

---

## 2. Architecture & Design Highlights

### Offline-First Architecture
- **Zero-Network First Launch:** Works immediately upon install in Airplane Mode without requiring Firebase credentials, logins, or active network connections.
- **Repeatable Bundled Seeding:** On database initialization, sample projects, milestones, and initial support tickets are populated idempotently into the Room database without creating duplicate entries.
- **Draft Persistence:** Users can begin a quote request, save a draft, close the app completely or reboot the device, and reopen the draft intact.
- **Idempotent Reference System:**
  - Demo mode generates local references: `VLT-DEMO-2026-XXXX`.
  - Production mode generates cloud references: `VLT-PROD-2026-XXXX`.
  - In-memory submission tracking and debounce locks prevent duplicate records on repeated taps or network retries.
- **Ethiopian Phone Validation & Normalizer:**
  - Validates `+2519...`, `+2517...`, `09...`, `07...`, and raw 9-digit formats.
  - Automatically normalizes inputs to standardized format: `+251 9XX XXX XXX`.

---

## 3. Fast Verification & Build Commands

Run the following commands in the project root:

### A. Run Unit & Robolectric Tests
```bash
gradle :app:testDebugUnitTest
```
*Executes validation tests for Ethiopian phone parsing, annual support plan savings calculations, Room database persistence, idempotent seeding, and Compose UI end-to-end quote flow.*

### B. Build Installable Demo Debug APK
```bash
gradle assembleDebug
```
*Generated APK Output:*
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 4. Beginner-Friendly Phone Installation Guide

Follow these steps to demonstrate the application to your client on a physical Android device:

1. **Copy the APK to your phone:**
   - Connect your phone via USB cable and copy `app/build/outputs/apk/debug/app-debug.apk` to your phone's **Download** folder.
   - Alternatively, send the `.apk` file via Telegram, Google Drive, or local Wi-Fi share.

2. **Enable Airplane Mode Before Opening:**
   - Pull down the Android notification shade and tap **Airplane Mode** (turning off all Wi-Fi and Cellular data).

3. **Install the APK:**
   - Open your phone's **Files** or **Downloads** app.
   - Tap on `app-debug.apk`.
   - If prompted by Android with *"For your security, your phone is not allowed to install unknown apps from this source"*, tap **Settings** and toggle **Allow from this source**, then return and tap **Install**.

4. **Launch and Verify:**
   - Tap **Open** or find the **Voltavia** custom high-tech launcher icon on your home screen.
   - Notice the prominent **"Offline Demo"** badge in the top bar. All services, plan comparisons, sample projects, and quote submission workflows operate completely offline.

---

## 5. Client Demonstration Script (3-Minute Walkthrough)

When presenting the app to a client, follow this recommended sequence:

1. **Step 1: Introduction & Company Branding (Home Screen)**
   - *"Welcome to the Voltavia Client Portal. Notice our corporate branding, featured services, and the 'Offline Demo' indicator."*
   - Point out the Lead Developer attribution (*Hayyu Ragea Chewaka*) and company contact quick links.

2. **Step 2: Service Catalog & Scope Exploration**
   - Tap **Services** in the navigation bar.
   - Filter by categories: *Software & Web*, *Solar Energy*, *CCTV Security*, *Networking*, and *ICT Support*.
   - Tap on **Solar Energy Design & Installation**.
   - Show the client the detailed 5-stage delivery methodology (Site Survey, Blueprint, Procurement, Wiring, Commissioning) and the indicative pricing disclaimer.

3. **Step 3: ICT Support Plan Comparison & Live Tax Computation**
   - Tap **Plans** in the navigation bar.
   - Toggle between **Monthly** and **Yearly** billing.
   - Point out the dynamic annual savings badge: *"Choosing the yearly enterprise plan saves ETB 66,000 annually (~20% discount)."*
   - Toggle the **15% VAT estimate** switch to demonstrate transparent Ethiopian tax estimation.
   - Tap **Select & Request Quote** on the Business Enterprise plan.

4. **Step 4: Requesting a Quote & Draft Persistence**
   - The selected service is pre-filled into the form.
   - Enter client name (e.g., `Abebe Bikila`) and an Ethiopian phone number (`0911234567`).
   - Tap **Save Draft**. Show the confirmation message: *"Draft saved to device successfully."*
   - Tap **Review Details**. Show how the phone number is normalized to `+251 91 123 4567` and the review summary card appears.
   - Tap **Confirm & Submit Request**.
   - The confirmation screen generates a unique reference code: `VLT-DEMO-2026-XXXX` tagged with **"Saved on this device"**.

5. **Step 5: Project Milestones & Live Support Ticket**
   - Tap **Projects** to showcase live sample projects (such as the *Sheger Commercial Center Solar Hybrid 30kVA*).
   - Tap to inspect the 6-stage milestone tracker with verified engineer sign-off notes.
   - Tap **Support** to view the active incident desk.
   - Open ticket `TKT-DEMO-5012`, inspect the technician updates, and post a test client reply into the live thread.

6. **Step 6: Profile & Direct Communications**
   - Tap **Contact / Profile**.
   - Show the quick action buttons for direct calling (`+251 91 123 4567`) and emailing.
   - Show the **"Reset Sample Demo Data"** action to return the device to initial clean state whenever starting a new presentation.

---

## 6. Production Backend Setup (Firebase Firestore & Auth)

When you are ready to connect to a live cloud backend for client accounts and multi-tenant data sync:

### 1. Create a Firebase Project
1. Visit the [Firebase Console](https://console.firebase.google.com/) and create a new project named `voltavia-client-portal`.
2. Add an Android app with Package Name: `com.voltavia.clientportal`.
3. Download the generated `google-services.json` and place it in the `app/` folder.

### 2. Enable Authentication
1. In the Firebase Console, navigate to **Build > Authentication > Sign-in method**.
2. Enable **Email/Password**.

### 3. Deploy Security Rules & Indexes
Install the Firebase CLI and deploy the included configuration:
```bash
# Login to Firebase
firebase login

# Set your project
firebase use --add voltavia-client-portal

# Deploy Firestore rules and composite indexes
firebase deploy --only firestore:rules,firestore:indexes
```

### 4. Firestore Zero-Trust Security Model
The included `firestore.rules` file enforces strict isolation:
- **Customer Isolation:** Customers can only read and write their own documents matching `request.auth.uid == customerId`. Customer A can never access Customer B's quotes, projects, or tickets.
- **Staff-Only Guardrails:** Customers cannot alter administrative fields such as `approvedStatus`, `bindingPrice`, `milestoneStatus`, `resolutionCode`, or `isStaff` roles.
- **Staff Operations:** Authorized Voltavia engineers use the staff role custom claim (`request.auth.token.role == 'staff'`) via the Voltavia Engineering Console to approve quotes and advance project milestones.

### 5. Local Emulator Suite Testing
To run and test security rules locally:
```bash
firebase emulators:start --only firestore
```

---

## 7. Release Signing & Production Distribution

### Generating an Upload Keystore
To sign the production APK or Android App Bundle (AAB):
```bash
keytool -genkey -v -keystore voltavia-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias voltavia_key
```

### Building Distribution Artifacts
- **Direct Side-loading APK:**
  ```bash
  gradle assembleRelease
  ```
  *Produces an installable `.apk` file for direct phone installation or enterprise distribution.*

- **Google Play Store App Bundle (AAB):**
  ```bash
  gradle bundleRelease
  ```
  *Produces the optimized `.aab` file required by Google Play Console.*

---

## 8. Configurable Placeholders Catalog

To customize this portal for another enterprise client or update contact details, edit the following centralized files:

| Configurable Item | Current Placeholder Value | Configuration File |
| :--- | :--- | :--- |
| **App Name** | Voltavia Client Portal | `app/src/main/res/values/strings.xml` |
| **Company Legal Name** | Voltavia Technologies | `app/src/main/java/com/example/data/model/CompanyConfig.kt` |
| **Lead Developer** | Hayyu Ragea Chewaka | `CompanyConfig.kt` & `strings.xml` |
| **Primary Support Phone** | `+251 91 123 4567` | `CompanyConfig.PHONE_PRIMARY` |
| **Office Phone** | `+251 11 654 3210` | `CompanyConfig.PHONE_OFFICE` |
| **Support Email** | `support@voltavia.com` | `CompanyConfig.EMAIL_SUPPORT` |
| **Physical Address** | Bole Sub-City, Addis Ababa, Ethiopia | `CompanyConfig.HEADQUARTERS_ADDRESS` |
| **Default VAT Rate** | 15% (0.15) | `CompanyConfig.DEFAULT_VAT_RATE` |
| **Services & BOQ Catalog**| 6 Primary Engineering Lines | `app/src/main/java/com/example/data/model/ServiceItem.kt` |
| **Support Plans & SLAs** | Standard, Enterprise, Mission-Critical | `app/src/main/java/com/example/data/model/IctPlan.kt` |
