let currentlyEditingClientID = "";
let JAVA__READABLE__TEXT = "";

const minimizewindow = function(){InterfaceAPIOBJ.minimizeWindow();}

const closewindow = function(){InterfaceAPIOBJ.closeWindow();}

const changeSelectedTab = function(classToSelect){
    const selectedTab = document.querySelector(".selected-navigation-tab");
    const toSelectTab = document.querySelector(classToSelect);

    if(toSelectTab != null && toSelectTab.classList.contains(classToSelect))return;

    if(selectedTab != null){
        selectedTab.classList.remove("selected-navigation-tab");
        selectedTab.classList.remove("selected-notifications-tab");
    }
    if(toSelectTab != null){
        toSelectTab.classList.add("selected-navigation-tab");
        if(classToSelect === ".notifications-tab")toSelectTab.classList.add("selected-notifications-tab"); 
    }

    //change to navigation tab specified
    if(classToSelect === ".dashboard-tab")InterfaceAPIOBJ.fillDashBoard();
    else if(classToSelect === ".staff-tab")InterfaceAPIOBJ.fillStaffTab();
    else if(classToSelect === ".films-tab")InterfaceAPIOBJ.fillFilmTab();
    else if(classToSelect == ".report-tab")InterfaceAPIOBJ.getAllRecords();
    else if(classToSelect == ".notifications-tab")InterfaceAPIOBJ.selectClientsTab();
    else if(classToSelect == ".about-tab")selectAboutTabUI();
    else cleanUI();
}




const selectDashboardUI = function(jsondata){
    const DashboardUI__JSONDATA = JSON.parse(jsondata);
    const mainApp = document.querySelector(".main-app");

    //json parse data

    mainApp.innerHTML = '<div class="dashboard-ui">'+
                            '<div class="film-count">'+
                                '<div class="film-count-img">'+
                                    '<img src="assets/film-count.png" alt="film-count-img"/>'+
                                '</div>'+
                                '<div class="film-count-layer_one"></div>'+
                                '<div class="film-count-layer_two"></div>'+
                                '<div class="film-count-content">'+
                                    '<h3>available films</h3>'+
                                    '<h2>'+ DashboardUI__JSONDATA.filmCount[0] +'</h2>'+
                                '</div>'+
                            '</div>'+
                            '<div class="last-added-film">'+
                                '<div class="last-added-film-img">'+
                                    '<img src="assets/last-added-film.png" alt="last-added-film-img"/>'+
                                '</div>'+
                                '<div class="last-added-film-content">'+
                                    '<h3>most recently created movie</h3>'+
                                    '<h2>title: '+ DashboardUI__JSONDATA.lastCreatedMovie[0].title +'</h2>'+
                                    '<h2>length: '+ DashboardUI__JSONDATA.lastCreatedMovie[0].length +' mins</h2>'+
                                    '<h2>genre: '+ DashboardUI__JSONDATA.lastCreatedMovie[0].genre +'</h2>'+
                                    '<h2>year: '+ DashboardUI__JSONDATA.lastCreatedMovie[0].year +'</h2>'+
                                    '<h2>rating: '+ DashboardUI__JSONDATA.lastCreatedMovie[0].rating +'</h2>'+
                                '</div>'+
                            '</div>'+
                            '<div class="last-added-client">'+
                                '<div class="last-added-client-img">'+
                                    '<img src="assets/Gradient1.png" alt="last-added-client-img"/>'+
                                '</div>'+
                                '<div class="last-added-client-layer"></div>'+
                                '<div class="last-added-client-content">'+
                                    '<h2>last inserted client</h2>'+
                                    '<h3>'+ DashboardUI__JSONDATA.lastCreatedClient[0].first_name +'</h3>'+
                                    '<h3>'+ DashboardUI__JSONDATA.lastCreatedClient[0].last_name +'</h3>'+
                                    '<h3>'+ DashboardUI__JSONDATA.lastCreatedClient[0].email +'</h3>'+
                                    '<h3>'+ clientActiveStatus(DashboardUI__JSONDATA.lastCreatedClient[0].active) +'</h3>'+
                                '</div>'+
                            '</div>'+
                            '<div class="genres-tabs-and-more">'+
                                '<div class="genres-tabs-and-more-img">'+
                                    '<img src="assets/Gradient2.png" alt="genres-tabs-and-more-img"/>'+
                                '</div>'+
                                '<div class="genres-tabs-and-more-layer"></div>'+
                                '<div class="genres-tabs-and-more-content">'+
                                    '<h2>film <br> genres</h2>'+
                                    '<div class="genres-tabs-and-more-genre">'+
                                        '<h3>'+ DashboardUI__JSONDATA.genres[0] +'</h3>'+
                                    '</div>'+
                                    '<div class="genres-tabs-and-more-genre">'+
                                        '<h3>'+ DashboardUI__JSONDATA.genres[1] +'</h3>'+
                                    '</div>'+
                                    '<div class="genres-tabs-and-more-genre">'+
                                        '<h3>'+ DashboardUI__JSONDATA.genres[2] +'</h3>'+
                                    '</div>'+
                                '</div>'+
                            '</div>'+
                        '</div>';
}

