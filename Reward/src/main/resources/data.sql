INSERT INTO users (FIRST_NAME, LAST_NAME) VALUES ('John', 'Wick');
INSERT INTO users (FIRST_NAME, LAST_NAME) VALUES ('Howard', 'Stark');
INSERT INTO users (FIRST_NAME, LAST_NAME) VALUES ('Clark', 'Kent');

INSERT INTO accounts (ID, CUSTOMER_ID, AMOUNT) VALUES (1, 1, 20);
INSERT INTO accounts (ID, CUSTOMER_ID, AMOUNT) VALUES (2, 2, 580);

INSERT INTO transactions (ID, ACCOUNT_ID, AMOUNT, T_TYPE) VALUES (1, 1, 20, 'A');
INSERT INTO transactions (ID, ACCOUNT_ID, AMOUNT, T_TYPE, UPDATE_TIME) VALUES (2, 2, 110, 'A', DATEADD('MONTH',-4, CURRENT_TIMESTAMP()));
INSERT INTO transactions (ID, ACCOUNT_ID, AMOUNT, T_TYPE, UPDATE_TIME) VALUES (3, 2, 120, 'A', DATEADD('MONTH',-2, CURRENT_TIMESTAMP()));
INSERT INTO transactions (ID, ACCOUNT_ID, AMOUNT, T_TYPE, UPDATE_TIME) VALUES (4, 2, 50, 'A', DATEADD('DAY',-4, CURRENT_TIMESTAMP()));
INSERT INTO transactions (ID, ACCOUNT_ID, AMOUNT, T_TYPE, UPDATE_TIME) VALUES (5, 2, 70, 'A', DATEADD('DAY',-3, CURRENT_TIMESTAMP()));
INSERT INTO transactions (ID, ACCOUNT_ID, AMOUNT, T_TYPE, UPDATE_TIME) VALUES (6, 2, 100, 'A', DATEADD('HOUR',-2, CURRENT_TIMESTAMP()));
INSERT INTO transactions (ID, ACCOUNT_ID, AMOUNT, T_TYPE) VALUES (7, 2, 130, 'A');