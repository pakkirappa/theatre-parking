from fastapi import FastAPI,Request
from fastapi.middleware.gzip import GZipMiddleware 
from fastapi.middleware.cors import CORSMiddleware
import uvicorn
from constants import WELCOME_MESSAGE,HOST,PORT,SERVERS
# Importing all the routers
from controllers.client_controller import router as client_router
from controllers.show_controller import router as show_router
from controllers.user_controller import router as user_router
from controllers.parking_controller import router as parking_router

app = FastAPI(title='T-Parking',description='Api for the T-Parking',version='1.0.0',servers=SERVERS)
app.add_middleware(GZipMiddleware , minimum_size=1000) # compresses the response if size is greater than 1000 bytes (1kb)
app.add_middleware(CORSMiddleware , allow_origins=['*'],allow_credentials=True,allow_methods=['*'],allow_headers=['*']) # allows all origins to access the api

@app.middleware('http')
async def check_token(request:Request,call_next):
    response = await call_next(request)
    response.headers['X-Powered-By'] = 'Young Minds Technology Solutions Pvt Ltd'
    return response

app.include_router(client_router)
app.include_router(show_router)
app.include_router(user_router)
app.include_router(parking_router)

@app.get('/')
def index():
    return WELCOME_MESSAGE


if __name__ == '__main__':
    uvicorn.run('app:app',host=HOST,port=PORT,reload=True)