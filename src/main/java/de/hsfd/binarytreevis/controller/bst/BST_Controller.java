package de.hsfd.binarytreevis.controller.bst;

import de.hsfd.binarytreevis.controller.TreeController;
import de.hsfd.binarytreevis.services.Author;
import de.hsfd.binarytreevis.services.bst.BSTree;
import javafx.stage.Stage;

import java.io.IOException;

@Author (name = "Ankit Sharma", date = "12 Oct 2018")

public class BST_Controller extends TreeController {

    public BST_Controller( ) throws IOException {
        super("BST Visualisation", new BSTree<>());
    }

    @Override
    public void start(Stage primaryStage){
        final BST_Pane view = new BST_Pane(this.getTree(), this.getMainCanvas());
        try {
            this.setStage(this.setComponent(view), primaryStage, view);
        } catch (IOException e) {
            showErrorBox(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
