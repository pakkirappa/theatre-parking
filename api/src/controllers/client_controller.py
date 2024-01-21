from fastapi.routing import APIRouter
from fastapi import Response
from db import db_query
from constants import TBL_CLIENTS
from dto.models import ClientDto
from utils.converters import convert_to_client

router = APIRouter(prefix='/api/clients' , tags=['clients'])

@router.get('/')
async def get_clients():
    sql = f'select * from {TBL_CLIENTS}'
    rows = await db_query(sql)
    clients = []
    for row in rows:
        clients.append(convert_to_client(row))
    return clients

@router.get('/{id}')
async def get_client(id:int,response:Response):
    sql = f'select * from {TBL_CLIENTS} where id=%s'
    client = await db_query(sql,[id])
    if len(client) == 0:
        response.status_code = 404
        return {'msg':'client not found'}
    client = convert_to_client(client[0])
    return client

@router.post('/')
async def add_client(client:ClientDto,response:Response):
    sql = f'call prc_add_client(%s)'
    [data] = await db_query(sql,[client.name])
    result = data[0]
    code = data[1]
    response.status_code = code
    return {"msg":result}

@router.put('/{id}')
async def update_client(id:int,client:ClientDto):
    sql = f'start transaction;update {TBL_CLIENTS} set name=%s where id=%s;commit;'
    await db_query(sql,[client.name,id])
    return {'msg':'client updated successfully'}

@router.delete('/{id}')
async def delete_client(id:int):
    sql = f'delete from {TBL_CLIENTS} where id=%s'
    await db_query(sql,[id])
    return {'msg':'client deleted successfully'}


