package de.hsfd.binarytreevis.services;

import de.hsfd.binarytreevis.services.avl.AVLTree;
import org.junit.jupiter.api.Test;

import static de.hsfd.binarytreevis.services.TreeNode.DEFAULT_HEIGHT;
import static de.hsfd.binarytreevis.services.BSTTest.isBinarySearchTree;
import static org.junit.jupiter.api.Assertions.*;

public class AVLTest {

    @Test
    void insert_singleRotationLeft() {
        AVLTree<Integer> avlTree = new AVLTree<>();
        try {
            avlTree.insert(10);
            avlTree.insert(20);
            avlTree.insert(30); // Should trigger left rotation

            assertEquals(20, avlTree.getRoot().getData());
            assertEquals(10, avlTree.getRoot().getLeft().getData());
            assertEquals(30, avlTree.getRoot().getRight().getData());
        } catch (TreeException e) {
            fail("Exception occurred during insertion: " + e.getMessage());
        }
    }

    @Test
    void insert_singleRotationRight() {
        AVLTree<Integer> avlTree = new AVLTree<>();
        try {
            avlTree.insert(30);
            avlTree.insert(20);
            avlTree.insert(10); // Should trigger right rotation

            assertEquals(20, avlTree.getRoot().getData());
            assertEquals(10, avlTree.getRoot().getLeft().getData());
            assertEquals(30, avlTree.getRoot().getRight().getData());
        } catch (TreeException e) {
            fail("Exception occurred during insertion: " + e.getMessage());
        }
    }

    @Test
    void insert_doubleRotationLeftRight() {
        AVLTree<Integer> avlTree = new AVLTree<>();
        try {
            avlTree.insert(30);
            avlTree.insert(10);
            avlTree.insert(20); // Should trigger left-right rotation

            assertEquals(20, avlTree.getRoot().getData());
            assertEquals(10, avlTree.getRoot().getLeft().getData());
            assertEquals(30, avlTree.getRoot().getRight().getData());
        } catch (TreeException e) {
            fail("Exception occurred during insertion: " + e.getMessage());
        }
    }

    @Test
    void insert_doubleRotationRightLeft() {
        AVLTree<Integer> avlTree = new AVLTree<>();
        try {
            avlTree.insert(10);
            avlTree.insert(30);
            avlTree.insert(20); // Should trigger right-left rotation

            assertEquals(20, avlTree.getRoot().getData());
            assertEquals(10, avlTree.getRoot().getLeft().getData());
            assertEquals(30, avlTree.getRoot().getRight().getData());
        } catch (TreeException e) {
            fail("Exception occurred during insertion: " + e.getMessage());
        }
    }

    @Test
    void leftRotate() {
        AVLTree<Integer> avlTree = new AVLTree<>();
        try {
            avlTree.insert(10);
            avlTree.insert(20);
            avlTree.insert(30); // Trigger left rotation

            assertEquals(20, avlTree.getRoot().getData());
        } catch (TreeException e) {
            fail("Exception occurred during left rotation: " + e.getMessage());
        }
    }

    @Test
    void rightRotate() {
        AVLTree<Integer> avlTree = new AVLTree<>();
        try {
            avlTree.insert(30);
            avlTree.insert(20);
            avlTree.insert(10); // Trigger right rotation

            assertEquals(20, avlTree.getRoot().getData());
        } catch (TreeException e) {
            fail("Exception occurred during right rotation: " + e.getMessage());
        }
    }

