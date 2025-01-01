package de.hsfd.binarytreevis.controller.rbt;

import de.hsfd.binarytreevis.controller.TreePane;
import de.hsfd.binarytreevis.services.Author;
import de.hsfd.binarytreevis.services.TreeService;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

@Author(name = "Ankit Sharma", date = "20 Nov 2018")
public class RBT_Pane extends TreePane {

    public RBT_Pane( TreeService<Integer> tree, Pane mainCanvas) {
        super(tree, mainCanvas);
        mainCanvas.setBackground(new Background(new BackgroundFill(Color.web("#FAF0E6"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @Override
    public void displayTree(){
        this.getCanvas().getChildren().clear();
        if(this.getTree().getRoot() != null){
            displayTree(this.getTree().getRoot(), this.getCanvas().getWidth() / 2, vGap, this.getCanvas().getWidth() / 4, null);
        }
    }
}
