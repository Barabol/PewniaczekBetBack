from fastapi import FastAPI
from src.controllers.userRouter import userRouter
from src.controllers.betRouter import betRouter
from starlette.middleware.sessions import SessionMiddleware

app = FastAPI()

app.add_middleware(SessionMiddleware, secret_key = "test", max_age = 60*60*8)

app.include_router(userRouter)
app.include_router(betRouter)
