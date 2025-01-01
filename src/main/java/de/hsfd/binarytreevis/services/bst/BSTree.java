package de.hsfd.binarytreevis.services.bst;

import de.hsfd.binarytreevis.services.Author;
import de.hsfd.binarytreevis.services.TreeException;
import de.hsfd.binarytreevis.services.TreeNode;
import de.hsfd.binarytreevis.services.TreeService;

@Author(name = "Agha Muhammad Aslam", date = "31 Dec 2024")
public class BSTree<E extends Comparable<E>> extends TreeService<E> {

    public BSTree() {
        super();
    }

    public BSTree(TreeService<E> treeService) {
        super(treeService);
    }

    @Override
    public void insert(E x) throws TreeException {
        TreeNode<E> newNode = new TreeNode<>(x);
        insertNode(newNode);
    }

    @Override
    protected TreeNode<E> deleteTarget(TreeNode<E> parentTarget, TreeNode<E> target, CHILD positionOfTarget) throws TreeException {
        // target found
        if(parentTarget == null && target.isLeaf()) { // target is the root and only one
            addRecord("> Case 0 the element is the root and the only one. Set the root into null\n");
            this.setRoot(null);
        } else {
            // Case 2 if the target has two children. This case is from the Lecture AlgoDS 24/25 HS Fulda
            // Then take the inorder approach to find the smallest children of the right target's children
            if(target.getRight() != null && target.getLeft() != null) {
                replaceWithTheSmallestOfRightChildren(target);
            } else {
                return deleteTargetWithOneChildOrNone(parentTarget, target, positionOfTarget);
            }
        }
        return null;
    }
}
