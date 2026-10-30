# MKWK - Social Media Services Ordering App

A Kotlin-based Android application designed for ordering social media services (likes, followers, views, etc.). The app provides separate panels for **Users** and **Admin** to manage service orders, payments, and notifications efficiently.

## 📱 About Project

MKWK is a service ordering platform where users can browse available social media services, place orders by providing required details, make payments via uploaded payment methods, and track their order history. Admins can manage services, handle incoming orders (New, Completed, Cancelled), send notifications to users, and configure payment/contact settings.

## ✨ Features

### 👤 User Side
- **Authentication** - User login & signup
- **Service Browsing** - View available social media services
- **Place Orders** - Place orders for selected services with required information
- **Payment System** - View available payment methods and submit payment details
- **Order History** - Track all orders (New, Completed, Cancelled)
- **Order Details** - View complete order information
- **Notifications** - Receive admin notifications
- **Contact Support** - Access contact/admin details
- **Profile Management** - User dashboard access

### 🧑‍💼 Admin Side
- **Admin Dashboard** - Overview of orders and system activity
- **Service Management** - Add, update and manage available services
- **Order Management**
  - **New Orders** - View and process incoming orders
  - **Completed Orders** - Track successfully completed orders
  - **Cancelled Orders** - View cancelled/rejected orders
- **User Management** - Manage registered users
- **Payment Management** - Add, view, edit and delete payment methods
- **Notifications** - Send notifications to users
- **Contact Settings** - Configure contact/payment details for users
- **Order Details** - View full order & payment details

## 🛠️ Tech Stack

| Technology | Usage |
|-------------|-------|
| **Kotlin** | Primary programming language |
| **Android SDK** | Native Android Development |
| **XML** | UI Design & Layouts |
| **ViewBinding** | Type-safe view access |
| **Firebase Authentication** | User Login & Signup |
| **Firebase Firestore** | Real-time database for orders, services, users, payments |
| **Material Design Components** | Modern UI elements |
| **CircleImageView** | Circular profile images |
| **AndroidX** | Core Android libraries |

## 🗂️ Project Structure

```text
app/src/main/java/com/mk/mkwk/
├── Fragment/                # Fragments for User & Admin panels
│   ├── AdminDashboardFragment.kt
│   ├── AdminServiceFragment.kt
│   ├── AdminNotificationFragment.kt
│   ├── UserDashboardFragment.kt
│   ├── UserOrderHistoryFragment.kt
│   ├── UserDepositFragment.kt
│   ├── UserContactFragment.kt
│   ├── UserManagementFragment.kt
│   ├── AddPaymentFragment.kt
│   ├── ShowPaymentFragment.kt
│   ├── ViewEditPaymentFragment.kt
│   └── DeletePaymentFragment.kt
├── Adapters                 # RecyclerView adapters for lists
├── Models/Activities        # Data classes & Activities
└── Authentication          # Welcome, Login, Signup
```

## ⚙️ Requirements

- Android Studio (Latest Stable Version Recommended)
- JDK 11+
- Android SDK 23 (Min) to 36 (Target/Compile)
- Firebase Project with `google-services.json`
- Internet Connection

## 🚀 How to Run Locally

1. **Clone the Repository**
   ```bash
   git clone https://github.com/awaisahmad6059/Socialmediaapp.git
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select `File > Open` and choose the cloned `Socialmediaapp` folder

3. **Setup Firebase**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use existing one
   - Add Android app with package name: `com.mk.mkwk`
   - Download `google-services.json` and place it inside `app/` directory
   - Enable **Firebase Authentication** (Email/Password) and **Cloud Firestore**

4. **Sync & Run**
   - Sync Gradle files
   - Connect an emulator or physical device
   - Click `Run > Run 'app'`

## 📄 App Info

- **Package Name:** `com.mk.mkwk`
- **Version Code:** `1`
- **Version Name:** `1.0`
- **Min SDK:** `23 (Android 6.0+)`
- **Target SDK:** `36`
- **Compile SDK:** `36`

## 📝 Note

This project uses Firebase for Authentication and Firestore as its backend/database. Proper Firestore security rules are recommended before deploying to production.