const clientActiveStatus = function(status){return status == 1 ? "active" : "not active";}




const staffActiveStatus = function(status){return status == "t" ? "active" : "not active";}

const selectStaffTabUI = function(jsondata){
    const mainApp = document.querySelector(".main-app");

    mainApp.innerHTML = '<div class="staff-tab-ui">' +
                            '<div class="staff-tab-control-centre">' +
                                '<div class="staff-tab-searchable-area">' +
                                    '<input id="staff-tab-search-input" type="text" placeholder="Search for any attribute..."/>' +
                                    '<div class="staff-tab-control-centre-search-btn" onmouseup="searchForStaff()">' +
                                        '<img src="assets/search.svg" alt="staff-tab-control-centre-search-btn"/>' +
                                    '</div>' +
                                '</div>' +
                                '<div class="staff-tab-table-headers">' +
                                    '<h2>first name</h2>' +
                                    '<h2>last name</h2>' +
                                    '<h2>address</h2>' +
                                    '<h2>address 2</h2>' +
                                    '<h2>district</h2>' +
                                    '<h4>city</h4>' +
                                    '<h2>PO code</h2>' +
                                    '<h3>phone</h3>' +
                                    '<h2>workplace</h2>' +
                                    '<h2>status</h2>' +
                                '</div>' +
                            '</div>' +
                            '<div class="staff-tab-results"></div>' +
                        '</div>';
    populateStaffTab(jsondata);
}

const populateStaffTab = function(jsonData){
    const StaffUI__JSONDATA = JSON.parse(jsonData);
    const stafftabresults = document.querySelector(".staff-tab-results");
    if(stafftabresults == null)return;
    
    stafftabresults.innerHTML = "";
    if(StaffUI__JSONDATA.staff.length === 0)stafftabresults.innerHTML = "<h2 class='staff-tab-results'>no results found in database</h2>";
    
    for(let i = 0; i < StaffUI__JSONDATA.staff.length; ++i){
        stafftabresults.innerHTML += '<div class="staff-element">' +
                                        '<h3 class="firstname">'+ StaffUI__JSONDATA.staff[i].first_name+'</h3>' +
                                        '<h3 class="lastname">'+ StaffUI__JSONDATA.staff[i].last_name+'</h3>' +
                                        '<h3 class="address">'+ StaffUI__JSONDATA.staff[i].address+'</h3>' +
                                        '<h3 class="address2">'+ StaffUI__JSONDATA.staff[i].address2+'</h3>' +
                                        '<h3 class="district">'+ StaffUI__JSONDATA.staff[i].district+'</h3>' +
                                        '<h3 class="city">'+ StaffUI__JSONDATA.staff[i].city+'</h3>' +
                                        '<h3 class="pocode">'+ StaffUI__JSONDATA.staff[i].pocode+'</h3>' +
                                        '<h3 class="phone">'+ StaffUI__JSONDATA.staff[i].phone+'</h3>' +
                                        '<h3 class="workplace">'+ StaffUI__JSONDATA.staff[i].workplaceAddress+'</h3>' +
                                        '<h3 class="status">'+ staffActiveStatus(StaffUI__JSONDATA.staff[i].activeStatus) +'</h3>' +
                                    '</div>';
    }
}

