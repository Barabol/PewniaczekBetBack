from fastapi import FastAPI
from src.controllers.userRouter import userRouter
from src.controllers.betRouter import betRouter
from starlette.middleware.sessions import SessionMiddleware

app = FastAPI(description="API for internal use <U>only</U><br>Copyright: <B>PewniaczekBet 2026<B>",title="PewniaczekBet LLC.")

app.add_middleware(SessionMiddleware, secret_key = "test", max_age = 60*60*8)

app.include_router(userRouter)
app.include_router(betRouter)
