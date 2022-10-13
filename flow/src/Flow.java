import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Flow {
  
  public static String[] stations;
  public static Edge[] originalPaths;

  public static void main(String[] args) {
    var sc = new Scanner(System.in);

    int n = Integer.parseInt(sc.nextLine());

    stations = new String[n];

    for(int i = 0; i < n; i++){
      stations[i] = sc.nextLine();
    }

    int m = sc.nextInt();
    
    originalPaths = new Edge[m*2]; 

    for(int i = 0; i < m * 2; i += 2){
      int u = sc.nextInt();
      int v = sc.nextInt();
      int c = sc.nextInt();
      if (c == -1)
        c = Integer.MAX_VALUE;
      originalPaths[i] = new Edge(u, v, c);
      originalPaths[i + 1] = new Edge(v, u, c);
    }

    // Arrays.sort(originalPaths, Collections.reverseOrder());
    sc.close(); 

    System.out.println(fordFulkerson());
  }

  public static int fordFulkerson() {
    int flow = 0;
    
    var foundPath = findPath(0);
    while (foundPath.size() > 0) {
      var before = flow;
      flow += augment(foundPath);
      if ( flow - before <= 0 ) {
        System.out.println("0 flow augment");
      }
      foundPath = findPath(0);
    }

    return flow;
  }

  public static int augment(ArrayList<Edge> p){
    // bottleneck / minimum
    int b = Collections.min(p).c;

    ArrayList<Edge> newP = new ArrayList<Edge>();

    for (Edge edge : p) {
      var updatedOld = new Edge(edge.u, edge.v, edge.c - b);
      var reversed = new Edge(edge.v, edge.u, b);
      newP.add(updatedOld);
      newP.add(reversed);
    }
    
    for (Edge edge : originalPaths) {
      // Collections.
      // p.stream().findFirst(e -> edge.u == e.u && edge.v == e.v && edge.c == e.c)

      for (Edge edge2 : p) {
        if (edge.u == edge2.u && edge.v == edge2.v && edge.c == edge2.c) {
          continue;
        }
        newP.add(edge);
      } 
    }

    Edge[] x = new Edge[newP.size()];
    x = newP.toArray(x);
    // Arrays.sort(x, Collections.reverseOrder());
    originalPaths = x;
    return b;
  }

  /**
   * Finds a path from origins to destinations.
   */
  public static ArrayList<Edge> findPath(int i) {
    ArrayList<Edge> fromStations = new ArrayList<Edge>();
    
    for (Edge edge : originalPaths) {
      int currentStartIndex = edge.u;
      if (currentStartIndex == i) {

        int nextStationIndex = edge.v;
        if(nextStationIndex == 54){
          var result = new ArrayList<Edge>();
          result.add(edge);
          return result;
        }

        fromStations.add(edge);
      }
    }

    for (Edge edge : fromStations){
      int nextStationIndex = edge.v;
      var optimal = findPath(nextStationIndex);
      if (optimal.size() > 0) {
        optimal.add(edge);
        return optimal;
      }
    }

    return new ArrayList<>();
  }
}

class Edge implements Comparable<Edge> {
  int u;
  int v;
  int c;
  public Edge(int u, int v, int c) {
    this.u = u;
    this.v = v;
    this.c = c;
  }

  @Override
  public String toString() {
    return "from " + u + " to " + v + " with c " + c;
  }

  @Override
  public int compareTo(Edge other){  
    if(c == other.c)  
      return 0;  
    else if(c > other.c)  
      return 1;  
    else  
      return -1;
  } 
}

