module org.example.game {
    requires javafx.controls;
    requires javafx.fxml;

    exports main;

    opens engine to javafx.fxml;
    exports engine;
    exports entity;
    opens entity to javafx.fxml;
    exports world;
    opens world to javafx.fxml;
    exports util;
    opens util to javafx.fxml;
}
