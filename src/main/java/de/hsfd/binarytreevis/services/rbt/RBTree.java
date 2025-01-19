package de.hsfd.binarytreevis.services.rbt;

import de.hsfd.binarytreevis.services.Author;
import de.hsfd.binarytreevis.services.TreeException;
import de.hsfd.binarytreevis.services.TreeNode;
import de.hsfd.binarytreevis.services.TreeNode.COLOR;
import de.hsfd.binarytreevis.services.TreeService;

import java.util.Objects;

import static de.hsfd.binarytreevis.services.TreeNode.COLOR.BLACK;
import static de.hsfd.binarytreevis.services.TreeNode.COLOR.RED;

@Author(name = "Agha Muhammad Aslam", date = "31 Dec 2024")
public class RBTree<E extends Comparable<E>> extends TreeService<E> {

    public RBTree() {
        super();
    }

    public RBTree(TreeService<E> treeService) {
        super(treeService);
    }

    @Override
    public void insert(E data) throws TreeException {
        TreeNode<E> newNode = new TreeNode<>(data, RED);
        insertNode(newNode);
        RBInsertFixup(newNode);
    }

    @Override
    protected TreeNode<E> deleteTarget(TreeNode<E> parentTarget, TreeNode<E> target, CHILD positionOfTarget) throws TreeException {
        // target found
        if(parentTarget == null && target.isLeaf()) {
            // target is the root and only one
            addRecord("> Case 0 the element is the root and the only one. Set the root into null\n");
            this.setRoot(null);
        } else {
            TreeNode<E> x, nullNode = new TreeNode<>(null, BLACK);
            COLOR targetOriginalColor = target.getColor();
            if(target.getRight() != null && target.getLeft() != null) {
                // Case 2 if the target has two children. Based on Lecture AlgoDS 24/25 HS Fulda
                // Then take the inorder approach to find the smallest children of the right target's children
                parentTarget = replaceWithTheSmallestOfRightChildren(target);
                
                if (target == parentTarget) {
                    // target.getRight() does not have other child.
                    // y.p == z, based on Delete Method the book Introduction to Algorithm
                    if(target.getRight() == null){
                        target.setRight(nullNode);
                        x = nullNode;
                    } else x = target.getRight();
                } else {
                    // the smallest value, children or grandchildren of target.getRight() exist
                    // y.p != z, based on Delete Method the book Introduction to Algorithm
                    if(parentTarget.getRight() == null){
                        parentTarget.setRight(nullNode);
                        x = nullNode;
                    } else x = parentTarget.getRight();
                }
            } else {
                x = deleteTargetWithOneChildOrNone(parentTarget, target, positionOfTarget);
                if(x.getData() == null) nullNode = x;
            }

            if(targetOriginalColor == BLACK && x.getData() != null) {
                this.newRBDeleteFixup(x);
            }
            return nullNode;
        }
        return null;
    }

    /**
     * A fix method of the tree to make sure the tree stays
     * balanced according to Red-Black Tree concept. Bottom-up approach.
     * Inspired from Introduction to Algorithm
     * @param z the inserted node that has been connected to the tree
     */
    private void RBInsertFixup(TreeNode<E> z) {
        StringBuilder record = new StringBuilder();
        while (z.getParent() != null &&
                z.getParent().getParent() != null &&
                z.getParent().getColor() == RED) {

            TreeNode<E> parent = z.getParent();
            TreeNode<E> grandParent = parent.getParent();

            boolean isGrandparentLeftChild = parent == grandParent.getLeft();
            TreeNode<E> case1 = isGrandparentLeftChild ? grandParent.getRight() : grandParent.getLeft();
            TreeNode<E> case2 = isGrandparentLeftChild ? parent.getRight() : parent.getLeft();

            TreeNode<E> uncleY = case1 == null ? new TreeNode<>(null, BLACK) : case1;
            if (uncleY.getColor() == RED) {
                record.append("> Case 1: change color [uncleY (").append(uncleY.getData())
                        .append(") to black, parent (").append(parent.getData())
                        .append(") to black, grandParent (").append(grandParent.getData())
                        .append(") to red]. Set z to grandParent (").append(grandParent.getData()).append(").\n")
                                .append("Before:\n");
                recordTreeAsImage(z.getParent(),record);

                parent.setColor(BLACK);
                uncleY.setColor(BLACK);
                grandParent.setColor(RED);
                z = grandParent;

                record.append("after:\n");
                recordTreeAsImage(z.getParent(),record);
            } else {
                if (z == case2) {
                    record.append("> Case 2: Set z (").append(z.getData())
                            .append(") to parent (").append(parent.getData())
                            .append(") and then rotate z (").append(parent.getData()).append(").\n")
                            .append("Before:\n");
                    recordTreeAsImage(z.getParent(),record);

                    z = parent;
                    if (isGrandparentLeftChild) leftRotate(z);
                    else rightRotate(z);
                    parent = parent.getParent();

                    record.append("after:\n");
                    recordTreeAsImage(z.getParent(),record);

                }
                record.append("> Case 3: rotate grandParent (").append(grandParent.getData()).append("). ")
                        .append("Change color grandParent (").append(grandParent.getData())
                        .append(") to red and parent (").append(parent.getData()).append(") to black.\n")
                        .append("Before:\n");
                recordTreeAsImage(z.getParent(),record);

                parent.setColor(BLACK);
                grandParent.setColor(RED);
                if (isGrandparentLeftChild) rightRotate(grandParent);
                else leftRotate(grandParent);

                record.append("After:\n");
                recordTreeAsImage(z.getParent(),record);
            }
        }
        record.append("> Loop is finished, set the root (").append(this.getRoot().getData()).append(") into black\n");
        this.getRoot().setColor(BLACK);
        addRecord(record.toString());
    }

