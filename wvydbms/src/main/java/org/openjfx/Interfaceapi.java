package org.openjfx;

import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

public class Interfaceapi {
    private final WebEngine webEngine;
    private final Controller DatabaseManager;
    private final Stage stage;

    public Interfaceapi(WebEngine webengine, Stage nstage){
        this.webEngine = webengine;
        this.DatabaseManager = new Controller();
        this.stage = nstage;
    }

    public void minimizeWindow(){this.stage.setIconified(true);}

    public void closeWindow(){this.DatabaseManager.closeDatabase(); this.stage.close();}

    public void fillDashBoard(){this.webEngine.executeScript("selectDashboardUI('"+ DatabaseManager.getDashBoardValues() +"')");}

    public void fillStaffTab(){this.webEngine.executeScript("selectStaffTabUI('"+ DatabaseManager.getAllStaff() +"')");}

    public void searchForStaff(){
        String staffAttribute = (String) webEngine.executeScript("JAVA__READABLE__TEXT;");
        this.webEngine.executeScript("populateStaffTab('"+ DatabaseManager.searchStaff(staffAttribute) +"')");
    }

    public void fillFilmTab(){this.webEngine.executeScript("selectFilmsTabUI('"+ DatabaseManager.getAllFilms() +"')");}

    //public void addFilm(){}

}
