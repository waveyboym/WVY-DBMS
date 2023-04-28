# WVY-DBMS

NB: The instructions in the readme file on github are easier to follow, more well formated and even have
images so access them here: https://github.com/waveyboym/WVY-DBMS/blob/main/README.md
I included this file as it is a requirement in the prac spec.


A movie database application with HTML, CSS and Javascript as the frontend, and Java, more
specifically JAVAFX on the backend and a postgresql database

# TL; DR

1.  This project was done for assignment 4 of COS221
2.  This project is hosted and done in VS Code with the Maven build tool
3.  You can fork and edit and play around with this project(if you want
    to) and even distribute as long as you link back to this repo as
    original source
4.  Although this app makes use of JAVAFX, I
    never used native functionality to design the app but rather embeded
    a web app into the webview. I made this decision because of time
    constraints.
5.  The UI was fully designed in figma. 
    Click here to access the design file: 
        https://drive.google.com/file/d/1ul2-0aOcYZJKbd5Npo7ZbMOWKqbwjlWg/view?usp=sharing 
    (Note you need to have figma installed in order to open fig files)

# How to use this project

***NB: if you feel these instructions are unclear or confusing, please
have a look below them, I have included a section with links to other
forms such as instructions from VS code and JAVAFX's for setup which I
used when I was setting up everything.*** 
1. First download and install VS Code

2. Follow this article : https://mkyong.com/maven/how-to-install-maven-in-windows/
to download maven and set the correct path variables. Also make sure you
   hava java installed. I used jdk version 20: https://www.oracle.com/java/technologies/javase/jdk20-archive-downloads.html. 

3. In VS code open the extensions tab and download and install the Java
   extension pack: https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack

4. At the top of the nav bar in vs code, there should be tab titled as view. Click
   on that then click on command pallete... and you will get a popup.

5. Search "crate java project" in the searchbar of the popup that recently appeared 

6. Click on the first reulst from search, it should be titled as "Java: Create Java Project..." 
   then a new popup will appear with various choices 

7. Click on the option that says Maven create from archetype 

8. Another popup will appear but you can ignore it. To make it go away, click on
   the tab titled as view in the navbar. Click on command pallete and then
   when the command pallete appears, click anywhere outside of the command
   pallete popup and it will go away. 

9. Download and install postgresql.

10. Set up the postgresql database by following the instructions on this
    article: https://www.postgresqltutorial.com/postgresql-getting-started/load-postgresql-sample-database/
    I used pgAdmin as it was far easier for me to do so. The database file you need is linked here: 
    https://drive.google.com/file/d/1DJPTJlTi9a4xgYaJBDIHHTPmft_xh_3_/view 
    Once you have imported the database in postgresql, rename it to `u21546551_sakila`
 
11. Fill in the correct details in the config file which you downloaded from this repo:
    https://github.com/waveyboym/WVY-DBMS/blob/main/wvydbms/src/main/resources/config.properties

12. Now that that is done, go back to VS code and click to open the
    explorer or press CTRL-SHIFT-E on your keyboard to open the explorer. It
    usually has a file looking icon.

13. At the bottom of that side bar that would have opened, you should
    see a tab named as MAVEN. Before you click on it. Add the folder containing this repo to your workspace.

14. If you don't know how to add a folder to a workspace:
    https://code.visualstudio.com/docs/editor/multi-root-workspaces#:~:text=The%20File%20%3E%20Add%20Folder%20to,to%20add%20or%20remove%20folders

15. Now click on that MAVEN tab. 

16. Click on wvydbms. 

17. Click on plugins 

18. Click on javafx 

19. Please double check to make sure you have done every step correctly otherwise Java may
    throw errors and Java errors are not always the easiest to deal with.

20. If everything is ready, Click on run

21. The application should compile(could take some time) and launch.

If you have any trouble setting this application up and need clearer
guidance/instructions, please send me an email at: u21546551@tuks.co.za

# Other forms of setup instructions

1.  VS code's official setup instructions:
    https://code.visualstudio.com/docs/java/java-gui
2.  Javafx's official setup instructions:
    https://openjfx.io/openjfx-docs/
