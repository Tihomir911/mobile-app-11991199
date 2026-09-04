# 📱 mobile-app – Android College Project

A simple yet functional mobile application developed as a group project for college. It combines user authentication, local SQLite storage, and a built-in text editor with file management capabilities.

## 🚀 Features

- **🔐 User Authentication**  
  - Tap the **Login** button to reveal login/password fields.  
  - Credentials are checked against a local SQLite database.  
  - If the user doesn't exist, a new account is created automatically.  
  - All user data is stored internally on the device.

- **📝 Built-in Text Editor**  
  - After successful login, you get access to a simple text editor.  
  - Write anything you like and hit **Save** – your notes are stored in the database.

- **📂 File Management**  
  - **Select File** button lets you pick an existing file to write text into.  
  - **+** button allows you to create a new file with a custom name.  
  - Both actions happen within the same file‑selection interface.

- **🚪 Logout**  
  - One‑tap logout clears the session and returns you to the login screen.

## 🛠️ Tech Stack

- **Language:** Kotlin (or Java – choose whichever you prefer)  
- **IDE:** Android Studio (latest stable version)  
- **Database:** SQLite (built‑in Android support)  
- **UI:** XML layouts with Material Design components  
- **Version Control:** Git & GitHub

## 📦 Getting Started

### Prerequisites
- Android Studio (recommended version: Quail 2026.1.1 or later)
- Android SDK (API level 21 or higher)
- Git (for cloning)




