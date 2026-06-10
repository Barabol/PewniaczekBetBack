DO $$ 
DECLARE
    r RECORD;
BEGIN
    FOR r IN (
        SELECT tablename 
        FROM pg_tables 
        WHERE schemaname = 'public'
    )
    LOOP
        EXECUTE 'DROP TABLE IF EXISTS public.' 
        || quote_ident(r.tablename) 
        || ' CASCADE';
    END LOOP;
END $$;

CREATE TABLE SPRING_SESSION (
    PRIMARY_ID CHAR(36) NOT NULL,
    SESSION_ID CHAR(36) NOT NULL,
    CREATION_TIME BIGINT NOT NULL,
    LAST_ACCESS_TIME BIGINT NOT NULL,
    MAX_INACTIVE_INTERVAL INT NOT NULL,
    EXPIRY_TIME BIGINT NOT NULL,
    PRINCIPAL_NAME VARCHAR(100),
    CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
);

CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE SPRING_SESSION_ATTRIBUTES (
    SESSION_PRIMARY_ID CHAR(36) NOT NULL,
    ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
    ATTRIBUTE_BYTES BYTEA NOT NULL,
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
);

CREATE TABLE account_types(
	id SERIAL PRIMARY KEY,
	name VARCHAR(30) UNIQUE
);

CREATE TABLE users(
	id SERIAL PRIMARY KEY,
	name VARCHAR(40),
	surname VARCHAR(40),
	email VARCHAR(64) UNIQUE,
	password VARCHAR(128),
	balance INTEGER,
	free_bet_balance INTEGER,
	wins INTEGER,
	losses INTEGER,
	wins_amount INTEGER,
	losses_amount INTEGER,
	is_public BOOLEAN,
	account_type_id Integer REFERENCES account_types(id)
);

CREATE TABLE teams(
	id SERIAL PRIMARY KEY,
	name VARCHAR(45) UNIQUE
);

CREATE TABLE sports(
	id SERIAL PRIMARY KEY,
	name VARCHAR(30) UNIQUE
);

CREATE TABLE games(
	id SERIAL PRIMARY KEY,
	team1_id Integer REFERENCES teams(id),
	team2_id Integer REFERENCES teams(id),
	sport_id Integer REFERENCES sports(id),
	team1_score INTEGER,
	team2_score INTEGER,
	start_date TIMESTAMP,
	name VARCHAR(80) UNIQUE
);

CREATE TABLE win_bets(
	id SERIAL PRIMARY KEY,
	name VARCHAR(80),
	curent_multiplyer FLOAT,
	stop_date TIMESTAMP,
	game_id Integer REFERENCES games(id)
);

CREATE TABLE score_bets(
	id SERIAL PRIMARY KEY,
	name VARCHAR(80),
	curent_multiplyer FLOAT,
	stop_date TIMESTAMP,
	game_id Integer REFERENCES games(id)
);

CREATE TABLE predictions(
	id SERIAL PRIMARY KEY,
	name VARCHAR(80),
	start_date TIMESTAMP,
	stop_date TIMESTAMP,
	true_bets INTEGER,
	false_bets INTEGER,
	true_bets_amount INTEGER,
	false_bets_amount INTEGER,
	pot INTEGER,
	ended_with BOOLEAN
);

CREATE TABLE user_win_bets(
	id SERIAL PRIMARY KEY,
	user_id Integer REFERENCES users(id),
	bet_id Integer REFERENCES win_bets(id),
	team_id Integer REFERENCES teams(id),
	multiplyer FLOAT,
	amount INTEGER
);

CREATE TABLE user_score_bets(
	id SERIAL PRIMARY KEY,
	user_id Integer REFERENCES users(id),
	bet_id Integer REFERENCES score_bets(id),
	team1_score INTEGER,
	team2_score INTEGER,
	multiplyer FLOAT,
	amount INTEGER
);

CREATE TABLE user_predictions(
	id SERIAL PRIMARY KEY,
	user_id Integer REFERENCES users(id),
	prediction_id Integer REFERENCES predictions(id),
	predicted BOOLEAN,
	amount INTEGER
);

CREATE TABLE updates_score(
	id SERIAL PRIMARY KEY,
	min_time TIMESTAMP,
	max_time TIMESTAMP,
	multiplyer FLOAT
);

CREATE TABLE updates_win(
	id SERIAL PRIMARY KEY,
	min_time TIMESTAMP,
	max_time TIMESTAMP,
	multiplyer FLOAT
);

