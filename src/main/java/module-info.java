module org.example.game {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens org.example.game to javafx.fxml;
    exports org.example.game;
}