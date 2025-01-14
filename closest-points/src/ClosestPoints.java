import java.util.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

public class ClosestPoints {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    Node[] nodes = loadTSPInput(sc);
    double closestPair = closestPair(nodes);

    DecimalFormat df = new DecimalFormat("###.##############"); 
    String formatted = df.format(closestPair); 
    System.out.println(formatted);
  }

  private static Node[] loadTSPInput(Scanner sc) {
    int dimension = 0;
    String input = sc.nextLine();

    while(!input.contains("NODE_COORD_SECTION")){
      input = sc.nextLine();

      if(input.contains("DIMENSION")){
        dimension = Integer.parseInt(input.split(":")[1].trim());
      }
    }

    System.out.print(dimension + " ");

    Node[] nodes = new Node[dimension];

    for (int i = 0; i < dimension; i++) {
      double id = sc.nextDouble();
      double x = sc.nextDouble();
      double y = sc.nextDouble();
      nodes[i] = new Node(x, y);
    }

    Arrays.sort(nodes, Node::compareByX); 
    return nodes;
  }

  public static double closestPairBrute(Node[] points) {
    double currentMinDist = Double.POSITIVE_INFINITY;

    for (int i = 0; i < points.length; i++) {
      for (int j = i+1; j < points.length; j++) {
        currentMinDist = Math.min(currentMinDist, Node.dist(points[i], points[j]));
      }
    }
    
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
    double delta_lx = points[split].x - delta;
    double delta_rx = points[split].x + delta;

    // Find insertion point
    int delta_li = Math.abs(~Arrays.binarySearch(points, new Node(delta_lx, 0), Node::compareByX));
    int delta_ri = Math.abs(~Arrays.binarySearch(points, new Node(delta_rx, 0), Node::compareByX));

    Node[] s = Arrays.copyOfRange(points, delta_li, delta_ri);
    Arrays.sort(s, Node::compareByY);

    double min_s = Double.POSITIVE_INFINITY;
    for (int i = 0; i < s.length; i++) {
      for (int j = i+1; j < Math.min(i+15, s.length); j++) {
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
      return Point2D.distance(a.x, a.y, b.x, b.y);
    }

    public static int compareByX(Node a, Node b) {
      if (b.x - a.x > 0) return -1;
      else if (b.x - a.x == 0) return 0;
      return 1;
    }
    
    public static int compareByY(Node a, Node b) {
      if (b.y - a.y > 0) return -1;
      else if (b.y - a.y == 0) return 0;
      return 1;
    }

    public String toString() {
      return "(" + x + "," + y + ")";
    }
  }                                                    

  public static <T> void printArr(T[] arr) {
    System.out.print("[");
    for (int i = 0; i < arr.length; i++) System.out.print(arr[i].toString() + " ");
    System.out.println("]");
  }

}

