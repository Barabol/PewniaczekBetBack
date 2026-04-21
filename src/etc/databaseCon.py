import psycopg2

#TODO: make it use .env for connection atributes
__con = psycopg2.connect(database="isi", user="user", password="", host="localhost", port=5432)

def getConection():
    return __con
