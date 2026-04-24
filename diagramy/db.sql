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

CREATE TABLE account_types(
	id SERIAL PRIMARY KEY,
	name VARCHAR(30)
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
	name VARCHAR(45)
);

CREATE TABLE sports(
	id SERIAL PRIMARY KEY,
	name VARCHAR(30)
);

CREATE TABLE games(
	id SERIAL PRIMARY KEY,
	team1_id Integer REFERENCES teams(id),
	team2_id Integer REFERENCES teams(id),
	sport_id Integer REFERENCES sports(id),
	team1_score INTEGER,
	team2_score INTEGER,
	start_date TIMESTAMP,
	name VARCHAR(80)
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
	bet_id Integer REFERENCES win_bets(id),
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

-- <> WARTOŚCI ACOUNT TYPES <> --

INSERT INTO account_types(id,name) VALUES 
(0,'user'),
(1,'worker'),
(2,'admin')
ON CONFLICT (id) DO NOTHING;

-- <> WARTOŚCI ACOUNT TYPES <> --


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

INSERT INTO predictions (id,name,start_date,stop_date,true_bets,false_bets,pot,ended_with) VALUES 
(1,'czy trump podbije iran?','2026-04-16 15:15:30', '2027-04-16 15:15:30',0,0,0,null),
(2,'czy trump podbije grenlandie?','2026-01-16 15:15:30', '2025-03-16 15:15:30',0,0,0,true)
ON CONFLICT (id) DO NOTHING;

-- <> WARTOŚCI PREDICTIONS <> --
