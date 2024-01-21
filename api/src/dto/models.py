from pydantic import BaseModel

class ClientDto(BaseModel):
    name: str

class ShowDto(BaseModel):
    name: str
    client_id:int

class UserDto(BaseModel):
    name: str
    phone: str
    username: str
    email: str
    password: str
    client_id: int
    is_owner: bool = False

class LoginDto(BaseModel):
    username: str
    password: str

class ParkingDto(BaseModel):
    vehicle_number: str
    user_id: int
    client_id: int
    show_id: int
    amount: int = 20