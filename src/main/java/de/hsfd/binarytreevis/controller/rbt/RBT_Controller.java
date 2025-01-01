package de.hsfd.binarytreevis.controller.rbt;

import de.hsfd.binarytreevis.controller.TreeController;
import de.hsfd.binarytreevis.services.Author;
import de.hsfd.binarytreevis.services.rbt.RBTree;
import javafx.stage.Stage;

import java.io.IOException;

@Author (name = "Ankit Sharma", date = "20 Nov 2018")
@Author (name = "Agha Muhammad Aslam", date = "12 Dec 2023")
public class RBT_Controller extends TreeController {
    public RBT_Controller() throws IOException {
        super("RBT Visualisation",new RBTree<>());
    }

    @Override
    public void start(Stage primaryStage){
        final RBT_Pane view = new RBT_Pane(this.getTree(), this.getMainCanvas() );
        try {
            this.setStage(this.setComponent(view), primaryStage, view);
        } catch (IOException e) {
            showErrorBox(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
