from datetime import datetime
from typing import List, Union
from fastapi import Request, Response, status
from pydantic import BaseModel

from src.models.sportBetObject import SportBetObject
from src.models.userModel import UserDetails, getUserDetails
from src.etc.databaseCon import getConection
from src.etc.sportDict import getSportDict

def provideWinBetGet(sport:str,response:Response) -> Union[str, List[SportBetObject]]:
    sql:str = ""
    if sport == "all":
        sql = """SELECT win_bets.id,win_bets.name,games.name,curent_multiplyer,start_date,stop_date,t1.name,t2.name,team1_score,team2_score,sports.name
         FROM win_bets JOIN games ON games.id = win_bets.game_id JOIN sports ON sports.id = games.sport_id
         JOIN teams AS t1 ON t1.id = games.team1_id JOIN teams AS t2 ON t2.id = games.team2_id WHERE stop_date >= timestamp 'now';"""
    else:
        sd = getSportDict()
        if sport not in sd:
            response.status_code = status.HTTP_400_BAD_REQUEST
            return "provided sport is not aviable"
        else:
            sql = """SELECT win_bets.id,win_bets.name,games.name,curent_multiplyer,start_date,stop_date,t1.name,t2.name,team1_score,team2_score,sports.name
             FROM win_bets JOIN games ON games.id = win_bets.game_id JOIN sports ON sports.id = games.sport_id
             JOIN teams AS t1 ON t1.id = games.team1_id JOIN teams AS t2 ON t2.id = games.team2_id WHERE stop_date >= timestamp 'now' AND sport_id = """ + sd[sport]


    con = getConection()
    cursor = con.cursor()
    cursor.execute(sql)
    ret = cursor.fetchall()
    cursor.close()
    print(ret)
    res:List[SportBetObject] = [SportBetObject(id = x[0],bet_name=x[1],game_name=x[2],curent_multiplayer=x[3],start_date=x[4],stop_date=x[5],team1=x[6],team2=x[7],team1_score=x[8],team2_score=x[9],sport=x[10]) for x in ret]
    return res

def provideWinBetGetAll(sport:str,response:Response) -> Union[str, List[SportBetObject]]:
    sql:str = ""
    if sport == "all":
        sql = """SELECT win_bets.id,win_bets.name,games.name,curent_multiplyer,start_date,
         stop_date,t1.name,t2.name,team1_score,team2_score,sports.name
         FROM win_bets JOIN games ON games.id = win_bets.game_id JOIN sports ON sports.id = games.sport_id
         JOIN teams AS t1 ON t1.id = games.team1_id JOIN teams AS t2 ON t2.id = games.team2_id;"""
    else:
        sd = getSportDict()
        if sport not in sd:
            response.status_code = status.HTTP_400_BAD_REQUEST
            return "provided sport is not aviable"
        else:
            sql = """SELECT win_bets.id,win_bets.name,games.name,curent_multiplyer,start_date,
             stop_date,t1.name,t2.name,team1_score,team2_score,sports.name
             FROM win_bets JOIN games ON games.id = win_bets.game_id JOIN sports ON sports.id = games.sport_id
             JOIN teams AS t1 ON t1.id = games.team1_id JOIN teams AS t2 ON t2.id = games.team2_id 
             WHERE sport_id = """ + sd[sport]

    con = getConection()
    cursor = con.cursor()
    cursor.execute(sql)
    ret = cursor.fetchall()
    cursor.close()
    print(ret)
    res:List[SportBetObject] = [SportBetObject(id = x[0],bet_name=x[1],game_name=x[2],
                                           curent_multiplayer=x[3],start_date=x[4],stop_date=x[5],
                                           team1=x[6],team2=x[7],team1_score=x[8],team2_score=x[9],
                                           sport=x[10]) for x in ret]
    return res

class WinBetPlaceObject(BaseModel):
    team: int
    amount: int
    betId: int
    freeBetWallet: bool # is money taken form free bet wallet

