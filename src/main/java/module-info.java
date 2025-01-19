module de.hsfd.binarytreevis {
    requires javafx.fxml;
    requires javafx.web;
    requires txtmark;
    requires guru.nidi.graphviz;
    requires java.desktop;
    requires java.logging;
    requires jdk.compiler;


    opens de.hsfd.binarytreevis to javafx.fxml;
    exports de.hsfd.binarytreevis;
}