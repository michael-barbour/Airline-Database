DROP TABLE IF EXISTS Can_Land;
DROP TABLE IF EXISTS Fares;
DROP TABLE IF EXISTS Leg_Instance;
DROP TABLE IF EXISTS Seat_Reservation;
DROP TABLE IF EXISTS Flight_Leg;
DROP TABLE IF EXISTS Airplane;
DROP TABLE IF EXISTS Airport;
DROP TABLE IF EXISTS Airplane_Type;
DROP TABLE IF EXISTS Flight;
DROP VIEW IF EXISTS Flight_Roster;
DROP FUNCTION IF EXISTS Seat_Warning;
DROP PROCEDURE IF EXISTS Show_Project;
DROP PROCEDURE IF EXISTS Add_Airport;


create table Flight
	(Flight_Number		varchar(5) NOT NULL,
	 Airline			varchar(20),
	 Weekday			varchar(9) NOT NULL,
	 primary key (Flight_Number)
	) engine=INNODB;
	
create table Airplane_Type
	(Type_Name		varchar(15) NOT NULL,
	 Max_Seats		int,
	 Company		varchar(10),
	 primary key (Type_Name)
	) engine=INNODB;
	
create table Airport
	(Airport_Code	varchar(3) NOT NULL,
	 Name			varchar(50),
	 City			varchar(15),
	 US_State		varchar(15),
	 primary key (Airport_Code)
	) engine=INNODB;
	
CREATE TABLE Airplane (
    Airplane_ID 			VARCHAR(5) NOT NULL,
    Total_Number_of_Seats 	INT,
    Airplane_Type 			VARCHAR(15) NOT NULL,
    PRIMARY KEY (Airplane_ID),
    FOREIGN KEY (Airplane_Type)
        REFERENCES Airplane_Type (Type_Name)
        ON DELETE CASCADE
)  engine=INNODB;
	
create table Flight_Leg
	(Flight_Number				varchar(5) NOT NULL,
	 Leg_Number					varchar(3) NOT NULL,
	 Departure_Airport_Code		varchar(4) NOT NULL,
	 Scheduled_Departure_Time	time,
	 Arrival_Airport_Code		varchar(4) NOT NULL,
	 Arrival_Time				time,
	 foreign key (Departure_Airport_Code) references Airport(Airport_Code) on delete CASCADE,
	 foreign key (Arrival_Airport_Code) references Airport(Airport_Code) on delete CASCADE,
	 foreign key (Flight_Number) references Flight(Flight_Number) on delete CASCADE,
	 constraint PK_Flight_Leg primary key (Flight_Number,Leg_Number) 
	) engine=INNODB;
	
create table Seat_Reservation
	(Flight_Number		varchar(5) NOT NULL,
	 Leg_Number			varchar(3) NOT NULL,
	 Rdate				date NOT NULL,
	 Seat_Number		varchar(3),
	 Customer_Name		varchar(20),
	 Customer_Phone		varchar(15),
	 foreign key (Flight_Number,Leg_Number) references Flight_Leg(Flight_Number,Leg_Number)  on delete CASCADE,
	 constraint PK_Seat_Reservation primary key (Flight_Number,Leg_Number,Rdate, Seat_Number)
	) engine=INNODB;

create table Leg_Instance
	(Flight_Number				varchar(5) NOT NULL,
	 Leg_Number					varchar(10) NOT NULL,
	 Adate						date NOT NULL,
	 Number_Of_Available_Seats	int,
	 Airplane_ID 				varchar(5) NOT NULL,
	 Departure_Airport_Code		varchar(4) NOT NULL,
	 Departure_Time				time,
	 Arrival_Airport_Code		varchar(4) NOT NULL,
	 Arrival_Time				time,
	 foreign key (Flight_Number,Leg_Number) references Flight_Leg(Flight_Number,Leg_Number) on delete CASCADE,
	 foreign key (Departure_Airport_Code) references Airport(Airport_Code) on delete CASCADE,
	 foreign key (Arrival_Airport_Code) references Airport(Airport_Code) on delete CASCADE,
	 constraint PK_Leg_Instance primary key (Flight_Number,Leg_Number,Adate)
	) engine=INNODB;
	
CREATE TABLE Fares (
    Flight_Number VARCHAR(5) NOT NULL,
    Fare_Code VARCHAR(1) NOT NULL,
    Amount double(7 , 2 ),
    Restrictions VARCHAR(10),
    FOREIGN KEY (Flight_Number)
        REFERENCES Flight (Flight_Number)
        ON DELETE CASCADE,
    CONSTRAINT PK_Fares PRIMARY KEY (Flight_Number , Fare_Code)
)  engine=INNODB;
	
CREATE TABLE Can_Land (
    Airplane_Type_Name VARCHAR(15) NOT NULL,
    Airport_Code VARCHAR(4),
    FOREIGN KEY (Airplane_Type_Name)
        REFERENCES Airplane_Type (Type_Name)
        ON DELETE CASCADE,
    FOREIGN KEY (Airport_Code)
        REFERENCES Airport (Airport_Code)
        ON DELETE CASCADE,
    CONSTRAINT PK_Can_Land PRIMARY KEY (Airplane_Type_Name , Airport_Code)
)  engine=INNODB;

create view Flight_Roster as
	select
		Leg_Instance.Flight_Number, Leg_Number, Airline, Departure_Airport_Code, Departure_Time, Adate
	from
		Leg_Instance, Flight
	where Leg_Instance.Flight_Number = Flight.Flight_Number
	order by Departure_Time;
	
create trigger Subtract_Seat
	after insert on Seat_Reservation
	for each row update Leg_Instance
	set Leg_Instance.Number_Of_Available_Seats = Leg_Instance.Number_Of_Available_Seats - 1
	where Leg_Instance.Flight_Number = NEW.Flight_Number and Leg_Instance.Leg_Number = NEW.Leg_Number;

create trigger Add_Seat
	after delete on Seat_Reservation
	for each row update Leg_Instance
	set Leg_Instance.Number_Of_Available_Seats = Leg_Instance.Number_Of_Available_Seats + 1
	where Leg_Instance.Flight_Number = OLD.Flight_Number and Leg_Instance.Leg_Number = OLD.Leg_Number;

DELIMITER $$

create FUNCTION Seat_Warning(Number_Of_Available_Seats int) returns varchar(10)
    deterministic
begin
    declare Warning varchar(10);

    IF (Number_Of_Available_Seats > 20 and Number_Of_Available_Seats <= 30) THEN
        set Warning = 'Caution';
    ELSEIF (Number_Of_Available_Seats > 10 and Number_Of_Available_Seats <= 20) THEN
        set Warning = 'Warning';
    ELSEIF (Number_Of_Available_Seats > 0 and Number_Of_Available_Seats <= 10) THEN
        set Warning = 'Stop';
    END IF;

RETURN (Warning);
END
$$

DELIMITER ;


DELIMITER $$

create PROCEDURE Show_Project()
begin
    describe Flight;
    describe Airport;
    describe Airplane_Type;
    describe Airplane;
    describe Flight_Leg;
    describe Leg_Instance;
    describe Seat_Reservation;
    describe Fares;
    describe Can_Land;
    describe view Flight_Roster;
end
$$

DELIMITER ;

DELIMITER $$

create PROCEDURE Add_Airport(Airport_Code varchar(3),Name varchar(50),City varchar(15),US_State varchar(15))
begin
    insert Airport values (Airport_Code, Name, City, US_State);
    #select * from Airport;
end;
$$

DELIMITER ;