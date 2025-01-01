package de.hsfd.binarytreevis.controller;

import de.hsfd.binarytreevis.services.Author;
import de.hsfd.binarytreevis.services.TreeException;
import de.hsfd.binarytreevis.services.TreeService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;



@Author(name = "Agha Muhammad Aslam", date = "12 Dec 2023")
public abstract class TreeController extends Application {
    enum StatusType {
        WARNING,
        ERROR,
        NORMAL
    }

    private Timeline blinkAnimation;

    private final String TITLE;

    private final TreeService<Integer> tree;

    public TreeService<Integer> getTree( ) {
        return tree;
    }

    private static final String GROUP_NAME = "/" + TreeController.class.getPackageName().replace('.', '/') + "/";

    private boolean actionMode = true; // if there is an error within the application, then set this to false
    //indicates that the application is not working properly and the user should restart the application

    private final Parent mainScreen = FXMLLoader.load(
            Objects.requireNonNull(TreeController.class.getResource(GROUP_NAME + "TreeView.fxml"),
                    () -> {
                        System.err.println("Failed to load resource from GROUP_NAME: " + GROUP_NAME);
                        return null;
                    }));

    private final HBox statusBox = (HBox) mainScreen.lookup("#statusBox");

    private final Label statusLabel = (Label) mainScreen.lookup("#statusLabel");

    private final Pane mainCanvas = (Pane) mainScreen.lookup("#canvas");

    private final TextArea textAreaBox = (TextArea) mainScreen.lookup("#textAreaBox");

    public Pane getMainCanvas( ) {
        return mainCanvas;
    }

    protected TreeController(String title, TreeService<Integer> tree ) throws IOException {
        this.TITLE = title;
        this.tree = tree;
    }

    // (every insertion or deletion of a node in a tree will be saved in this list)
    private final ArrayList<TreePane> treePanes = new ArrayList<>(); // list of all the tree panes

    private final AtomicInteger index = new AtomicInteger(-1); // index of the current tree pane Lists

    @Override
    public abstract void start(Stage primaryStage);

    AtomicBoolean isHistorySelected = new AtomicBoolean(false);

    public Parent setComponent( TreePane view ) throws IOException {

        try {
            Button insert = (Button) mainScreen.lookup("#insertButton");
            Button delete = (Button) mainScreen.lookup("#deleteButton");
            TextField textField = (TextField) mainScreen.lookup("#textField");
            Platform.runLater(textField::requestFocus);

            Text nodesView = (Text) mainScreen.lookup("#nodesView");
            ToggleButton history = (ToggleButton) mainScreen.lookup("#historyButton");

            Button nextButton = (Button) mainScreen.lookup("#nextButton");
            Button prevButton = (Button) mainScreen.lookup("#prevButton");
            Text status = (Text) mainScreen.lookup("#pageView");

            textAreaBox.setWrapText(true);

            addFunctionalities(status,textField, insert, delete, nextButton,
                               prevButton, history, tree, view, treePanes, index,
                               mainCanvas);
            addHistoryFunctionalities(status, nextButton, prevButton,
                                      index, treePanes, tree);

            tree.setStatus(nodesView::setText);
            tree.setHistoryService(s -> {
                textAreaBox.clear(); // every time the history accepts a record, clear all the log, and add a new one!
                textAreaBox.appendText(s);
            });

        } catch (NullPointerException e){
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            updateStatus("Something went wrong!\n" + e.getMessage(), StatusType.ERROR);
        }
        return mainScreen;
    }


    /**
     * Sets the stage for the JavaFX application.
     *
     * @param parent The parent pane to be displayed in the stage.
     * @param primaryStage The primary stage of the JavaFX application.
     * @param view The tree pane used to display the tree. (In case the width and height of the scene changes)
     */
    public void setStage(Parent parent, Stage primaryStage, TreePane view) {
        Scene scene = new Scene(parent);

        // Add listeners to scene width and height properties
        scene.widthProperty().addListener((_, _, _) -> {
            if(isHistorySelected.get()) {
                // Redrawing the current view from treePanes at index
                treePanes.get(index.get()).displayTree();
            } else {
                view.displayTree();
            }
        });

        scene.heightProperty().addListener((_, _, _) -> {
            if(isHistorySelected.get()) {
                // Redrawing the current view from treePanes at index
                treePanes.get(index.get()).displayTree();
            } else {
                view.displayTree();
            }
        });

        primaryStage.setTitle(TITLE);
        primaryStage.getIcons().add(new Image("file:data/tree.png"));
        primaryStage.setScene(scene);

        primaryStage.fullScreenProperty().addListener((_, _, _) -> {
            if(isHistorySelected.get()) {
                // Redrawing the current view from treePanes at index
                treePanes.get(index.get()).displayTree();
            } else {
                view.displayTree();
            }
        });

        primaryStage.setMinWidth(820);
        primaryStage.setMinHeight(560);
        updateStatus("Welcome to " + TITLE + "!", StatusType.NORMAL);
        primaryStage.show();
    }


