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
    else if(classToSelect == ".report-tab")selectReportTabUI();
    else if(classToSelect == ".notifications-tab")selectNotificationTabUI();
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
    
    if(StaffUI__JSONDATA.staff.length === 0){
        stafftabresults.innerHTML = "<h2 class='staff-tab-results'>no results found in database</h2>";
        return;
    }
    stafftabresults.innerHTML = "";

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
                            '<div class="add-film-form"></div>' +
                        '</div>';
    populateFilmTab(jsonData);
}

const populateFilmTab = function(jsonData){
    const FilmsUI__JSONDATA = JSON.parse(jsonData);
    const filmsresults = document.querySelector(".films-results");
    if(filmsresults == null)return;

    if(FilmsUI__JSONDATA.films.length === 0){
        filmsresults.innerHTML = "<h2 class='film-tab-results'>no results found in database</h2>";
        return;
    }
    filmsresults.innerHTML = "";

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




const selectReportTabUI = function(){
    const mainApp = document.querySelector(".main-app");
    mainApp.innerHTML = '<div class="report-tab-ui">' +
                    '<div class="report-tab-header">' +
                        '<div class="report-tab-main-header">' +
                            '<h2>report</h2>' +
                            '<div class="download-btn">' +
                                '<img src="assets/download.svg" alt="download"/>' +
                            '</div>' +
                        '</div>' +
                        '<div class="report-tab-sub-header">' +
                            '<h2>store name</h2>' +
                            '<h3>genre name</h3>' +
                            '<h4>number of movies</h4>' +
                        '</div>' +
                    '</div>' +
                    '<div class="report-tab-results">' +
                        '<div class="report-tab-element">' +
                            '<h2>Lorem ipsem doe rosje shue euihe euhgtfe ruuudg shhhsv d</h2>' +
                            '<h3>horror</h3>' +
                            '<h4>34400</h4>' +
                        '</div>' +
                    '</div>' +
                '</div>';
}

const selectNotificationTabUI = function(){
    const mainApp = document.querySelector(".main-app");
    mainApp.innerHTML = '<div class="notifications-tab-ui">' +
                            '<div class="notifications-tab-heading">' +
                                '<h2>notifications</h2>' +
                                '<h3 class="all-clients notifications-tab-currently-selected" onmouseup="selectNotificationALLCLIENTS(false)">all clients</h3>' +
                                '<h3 class="dropped-rental" onmouseup="selectNotificationDROPPEDRENTAL()">dropped rental</h3>' +
                                '<input type="text" placeholder="Search for attribute..."/>' +
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
                                                '<label for="nameoptional">name(optional)</label>' +
                                                '<input name="nameoptional" type="text" id="nameoptional"/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="surnameoptional">surname(optional)</label>' +
                                                '<input name="surnameoptional" type="text" id="surnameoptional"/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="emailoptional">email(optional)</label>' +
                                                '<input name="eamiloptional" type="text" id="emailoptional"/>' +
                                            '</div>' +
                                            '<div>' +
                                                '<label for="activestatusoptional">active status(optional)</label>' +
                                                '<input name="activestatusoptional" type="text" id="activestatusoptional"/>' +
                                            '</div>' +
                                        '</div>' +
                                        '<input type="submit" value="update client info">' +
                                    '</form>' +
                                '</div>' +
                            '</div>' +
                        '</div>';
    selectNotificationALLCLIENTS(true);
}

const selectNotificationALLCLIENTS = function(overRideReturn){
    const selectedSubTab = document.querySelector(".notifications-tab-currently-selected");
    const thisSubTab = document.querySelector(".all-clients");
    const resultsDiv = document.querySelector(".notifications-tab-results");
    
    if(thisSubTab == null || selectedSubTab == null || resultsDiv == null)return;
    if(thisSubTab.classList.contains("notifications-tab-currently-selected") && !overRideReturn)return;
    selectedSubTab.classList.remove("notifications-tab-currently-selected");
    thisSubTab.classList.add("notifications-tab-currently-selected");

    resultsDiv.innerHTML = "";

    resultsDiv.innerHTML += '<div class="client-element" id="client1">' +
                                '<div class="client-details">' +
                                    '<h2>Name: wufug efewbire bweiiheuiw whuweui</h2>' +
                                    '<h2>Surname: wufug efewbire bweiiheuiw whuweui</h2>' +
                                    '<h2>Email: wufug efewbire bweiiheuiw whuweui</h2>' +
                                    '<h2>Active status: active</h2>' +
                                '</div>' +
                                '<div class="client-methods">' +
                                    '<div class="edit-client-btn" onmouseup="openEditClient(\'client1\')">' +
                                        '<img src="assets/edit-client.svg" alt="edit-client-btn"/>' +
                                    '</div>' +
                                    '<div class="delete-client-btn" onmouseup="deleteClient(\'client1\')">' +
                                        '<img src="assets/delete-client.svg" alt="delete-client-btn"/>' +
                                    '</div>' +
                                '</div>' +
                            '</div>';
}

const selectNotificationDROPPEDRENTAL = function(){
    const selectedSubTab = document.querySelector(".notifications-tab-currently-selected");
    const thisSubTab = document.querySelector(".dropped-rental");
    const resultsDiv = document.querySelector(".notifications-tab-results");

    if(thisSubTab == null || selectedSubTab == null || resultsDiv == null)return;
    if(thisSubTab.classList.contains("notifications-tab-currently-selected"))return;
    selectedSubTab.classList.remove("notifications-tab-currently-selected");
    thisSubTab.classList.add("notifications-tab-currently-selected");

    resultsDiv.innerHTML = "";

    resultsDiv.innerHTML += '<div class="dropped-rental-sub-headers">' +
                                '<h2>name</h2>' +
                                '<h2>surname</h2>' +
                                '<h3>email</h3>' +
                                '<h2>active status</h2>' +
                            '</div>' +
                            '<div class="dropped-rental-sub-results">' +
                                '<div class="dropped-rental-sub-element">' +
                                    '<h2>wufug efewbire bweiiheuiw whuweui</h2>' +
                                    '<h3>wufug efewbire bweiiheuiw whuweui</h3>' +
                                    '<h4>wufug efewbire bweiiheuiw whuweui</h4>' +
                                    '<h5>active</h5>' +
                                '</div>' +
                            '</div>';
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

    //perform backend delete;
}

const cleanUI = function(){document.querySelector(".main-app").innerHTML = "";}