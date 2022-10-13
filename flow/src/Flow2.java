import java.util.*;

public class Flow2 {

  public static void main(String[] args) {
    var sc = new Scanner(System.in);
    int n = Integer.parseInt(sc.nextLine());

    var stations = new String[n];
    for(int i = 0; i < n; i++){
      stations[i] = sc.nextLine();
    }

    int m = sc.nextInt();
        
    var g = new Graph(m*2);

    for(int i = 0; i < m; i++) {
      int u = sc.nextInt();
      int v = sc.nextInt();
      int c = sc.nextInt();
      if (c == -1)
        c = Integer.MAX_VALUE;
      g.V.get(u).put(v, new Edge(u,v,c,0));
      g.V.get(v).put(u, new Edge(v,u,c,0));
    }

    var ff = new FordFulkerson(g, 0, 54);
    System.out.println(ff.maxflow);
    for (var s : ff.minCut) {
      System.out.println(s);
    }
    
    sc.close();
  }
}

class Edge {
  int u,v,c;
  public Edge(int u, int v, int c, int f) {
    this.u = u; this.v = v; this.c = c;
  }

  @Override
  public String toString() {
    return u + " " + v + " " + c;
  }
}

class Graph {
  ArrayList<Map<Integer,Edge>> V;
  public Graph(int n) {
    this.V = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      this.V.add(new HashMap<>());
    }
  }
}

class FordFulkerson {
  static int init_threshold = 1;
  int maxflow = 0;
  ArrayList<Edge> minCut;
  public FordFulkerson(Graph g, int source, int sink){
    computeMaxFlow(g, source, sink);
    this.minCut = computeMinCut(g, sink, source);
  }

  int augment(Graph g, int current, int sink, int flow, Set<Integer> seen, int threshold) {
    if (current == sink) {
      return flow;
    } else if (!seen.contains(current)) {
      seen.add(current);
      for (var nxt : g.V.get(current).values()) {
        if (nxt.c >= threshold){
          int increase = augment(g, nxt.v, sink, Math.min(flow, nxt.c), seen, threshold);
          if (increase > 0) {
            nxt.c -= increase;
            g.V.get(nxt.v).get(nxt.u).c += increase;
            return increase;
          }
        }
      }
    }

    return 0;
  }

  void computeMaxFlow(Graph g, int source, int sink){
    int inf = Integer.MAX_VALUE;
    int flow = 0, threshold = init_threshold, increase;

    while (threshold != 0) {
      increase = -1;
      while (increase != 0) {
        increase = augment(
          g,
          source,
          sink,
          inf,
          new HashSet<Integer>(g.V.size()),
          threshold
        );
        flow += increase;
      }
      threshold /= 2;
    } 

    this.maxflow = flow;
  }

  ArrayList<Edge> computeMinCut(Graph g, int sink, int source) {
    var cut = new ArrayList<Edge>(); 
    var marked = new HashSet<Integer>();

    augment(g,source,sink,1,marked,1);
    
    for (int i = 0; i < g.V.size(); i++) {
      if (marked.contains(i)) {
        // System.out.println(g.V.get(i).entrySet().size());
        if (g.V.get(i).entrySet().size() == 2){
          int u = -1;
          int v = -1;
          int c = -1;

          for (var entry : g.V.get(i).entrySet()) {
            var edge = entry.getValue();
            if (edge.c == 0){
              u = edge.u;
              v = edge.v;
            } else {
              c = edge.c;
            }
          }
          cut.add(new Edge(u, v, c, 0));
        }  
        
        // check all  
      }
    }
    return cut;
  }
}
