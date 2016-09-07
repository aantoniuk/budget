CREATE TABLE CURRENCY (
ID INTEGER NOT NULL AUTO_INCREMENT,
NAME VARCHAR(3) NOT NULL,
VALUE NUMBER(19,2) NOT NULL,
PRIMARY KEY (ID));

CREATE TABLE CATEGORY (
ID INTEGER NOT NULL AUTO_INCREMENT,
NAME VARCHAR(255) NOT NULL,
PARENT_ID INTEGER DEFAULT NULL,
PRIMARY KEY (ID));