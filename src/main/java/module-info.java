module com.checkersgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.checkersgui to javafx.fxml;
    exports com.checkersgui.UI;
}
