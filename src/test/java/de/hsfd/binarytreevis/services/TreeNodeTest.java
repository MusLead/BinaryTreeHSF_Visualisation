package de.hsfd.binarytreevis.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TreeNodeTest {
    @Test
    void setLeft() {
        // Create nodes
        TreeNode<Integer> parent = new TreeNode<>(10);
        TreeNode<Integer> child = new TreeNode<>(5);

        // Set child as the left child of the parent
        parent.setLeft(child);

        // Assert parent-child connection
        assertEquals(child, parent.getLeft(), "Child node should be the left child of the parent.");
        assertEquals(parent, child.getParent(), "Parent node should be correctly set in the child.");

        // Replace left child
        TreeNode<Integer> newChild = new TreeNode<>(3);
        parent.setLeft(newChild);

        // Assert the new child connection
        assertEquals(newChild, parent.getLeft(), "New child should replace the old left child.");
        assertEquals(parent, newChild.getParent(), "Parent node should be correctly set in the new child.");

        // Assert the old child is disconnected
        assertNull(child.getParent(), "Old child should no longer have a parent.");
    }

    @Test
    void setRight() {
        // Create nodes
        TreeNode<Integer> parent = new TreeNode<>(10);
        TreeNode<Integer> child = new TreeNode<>(15);

        // Set child as the right child of the parent
        parent.setRight(child);

        // Assert parent-child connection
        assertEquals(child, parent.getRight(), "Child node should be the right child of the parent.");
        assertEquals(parent, child.getParent(), "Parent node should be correctly set in the child.");

        // Replace right child
        TreeNode<Integer> newChild = new TreeNode<>(20);
        parent.setRight(newChild);

        // Assert the new child connection
        assertEquals(newChild, parent.getRight(), "New child should replace the old right child.");
        assertEquals(parent, newChild.getParent(), "Parent node should be correctly set in the new child.");

        // Assert the old child is disconnected
        assertNull(child.getParent(), "Old child should no longer have a parent.");
    }

    @Test
    void checkParentConnectionAndRemove() {
        // Create nodes
        TreeNode<Integer> parent = new TreeNode<>(10);
        TreeNode<Integer> oldParent = new TreeNode<>(100);
        TreeNode<Integer> leftChild = new TreeNode<>(5);
        TreeNode<Integer> rightChild = new TreeNode<>(15);

        // Set children
        oldParent.setLeft(leftChild);
        oldParent.setRight(rightChild);
    
        // Assert initial connections
        assertEquals(leftChild, oldParent.getLeft(), "Left child should be set correctly.");
        assertEquals(rightChild, oldParent.getRight(), "Right child should be set correctly.");
        assertNull(parent.getRight(), "Parent should have no children");
        assertNull(parent.getLeft(), "Parent should have no children");
    
        // Remove connections
        parent.checkParentConnectionAndRemove(leftChild);
        parent.checkParentConnectionAndRemove(rightChild);
    
        // Assert disconnections
        assertNull(oldParent.getLeft(), "Left child should be removed from the oldParent.");
        assertNull(oldParent.getRight(), "Right child should be removed from the oldParent.");
        assertEquals(leftChild.getParent(), parent, "Parent should have right child.");
        assertEquals(rightChild.getParent(), parent, "Parent should have left child.");
        
        
    }
}
