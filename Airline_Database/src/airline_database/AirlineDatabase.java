/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airline_database;
import java.sql.*;

/**
 *
 * @author Michael
 */
public class AirlineDatabase {    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Connection conn;
        String urlString = "jdbc:mysql://localhost:3306/mysql";
        //String driver = "com.mysql.jdbc.Driver";
        
        try{
            //Class.forName(driver);
            conn = DriverManager.getConnection(urlString, "root", "admin");
            conn.createStatement().executeQuery("use airlines");
            System.out.println("Successfully Connected to " + urlString);
            AirlineQueries database = new AirlineQueries(conn);
            
            database.beginTransactionProcessing();
            createInterface(database);
        }
        catch (Exception e){
            e.printStackTrace();
            System.err.println("Exception in getLocalConeection() "+e.getMessage());
            System.exit(-1);
        }
    }
    
    public static void createInterface(AirlineQueries database){
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainAirlineWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainAirlineWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainAirlineWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainAirlineWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        MainAirlineWindow airlineFrontend = new MainAirlineWindow(database);
        MainMenuPanel mainMenuPanel = new MainMenuPanel(database, airlineFrontend);
        airlineFrontend.setVisible(true);
    }
}
