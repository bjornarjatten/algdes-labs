#include <iostream>
#include <vector>

using namespace std;

class Vertex {
  public:
    int id;
    bool isRed;
};

class Edge {       
  public:
    int u;
    int v;
  Edge(int c_u, int c_v)
  {
    u = c_u;
    v = c_v;
  }
};

class Graph {
  public:
    int s;
    int t;
    vector<Vertex> vertices;
    vector<Edge> edges;
};

Graph loadGraph()
{
  // ios_base::sync_with_studio(0);
  // cin.tie(0);
  
  string redSymbol;
  int V, E, R;
  cin >> V >> E >> R;
  int S, T;
  cin >> S >> T;
  Graph G { S, T };
  
  for (int i = 0; i < V; i++)
  {
    int v; 
    cin >> v;
    if (cin.peek() == 32) 
    {
      cin >> redSymbol;
      G.vertices.emplace_back(Vertex{v, true});
    } 
    else 
    {
      G.vertices.emplace_back(Vertex{v, false});
    }

  }
  
  for (int i = 0; i < E; i++)
  {
    int u, v;
    string dashes;
    cin >> u >> dashes >> v;
    G.edges.emplace_back(Edge{ u, v });
  }

  return G;
}

// int main() 
// {
//   auto [vertices, edges] = loadGraph();
//   // for (Vertex v : vertices) 
//   // {
//   //   cout << v.id << ", is red: " << v.isRed << "\n";
//   // }
//   // for (Edge e : edges) 
//   // {
//   //   cout << e.u << " -- " << e.v << "\n";
//   // }
//   return 0;
// }
