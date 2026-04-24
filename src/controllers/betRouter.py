from typing import List, Union
from fastapi import APIRouter, Request, Response

from src.models.predictionBet import PredictionBetPlaceObejct, PredictionObject, providePredictionBetGet, providePredictionBetGetAll, providePredictionPlace
from src.models.scoreBetModel import ScoreBetPlaceObject, providePlaceScoreBet, provideScoreBetGet, provideScoreBetGetAll
from src.models.sportBetObject import SportBetObject
from src.models.winBetModel import WinBetPlaceObject, providePlaceWinBet, provideWinBetAmount, provideWinBetGet, provideWinBetGetAll

betRouter = APIRouter(prefix="/bet",tags=["bets"])

# Win bet routes
@betRouter.get("/win/curent",description="returns list of all win-bets that can be used")
def winBetGet(response: Response,sport:str="all") -> Union[str, List[SportBetObject]]:
    return provideWinBetGet(sport,response)

@betRouter.get("/win/all",description="returns list of all win-bets including old ones")
def winBetGetAll(response: Response,sport:str="all") -> Union[str, List[SportBetObject]]:
    return provideWinBetGetAll(sport,response)

@betRouter.post("/win/place",
                description="places bet for win bet, team € [0,2] <br> team = 0 - draw <br> tema = 1 - team1 <br> team = 2 - team2")
def placeWinBet(request: Request, response: Response, team:WinBetPlaceObject) -> Union[str, List[SportBetObject]]:
    return providePlaceWinBet(request,response,team)

@betRouter.get("/win/amount",description="returns amount of aviable bets of win category for provided sport")
def winBetGetAmount(response: Response,sport:str="all") -> Union[str, int]:
    return provideWinBetAmount(sport,response)

# Score bet routes
@betRouter.get("/score/curent",description="returns list of all score-bets that can be used")
def scoreBetGet(response: Response,sport:str="all") -> Union[str, List[SportBetObject]]:
    return provideScoreBetGet(sport,response)

@betRouter.get("/score/all",description="returns list of all score-bets including old ones")
def scoreBetGetAll(response: Response,sport:str="all") -> Union[str, List[SportBetObject]]:
    return provideScoreBetGetAll(sport,response)

@betRouter.post("/score/place",
                description="places bet for score bet")
def placeScoreBet(request: Request, response: Response, bet:ScoreBetPlaceObject) -> Union[str, List[SportBetObject]]:
    return providePlaceScoreBet(request,response,bet)

# Prediction market
@betRouter.get("/prediction/curent",description="returns list of all prediction-bets that can be used")
def predictionBetGet() ->  List[PredictionObject]:
    return providePredictionBetGet()

@betRouter.get("/prediction/all",description="returns list of all prediction-bets that can be used")
def predictionBetGetAll() ->  List[PredictionObject]:
    return providePredictionBetGetAll()

@betRouter.post("/prediction/place",description="places prediction")
def predictionBetPlace(request: Request,response:Response,bet: PredictionBetPlaceObejct) ->  str:
    return providePredictionPlace(request,response,bet)
