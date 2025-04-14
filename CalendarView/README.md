# CalendarView

A simple Android application for managing class schedules and events.

## Features

- Calendar interface to select dates
- View events for a specific date
- Add new class events with exam dates
- Store events in a MySQL database via Flask API

## Setup Instructions

### Database Setup (MySQL with XAMPP)

1. Install and start XAMPP (Apache and MySQL services)
2. Open phpMyAdmin at http://localhost/phpmyadmin
3. The database and tables will be created automatically when the server starts

### Server Setup

1. Install Python, Flask and PyMySQL:
   ```
   pip install flask pymysql
   ```

2. Run the server:
   ```
   cd app
   python server.py
   ```

3. The server will run at `http://192.168.1.107:5001`

### Android App Setup

1. Open the project in Android Studio
2. Run sync to download all dependencies
3. Build and run the app on an emulator or device

## API Endpoints

### Get Reminders
```
GET /reminders
```
Returns all reminders in the database

### Get Reminders by Date
```
GET /reminders?date=2023-05-20
```
Returns reminders for a specific date

### Create Reminder
```
POST /reminders
```
Body:
```json
{
  "class": "SE310",
  "exam": "2023-05-20"
}
```

## Technical Details

- Android application written in Kotlin
- Uses OkHttp3 for network requests
- Uses Coroutines for background processing
- MySQL database via XAMPP for data storage
- Flask REST API for data persistence 