const searchForStaff = function(){
    const searchval = document.getElementById("staff-tab-search-input");
    if(searchval == null)return;
    JAVA__READABLE__TEXT = searchval.value;
    InterfaceAPIOBJ.searchForStaff();
}





const selectFilmsTabUI = function(jsonData){
    const mainApp = document.querySelector(".main-app");

    //json parse data
    mainApp.innerHTML = '<div class="films-tab-ui">' +
                            '<div class="films-tab-header">' +
                                '<h2>films</h2>' +
                                '<div class="add-films-btn" onmouseup="openAddFilm()">' +
                                    '<img src="assets/add-data.svg" alt="add-films"/>' +
                                '</div>' +
                            '</div>' +
                            '<div class="films-results"></div>' +
                        '</div>' +
                        '<div class="add-new-film-form">' +
                            '<div class="close-add-films-btn" onmouseup="closeAddfilm()">' +
                                '<img src="assets/close-add-film.svg" alt="close-add-films"/>' +
                            '</div>' +
                            '<div class="add-film-form">'+
                                '<div class="add-film-form-inputs-container">' +
                                    '<div>' +
                                        '<label for="film_title">title</label>' +
                                        '<input name="film_title" type="text" id="film_title" required/>' +
                                    '</div>' +
                                    '<div>' +
                                        '<label for="film_genre">genre</label>' +
                                        '<input name="film_genre" type="text" id="film_genre" required/>' +
                                    '</div>' +
                                    '<div>' +
                                        '<label for="film_language">language</label>' +
                                        '<input name="film_language" type="text" id="film_language" required/>' +
                                    '</div>' +
                                    '<div>' +
                                        '<label for="film_release_year">release year</label>' +
                                        '<input name="film_release_year" type="text" id="film_release_year" required/>' +
                                    '</div>' +
                                    '<div>' +
                                        '<label for="film_rental_duration">rental duration</label>' +
                                        '<input name="film_rental_duration" type="text" id="film_rental_duration" required/>' +
                                    '</div>' +
                                    '<div>' +
                                        '<label for="film_rental_rate">rental rate</label>' +
                                        '<input name="film_rental_rate" type="text" id="film_rental_rate" required/>' +
                                    '</div>' +
                                    '<div>' +
                                        '<label for="film_length">length</label>' +
                                        '<input name="film_length" type="text" id="film_length" required/>' +
                                    '</div>' +
                                    '<div>' +
                                        '<label for="film_replacement_cost">replacement cost</label>' +
                                        '<input name="film_replacement_cost" type="text" id="film_replacement_cost" required/>' +
                                    '</div>' +
                                    '<div>' +
                                        '<label for="film_rating">rating</label>' +
                                        '<input name="film_rating" type="text" id="film_rating" required/>' +
                                    '</div>' +
                                    '<div>' +
                                        '<label for="store_address">store address</label>' +
                                        '<input name="store_address" type="text" id="store_address" required/>' +
                                    '</div>' +
                                    '<div class="film_special_features-div">' +
                                        '<label for="film_special_features">special features format: "sf 1", "sf2", ..., "sf n"</label>' +
                                        '<input name="film_special_features" type="text" id="film_special_features" required/>' +
                                    '</div>' +
                                    '<div>' +
                                        '<label for="film_description">description</label>' +
                                        '<textarea name="film_description" id="film_description" required></textarea>' +
                                    '</div>' +
                                '</div>' +
                                '<h4></h4>'+
                                '<input type="submit" onmouseup="addNewFilm()" value="Add film to database">' +
                            '</div>' +
                        '</div>';
    populateFilmTab(jsonData);
}

