package de.hsfd.binarytreevis.services.avl;

import de.hsfd.binarytreevis.services.Author;
import de.hsfd.binarytreevis.services.TreeException;
import de.hsfd.binarytreevis.services.TreeNode;
import de.hsfd.binarytreevis.services.TreeService;

@Author(name = "Agha Muhammad Aslam", date = "31 Dec 2024")
public class AVLTree<E extends Comparable<E>> extends TreeService<E> {
    public AVLTree() {
        super();
    }

    public AVLTree(TreeService<E> treeService) {
        super(treeService);
    }

    @Override
    protected TreeNode<E> deleteTarget(TreeNode<E> parentTarget, TreeNode<E> target, CHILD positionOfTarget) throws TreeException {
        // target found
        if(parentTarget == null && target.isLeaf()) {
            // target is the root and only one
            addRecord("> Case 0 the element is the root and the only one. Set the root into null\n");
            this.setRoot(null);
        } else {
            TreeNode<E> possibleNullNode = null;
            if(target.getRight() != null && target.getLeft() != null) {
                // Case 2 if the target has two children. This case is from the Lecture AlgoDS 24/25 HS Fulda
                // Then take the inorder approach to find the smallest children of the right target's children
                parentTarget = replaceWithTheSmallestOfRightChildren(target);
            } else {
                 possibleNullNode = deleteTargetWithOneChildOrNone(parentTarget, target, positionOfTarget);
            }

            if(target.getParent() == null && target != getRoot()) target = parentTarget;
            balanceTheTree(target);
            return possibleNullNode;
        }
        return null;
    }


    @Override
    public void insert(E x) throws TreeException {
        TreeNode<E> newNode = new TreeNode<>(x);
        insertNode(newNode);
        balanceTheTree(newNode.getParent());
    }

    /**
     * Update the height of the node. This should be executed before calculating the balance factor
     * so that the height is actual.
     *
     * @param parent the node
     */
    private void updateHeight(TreeNode<E> parent) {
        int leftHeight = height(parent.getLeft());
        int rightHeight = height(parent.getRight());
        parent.setHeight(Math.max(leftHeight,rightHeight) + 1);
    }

    /**
     * Balances the AVL tree after an insertion or deletion to ensure the tree maintains
     * its AVL property where the height difference between left and right subtrees
     * of any node is at most 1.
     *
     * @param parent the parent node from which balancing starts
     * @throws TreeException if a violation of AVL tree properties persists after balancing
     */
    private void balanceTheTree(TreeNode<E> parent) throws TreeException {
        StringBuilder record = new StringBuilder();
        while(parent != null) {
            updateHeight(parent);
            int balance = getBalanceFactor(parent);

            if(balance > 1 || balance < -1) {
                if (balance > 0) {
                    record.append("> Left heavy from the parent ").append(parent.getData()).append(", before rotation:\n");
//                    record.append(getTreePrinter().prettyPrint());
                    if(getBalanceFactor(parent.getLeft()) < 0 ){
                        record.append("-> Left Rotation, after rotation:\n");
                        leftRotate(parent.getLeft());// Left Right Case
//                        record.append(getTreePrinter().prettyPrint());
                    }
                    record.append("-> Right Rotation, after rotation:\n");
                    rightRotate(parent);
//                    record.append(getTreePrinter().prettyPrint());
                } else { // (balance < 0) right heavy from the parent
                    record.append("> Right heavy from the parent ").append(parent.getData()).append(", before rotation:\n");
//                    record.append(getTreePrinter().prettyPrint());
                    if ( getBalanceFactor(parent.getRight()) > 0 ){
                        record.append("-> Right Rotation, after rotation:\n");
                        rightRotate(parent.getRight());// Right Left Case
//                        record.append(getTreePrinter().prettyPrint());
                    }
                    record.append("-> Left Rotation, after rotation:\n");
                    leftRotate(parent);
//                    record.append(getTreePrinter().prettyPrint());
                }
                if(parent.getParent() == null) this.setRoot(parent);
                if (!(getBalanceFactor(parent) <= 1 && getBalanceFactor(parent) >= -1)) // this node to the leaf should now be balanced
                    throw new TreeException("Violates the AVL tree rule\nNode: " +
                            parent.getData() +"\nHeight: " + parent.getHeight() + "\nTree:\n"
                            + this.getTreePrinter().prettyPrint());
            }
            parent = parent.getParent();
        }
        if(record.isEmpty()) record.append("> Nothing to rotate here\n");
        addRecord(record.toString());
    }

    /**
     * Get Balance factor of node N: left - right
     * <p>
     * <p>> if left is bigger than 1, three is an imbalance on the left side </p>
     * <p>> if right is smaller than -1, there is an imbalance on the right side </p>
     * @param n the root to be checked whether imbalance exists
     * @return the integer result of the balance factor
     */
    private int getBalanceFactor(TreeNode<E> n) {
        if (n == null) // it means the node has not been initialised
            return 0;
        return height(n.getLeft()) - height(n.getRight());
    }

    /**
     * A function to balancing the tree of the newNode with bottom up approach <p>
     * inspired from: <a href="https://www.geeksforgeeks.org/insertion-in-an-avl-tree/">Geek For Geeks</a>
     * @deprecated
     *      <p>This method is no longer acceptable because it is not flexible with the implementation.
     *       We want a function that could be also used for the other function like delete, not only for insert</p>
     *      Please refer to {@link #balanceTheTree(TreeNode)}
     * @param parent the parent of the newNode
     * @param newNode will be used for the comparison checking.
     */
    @Deprecated
    @SuppressWarnings("unused")
    private void balanceTheTreeOld(TreeNode<E> parent, TreeNode<E> newNode) throws TreeException {
        while(parent != null) {
            int leftHeight = height(parent.getLeft());
            int rightHeight = height(parent.getRight());
            parent.setHeight(Math.max(leftHeight,rightHeight) + 1);

            // this code can only be executed if initialised height for a node is 0!
            if(parent.getHeight() > 1) {
                int balance = getBalanceFactor(parent);
                if (balance > 0) { //left heavy from the parent
                    if (newNode.getData().compareTo(parent.getLeft().getData()) > 0) {
                        leftRotate(parent.getLeft());
                    }
                    rightRotate(parent);
                } else if (balance < 0) { // right heavy from the parent
                    if (newNode.getData().compareTo(parent.getRight().getData()) < 0) {
                        rightRotate(parent.getRight());
                        
                    }
                    leftRotate(parent);
                }
                if(parent.getParent() == null) this.setRoot(parent);
                if(parent.getHeight() > 1)
                    throw new TreeException("Violates the AVL tree rule\nTreeNode<E>: " +
                            parent.getData() +"\nHeight: " + parent.getHeight() + "\nTree:\n"
                            + this.getTreePrinter().prettyPrint());
            }

            parent = parent.getParent();
        }
    }

}
