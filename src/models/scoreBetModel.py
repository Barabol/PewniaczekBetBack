
from datetime import datetime
from typing import List, Union
from fastapi import Request, Response, status
from pydantic import BaseModel

from src.models.userModel import UserDetails, getUserDetails
from src.etc.databaseCon import getConection
from src.models.sportBetObject import SportBetObject
from src.etc.sportDict import getSportDict


def provideScoreBetGet(sport:str,response:Response) -> Union[str, List[SportBetObject]]:
    sql:str = ""
    if sport == "all":
        sql = """SELECT score_bets.id,score_bets.name,games.name,curent_multiplyer,
         start_date,stop_date,t1.name,t2.name,team1_score,team2_score,sports.name
         FROM score_bets JOIN games ON games.id = score_bets.game_id JOIN sports ON sports.id = games.sport_id
         JOIN teams AS t1 ON t1.id = games.team1_id JOIN teams AS t2 ON t2.id = games.team2_id WHERE stop_date >= timestamp 'now';"""
    else:
        sd = getSportDict()
        if sport not in sd:
            response.status_code = status.HTTP_400_BAD_REQUEST
            return "provided sport is not aviable"
        else:
            sql = """SELECT score_bets.id,score_bets.name,games.name,curent_multiplyer,
         start_date,stop_date,t1.name,t2.name,team1_score,team2_score,sports.name
         FROM score_bets JOIN games ON games.id = score_bets.game_id JOIN sports ON sports.id = games.sport_id
         JOIN teams AS t1 ON t1.id = games.team1_id JOIN teams AS t2 ON t2.id = games.team2_id WHERE stop_date >= timestamp 'now' AND sport_id = """ + sd[sport]


    con = getConection()
    cursor = con.cursor()
    cursor.execute(sql)
    ret = cursor.fetchall()
    cursor.close()
    print(ret)
    res:List[SportBetObject] = [SportBetObject(id = x[0],bet_name=x[1],game_name=x[2],curent_multiplayer=x[3],start_date=x[4],stop_date=x[5],team1=x[6],team2=x[7],team1_score=x[8],team2_score=x[9],sport=x[10]) for x in ret]
    return res

def provideScoreBetGetAll(sport:str,response:Response) -> Union[str, List[SportBetObject]]:
    sql:str = ""
    if sport == "all":
        sql = """SELECT score_bets.id,score_bets.name,games.name,curent_multiplyer,start_date,
         stop_date,t1.name,t2.name,team1_score,team2_score,sports.name
         FROM score_bets JOIN games ON games.id = score_bets.game_id JOIN sports ON sports.id = games.sport_id
         JOIN teams AS t1 ON t1.id = games.team1_id JOIN teams AS t2 ON t2.id = games.team2_id;"""
    else:
        sd = getSportDict()
        if sport not in sd:
            response.status_code = status.HTTP_400_BAD_REQUEST
            return "provided sport is not aviable"
        else:
            sql = """SELECT score_bets.id,score_bets.name,games.name,curent_multiplyer,start_date,
             stop_date,t1.name,t2.name,team1_score,team2_score,sports.name
             FROM score_bets JOIN games ON games.id = score_bets.game_id JOIN sports ON sports.id = games.sport_id
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

class ScoreBetPlaceObject(BaseModel):
    team1_score: int
    team2_score: int
    amount: int
    betId: int
    freeBetWallet: bool # is money taken form free bet wallet

class InternalBetInfo:
    multiplyer: float
    stop_date: datetime
    def __init__(self,multiplyer: float,stop_date:datetime) -> None:
        self.multiplyer = multiplyer
        self.stop_date = stop_date


def providePlaceScoreBet(request: Request, response: Response, bet:ScoreBetPlaceObject) -> str:
    userId = request.session.get("user")

    if userId == None:
        response.status_code = status.HTTP_401_UNAUTHORIZED
        return "you must be logged in to use this function"

    if bet.amount < 100:
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "bet amount should be 100 (1 PLN) or greater"

    if bet.team1_score < 0:
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "#1 team score cannot be lower than 0"

    if bet.team2_score < 0:
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "#2 team score cannot be lower than 0"

    details: Union[UserDetails,None] = getUserDetails(userId)
    if details == None:
        response.status_code = status.HTTP_500_INTERNAL_SERVER_ERROR
        return "unable to get user details"


    con = getConection()
    cursor = con.cursor()

    cursor.execute("SELECT curent_multiplyer,stop_date FROM score_bets WHERE id = %s;",
                   (bet.betId,))
    ret = cursor.fetchone()
    if ret == None:
        response.status_code = status.HTTP_400_BAD_REQUEST
        cursor.close()
        return "unable to find bet with provided id"

    ret = InternalBetInfo(ret[0],ret[1])

    if datetime.now() > ret.stop_date:
        cursor.close()
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "unable to place bet on expired bet"

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

        cursor.execute("INSERT INTO user_score_bets(user_id,bet_id,team1_score,team2_score,multiplyer,amount) VALUES (%s,%s,%s,%s,%s,%s);",
                       (userId,bet.betId,bet.team1_score,bet.team2_score,ret.multiplyer,bet.amount,))

    except Exception as e:
        print(e)
        cursor.execute("ROLLBACK")
        cursor.close()
        response.status_code = status.HTTP_500_INTERNAL_SERVER_ERROR
        return "Internal server error ocured while creating bet"

    con.commit()
    cursor.close()
    return "OK"
