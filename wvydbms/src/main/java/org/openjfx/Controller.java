package org.openjfx;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Controller {
    private final Database databaseinstance;
    enum parseType {
        FILMCOUNT, 
        LASTCREATEDMOVIE, 
        LASTCREATEDCLIENT, 
        GENRES, 
        STAFF, 
        STOREADDRESS, 
        FILMS,
        REPORT,
        CLIENTS,
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

    public String getAllStaff(){//YYY
        return "{\"staff\": [" + 
                    this.jsonParseResults(
                        this.queryDBNoVal("SELECT * FROM ((city INNER JOIN address ON city.city_id = address.city_id) " +
                        "INNER JOIN staff ON staff.address_id = address.address_id) ORDER BY staff.staff_id ASC LIMIT 20;"), 
                    parseType.STAFF)
                    +"]}";
    }

    public String searchStaff(String attribute){//YYY
        this.databaseinstance.PrepareStatement("SELECT * FROM ((city INNER JOIN address ON city.city_id = address.city_id) " +
                                                "INNER JOIN staff ON staff.address_id = address.address_id) " +
                                                "WHERE staff.first_name ILIKE ?" + 
                                                " OR staff.last_name ILIKE ? ORDER BY staff.staff_id ASC LIMIT 20;");
        
        this.databaseinstance.setValues(1, "%" + attribute + "%");
        this.databaseinstance.setValues(2, "%" + attribute + "%");

        return "{\"staff\": [" + this.jsonParseResults(this.databaseinstance.getResults(), parseType.STAFF) +"]}";
    }

    public String getAllFilms(){//YYY
        return "{\"films\": [" +
            this.jsonParseResults(
                this.queryDBNoVal("SELECT * FROM film ORDER BY film_id DESC LIMIT 24;"),
                parseType.FILMS)               
        + "]}";
    }

    public void AddNewFilm(String jsondata){//---???

    }

    public String getRecords(){//YYY
        return "{" +
            "\"records\": ["+ 
                this.jsonParseResults(
                    this.queryDBNoVal("SELECT COUNT(res.film_id), res.name, res.store_id FROM " +
                    "(SELECT film.film_id, inventory.store_id, category.name FROM (((category " +
                        "INNER JOIN film_category ON category.category_id = film_category.category_id) " +
                        "INNER JOIN film ON film.film_id = film_category.film_id) " +
                        "INNER JOIN inventory ON inventory.film_id = film.film_id) " + "LIMIT 100)" +
                        " as res GROUP BY res.store_id, res.name;"),
                    parseType.REPORT)
                    + "]}";
    }

    public String getAllClients(){//YYY
        return "{" +
            "\"clients\": [" +
                this.jsonParseResults(
                    this.queryDBNoVal("SELECT customer_id, first_name, last_name, email, active FROM customer ORDER BY customer_id DESC LIMIT 24;"),
                    parseType.CLIENTS)   
            + "]}"; 
    }

    public void deleteClient(int id){//??? ask tutor or lecturer idk
        //delete client from rental
        this.databaseinstance.PrepareStatement("DELETE FROM rental WHERE customer_id = ?;");
        this.databaseinstance.setIntValues(1, id);
        this.databaseinstance.ExecuteUpdate();
        //delete client from from payment
        this.databaseinstance.PrepareStatement("DELETE FROM payment WHERE customer_id = ?;");
        this.databaseinstance.setIntValues(1, id);
        this.databaseinstance.ExecuteUpdate();
        //delete client from customer
        this.databaseinstance.PrepareStatement("DELETE FROM customer WHERE customer_id = ?;");
        this.databaseinstance.setIntValues(1, id);
        this.databaseinstance.ExecuteUpdate();
    }

    public void updateClient(String jsondata){//----???
        JSONObject clientdata = (JSONObject)JSONValue.parse(jsondata);
        Long id = (Long)clientdata.get("id");
        String fname = (String)clientdata.get("first_name");
        String lname = (String)clientdata.get("last_name");
        String email = (String)clientdata.get("email");
        String active = (String)clientdata.get("active");
        Boolean gotFirstEl = false;
        int assignValCount = 0;
        if(fname == "" && lname == "" && email == "" && active == "")return;

        String updateSTMT = "UPDATE customer ";
        if(fname != "" && !gotFirstEl){
            gotFirstEl = true;
            updateSTMT += " SET first_name = ?";
            ++assignValCount;
        }
        else if(fname != "" && gotFirstEl){updateSTMT += " ,SET first_name = ?"; ++assignValCount;}

        if(lname != "" && !gotFirstEl){
            gotFirstEl = true;
            updateSTMT += " SET last_name = ?";
            ++assignValCount;
        }
        else if(lname != "" && gotFirstEl){updateSTMT += " ,SET last_name = ?"; ++assignValCount;}

        if(email != "" && !gotFirstEl){
            gotFirstEl = true;
            updateSTMT += " SET email = ?";
            ++assignValCount;
        }
        else if(email != "" && gotFirstEl){updateSTMT += " ,SET email = ?"; ++assignValCount;}

        if(active != "" && !gotFirstEl){
            gotFirstEl = true;
            updateSTMT += " SET active = ?";
            ++assignValCount;
        }
        else if(active != "" && gotFirstEl){updateSTMT += " ,SET active = ?"; ++assignValCount;}

        updateSTMT += " WHERE customer_id = ?;";
        if(assignValCount == 0)return;

        this.databaseinstance.PrepareStatement(updateSTMT);
        for(int i = 1; i <= assignValCount; ++i){
            if(fname != "")this.databaseinstance.setValues(i, fname);
            else if(lname != "")this.databaseinstance.setValues(i, lname);
            else if(email != "")this.databaseinstance.setValues(i, email);
            else if(active != "")this.databaseinstance.setValues(i, active);
        }

        this.databaseinstance.setIntValues(assignValCount + 1, id.intValue());
        this.databaseinstance.ExecuteUpdate();
    }

    public void addNewClient(String jsondata){//----???

    }

    public String searchForClient(String attribute){//YYY
        this.databaseinstance.PrepareStatement("SELECT customer_id, first_name, last_name, email, active FROM customer " +
            "WHERE first_name ILIKE ? OR last_name ILIKE ? OR email ILIKE ? ORDER BY customer_id DESC LIMIT 24;");
        this.databaseinstance.setValues(1, "%" + attribute + "%");
        this.databaseinstance.setValues(2, "%" + attribute + "%");
        this.databaseinstance.setValues(3, "%" + attribute + "%");
        return "{\"clients\": [" + this.jsonParseResults(this.databaseinstance.getResults(), parseType.CLIENTS) + "]}";
    }

    public String droppedRentalSub(){//YYY
        return "{" +
            "\"clients\": [" +
                this.jsonParseResults(
                    this.queryDBNoVal("SELECT customer_id, first_name, last_name, email, active FROM customer WHERE active = 0 ORDER BY customer_id DESC LIMIT 24;"),
                    parseType.CLIENTS)
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
            else if(ENUMVAL == parseType.REPORT && results != null && results.next()){
                String data = "{";
                data += "\"count\": " + Integer.toString(results.getInt(1)) + ",";
                data += "\"genre\": \"" + results.getString("name") + "\",";
                data += "\"workplaceAddress\": "
                + this.jsonParseResults(
                    this.queryDBNoVal(
                            "SELECT address.address FROM address INNER JOIN store ON address.address_id = store.address_id " +
                            "WHERE store.store_id = "+ results.getString("store_id") +" LIMIT 1;"),
                        parseType.STOREADDRESS);
                data += "}";

                while(results.next()){
                    data += ",{\"count\": " + Integer.toString(results.getInt(1)) + ",";
                    data += "\"genre\": \"" + results.getString("name") + "\",";
                    data += "\"workplaceAddress\": "
                    + this.jsonParseResults(
                        this.queryDBNoVal(
                                "SELECT address.address FROM address INNER JOIN store ON address.address_id = store.address_id " +
                                "WHERE store.store_id = "+ results.getString("store_id") +" LIMIT 1;"),
                            parseType.STOREADDRESS);
                    data += "}";
                }

                return data;
            }
            else if(ENUMVAL == parseType.CLIENTS && results != null && results.next()){
                String data = "{";
                data += "\"customer_id\": \"" + results.getString("customer_id") + "\",";
                data += "\"first_name\": \"" + results.getString("first_name") + "\",";
                data += "\"last_name\": \"" + results.getString("last_name") + "\",";
                data += "\"email\": \"" + results.getString("email") + "\",";
                data += "\"active\": " + results.getString("active");
                data += "}";

                while(results.next()){
                    data += ",{\"customer_id\": \"" + results.getString("customer_id") + "\",";
                    data += "\"first_name\": \"" + results.getString("first_name") + "\",";
                    data += "\"last_name\": \"" + results.getString("last_name") + "\",";
                    data += "\"email\": \"" + results.getString("email") + "\",";
                    data += "\"active\": " + results.getString("active");
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
