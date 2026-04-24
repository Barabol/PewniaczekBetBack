from datetime import datetime
from typing import Union

from pydantic import BaseModel


class SportBetObject(BaseModel):
    id: int
    bet_name: str
    game_name: str
    curent_multiplayer: float
    start_date: datetime
    stop_date: datetime
    team1: str
    team2: str
    team1_score: Union[None,int]
    team2_score: Union[None,int]
    sport: str
