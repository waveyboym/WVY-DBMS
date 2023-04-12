package org.openjfx;

import java.sql.ResultSet;
import java.sql.SQLException;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.JSONValue;

public class Controller {
    private final Database databaseinstance;
    enum parseType {
        FILMCOUNT, 
        LASTCREATEDMOVIE, 
        LASTCREATEDCLIENT, 
        GENRES, 
        STAFF, 
        STOREADDRESS, 
        FILMS
    }

    public Controller(){this.databaseinstance = new Database();}

    public void closeDatabase(){this.databaseinstance.close();}

    public String getDashBoardValues(){
        return "{" +
            "\"filmCount\": ["+ this.jsonParseResults(this.queryDBNoVal("SELECT COUNT(*) FROM film;"), parseType.FILMCOUNT) +"]," +
            "\"lastCreatedMovie\": ["+ 
                this.jsonParseResults(
                    this.queryDBNoVal("SELECT film.title, film.length, film.release_year, film.rating, category.name "+
                        "FROM ((category " +
                        "INNER JOIN film_category ON category.category_id = film_category.category_id) " +
                        "INNER JOIN film ON film.film_id = film_category.film_id) " +
                        "ORDER BY film.film_id DESC LIMIT 1;"),
                    parseType.LASTCREATEDMOVIE) 
                +"]," +
            "\"lastCreatedClient\": [" + 
                this.jsonParseResults(
                    this.queryDBNoVal("SELECT first_name, last_name, email, active FROM customer ORDER BY customer_id DESC LIMIT 1;"), 
                    parseType.LASTCREATEDCLIENT) 
                + "]," +
            "\"genres\": ["+ 
                this.jsonParseResults(
                    this.queryDBNoVal("SELECT name FROM category ORDER BY RANDOM() LIMIT 3;"), 
                    parseType.GENRES)
                + "]}";
    }

    public String getAllStaff(){ 
        return "{\"staff\": [" + 
                    this.jsonParseResults(
                        this.queryDBNoVal("SELECT * FROM ((city INNER JOIN address ON city.city_id = address.city_id) " +
                        "INNER JOIN staff ON staff.address_id = address.address_id) ORDER BY staff.staff_id ASC LIMIT 20;"), 
                    parseType.STAFF)
                    +"]}";
    }

    public String searchStaff(String attribute){
        return "{\"staff\": [" + 
                    this.jsonParseResults(
                        this.queryDBNoVal("SELECT * FROM ((city INNER JOIN address ON city.city_id = address.city_id) " +
                        "INNER JOIN staff ON staff.address_id = address.address_id) " +
                        "WHERE staff.first_name ILIKE '%"+ attribute +"%'" + 
                        " OR staff.last_name ILIKE '%"+ attribute +"%'" + 
                        " ORDER BY staff.staff_id ASC LIMIT 20;"), 
                    parseType.STAFF)
                    +"]}";
    }

    public String getAllFilms(){
        return "{\"films\": [" +
            this.jsonParseResults(
                this.queryDBNoVal("SELECT * FROM film ORDER BY film_id DESC LIMIT 15;"),
                parseType.FILMS)               
        + "]}";
    }

    private ResultSet queryDBNoVal(String stmnt){
        this.databaseinstance.PrepareStatement(stmnt);
        return this.databaseinstance.getResults();
    }

