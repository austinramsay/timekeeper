
package com.austinramsay.timekeeperserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author austinramsay
 */
public class Authenticator {
 
 
    private final String CLIENT_IP;
    
    
    
    public Authenticator(String CLIENT_IP)
    {
        this.CLIENT_IP = CLIENT_IP;
    }
    
    
    
    /**
     * Compares user password to stored database password <br>
     * Verifies user is connecting from a well known IP address <br
     * @param username The client username
     * @param hashed_pwd The user's hashed password
     * @return True if user is fully verified, False if password/IP address doesn't match
     */
    public boolean wasAuthenticated(String username, String hashed_pwd)
    {
        return (comparePassword(username, hashed_pwd));
    }
    
    
    
    /**
     * Fetches stored password from the database <br>
     * Compares passwords and returns boolean value true if matched <br>
     * @return Boolean true if correct, false if incorrect
     */
    public boolean comparePassword(String username, String hashed_pwd)
    {
        String command = "SELECT password FROM accounts WHERE username=?";
        String stored_pwd; 
        
        
        Connection dbConnection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        
        
        try 
        {
            dbConnection = getDBconnection();
            
            // Set the SQL command to use the sent username
            statement = dbConnection.prepareStatement(command);
            statement.setString(1, username);
            result = statement.executeQuery();
            
            
            // If a result is returned, set our stored_pwd equal to the result
            // If no result is returned, return false
            if (result.first())
                stored_pwd = result.getString(1);
            else
                return false;

            
        }
        catch (SQLException e)
        {
            // TODO: Log the thrown exception
            System.out.printf("Client %s: Failed to retrieve stored password.%n", CLIENT_IP);
            return false;
        }
        finally 
        {
            try {
                if (result != null)
                    result.close();
                if (statement != null)
                    statement.close();
                if (dbConnection != null)
                    dbConnection.close();
            } catch (SQLException e) { System.out.printf("Client %s: Failed to close database connection upon fetching password.", CLIENT_IP); }
        }        
        
        
        /*
        // Now that we retrieved the stored password from the database, let's compare the hashed passwords
        */
        boolean matched = hashed_pwd.equals(stored_pwd);
        
        
        /*
        // Prefer not to keep information stored
        */
        username = null;
        stored_pwd = null;
        hashed_pwd = null;
        
        
        /*
        // Return our boolean for a matched password couple
        */
        return matched;
    }    
    
    
        
    /**
     * @return A connection to the Time Keeper database
     */
    private Connection getDBconnection()
    {
        try 
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection dbConnection = DriverManager.getConnection("jdbc:mysql://" + TimeKeeperServer.dbIpAddr + ":3306/" + TimeKeeperServer.dbName + "?verifyServerCertificate=false&useSSL=true", TimeKeeperServer.dbUsername, TimeKeeperServer.dbPassword);
            return dbConnection;
        }
        catch (ClassNotFoundException e)
        {
            // TODO: Log the thrown exception
            System.out.println("Failed to load JDBC driver.");
            return null;
        }
        catch (SQLException e)
        {
            // TODO: Log the thrown exception.
            System.out.printf("Client %s: SQL Exception occured when fetching connection to database.", CLIENT_IP);
            e.printStackTrace();
            return null;
        }
    }    
}
