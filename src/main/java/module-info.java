module org.example.game {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens engine to javafx.fxml;
    exports engine;
}