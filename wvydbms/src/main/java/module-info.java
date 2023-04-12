module org.openjfx {
    requires javafx.controls;
    requires transitive javafx.web;
    requires javafx.fxml;
    requires jdk.jsobject;
    requires transitive java.sql;
    //requires json.simple;

    opens org.openjfx to javafx.fxml;
    exports org.openjfx;
}
