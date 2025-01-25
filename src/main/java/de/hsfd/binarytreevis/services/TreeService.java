package de.hsfd.binarytreevis.services;

import de.hsfd.binarytreevis.TreePrinter;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.function.Consumer;


/**
 * Service class providing a structured representation of a generic Tree and various operations.
 * This class allows the creation, modification, and management of tree structures and their nodes.
 * Specific tree algorithms such as insertion, deletion, and traversal are included.
 *
 * @param <E> the type of elements maintained by this tree
 */
@Author(name = "Ankit Sharma", date = "12 Oct 2018")
@Author(name = "Agha Muhammad Aslam", date = "12 Dec 2023")
public abstract class TreeService<E extends Comparable<E>> {

    public TreeService( ) {}

    public TreeService( TreeService<E> tree) {
        this.root = tree.root == null ? null : tree.root.copyWithSubtreeOf(tree.root);
        this.size = tree.size;
        this.record = tree.record;
        this.nodes.addAll(tree.nodes);
        this.recordList.addAll(tree.recordList);
        this.status = tree.status;
        this.historyService = tree.historyService;
    }

    protected int size = 0;

    private TreeNode<E> root;

    private String record = "";

    private final ArrayList<E> nodes = new ArrayList<>();

    private final ArrayList<String> recordList = new ArrayList<>();

    private Consumer<String> status = _ -> {};

    private Consumer<String> historyService = _ -> {};

    public void setRoot( TreeNode<E> root ) {
        this.root = root;
    }

    public TreeNode<E> getRoot() {
        return root;
    }

    public ArrayList<E> getNodes( ) {
        return nodes;
    }

    public ArrayList<String> getRecordList( ) {
        return recordList;
    }

    public E lastInserted() {
        if (nodes.isEmpty()) throw new NoSuchElementException("There is no element in the nodes list.\n please check again the implementation in the TreeService class.\n");
        return nodes.getLast();
    }

    public boolean search( E e) {
        TreeNode<E> current = root;
        while (current != null) {
            if (e.compareTo(current.getData()) < 0)
                current = current.getLeft();
            else if (e.compareTo(current.getData()) > 0)
                current = current.getRight();
            else
                return true;
        }
        return false;
    }

    /**
     * Sets the status using a provided Consumer function that accepts a String.
     * This method updates the status field and applies the Consumer to the
     * string representation of the nodes.
     *  <p>This function should be called in the controller classes only to update the status of the elements in the Tree.</p>
     * @param init a Consumer function that processes the string representation
     *             of the nodes
     */
    public void setStatus(Consumer<String> init ) {
        this.status = init;
        this.status.accept(nodes.toString()); // show the empty tree (no inserted nodes)
    }

    /**
     * Sets the history service to the provided Consumer.
     *  <p>This function should only be called in the controller classes only to update the logs in UI</p>
     * @param historyService a Consumer that processes history related data represented as a String
     */
    public void setHistoryService(Consumer<String> historyService) {
        this.historyService = historyService;
    }

    /**
     * A Special method to record the modification of the tree
     * Updates the current status of the tree.
     * Showing the inserted nodes int the tree from left to right.
     */
    public void updateCurrentStatus( ) {
        status.accept(nodes.toString());
    }

