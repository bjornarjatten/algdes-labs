//call many, see if it is 0
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
    cout << "-\n";
    return 0;
  }

  int dist = graph::dijkstraWithDefault(&adj, G.s, G.t, 1);
  if (dist < 0) {
    cout << "true\n";
  } else {
    cout << "false\n";
  }
  return 0;
}
