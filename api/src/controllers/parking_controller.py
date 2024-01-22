from fastapi import Response
from fastapi.routing import APIRouter
from db import db_query
from constants import TBL_PARKINGS,VIEW_MY_PARKINGS
from dto.models import ParkingDto
from utils.converters import convert_to_my_parking,convert_to_parking

router = APIRouter(prefix='/api/parkings' , tags=['parkings'])

@router.get('/filter')
async def filter_parking(userId:str,items:int = 10,page:int = 1):
    sql = f'select * from {VIEW_MY_PARKINGS} where user_id=%s order by entry_time desc limit %s,%s'
    rows = await db_query(sql,[userId , (page - 1) * items,items])
    parkings = []
    for row in rows:
        parkings.append(convert_to_my_parking(row))
    return parkings

@router.get('/')
async def get_parkings():
    sql = f'select * from {TBL_PARKINGS}'
    rows = await db_query(sql)
    parkings = []
    for row in rows:
        parkings.append(convert_to_parking(row))
    return parkings

@router.get('/{id}')
async def get_parking(id:int,response:Response):
    sql = f'select * from {TBL_PARKINGS} where id=%s'
    parking = await db_query(sql,[id])
    if len(parking) == 0:
        response.status_code = 404
        return {'msg':'Parking not found'}
    parking = convert_to_parking(parking[0])
    return parking

@router.post('/')
async def add_parking(parking:ParkingDto,response:Response):
    # vehicle_number,user_id,client_id,show_id
    sql = f'call prc_add_parking(%s,%s,%s,%s,%s)'
    [data] = await db_query(sql,[parking.vehicle_number,parking.user_id,parking.client_id,parking.show_id,parking.amount])
    result = data[0]
    code = data[1]
    response.status_code = code
    return {"msg":result}

@router.put('/{id}')
async def update_parking(id:int,parking:ParkingDto):
    sql = f'start transaction;update {TBL_PARKINGS} set name=%s where id=%s;commit;'
    await db_query(sql,[parking.name,id])
    return {'msg':'Parking updated successfully'}

@router.delete('/{id}')
async def delete_parking(id:int):
    sql = f'delete from {TBL_PARKINGS} where id=%s'
    await db_query(sql,[id])
    return {'msg':'Parking deleted successfully'}


