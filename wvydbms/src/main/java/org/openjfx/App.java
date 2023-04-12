package org.openjfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.io.IOException;
import javafx.concurrent.Worker.State;
import java.util.Objects;
import netscape.javascript.JSObject;

import javafx.stage.StageStyle;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        try{
            WebView webView = new WebView();
			final WebEngine webEngine = webView.getEngine();
			Interfaceapi interfaceobj = new Interfaceapi(webEngine, stage); //init app and get init data from sql table

			webEngine.load(Objects.requireNonNull(App.class.getResource("frontend/index.html")).toExternalForm());
			webEngine.getLoadWorker().stateProperty().addListener(
					(ov, oldState, newState) -> {
						if (newState == State.SUCCEEDED){
                            interfaceobj.fillDashBoard();
							JSObject window = (JSObject) webEngine.executeScript("window");
							window.setMember("InterfaceAPIOBJ", interfaceobj);
						}
					});
            webView.setOnMousePressed(pressEvent -> {
                webView.setOnMouseDragged(dragEvent -> {
                    if((pressEvent.getSceneX() >= 0 && pressEvent.getSceneX() <= 964) 
                    && (pressEvent.getSceneY() >= 0 && pressEvent.getSceneY() <= 55)){
                        stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                        stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
                    }
                });
            });

            VBox vBox  = new VBox(webView);
            Scene scene = new Scene(vBox, 1066, 600);
            stage.setTitle("wvydbms");
            stage.initStyle(StageStyle.UNDECORATED);
    
            String imageUrl = App.class.getResource("frontend/assets/app-icon.png").toExternalForm();
            stage.getIcons().add(new Image(imageUrl));
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e){e.printStackTrace();}
    }

    public static void main(String[] args) {
        launch();
    }

}