    private void
    addHistoryFunctionalities(
            Text status, Button nextButton,
            Button prevButton, AtomicInteger index, ArrayList<TreePane> treePanes,
            TreeService<Integer> tree
    )
    {

        nextButton.setOnAction(_ -> {
            if(index.get() == treePanes.size() - 1){
                updateStatus("You have reached the end of the history!", StatusType.WARNING);
                return;
            } else {
                updateStatus("History mode", StatusType.NORMAL);
                prevButton.setDisable(false);
            }

            TreePane nextView = treePanes.get(index.incrementAndGet());
            nextView.displayTree();

            if( !tree.getRecordList().isEmpty() ) {
                textAreaBox.clear();
                textAreaBox.appendText(tree.getRecordList().get(index.get()));
            }

            status.setText((index.get() + 1) + "/" + treePanes.size());   // update the status

            if (index.get() == treePanes.size() - 1) nextButton.setDisable(true);
        });

        prevButton.setOnAction(_ -> {
            if(index.get() == 0){
                updateStatus("You have reached the start of the history!", StatusType.WARNING);
                return;
            } else {
                updateStatus("History mode", StatusType.NORMAL);
                nextButton.setDisable(false);
            }

            TreePane prevView = treePanes.get(index.decrementAndGet());
            prevView.displayTree();

            if( !tree.getRecordList().isEmpty() ) {
                textAreaBox.clear();
                textAreaBox.appendText(tree.getRecordList().get(index.get()));
            }

            status.setText((index.get() + 1) + "/" + treePanes.size());   // update the status

            if(index.get() == 0) prevButton.setDisable(true);
        });

    }