const populateFilmTab = function(jsonData){
    const FilmsUI__JSONDATA = JSON.parse(jsonData);
    const filmsresults = document.querySelector(".films-results");
    if(filmsresults == null)return;

    filmsresults.innerHTML = "";
    if(FilmsUI__JSONDATA.films.length === 0)filmsresults.innerHTML = "<h2 class='film-tab-results'>no results found in database</h2>";

    for(let i = 0; i < FilmsUI__JSONDATA.films.length; ++i){
        filmsresults.innerHTML += '<div class="film-element">' +
                                    '<div class="film-element-img">' +
                                        '<img src="'+ getImg() +'" alt="film-element-img"/>' +
                                    '</div>' +
                                    '<div class="film-element-layer_one"></div>' +
                                    '<div class="film-element-layer_two"></div>' +
                                    '<div class="film-element-content">' +
                                        '<h3>'+ FilmsUI__JSONDATA.films[i].title +'</h3>' +
                                        '<h2>'+ FilmsUI__JSONDATA.films[i].length +' mins</h2>' +
                                        '<div class="film-element-divider">' +
                                            '<h4>'+ FilmsUI__JSONDATA.films[i].year +'</h4>' +
                                            '<h4>'+ FilmsUI__JSONDATA.films[i].rating +'</h4>' +
                                        '</div>' +
                                    '</div>' +
                                '</div>';
    }
}

const getImg = function(){
    const min = Math.ceil(1);
    const max = Math.floor(4);
    const rand = Math.floor(Math.random() * (max - min + 1) + min);

    if(rand === 1)return "assets/film1.png";
    if(rand === 2)return "assets/film2.png";
    if(rand === 3)return "assets/film3.png";
    if(rand === 4)return "assets/film4.png";
}




const selectReportTabUI = function(jsonData){
    const mainApp = document.querySelector(".main-app");
    mainApp.innerHTML = '<div class="report-tab-ui">' +
                    '<div class="report-tab-header">' +
                        '<div class="report-tab-main-header">' +
                            '<h2>report</h2>' +
                            '<div class="download-btn" onmouseup="downloadReportAsPDF()">' +
                                '<img src="assets/download.svg" alt="download"/>' +
                            '</div>' +
                        '</div>' +
                        '<div class="report-tab-sub-header">' +
                            '<h2>store name</h2>' +
                            '<h3>genre name</h3>' +
                            '<h4>number of movies</h4>' +
                        '</div>' +
                    '</div>' +
                    '<div class="report-tab-results"></div>' +
                '</div>';

    populateReportTab(jsonData);
}

const populateReportTab = function(jsonData){
    const Report__JSONDATA = JSON.parse(jsonData);
    const reportresults = document.querySelector(".report-tab-results");
    if(reportresults == null)return;

    reportresults.innerHTML = "";
    if(Report__JSONDATA.records.length === 0)reportresults.innerHTML = "<h2 class='report-tab-results'>no results found in database</h2>";

    for(let i = 0; i < Report__JSONDATA.records.length; ++i){
        reportresults.innerHTML += '<div class="report-tab-element">' +
                                        '<h2>'+ Report__JSONDATA.records[i].workplaceAddress +'</h2>' +
                                        '<h3>'+ Report__JSONDATA.records[i].genre +'</h3>' +
                                        '<h4>'+ Report__JSONDATA.records[i].count +'</h4>' +
                                    '</div>';
    }
}

const downloadReportAsPDF = function(){
    //fill later
}






const selectAllClients = function(){
    const selectedSubTab = document.querySelector(".notifications-tab-currently-selected");
    if(selectedSubTab != null && selectedSubTab.classList.contains("dropped-rental"))return;
    InterfaceAPIOBJ.fillClientsTab();
}

const selectDroppedSub = function(){
    const selectedSubTab = document.querySelector(".notifications-tab-currently-selected");
    if(selectedSubTab != null && selectedSubTab.classList.contains("dropped-rental"))return;
    InterfaceAPIOBJ.droppedRentalSub();
}

