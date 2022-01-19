DROP TABLE FLIGHT_CLASS;
DROP TABLE FLIGHT;
DROP TABLE PLANE;
DROP TABLE PLANE_TYPE_CLASS;
DROP TABLE PLANE_TYPE;
DROP TABLE CITY;

CREATE TABLE CITY (
      ID SERIAL NOT NULL,
      NAME VARCHAR(50) NOT NULL,
      DISTANCE_TO_PARIS DECIMAL(10, 2) NOT NULL,
      FUEL_COST DECIMAL(10, 2) NOT NULL,
      CONSTRAINT CST_PK_CITY PRIMARY KEY (ID)
);

CREATE TABLE PLANE_TYPE (
      ID SERIAL NOT NULL,
      NAME VARCHAR(50) NOT NULL,
      SALE_PRICE DECIMAL(10, 2) NOT NULL,
      MAINTENANCE_COST DECIMAL(10, 2) NOT NULL,
      CONSTRAINT CST_PK_PLANE_TYPE PRIMARY KEY (ID)
);

CREATE TABLE PLANE_TYPE_CLASS (
    ID SERIAL NOT NULL,
    NAME VARCHAR(50) NOT NULL,
    COUNT_TOTAL_PLACES NUMERIC(3) NOT NULL,
    PLANE_TYPE_ID INT8 NOT NULL,
    CONSTRAINT CST_PK_PLANE_TYPE_CLASS PRIMARY KEY (ID),
    CONSTRAINT CST_FK_PLANE_TYPE_CLASS_PLANE_TYPE_ID FOREIGN KEY (PLANE_TYPE_ID) REFERENCES PLANE_TYPE(ID)
);

CREATE TABLE PLANE (
    ID SERIAL NOT NULL,
    PLANE_TYPE_ID INT8 NOT NULL,
    CONSTRAINT CST_PK_PLANE PRIMARY KEY (ID),
    CONSTRAINT CST_FK_PLANE_PLANE_TYPE_ID FOREIGN KEY (PLANE_TYPE_ID) REFERENCES PLANE_TYPE(ID)
);

CREATE TABLE FLIGHT (
   ID SERIAL NOT NULL,
   START_DATE TIMESTAMP NOT NULL,
   ARRIVAL_DATE TIMESTAMP NOT NULL,
   FLOOR_PRICE DECIMAL(10, 2) NOT NULL,
   START_CITY_ID INT8 NOT NULL,
   ARRIVAL_CITY_ID INT8 NOT NULL,
   PLANE_ID INT8 NOT NULL,
   CONSTRAINT CST_PK_FLIGHT PRIMARY KEY (ID),
   CONSTRAINT CST_FK_FLIGHT_START_CITY_ID FOREIGN KEY (START_CITY_ID) REFERENCES CITY(ID),
   CONSTRAINT CST_FK_FLIGHT_ARRIVAL_CITY_ID FOREIGN KEY (ARRIVAl_CITY_ID) REFERENCES CITY(ID),
   CONSTRAINT CST_FK_FLIGHT_PLANE_ID FOREIGN KEY (PLANE_ID) REFERENCES PLANE(ID)
);

CREATE TABLE FLIGHT_CLASS (
    ID SERIAL NOT NULL PRIMARY KEY,
    FLOOR_PLACE_PRICE DECIMAL(10, 2) NOT NULL,
    PLACE_PRICE DECIMAL(10, 2) NOT NULL,
    COUNT_AVAILABLE_PLACES NUMERIC(3) NOT NULL,
    COUNT_OCCUPIED_PLACES NUMERIC(3) NOT NULL,
    FLIGHT_ID INT8 NOT NULL,
    CONSTRAINT CST_FK_FLIGHT_CLASS_FLIGHT_ID FOREIGN KEY (FLIGHT_ID) REFERENCES FLIGHT(ID)
);