    /**
     * A Special method to record the modification of the tree
     * Add some strings to the rec indicates the process of modification within the tree.
     * <p>
     * Remember to add a new line character at the end of the string! </p>
     * <p>
     * Remember to call acceptRecordInHistory() method after finishing the modification! </p>
     * @param rec the string to be added to the record
     */
    public void addRecord( String rec) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = stackTrace[2].getClassName().substring(stackTrace[2].getClassName().lastIndexOf('.') + 1);
        String methodName = stackTrace[2].getMethodName();
        record += (className + "." + methodName + ":\n"+ rec + "\n");
    }

    /**
     * A Special method to record the modification of the tree. <br/>
     * Accepts the record in the history.
     * After calling this method, the record will be added to the recordList.
     * It will be later used in the history panel (TreeController.java)
     * as a history's record of each modification's state.
     */
    public void acceptRecordInHistory() {
        historyService.accept(record);
        recordList.add(record);
    }

    @Override
    public String toString() {
        return "TreeService{" +
                "root=" + root +
                ", size=" + size +
                '}';
    }

    /**
     * Compares all elements of two trees recursively.
     * Checks if the root, left and right nodes of both trees are equal.
     *
     * @param obj the TreeService object to be compared.
     * @return true if trees are identical, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        // If the object is compared with itself, then return true
        if (this == obj) {
            return true;
        }

        // Check if obj is an instance of TreeService or not
        if (!(obj instanceof TreeService<?> other)) {
            return false;
        }

        return equals(this.getRoot(), other.getRoot());
    }

    /**
     * Recursive helper function to check if two tree nodes and its children are equal.
     *
     * @param node1 the first node.
     * @param node2 the second node.
     * @return true if nodes are identical, false otherwise.
     */
    private boolean equals(TreeNode<?> node1, TreeNode<?> node2) {
        if(node1 == null && node2 == null) return true;
        if(node1 == null || node2 == null) return false;

        // Return false if the node's values are not equal
        // Else continue to check the left and right nodes
        return node1.getData().equals(node2.getData())
                && node1.getColor() == node2.getColor() // check if both nodes are red or black
                && equals(node1.getLeft(), node2.getLeft())
                && equals(node1.getRight(), node2.getRight());
    }
    
    
    // Agha implementation

    /**
     * Inserts a new element into the binary tree while maintaining its structure.
     * This is an abstract method, and its implementation should define the specific
     * logic for insertion based on the type of binary tree (e.g., Binary Search Tree, AVL Tree, etc.).
     * <p>Please implement the {@link TreeService#insertNode(TreeNode)} function to this method,
     * so that it will be inserted directly into the respected position.</p>
     * <p> Idea Inspired from AlgoDS Lecture HS Fulda 24/25 </p>
     * @param data the value to be inserted into the binary tree. Must implement the Comparable interface
     * to allow comparisons with other elements in the tree.
     */
    protected abstract void insert(E data) throws TreeException;

    public void main_insert(E data) throws TreeException {
        insert(data);
        this.acceptRecordInHistory();
    }

    /**
     * Deletes a specified target node from the binary tree.
     * This method will only be executed, when the node is found in the tree by the delete method.
     * The method handles the appropriate removal of the target based on its position and updates the parent node's reference.
     * This is an abstract method, and its implementation should define the specific logic for deletion.
     * <p>Please implement these functions when there are further children from the target: {@link TreeService#replaceWithTheSmallestOfRightChildren(TreeNode)} and {@link TreeService#deleteTargetWithOneChildOrNone(TreeNode, TreeNode, CHILD)}</p>
     * <p> Idea Inspired from AlgoDS Lecture HS Fulda 24/25 </p>
     * @param parentTarget The parent node of the target node that needs to be deleted.
     * @param target The node to be deleted from the binary tree.
     * @param positionOfTarget Specifies whether the target node is the left or right child of the parent node.
     * @throws TreeException If a problem occurs during node deletion.
     * @return a nullNode that has been used for the operation. Otherwise, it should return null
     */
    protected abstract TreeNode<E> deleteTarget(TreeNode<E> parentTarget, TreeNode<E> target, CHILD positionOfTarget) throws TreeException;

    protected enum CHILD {rightChildOfParent, leftChildOfParent}

    /**
     * A search function to look if the value x exist in the tree. <p>
     * inspired from: AlgoDS Lecture HS Fulda 24/25
     * @param x the target value
     * @return the same value if found, otherwise null
     */
    @SuppressWarnings( "unused")
    public E lookup(E x) {
        TreeNode<E> k = root;
        while(k != null) {
            if(x.compareTo(k.getData()) < 0) { // x is smaller than k
                k = k.getLeft();
            } else if(x.compareTo(k.getData()) > 0){ // x is bigger than k
                k = k.getRight();
            } else if(x.compareTo(k.getData()) == 0) {
                return k.getData();
            }
        }
        return null;
    }

    /**
     * Insert the node into the tree without any self-balancing methods.
     * After the execution, the newNode will have a parent if root != null. <p>
     * inspired from: AlgoDS Lecture HS Fulda 24/25
     * @param newNode will be added to the tree
     */
    protected void insertNode(TreeNode<E> newNode) throws TreeException {
        addRecord("\n- Insert " + newNode.getData() + " to the tree\n");
        if(root == null)
            root = newNode;
        else {
            TreeNode<E> parent = getParentOf(newNode.getData());

            if(newNode.getData().compareTo(parent.getData()) < 0) {
                parent.setLeft(newNode);
            } else if(newNode.getData().compareTo(parent.getData()) > 0) {
                parent.setRight(newNode);
            } else throw new TreeException("The value is already in the tree. No Parent will be returned");
        }

        nodes.add(newNode.getData());
        updateCurrentStatus();
        size++;
    }

    /**
     * This function is intended for insert method. (top-down approach)
     * Iterate through the nodes of the tree to find a parent node where a new node with the value x can be attached.
     * If the value of a node is the same as x, the function should return an error message, as duplicate values are not accepted. <p>
     *     inspired from: AlgoDS Lecture HS Fulda 24/25
     * </p>
     * @param x The value that needs to be attached to the parent
     * @return the available parent
     */
    protected TreeNode<E> getParentOf(E x) throws TreeException {
        TreeNode<E> parent = null;
        TreeNode<E> n = root;
        while(n != null) {
            parent = n;
            if (x.compareTo(n.getData()) < 0) { // x is smaller than n
                n = n.getLeft();
            } else if (x.compareTo(n.getData()) > 0) { // x is bigger than n
                n = n.getRight();
            } else // the value is the same, do not add the value
                throw new TreeException("The value is already in the tree. No Parent will be returned");
        }
        return parent;
    }

    /**
     * Deletes a specified element from the binary tree, if it exists.
     * The method searches for the node containing the specified value,
     * removes it, and adjusts the tree to maintain its properties.
     * <p>Idea inspired from ALgoDS Lecture HS Fulda 24/25</p>
     *
     * @param x the value to be deleted from the binary tree.
     * @throws TreeException if the tree is empty or the value to delete is not found.
     */
    public void delete(E x) throws TreeException {
        TreeNode<E> target = root;
        TreeNode<E> parentTarget = null;
        if(target == null)
            throw new TreeException("The tree is empty");

        CHILD positionOfTarget = null;
        while(target != null) {
            if(x.compareTo(target.getData()) < 0) {
                parentTarget = target;
                target = parentTarget.getLeft();
                positionOfTarget = CHILD.leftChildOfParent;
            } else if(x.compareTo(target.getData()) > 0) {
                parentTarget = target;
                target = parentTarget.getRight();
                positionOfTarget = CHILD.rightChildOfParent;
            } else if(x.compareTo(target.getData()) == 0) {
                addRecord("- Delete " + x + " from the tree\n");

                removeNullNode(deleteTarget(parentTarget, target, positionOfTarget));

                nodes.remove(x);
                updateCurrentStatus();
                this.acceptRecordInHistory();

                size--;
                return;
            }
        }
        throw new TreeException("The value " + x + " could not be found in the tree.");
    }

    /**
     * Delete a node with one Child or none
     * <p>
     * Case 0 if the target is leaf, then it will take the children of the target for the parent.
     * since it child of the target is null, the children of the parent will be null. </p>
     * <p>
     * Case 1 if the target has only one child, the parentTarget will
     * take either the target's children (subtree) left or right</p>
     * Idea Inspired from AlgoDS Lecture HS Fulda 24/25
     *
     * @param parentTarget The parent node of the target node to be deleted.
     *                     If the target node is the root, this parameter is null.
     * @param target       The node to be deleted from the binary tree.
     * @param positionOfTarget Indicates whether the target node is the left or right
     *                         child of the parent node.
     * @return The replacement node that takes the place of the deleted node, or null
     *         if the target node had no children.
     */
    protected TreeNode<E> deleteTargetWithOneChildOrNone(TreeNode<E> parentTarget, TreeNode<E> target, CHILD positionOfTarget) {
        TreeNode<E> replacement = target.getRight() == null ? target.getLeft() : target.getRight();
        TreeNode<E> nullNode = new TreeNode<>(null, TreeNode.COLOR.BLACK);
        replacement = replacement == null ? nullNode : replacement;
        if(parentTarget != null) {
            switch (positionOfTarget) {
                case leftChildOfParent -> parentTarget.setLeft(replacement);
                case rightChildOfParent -> parentTarget.setRight(replacement);
            }
        } else {
            // If the target is root, then the replacement becomes the root
            root = replacement;
        }
        addRecord("> Case 1 one child: replace target " + target.getData() + " with the children " + replacement.getData() +"\n");
        return replacement;
    }

    /**
     * Finds and removes the smallest node from the left subtree of the right child of the current node.
     * <p>
     * This specialized utility is primarily used in tree deletion operations, particularly
     * when replacing a node being removed (e.g., during the deletion of a node in a binary search tree).
     * The function identifies the smallest node in the left subtree of the right child,
     * removes it from the tree, and returns it.
     * </p>
     * <p>
     * The function assumes that the current node has a right child, and the right child has a left subtree
     * (or is a leaf node). If the right child does not have a left subtree, the right child itself
     * will replace the current node. If the right child is empty, the function will throw an exception, ensuring
     * that it is only called under appropriate conditions.
     * </p>
     * <p>Idea inspired from AlgoDS lecture HS Fulda 24/25</p>
     * @throws TreeException If the right child does not exist, an exception is thrown,
     *         as the function requires a valid right child to operate.
     */
    public TreeNode<E> replaceWithTheSmallestOfRightChildren(TreeNode<E> target) throws TreeException {
        if (target.getRight() == null)
            throw new TreeException("This method should not be called because there are no right child nodes.");

        TreeNode<E> parent = target;
        TreeNode<E> result = target.getRight();

        // Traverse to the leftmost node in the right subtree
        while (result.getLeft() != null) {
            // if this lines being executed, that means, there are children on the left side.
            // result will take the left child,
            // therefore parent.getRight != result after the iteration
            parent = result;
            result = parent.getLeft();
        }

        // Detach the smallest node from its parent
        if (parent.getRight() == result) {
            // If the smallest node is directly the right child
            // Because there is no other child on the left side.
            parent.setRight(result.getRight());
            addRecord("> Case 2 two children: Replace with the right target's children "+ result.getData() +". Because it does not have other children with smaller number anymore\n");
        } else {
            // If the smallest node is further down the left subtree
            // then take the right subtree of the result. if it does not exist then
            // the parent.left should be null
            parent.setLeft(result.getRight());
            addRecord("> Case 2 two children: Replace the target " + target.getData() + " with the smallest value on " +
                    "the left of the right target's children "+ result.getData() +"\n");
        }

        target.setData(result.getData());
        return parent;
    }

    /**
     * Constructs a tree structure that mirrors the current binary tree
     * and returns the root of the corresponding TreePrinter object.
     * The TreePrinter object is useful for visually representing the
     * structure of the binary tree with the terminal as String format.
     *
     * @return the root of the TreePrinter representation of the binary tree
     * @throws NullPointerException if the tree is empty (root is null)
     */
    public TreePrinter getTreePrinter() {
        if (root == null) throw new NullPointerException("The tree is empty");
        ArrayList<TreeNode<E>> queueNodes = new ArrayList<>();
        ArrayList<TreePrinter> queueNode = new ArrayList<>();
        String color = root.getColor() == null ? "green" : root.getColor().toString();
        TreePrinter treePrinterRoot = new TreePrinter(Integer.parseInt(root.getData().toString()), null, null, color);
        TreePrinter iterNode = treePrinterRoot;

        queueNodes.add(root);
        while (!queueNodes.isEmpty()) {
            TreeNode<E> current = queueNodes.removeFirst();
            if (current.getLeft() != null && current.getLeft().getData() != null) {
                int leftValue = Integer.parseInt(current.getLeft().getData().toString());
                String colorLeft = current.getLeft().getColor() == null ? "green" : current.getLeft().getColor().toString();
                iterNode.setLeft(new TreePrinter(leftValue, null, null, colorLeft));
                queueNode.add(iterNode.getLeft());
                queueNodes.add(current.getLeft());
            }
            if (current.getRight() != null && current.getRight().getData() != null) {
                int rightValue = Integer.parseInt(current.getRight().getData().toString());
                String colorRight = current.getRight().getColor() == null ? "green" : current.getRight().getColor().toString();
                iterNode.setRight(new TreePrinter(rightValue, null, null, colorRight));
                queueNode.add(iterNode.getRight());
                queueNodes.add(current.getRight());
            }
            if (!queueNode.isEmpty()) iterNode = queueNode.removeFirst(); // remove the first element, so that the next element is the next node
        }
        return treePrinterRoot;
    }

    /**
     * Based on right-right case.
     * inspired from: <a href="https://www.geeksforgeeks.org/insertion-in-an-avl-tree/">Geek For Geeks</a>
     *
     * @param z the root of rotation
     */
    protected void leftRotate(TreeNode<E> z) {
        TreeNode<E> y = z.getRight();
        TreeNode<E> T2 = y.getLeft();

        updateParent(z, y);

        z.setRight(T2);
        y.setLeft(z);
        if (root == z) root = y;
        updateHeightAfterRotation(z,y);
    }

    /**
     * Based on the lef-left case.
     * inspired from: <a href="https://www.geeksforgeeks.org/insertion-in-an-avl-tree/">Geek For Geeks</a>
     *
     * @param z the root of rotation
     */
    protected void rightRotate(TreeNode<E> z) {
        TreeNode<E> y = z.getLeft();
        TreeNode<E> T3 = y.getRight();

        updateParent(z, y);

        z.setLeft(T3);
        y.setRight(z);
        if (root == z) root = y;
        updateHeightAfterRotation(z,y);
    }

    /**
     * Updates the parent of the given node `z` to point to node `y` instead, if a parent exists.
     * If `z` is the left child of its parent, the parent's left child is updated to `y`.
     * If `z` is the right child of its parent, the parent's right child is updated to `y`.
     *
     * @param <E> the type parameter that extends Comparable
     * @param z the node whose parent relationship is being updated
     * @param y the node that will replace `z` as the child of `z`'s parent
     */
    private static <E extends Comparable<E>> void updateParent(TreeNode<E> z, TreeNode<E> y) {
        TreeNode<E> parentZ = z.getParent();
        if(parentZ != null) {
            if(parentZ.getLeft() == z) parentZ.setLeft(y);
            else parentZ.setRight(y);
        }
    }

    /**
     * Special function only for the rightRotate and leftRotate
     * function only!
     * @param z node
     * @param y node
     */
    private void updateHeightAfterRotation(TreeNode<E> z, TreeNode<E> y) {
        z.setHeight(Math.max(height(z.getLeft()),
                height(z.getRight()) + 1));
        y.setHeight(Math.max(height(y.getLeft()),
                height(y.getRight())) + 1);
    }

    /**
     *  This function should only be used to calculate balance factor or updating height after insertion or deletion
     *  to reduce redundancy.
     *  </p>
     *  If return of the height is 0, it could mean two things. Either the node is the leaf or n is unavailable
     * @param n the target node
     * @return the height of n
     */
    protected int height(TreeNode<E> n) {
        return n == null ? 0 : n.getHeight() ;
    }

    /**
     * Removes a null node from its parent node in a binary tree.
     *
     * @param nullNode the node to be removed, which must have a non-null parent in order
     *                 to perform the operation. If the specified node is null or its
     *                 parent is null, the method does nothing.
     */
    private void removeNullNode(TreeNode<E> nullNode){
        if(nullNode != null && nullNode.getParent() != null && nullNode.getData() == null){
            TreeNode<E> nullNodeParent = nullNode.getParent();
            if(nullNodeParent.getLeft() == nullNode) nullNodeParent.setLeft(null);
            else nullNodeParent.setRight(null);
        }
    }

    /**
     * Records the tree structure as an SVG image and appends it to the provided StringBuilder.
     * The SVG content is wrapped in a <div> element. If the SVG content is successfully generated,
     * a downloadable link for the SVG file is also appended to the StringBuilder.
     * 
     * @param <T> The type of the tree node data, which must be comparable.
     * @param parent The root node of the tree to be recorded.
     * @param record The StringBuilder to which the SVG content and download link will be appended.
     */
    protected <T extends Comparable<T>> void recordTreeAsImage(TreeNode<T> parent, StringBuilder record) {
        String svgContent = this.getTreePrinter().getTreeAsImage();
        record.append("<div>").append(svgContent).append("</div>\n");
        if (svgContent != null) {
            String downloadLink = TreePrinter.generateDownloadableSVGLink(svgContent, "tree_" +
                    (parent != null ? parent.getData() : "null") + ".svg");
            record.append(downloadLink).append("\n\n");
        } else {
            record.append("Failed to generate tree as SVG.\n\n");
        }
    }

}