const selectNotificationTabUI = function(){
    const mainApp = document.querySelector(".main-app");
    mainApp.innerHTML = '<div class="notifications-tab-ui">' +
                            '<div class="notifications-tab-heading">' +
                                '<h2>notifications</h2>' +
                                '<h3 class="all-clients" onmouseup="selectAllClients()">all clients</h3>' +
                                '<h3 class="dropped-rental" onmouseup="selectDroppedSub()">dropped rental</h3>' +
                                '<input type="text" placeholder="Search for attribute..." id="clientSearchAttribute"/>' +
                                '<div class="search-client-btn" onmouseup="searchClient()">' +
                                    '<img src="assets/search.svg" alt="search-client-btn"/>' +
                                '</div>' +
                                '<div class="add-client-btn" onmouseup="openAddClient()">' +
                                    '<img src="assets/add-data.svg" alt="add-client-btn"/>' +
                                '</div>' +
                            '</div>' +
                            '<div class="notifications-tab-results"></div>' +
                            '<div class="notifications-tab-add-client-dialogue">' +
                                '<div class="close-add-client-btn" onmouseup="closeAddClient()">' +
                                    '<img src="assets/close-add-film.svg" alt="close-add-client-btn"/>' +
                                '</div>' +
                                '<div class="add-client-form">' +
                                    '<form action="" onsubmit="return addNewClientToDatabase()">' +
                                        '<div class="add-client-form-inputs-container">' +
                                            '<div>' +
                                                '<label for="name">name</label>' +
                                                '<input name="name" type="text" id="name" required/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="surname">surname</label>' +
                                                '<input name="surname" type="text" id="surname" required/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="email">email</label>' +
                                                '<input name="eamil" type="text" id="email" required/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="activestatus">active status</label>' +
                                                '<input name="activestatus" type="text" id="activestatus" required/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="store_id">store id</label>' +
                                                '<input name="store_id" type="text" id="store_id" required/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="country">country</label>' +
                                                '<input name="country" type="text" id="country" required/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="city">city</label>' +
                                                '<input name="city" type="text" id="city" required/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="district">district</label>' +
                                                '<input name="district" type="text" id="district" required/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="postal_code">postal code</label>' +
                                                '<input name="postal_code" type="text" id="postal_code" required/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="phone">phone</label>' +
                                                '<input name="phone" type="text" id="phone" required/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="address">address</label>' +
                                                '<textarea name="address" id="address" required></textarea>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="address2">address 2</label>' +
                                                '<textarea name="address2" id="address2" required></textarea>' +
                                            '</div>' +
                                        '</div>' +
                                        '<input type="submit" value="Add client to database">' +
                                    '</form>' +
                                '</div>' +
                            '</div>' +
                            '<div class="notifications-tab-edit-client-dialogue">' +
                                '<div class="close-edit-client-btn" onmouseup="closeEditClient()">' +
                                    '<img src="assets/close-add-film.svg" alt="close-edit-client-btn"/>' +
                                '</div>' +
                                '<div class="edit-client-form">' +
                                    '<form action="" onsubmit="return upDateClientDataDatabase()">' +
                                        '<div class="edit-client-form-inputs-container">' +
                                            '<div>' +
                                                '<label for="name_optional">name(optional)</label>' +
                                                '<input name="name_optional" type="text" id="name_optional"/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="surname_optional">surname(optional)</label>' +
                                                '<input name="surname_optional" type="text" id="surname_optional"/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="email_optional">email(optional)</label>' +
                                                '<input name="email_optional" type="text" id="email_optional"/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="activestatus_optional">active status(optional)</label>' +
                                                '<input name="activestatus_optional" type="text" id="activestatus_optional"/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="store_id_optional">store id(optional)</label>' +
                                                '<input name="store_id_optional" type="text" id="store_id_optional"/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="country_optional">country(optional)</label>' +
                                                '<input name="country_optional" type="text" id="country_optional"/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="city_optional">city(optional)</label>' +
                                                '<input name="city_optional" type="text" id="city_optional"/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="district_optional">district(optional)</label>' +
                                                '<input name="district_optional" type="text" id="district_optional"/>' +
                                            '</div>' +
                                        '<div>' +
                                            '<label for="postal_code_optional">postal code(optional)</label>' +
                                            '<input name="postal_code_optional" type="text" id="postal_code_optional"/>' +
                                        '</div>' +
                                        '<div>' +
                                            '<label for="phone_optional">phone(optional)</label>' +
                                            '<input name="phone_optional" type="text" id="phone_optional"/>' +
                                        '</div>' +
                                        '<div>' +
                                            '<label for="address_optional">address(optional)</label>' +
                                            '<textarea name="address_optional" id="address_optional"></textarea>' +
                                        '</div>' +
                                        '<div>' +
                                            '<label for="address2_optional">address 2(optional)</label>' +
                                            '<textarea name="address2_optional" id="address2_optional"></textarea>' +
                                        '</div>' +
                                        '</div>' +
                                        '<input type="submit" value="update client info">' +
                                    '</form>' +
                                '</div>' +
                            '</div>' +
                        '</div>';
}

