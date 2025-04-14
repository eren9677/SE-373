from flask import Flask, request, jsonify
import pymysql
import os

app = Flask(__name__)

# Database configuration
DB_HOST = 'localhost'
DB_USER = 'root'  # default XAMPP username
DB_PASSWORD = ''  # default XAMPP password (empty)
DB_NAME = 'calendar_events'

# Initialize database connection
def get_db_connection():
    return pymysql.connect(
        host=DB_HOST,
        user=DB_USER,
        password=DB_PASSWORD,
        db=DB_NAME,
        charset='utf8mb4',
        cursorclass=pymysql.cursors.DictCursor
    )

# Initialize database
def init_db():
    try:
        # First create the database if it doesn't exist
        conn = pymysql.connect(
            host=DB_HOST,
            user=DB_USER,
            password=DB_PASSWORD
        )
        cursor = conn.cursor()
        
        # Create database if not exists
        cursor.execute(f"CREATE DATABASE IF NOT EXISTS {DB_NAME}")
        conn.commit()
        conn.close()
        
        # Connect to the database and create table
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute('''
        CREATE TABLE IF NOT EXISTS reminders (
            id INT AUTO_INCREMENT PRIMARY KEY,
            class VARCHAR(255) NOT NULL,
            exam VARCHAR(255) NOT NULL
        )
        ''')
        conn.commit()
        conn.close()
        print("Database initialized successfully!")
    except Exception as e:
        print(f"Database initialization error: {e}")

# Run database initialization
init_db()

@app.route('/reminders', methods=['GET'])
def get_reminders():
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        
        # Check if date parameter is provided
        date_param = request.args.get('date')
        
        if date_param:
            # Filter by date
            cursor.execute("SELECT * FROM reminders WHERE exam = %s", (date_param,))
        else:
            # Return all reminders
            cursor.execute("SELECT * FROM reminders")
        
        reminders = cursor.fetchall()
        conn.close()
        
        return jsonify(reminders)
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/reminders', methods=['POST'])
def add_reminder():
    try:
        data = request.get_json()
        
        if not data or 'class' not in data or 'exam' not in data:
            return jsonify({"error": "Missing required fields: class and exam"}), 400
        
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("INSERT INTO reminders (class, exam) VALUES (%s, %s)",
                  (data['class'], data['exam']))
        conn.commit()
        reminder_id = cursor.lastrowid
        conn.close()
        
        return jsonify({"id": reminder_id, "message": "Reminder added successfully"}), 201
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001, debug=True) 