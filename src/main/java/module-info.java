module de.hsfd.binarytreevis {
    requires javafx.controls;
    requires javafx.fxml;


    opens de.hsfd.binarytreevis to javafx.fxml;
    exports de.hsfd.binarytreevis;
}