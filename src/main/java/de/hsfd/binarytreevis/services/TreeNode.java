package de.hsfd.binarytreevis.services;

@Author(name = "Ankit Sharma", date = "12 Oct 2018")
@Author(name = "Agha Muhammad Aslam", date = "12 Dec 2023")
public class TreeNode<E extends Comparable<E>> {
    public TreeNode(E e){
        data = e;
        if(data == null) this.height = DEFAULT_HEIGHT - 1 ;
    }

    public TreeNode(E e, COLOR color) {
        data = e;
        this.color = color;
        if(data == null) this.height = DEFAULT_HEIGHT - 1 ;
    }

    public TreeNode<E> getParent() {
        return parent;
    }

    public COLOR getColor() {
        return color;
    }

    public void setColor(COLOR color) {
        this.color = color;
    }

    /**
     * A default value when a node data is not null
     */
    public static final int DEFAULT_HEIGHT = 1;
    public enum COLOR {RED, BLACK}
    private E data;
    private TreeNode<E> left;
    private TreeNode<E> parent;
    private TreeNode<E> right;
    private int height = 1;

    private COLOR color = null;

    public boolean isLeaf() {
        return left == null && right == null;
    }

    public E getData( ) {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    public TreeNode<E> getLeft( ) {
        return left;
    }

    public void setLeft(TreeNode<E> n ) {
        checkParentConnectionAndRemove(n);
        // make sure that this.left.parent also null, so that the left children of this
        // does not have the connection to the parent or predecessor.
        if (this.left != null) this.left.parent = null;
        this.left = n;
    }

    public TreeNode<E> getRight( ) {
        return right;
    }

    public void setRight(TreeNode<E> n ) {
        checkParentConnectionAndRemove(n);
        // make sure that this.right.parent also null, so that the right children of this
        // does not have the connection to the parent or predecessor.
        if (this.right != null) this.right.parent = null;
        this.right = n;
    }

    public int getHeight( ) {
        return height;
    }

    public void setHeight( int height ) {
        this.height = height;
    }

    /**
     * Ensures the parent-child relationship is correctly updated for a given node.
     * This method only be used for the setLeft and setRight
     * If the provided node has a parent, the method disconnects the node from its current parent.
     * It then assigns the current object as the parent of the given node.
     *
     * @param n the TreeNode whose parent connection is being checked and potentially updated
     */
    protected void checkParentConnectionAndRemove(TreeNode<E> n) {
        if(n != null) {
            if (n.parent != null) {
                // make sure that the n does not have any parent anymore
                // since we want to connect n parent to this object
                TreeNode<E> parent = n.parent;
                if (parent.right == n) parent.right = null;
                else parent.left = null;
            }
            //this node should be n's parent
            n.parent = this;
        }
    }

    @Override
    public String toString() {
        // return element and left and right elements
        return data + ": (l: " + left + ", r:" + right + ")";
    }

    public TreeNode<E> copyWithSubtreeOf( TreeNode<E> root) {
        if (root == null) {
            return null;
        }

        TreeNode<E> copied;
        copied = new TreeNode<>(root.getData(), root.color);

        copied.setLeft(copyWithSubtreeOf(root.getLeft()));
        copied.setRight(copyWithSubtreeOf(root.getRight()));
        return copied;
    }
}