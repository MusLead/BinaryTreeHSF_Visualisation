package de.hsfd.binarytreevis.services;

import de.hsfd.binarytreevis.services.bst.BSTree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BSTTest {

    @Test
    public void testDeleteRoot() throws TreeException {
        BSTree<Integer> tree = new BSTree<>();
        tree.insert(50);
        tree.delete(50);
        assertNull(tree.getRoot(), "Root should be null after deletion.");
    }

    @Test
    public void testDeleteLeafNode() throws TreeException {
        BSTree<Integer> tree = new BSTree<>();
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);

        tree.delete(30);
        assertNotNull(tree.getRoot(), "Root should not be null after deletion of a leaf node.");
        assertNull(tree.getRoot().getLeft(), "Left child of root should be null after deletion.");
    }

    @Test
    public void testDeleteNodeWithOneChild() throws TreeException {
        BSTree<Integer> tree = new BSTree<>();
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);
        tree.insert(60); // Right child of 50 has one child (60)

        tree.delete(70);

        assertNotNull(tree.getRoot(), "Root should not be null after deletion.");
        assertEquals(60, tree.getRoot().getRight().getData(), "Right child should be updated to the only child (60).");
    }

    @Test
    public void testDeleteNodeWithTwoChildren() throws TreeException {
        BSTree<Integer> tree = new BSTree<>();
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);
        tree.insert(60);
        tree.insert(80);

        tree.delete(70);

        assertNotNull(tree.getRoot(), "Root should not be null after deletion.");
        assertEquals(80, tree.getRoot().getRight().getData(), "Right child should be replaced with the smallest node from the right subtree (80).");
    }

    @Test
    public void testDeleteNonExistentNode() throws TreeException {
        BSTree<Integer> tree = new BSTree<>();
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);

        assertThrows(TreeException.class, () -> tree.delete(100), "Deleting a non-existent node should throw TreeException.");
    }

    @Test
    public void testDeleteFromEmptyTree() {
        BSTree<Integer> tree = new BSTree<>();
        assertThrows(TreeException.class, () -> tree.delete(50), "Deleting from an empty tree should throw TreeException.");
    }

    @Test
    public void testDeleteRootWithOneChildLeft() throws TreeException {
        BSTree<Integer> tree = new BSTree<>();
        tree.insert(50);
        tree.insert(30);
        tree.delete(50);

        assertEquals(30, tree.getRoot().getData(), "Root should be updated to the only child (50).");
    }

    @Test
    public void testDeleteRootWithTwoChild() throws TreeException {
        BSTree<Integer> tree = new BSTree<>();
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);
        tree.insert(60);

        tree.delete(50);

        assertEquals(60, tree.getRoot().getData(), "Root should be updated to the only child (50).");
    }

    @Test
    public void testDeleteRootWithOneChildRight() throws TreeException {
        BSTree<Integer> tree = new BSTree<>();
        tree.insert(50);
        tree.insert(70);

        tree.delete(50);

        assertEquals(70, tree.getRoot().getData(), "Root should be updated to the only child (50).");
    }


    @Test
    public void testLookup() throws TreeException {
        BSTree<Integer> tree = new BSTree<>();
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);

        assertNotNull(tree.lookup(50), "Lookup should find the root node.");
        assertNotNull(tree.lookup(30), "Lookup should find the left child.");
        assertNotNull(tree.lookup(70), "Lookup should find the right child.");
        assertNull(tree.lookup(100), "Lookup should return null for non-existent nodes.");
    }

    BSTree<Integer> bst = new BSTree<>();

    @Test
    public void testInsertion() throws IllegalAccessException {
        assertDoesNotThrow(() -> bst.insert(50));
        assertDoesNotThrow(() -> bst.insert(30));
        assertDoesNotThrow(() -> bst.insert(70));
        assertThrows(TreeException.class, () -> bst.insert(50)); // Trying to insert a duplicate

        // You can implement a method in BST to check structure or properties,
        // For example, inOrderTraversal() to check if elements are in ascending order
        assertTrue(isBinarySearchTree(bst.getRoot()));
    }

    @Test
    public void testDeletion() throws TreeException, IllegalAccessException {
        bst.insert(50);
        bst.insert(30);
        bst.insert(70);

        bst.delete(30); // Delete a leaf/one-child node
        bst.delete(50); // Delete a node with two children
        assertThrowsExactly(TreeException.class, () -> bst.delete(80)); // Try to delete a non-existing node

        assertTrue(isBinarySearchTree(bst.getRoot()));
    }

    public static boolean isBinarySearchTree(TreeNode<Integer> node) throws IllegalAccessException {
        return isBinarySearchTree(node, null, null);
    }

    private static boolean isBinarySearchTree(TreeNode<Integer> node, Integer min, Integer max) throws IllegalAccessException {
        if (node == null) {
            return true;
        }

        if(node.getData() == null)
            throw new IllegalAccessException("The nullNode should not be in the tree!");

        if (min != null && node.getData().compareTo(min) <= 0) {
            return false;
        }

        if (max != null && node.getData().compareTo(max) >= 0) {
            return false;
        }

        return isBinarySearchTree(node.getLeft(), min, node.getData()) &&
                isBinarySearchTree(node.getRight(), node.getData(), max);
    }

}
