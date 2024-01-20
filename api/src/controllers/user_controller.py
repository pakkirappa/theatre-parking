from fastapi.routing import APIRouter
from fastapi import Response
from db import db_query
from constants import TBL_USERS
from dto.models import UserDto,LoginDto
from utils.converters import convert_to_user

router = APIRouter(prefix='/users' , tags=['users'])

@router.post('/login')
async def get_users(loginDto:LoginDto,response:Response):
    sql = f'select * from {TBL_USERS} where username = %s OR email = %s'
    user = await db_query(sql)
    if user != loginDto.password:
        response.status_code = 400
        return {'msg':'invalid password'}
    token = '1234567890'
    return {'token':token,'msg':'User logged in successfully','id':user.id}

@router.get('/')
async def get_users():
    sql = f'select * from {TBL_USERS}'
    rows = await db_query(sql)
    users = []
    for row in rows:
        users.append(convert_to_user(row))
    return users

@router.get('/{id}')
async def get_show(id:int,response:Response):
    sql = f'select * from {TBL_USERS} where id=%s'
    user = await db_query(sql,[id])
    if len(user) == 0:
        response.status_code = 404
        return {'msg':'user not found'}
    user = convert_to_user(user[0])
    return user

@router.post('/')
async def add_show(user:UserDto,response:Response):
    # name,phone,username,email,password,client_id,is_owner
    sql = f'call prc_add_user(%s,%s,%s,%s,%s,%s,%s)'
    [data] = await db_query(sql,[user.name,user.phone,user.username,user.email,user.password,user.client_id,user.is_owner])
    result = data[0]
    code = data[1]
    response.status_code = code
    return {"msg":result}

@router.put('/{id}')
async def update_client(id:int,user:UserDto):
    sql = f'start transaction;update {TBL_USERS} set name=%s where id=%s;commit;'
    await db_query(sql,[user.name,id])
    return {'msg':'user updated successfully'}

@router.delete('/{id}')
async def delete_client(id:int):
    sql = f'delete from {TBL_USERS} where id=%s'
    await db_query(sql,[id])
    return {'msg':'user deleted successfully'}


