import java.util.*;

public class ClosestPoints {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    Node[] nodes = loadTSPInput(sc);
    double closestPair = closestPair(nodes);
  }

  private static Node[] loadTSPInput(Scanner sc) {
    String name = sc.nextLine().split("NAME : ")[1];
    String comment = sc.nextLine();
    String type = sc.nextLine().split("TYPE : ")[1];
    int dimension = Integer.parseInt(sc.nextLine().split("DIMENSION: ")[1]);
    String _1 = sc.nextLine();
    String _2 = sc.nextLine();

    Node[] nodes = new Node[dimension];

    for (int i = 0; i < dimension; i++) {
      String[] line = sc.nextLine().split(" ");
      int id = Integer.parseInt(line[0]);
      double x = Double.parseDouble(line[1]);
      double y = Double.parseDouble(line[2]);
      nodes[i] = new Node(x, y);
    }

    Arrays.sort(nodes, Node::compareByX); 
    return nodes;
  }

  public static double closestPairBrute(Node[] points) {
    var currentMinDist = Double.POSITIVE_INFINITY;

    for (int i = 0; i < points.length; i++)
      for (int j = i+1; j < points.length; j++)
        currentMinDist = Math.min(currentMinDist, Node.dist(points[i], points[j]));
    
    return currentMinDist; 
  }


  /**
    * @param points   points sorted by x
  */
  public static double closestPair(Node[] points) {
    // Need at least two points on either side of split
    // for the recursion to make any sense whatsoever!!
    if (points.length <= 3) return closestPairBrute(points);

    // Java truncating integer division, equivalent to floor division
    int split = points.length / 2;


    Node[] left = Arrays.copyOf(points, split + 1);
    Node[] right = Arrays.copyOfRange(points, split + 1, points.length);
    
    double min_l = closestPair(left);
    double min_r = closestPair(right);

    double delta = Math.min(min_l, min_r);
    double delta_lx = points[split+1].x - delta;
    double delta_rx = points[split].x + delta;

    // Find insertion point
    int delta_li = ~Arrays.binarySearch(points, new Node(delta_lx, 0), Node::compareByX);
    int delta_ri = ~Arrays.binarySearch(points, new Node(delta_rx, 0), Node::compareByX);

    Node[] s = Arrays.copyOfRange(points, delta_li, delta_ri+1);
    Arrays.sort(s, Node::compareByY);

    double min_s = Double.POSITIVE_INFINITY;
    for (int i = 0; i < s.length; i++) {
      for (int j = i+1; j < Math.min(i+12, s.length); j++) {
        min_s = Math.min(min_s, Node.dist(s[i], s[j]));
      }
    }

    return Math.min(min_s, delta);
  }

  static class Node {
    public double x;
    public double y;

    public Node(double x, double y) {
      this.x = x;
      this.y = y;
    }

    public static double dist(Node a, Node b) {
      double dist = Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2);
      return dist; 
    }

    public static int compareByX(Node a, Node b) {
      return b.x - a.x > 0 ? -1 : 1;
    }
    
    public static int compareByY(Node a, Node b) {
      return b.y - a.y > 0 ? -1 : 1;
    }
  }                                                    
}