CREATE TABLE followers(
	follower_id Integer REFERENCES users(id),
	followed_id Integer REFERENCES users(id),
	PRIMARY KEY (follower_id, followed_id)
);

CREATE TABLE oath_services(
	id SERIAL PRIMARY KEY,
	name varchar(64) UNIQUE
);

CREATE TABLE oath(
	id SERIAL PRIMARY KEY,
	user_id Integer REFERENCES users(id),
	token varchar(512) UNIQUE,
	service_id Integer REFERENCES oath_services(id),
	login varchar(128),
	url varchar(256),
	avatar_url varchar(256),
	email varchar(256),
	service_user_id Integer UNIQUE -- id użutkownika w serwisie github itd
);

CREATE TABLE payment_status(
	id SERIAL PRIMARY KEY,
	name varchar(64) UNIQUE
);

CREATE TABLE payments(
	id SERIAL PRIMARY KEY,
	sid varchar(512) UNIQUE,
	user_id Integer REFERENCES users(id),
	amount Integer,
	description varchar(1024),
	payment_date TIMESTAMP,
	status_id Integer REFERENCES payment_status(id)
);

-- <> WARTOŚCI PAYMENT STATUS <> --

INSERT INTO payment_status(id,name) VALUES 
(0,'unpaid'),
(1,'paid'),
(2,'no_payment_required'),
(3,'cancled')
ON CONFLICT (id) DO NOTHING;

-- <> WARTOŚCI PAYMENT STATUS <> --

-- <> WARTOŚCI ACOUNT TYPES <> --

INSERT INTO account_types(id,name) VALUES 
(0,'user'),
(1,'worker'),
(2,'admin')
ON CONFLICT (id) DO NOTHING;

-- <> WARTOŚCI ACOUNT TYPES <> --

-- <> WARTOŚCI OATH SERVICES <> --

INSERT INTO oath_services(id,name) VALUES 
(0,'github')
ON CONFLICT (id) DO NOTHING;

-- <> WARTOŚCI OATH SERVICES <> --

-- <> WARTOŚCI SPORTS<> --

INSERT INTO sports(id,name) VALUES 
(1,'piłka nożna'),
(2,'koszykówka'),
(3,'siatkówka'),
(4,'piłka ręczna'),
(5,'tenis'),
(6,'ping-pong')
ON CONFLICT (id) DO NOTHING;

-- <> WARTOŚCI SPORTS<> --


-- <> WARTOŚCI TEAMS <> --

INSERT INTO teams(id,name) VALUES 
(1,'fc-barcelona'),
(2,'real madryt'),
(3,'korona kielce'),
(4,'klub 2ring'),
(5,'psg')
ON CONFLICT (id) DO NOTHING;

-- <> WARTOŚCI TEAMS <> --


-- <> WARTOŚCI GAMES <> --

INSERT INTO games(id,name,team1_id,team2_id,sport_id,team1_score,team2_score,start_date) VALUES 
(1,'fc-barcelona vs real madryt 2026',1,2,6,null,null,'2026-04-25 15:15:30'),
(2,'fc-barcelona vs 2Ring 2026',1,4,3,2,15,'2026-04-15 15:15:30')
ON CONFLICT (id) DO NOTHING;

-- <> WARTOŚCI GAMES <> --


-- <> WARTOŚCI WIN_BETS <> --

INSERT INTO win_bets(id,name,curent_multiplyer,stop_date,game_id) VALUES 
(1,'ultimate ping-pong turnament',2.5,'2027-04-25 15:15:30',1),
(2,'2Ring vs fc-barcelona EZ win',1,'2026-04-16 15:15:30',2)
ON CONFLICT (id) DO NOTHING;

-- <> WARTOŚCI WIN_BETS <> --


-- <> WARTOŚCI PREDICTIONS <> --

INSERT INTO predictions (id,name,start_date,stop_date,true_bets,false_bets,pot,ended_with,true_bets_amount,false_bets_amount) VALUES 
(1,'czy trump podbije iran?','2026-04-16 15:15:30', '2027-04-16 15:15:30',0,0,0,null,0,0),
(2,'czy trump podbije grenlandie?','2026-01-16 15:15:30', '2025-03-16 15:15:30',0,0,0,true,0,0)
ON CONFLICT (id) DO NOTHING;

-- <> WARTOŚCI PREDICTIONS <> --
