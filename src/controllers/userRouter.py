from typing import List, Union
from fastapi import APIRouter, Request, Response 

from src.models.userModel import UserCreationInfo, UserCredentials, UserDetails, UserInfo, getUserDetails, provideFollow, provideFollowDelete, provideFollowed, provideFollowers, provideLogin, provideLogout, provideRegister, provideUserDetails, provideUserInfo

userRouter = APIRouter(prefix="/user",tags=["user"])

@userRouter.post("/login",description="login route")
def login(response: Response, request: Request, credentials: UserCredentials) -> str:
    return provideLogin(response,request,credentials)

@userRouter.post("/register",description="register route")
def register(response: Response, info: UserCreationInfo) -> str:
    return provideRegister(response,info)

@userRouter.get("/logout",description="logout route")
def userLogout(request: Request):
    return provideLogout(request)

@userRouter.get("/info/{userId}",description="returns user info of provided user<br>if account is private returns only non public information if private user does not follow you")
def userInfo(request: Request, response: Response, userId:int) -> Union[str,UserInfo]:
    return provideUserInfo(request,response, userId)

@userRouter.post("/follow",description="follow user with provided id")
def userFollow(request: Request,response: Response, userId: int) -> str:
    return provideFollow(request,response,userId)

@userRouter.delete("/follow/{userId}",description="unfollow user with provided id")
def userFollowDelete(request: Request,response: Response, userId: int) -> str:
    return provideFollowDelete(request,response,userId)

@userRouter.get("/followers",description="returns list of all of your followers")
def userGetFollowers(request: Request,response:Response) -> Union[List[int],str]:
    return provideFollowers(request,response)

@userRouter.get("/followed",description="returns list of all users that follow you")
def userGetFollowed(request: Request,response:Response) -> Union[List[int],str]:
    return provideFollowed(request,response)

@userRouter.get("/details",description="returns user details of yourself")
def userGetDetails(request: Request,response:Response) -> Union[UserDetails,str]:
    return provideUserDetails(request,response)

@userRouter.post("/changeVisibility")
def userChangeVisibility(request:Request,response: Response,value:bool) -> str:
    return "unimplemented"
