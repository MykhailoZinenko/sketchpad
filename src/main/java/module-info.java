module com.mykhailozinenko.sketchpad {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.mykhailozinenko.sketchpad to javafx.fxml;
    exports com.mykhailozinenko.sketchpad;
}