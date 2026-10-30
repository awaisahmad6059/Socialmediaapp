# MKWK - Detailed Project Documentation

## Project Name
**MKWK (Social Media Services Ordering App)**

## Purpose
To provide a digital platform for users to order social media growth services online with a simple order submission, payment proof/upload flow, and an admin panel to manage and fulfill orders.

## User Roles & Responsibilities

| Role | Responsibilities |
|---|---|
| **User** | Browse services, place orders, submit payment details, view order history & status, receive notifications, view contact info. |
| **Admin** | Manage services, process new orders, mark orders as completed/cancelled, manage users, add/edit/delete payment methods, send notifications, update contact settings. |

## Module Breakdown

### Authentication Module
- `WelcomeActivity` – App intro/splash-like entry point
- `LoginActivity` – User/Admin login
- `SignUpActivity` – New user registration

### User Module (`UserSide` & `UserFragment/`)
Allows users to explore services, place orders, manage payments, track order history, get notifications, and contact admin.

### Admin Module (`AdminSide` & `Fragment/`)
Complete control over services, orders, payments, users, notifications and system settings.

## Key Activities Overview

| Activity | Description |
|---|---|
| `WelcomeActivity` | Entry point of the application |
| `LoginActivity` / `SignUpActivity` | Authentication screens |
| `UserDashboardActivity` | Main user panel with bottom/navigation to user features |
| `AdminDashboardActivity` | Main admin panel for managing the entire system |
| `UserNewOrderActivity` | Place a new order for selected service |
| `UserOrderHistoryDetailActivity` | Detailed view of user's order history |
| `AdminNewOrderActivity` / `AdminNewOrderDetailActivity` | View & process new incoming orders |
| `AdminCompleteorderActivity` / `AdminCompleteOrderDetailActivity` | View completed orders with details |
| `AdmincancelActivity` | View cancelled orders |
| `AdminAddServiceActivity` | Add new social media service |
| `AddPaymentmethodActivity` | Add payment method for users |
| `NotificationDetailActivity` | View notification details |
| `AdminNotificationSentActivity` | Admin view for sent notifications |
| `AdminContactSettingActivity` | Update contact/payment info shown to users |

## Key Fragments Overview

| Fragment | Description |
|---|---|
| `UserDashboardFragment` | User home with services listing |
| `UserOrderHistoryFragment` | User's order history by status |
| `UserDepositFragment` | Payment/deposit related section |
| `UserContactFragment` | Contact details & support info |
| `AddPaymentFragment` / `ShowPaymentFragment` / `ViewEditPaymentFragment` / `DeletePaymentFragment` | Payment methods management (Admin side) |
| `AdminDashboardFragment` | Admin overview |
| `AdminServiceFragment` | Service management panel |
| `AdminNotificationFragment` | Admin notification management |
| `UserManagementFragment` | View/manage registered users |

## Data Models / Classes

Key classes used across the app:
- `Service.kt` / `ServiceAdapter.kt` – Service listing
- `Order.kt` / `NewOrder.kt` / `CompletedOrder.kt` – Order data
- `NewOrderAdapter.kt`, `CompletedOrderAdapter.kt`, `OrderHistoryAdapter.kt` – Order lists
- `PaymentListAdapter.kt`, `UserPaymentAdapter.kt`, `DeletePaymentAdapter.kt`, `ViewEditPaymentAdapter.kt` – Payment handling
- `Notification.kt`, `NotificationUser.kt`, `NotificationAdapter.kt`, `AdminUsersNotificationAdapter.kt` – Notifications
- `UserAdapter.kt` – User listing

## UI Components

- **RecyclerView Adapters** – For services, orders, payments, users & notifications
- **Fragments** – Modular UI for dashboards
- **ViewBinding** – Safe & clean UI binding
- **CircleImageView** – Profile/image views
- **Material Components** – Buttons, cards, layouts

## Dependencies Used

```kotlin
// Core
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.appcompat)
implementation(libs.material)
implementation(libs.androidx.activity)
implementation(libs.androidx.constraintlayout)

// Firebase
implementation(libs.firebase.auth)
implementation(libs.firebase.firestore)

// Google Credentials
implementation(libs.androidx.credentials)
implementation(libs.androidx.credentials.play.services.auth)
implementation(libs.googleid)

// Third-party
implementation("de.hdodenhof:circleimageview:3.1.0")
implementation("com.google.android.material:material:1.6.0")

// Testing
testImplementation(libs.junit)
androidTestImplementation(libs.androidx.junit)
androidTestImplementation(libs.androidx.espresso.core)
```

## Build Configuration Highlights

- **Namespace:** `com.mk.mkwk`
- **Application ID:** `com.mk.mkwk`
- **ViewBinding Enabled:** Yes
- **Google Services Plugin:** Applied for Firebase
- **Kotlin JVM Target:** `11`
- **Java Compatibility:** `11`
- **Min SDK:** `23` | **Target SDK:** `36` | **Compile SDK:** `36`

## Order Flow Summary

1. **User** browses services from User Dashboard
2. **User** places order via `UserNewOrderActivity` with required details
3. **User** views payment methods and submits payment info
4. **Admin** sees order in `AdminNewOrderActivity` and reviews details
5. **Admin** can process/order update to **Completed** or **Cancelled**
6. **User** can track status via **Order History** and receive **Notifications**

## Future Improvements (Suggestions)
- Add order status push notifications (FCM)
- Upload payment screenshot directly to Firebase Storage
- Add order search/filter for admin
- Implement order pricing calculator
- Add dark mode support

## Author Info
Project developed for service-based ordering demo purposes under the name **MKWK**.
