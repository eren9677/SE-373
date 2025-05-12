from flask import Flask, jsonify
from flask_cors import CORS
import database as db
from urllib.parse import unquote

app = Flask(__name__)
CORS(app)

@app.route('/register_news/<title>', methods=['POST'])
def register_news(title):
    connection = db.create_connection()
    if connection is None:
        return jsonify({'error': 'Database connection failed'}), 500

    try:
        cursor = connection.cursor()
        decoded_title = unquote(title)
        cursor.execute("""
            INSERT IGNORE INTO news_stats (title, pos, neg)
            VALUES (%s, 0, 0)
        """, (decoded_title,))
        connection.commit()
        return jsonify({'message': 'News registered successfully'})
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        cursor.close()
        connection.close()

@app.route('/thumb_up/<title>', methods=['POST'])
def thumb_up(title):
    connection = db.create_connection()
    if connection is None:
        return jsonify({'error': 'Database connection failed'}), 500

    try:
        cursor = connection.cursor()
        decoded_title = unquote(title)
        cursor.execute("""
            UPDATE news_stats 
            SET pos = pos + 1 
            WHERE title = %s
        """, (decoded_title,))
        connection.commit()
        
        # Get updated counts
        cursor.execute("""
            SELECT pos, neg FROM news_stats 
            WHERE title = %s
        """, (decoded_title,))
        result = cursor.fetchone()
        if result:
            pos, neg = result
            return jsonify({'pos': pos, 'neg': neg})
        return jsonify({'error': 'News not found'}), 404
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        cursor.close()
        connection.close()

@app.route('/thumb_down/<title>', methods=['POST'])
def thumb_down(title):
    connection = db.create_connection()
    if connection is None:
        return jsonify({'error': 'Database connection failed'}), 500

    try:
        cursor = connection.cursor()
        decoded_title = unquote(title)
        cursor.execute("""
            UPDATE news_stats 
            SET neg = neg + 1 
            WHERE title = %s
        """, (decoded_title,))
        connection.commit()
        
        # Get updated counts
        cursor.execute("""
            SELECT pos, neg FROM news_stats 
            WHERE title = %s
        """, (decoded_title,))
        result = cursor.fetchone()
        if result:
            pos, neg = result
            return jsonify({'pos': pos, 'neg': neg})
        return jsonify({'error': 'News not found'}), 404
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        cursor.close()
        connection.close()

@app.route('/get_stats/<title>', methods=['GET'])
def get_stats(title):
    connection = db.create_connection()
    if connection is None:
        return jsonify({'error': 'Database connection failed'}), 500

    try:
        cursor = connection.cursor()
        decoded_title = unquote(title)
        cursor.execute("""
            SELECT pos, neg FROM news_stats 
            WHERE title = %s
        """, (decoded_title,))
        result = cursor.fetchone()
        if result:
            pos, neg = result
            return jsonify({'pos': pos, 'neg': neg})
        return jsonify({'pos': 0, 'neg': 0})
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        cursor.close()
        connection.close()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5005)
