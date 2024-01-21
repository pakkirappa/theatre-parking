def convert_to_client(row):
    return {
        'id':row[0],
        'name':row[1],
        'total_users':row[2],
        'status':row[3],
    }

def convert_to_show(row):
    return {
        'id':row[0],
        'name':row[1],
        'client_id':row[2],
        'status':row[3],
    }

def convert_to_user(row):
    return {
        'id':row[0],
        'name':row[1],
        'show_id':row[2],
        'status':row[3],
    }

def convert_to_parking(row):
    return {
        'id':row[0],
        'entry_time' : row[1],
        'exit_time' : row[2],
    }

def convert_to_my_parking(row):
    return {
        'entry_time' : row[0],
        'exit_time' : row[1],
        'time_spent' : row[2],
        'vehicle_number' : row[3],
        'user_id' : row[4],
        'amount' : row[5],
    }