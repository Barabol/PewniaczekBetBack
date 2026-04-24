from datetime import datetime
from typing import List, Union
from fastapi import Request, Response, status
from pydantic import BaseModel

from src.models.userModel import UserDetails, getUserDetails
from src.etc.databaseCon import getConection

class PredictionObject(BaseModel):
    id: int
    name: str
    start_date: datetime
    stop_date: datetime
    true_bets: int
    false_bets: int
    pot: int
    ended_with: Union[bool,None]

def providePredictionBetGet() -> List[PredictionObject]:
    con = getConection()
    cursor = con.cursor()

    cursor.execute("SELECT id,name,start_date,stop_date,true_bets,false_bets,pot,ended_with FROM predictions WHERE stop_date > timestamp 'now';")
    res = cursor.fetchall()

    ret: List[PredictionObject] = []
    for x in res:
        ret.append(PredictionObject(id=x[0],name=x[1],start_date=x[2],stop_date=x[3],true_bets=x[4],false_bets=x[5],pot=x[6],ended_with=x[7]))

    return ret

def providePredictionBetGetAll() -> List[PredictionObject]:
    con = getConection()
    cursor = con.cursor()

    cursor.execute("SELECT id,name,start_date,stop_date,true_bets,false_bets,pot,ended_with FROM predictions;")
    res = cursor.fetchall()

    ret: List[PredictionObject] = []
    for x in res:
        ret.append(PredictionObject(id=x[0],name=x[1],start_date=x[2],stop_date=x[3],true_bets=x[4],false_bets=x[5],pot=x[6],ended_with=x[7]))

    return ret

class PredictionBetPlaceObejct(BaseModel):
    prediction: bool
    amount: int
    betId: int
    freeBetWallet: bool # is money taken form free bet wallet

def providePredictionPlace(request: Request,response:Response,bet: PredictionBetPlaceObejct) -> str:
    userId = request.session.get("user")

    if userId == None:
        response.status_code = status.HTTP_401_UNAUTHORIZED
        return "you must be logged in to use this function"

    if bet.amount < 100:
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "bet amount should be 100 (1 PLN) or greater"

    details: Union[UserDetails,None] = getUserDetails(userId)
    if details == None:
        response.status_code = status.HTTP_500_INTERNAL_SERVER_ERROR
        return "unable to get user details"

    con = getConection()
    cursor = con.cursor()

    cursor.execute("SELECT id,name,start_date,stop_date,true_bets,false_bets,pot,ended_with FROM predictions WHERE id = %s",(bet.betId,))

    ret = cursor.fetchone()

    if ret == None:
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "unable to find provided bet"

    ret = PredictionObject(id=ret[0],name=ret[1],start_date=ret[2],stop_date=ret[3],true_bets=ret[4],false_bets=ret[5],pot=ret[6],ended_with=ret[7])

    if ret.stop_date < datetime.now():
        response.status_code = status.HTTP_400_BAD_REQUEST
        return "provided bet is expired"
    
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

        cursor.execute("UPDATE predictions SET pot = pot + %s WHERE id = %s;",(bet.amount,bet.betId))
        if bet.prediction:
            cursor.execute("UPDATE predictions SET true_bets = true_bets + 1 WHERE id = %s;",(bet.betId,))
        else:
            cursor.execute("UPDATE predictions SET false_bets = false_bets + 1 WHERE id = %s;",(bet.betId,))

        cursor.execute("INSERT INTO user_predictions(user_id,prediction_id,predicted,amount) VALUES (%s,%s,%s,%s);",
                       (userId,bet.betId,bet.prediction,bet.amount))

    except Exception as e:
        cursor.execute("ROLLBACK")
        cursor.close()
        print(e)
        return "Internal server error ocured while creating bet"

    con.commit()
    cursor.close()

    return "OK"
