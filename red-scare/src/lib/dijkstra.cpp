/**
 * @file
 * @brief [Graph Dijkstras Shortest Path Algorithm
 * (Dijkstra's Shortest Path)]
 * (https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm)
 *
 * @author [Ayaan Khan](http://github.com/ayaankhan98)
 *
 * @details
 * Dijkstra's Algorithm is used to find the shortest path from a source
 * vertex to all other reachable vertex in the graph.
 * The algorithm initially assumes all the nodes are unreachable from the
 * given source vertex so we mark the distances of all vertices as INF
 * (infinity) from source vertex (INF / infinity denotes unable to reach).
 *
 * in similar fashion with BFS we assume the distance of source vertex as 0
 * and pushes the vertex in a priority queue with it's distance.
 * we maintain the priority queue as a min heap so that we can get the
 * minimum element at the top of heap
 *
 * Basically what we do in this algorithm is that we try to minimize the
 * distances of all the reachable vertices from the current vertex, look
 * at the code below to understand in better way.
 *
 */
#include <cassert>
#include <iostream>
#include <limits>
#include <memory>
#include <queue>
#include <utility>
#include <vector>

constexpr int64_t INF = std::numeric_limits<int64_t>::max();

/**
 * @namespace graph
 * @brief Graph Algorithms
 */

namespace graph {
/**
 * @brief Function that add edge between two nodes or vertices of graph
 *
 * @param u any node or vertex of graph
 * @param v any node or vertex of graph
 */
void addEdge(std::vector<std::vector<std::pair<int, int>>> *adj, int u, int v,
             int w) {
    (*adj)[u].push_back(std::make_pair(v, w));
    // (*adj)[v - 1].push_back(std::make_pair(u - 1, w));
}

/**
 * @brief Function runs the dijkstra algorithm for some source vertex and
 * target vertex in the graph and returns the shortest distance of target
 * from the source.
 *
 * @param adj input graph
 * @param s source vertex
 * @param t target vertex
 *
 * @return shortest distance if target is reachable from source else -1 in
 * case if target is not reachable from source.
 */
int dijkstra(std::vector<std::vector<std::pair<int, int>>> *adj, int s, int t) {
    /// n denotes the number of vertices in graph
    int n = adj->size();

    /// setting all the distances initially to INF
    std::vector<int64_t> dist(n, INF);

    /// creating a min heap using priority queue
    /// first element of pair contains the distance
    /// second element of pair contains the vertex
    std::priority_queue<std::pair<int, int>, std::vector<std::pair<int, int>>,
                        std::greater<std::pair<int, int>>>
        pq;

    /// pushing the source vertex 's' with 0 distance in min heap
    pq.push(std::make_pair(0, s));

    /// marking the distance of source as 0
    dist[s] = 0;

    while (!pq.empty()) {
        /// second element of pair denotes the node / vertex
        int currentNode = pq.top().second;

        /// first element of pair denotes the distance
        int currentDist = pq.top().first;

        pq.pop();

        /// for all the reachable vertex from the currently exploring vertex
        /// we will try to minimize the distance
        for (std::pair<int, int> edge : (*adj)[currentNode]) {
            /// minimizing distances
            if (currentDist + edge.second < dist[edge.first]) {
                dist[edge.first] = currentDist + edge.second;
                pq.push(std::make_pair(dist[edge.first], edge.first));
            }
        }
    }
    if (dist[t] != INF) {
        return dist[t];
    }

    return -1;
}

int dijkstraWithDefault(std::vector<std::vector<std::pair<int, int>>> *adj, int s, int t, int def) {
    /// n denotes the number of vertices in graph
    int n = adj->size();

    /// setting all the distances initially to INF
    std::vector<int64_t> dist(n, INF);

    /// creating a min heap using priority queue
    /// first element of pair contains the distance
    /// second element of pair contains the vertex
    std::priority_queue<std::pair<int, int>, std::vector<std::pair<int, int>>,
                        std::greater<std::pair<int, int>>>
        pq;

    /// pushing the source vertex 's' with 0 distance in min heap
    pq.push(std::make_pair(0, s));

    /// marking the distance of source as 0
    dist[s] = 0;

    while (!pq.empty()) {
        /// second element of pair denotes the node / vertex
        int currentNode = pq.top().second;

        /// first element of pair denotes the distance
        int currentDist = pq.top().first;

        pq.pop();

        /// for all the reachable vertex from the currently exploring vertex
        /// we will try to minimize the distance
        for (std::pair<int, int> edge : (*adj)[currentNode]) {
            /// minimizing distances
            if (currentDist + edge.second < dist[edge.first]) {
                dist[edge.first] = currentDist + edge.second;
                pq.push(std::make_pair(dist[edge.first], edge.first));
            }
        }
    }
    if (dist[t] != INF) {
        return dist[t];
    }

    return def;
}

}  // namespace graph
