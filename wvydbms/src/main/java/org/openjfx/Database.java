package org.openjfx;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Database {
    private static Connection conn;
    private static PreparedStatement query;

    public Database(){
        try{  
            InputStream input = Database.class.getClassLoader().getResourceAsStream("config.properties");
            if (input == null)throw new Exception("NullPointerException: Database.java:20(cannot find config file)");
            
            Properties prop = new Properties();
            prop.load(input);

            final String url = prop.getProperty("db.dvdrental_DB_PROTO") + ":" +
                       prop.getProperty("db.dvdrental_DB_SQL") +  "://" + 
                       prop.getProperty("db.dvdrental_DB_HOST") + ":" + 
                       prop.getProperty("db.dvdrental_DB_PORT") + "/" + 
                       prop.getProperty("db.dvdrental_DB_NAME");
            final String username = prop.getProperty("db.dvdrental_DB_USERNAME");
            final String password = prop.getProperty("db.dvdrental_DB_PASSWORD");

            conn = DriverManager.getConnection(url,username,password);
            System.out.println("Successful connection");
        }
        catch(Exception e){ System.out.println("Error with connection: " + e.getMessage()); }
    }

    public void close(){
        try{conn.close();}
        catch(Exception e){ System.out.println("Closing connection: " + e.getMessage());} 
    }

    public void PrepareStatement(String statement){
        if(conn == null)return;
        try{query = conn.prepareStatement(statement);}
        catch (SQLException e) {System.out.println("Error: Database.java:45 : " + e.getMessage());}
    }

    public void setValues(int Index, String value){
        if(conn == null)return;
        try{
            if(query == null)throw new Exception("NullPointerException: Database.java:46(query is null)");
            query.setString(Index, value);
        }
        catch(Exception e){ System.out.println("Error: Database.java:54 : " + e.getMessage());}
    }

    public void setIntValues(int Index, int value){
        if(conn == null)return;
        try{
            if(query == null)throw new Exception("NullPointerException: Database.java:60(query is null)");
            query.setInt(Index, value);
        }
        catch(Exception e){ System.out.println("Error: Database.java:63 : " + e.getMessage());}
    }

    public void setfloatValues(int Index, float value){
        if(conn == null)return;
        try{
            if(query == null)throw new Exception("NullPointerException: Database.java:60(query is null)");
            query.setFloat(Index, value);
        }
        catch(Exception e){ System.out.println("Error: Database.java:63 : " + e.getMessage());}
    }

    public void setBooleanValues(int Index, boolean value){
        if(conn == null)return;
        try{
            if(query == null)throw new Exception("NullPointerException: Database.java:60(query is null)");
            query.setBoolean(Index, value);
        }
        catch(Exception e){ System.out.println("Error: Database.java:63 : " + e.getMessage());}
    }
    
    public void setArrayValues(int Index, String[] value){
        if(conn == null)return;
        try{
            if(query == null)throw new Exception("NullPointerException: Database.java:60(query is null)");
            query.setArray(Index, conn.createArrayOf("TEXT", value));
        }
        catch(Exception e){ System.out.println("Error: Database.java:63 : " + e.getMessage());}
    }

    public ResultSet getResults(){
        if(conn == null)return null;
        try{ResultSet result = query.executeQuery(); return result;}
        catch(Exception e){ System.out.println("Error: Database.java:69 : " + e.getMessage()); return null;}
    }

    public boolean ExecuteUpdate(){
        if(conn == null)return false;
        try{query.executeUpdate(); return true;}
        catch(Exception e){ System.out.println("Error: Database.java:75 : "  +e.getMessage()); return false;}
    }
}
