module org.example.game {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.apiguardian.api;    // if you annotate your own code with @API

    // expose your packages to others (including the test module):
    exports main;
    exports engine.core;
    exports engine.input;
    exports engine.level;
    exports engine.save;
    exports engine.ui;
    exports entity;
    exports world;
    exports util;

    // allow JavaFX to reflectively instantiate controllers, etc.
    opens main to javafx.fxml;
    opens engine.input to javafx.fxml;
    opens engine.ui to javafx.fxml;
    opens entity to javafx.fxml;
    opens world to javafx.fxml;
    opens util to javafx.fxml;
}
