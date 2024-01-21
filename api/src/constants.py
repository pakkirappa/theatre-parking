import os

WELCOME_MESSAGE = {
    'msg' : 'Welcome To The Api Of T-Parking',
    'status' : 200,
}

TBL_CLIENTS = 'clients'
TBL_PARKINGS = 'parkings'
TBL_USERS = 'users'
TBL_SESSIONS = 'sessions'
TBL_SHOWS = 'shows'
VIEW_MY_PARKINGS = 'view_my_parkings'


HOST = os.environ.get('HOST', '0.0.0.0')
PORT = os.environ.get('PORT', 80)
DB_HOST = os.environ.get('DB_HOST', 'localhost')
DB_PORT = os.environ.get('DB_PORT', 3306)
DB_USER = os.environ.get('DB_USER', 'root')
DB_PASSWORD = os.environ.get('DB_PASSWORD', 'root')
DB_NAME = os.environ.get('DB_NAME', 'parking_manager')


SERVERS = [
    {
        'url':'http://localhost',
        'description':'Local Server'
    },
    {
        'url':'https://tparking.herokuapp.com',
        'description':'Production Server'
    }
]