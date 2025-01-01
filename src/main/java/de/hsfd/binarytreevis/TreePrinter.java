/**
 * Author: Murtaza Raja
 * Date: February 27, 2016.
 * Description: This class is used to print the Binary tree as string in a pretty format.
 * GitHub: https://github.com/murtraja/java-binary-tree-printer/tree/master
 */
package de.hsfd.binarytreevis;

import de.hsfd.binarytreevis.services.Author;

import java.util.Scanner;

@Author(name = "Murtaza Raja", date = "27 Feb 2016")
@Author(name = "Agha Muhammad Aslam", date = "31 Dec 2024")
public class TreePrinter {
    private final int data;
    private TreePrinter left;
    private TreePrinter right;

    public TreePrinter(int data, TreePrinter left, TreePrinter right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }

    public void setLeft(TreePrinter left) {
        this.left = left;
    }

    public void setRight(TreePrinter right) {
        this.right = right;
    }

    public TreePrinter getLeft() {
        return this.left;
    }

    public TreePrinter getRight() {
        return this.right;
    }

    public StringBuilder prettyPrint(int height) {
        return generateTreeVisualization(this, 1, height);
    }

    public String prettyPrint() {
        return prettyPrint(getHeight()).toString();
    }

    /**
     * Generates a visual representation of a binary tree in a textual format.
     * The visualization aligns nodes and branches based on their levels and
     * relative positioning in the tree hierarchy.
     *
     * @param root the root node of the tree or subtree to visualize
     * @param currentHeight the current depth level in the tree being processed
     * @param totalHeight the total height of the tree to properly calculate spacing for nodes
     * @return a StringBuilder object containing the textual visualization of the tree
     */
    private StringBuilder generateTreeVisualization(TreePrinter root, int currentHeight, int totalHeight) {
        StringBuilder sb = new StringBuilder();
        int spaces = getSpaceCount(totalHeight - currentHeight + 1);
        if (root == null) {
            // create a 'spatial' block and return it
            String row = String.format("%" + (2 * spaces + 1) + "s%n", "");
            // now repeat this row space+1 times
            String block = new String(new char[spaces + 1]).replace("\0", row);
            return new StringBuilder(block);
        }
        if (currentHeight == totalHeight)
            return new StringBuilder(root.data + "");
        int slashes = getSlashCount(totalHeight - currentHeight + 1);
        sb.append(String.format("%" + (spaces + 1) + "s%" + spaces + "s", root.data + "", ""));
        sb.append("\n");
        // now print / and \
        // but make sure that left and right exists
        char leftSlash = root.left == null ? ' ' : '/';
        char rightSlash = root.right == null ? ' ' : '\\';
        int spaceInBetween = 1;
        for (int i = 0, space = spaces - 1; i < slashes; i++, space--, spaceInBetween += 2) {
            sb.append(" ".repeat(Math.max(0, space)));
            sb.append(leftSlash);
            sb.append(" ".repeat(Math.max(0, spaceInBetween)));
            sb.append(rightSlash);
            sb.append(" ".repeat(Math.max(0, space)));
            sb.append("\n");
        }

        // now get string representations of left and right subtrees
        StringBuilder leftTree = generateTreeVisualization(root.left, currentHeight + 1, totalHeight);
        StringBuilder rightTree = generateTreeVisualization(root.right, currentHeight + 1, totalHeight);

        // now line by line print the trees side by side
        Scanner leftScanner = new Scanner(leftTree.toString());
        Scanner rightScanner = new Scanner(rightTree.toString());
        while (leftScanner.hasNextLine()) {
            if (currentHeight == totalHeight - 1) {
                sb.append(String.format("%-2s %2s", leftScanner.nextLine(), rightScanner.nextLine()));
                sb.append("\n");
                spaceInBetween -= 2;
            } else {
                sb.append(leftScanner.nextLine());
                sb.append(" ");
                sb.append(rightScanner.nextLine()).append("\n");
            }
        }
        leftScanner.close();
        rightScanner.close();

        return sb;

    }

    private int getSlashCount(int height) {
        if (height <= 3)
            return height - 1;
        return (int) (3 * Math.pow(2, (double) height - 3) - 1);
    }

    private int getSpaceCount(int height) {
        return (int) (3 * Math.pow(2, (double) height - 2) - 1);
    }

    public int getHeight() {
        return getHeight(this);
    }

    private int getHeight(TreePrinter root) {
        if (root == null)
            return 0;
        return Math.max(getHeight(root.left), getHeight(root.right)) + 1;
    }

    @Override
    public String toString() {
        return this.data + "";
    }
}