const selectNotificationALLCLIENTS = function(jsonData){
    const selectedSubTab = document.querySelector(".notifications-tab-currently-selected");
    const thisSubTab = document.querySelector(".all-clients");
    const resultsDiv = document.querySelector(".notifications-tab-results");
    
    if(resultsDiv == null)return;
    if(selectedSubTab != null)selectedSubTab.classList.remove("notifications-tab-currently-selected");
    if(thisSubTab != null)thisSubTab.classList.add("notifications-tab-currently-selected");

    resultsDiv.innerHTML = "";
    const Clients__JSONDATA = JSON.parse(jsonData);
    if(Clients__JSONDATA.clients.length === 0)filmsresults.innerHTML = "<h2 class='all-clients-results'>no results found in database</h2>";

    for(let i = 0; i < Clients__JSONDATA.clients.length; ++i){
        resultsDiv.innerHTML += '<div class="client-element" id="'+ Clients__JSONDATA.clients[i].customer_id +'">' +
                                    '<div class="client-details">' +
                                        '<h2>Name: '+ Clients__JSONDATA.clients[i].first_name +'</h2>' +
                                        '<h2>Surname: '+ Clients__JSONDATA.clients[i].last_name +'</h2>' +
                                        '<h2>Email: '+ Clients__JSONDATA.clients[i].email +'</h2>' +
                                        '<h2>Active status: '+ clientActiveStatus(Clients__JSONDATA.clients[i].active) +'</h2>' +
                                    '</div>' +
                                    '<div class="client-methods">' +
                                        '<div class="edit-client-btn" onmouseup="openEditClient(\''+ Clients__JSONDATA.clients[i].customer_id +'\')">' +
                                            '<img src="assets/edit-client.svg" alt="edit-client-btn"/>' +
                                        '</div>' +
                                        '<div class="delete-client-btn" onmouseup="deleteClient(\''+ Clients__JSONDATA.clients[i].customer_id +'\')">' +
                                            '<img src="assets/delete-client.svg" alt="delete-client-btn"/>' +
                                        '</div>' +
                                    '</div>' +
                                '</div>';
    }
}

const selectNotificationDROPPEDRENTAL = function(jsonData){
    const selectedSubTab = document.querySelector(".notifications-tab-currently-selected");
    const thisSubTab = document.querySelector(".dropped-rental");
    const resultsDiv = document.querySelector(".notifications-tab-results");

    if(resultsDiv == null)return;
    if(selectedSubTab != null)selectedSubTab.classList.remove("notifications-tab-currently-selected");
    if(thisSubTab != null)thisSubTab.classList.add("notifications-tab-currently-selected");

    resultsDiv.innerHTML = "";
    const Clients__JSONDATA = JSON.parse(jsonData);
    
    resultsDiv.innerHTML += '<div class="dropped-rental-sub-headers">' +
                                    '<h2>name</h2>' +
                                    '<h2>surname</h2>' +
                                    '<h3>email</h3>' +
                                    '<h2>active status</h2>' +
                                '</div>' +
                                '<div class="dropped-rental-sub-results"></div>';
    const rentalresultsDiv = document.querySelector(".dropped-rental-sub-results");
    if(rentalresultsDiv == null)return;

    if(Clients__JSONDATA.clients.length === 0)rentalresultsDiv.innerHTML = "<h2 class='dropped-rental-clients-results'>no results found in database</h2>";

    for(let i = 0; i < Clients__JSONDATA.clients.length; ++i){
        rentalresultsDiv.innerHTML += '<div class="dropped-rental-sub-element">' +
                                        '<h2>'+ Clients__JSONDATA.clients[i].first_name +'</h2>' +
                                        '<h3>'+ Clients__JSONDATA.clients[i].last_name +'</h3>' +
                                        '<h4>'+ Clients__JSONDATA.clients[i].email +'</h4>' +
                                        '<h5>'+ clientActiveStatus(Clients__JSONDATA.clients[i].active) +'</h5>' +
                                    '</div>';
    }
}





