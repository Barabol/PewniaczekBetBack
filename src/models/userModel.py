import re
from typing import List, Union
from fastapi import Request, Response, status
from pydantic import BaseModel
import bcrypt

from src.etc.databaseCon import getConection

class UserCredentials(BaseModel):

    email: str
    password: str

    def isValid(self) -> bool:
        if not re.fullmatch(r"^([a-zA-Z]|[0-9]|\.|\*|&|\$|@){6,64}$",self.email):
            print("mail")
            return False
        if not re.fullmatch(r"^[a-zA-Z]([a-zA-Z]|[0-9]|\.){5,29}$",self.password):
            print("password")
            return False
        return True

class UserCreationInfo(BaseModel):

    _id: int
    name: str
    surname: str
    email: str
    password: str

    def isValid(self) -> bool:
        print(self)
        if not re.fullmatch(r"[A-Z]([a-z]){3,24}",self.name):
            print("name")
            return False
        if not re.fullmatch(r"[A-Z]([a-z]){3,24}",self.surname):
            print("surname")
            return False
        if not re.fullmatch(r"^([a-zA-Z]|[0-9]|\.|\*|&|\$|@){6,64}$",self.email):
            print("mail")
            return False
        if not re.fullmatch(r"^[a-zA-Z]([a-zA-Z]|[0-9]|\.){5,29}$",self.password):
            print("password")
            return False
        return True

class UserInfo(BaseModel):
    name: str
    surname: str
    wins: int   #if public will be set to -1
    losses: int #if public will be set to -1
    is_public: bool

class UserDetails(BaseModel):
    name: str
    surname: str
    balance: int
    freeBetBalance:int
    wins: int  
    losses: int
    won_amount: int  
    lost_amount: int
    is_public: bool

def getUserDetails(userId:int)-> Union[UserDetails,None]:
    con = getConection()
    cursor = con.cursor()
    cursor.execute("SELECT name,surname,balance,free_bet_balance,wins,losses,wins_amount,losses_amount,is_public FROM users WHERE id = %s;",(userId,))
    res = cursor.fetchone()
    cursor.close()
    if res == None:
        return None
    return UserDetails(name=res[0],surname=res[1],balance=res[2],freeBetBalance=res[3],wins=res[4],losses=res[5],won_amount=res[6],lost_amount=res[7],is_public=res[8])

def provideLogin(response: Response, request: Request, credentials :UserCredentials) -> str:
    if not credentials.isValid():
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "bad email or password"

    con = getConection()
    cursor = con.cursor()
    cursor.execute("SELECT id,password FROM users WHERE email = %s;",(credentials.email,))
    res = cursor.fetchone()
    cursor.close()

    if res != None and bcrypt.checkpw(credentials.password.encode(), res[1].encode()):
        request.session['user'] = res[0]
        return "OK"
    response.status_code = status.HTTP_400_BAD_REQUEST
    return "bad email or password"

def provideRegister(response: Response, info: UserCreationInfo) -> str:
    # TODO: give free bets to new users
    if not info.isValid():
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "bad user informations"
    passwdHash = bcrypt.hashpw(info.password.encode(),bcrypt.gensalt())
    con = getConection()
    cursor = con.cursor()
    try:
        cursor.execute(f"INSERT INTO users(name,surname,email,password,free_bet_balance"
                       ",balance,wins,losses,wins_amount,losses_amount,is_public,account_type_id)"
                       " VALUES (%s,%s,%s,%s,%s,0,0,0,0,0,true,0);",
                       (info.name,info.surname,info.email,passwdHash.decode(),1000))
        con.commit()
    except Exception as e:
        print(e)
        cursor.execute("ROLLBACK")
        con.commit()
        cursor.close()
        return "this email is already taken"
    cursor.close()

    return "OK"

def provideLogout(request: Request):
    if request.session.get("user") != None:
        request.session.popitem()
        return "OK"
    return "OK?"

def provideFollow(request: Request, response: Response,followId: int)->str:

    userId = request.session.get("user")

    if userId == None:
        response.status_code = status.HTTP_401_UNAUTHORIZED
        return "you must be logged in to use this function"

    if userId == followId:
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "you cannot follow yourself"

    con = getConection()
    cursor = con.cursor()

    try:
        cursor.execute("INSERT INTO followers(follower_id, followed_id) VALUES (%s,%s);",(userId,followId))
    except Exception as e:
        cursor.execute("ROLLBACK")
        con.commit()
        cursor.close()
        print(e)
        return "unable to follow"
    con.commit()
    cursor.close()

    return "OK"

def provideFollowers(request: Request,response: Response) -> Union[List[int],str]:

    userId = request.session.get("user")

    if userId == None:
        response.status_code = status.HTTP_401_UNAUTHORIZED
        return "you must be logged in to use this function"
    con = getConection()
    cursor = con.cursor()
    cursor.execute("SELECT follower_id FROM followers WHERE followed_id = %s;",(userId,))
    res = cursor.fetchall()
    cursor.close()
    res = [int(x[0]) for x in res]
    return res

def provideFollowed(request: Request,response: Response) -> Union[List[int],str]:

    userId = request.session.get("user")

    if userId == None:
        response.status_code = status.HTTP_401_UNAUTHORIZED
        return "you must be logged in to use this function"
    con = getConection()
    cursor = con.cursor()
    cursor.execute("SELECT followed_id FROM followers WHERE follower_id = %s;",(userId,))
    res = cursor.fetchall()
    cursor.close()
    res = [int(x[0]) for x in res]
    return res

def provideUserInfo(request: Request, response: Response, userId:int) -> Union[str,UserInfo]:
    con = getConection()
    cursor = con.cursor()
    cursor.execute("SELECT name,surname,wins,losses,is_public FROM users WHERE id = %s;",(userId,))
    res = cursor.fetchone()
    cursor.close()
    if res == None:
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "this user does not exist"
    user = UserInfo(name=res[0],surname=res[1],wins=res[2],losses=res[3],is_public=res[4])
    if not user.is_public:
        selfId = request.session.get("user")
        if selfId == userId:
            return user
        else:
            cursor = con.cursor()
            cursor.execute("SELECT count(*) FROM followers WHERE follower_id = %s AND followed_id = %s;",(userId,selfId))
            res = cursor.fetchone()
            cursor.close()
            print(res)
            if res != None and res[0] != 0:
                return user
            else:
                user.wins = -1
                user.losses = -1
    return user

def provideFollowDelete(request: Request,response: Response,followId:int) -> str:

    userId = request.session.get("user")

    if userId == None:
        response.status_code = status.HTTP_401_UNAUTHORIZED
        return "you must be logged in to use this function"

    if userId == followId:
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "you cannot unfollow yourself"

    con = getConection()
    cursor = con.cursor()

    isDeleted = 0
    cursor.execute("DELETE FROM followers WHERE follower_id = %s AND followed_id = %s;",(userId,followId))
    isDeleted = cursor.rowcount
    con.commit()
    cursor.close()
    
    if isDeleted == 0:
        return "unable to unfollow not followed person"
    return "OK"   

def provideUserDetails(request: Request,response: Response) -> Union[UserDetails,str]:
    userId = request.session.get("user")

    if userId == None:
        response.status_code = status.HTTP_401_UNAUTHORIZED
        return "you must be logged in to use this function"

    details = getUserDetails(userId)
    if details == None:
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "somehow unable to find user"
    
    return details
