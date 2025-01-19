package de.hsfd.binarytreevis.controller;

import com.github.rjeschke.txtmark.Processor;
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
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    private final WebView messageBox = (WebView) mainScreen.lookup("#messageBox");

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

            messageBox.getStyleClass().add("browser");

            addFunctionalities(status,textField, insert, delete, nextButton,
                               prevButton, history, tree, view, treePanes, index,
                               mainCanvas);
            addHistoryFunctionalities(status, nextButton, prevButton,
                                      index, treePanes, tree);

            tree.setStatus(nodesView::setText);
            tree.setHistoryService(s -> reparse(s,messageBox));

        } catch (NullPointerException e){
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            updateStatus("Something went wrong!\n" + e.getMessage(), StatusType.ERROR);
        }
        return mainScreen;
    }

    private void reparse(String s, WebView messageBox) {
        try {
            // Define the HTML template
            String doc = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\">"
                    + "<link href=\"%s\" rel=\"stylesheet\"/></head><body>%s%s</body></html>";

            // Define the CSS file link
            String css = "https://raw.github.com/nicolashery/markdownpad-github/master/markdownpad-github.css";
                        //"https://kevinburke.bitbucket.org/markdowncss/markdown.css";

            // Define the JavaScript for auto-scrolling
            String scrollScript = "<script>window.onload = function() { "
                    + "window.scrollTo(0, document.body.scrollHeight); "
                    + "}</script>";

            // Process and adjust the input text
            String textHtml = Processor.process(s);
            String adjustedHtml = adjustCharactersInSVG(textHtml);

            // Combine all components into the final HTML
            String html = String.format(doc, css, adjustedHtml, scrollScript);

            // Load the HTML content into the WebView
            messageBox.getEngine().loadContent(html, "text/html");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adjusts the characters within the <text> elements of an SVG block in the provided HTML string.
     * This method processes the HTML to find <svg> tags and then adjusts the content of <text> elements
     * within those <svg> tags to ensure correct rendering of characters.
     *
     * @param textHtml The HTML string containing SVG elements to be processed.
     * @return A new HTML string with adjusted characters within the <text> elements of SVG blocks.
     */
    private String adjustCharactersInSVG(String textHtml) {
        // Debugging step: Log processed HTML
        // System.out.println("Processed HTML: " + textHtml);

        // Match the <svg> tag and process its content
        Pattern svgPattern = Pattern.compile("<svg[^>]*>.*?</svg>", Pattern.DOTALL);
        Matcher svgMatcher = svgPattern.matcher(textHtml);
        StringBuilder adjustedText = new StringBuilder();

        while (svgMatcher.find()) {
            String svgContent = svgMatcher.group(); // Extract the entire <svg> block

            // Match and adjust the <text> content inside the <svg>
            Pattern textPattern = Pattern.compile("(<text[^>]*>)(.*?)(</text>)", Pattern.DOTALL);
            StringBuilder updatedSvgContent = getUpdatedSvgContent(textPattern, svgContent);

            // Replace the original <svg> block with the updated one
            svgMatcher.appendReplacement(adjustedText, updatedSvgContent.toString());
        }
        svgMatcher.appendTail(adjustedText);

        return adjustedText.toString();
    }

    /**
     * <p>
     * Adjusts the characters within the <text> elements of an SVG block in the provided HTML string.
     * This method processes the HTML to find <svg> tags and then adjusts the content of <text> elements
     * within those <svg> tags to ensure correct rendering of characters. 
     * </p>
     * This method is used only by {@link #adjustCharactersInSVG(String)} to process the content of <text> elements.
     *
     * @param textPattern The pattern to match <text> elements within an SVG block.
     * @param svgContent The SVG content to be processed.
     * @return A new StringBuilder containing the SVG content with adjusted characters within the <text> elements.
     */
    private static StringBuilder getUpdatedSvgContent(Pattern textPattern, String svgContent) {
        Matcher textMatcher = textPattern.matcher(svgContent);
        StringBuilder updatedSvgContent = new StringBuilder();

        while (textMatcher.find()) {
            String textOpeningTag = textMatcher.group(1); // <text ...>
            String textContent = textMatcher.group(2);    // Characters inside <text>...</text>
            String textClosingTag = textMatcher.group(3); // </text>

            StringBuilder incrementedContent = new StringBuilder();

            // Increment each character in the text content
            for (char c : textContent.toCharArray()) {
                incrementedContent.append((char) (c + 1));
            }

            // Reassemble the <text> block with updated content
            textMatcher.appendReplacement(updatedSvgContent,
                    textOpeningTag + incrementedContent + textClosingTag);
        }
        textMatcher.appendTail(updatedSvgContent);
        return updatedSvgContent;
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
                String s = tree.getRecordList().get(index.get());
                reparse(s,messageBox);
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
                String s = tree.getRecordList().get(index.get());
                reparse(s,messageBox);
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
                                               index, pane, messageBox, view);
                next.setDisable(index.get() == treePanes.size() - 1);
            } else {
                setHistoryDefaultConfiguration(statusPage,"History",isHistorySelected,textField, insert,
                                               delete, next, prev, history, tree, treePanes,
                                               index, pane, messageBox, view);
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
//                    messageBox.appendText(tree.getRecordList().get(index.incrementAndGet()));
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
                                                 Pane pane, WebView messageBox, TreePane view
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
            String s = tree.getRecordList().get(index.get());
            reparse(s,messageBox);
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