const selectAboutTabUI = function(){
    const mainApp = document.querySelector(".main-app");
    mainApp.innerHTML = '<div class="about-tab-ui">' +
                    '<div class="about-tab-header">' +
                        '<h2>about</h2>' +
                    '</div>' +
                    '<div class="about-content">' +
                        '<h3>Database Management System 2023</h3>' +
                        '<h2>Owner: https://github.com/waveyboym</h2>' +
                        '<h2>Repo link: https://github.com/waveyboym/WVY-DBMS</h2>' +
                    '</div>' +
                '</div>';
}





const openAddFilm = function(){
    if(document.querySelector(".add-new-film-form"))
        document.querySelector(".add-new-film-form").style.display = "block";
}

const closeAddfilm = function(){
    if(document.querySelector(".add-new-film-form"))
        document.querySelector(".add-new-film-form").style.display = "none";
}

const addNewFilm = function(){
    const title = document.getElementById("film_title");//'Chamber Italian' max 255 characters
    const genre = document.getElementById("film_genre");//'Action' max 25 characters
    const lang = document.getElementById("film_language");//'English' max 20 characters
    const release = document.getElementById("film_release_year");//optional - year number between 0000 and 9999
    const rentaldur = document.getElementById("film_rental_duration");//number between -32768 and 32768
    const rentalrate = document.getElementById("film_rental_rate");//number between -9999.99 and 9999.99
    const len = document.getElementById("film_length");//optional - number between -32768 and 32768
    const replacementcost = document.getElementById("film_replacement_cost");//number between -99999.99 and 99999.99
    const rating = document.getElementById("film_rating");//either 'G','PG','PG-13','R','NC-17'
    const specialfeatures = document.getElementById("film_special_features");//'{Trailers}'
    const description = document.getElementById("film_description");//optional - max 65535  characters
    const storeaddress = document.getElementById("store_address");

    const errorContext = document.querySelector(".add-film-form h4");

    if(title === null || genre === null || lang === null || release === null 
        || rentaldur === null || len === null || rentalrate === null ||
        replacementcost === null || rating === null || specialfeatures === null
        || description === null || storeaddress === null){
            if(errorContext !== null)errorContext.innerHTML = "An error occurred with the application";
            return;
    }

    if(title.value === "" || genre.value === "" || lang.value === "" || release.value === "" 
        || rentaldur.value === "" || len.value === "" || rentalrate.value === "" ||
        replacementcost.value === "" || rating.value === "" || specialfeatures.value === ""
        || description.value === "" || storeaddress.value === ""){
            if(errorContext !== null)errorContext.innerHTML = "No fields on the form can be empty";
            return;
    }

    if(title.value.length > 128){
        errorContext.innerHTML = "Title must be less than  or equal to 128 characters";
        return;
    }
    if(!(release.value >= 0000 && release.value <= 3000)){
        errorContext.innerHTML = "Release year must be between 0000 and 3000";
        return;
    }
    if(!(rentaldur.value >= 0 && rentaldur.value <= 255)){
        errorContext.innerHTML = "Rental duration must be between 0 and 255";
        return;
    }
    if(!(rentalrate.value >= 0.00 && rentalrate.value <= 99.99)){
        errorContext.innerHTML = "Rental rate must be between 0.00 and 99.99";
        return;
    }
    if(!(len.value >= -32768 && len.value <= 32768)){
        errorContext.innerHTML = "Length of film must be between -32768 and 32768";
        return;
    }
    if(!(replacementcost.value >= 0.00 && replacementcost.value <= 999.99)){
        errorContext.innerHTML = "Replacement cost must be between 0.00 and 999.99";
        return;
    }
    if(rating.value !== "G" && rating.value !== "PG" && rating.value !== "PG-13" && rating.value !== "R" && rating.value !== "NC-17"){
        errorContext.innerHTML = "Rating can only be between: 'G','PG','PG-13','R','NC-17'";
        return;
    }
    if(!specialfeatures.value.includes("Trailers") && !specialfeatures.value.includes("Commentaries") 
        && !specialfeatures.value.includes("Deleted Scenes") && !specialfeatures.value.includes("Behind the Scenes")){
            errorContext.innerHTML = "Special features can only include: 'Trailers','Commentaries','Deleted Scenes' or 'Behind the Scenes'";
            return;
    }
    if(description.value.length > 65535){
        errorContext.innerHTML = "Description must be less than  or equal to 65535 characters";
        return;
    }
        
    JAVA__READABLE__TEXT = "{" +
                                "\"title\":\"" + title.value + "\"," +
                                "\"genre\":\"" + genre.value + "\"," +
                                "\"language\":\"" + lang.value + "\"," +
                                "\"release_year\":" + release.value + "," +
                                "\"rental_duration\":" + rentaldur.value + "," +
                                "\"length\":" + len.value + "," +
                                "\"rental_rate\":" + rentalrate.value + "," +
                                "\"replacement_cost\":" + replacementcost.value + "," +
                                "\"rating\":\"" + rating.value + "\"," +
                                "\"special_features\":\"" + specialfeatures.value + "\"," +
                                "\"description\":\"" + description.value + "\"," +
                                "\"fulltext\":\"''chamber'':1 ''fate'':4 ''husband'':11 ''italian'':2 ''monkey'':16 ''moos'':8 ''must'':13 ''nigeria'':18 ''overcom'':14 ''reflect'':5\"," +
                                "\"storeaddress\":\"" + storeaddress.value + "\"," +
                            "}";
    InterfaceAPIOBJ.addFilmFunc();
}

