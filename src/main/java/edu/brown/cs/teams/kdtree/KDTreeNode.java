package edu.brown.cs.teams.kdtree;

/**
 * Class to store a node of the KDTree. Has left and right nodes.
 * @param <T> a point in space
 */
public class KDTreeNode<T extends CartesianPoint> {
  private KDTreeNode<T> left;
  private KDTreeNode<T> right;
  private int depth;
  private T value;

  /**
   * Constructor for KDTreeNode class.
   * @param depth takes in the depth of the current node.
   */
  public KDTreeNode(int depth) {
    this.depth = depth;
  }

  /**
   * Getter for the node's position in space.
   * @return node's position.
   */
  public T getCartesianPoint() {
    return this.value;
  }

  /**
   * Setter for the node's position in space.
   * @param val node's position.
   */
  public void setCartesianPoint(T val) {
    this.value = val;
  }

  /**
   * Getter for depth of current node in k-d tree.
   * @return depth of node
   */
  public int getDepth() {
    return this.depth;
  }

  /**
   * Getter for left subtree.
   * @return left node
   */
  public KDTreeNode<T> getLeft() {
    return this.left;
  }

  /**
   * Setter for left subtree.
   * @param node left node
   */
  public void setLeft(KDTreeNode<T> node) {
    this.left = node;
  }

  /**
   * Getter for right subtree.
   * @return right node
   */
  public KDTreeNode<T> getRight() {
    return this.right;
  }

  /**
   * Setter for right subtree.
   * @param node right node
   */
  public void setRight(KDTreeNode<T> node) {
    this.right = node;
  }
}