    @Test
    void delete_cases() {
        AVLTree<Integer> avlTree = new AVLTree<>();
        try {
            // Case 1: Delete a leaf node
            avlTree.insert(20);
            avlTree.insert(10);
            avlTree.insert(30);
            System.out.println("The tree:\n"+avlTree.getTreePrinter().prettyPrint());
            avlTree.delete(10); // Deleting leaf node
            System.out.println("After delete 10:\n"+avlTree.getTreePrinter().prettyPrint());
            assertNull(avlTree.getRoot().getLeft());

            // Case 2: Delete a node with one child
            avlTree.insert(25);
            System.out.println("After insert 25:\n"+avlTree.getTreePrinter().prettyPrint());
            avlTree.delete(30); // Deleting node with one child
            System.out.println("After delete 30:\n"+avlTree.getTreePrinter().prettyPrint());
            assertEquals(25, avlTree.getRoot().getData());

            // Case 3: Delete a node with two children
            avlTree.insert(5);
            System.out.println("After insert 5:\n"+avlTree.getTreePrinter().prettyPrint());
            avlTree.insert(15);
            System.out.println("After insert 15:\n"+avlTree.getTreePrinter().prettyPrint());
            avlTree.delete(20); // Deleting root node with two children
            System.out.println("After delete 20:\n"+avlTree.getTreePrinter().prettyPrint());
            assertEquals(15, avlTree.getRoot().getData());

            // Case 4: Delete root node
            avlTree.delete(15);
            System.out.println("After delete 15:\n"+avlTree.getTreePrinter().prettyPrint());
            assertEquals(25, avlTree.getRoot().getData());

            // Case 5: Delete causing balancing
            avlTree.insert(40);
            avlTree.insert(50);
            System.out.println("After insert 40 and 50:\n"+avlTree.getTreePrinter().prettyPrint());
            avlTree.delete(5); // Should trigger balancing
            System.out.println("After delete 5:\n"+avlTree.getTreePrinter().prettyPrint());
            assertEquals(50, avlTree.getRoot().getRight().getData());

            //case 6:
            avlTree.insert(42);
            System.out.println("After insert 42:\n"+avlTree.getTreePrinter().prettyPrint());
            assertEquals(50, avlTree.getRoot().getRight().getData());
            avlTree.delete(40);
            System.out.println("After delete 40:\n"+avlTree.getTreePrinter().prettyPrint());
            assertEquals(42, avlTree.getRoot().getData());

        } catch (TreeException e) {
            fail("Exception occurred during deletion: " + e.getMessage());
        }
    }

    private final AVLTree<Integer> avl = new AVLTree<>();

    @Test
    public void testInsertion() throws TreeException, IllegalAccessException {
        avl.insert(50);
        avl.insert(30);
        avl.insert(70);
        avl.insert(20);
        avl.insert(40);
        avl.insert(60);
        avl.insert(80);

        // Test AVL tree properties
        assertTrue(isBinarySearchTree(avl.getRoot()));
        assertTrue(isBalanced(avl.getRoot()));
    }

    @Test
    public void testUnbalancedInsertion() throws TreeException, IllegalAccessException {
        avl.insert(50);
        avl.insert(30);
        avl.insert(20);

        // Test AVL tree properties
        assertTrue(isBinarySearchTree(avl.getRoot()));
        assertEquals(30, (int) avl.getRoot().getData());
        assertTrue(isBalanced(avl.getRoot())); // This should be false if the tree is unbalanced.
    }


    @Test
    public void testDeletion() throws TreeException, IllegalAccessException {
        avl.insert(50);
        avl.insert(30);
        avl.insert(70);
        avl.insert(20);
        avl.insert(40);
        avl.insert(60);
        avl.insert(80);

        avl.delete(50);
        avl.delete(30);

        // Test AVL tree properties after deletion
        assertTrue(isBinarySearchTree(avl.getRoot()));
        assertTrue(isBalanced(avl.getRoot()));
    }


    private boolean isBalanced(TreeNode<Integer> node) {
        if (node == null) {
            return true;
        }

        int leftHeight = heightOf(node.getLeft());
        int rightHeight = heightOf(node.getRight());

        return Math.abs(leftHeight - rightHeight) <= 1 &&
                isBalanced(node.getLeft()) &&
                isBalanced(node.getRight());
    }

    private int heightOf(TreeNode<Integer> node) {
        if (node == null) {
            return DEFAULT_HEIGHT - 1; // -1 or 0,  depending on your height definition
        }
        return node.getHeight();
    }
}
