//How many red vertices in the path? (inklusive 0)
// This is a longest path problem where we weight red vertices.
// This algorithm only works in directed acyclic graphs (DAG's)
// We give all edges a negative weight 

// we assume that the edges are given to us in their directed way 

#include <iostream>
#include <vector>
#include <utility>
#include "GraphReader.cpp"
#include "lib/dijkstra.cpp"
#include "lib/cycle_check.cpp"

using namespace std;

void setupLibraryGraph(vector<vector<pair<int, int>>> *adj, Graph G, Cycle::Graph *cycG) {
  for(Edge e : G.edges)
  {
    if (G.vertices[e.v].isRed)  // Anything -> Red
      graph::addEdge(adj, e.u, e.v, -1);
    else // Anything -> Black
      graph::addEdge(adj, e.u, e.v, 0);

    cycG->addEdge(Cycle::Edge{e.u, e.v});
  }

}

int main() 
{ 
  Graph G = loadGraph();
  vector<vector<pair<int, int>>> adj(
    G.vertices.size(), vector<pair<int, int>>()
  );

  Cycle::Graph* cycG = new Cycle::Graph(
    G.vertices.size(), 
    std::map<int, std::vector<int>>()
  );

  setupLibraryGraph(&adj, G, cycG);

  if (Cycle::CycleCheck::isCyclicBFS(*cycG)) {
    cout << "Graph is cyclic\n";
    return 0;
  }

  int dist = graph::dijkstraWithDefault(&adj, G.s, G.t, 1);
  if (dist == 1) {
    cout << "Target not reachable from source\n";
  } else {
    cout << "Max RED Path Distance : " << -dist << "\n";
  }
  return 0;
}
