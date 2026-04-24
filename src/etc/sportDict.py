from typing import Dict

from src.etc.databaseCon import getConection


_sportDict: Dict = {}

def getSportDict() -> Dict:
    return _sportDict

def reloadSportDict() -> None:
    con = getConection()
    cursor = con.cursor()
    cursor.execute("SELECT id,name FROM SPORTS;")
    ret = cursor.fetchall()
    slist = getSportDict()
    slist.clear()
    for x in ret:
        slist[x[1]] = str(x[0])
    print(slist.__len__())

reloadSportDict()
