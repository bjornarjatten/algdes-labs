#include <iostream>
#include <vector>
#include <utility>
#include "GraphReader.cpp"
#include "lib/dijkstra.cpp"

using namespace std;

int main() 
{
  Graph G = loadGraph();
  // cout << "SIZE: " << G.vertices.size() << "\n";
  vector<vector<pair<int, int>>> adj(
    G.vertices.size(), vector<pair<int, int>>()
  );

  for(Edge e : G.edges)
  {
    if (G.vertices[e.u].isRed || G.vertices[e.v].isRed)
    {
      continue;
    }
    graph::addEdge(&adj, e.u, e.v, 1);
    graph::addEdge(&adj, e.v, e.u, 1);
  }

  // for (size_t i = 0; i < adj.size(); i++)
  // {
  //   for (auto vw : adj[i]){
  //     cout << "Included: " << i << " " << vw.first << " W=" << vw.second << "\n";
  //   }
  // }
  

  int dist = graph::dijkstra(&adj, G.s, G.t);
  // cout << "s: " << G.s << ", t: " << G.t << "\n";
  if (dist == -1) {
    cout << "-1\n";
  } else {
    cout << dist << "\n";
  }
  return 0;
}

// int vertices = int(), edges = int();
//     std::cout << "Enter the number of vertices : ";
//     std::cin >> vertices;
//     std::cout << "Enter the number of edges : ";
//     std::cin >> edges;
 
//     std::vector<std::vector<std::pair<int, int>>> adj(
//         vertices, std::vector<std::pair<int, int>>());
 
//     int u = int(), v = int(), w = int();
//     while (edges--) {
//         std::cin >> u >> v >> w;
//         graph::addEdge(&adj, u, v, w);
//     }
 
//     int s = int(), t = int();
//     std::cin >> s >> t;
//     int dist = graph::dijkstra(&adj, s - 1, t - 1);
//     if (dist == -1) {
//         std::cout << "Target not reachable from source" << std::endl;
//     } else {
//         std::cout << "Shortest Path Distance : " << dist << std::endl;
//     }