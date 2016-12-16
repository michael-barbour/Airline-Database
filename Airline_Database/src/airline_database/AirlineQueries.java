package airline_database;

import java.sql.*;
/**
 *
 * @author Michael, Joshua
 * 
 * Airline Queries
 * 
 * Preconditions:  Connection to SQL server
 * 
 * This class contains all of the queries, updates, inserts, deletes
 * to the SQL server used by the various forms.
 * 
 * Each query will return a string that will either be put into a text pane or text area
 */
public class AirlineQueries {
    private final Connection conn;
    
    /**
     * Constructor
     * @author Michael barbour
     * 
     * @param conn: Connection to an SQL Server
     * 
     * Constructor:  Initializes class variable conn (connection) to passed variable conn.
     */
    public AirlineQueries(Connection conn){
        this.conn = conn;
    }
    
    /**
     * beginTransactionProcessing
     * 
     * @author Michael
     * 
     * Begins Transaction Processing.  Called when Transaction processing is beginning.
     *      all transactions will be recorded to be rolled back or saved when either:
     *          saveTransactions() or
     *          rollbackTransactions()
     *      are called.
     */
    public void beginTransactionProcessing(){
        try{
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("begin");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //Michael
    public void saveTransactions(){
        try{
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("commit");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //Michael
    public void rollbackTransactions(){
        try{
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("rollback");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
        
    //Michael
    public void quit(){    
        try{
            conn.close();
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println("Exception in getLocalConeection() "+e.getMessage());
            System.exit(-1);
        }
        
        System.exit(0);
    }
    
    //Michael/Joshua
    public String printFlightSchedule(String userFlightNum) {
        String query = "select * from Flight_Leg where Flight_Number = " +
            userFlightNum + " order by Scheduled_Departure_Time";
        String textArea = "Flight_Number\t" +
            "Leg_Number\t"+
            "Departure_Airport_Code\t" +
            "Scheduled_Departure_Time\t" +
            "Arrival_Airport_Code\t" +
            "Arrival_Time\n";

        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println(rs);
            while(rs.next()){
                textArea += rs.getString("Flight_Number") + "\t";
                textArea += rs.getString("Leg_Number") + "\t";
                textArea += rs.getString("Departure_Airport_Code") + "\t\t";
                textArea += rs.getString("Scheduled_Departure_Time") + "\t\t";
                textArea += rs.getString("Arrival_Airport_Code") + "\t\t";
                textArea += rs.getString("Arrival_Time") + "\n";
            }
            return textArea;
        }
        catch(Exception e){
            e.printStackTrace();
            return e.toString();
        }
    }

    //Joshua
    public String updateFares(String userFlightNum, String Fare, String Amount, String Restrictions) {
        String query = "update Fares set Amount = " 
		+ Amount + ", Restrictions = '" + Restrictions + "' where Flight_Number = "
                + userFlightNum + " and Fare_Code = " + Fare;

        try{
            Statement stmt = conn.createStatement();
	    stmt.executeUpdate(query);
            return("Completed");
        }
        catch(Exception e){
            e.printStackTrace();
            return("Failed to update");
        }
    }

    //Joshua
    public String addAirport(String Code, String Name, String City, String State) {
        //String query = "insert into Airport values ('" + Code 
		//+ "','" + Name + "','" + City + "','" + State + "');";
        
        String procedureCall = "{call Add_Airport('" + Code + "','" 
                + Name + "','" + City + "','" + State + "')}";

        try{
            //Statement stmt = conn.createStatement();
            CallableStatement cstmnt = null;
            
            cstmnt = conn.prepareCall(procedureCall);
            cstmnt.executeUpdate(procedureCall);
            
	    //stmt.executeUpdate(query);
            
            return("Completed");
        }
        catch(Exception e){
            e.printStackTrace();
            return("Failed to update");
        }
    }
    
    //Joshua
    public String dropFlight(String userFlightNum) {
        String query = "delete from Flight where Flight_Number = " + userFlightNum;
        //String query2 = "delete from Fares where Flight_Number = " + userFlightNum;

        try{
            Statement stmt = conn.createStatement();
	    stmt.executeUpdate(query);
            //stmt.executeUpdate(query2);
            return("Completed");
        }
        catch(Exception e){
            e.printStackTrace();
            return("Failed to update");
        }
    }

    //Joshua
    public String addFlight(String userFlightNum, String Airline, String Day) {
        String query = "insert into Flight values (" + userFlightNum 
		+ ",'" + Airline + "','" + Day + "');";
        String query2 = "insert into Fares values (" + userFlightNum 
                + ", 1, 700.00, 'none');";

        try{
            Statement stmt = conn.createStatement();
	    stmt.executeUpdate(query);
            stmt.executeUpdate(query2);
            return("Completed");
        }
        catch(Exception e){
            e.printStackTrace();
            rollbackTransactions();
            return("Failed to update");
        }
    }

    //Joshua
    public String printFlightPerformanceReportDelayed(String Airline) {
        String query = "select Flight_Leg.Flight_Number,Flight_Leg.Leg_Number, "
                + "Leg_Instance.Adate from Leg_Instance join "
                + "(Flight_Leg natural join Flight) where Flight_Leg.Flight_Number "
                + "= Leg_Instance.Flight_Number and Flight_Leg.Scheduled_Departure_Time "
                + "!= Leg_Instance.Departure_Time and Airline = '" + Airline + "'";
        String textArea = "Flight_Number\t" + 
        "Leg_Number\t" +
	"Date\n";

        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println(rs);
            while(rs.next()){
                textArea += rs.getString("Flight_Number") + "\t";
		textArea += rs.getString("Leg_Number") + "\t";
                textArea += rs.getString("ADate") + "\n";
            }
            return textArea;
        }
        catch(Exception e){
            e.printStackTrace();
            return e.toString();
        }
    }

    //Joshua
    public String printFlightPerformanceReportOnTime(String Airline) {
        String query = "select Flight_Leg.Flight_Number,Flight_Leg.Leg_Number, "
                + "Leg_Instance.Adate from Leg_Instance join (Flight_Leg natural join Flight)"
                + " where Flight_Leg.Flight_Number = Leg_Instance.Flight_Number and "
                + "Flight_Leg.Scheduled_Departure_Time = Leg_Instance.Departure_Time and Airline = '" +
	Airline + "'";
        String textArea = "Flight_Number\t" + 
        "Leg_Number\t" +
	"Date\n";

        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println(rs);
            while(rs.next()){
                textArea += rs.getString("Flight_Number") + "\t";
		textArea += rs.getString("Leg_Number") + "\t";
                textArea += rs.getString("ADate") + "\n";
            }
            return textArea;
        }
        catch(Exception e){
            e.printStackTrace();
            return e.toString();
        }
    }
    
    //Joshua
    public String createLegInstance(String userFlightNum, String userLegNum, 
            String date, String userNumSeats, String airplaneID, 
            String depAirportCode, String depTime, String arrivalAirportCode, 
            String arTime) {
        String query = "insert into Flight_Leg values(" + userFlightNum + ", " +
                userLegNum + ", '" + depAirportCode + "', '" + depTime + "', '" +
                arrivalAirportCode + "', '" + arTime + "');";
        
        String query2ElectricBoogaloo = "insert into Leg_Instance values (" + userFlightNum 
		+ "," + userLegNum + ",'" + date + "'," + userNumSeats + "," 
		+ airplaneID + ",'" + depAirportCode + "','" + depTime + "','" 
		+ arrivalAirportCode + "','" + arTime + "');";

        try{
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
	    stmt.executeUpdate(query2ElectricBoogaloo);
            return("Completed");
        }
        catch(Exception e){
            e.printStackTrace();
            return("Failed to update");
        }
    }
    
    //Joshua
    public String updateLegInstance(String userFlightNum, String userLegNum, 
            String date, String userNumOfSeats, String airplaneID, 
            String depAirportCode, String depTime, String arrivalAirportCode, 
            String arTime) {
        String query = "update Leg_Instance set Number_Of_Available_Seats = " 
                + userNumOfSeats + ", Adate = '" + date + "', Airplane_ID = " 
                + airplaneID + ", Departure_Airport_Code = '" + depAirportCode 
                + "', Departure_Time = '" + depTime + "', Arrival_Airport_Code = '" 
                + arrivalAirportCode + "', Arrival_Time = '" 
                + arTime + "' where Leg_Number = " + userLegNum 
                + " and Flight_Number = " + userFlightNum;


        try{
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            return("Completed");
        }
        catch(Exception e){
            e.printStackTrace();
            return("Failed to update");
        }
    }
    
    //Joshua
    public String printFlightRoster(String Code) {
        String query = "select Flight_Number, Leg_Number, ADate from Flight_Roster where Flight_Number = '" 
            + Code + "'";
        String textArea = "Flight_Number\t" + 
            "Leg_Number\t" +
            "Date\n";

        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println(rs);
            while(rs.next()){
                textArea += rs.getString("Flight_Number") + "\t";
		textArea += rs.getString("Leg_Number") + "\t";
                textArea += rs.getString("ADate") + "\n";
            }
            System.out.print(textArea);
            return textArea;
        }
        catch(Exception e){
            e.printStackTrace();
            return e.toString();
        }
    }
    
    //Joshua
    public String makeReservation(String userFlightNumber,String userLegNumber, 
            String Date, String userSeat,
            String customerName, String customerPhoneNumber) {           
        String query1 = "insert into Seat_Reservation values(" + userFlightNumber 
                + "," + userLegNumber + ",'" + Date + "','" + userSeat 
                + "','" + customerName + "','" + customerPhoneNumber + "');";

        try{
            Statement stmt = conn.createStatement();
	    stmt.executeUpdate(query1);
            return("Completed");
        }
        catch(Exception e){
            e.printStackTrace();
            rollbackTransactions();
            return("Failed to update");
        }
    }
    
    
    public String cancelReservation(String flightNumber, String legNumber,
            String customerName, String customerNumber) {           //Q 2 Prescott 
        String query = "Delete From Seat_Reservation Where"
                + " Flight_Number = " + flightNumber
                + " and Leg_Number = " + legNumber
                + " and Customer_Name = '" + customerName + "'"
                + " and Customer_Phone = '" + customerNumber + "'";
        
        try{
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            return("Completed") ;
        }
        catch(Exception e){
            e.printStackTrace();
            return("Failed to update");
        }
    }
    
    public String printFlightItinerary(String Customer_Name, String Customer_Phone) {           //Q 3 Prescott
        String query = "SELECT Flight_Number, Departure_Airport_Code,"
                + " Arrival_Airport_Code, Seat_Number, Rdate  FROM flight_leg "
                + "NATURAL JOIN (Select customer_Name,  Seat_Number, Rdate from "
                + "Seat_Reservation where Customer_Phone = '"+ Customer_Phone 
                + "' AND Customer_Name = '" + Customer_Name + "') "
                + "AS Specific_Seat GROUP BY Specific_Seat.Customer_Name";
        String textArea = "Flight_Number\t" +
        "Departure_Airport_Code\t" +
        "Arrival_Airport_Code\t" +
        "Seat_Number\t" +
        "Rdate\n";

        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println(rs);
            while(rs.next()){
                textArea += rs.getString("Flight_Number") + "\t";
                textArea += rs.getString("Departure_Airport_Code") + "\t\t";
                textArea += rs.getString("Arrival_Airport_Code") + "\t\t";
                textArea += rs.getString("Seat_Number") + "\t";
                textArea += rs.getString("Rdate") + "\n";
            }
            return textArea;
        }
        catch(Exception e){
            e.printStackTrace();
            quit();
            return e.toString();
        }
    }
    
    public String confirmReservation(String userFlightNum, String userLegNum, String userNumSeats) {           //Q 4  Prescott
        String query = "SELECT Customer_Name, Departure_Airport_Code, Arrival_Airport_Code,"
                + " Seat_Number, Rdate  FROM flight_leg NATURAL JOIN "
                + "(Select customer_Name,  Seat_Number, Rdate from Seat_Reservation"
                + " where Seat_number = " + userNumSeats + " AND Flight_Number = " 
                + userFlightNum + " and Leg_Number = " + userLegNum + ")" 
                + " AS Specific_Seat GROUP BY Specific_Seat.Customer_Name";
        String textArea = "Customer_Name\t" +
        "Departure_Airport_Code\t" +
        "Arrival_Airport_Code\t" +
        "Seat_Number\t" +
        "Rdate\n";

        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println(rs);
            while(rs.next()){
                textArea += rs.getString("Customer_Name") + "\t";
                textArea += rs.getString("Departure_Airport_Code") + "\t\t";
                textArea += rs.getString("Arrival_Airport_Code") + "\t\t";
                textArea += rs.getString("Seat_Number") + "\t\t";
                textArea += rs.getString("Rdate") + "\n";
            }
            return textArea;
        }
        catch(Exception e){
            e.printStackTrace();
            quit();
            return e.toString();
        }
    }
    
    
    public String locateFare(String userFlightNum, String fareCode) {           //Q 5  Prescott
        String query = "SELECT Flight_Number, Amount FROM Fares WHERE Flight_Number = " + userFlightNum 
                + " AND Fare_Code = " + fareCode;
        String textArea = "Flight_Number\t" + "Amount\n";

        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println(rs);
            while(rs.next()){
                textArea += rs.getString("Flight_Number") + "\t\t";    //begining of Flight_Leg information
                textArea += rs.getString("Amount") + "\n";
            }
            return textArea;
        }
        catch(Exception e){
            e.printStackTrace();
            quit();
            return e.toString();
        }
    }
}