    /**
     * Repairs the Red-Black Tree properties after a node deletion, ensuring the tree maintains its color and
     * structural properties. This method resolves color violations and structure imbalances that may arise
     * during the deletion process.
     * This is an optimised algorithm design of {@link RBTree#RBDeleteFixup(TreeNode)}
     *
     * @param x the node from which to start the fix-up process; this is typically
     *          the node in place of the deleted node or its sibling.
     */
    private void newRBDeleteFixup(TreeNode<E> x) {
        StringBuilder record = new StringBuilder("> start delete fixup from "+x.getData()+"\n");
        while (x != this.getRoot() && x.getColor() == BLACK ) {
            
            boolean isLeftChildrenOfParent = x == x.getParent().getLeft();
            TreeNode<E> w = isLeftChildrenOfParent ? x.getParent().getRight() : x.getParent().getLeft();
            
            w = w == null ? new TreeNode<>(null, BLACK) : w;
            
            if(w.getColor() == RED) {
                // case 1
                record.append("> Case 1: change color w(").append(w.getData())
                        .append("), x(").append(x.getData())
                        .append(") and x.parent (").append(x.getParent().getData())
                        .append("). Rotate x and then the sibling is w \n")
                        .append("Before:\n");
                recordTreeAsImage(x.getParent(),record);

                w.setColor(BLACK);
                x.getParent().setColor(RED);
                if(isLeftChildrenOfParent) leftRotate(x.getParent());
                else rightRotate(x.getParent());
                w = isLeftChildrenOfParent ? x.getParent().getRight() : x.getParent().getLeft();

                record.append("After:\n");
                recordTreeAsImage(x.getParent(),record);
            }
            w = w == null ? new TreeNode<>(null, BLACK) : w;
            TreeNode<E> wLeftChild = w.getLeft() == null ? new TreeNode<>(null, BLACK) : w.getLeft();
            TreeNode<E> wRightChild = w.getRight() == null ? new TreeNode<>(null, BLACK) : w.getRight();
            
            if(wLeftChild.getColor() == BLACK && wRightChild.getColor() == BLACK) {
                // case 2
                record.append("> Case 2: change color w (").append(w.getData())
                        .append(") to red and x (").append(x.getData())
                        .append(") is the x.parent (").append(x.getParent().getData()).append(")\n")
                        .append("Before:\n");
                recordTreeAsImage(x.getParent(),record);

                w.setColor(RED);
                x = x.getParent();

                record.append("after:\n");
                recordTreeAsImage(x.getParent(),record);
            } else {
                COLOR wChildrenColor = isLeftChildrenOfParent ? wRightChild.getColor() : wLeftChild.getColor();
                if(wChildrenColor == BLACK) {
                    // case 3
                    // The children of x in the if statement must not be null,
                    // otherwise something totally wrong!
                    if (isLeftChildrenOfParent) {
                        E leftData = Objects.requireNonNull(w.getLeft()).getData();
                        record.append("> Case 3: set w left child (").append(leftData).append(") into black. ")
                                .append("Before:\n");
                        recordTreeAsImage(x.getParent(),record);
                        w.getLeft().setColor(BLACK);
                    } else {
                        E rightData = Objects.requireNonNull(w.getRight()).getData();
                        record.append("> Case 3: set w right child (").append(rightData).append(") into black. ")
                                .append("Before:\n");
                        recordTreeAsImage(x.getParent(),record);
                        w.getRight().setColor(BLACK);
                    }

                    w.setColor(RED);

                    record.append("Change w (").append(w.getData()).append(") color into red. ")
                            .append("\n");
                    recordTreeAsImage(x.getParent(),record);

                    if (isLeftChildrenOfParent) rightRotate(w);
                    else leftRotate(w);

                    record.append("Rotate w (").append(w.getData()).append("). ")
                            .append("\n");
                    recordTreeAsImage(x.getParent(),record);

                    w = isLeftChildrenOfParent ? x.getParent().getRight() : x.getParent().getLeft();
                    w = w == null ? new TreeNode<>(null, BLACK) : w;

                    record.append("The sibling of x (").append(x.getData()).append(") become w (").append(w.getData()).append(") \n")
                            .append("After:\n");
                    recordTreeAsImage(x.getParent(),record);

                }
                // case 4
                w.setColor(x.getParent().getColor());

                record.append("> Case 4: Set w (").append(w.getData()).append(") color to x.parent (")
                        .append(x.getParent().getColor()).append(") color.\n");
                recordTreeAsImage(x.getParent(),record);

                x.getParent().setColor(BLACK);

                record.append("Set x.parent (").append(x.getParent().getData()).append(") color to BLACK.\n");
                recordTreeAsImage(x.getParent(),record);

                if (isLeftChildrenOfParent) {
                    w.getRight().setColor(BLACK);

                    record.append("Set w.right child (").append(w.getRight().getData()).append(") color to BLACK.\n");
                    recordTreeAsImage(x.getParent(),record);
                } else {
                    w.getLeft().setColor(BLACK);

                    record.append("Set w.left child (").append(w.getLeft().getData()).append(") color to BLACK.\n");
                    recordTreeAsImage(x.getParent(),record);
                }

                if (isLeftChildrenOfParent) leftRotate(x.getParent());
                else rightRotate(x.getParent());

                record.append("Perform Rotation on x.parent (").append(x.getParent().getData()).append(").\n");
                recordTreeAsImage(x.getParent(),record);

                x = this.getRoot();
                record.append("Set x to the root (").append(x.getData()).append("). \n")
                        .append("After:\n");
                recordTreeAsImage(x.getParent(),record);
            }
        }
        x.setColor(BLACK);
        record.append("> Finish delete fixup. Change x (").append(x.getData()).append(") color to BLACK.\n");
        addRecord(record.toString());
    }