⠀⠀⠀⠀⠀⠀⠀⢀⡎⠀⠀⠀⠀⠀⠀⣀⠴⠲⣄⣀⣠⠤⠤⠤⠤⠤⠤⢤⣀⣀⠀⠀⠀⠀⠀⠀⢄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⢠⠎⠀⣀⣀⠀⠀⣠⠞⣡⣾⣿⣿⣿⠄⠀⠀⠀⠀⠀⢀⣀⡀⠀⠉⠑⠒⠤⣀⡀⠈⠲⣄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⢠⠋⠀⣼⣿⣿⡿⣫⠴⠚⠛⠛⠛⠛⠛⠒⠀⠠⠶⣾⣿⣿⣿⣿⣧⠀⠀⠀⠀⠀⠉⠓⢤⡈⠳⢤⣀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⣤⠋⠀⢀⣿⣿⠿⠋⢀⠔⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠙⠿⣿⣿⣧⠀⠀⠀⠀⠀⢣⡀⠹⣷⣦⡈⠉⠀⠀⠀⠀⠀⠀
⠀⠀⢀⣼⠃⠀⠀⢸⡿⠁⢀⠴⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠙⢿⣧⠀⠀⠀⠀⠸⡳⣄⣿⣿⣿⣆⠀⠀⢀⣀⣤⣄
⠀⠀⢈⠇⠀⠀⢠⠞⠀⡠⠋⠀⣰⠀⠀⠀⠀⠘⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠹⡆⠀⠀⠀⠀⢇⠈⠻⣿⣿⣿⣷⣾⣿⣿⣿⡿
⠀⠀⠈⠀⠀⡰⠃⠀⣰⠁⠀⢠⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀⠘⡄⠀⠈⠻⣿⣿⣿⣿⣿⣿⠃
⠀⠀⠀⠀⡰⠁⠀⢠⠇⠀⠀⡌⠀⠀⠀⠀⢀⡆⠀⠀⠀⠀⠀⠀⠀⢸⠀⠀⠀⠀⠀⠀⠈⠀⠀⠀⠀⠀⠘⣆⠀⠀⢈⠛⢿⣿⣿⡁⠀
⠀⠀⠀⡰⠁⠀⠀⡞⠀⠀⢰⠃⣸⠀⠀⠀⣸⠄⠀⠀⠀⠀⠀⠀⠀⠈⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣆⠀⠸⡟⢶⣽⣿⣿⡆
⠀⠀⠐⠁⠀⠀⢰⠃⠀⠀⢸⠀⣿⠀⠀⠀⣿⠀⠀⠀⠀⠀⠀⠀⢠⠀⣇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⢿⣧⡀⢣⠈⣿⡿⠋⠀
⠀⠀⠀⠀⠀⠀⢹⠀⠀⠀⣾⢰⢻⠀⠀⢸⢹⠀⡀⠀⠀⠀⠀⠀⢸⠀⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⠈⠁⠈⠢⣧⣸⠀⠀⠀
⠀⠀⠀⠀⠀⠀⣼⠀⠀⢸⠿⡿⠙⡗⠲⡟⢹⠀⡇⠀⢀⣀⡀⠀⣾⡀⣿⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⠀⠀⠀⠀⠈⢹⠀⠀⠀
⠀⠀⠀⡆⠀⠀⣏⢰⠀⢸⠀⠇⠀⢱⡸⠀⠀⣷⣷⠀⠀⠁⠉⢙⡟⣷⠧⣧⣀⣀⠃⠀⠀⡆⠀⠀⠀⠀⠀⢸⡆⠀⠀⠀⠀⠀⣇⠀⠀
⡇⠀⠀⡇⠀⠀⢿⣼⡄⡾⠤⣀⣀⣠⢇⠀⠀⢿⠘⡄⡀⠀⠀⠸⠀⢸⠀⢸⠀⢹⠉⠒⢠⡇⠀⠀⠀⠀⠀⠸⡇⠀⠀⠀⠀⠀⠸⡀⠀
⣿⣧⠀⢳⠀⠀⢸⣿⣷⡇⢀⣿⣿⣿⣿⠀⠀⠸⠀⢱⠿⠤⠤⢤⣄⣀⣀⠀⠇⡿⠀⠀⢸⠀⠀⣆⠀⠀⠀⠀⢣⠀⠀⠀⠰⠄⠀⠁⠀
⣿⣿⣷⡈⣆⢶⡘⣿⡿⠀⠈⣿⣿⣿⠇⠀⠀⠀⠀⠘⡇⠀⠀⢸⣿⡿⣿⣿⣿⡇⠀⠀⡸⠀⣼⣿⣦⠀⠀⠀⠘⡄⠀⠀⠀⠀⠀⠀⠀
⠈⢻⣿⣿⣼⠆⣷⣿⡇⠀⠀⠈⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⣿⡿⢹⠀⠀⠀⡇⣰⣿⡿⠈⢣⣷⢄⡀⢱⠀⠀⠰⠀⠀⠀⠀
⠀⠀⠙⢿⣿⡆⠉⢻⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠋⠁⠀⡎⣠⠀⢸⣷⣻⣿⡇⠀⠀⠙⡄⠙⢦⡆⠀⠀⠇⠀⠀⠀
⠀⠀⠀⠀⠹⣿⡇⠀⣇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⣀⠀⢸⣷⡇⠀⠼⢡⣿⣿⡇⠀⠀⠀⠀⠀⠀⠉⡦⢀⡄⠀⠀⠀
⠀⠀⠀⠀⠀⠘⡇⠀⢸⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠋⢀⠟⣸⠀⠀⢠⣿⣿⣿⡇⠀⢀⡇⠀⠀⠀⠀⠀⢸⠀⠀⠀⠀
⠁⢦⣄⠀⠀⠀⣧⠀⠀⣿⣦⣀⠀⠀⠀⢠⢤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠈⢠⡇⠀⢀⣾⣿⣿⣿⡇⠀⣼⣷⡇⠀⠀⠀⠀⡌⠀⠀⠀⠀
⠀⠀⠹⡟⢷⡀⢹⡀⠀⢿⣿⣿⣷⣦⣄⠀⠀⠉⠀⠀⠀⣀⣀⣤⣴⣶⠞⣿⠀⢀⣾⣿⣿⣿⣿⠁⣰⡿⠇⢹⡀⠀⠀⢀⠇⠀⠀⠀⠀
⠀⠀⠀⠹⡄⠙⢮⣧⠀⠈⢿⣿⣿⣿⣿⣷⣦⣤⣶⣾⣿⣿⣿⠟⠉⠀⣠⠇⢀⡾⢿⣿⣿⣿⣿⣰⠁⢣⠀⠀⢧⢰⠀⡼⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠱⠀⠀⠙⣧⡀⠈⣿⡏⣿⣿⣿⡿⢿⣿⡇⠈⠉⢀⡤⠒⠉⡹⢠⠞⠀⠀⠙⢿⣿⣿⡇⠀⠈⡆⠀⠘⣿⣷⠃⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⢦⠀⠀⠘⣷⡄⢹⠃⠘⠃⢻⠇⠈⣿⠇⢠⠔⠁⠀⠀⣠⣧⠏⠀⠀⠀⠀⠀⠙⣿⡇⠀⠀⢸⡀⠀⢘⡏⠀⠀⠀⠀⠀⠀
