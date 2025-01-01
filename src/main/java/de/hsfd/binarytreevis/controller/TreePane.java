package de.hsfd.binarytreevis.controller;

import de.hsfd.binarytreevis.controller.avl.AVL_Pane;
import de.hsfd.binarytreevis.controller.bst.BST_Pane;
import de.hsfd.binarytreevis.controller.rbt.RBT_Pane;
import de.hsfd.binarytreevis.services.Author;
import de.hsfd.binarytreevis.services.TreeNode;
import de.hsfd.binarytreevis.services.TreeService;
import de.hsfd.binarytreevis.services.avl.AVLTree;
import de.hsfd.binarytreevis.services.bst.BSTree;
import de.hsfd.binarytreevis.services.rbt.RBTree;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;


/*
 * This is an extended abstract class by all the tree visualizers
 * displayTree() is an abstract method which is implemented by all the visualizers
 */
@Author(name = "Agha Muhammad Aslam", date = "12 Dec 2023")
public abstract class TreePane {

    protected final double vGap = 50;

    private final TreeService<Integer> tree;

    private final Pane mainCanvas;

    public Pane getCanvas( ) {
        return mainCanvas;
    }

    public TreeService<Integer> getTree( ) {
        return tree;
    }

    protected TreePane( TreeService<Integer> tree, Pane mainCanvas ) {
        this.mainCanvas = mainCanvas;
        this.tree = tree;
    }

    public abstract void displayTree();

    /**
     * Displays a tree structure on a canvas with customizable layout and styling.
     * This method is implemented by all the visualizers
     * It displays the tree on the pane by calling displayTree() recursively.
     * <p> If color is null then it must be RBT.
     * The color of the node is set according to the color of the node in the RBTree.</p>
     *
     * @param root The root node of the tree.
     * @param x The x-coordinate of the current node.
     * @param y The y-coordinate of the current node.
     * @param hGap The horizontal gap between nodes.
     * @param color The color to fill the nodes.
     */
    @SuppressWarnings({"UnnecessaryLocalVariable", "CommentedOutCode"})
    protected void displayTree(TreeNode<Integer> root, double x, double y, double hGap, Color color) {
        // Dynamically adjust horizontal gap based on depth
//        int depth = root.getHeight();
//        double adjustedHGap = hGap / (depth); // (dept) only if the depth is 0, otherwise it should be (depth + 1)
        double adjustedHGap = hGap;

        if (root.getLeft() != null) {
            mainCanvas.getChildren().add(new Line(x - adjustedHGap, y + vGap, x, y));
            displayTree(root.getLeft(), x - adjustedHGap, y + vGap, hGap / 2, color);
        }

        if (root.getRight() != null) {
            mainCanvas.getChildren().add(new Line(x + adjustedHGap, y + vGap, x, y));
            displayTree(root.getRight(), x + adjustedHGap, y + vGap, hGap / 2, color);
        }

        // Calculate the radius based on the number of digits
        Circle circle = createAdjustableCircle(x, y, root.getData());
        if(root.getColor() == null) {
            circle.setFill(color);
            circle.setStroke(Color.BLACK);
        } else setNodeColour(root, circle);

        mainCanvas.getChildren().addAll(circle, setText(root, x, y));
    }

    private void setNodeColour(TreeNode<Integer> root, Circle circle) {
        circle.setStroke(Color.BLACK);
        if(root.getColor() == TreeNode.COLOR.RED)
            circle.setFill(Color.INDIANRED);
        else {
            circle.setFill(Color.GRAY);
        }
    }


    protected Text setText( TreeNode<Integer> root, double x, double y ) {
        // Create and position the text
        Text text = new Text(root.getData() + "");
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();
        text.setX(x - textWidth / 2); // Centering the text horizontally
        text.setY(y + textHeight / 4); // Centering the text vertically
        return text;
    }

    protected Circle createAdjustableCircle( double x, double y, Integer number) {
        int length = number == null ? "null".length() - 2 : number.toString().length();
        // Example: Base radius of 15 and increase by 5 for each additional digit
        int radius = 15 + (length - 1) * 5;
        return new Circle(x, y, radius);
    }

    /**
     * Static method that copies an existing TreePane into a new instance.
     * @param original The original TreePane to copy.
     * @return A newly created TreePane with the same properties as original.
     */
    public static TreePane copyOf(TreePane original) {

        if (original == null) {
            return null;
        }

        // Initialize a new TreePane with the same tree as-in-original
        // Assuming there's a TreePane concrete class implementation

        return switch (original) {
            case AVL_Pane _ -> new AVL_Pane(new AVLTree<>(original.tree), original.mainCanvas);
            case BST_Pane _ -> new BST_Pane(new BSTree<>(original.tree), original.mainCanvas);
            case RBT_Pane _ -> new RBT_Pane(new RBTree<>(original.tree), original.mainCanvas);
            default -> throw new RuntimeException("Unknown TreePane type");
        };

        // You might want to copy other properties from the original
        // This will be specific to your implementation of TreePane
        // copied.setSomeProperty(original.getSomeProperty());
    }

}

