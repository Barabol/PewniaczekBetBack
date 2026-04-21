from typing import List, Union
from fastapi import APIRouter, Request, Response 

from src.models.userModel import UserCreationInfo, UserCredentials, UserInfo, provideFollow, provideFollowDelete, provideFollowed, provideFollowers, provideLogin, provideLogout, provideRegister, provideUserInfo

userRouter = APIRouter(prefix="/user")

@userRouter.post("/login")
def login(response: Response, request: Request, credentials: UserCredentials) -> str:
    return provideLogin(response,request,credentials)

@userRouter.post("/register")
def register(response: Response, info: UserCreationInfo) -> str:
    return provideRegister(response,info)

@userRouter.get("/logout")
def userLogout(request: Request):
    return provideLogout(request)

@userRouter.get("/info/{userId}")
def userInfo(request: Request, response: Response, userId:int) -> Union[str,UserInfo]:
    return provideUserInfo(request,response, userId)

@userRouter.post("/follow")
def userFollow(request: Request,response: Response, userId: int) -> str:
    return provideFollow(request,response,userId)

@userRouter.delete("/follow/{userId}")
def userFollowDelete(request: Request,response: Response, userId: int) -> str:
    return provideFollowDelete(request,response,userId)

@userRouter.get("/followers")
def userGetFollowers(request: Request,response:Response) -> Union[List[int],str]:
    return provideFollowers(request,response)

@userRouter.get("/followed")
def userGetFollowed(request: Request,response:Response) -> Union[List[int],str]:
    return provideFollowed(request,response)
