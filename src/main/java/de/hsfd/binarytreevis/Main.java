package de.hsfd.binarytreevis;

import de.hsfd.binarytreevis.controller.TreeController;
import de.hsfd.binarytreevis.controller.avl.AVL_Controller;
import de.hsfd.binarytreevis.controller.bst.BST_Controller;
import de.hsfd.binarytreevis.controller.rbt.RBT_Controller;
import de.hsfd.binarytreevis.services.Author;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

@Author (name = "Ankit Sharma", date = "20 Nov 2018")
@Author (name = "Agha Muhammad Aslam", date = "12 Dec 2023")
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            final Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("View.fxml"), () -> {
                System.err.println("Failed to load resource View.fxml, check again the path!");
                return null;
            }));
            primaryStage.setTitle("Trees");
            primaryStage.setScene(new Scene(root, 410, 340));
            primaryStage.getIcons().add(new Image("file:data/icon.jpg"));

            Button avl = (Button) root.lookup("#avl");
            avl.setOnAction(_ -> {
                try {
                    setStage(new AVL_Controller());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            Button bts = (Button) root.lookup("#bst");
            bts.setOnAction(_ -> {
                try {
                    setStage(new BST_Controller());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            Button rbt = (Button) root.lookup("#rbt");
            rbt.setOnAction(_ -> {
                try {
                    setStage(new RBT_Controller());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            primaryStage.show();
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            showAlert();
        }
    }

    private void setStage( TreeController menu){
        Stage menuStage = new Stage();
        menu.start(menuStage);
        menuStage.show();
    }

    private void showAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Loading Application");
        alert.setHeaderText(null);
        alert.setContentText("Error loading Application, please check the logs and console!");
        alert.showAndWait();
    }
}
