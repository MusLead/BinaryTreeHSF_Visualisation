module de.hsfd.binarytreevis {
    requires javafx.fxml;
    requires javafx.web;
    requires txtmark;
    requires org.jetbrains.annotations;


    opens de.hsfd.binarytreevis to javafx.fxml;
    exports de.hsfd.binarytreevis;
}