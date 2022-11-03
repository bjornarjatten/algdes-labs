#include <iostream>
#include <vector>
#include <map>
#include <string>

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
  map<string, int> names; 
  string redSymbol;
  int V, E, R;
  cin >> V >> E >> R;
  string Sname, Tname;
  cin >> Sname >> Tname;
  Graph G;
  
  for (int i = 0; i < V; i++)
  {
    string name; 
    cin >> name;
    names[name] = i;

    if (cin.peek() == 32) 
    {
      cin >> redSymbol;
      G.vertices.emplace_back(Vertex{i, true});
    } 
    else 
    {
      G.vertices.emplace_back(Vertex{i, false});
    }

  }
  

  for (int i = 0; i < E; i++)
  {
    string uname, vname;
    int u, v;
    string dashes;
    cin >> uname >> dashes >> vname;
    u = names[uname];
    v = names[vname];

    G.edges.emplace_back(Edge{ u, v });
    if(dashes.find(">") == string::npos) G.edges.emplace_back(Edge{ v, u });
  }

  G.s = names[Sname];
  G.t = names[Tname];

  return G;
}