    public void
    addFunctionalities(
            Text statusPage, TextField textField,
            Button insert, Button delete, Button next, Button prev,
            ToggleButton history, TreeService<Integer> tree, TreePane view,
            ArrayList<TreePane> treePanes, AtomicInteger index, Pane pane
    ){

        history.setOnAction(_ -> {
            this.isHistorySelected.set(history.isSelected());
            if (history.isSelected()) {
                setHistoryDefaultConfiguration(statusPage,"Action",isHistorySelected,textField, insert,
                                               delete, next, prev, history, tree, treePanes,
                                               index, pane, textAreaBox, view);
                next.setDisable(index.get() == treePanes.size() - 1);
            } else {
                setHistoryDefaultConfiguration(statusPage,"History",isHistorySelected,textField, insert,
                                               delete, next, prev, history, tree, treePanes,
                                               index, pane, textAreaBox, view);
            }
        });

        insert.setOnAction(_ -> insertNewNode(statusPage,textField, tree, view, treePanes, index));

        delete.setOnAction(_ ->{
            if(!actionMode) return; // there is an error within the application, so don't do anything

            int key;

            if( textField.getText().isEmpty() ) {
                if(tree.getRoot() == null){
                    updateStatus("Nothing to delete!", StatusType.WARNING);
                    return;
                }
                // if the text field is empty, then delete the last node inserted
                try {
                    key = tree.lastInserted();
                } catch (Exception ex) {
                    updateStatus(ex.getMessage(), StatusType.ERROR);
                    throw new RuntimeException(ex);
                }
            } else {
                try{
                    key = Integer.parseInt(textField.getText());
                } catch (NumberFormatException ex){
                    updateStatus("You have entered an invalid input!\nInteger numbers only!\n" + ex.getMessage(), StatusType.WARNING);
                    return;
                }
            }

            if(!tree.search(key)){
                view.displayTree();
                updateStatus("You have entered a value which is not present in the tree!", StatusType.WARNING);
            }
            else{
                try {
                    tree.delete(key);
                } catch ( Exception ex ) {
                    //noinspection CallToPrintStackTrace
                    ex.printStackTrace();
                    String msg = tree.getRecordList().get(index.get())+ "----\n" + ex.getMessage();
                    tree.getRecordList().add(msg);
                    textAreaBox.appendText(tree.getRecordList().get(index.incrementAndGet()));
                    updateStatus("Trying delete: " + key +"\nSomething went wrong,\nCheck the console!", StatusType.ERROR);
                } catch (TreeException ex) {
                    throw new RuntimeException(ex);
                }
                view.displayTree();
                treePanes.add(TreePane.copyOf(view));
                index.set(treePanes.size() - 1);
                statusPage.setText((index.get() + 1) + "/" + treePanes.size());   // update the statusPage
                updateStatus("Node deleted successfully!", StatusType.NORMAL);
            }

            textField.clear();
        });

        //set a keyboard listener if enter is pressed then insertNewNode
        textField.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) insertNewNode(statusPage,textField, tree, view, treePanes, index);
        });
    }

    private void setHistoryDefaultConfiguration( Text status, String str,
                                                 AtomicBoolean isHistorySelected,
                                                 TextField textField, Button insert,
                                                 Button delete, Button next, Button prev,
                                                 ToggleButton history, TreeService<Integer> tree,
                                                 ArrayList<TreePane> treePanes, AtomicInteger index,
                                                 Pane pane, TextArea textAreaBox, TreePane view
    ) {
        history.setText(str);
        updateStatus((str.equals("History") ? "Action" : "History") +" mode", StatusType.NORMAL);

        textField.setDisable(isHistorySelected.get());
        insert.setDisable(isHistorySelected.get());
        delete.setDisable(isHistorySelected.get());
        next.setDisable(!isHistorySelected.get());
        prev.setDisable(!isHistorySelected.get());

        pane.getChildren().clear();
        index.set(treePanes.size() - 1);
        TreePane thisView = treePanes.get(index.get());

        if( !tree.getRecordList().isEmpty() ) {
            textAreaBox.clear();
            textAreaBox.appendText(tree.getRecordList().get(index.get()));
        }

        if(!thisView.getTree().equals( view.getTree())) {
            updateStatus("Something not right with the tree!\nCheck the console!", StatusType.ERROR);
            throw new RuntimeException("Something not right with the tree:\n - Check if the copyOf() works as intended."
                                               + "\nthisView:" + thisView.getTree() + "\nview:" + view.getTree());
        }

        thisView.displayTree();

        status.setText((index.get() + 1) + "/" + treePanes.size());   // update the status
    }

    private void  insertNewNode( Text historyTimeLine,TextField textField, TreeService<Integer> tree, TreePane view, ArrayList<TreePane> treePanes, AtomicInteger index ) {

        if(!actionMode) return; // there is an error within the application, so don't do anything

        if( textField.getText().isEmpty() ) {
            updateStatus("You haven't entered anything!", StatusType.WARNING);
        }
        else {
            try {
                int key = Integer.parseInt(textField.getText());
                if ( tree.search(key) ) {
                    view.displayTree();
                    updateStatus("You have entered a duplicate value!", StatusType.WARNING);
                } else {
                    try {
                        tree.main_insert(key);
                        view.displayTree();
                        treePanes.add(TreePane.copyOf(view));
                        index.set(treePanes.size() - 1);
                        historyTimeLine.setText((index.get() + 1) + "/" + treePanes.size());   // update the status
                        textField.clear();
                        updateStatus("Node inserted successfully!", StatusType.NORMAL);
                    } catch ( Exception | TreeException e) {
                        updateStatus("""
                                Something went wrong!
                                Failed to add element to the tree.
                                Please check insert function!
                                """ + e.getMessage(), StatusType.ERROR);
                        throw new RuntimeException(e);
                    }
                }
            } catch ( NumberFormatException ex ) {
                updateStatus("You have entered an invalid input!\nInteger numbers only", StatusType.WARNING);
            }
        }
    }

    protected static void showErrorBox( String s ) {
        Alert alert = new Alert(Alert.AlertType.ERROR, s, ButtonType.OK);
        alert.getDialogPane().setMinHeight(80);
        alert.show();
    }

    @SuppressWarnings ("ClassEscapesDefinedScope")
    public void updateStatus( String message, StatusType statusType) {
        if(!actionMode) return; // there is an error within the application, so don't do anything

        if(statusType == StatusType.ERROR) {
            message += """
    
                    -----
                    Delete and Insert will not working.
                    Please debug and restart the application!""";

            ArrayList<String> recordList = tree.getRecordList();
            String log = !recordList.isEmpty() ? recordList.get(index.get()) + message : message;
            textAreaBox.appendText(log);

        }

        statusLabel.setText(statusType == StatusType.ERROR ? "ERROR" : message);

        if (blinkAnimation != null) {
            blinkAnimation.stop(); // Stop previous animation if running
        }

        if(statusType == StatusType.ERROR) actionMode = false;
        switch (statusType) {
            case WARNING -> startWarningBlinking();
            case ERROR -> statusBox.setStyle("-fx-background-color: red;");
            case NORMAL -> statusBox.setStyle("-fx-background-color: #49de49;");
        }
    }

    private void startWarningBlinking() {
        statusBox.setStyle("-fx-background-color:orange;");
        blinkAnimation = new Timeline(
                new KeyFrame(Duration.seconds(1.7), _ -> statusBox.setStyle("-fx-background-color:orange;")),
                new KeyFrame(Duration.seconds(1), _ -> statusBox.setStyle("-fx-background-color: transparent;"))
        );
        blinkAnimation.setCycleCount(Timeline.INDEFINITE);
        blinkAnimation.play();
    }

}
