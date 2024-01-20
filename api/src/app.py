from fastapi import FastAPI
from fastapi.middleware.gzip import GZipMiddleware
from fastapi.middleware.cors import CORSMiddleware
import uvicorn
from constants import WELCOME_MESSAGE,HOST,PORT,SERVERS
from controllers.client_controller import router as client_router
from controllers.show_controller import router as show_router
from controllers.user_controller import router as user_router

app = FastAPI(title='T-Parking',description='Api for the T-Parking',version='1.0.0',servers=SERVERS)
app.add_middleware(GZipMiddleware , minimum_size=1000) # compresses the response if size is greater than 1000 bytes (1kb)
app.add_middleware(CORSMiddleware , allow_origins=['*'],allow_credentials=True,allow_methods=['*'],allow_headers=['*']) # allows all origins to access the api

app.include_router(client_router)
app.include_router(show_router)
app.include_router(user_router)

@app.get('/')
def index():
    return WELCOME_MESSAGE

if __name__ == '__main__':
    uvicorn.run('app:app',host=HOST,port=PORT,reload=True)