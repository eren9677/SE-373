import mysql.connector
from mysql.connector import Error

def create_connection():
    try:
        connection = mysql.connector.connect(
            host="localhost",
            user="root",
            password="",  # Default XAMPP has no password
            database="news_db"  # Will create this database
        )
        return connection
    except Error as e:
        print(f"Error connecting to MySQL Database: {e}")
        return None

def create_database():
    try:
        connection = mysql.connector.connect(
            host="localhost",
            user="root",
            password=""
        )
        cursor = connection.cursor()
        cursor.execute("CREATE DATABASE IF NOT EXISTS news_db")
    except Error as e:
        print(f"Error creating database: {e}")

def create_tables():
    connection = create_connection()
    if connection is None:
        return

    try:
        cursor = connection.cursor()
        
        # Create news_stats table
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS news_stats (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL UNIQUE,
                pos INT DEFAULT 0,
                neg INT DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """)
        
        connection.commit()
        print("Table created successfully")
    except Error as e:
        print(f"Error creating table: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

# Initialize database and tables
if __name__ == "__main__":
    create_database()
    create_tables()