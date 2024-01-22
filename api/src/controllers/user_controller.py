from fastapi.routing import APIRouter
from fastapi import Response
from db import db_query
from constants import TBL_USERS,TBL_SESSIONS,TBL_SHOWS
from dto.models import UserDto,LoginDto
from utils.converters import convert_to_user

router = APIRouter(prefix='/api/users' , tags=['users'])

@router.post('/logout')
async def login_user(loginDto:LoginDto,response:Response):
    user_id = 1
    logout_sql = f"start transaction;update {TBL_SESSIONS} set logout_time = convert_tz(now(),'utc','Asia/Kolkata'),time_spent = timediff(convert_tz(now(),'utc','Asia/Kolkata'),login_time) where user_id = {user_id} and logout_time is null;commit;"
    await db_query(logout_sql)
    return {'msg':'User logged out successfully'}

@router.post('/login')
async def login_user(loginDto:LoginDto,response:Response):
    delete_sql = f'truncate table {TBL_SESSIONS}'
    await db_query(delete_sql)
    sql = f'select id,username,password,client_id from {TBL_USERS} where username = %s OR email = %s'
    user = await db_query(sql,[loginDto.username,loginDto.username])
    if len(user) == 0:
        response.status_code = 404
        return {'msg':'User Not Found With This Username Or Email'}
    user = user[0]
    if user[2] != loginDto.password:
        response.status_code = 400
        return {'msg':'Invalid Password'}
    user_id = user[0]
    session_sql = f"select id from {TBL_SESSIONS} where user_id = {user_id} and logout_time is null"
    session = await db_query(session_sql)
    if len(session) > 0:
        response.status_code = 400
        return {'msg':'User Already Logged In , please logout first and then try to login'}
    login_sql = f"start transaction;insert into {TBL_SESSIONS} (user_id,login_time) values ({user_id},convert_tz(now(),'utc','Asia/Kolkata'));commit;"
    shows_sql = f"select name from {TBL_SHOWS} where client_id = {user[3]}"
    rows = await db_query(shows_sql)
    shows = []
    for row in rows:
        shows.append(row[0])
    await db_query(login_sql)
    token = '1234567890'
    return {'token':token,'msg':'User logged in successfully','id':user_id,'clientId' :user[3],'shows':shows}

@router.get('/')
async def get_users():
    sql = f'select * from {TBL_USERS}'
    rows = await db_query(sql)
    users = []
    for row in rows:
        users.append(convert_to_user(row))
    return users

@router.get('/{id}')
async def get_user(id:int,response:Response):
    sql = f'select * from {TBL_USERS} where id=%s'
    user = await db_query(sql,[id])
    if len(user) == 0:
        response.status_code = 404
        return {'msg':'user not found'}
    user = convert_to_user(user[0])
    return user

@router.post('/')
async def add_user(user:UserDto,response:Response):
    # name,phone,username,email,password,client_id,is_owner
    sql = f'call prc_add_user(%s,%s,%s,%s,%s,%s,%s)'
    [data] = await db_query(sql,[user.name,user.phone,user.username,user.email,user.password,user.client_id,user.is_owner])
    result = data[0]
    code = data[1]
    response.status_code = code
    return {"msg":result}

@router.put('/{id}')
async def update_user(id:int,user:UserDto):
    sql = f'start transaction;update {TBL_USERS} set name=%s where id=%s;commit;'
    await db_query(sql,[user.name,id])
    return {'msg':'user updated successfully'}

@router.delete('/{id}')
async def delete_user(id:int):
    sql = f'delete from {TBL_USERS} where id=%s'
    await db_query(sql,[id])
    return {'msg':'user deleted successfully'}


