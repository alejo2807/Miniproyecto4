module com.example.myfirstnavalbattle {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires java.desktop;

    opens com.example.myfirstnavalbattle to javafx.fxml;
    exports com.example.myfirstnavalbattle;
    opens com.example.myfirstnavalbattle.model to javafx.fxml;

    opens com.example.myfirstnavalbattle.controller to javafx.fxml;
    opens com.example.myfirstnavalbattle.controller.setupStage to javafx.fxml;
    opens com.example.myfirstnavalbattle.view to javafx.fxml;
}