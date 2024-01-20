from constants import *
import aiomysql, asyncio

async def _db_connect():
    db = await aiomysql.connect(
        host=DB_HOST,
        port=DB_PORT,
        user=DB_USER,
        password=DB_PASSWORD,
        db=DB_NAME,
        autocommit=True,
        loop=asyncio.get_event_loop()
    )
    cursor = await db.cursor()
    return db, cursor

async def db_query(sql,params=[]):
    db, cursor = await _db_connect()
    await cursor.execute(sql,params)
    result = await cursor.fetchall()
    db.close()
    return result