#include <iostream>
#include <vector>
#include <utility>
#include "GraphReader.cpp"
#include "lib/dijkstra.cpp"

int main() 
{
  Vertex v, u;
  Graph G = loadGraph();
  // cout << "SIZE: " << G.vertices.size() << "\n";
  vector<vector<pair<int, int>>> adj(
    G.vertices.size(), vector<pair<int, int>>()
  );

  // Remove all reflective edges (edges that go from black-black or red-red)

  for(Edge e : G.edges)
  {
    v = G.vertices[e.v]; u = G.vertices[e.u];
    if (v.isRed && u.isRed)  continue; // Red -> Red => Removed
    if (!v.isRed && !u.isRed) continue; // black -> black => Removed

    graph::addEdge(&adj, e.u, e.v, 1);
    graph::addEdge(&adj, e.v, e.u, 1);
  }

  // Then do shortest path on this graph.
  
  int dist = graph::dijkstra(&adj, G.s, G.t);
  if (dist == -1) {
    //cout << "Target not reachable from source\n";
    cout << "false\n";
  } else {
    cout << "true\n";
    //cout << "Shortest Path Distance : " << dist << "\n";
  }
  return 0;
}

