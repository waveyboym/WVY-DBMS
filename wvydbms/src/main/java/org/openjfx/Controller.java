package org.openjfx;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class Controller {
    private final Database databaseinstance;
    private String reportJSON = "";
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

    public void prepareANDsendEmailToAllClients(String emailbodyJSON){
        JSONObject clientdata = (JSONObject)JSONValue.parse(emailbodyJSON);
        String emailbody = (String)clientdata.get("emailbody");
        this.databaseinstance.PrepareStatement("SELECT email FROM customer LIMIT 1;");
        ResultSet results = this.databaseinstance.getResults();
        try{
            while(results.next()){
                String email = results.getString("email");
                //send email with its body
                System.out.println("email: " + email);
                System.out.println("body: " + emailbody);
            }
        }
        catch(Exception e){System.out.println(e.getMessage()); return;}
        System.out.println("Successfully sent emails");
    }

    public void prepareReportPDF(){
        /*
        * $Id: Tables.java 3373 2008-05-12 16:21:24Z xlv $
        *
        * This code is free software. It may only be copied or modified
        * if you include the following copyright notice:
        *
        * --> Copyright 2001-2005 by G. Martinelli and Bruno Lowagie <--
        *
        * This code is part of the 'OpenPDF Tutorial'.
        * You can find the complete tutorial at the following address:
        * https://github.com/LibrePDF/OpenPDFtutorial/
        *
        * This code is distributed in the hope that it will be useful,
        * but WITHOUT ANY WARRANTY; without even the implied warranty of
        * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
        *
        *  
        */

        /**This code came from openpdf but was modified to suit my needs */

        Font font8 = FontFactory.getFont(FontFactory.HELVETICA, 8);
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("report.pdf"));
            float width = document.getPageSize().getWidth();
            document.open();

            float[] columnDefinitionSize = { 50F, 25F, 25F };
            PdfPTable table = null;
            PdfPCell cell = null;

            table = new PdfPTable(columnDefinitionSize);
            table.getDefaultCell().setBorder(0);
            table.setHorizontalAlignment(0);
            table.setTotalWidth(width - 72);
            table.setLockedWidth(true);

            cell = new PdfPCell(new Phrase("Report"));
            cell.setColspan(columnDefinitionSize.length);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Workplace address", font8));
            cell.setColspan(1);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Genre", font8));
            cell.setColspan(1);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Count", font8));
            cell.setColspan(1);
            table.addCell(cell);

            JSONObject reportobjArr = (JSONObject)JSONValue.parse(this.reportJSON);
            JSONArray reportarr = (JSONArray)reportobjArr.get("records");
            for (int i = 0 ; i < reportarr.size(); ++i) {
                JSONObject obj = (JSONObject) reportarr.get(i);
                String workplaceAddress = (String) obj.get("workplaceAddress");
                String genre = (String) obj.get("genre");
                Number count = (Number) obj.get("count");

                table.addCell(new Phrase(workplaceAddress, font8));
                table.addCell(new Phrase(genre, font8));
                table.addCell(new Phrase(count.toString(), font8));
            }

            document.add(table);
        }

        catch (DocumentException | IOException de) {System.err.println(de.getMessage());}
        // step 5
        document.close();
    }

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
                        "INNER JOIN staff ON staff.address_id = address.address_id) ORDER BY staff.staff_id ASC LIMIT 40;"), 
                    parseType.STAFF)
                    +"]}";
    }

    public String searchStaff(String attribute){//YYY
        this.databaseinstance.PrepareStatement("SELECT * FROM ((city INNER JOIN address ON city.city_id = address.city_id) " +
                                                "INNER JOIN staff ON staff.address_id = address.address_id) " +
                                                "WHERE staff.first_name ILIKE ?" + 
                                                " OR staff.last_name ILIKE ? ORDER BY staff.staff_id ASC LIMIT 40;");
        
        this.databaseinstance.setValues(1, "%" + attribute + "%");
        this.databaseinstance.setValues(2, "%" + attribute + "%");

        return "{\"staff\": [" + this.jsonParseResults(this.databaseinstance.getResults(), parseType.STAFF) +"]}";
    }

    public String getAllFilms(){//YYY
        return "{\"films\": [" +
            this.jsonParseResults(
                this.queryDBNoVal("SELECT * FROM film ORDER BY film_id DESC LIMIT 48;"),
                parseType.FILMS)               
        + "]}";
    }

    public String AddNewFilm(String jsondata){//YYY
        JSONObject clientdata = (JSONObject)JSONValue.parse(jsondata);
        String title = (String)clientdata.get("title");
        String genre = (String)clientdata.get("genre");
        String language = (String)clientdata.get("language");
        Number release_year = (Number)clientdata.get("release_year");
        Number rental_duration = (Number)clientdata.get("rental_duration");
        Number length = (Number)clientdata.get("length");
        Number rental_rate = (Number)clientdata.get("rental_rate");
        Number replacement_cost = (Number)clientdata.get("replacement_cost");
        String rating = (String)clientdata.get("rating");
        String special_features = (String)clientdata.get("special_features");
        String description = (String)clientdata.get("description");
        String storeaddress = (String)clientdata.get("storeaddress");
        

        //get store id
        int store_id = -1;
        this.databaseinstance.PrepareStatement("SELECT store.store_id FROM address INNER JOIN store " +
            "on store.address_id = address.address_id WHERE address.address ILIKE ? LIMIT 1;");
        this.databaseinstance.setValues(1, "%" + storeaddress + "%");
        ResultSet res = this.databaseinstance.getResults();
        try{
            if(res.next())store_id = res.getInt(1);
            else return "{\"result\": \"error\", \"data\": \"This address does not exist\"}";
        }
        catch(Exception e){return "{\"result\": \"error\", \"data\": \"This address does not exist\"}";}
    

        //get language id if none, create a new language and get new language id
        int language_id = -1;
        this.databaseinstance.PrepareStatement("SELECT language_id FROM language WHERE name ILIKE ? LIMIT 1;");
        this.databaseinstance.setValues(1, "%" + language + "%");
        res = this.databaseinstance.getResults();
        try{
            if(res.next())language_id = res.getInt(1);
            else{
                this.databaseinstance.PrepareStatement("INSERT INTO language(name) VALUES(?);");
                this.databaseinstance.setValues(1, language);
                this.databaseinstance.ExecuteUpdate();
    
                this.databaseinstance.PrepareStatement("SELECT language_id FROM language WHERE name ILIKE ? LIMIT 1;");
                this.databaseinstance.setValues(1, "%" + language + "%");
                res = this.databaseinstance.getResults();
                if(res.next())language_id = res.getInt(1);
                else return "{\"result\": \"error\", \"data\": \"Database failed to add new language\"}";
            }
        }
        catch(Exception e){return "{\"result\": \"error\", \"data\": \"Database failed to get language\"}";}
        
        
        //get category id(genre) if none, create a new category and get new category id
        int category_id = -1;
        this.databaseinstance.PrepareStatement("SELECT category_id FROM category WHERE name ILIKE ? LIMIT 1;");
        this.databaseinstance.setValues(1, "%" + genre + "%");
        res = this.databaseinstance.getResults();
        try{
            if(res.next())category_id = res.getInt(1);
            else{
                this.databaseinstance.PrepareStatement("INSERT INTO category(name) VALUES(?);");
                this.databaseinstance.setValues(1, genre);
                this.databaseinstance.ExecuteUpdate();
    
                this.databaseinstance.PrepareStatement("SELECT category_id FROM category WHERE name ILIKE ? LIMIT 1;");
                this.databaseinstance.setValues(1, "%" + genre + "%");
                res = this.databaseinstance.getResults();
                if(res.next())category_id = res.getInt(1);
                else return "{\"result\": \"error\", \"data\": \"Database failed to add new genre\"}";
            }
        }
        catch(Exception e){return "{\"result\": \"error\", \"data\": \"Database failed to add new genre\"}";}


        //check if film exists
        this.databaseinstance.PrepareStatement("SELECT COUNT(*) FROM film WHERE title ILIKE ? AND description ILIKE ?;");
        this.databaseinstance.setValues(1, title);
        this.databaseinstance.setValues(2, description);
        res = this.databaseinstance.getResults();
        try{
            if(res.next()){
                int countval = res.getInt(1);
                if(countval >= 1)return "{\"result\":\"error\",\"data\":\"Database found duplicate of this film, cannot add new film\"}";
            }
            else return "{\"result\":\"error\",\"data\":\"Database failed to query if duplicate exists\"}";
        }
        catch(Exception e){return "{\"result\":\"error\",\"data\":\"Database failed to query if duplicate exists\"}";}


        //create a new film by inserting
        this.databaseinstance.PrepareStatement("INSERT INTO film(title, description, release_year, language_id, rental_duration, " +
        "rental_rate, length, replacement_cost, special_features, rating, fulltext) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, " +
        decipherRating(rating) + ", to_tsvector(?));");
        this.databaseinstance.setValues(1, title);
        this.databaseinstance.setValues(2, description);
        this.databaseinstance.setIntValues(3, release_year.intValue());
        this.databaseinstance.setIntValues(4, language_id);
        this.databaseinstance.setIntValues(5, rental_duration.intValue());
        this.databaseinstance.setfloatValues(6, rental_rate.floatValue());
        this.databaseinstance.setIntValues(7, length.intValue());
        this.databaseinstance.setfloatValues(8, replacement_cost.floatValue());
        this.databaseinstance.setArrayValues(9, special_features.split(","));
        this.databaseinstance.setValues(10, description);
        this.databaseinstance.ExecuteUpdate();

        //get that films id
        int film_id = -1;
        this.databaseinstance.PrepareStatement("SELECT film_id FROM film WHERE title = ? AND description = ? LIMIT 1;");
        this.databaseinstance.setValues(1, title);
        this.databaseinstance.setValues(2, description);
        res = this.databaseinstance.getResults();
        try{
            if(res.next())film_id = res.getInt(1);
            else return "{\"result\":\"error\",\"data\":\"Database failed to add new film\"}";
        }
        catch(Exception e){return "{\"result\":\"error\",\"data\":\"Database failed to add new film\"}";}

        //place the new film id with the category id into film_category
        this.databaseinstance.PrepareStatement("INSERT INTO film_category(film_id, category_id) VALUES(?, ?);");
        this.databaseinstance.setIntValues(1, film_id);
        this.databaseinstance.setIntValues(2, category_id);
        this.databaseinstance.ExecuteUpdate();

        //place the new film id and store id into inventory
        this.databaseinstance.PrepareStatement("INSERT INTO inventory(film_id, store_id) VALUES(?, ?);");
        this.databaseinstance.setIntValues(1, film_id);
        this.databaseinstance.setIntValues(2, store_id);
        this.databaseinstance.ExecuteUpdate();
        
        return "{\"result\":\"success\"," +
        "\"data\":{"+
            "\"title\":\""+ title +"\"," +
            "\"len\":\""+ length.intValue() +"\"," +
            "\"release\":\""+ release_year.intValue() +"\"," +
            "\"rating\":\""+ rating +"\"" +
        "}}";
    }

    public String getRecords(){//YYY
        this.reportJSON =  "{" +
            "\"records\": ["+ 
                this.jsonParseResults(
                    this.queryDBNoVal("SELECT COUNT(res.film_id), res.name, res.store_id FROM " +
                    "(SELECT film.film_id, inventory.store_id, category.name FROM (((category " +
                        "INNER JOIN film_category ON category.category_id = film_category.category_id) " +
                        "INNER JOIN film ON film.film_id = film_category.film_id) " +
                        "INNER JOIN inventory ON inventory.film_id = film.film_id))" +
                        " as res GROUP BY res.store_id, res.name;"),
                    parseType.REPORT)
                    + "]}";
        return this.reportJSON;
    }

    public String getAllClients(){//YYY
        return "{" +
            "\"clients\": [" +
                this.jsonParseResults(
                    this.queryDBNoVal("SELECT customer_id, first_name, last_name, email, active FROM customer ORDER BY customer_id DESC LIMIT 48;"),
                    parseType.CLIENTS)   
            + "]}"; 
    }

    public void deleteClient(int id){//YYY
        //delete client from from payment
        this.databaseinstance.PrepareStatement("DELETE FROM payment WHERE customer_id = ?;");
        this.databaseinstance.setIntValues(1, id);
        this.databaseinstance.ExecuteUpdate();
        //delete client from rental
        this.databaseinstance.PrepareStatement("DELETE FROM rental WHERE customer_id = ?;");
        this.databaseinstance.setIntValues(1, id);
        this.databaseinstance.ExecuteUpdate();
        //delete client from customer
        this.databaseinstance.PrepareStatement("DELETE FROM customer WHERE customer_id = ?;");
        this.databaseinstance.setIntValues(1, id);
        this.databaseinstance.ExecuteUpdate();
    }

    public String updateClient(String jsondata){//----???
        JSONObject clientdata = (JSONObject)JSONValue.parse(jsondata);
        String customer_id = (String)clientdata.get("customer_id");
        String name = (String)clientdata.get("name");
        String surname = (String)clientdata.get("surname");
        String email = (String)clientdata.get("email");
        String activestatus = (String)clientdata.get("activestatus");
        String city = (String)clientdata.get("city");
        String country = (String)clientdata.get("country");
        String district = (String)clientdata.get("district");
        String postalcode = (String)clientdata.get("postalcode");
        String phone = (String)clientdata.get("phone");
        String address = (String)clientdata.get("address");
        String address2 = (String)clientdata.get("address2");
        String storeaddress = (String)clientdata.get("storeaddress");

        if(!name.equals("")){
            this.databaseinstance.PrepareStatement("UPDATE customer SET first_name = ? WHERE customer_id = ?;");
            this.databaseinstance.setValues(1, name);
            this.databaseinstance.setIntValues(2, Integer.parseInt(customer_id));
            if(!this.databaseinstance.ExecuteUpdate())return "{\"result\": \"error\", \"data\": \"Failed to update name\"}";
        }

        if(!surname.equals("")){
            this.databaseinstance.PrepareStatement("UPDATE customer SET last_name = ? WHERE customer_id = ?;");
            this.databaseinstance.setValues(1, surname);
            this.databaseinstance.setIntValues(2, Integer.parseInt(customer_id));
            if(!this.databaseinstance.ExecuteUpdate())return "{\"result\": \"error\", \"data\": \"Failed to update surname\"}";
        }

        if(!email.equals("")){
            this.databaseinstance.PrepareStatement("UPDATE customer SET email = ? WHERE customer_id = ?;");
            this.databaseinstance.setValues(1, email);
            this.databaseinstance.setIntValues(2, Integer.parseInt(customer_id));
            if(!this.databaseinstance.ExecuteUpdate())return "{\"result\": \"error\", \"data\": \"Failed to update email\"}";
        }

        if(!activestatus.equals("")){
            this.databaseinstance.PrepareStatement("UPDATE customer SET activebool = ?, SET active = ?  WHERE customer_id = ?;");
            this.databaseinstance.setBooleanValues(1, activestatus.equals("active") ? true : false);
            this.databaseinstance.setIntValues(2, activestatus.equals("active") ? 1 : 0);
            this.databaseinstance.setIntValues(3, Integer.parseInt(customer_id));
            if(!this.databaseinstance.ExecuteUpdate())return "{\"result\": \"error\", \"data\": \"Failed to update active status\"}";
        }

        int country_id = -1;
        int city_id = -1;
        if(!country.equals("") && !city.equals("")){
            this.databaseinstance.PrepareStatement("SELECT country_id FROM country WHERE country ILIKE ? LIMIT 1;");
            this.databaseinstance.setValues(1, "%" + country + "%");
            ResultSet res = this.databaseinstance.getResults();
            try{
                if(res.next())country_id = res.getInt(1);
                else{
                    this.databaseinstance.PrepareStatement("INSERT INTO country(country) VALUES(?);");
                    this.databaseinstance.setValues(1, country);
                    this.databaseinstance.ExecuteUpdate();
        
                    this.databaseinstance.PrepareStatement("SELECT country_id FROM country WHERE country ILIKE ? LIMIT 1;");
                    this.databaseinstance.setValues(1, "%" + country + "%");
                    res = this.databaseinstance.getResults();
                    if(res.next())country_id = res.getInt(1);
                    else return "{\"result\": \"error\", \"data\": \"Database failed to add new country\"}";
                }
            }
            catch(Exception e){return "{\"result\": \"error\", \"data\": \"Database failed to get country\"}";}


            this.databaseinstance.PrepareStatement("SELECT city_id FROM city WHERE city ILIKE ? LIMIT 1;");
            this.databaseinstance.setValues(1, "%" + city + "%");
            res = this.databaseinstance.getResults();
            try{
                if(res.next())city_id = res.getInt(1);
                else{
                    this.databaseinstance.PrepareStatement("INSERT INTO city(city, country_id) VALUES(?, ?);");
                    this.databaseinstance.setValues(1, city);
                    this.databaseinstance.setIntValues(2, country_id);
                    this.databaseinstance.ExecuteUpdate();
        
                    this.databaseinstance.PrepareStatement("SELECT city_id FROM city WHERE city ILIKE ? LIMIT 1;");
                    this.databaseinstance.setValues(1, "%" + city + "%");
                    res = this.databaseinstance.getResults();
                    if(res.next())city_id = res.getInt(1);
                    else return "{\"result\": \"error\", \"data\": \"Database failed to add new city\"}";
                }
            }
            catch(Exception e){return "{\"result\": \"error\", \"data\": \"Database failed to get city\"}";}
        }

        int address_id = -1;
        if(!address.equals("") && !address2.equals("") && !district.equals("") 
            && !postalcode.equals("") && !phone.equals("") && !city.equals("") && city_id != -1){
            //get address id if not insert new address
            this.databaseinstance.PrepareStatement("SELECT address_id FROM address WHERE " +
            "address ILIKE ? AND address2 ILIKE ? AND district ILIKE ? AND postal_code ILIKE ? " +
            "AND phone ILIKE ? AND city_id = ? LIMIT 1;");
            this.databaseinstance.setValues(1, "%" + address + "%");
            this.databaseinstance.setValues(2, "%" + address2 + "%");
            this.databaseinstance.setValues(3, "%" + district + "%");
            this.databaseinstance.setValues(4, "%" + postalcode + "%");
            this.databaseinstance.setValues(5, "%" + phone + "%");
            this.databaseinstance.setIntValues(6, city_id);
            ResultSet res = this.databaseinstance.getResults();
            try{
                if(res.next())address_id = res.getInt(1);
                else{
                    this.databaseinstance.PrepareStatement("INSERT INTO address(address, address2, district, city_id, postal_code, phone) VALUES(?, ?, ?, ?, ?, ?);");
                    this.databaseinstance.setValues(1, address);
                    this.databaseinstance.setValues(2, address2);
                    this.databaseinstance.setValues(3, district);
                    this.databaseinstance.setIntValues(4, city_id);
                    this.databaseinstance.setValues(5, postalcode);
                    this.databaseinstance.setValues(6, phone);
                    this.databaseinstance.ExecuteUpdate();
        
                    this.databaseinstance.PrepareStatement("SELECT address_id FROM address WHERE " +
                    "address ILIKE ? AND address2 ILIKE ? AND district ILIKE ? AND postal_code ILIKE ? " +
                    "AND phone ILIKE ? AND city_id = ? LIMIT 1;");
                    this.databaseinstance.setValues(1, "%" + address + "%");
                    this.databaseinstance.setValues(2, "%" + address2 + "%");
                    this.databaseinstance.setValues(3, "%" + district + "%");
                    this.databaseinstance.setValues(4, "%" + postalcode + "%");
                    this.databaseinstance.setValues(5, "%" + phone + "%");
                    this.databaseinstance.setIntValues(6, city_id);
                    res = this.databaseinstance.getResults();
                    if(res.next())address_id = res.getInt(1);
                    else return "{\"result\": \"error\", \"data\": \"Database failed to add new address\"}";
                }
            }
            catch(Exception e){return "{\"result\": \"error\", \"data\": \"Database failed to get address\"}";}
        }
        
        //add new address id for the customer id
        if(address_id != -1){
            this.databaseinstance.PrepareStatement("UPDATE customer SET address_id = ? WHERE customer_id = ?;");
            this.databaseinstance.setIntValues(1, address_id);
            this.databaseinstance.setIntValues(2, Integer.parseInt(customer_id));
            if(!this.databaseinstance.ExecuteUpdate())return "{\"result\": \"error\", \"data\": \"Failed to update address\"}";
        }

        //Update store address get store id
        if(!storeaddress.equals("")){
            int store_id = -1;
            this.databaseinstance.PrepareStatement("SELECT store.store_id FROM address INNER JOIN store " +
                "on store.address_id = address.address_id WHERE address.address ILIKE ? LIMIT 1;");
            this.databaseinstance.setValues(1, "%" + storeaddress + "%");
            ResultSet res = this.databaseinstance.getResults();
            try{
                if(res.next())store_id = res.getInt(1);
                else return "{\"result\": \"error\", \"data\": \"This address does not exist\"}";
            }
            catch(Exception e){return "{\"result\": \"error\", \"data\": \"This address does not exist\"}";}

            this.databaseinstance.PrepareStatement("UPDATE customer SET store_id = ? WHERE customer_id = ?;");
            this.databaseinstance.setIntValues(1, store_id);
            this.databaseinstance.setIntValues(2, Integer.parseInt(customer_id));
            if(!this.databaseinstance.ExecuteUpdate())return "{\"result\": \"error\", \"data\": \"Failed to update name\"}";
        }

        return "{\"result\":\"success\"," +
                "\"data\":{"+
                    "\"customer_id\":\""+ customer_id +"\"," +
                    "\"first_name\":\""+ name +"\"," +
                    "\"last_name\":\""+ surname +"\"," +
                    "\"email\":\""+ email +"\"," +
                    "\"active\":\""+ activestatus +"\"" +
                "}}";
    }

    public String addNewClient(String jsondata){//YYY
        JSONObject clientdata = (JSONObject)JSONValue.parse(jsondata);
        String name = (String)clientdata.get("name");
        String surname = (String)clientdata.get("surname");
        String email = (String)clientdata.get("email");
        String activestatus = (String)clientdata.get("activestatus");
        String city = (String)clientdata.get("city");
        String country = (String)clientdata.get("country");
        String district = (String)clientdata.get("district");
        String postalcode = (String)clientdata.get("postalcode");
        String phone = (String)clientdata.get("phone");
        String address = (String)clientdata.get("address");
        String address2 = (String)clientdata.get("address2");
        String storeaddress = (String)clientdata.get("storeaddress");

        //get store id
        int store_id = -1;
        this.databaseinstance.PrepareStatement("SELECT store.store_id FROM address INNER JOIN store " +
            "on store.address_id = address.address_id WHERE address.address ILIKE ? LIMIT 1;");
        this.databaseinstance.setValues(1, "%" + storeaddress + "%");
        ResultSet res = this.databaseinstance.getResults();
        try{
            if(res.next())store_id = res.getInt(1);
            else return "{\"result\": \"error\", \"data\": \"This address does not exist\"}";
        }
        catch(Exception e){return "{\"result\": \"error\", \"data\": \"This address does not exist\"}";}

        //get country id if not insert new country
        int country_id = -1;
        this.databaseinstance.PrepareStatement("SELECT country_id FROM country WHERE country ILIKE ? LIMIT 1;");
        this.databaseinstance.setValues(1, "%" + country + "%");
        res = this.databaseinstance.getResults();
        try{
            if(res.next())country_id = res.getInt(1);
            else{
                this.databaseinstance.PrepareStatement("INSERT INTO country(country) VALUES(?);");
                this.databaseinstance.setValues(1, country);
                this.databaseinstance.ExecuteUpdate();
    
                this.databaseinstance.PrepareStatement("SELECT country_id FROM country WHERE country ILIKE ? LIMIT 1;");
                this.databaseinstance.setValues(1, "%" + country + "%");
                res = this.databaseinstance.getResults();
                if(res.next())country_id = res.getInt(1);
                else return "{\"result\": \"error\", \"data\": \"Database failed to add new country\"}";
            }
        }
        catch(Exception e){return "{\"result\": \"error\", \"data\": \"Database failed to get country\"}";}

        //get city id if not insert new city
        int city_id = -1;
        this.databaseinstance.PrepareStatement("SELECT city_id FROM city WHERE city ILIKE ? LIMIT 1;");
        this.databaseinstance.setValues(1, "%" + city + "%");
        res = this.databaseinstance.getResults();
        try{
            if(res.next())city_id = res.getInt(1);
            else{
                this.databaseinstance.PrepareStatement("INSERT INTO city(city, country_id) VALUES(?, ?);");
                this.databaseinstance.setValues(1, city);
                this.databaseinstance.setIntValues(2, country_id);
                this.databaseinstance.ExecuteUpdate();
    
                this.databaseinstance.PrepareStatement("SELECT city_id FROM city WHERE city ILIKE ? LIMIT 1;");
                this.databaseinstance.setValues(1, "%" + city + "%");
                res = this.databaseinstance.getResults();
                if(res.next())city_id = res.getInt(1);
                else return "{\"result\": \"error\", \"data\": \"Database failed to add new city\"}";
            }
        }
        catch(Exception e){return "{\"result\": \"error\", \"data\": \"Database failed to get city\"}";}

        //get address id if not insert new address
        int address_id = -1;
        this.databaseinstance.PrepareStatement("SELECT address_id FROM address WHERE " +
        "address ILIKE ? AND address2 ILIKE ? AND district ILIKE ? AND postal_code ILIKE ? " +
        "AND phone ILIKE ? AND city_id = ? LIMIT 1;");
        this.databaseinstance.setValues(1, "%" + address + "%");
        this.databaseinstance.setValues(2, "%" + address2 + "%");
        this.databaseinstance.setValues(3, "%" + district + "%");
        this.databaseinstance.setValues(4, "%" + postalcode + "%");
        this.databaseinstance.setValues(5, "%" + phone + "%");
        this.databaseinstance.setIntValues(6, city_id);
        res = this.databaseinstance.getResults();
        try{
            if(res.next())address_id = res.getInt(1);
            else{
                this.databaseinstance.PrepareStatement("INSERT INTO address(address, address2, district, city_id, postal_code, phone) VALUES(?, ?, ?, ?, ?, ?);");
                this.databaseinstance.setValues(1, address);
                this.databaseinstance.setValues(2, address2);
                this.databaseinstance.setValues(3, district);
                this.databaseinstance.setIntValues(4, city_id);
                this.databaseinstance.setValues(5, postalcode);
                this.databaseinstance.setValues(6, phone);
                this.databaseinstance.ExecuteUpdate();
    
                this.databaseinstance.PrepareStatement("SELECT address_id FROM address WHERE " +
                "address ILIKE ? AND address2 ILIKE ? AND district ILIKE ? AND postal_code ILIKE ? " +
                "AND phone ILIKE ? AND city_id = ? LIMIT 1;");
                this.databaseinstance.setValues(1, "%" + address + "%");
                this.databaseinstance.setValues(2, "%" + address2 + "%");
                this.databaseinstance.setValues(3, "%" + district + "%");
                this.databaseinstance.setValues(4, "%" + postalcode + "%");
                this.databaseinstance.setValues(5, "%" + phone + "%");
                this.databaseinstance.setIntValues(6, city_id);
                res = this.databaseinstance.getResults();
                if(res.next())address_id = res.getInt(1);
                else return "{\"result\": \"error\", \"data\": \"Database failed to add new address\"}";
            }
        }
        catch(Exception e){return "{\"result\": \"error\", \"data\": \"Database failed to get address\"}";}
        
        this.databaseinstance.PrepareStatement("INSERT INTO customer(store_id, first_name, last_name, email, address_id, activebool, active) VALUES(?, ?, ?, ?, ?, ?, ?);");
        this.databaseinstance.setIntValues(1, store_id);
        this.databaseinstance.setValues(2, name);
        this.databaseinstance.setValues(3, surname);
        this.databaseinstance.setValues(4, email);
        this.databaseinstance.setIntValues(5, address_id);
        this.databaseinstance.setBooleanValues(6, activestatus.equals("active") ? true : false);
        this.databaseinstance.setIntValues(7, activestatus.equals("active") ? 1 : 0);
        this.databaseinstance.ExecuteUpdate();

        //get that clients id
        int customer_id = -1;
        this.databaseinstance.PrepareStatement("SELECT customer_id FROM customer WHERE store_id = ? "+
        "AND first_name = ? AND last_name = ? AND email = ? AND address_id = ? AND activebool = ? "+
        "AND active = ? LIMIT 1;");
        this.databaseinstance.setIntValues(1, store_id);
        this.databaseinstance.setValues(2, name);
        this.databaseinstance.setValues(3, surname);
        this.databaseinstance.setValues(4, email);
        this.databaseinstance.setIntValues(5, address_id);
        this.databaseinstance.setBooleanValues(6, activestatus.equals("active") ? true : false);
        this.databaseinstance.setIntValues(7, activestatus.equals("active") ? 1 : 0);
        res = this.databaseinstance.getResults();
        try{
            if(res.next())customer_id = res.getInt(1);
            else return "{\"result\":\"error\",\"data\":\"Database failed to add new client\"}";
        }
        catch(Exception e){return "{\"result\":\"error\",\"data\":\"Database failed to add new client\"}";}

        return "{\"result\":\"success\"," +
                "\"data\":{"+
                    "\"customer_id\":\""+ Integer.toString(customer_id) +"\"," +
                    "\"first_name\":\""+ name +"\"," +
                    "\"last_name\":\""+ surname +"\"," +
                    "\"email\":\""+ email +"\"," +
                    "\"active\":\""+ activestatus +"\"" +
                "}}";
    }

    public String searchForClient(String attribute){//YYY
        this.databaseinstance.PrepareStatement("SELECT customer_id, first_name, last_name, email, active FROM customer " +
            "WHERE first_name ILIKE ? OR last_name ILIKE ? OR email ILIKE ? ORDER BY customer_id DESC LIMIT 48;");
        this.databaseinstance.setValues(1, "%" + attribute + "%");
        this.databaseinstance.setValues(2, "%" + attribute + "%");
        this.databaseinstance.setValues(3, "%" + attribute + "%");
        return "{\"clients\": [" + this.jsonParseResults(this.databaseinstance.getResults(), parseType.CLIENTS) + "]}";
    }

    public String droppedRentalSub(){//YYY
        return "{" +
            "\"clients\": [" +
                this.jsonParseResults(
                    this.queryDBNoVal("SELECT customer_id, first_name, last_name, email, active FROM customer WHERE active = 0 ORDER BY customer_id DESC LIMIT 48;"),
                    parseType.CLIENTS)
            + "]}";
    } 

    private String decipherRating(String rating){
        if(rating.equals("G"))return "'G'::public.mpaa_rating";
        else if(rating.equals("PG"))return "'PG'::public.mpaa_rating";
        else if(rating.equals("PG-13"))return "'PG-13'::public.mpaa_rating";
        else if(rating.equals("R"))return "'R'::public.mpaa_rating";
        else return "'NC-17'::public.mpaa_rating";
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
            else return "\"ServerError1\"";
        }
        catch(SQLException E){
            System.out.println("Error: Controller.java:69 : " + E.getMessage() + " " + ENUMVAL); 
            return "\"ServerError2\"";
        }
    }
}
