package edu.brown.cs.teams.kdtree;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * This class implements a k-d tree of arbitrary number of dimensions
 * and can operate
 * on any Cartesian Point.
 *
 * @param <T> A class that extends Cartesian Point, which guarantees
 *            that it has some spatial position
 */
public class KDTree<T extends CartesianPoint> {
  private KDTreeNode<T> root;
  private int k;
  private List<T> nodes;

  /**
   * Constructor for KDTree class.
   *
   * @param k dimensionality of tree
   */
  public KDTree(int k) {
    this.k = k;
  }

  /**
   * Getter for the root node of the tree.
   *
   * @return root node.
   */
  public KDTreeNode<T> getRoot() {
    return this.root;
  }

  /**
   * Builds a k-d tree given a list of nodes. Stores the root node in the
   * root field.
   *
   * @param treeNodes A list of nodes that store a position to structure into a
   *                  tree
   * @return a k-d tree
   */
  public KDTree<T> buildKDTree(List<T> treeNodes) {
    this.root = buildTree(new KDTreeNode<>(0), treeNodes);
    this.nodes = treeNodes;
    return this;
  }

  /*
  Recursive algorithm to build a k-d tree. Takes in a node, and builds the
   left and right subtrees
  from that node using the list of nodes passed in as input.
   */
  private KDTreeNode<T> buildTree(KDTreeNode<T> curr, List<T> treeNodes) {
    int size = treeNodes.size();

    // only build tree if there are nodes to build
    if (size > 0) {
      // sort the node along the axis of interest to find the median
      int depth = curr.getDepth();
      int axis = depth % this.k;
      treeNodes.sort(new KDNodeComparator(axis));
      int medianPos = treeNodes.size() / 2;
      T median = treeNodes.get(medianPos);

      // the median will be the root node at this depth
      curr.setCartesianPoint(median);

      // recursively build the left and right subtrees;
      curr.setLeft(buildTree(new KDTreeNode<>(depth + 1),
              treeNodes.subList(0, medianPos)));
      curr.setRight(buildTree(new KDTreeNode<>(depth + 1),
              treeNodes.subList(medianPos + 1, size)));

      // return the root node
      return curr;
    } else {
      return null;
    }
  }

  /**
   * Converts a queue to a list because the neighbors and radius commands return
   * a queue of nodes, but the program needs a list of queues to print out the IDs.
   *
   * @param pq priority queue of Cartesian Points
   * @return items in reverse order (furthest to closest)
   */
  public List<T> queueToList(PriorityQueue<T> pq) {
    List<T> l = new ArrayList<>();
    while (!pq.isEmpty()) {
      l.add(pq.poll());
    }
    Collections.reverse(l);
    return l;
  }

  /**
   * Searches for all points that are within a certain distance
   * from a target position.
   * Uses the k-d tree structure to limit the search area of the algorithm
   *
   * @param radius radius to search within
   * @param pos    target position to search around
   * @return nodes within radius of target
   */
  public List<T> radiusSearch(double radius, double[] pos) {
    PriorityQueue<T> pq = new PriorityQueue<>(new DistanceComparator(pos));
    radiusRecursive(this.root, pos, radius, pq);
    return queueToList(pq);
  }

  /*
  Manages the algorithm to find nodes within a certain distance of a target node. Operates
  by making a queue that stores the all nodes within a certain distance of the target,
  and only searching a branch if it can possibly contain another node closer than radius
  distance from the target.
  */
  private void radiusRecursive(KDTreeNode<T> ptr, double[] pos,
                               double radius, PriorityQueue<T> pq) {
    // ensure that the current node is not null
    if (ptr != null && ptr.getCartesianPoint() != null) {
      T curr = ptr.getCartesianPoint();
      double currDistance = curr.getDistance(pos);

      // if the current node is within the search distance, add it to the queue and search both
      // the left and right subtrees.
      if (Double.compare(currDistance, radius) <= 0) {
        pq.add(ptr.getCartesianPoint());
        radiusRecursive(ptr.getRight(), pos, radius, pq);
        radiusRecursive(ptr.getLeft(), pos, radius, pq);
      } else {
        int axis = ptr.getDepth() % this.k;
        double currAxisCoord = curr.getPositionAlongAxis(axis);
        double targetAxisCoord = pos[axis];
        double axisDistance = Math.abs(currAxisCoord - targetAxisCoord);
        // both subtrees could potentially contain nodes within a radius distance from the target.
        if (axisDistance < radius) {
          radiusRecursive(ptr.getRight(), pos, radius, pq);
          radiusRecursive(ptr.getLeft(), pos, radius, pq);
        } else {
          // only look at the right subtree
          if (currAxisCoord < targetAxisCoord) {
            radiusRecursive(ptr.getRight(), pos, radius, pq);
          } else {
            // only look at the left subtree
            radiusRecursive(ptr.getLeft(), pos, radius, pq);
          }
        }
      }
    }
  }

  /**
   * Returns a list of k closest neighbors to a certain point ordered from furthest
   * nodes to closest nodes. Uses the structure of the k-d tree to limit the search
   * space.
   *
   * @param kNeighbors number of neighbors to search for
   * @param pos        target position that is being searched around
   * @return list of closest neighbors
   */
  public List<T> getNeighbors(int kNeighbors, double[] pos) {
    PriorityQueue<T> pq = new PriorityQueue<>(new DistanceComparator(pos));
    if (kNeighbors > 0) {
      neighborsRecursive(this.root, pos, kNeighbors, pq);
    }
    return queueToList(pq);
  }

