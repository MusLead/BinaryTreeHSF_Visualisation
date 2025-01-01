package de.hsfd.binarytreevis.controller.avl;

import de.hsfd.binarytreevis.controller.TreePane;
import de.hsfd.binarytreevis.services.Author;
import de.hsfd.binarytreevis.services.TreeService;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;


@Author (name = "Ankit Sharma", date = "12 Oct 2018")
public class AVL_Pane extends TreePane {

    public AVL_Pane( TreeService<Integer> tree, Pane mainCanvas) {
        super(tree, mainCanvas);
        this.getCanvas().setBackground(
                new Background(new BackgroundFill(Color.web("#" + "9ACD32"),
                                                        CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @Override
    public void displayTree(){
        this.getCanvas().getChildren().clear();
        if(this.getTree().getRoot() != null){
            displayTree(this.getTree().getRoot(), this.getCanvas().getWidth() / 2, vGap,
                    getCanvas().getWidth() / 4, Color.SEAGREEN);
        }
    }

}