    /**
     * Repairs the Red-Black Tree properties after a node deletion, ensuring the tree maintains its color and
     * structural properties. This method resolves color violations and structure imbalances that may arise
     * during the deletion process.
     * This approach is pure code based on the book of Introduction to algorithm
     * @deprecated Because there is another function that is much more concise. Please refer to {@link RBTree#newRBDeleteFixup(TreeNode)}
     * @param x the node to start fixing from, typically the replacement node or the parent's child
     *          after the deletion.
     */
    @Deprecated
    @SuppressWarnings("unused")
    private void RBDeleteFixup(TreeNode<E> x) {
        while (x != this.getRoot() && x.getColor() == BLACK ) {
            if(x == x.getParent().getLeft()) {
                TreeNode<E> w = x.getParent().getRight();
                if(w.getColor() == RED) {
                    w.setColor(BLACK);
                    x.getParent().setColor(RED);
                    leftRotate(x.getParent());
                    w = x.getParent().getRight();
                }
                if(w.getLeft().getColor() == BLACK && w.getRight().getColor() == BLACK) {
                    w.setColor(RED);
                    x = x.getParent();
                } else {
                    if(w.getRight().getColor() == BLACK) {
                        w.getLeft().setColor(BLACK);
                        w.setColor(RED);
                        rightRotate(w);
                        w = x.getParent().getRight();
                    }
                    w.setColor(x.getParent().getColor());
                    x.getParent().setColor(BLACK);
                    w.getRight().setColor(BLACK);
                    leftRotate(x.getParent());
                    x = this.getRoot();
                }
            }
            else {
                TreeNode<E> w = x.getParent().getLeft();
                if(w.getColor() == RED) {
                    w.setColor(BLACK);
                    x.getParent().setColor(RED);
                    rightRotate(x.getParent());
                    w = x.getParent().getLeft();
                }
                if(w.getRight().getColor() == BLACK && w.getLeft().getColor() == BLACK) {
                    w.setColor(RED);
                    x = x.getParent();
                } else {
                    if(w.getLeft().getColor() == BLACK) {
                        w.getRight().setColor(BLACK);
                        w.setColor(RED);
                        leftRotate(w);
                        w = x.getParent().getLeft();
                    }
                    w.setColor(x.getParent().getColor());
                    x.getParent().setColor(BLACK);
                    w.getLeft().setColor(BLACK);
                    rightRotate(x.getParent());
                    x = this.getRoot();
                }
            }
            x.setColor(BLACK);
        }
    }
}