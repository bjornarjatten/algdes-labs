//set weight in relfective edges in Red to 1
//set weight for Black -> Red edges to 1
//set weight in reflective edges in Black to Black to 0
//shortest path : sum of weights in shortest path
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

  for(Edge e : G.edges)
  {
    v = G.vertices[e.v]; u = G.vertices[e.u];
    if (v.isRed)  // Anything -> Red
      graph::addEdge(&adj, e.u, e.v, 1);
    else // Anything -> Black
      graph::addEdge(&adj, e.u, e.v, 0);

    if (u.isRed) // Anything -> Red
      graph::addEdge(&adj, e.v, e.u, 1);
    else // Anything -> Black
      graph::addEdge(&adj, e.v, e.u, 0);
  }

  int dist = graph::dijkstra(&adj, G.s, G.t);
  if (dist == -1) {
    cout << "-1\n";
  } else {
    cout << dist << "\n";
  }
  return 0;
}