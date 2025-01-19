/**
 * Author: Murtaza Raja
 * Date: February 27, 2016.
 * Description: This class is used to print the Binary tree as string in a pretty format.
 * GitHub: https://github.com/murtraja/java-binary-tree-printer/tree/master
 */
package de.hsfd.binarytreevis;

import de.hsfd.binarytreevis.services.Author;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.Renderer;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The TreePrinter class provides functionality to visualize and export a binary
 * tree structure.
 * It supports generating a textual representation of the tree, as well as
 * exporting the tree in DOT format
 * for use with graph visualization tools like Graphviz. Additionally, it can
 * generate an SVG image of the tree
 * and provide a downloadable link for the SVG content.
 * 
 * <p>
 * Features include:
 * </p>
 * <ul>
 * <li>Pretty-printing the tree structure in a textual format.</li>
 * <li>Exporting the tree structure in DOT format for graph visualization.</li>
 * <li>Generating an SVG image of the tree.</li>
 * <li>Providing a downloadable link for the SVG content.</li>
 * </ul>
 * 
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>{@code
 * TreePrinter root = new TreePrinter(1, null, null, "RED");
 * root.setLeft(new TreePrinter(2, null, null, "BLACK"));
 * root.setRight(new TreePrinter(3, null, null, "GREEN"));
 * 
 * // Pretty print the tree
 * System.out.println(root.prettyPrint());
 * 
 * // Export the tree as DOT format
 * String dotRepresentation = root.exportDOTAsString();
 * System.out.println(dotRepresentation);
 * 
 * // Generate SVG image of the tree
 * String svgImage = root.getTreeAsImage();
 * System.out.println(svgImage);
 * 
 * // Generate downloadable SVG link
 * String svgLink = TreePrinter.generateDownloadableSVGLink(svgImage, "tree.svg");
 * System.out.println(svgLink);
 * }</pre>
 * 
 * <p>
 * Note: The class uses a Logger to log errors during SVG generation.
 * </p>
 *
 */
@Author(name = "Murtaza Raja", date = "27 Feb 2016")
@Author(name = "Agha Muhammad Aslam", date = "31 Dec 2024")
public class TreePrinter {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

    private final int data;
    private TreePrinter left;
    private TreePrinter right;
    private final String color;

    public TreePrinter(int data, TreePrinter left, TreePrinter right, String color) {
        this.data = data;
        this.left = left;
        this.right = right;
        this.color = color;
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
     * @param root          the root node of the tree or subtree to visualize
     * @param currentHeight the current depth level in the tree being processed
     * @param totalHeight   the total height of the tree to properly calculate
     *                      spacing for nodes
     * @return a StringBuilder object containing the textual visualization of the
     *         tree
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

    /**
     * Generates a base DOT representation of the binary tree starting from the
     * given node.
     * The DOT format is used for visualizing graphs. In this case for visualizing
     * the binary tree.
     * This method will be extended by {@link TreePrinter#exportDOTAsString()} to
     * generate a complete DOT representation.
     *
     * @param node     The current node in the binary tree.
     * @param builder  The StringBuilder used to accumulate the DOT representation.
     * @param depth    The current depth of the node in the tree.
     * @param depthMap A map that groups nodes by their depth for rank=same grouping
     *                 in DOT.
     */
    private void generateDOT(TreePrinter node, StringBuilder builder, int depth, Map<Integer, List<String>> depthMap) {
        if (node != null) {
            // Node color logic
            String fillColor = node.color.equals("RED") ? "red" : (node.color.equals("green") ? "green" : "black");
            String fontColor = node.color.equals("BLACK") ? "white" : "black";

            // Write the node representation
            builder.append(String.format("    \"%s\" [style=filled, fillcolor=%s, fontcolor=%s];\n",
                    node.data, fillColor, fontColor));

            // Add non-NIL node to depthMap for rank=same grouping
            // noinspection unused
            depthMap.computeIfAbsent(depth, k -> new ArrayList<>()).add(String.valueOf(node.data));

            // Left child
            if (node.getLeft() != null) {
                builder.append(String.format("    \"%s\" -> \"%s\";\n",
                        node.data, node.getLeft().data));
                generateDOT(node.getLeft(), builder, depth + 1, depthMap);
            } else if (node.getRight() != null) {
                // Invisible edge to represent missing left child
                String nilId = "NIL_" + System.identityHashCode(node) + "_left";
                builder.append(String.format(
                        "    \"%s\" [shape=circle, style=invis, fillcolor=black, width=0.1, height=0.1, label=\"\"];\n",
                        nilId));
                builder.append(String.format("    \"%s\" -> \"%s\" [style=invis];\n", node.data, nilId));
            }

            // Right child
            if (node.getRight() != null) {
                builder.append(String.format("    \"%s\" -> \"%s\";\n",
                        node.data, node.getRight().data));
                generateDOT(node.getRight(), builder, depth + 1, depthMap);
            } else if (node.getLeft() != null) {
                // Invisible edge to represent missing right child
                String nilId = "NIL_" + System.identityHashCode(node) + "_right";
                builder.append(String.format(
                        "    \"%s\" [shape=circle, style=invis, fillcolor=black, width=0.1, height=0.1, label=\"\"];\n",
                        nilId));
                builder.append(String.format("    \"%s\" -> \"%s\" [style=invis];\n", node.data, nilId));
            }
        }
    }

    /**
     * <p>
     * Exports the binary tree structure in DOT format as a string.
     * The DOT format is used for representing graphs and can be visualized using
     * tools like Graphviz.
     * </p>
     * The method generates DOT representation for the nodes and records their depth
     * levels.
     * It also ensures that nodes at the same depth level are ranked the same in the
     * DOT output.
     * 
     * @return A string containing the DOT representation of the binary tree.
     */
    private String exportDOTAsString() {
        StringBuilder builder = new StringBuilder();
        Map<Integer, List<String>> depthMap = new HashMap<>();

        builder.append("digraph Tree {\n");
        builder.append("    node [shape=circle];\n");

        // Generate DOT for nodes and record depth levels
        generateDOT(this, builder, 0, depthMap);

        // Add rank=same for nodes at the same depth
        for (Map.Entry<Integer, List<String>> entry : depthMap.entrySet()) {
            builder.append("    { rank=same; ");
            for (String nodeId : entry.getValue()) {
                builder.append(String.format("\"%s\" ", nodeId));
            }
            builder.append("}\n");
        }

        builder.append("}\n");
        return builder.toString();
    }

    /**
     * Converts the current tree structure into an SVG image representation.
     *
     * @return A string containing the SVG representation of the tree, or null if an
     *         error occurs.
     */
    public String getTreeAsImage() {
        try {
            MutableGraph graph = new Parser().read(this.exportDOTAsString());
            Renderer result = Graphviz.fromGraph(graph).render(Format.SVG);
            return result.toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred", e);
            return null;
        }
    }

    /**
     * Generates an HTML link for downloading an SVG image.
     * <p>
     * It might not be directly downloadable. But you can at least copy the link and
     * paste it in the browser to download the SVG image.
     * </p>
     * @param svgContent The SVG content to be encoded and included in the link.
     * @param fileName   The name of the file to be downloaded.
     * @return A string containing an HTML anchor tag with a base64-encoded SVG
     *         image for download.
     */
    public static String generateDownloadableSVGLink(String svgContent, String fileName) {
        String base64SVG = Base64.getEncoder().encodeToString(svgContent.getBytes(StandardCharsets.UTF_8));
        return String.format(
                "<a href=\"data:image/svg+xml;base64,%s\" download=\"%s\">SVG Link</a>",
                base64SVG, fileName);
    }

}
