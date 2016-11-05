CREATE TABLE CURRENCY (
ID INTEGER NOT NULL AUTO_INCREMENT,
NAME VARCHAR(3) NOT NULL,
VALUE NUMBER(19,2) NOT NULL,
PRIMARY KEY (ID),
UNIQUE (NAME)
);

CREATE TABLE CATEGORY (
ID INTEGER NOT NULL AUTO_INCREMENT,
NAME VARCHAR(255) NOT NULL,
TYPE VARCHAR(8) NOT NULL,
ENABLE BOOLEAN DEFAULT TRUE,
PARENT_ID INTEGER DEFAULT NULL,
PRIMARY KEY (ID),
FOREIGN KEY (PARENT_ID) REFERENCES CATEGORY (ID),
UNIQUE (NAME, TYPE, PARENT_ID)
);

CREATE TABLE USER (
ID INTEGER NOT NULL AUTO_INCREMENT,
LOGIN VARCHAR(255) NOT NULL,
PASSWORD VARCHAR(255) NOT NULL,
ENABLE BOOLEAN DEFAULT TRUE,
PRIMARY KEY (ID),
UNIQUE (LOGIN)
);

CREATE TABLE USER_CATEGORY (
ID INTEGER NOT NULL AUTO_INCREMENT,
NAME VARCHAR(255) NOT NULL,
TYPE VARCHAR(8) NOT NULL,
ENABLE BOOLEAN DEFAULT TRUE,
PARENT_ID INTEGER DEFAULT NULL,
USER_ID INTEGER NOT NULL,
PRIMARY KEY (ID),
FOREIGN KEY (USER_ID) REFERENCES USER (ID),
UNIQUE (USER_ID, NAME, TYPE, PARENT_ID)
);

CREATE TABLE USER_CURRENCY (
ID INTEGER NOT NULL AUTO_INCREMENT,
ENABLE BOOLEAN DEFAULT TRUE,
CURRENCY_ID INTEGER NOT NULL,
USER_ID INTEGER NOT NULL,
FOREIGN KEY (USER_ID) REFERENCES USER (ID),
FOREIGN KEY (CURRENCY_ID) REFERENCES CURRENCY (ID),
PRIMARY KEY (ID),
UNIQUE (USER_ID, CURRENCY_ID)
);

CREATE TABLE WALLET (
ID INTEGER NOT NULL AUTO_INCREMENT,
NAME VARCHAR(255) NOT NULL,
ENABLE BOOLEAN DEFAULT TRUE,
USER_ID INTEGER NOT NULL,
CURRENCY_ID INTEGER NOT NULL,
FOREIGN KEY (USER_ID) REFERENCES USER (ID),
FOREIGN KEY (CURRENCY_ID) REFERENCES CURRENCY (ID),
PRIMARY KEY (ID),
UNIQUE (NAME, USER_ID, CURRENCY_ID)
);