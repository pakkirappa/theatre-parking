from fastapi.routing import APIRouter
from fastapi import Response
from db import db_query
from constants import TBL_SHOWS
from dto.models import ShowDto
from utils.converters import convert_to_show

router = APIRouter(prefix='/api/shows' , tags=['shows'])

@router.get('/')
async def get_shows():
    sql = f'select * from {TBL_SHOWS}'
    rows = await db_query(sql)
    shows = []
    for row in rows:
        shows.append(convert_to_show(row))
    return shows

@router.get('/{id}')
async def get_show(id:int,response:Response):
    sql = f'select * from {TBL_SHOWS} where id=%s'
    show = await db_query(sql,[id])
    if len(show) == 0:
        response.status_code = 404
        return {'msg':'show not found'}
    show = convert_to_show(show[0])
    return show

@router.post('/')
async def add_show(show:ShowDto,response:Response):
    sql = f'call prc_add_show(%s,%s)'
    [data] = await db_query(sql,[show.name,show.client_id])
    result = data[0]
    code = data[1]
    response.status_code = code
    return {"msg":result}

@router.put('/{id}')
async def update_show(id:int,show:ShowDto):
    sql = f'start transaction;update {TBL_SHOWS} set name=%s where id=%s;commit;'
    await db_query(sql,[show.name,id])
    return {'msg':'show updated successfully'}

@router.delete('/{id}')
async def delete_show(id:int):
    sql = f'delete from {TBL_SHOWS} where id=%s'
    await db_query(sql,[id])
    return {'msg':'show deleted successfully'}