class InternalBetInfo:
    team1_id: int
    team2_id: int
    multiplyer: float
    stop_date: datetime
    def __init__(self,team1_id: int,team2_id: int,multiplyer: float,stop_date:datetime) -> None:
        self.team1_id = team1_id
        self.team2_id = team2_id
        self.multiplyer = multiplyer
        self.stop_date = stop_date

    def __str__(self) -> str:
        return f"{self.team1_id} {self.team2_id} {self.multiplyer} {self.stop_date}"

def providePlaceWinBet(request: Request, response: Response, bet:WinBetPlaceObject) -> str:
    userId = request.session.get("user")

    if userId == None:
        response.status_code = status.HTTP_401_UNAUTHORIZED
        return "you must be logged in to use this function"

    if bet.amount < 100:
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "bet amount should be 100 (1 PLN) or greater"

    if bet.team not in (0,1,2):
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "team should be 0,1 or 2"

    details: Union[UserDetails,None] = getUserDetails(userId)
    if details == None:
        response.status_code = status.HTTP_500_INTERNAL_SERVER_ERROR
        return "unable to get user details"


    con = getConection()
    cursor = con.cursor()

    cursor.execute("SELECT team1_id,team2_id,curent_multiplyer,stop_date FROM win_bets JOIN games ON games.id = win_bets.game_id WHERE win_bets.id = %s;",
                   (bet.betId,))
    ret = cursor.fetchone()
    if ret == None:
        response.status_code = status.HTTP_400_BAD_REQUEST
        cursor.close()
        return "unable to find bet with provided id"

    ret = InternalBetInfo(ret[0],ret[1],ret[2],ret[3])
    if datetime.now() > ret.stop_date:
        cursor.close()
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "unable to place bet on expired bet"
    #print(ret)
    try: 
        if bet.freeBetWallet:
            if details.freeBetBalance < bet.amount:
                cursor.close()
                response.status_code = status.HTTP_400_BAD_REQUEST
                return "unable to bet higher amount than aviable"
            cursor.execute("UPDATE users SET free_bet_balance = free_bet_balance - %s WHERE id = %s;",(bet.amount,userId))

        else:
            if details.balance < bet.amount:
                cursor.close()
                response.status_code = status.HTTP_400_BAD_REQUEST
                return "unable to bet higher amount than aviable"
            cursor.execute("UPDATE users SET balance = balance - %s WHERE id = %s;",(bet.amount,userId))

        team_id = None

        if bet.team == 1:
            team_id = ret.team1_id

        elif bet.team == 2:
            team_id = ret.team2_id

        cursor.execute("INSERT INTO user_win_bets(user_id,bet_id,team_id,multiplyer,amount) VALUES (%s,%s,%s,%s,%s);",
                       (userId,bet.betId,team_id,ret.multiplyer,bet.amount))

    except Exception as e:
        cursor.execute("ROLLBACK")
        cursor.close()
        #print(e)
        return "Internal server error ocured while creating bet"

    con.commit()
    cursor.close()
    return "OK"


def provideWinBetAmount(sport:str ,response: Response) -> Union[str,int]:
    sql:str = ""
    if sport == "all":
        sql = "SELECT COUNT(id) FROM win_bets WHERE stop_date > timestamp 'now';"
    else:
        sd = getSportDict()
        if sport not in sd:
            response.status_code = status.HTTP_400_BAD_REQUEST
            return "provided sport is not aviable"
        else:
            sql = """SELECT COUNT(win_bets.id) FROM win_bets JOIN games on games.id = win_bets.game_id
            WHERE stop_date > timestamp 'now' AND sport_id = """ + sd[sport]

    con = getConection()
    cursor = con.cursor()
    cursor.execute(sql)
    ret = cursor.fetchone()
    cursor.close()
    if ret == None:
        response.status_code = status.WS_1011_INTERNAL_ERROR
        return "internal server error"
    return int(ret[0])