  /*
  Manages the algorithm to find closest neighbors to a target. Operates
  by making a queue that stores the all nodes within a certain distance of the target,
  and only searching a branch if it can possibly contain another node closer than the furthest
  node in the queue from the target.
   */
  private void neighborsRecursive(KDTreeNode<T> ptr, double[] pos,
                                  int kNeighbors, PriorityQueue<T> pq) {
    // only find neighbors if the node is not null
    if (ptr != null && ptr.getCartesianPoint() != null) {
      // fill up the queue with any neighbors if it is not full
      if (pq.size() < kNeighbors) {
        pq.add(ptr.getCartesianPoint());
        neighborsRecursive(ptr.getRight(), pos, kNeighbors, pq);
        neighborsRecursive(ptr.getLeft(), pos, kNeighbors, pq);
      } else {
        T farthest = pq.peek();
        T curr = ptr.getCartesianPoint();
        assert farthest != null;
        double farDistance = farthest.getDistance(pos);
        double currDistance = curr.getDistance(pos);
        // if the current node is closer than the furthest node in the queue, replace that node
        if (farDistance > currDistance) {
          pq.poll();
          pq.add(curr);
          neighborsRecursive(ptr.getRight(), pos, kNeighbors, pq);
          neighborsRecursive(ptr.getLeft(), pos, kNeighbors, pq);
        } else {
          int axis = ptr.getDepth() % this.k;
          double currAxisCoord = curr.getPositionAlongAxis(axis);
          double farAxisCoord = farthest.getPositionAlongAxis(axis);
          double targetAxisCoord = pos[axis];
          double axisDistance = Math.abs(currAxisCoord - farAxisCoord);
          // search both sides
          if (farDistance > axisDistance
                  || Double.compare(currAxisCoord, farAxisCoord) == 0) {
            neighborsRecursive(ptr.getLeft(), pos, kNeighbors, pq);
            neighborsRecursive(ptr.getRight(), pos, kNeighbors, pq);
          } else {
            // search the right subtree
            if (currAxisCoord < targetAxisCoord) {
              neighborsRecursive(ptr.getRight(), pos, kNeighbors, pq);
            } else {
              // search the left subtree
              neighborsRecursive(ptr.getLeft(), pos, kNeighbors, pq);
            }
          }
        }
      }
    }
  }

  /**
   * Calculates the size of the tree by traversing through it. Only used for testing.
   *
   * @return size of tree
   */
  public int getSize() {
    return getSizeRecursive(root);
  }

  /**
   * Recursive method to calculate size of the tree.
   *
   * @param curr current node.
   * @return size of tree.
   */
  private int getSizeRecursive(KDTreeNode<T> curr) {
    if (curr == null) {
      return 0;
    }
    return 1 + getSizeRecursive(curr.getLeft())
            + getSizeRecursive(curr.getRight());
  }

  /**
   * Determines if a tree is valid by checking if left node is less than equal to current node
   * which is less than equal to right node. Does not check if tree is balanced.
   *
   * @return true if valid, false otherwise
   */
  public boolean isValidKDTree() {
    return isValidKDTreeRecursive(this.root);
  }

  /**
   * Recursive method to see if tree satisfies BST invariant.
   *
   * @param node current node
   * @return true if valid, false otherwise.
   */
  private boolean isValidKDTreeRecursive(KDTreeNode<T> node) {
    if (node == null) {
      return true;
    } else {
      KDTreeNode<T> left = node.getLeft();
      KDTreeNode<T> right = node.getRight();

      if (left == null && right == null) {
        return true;
      } else if (right == null) {
        int axis = node.getDepth() % this.k;
        double currPos = node.getCartesianPoint().getPositionAlongAxis(axis);
        double leftPos = left.getCartesianPoint().getPositionAlongAxis(axis);
        return Double.compare(currPos, leftPos) >= 0
                && isValidKDTreeRecursive(left);
      } else if (left == null) {
        int axis = node.getDepth() % this.k;
        double currPos = node.getCartesianPoint().getPositionAlongAxis(axis);
        double rightPos = right.getCartesianPoint().getPositionAlongAxis(axis);
        return Double.compare(currPos, rightPos) <= 0
                && isValidKDTreeRecursive(right);
      } else {
        int axis = node.getDepth() % this.k;
        double currPos = node.getCartesianPoint().getPositionAlongAxis(axis);
        double leftPos = left.getCartesianPoint().getPositionAlongAxis(axis);
        double rightPos = right.getCartesianPoint().getPositionAlongAxis(axis);
        return Double.compare(currPos, rightPos) <= 0
                && Double.compare(currPos, leftPos) >= 0
                && isValidKDTreeRecursive(left)
                && isValidKDTreeRecursive(right);
      }
    }
  }

  public List<T> naiveKnn(T target) {
    List<T> sorted = new ArrayList<>(this.nodes);
    Collections.sort(sorted, new CompareDist(target));
    return sorted;
  }

  /**
   * Comparator used for the Naive implementation of KNN.
   */
  public class CompareDist implements Comparator<T> {
    private T target;

    public CompareDist(T target) {
      this.target = target;
    }

    @Override
    public int compare(T t1, T t2) {
      return Double.compare(t1.getDistance(target.getPosition()),
              t2.getDistance(target.getPosition()));
    }
  }

}

