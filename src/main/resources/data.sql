INSERT INTO users (first_name, last_name) VALUES ('John', 'Wick');
INSERT INTO users (first_name, last_name) VALUES ('Howard', 'Stark');
INSERT INTO users (first_name, last_name) VALUES ('Clark', 'Kent');

INSERT INTO accounts (ID, USER_ID, AMOUNT) VALUES (1, 1, 20);
INSERT INTO accounts (ID, USER_ID, AMOUNT) VALUES (2, 2, 100);

INSERT INTO transactions (ID, ACCOUNT_ID, AMOUNT, T_TYPE, DELETED) VALUES (1, 1, 20, 'A', FALSE);
INSERT INTO transactions (ID, ACCOUNT_ID, AMOUNT, T_TYPE, DELETED) VALUES (2, 2, 100, 'A', FALSE);