const responseToAddNewFilm = function(jsonData){
    const res = JSON.parse(jsonData);

    if(res.result == "error"){
        const errorContext = document.querySelector(".add-film-form h4");
        errorContext.innerHTML = res.data;
    }
    else{
        const filmsresults = document.querySelector(".films-results");
        if(filmsresults == null)return false;

        const newFilmElement = document.createElement("div");
        newFilmElement.className = "film-element";
        newFilmElement.innerHTML = '<div class="film-element-img">' +
                                        '<img src="'+ getImg() +'" alt="film-element-img"/>' +
                                    '</div>' +
                                    '<div class="film-element-layer_one"></div>' +
                                    '<div class="film-element-layer_two"></div>' +
                                    '<div class="film-element-content">' +
                                        '<h3>'+ res.data.title +'</h3>' +
                                        '<h2>'+ res.data.len +' mins</h2>' +
                                        '<div class="film-element-divider">' +
                                            '<h4>'+ res.data.release +'</h4>' +
                                            '<h4>'+ res.data.rating +'</h4>' +
                                        '</div>' +
                                    '</div>';
        filmsresults.insertBefore(newFilmElement, filmsresults.children[0]);
        closeAddfilm();
    }
}




const openAddClient = function(){
    if(document.querySelector(".notifications-tab-add-client-dialogue"))
        document.querySelector(".notifications-tab-add-client-dialogue").style.display = "block";
}

const closeAddClient = function(){
    if(document.querySelector(".notifications-tab-add-client-dialogue"))
        document.querySelector(".notifications-tab-add-client-dialogue").style.display = "none";
}

const openEditClient = function(id){
    if(document.querySelector(".notifications-tab-edit-client-dialogue"))
        document.querySelector(".notifications-tab-edit-client-dialogue").style.display = "block";
    currentlyEditingClientID = id;
}

const closeEditClient = function(){
    if(document.querySelector(".notifications-tab-edit-client-dialogue"))
        document.querySelector(".notifications-tab-edit-client-dialogue").style.display = "none";
    currentlyEditingClientID = "";
}




const deleteClient = function(id){
    const clientEl = document.getElementById(id);
    if(clientEl != null)clientEl.remove();

    JAVA__READABLE__TEXT = id;
    InterfaceAPIOBJ.deleteClient();
}

const addNewClientToDatabase = function(){
    return false;
    //perform backend ADD;
}

const upDateClientDataDatabase = function(){
    return false;
    //perform backend UPDATE;
}

const searchClient = function(){
    JAVA__READABLE__TEXT = document.getElementById("clientSearchAttribute").value;
    InterfaceAPIOBJ.searchForClient();
}





const cleanUI = function(){document.querySelector(".main-app").innerHTML = "";}