    private String jsonParseResults(ResultSet results, parseType ENUMVAL){
        try{
            if(ENUMVAL == parseType.FILMCOUNT && results != null && results.next()){
                return Integer.toString(results.getInt(1));
            }
            else if(ENUMVAL == parseType.LASTCREATEDMOVIE && results != null && results.next()){
                String data = "{";
                data += "\"title\": \"" + results.getString("title") + "\",";
                data += "\"length\": \"" + results.getString("length") + "\",";
                data += "\"genre\": \"" + results.getString("name") + "\",";
                data += "\"year\": " + results.getString("release_year") + ",";
                data += "\"rating\": \"" + results.getString("rating") + "\"";
                return data +"}";
            }
            else if(ENUMVAL == parseType.LASTCREATEDCLIENT && results != null && results.next()){
                String data = "{";
                data += "\"first_name\": \"" + results.getString("first_name") + "\",";
                data += "\"last_name\": \"" + results.getString("last_name") + "\",";
                data += "\"email\": \"" + results.getString("email") + "\",";
                data += "\"active\": " + results.getString("active");
                return data +"}";
            }
            else if(ENUMVAL == parseType.GENRES && results != null && results.next()){
                String data = "\"" + results.getString("name") + "\"";
                while (results.next())data += ",\"" + results.getString("name") + "\"";
                return data +"";
            }
            else if(ENUMVAL == parseType.STAFF && results != null && results.next()){
                String data = "{";
                data += "\"first_name\": \"" + results.getString("first_name") + "\",";
                data += "\"last_name\": \"" + results.getString("last_name") + "\",";
                data += "\"address\": \"" + results.getString("address") + "\",";
                data += "\"address2\": \"" + results.getString("address2") + "\",";
                data += "\"district\": \"" + results.getString("district") + "\",";
                data += "\"city\": \"" + results.getString("city") + "\",";
                data += "\"pocode\": \"" + results.getString("postal_code") + "\",";
                data += "\"phone\": " + results.getString("phone") + ",";
                data += "\"workplaceAddress\": "
                + this.jsonParseResults(
                    this.queryDBNoVal(
                            "SELECT address.address FROM address INNER JOIN store ON address.address_id = store.address_id " +
                            "WHERE store.store_id = "+ results.getString("store_id") +" LIMIT 1;"),
                        parseType.STOREADDRESS) + ",";
                data += "\"activeStatus\": \"" + results.getString("active") + "\"";
                data += "}";

                while(results.next()){
                    data += ",{\"first_name\": \"" + results.getString("first_name") + "\",";
                    data += "\"last_name\": \"" + results.getString("last_name") + "\",";
                    data += "\"address\": \"" + results.getString("address") + "\",";
                    data += "\"address2\": \"" + results.getString("address2") + "\",";
                    data += "\"district\": \"" + results.getString("district") + "\",";
                    data += "\"city\": \"" + results.getString("city") + "\",";
                    data += "\"pocode\": \"" + results.getString("postal_code") + "\",";
                    data += "\"phone\": " + results.getString("phone") + ",";
                    data += "\"workplaceAddress\": "
                    + this.jsonParseResults(
                        this.queryDBNoVal(
                                "SELECT address.address FROM address INNER JOIN store ON address.address_id = store.address_id " +
                                "WHERE store.store_id = "+ results.getString("store_id") +" LIMIT 1;" ),
                        parseType.STOREADDRESS)+ ",";
                    data += "\"activeStatus\": \"" + results.getString("active") + "\"";
                    data += "}";
                }

                return data;
            }
            else if(ENUMVAL == parseType.STOREADDRESS && results != null && results.next()){
                return "\""+ results.getString("address") +"\"";
            }
            else if(ENUMVAL == parseType.FILMS && results != null && results.next()){
                String data = "{";
                data += "\"title\": \"" + results.getString("title") + "\",";
                data += "\"length\": \"" + results.getString("length") + "\",";
                data += "\"year\": \"" + results.getString("release_year") + "\",";
                data += "\"rating\": \"" + results.getString("rating") + "\"";
                data += "}";

                while(results.next()){
                    data += ",{\"title\": \"" + results.getString("title") + "\",";
                    data += "\"length\": \"" + results.getString("length") + "\",";
                    data += "\"year\": \"" + results.getString("release_year") + "\",";
                    data += "\"rating\": \"" + results.getString("rating") + "\"";
                    data += "}";
                }

                return data;
            }
            else return "[\"ServerError1\"]";
        }
        catch(SQLException E){
            System.out.println("Error: Controller.java:69 : " + E.getMessage() + " " + ENUMVAL); 
            return "[\"ServerError2\"]";
        }
    }
}
