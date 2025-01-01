package de.hsfd.binarytreevis.controller.avl;

import de.hsfd.binarytreevis.controller.TreeController;
import de.hsfd.binarytreevis.services.Author;
import de.hsfd.binarytreevis.services.avl.AVLTree;
import javafx.stage.Stage;

import java.io.IOException;

@Author (name = "Ankit Sharma", date = "12 Oct 2018")
public class AVL_Controller extends TreeController {

    public AVL_Controller( ) throws IOException {
        super("AVL Visualisation", new AVLTree<>());
    }

    @Override
    public void start(Stage primaryStage){
        final AVL_Pane view = new AVL_Pane(this.getTree(), this.getMainCanvas());
        try {
            this.setStage(this.setComponent(view), primaryStage, view);
        } catch (IOException e) {
            showErrorBox(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
