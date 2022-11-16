
import sys
from math import inf
from collections import deque, defaultdict

class UnionFind:
  def __init__(self, V):
    self.id = [i for i in range(V)]
    self.rn = [1 for _ in range(V)]

  def union(self, u, v):
    idu = self.find(u)
    idv = self.find(v)

    if idu == idv: return

    if self.rn[idu] == self.rn[idv]: self.rn[idv] += 1

    if self.rn[idu] < self.rn[idv]:
      self.id[idu] = self.id[idv]
    else:
      self.id[idv] = self.id[idu]

  def find(self, p):
    while p != self.id[p]:
      self.id[p] = self.id[self.id[p]]
      p = self.id[p]
    return p



def is_cyclic(g, s):
    import sys
    sys.setrecursionlimit(10**6)
    def dfs_cyclic(v, path, seen):
        if v in seen: 
            return False
        seen.add(v)
        for u, w in g[v].items():
            # if we took u -> v then discard v -> u 
            if len(path) > 1 and u == path[-2]: 
                continue
            if u in path: 
                return True 
            path.append(u)
            if dfs_cyclic(u, path, seen): 
                return True
            path.pop()
        return False
    return dfs_cyclic(s, [s], set())



def shortest_path(g, s, t):
    from heapq import heappush, heappop

    path_to = {s: s}
    dist_to = {s: 0}
    queue = [(s, 0)]

    while queue:
        v, d = heappop(queue)
        for u,w in g[v].items():
            if path_to[v] == u: continue
            if u not in dist_to or dist_to[u] > dist_to[v] + w: 
                dist_to[u] = dist_to[v] + w
                path_to[u] = v
                heappush(queue, (u, dist_to[u]))

    if t not in dist_to:
        return None, None

    dist = dist_to[t]
    path = [] 
    while t != s: 
        path.append(t)
        t = path_to[t]
    path.append(t)

    return dist, list(reversed(path))



def all_paths(g, s, t):
    import sys
    sys.setrecursionlimit(10**6)
    def dfs_paths(v, path):
        for u, w in g[v].items():
            # if we took u -> v then discard v -> u 
            if len(path) > 1 and u == path[-2]: continue
            if u in path: continue 

            path.append(u)
            if u == t: yield list(path)
            yield from dfs_paths(u, path)
            path.pop()

    return dfs_paths(s, [s])


def fdfs(graph, source, sink, lvl, flow, seen):
  if source == sink:
    return flow
  if source not in seen:
    seen.add(source)
    for nxt,cap in graph[source].items():
      if cap >= 1 and source in lvl and nxt in lvl and lvl[source] < lvl[nxt]:
        delta = fdfs(graph, nxt, sink, lvl, min(flow, cap), seen)
        if delta >= 1:
          graph[source][nxt] -= delta
          graph[nxt][source] += delta
          return delta
  return 0

def lvlbfs(g, s, t):
  lvl = {s:0}
  q = deque([(s,0)])
  while q:
    v,vlvl = q.pop()
    if v == t: 
      lvl[v] = vlvl + 1
      break
    for w,c in g[v].items():
      if w in lvl or c == 0: continue 
      lvl[w] = vlvl + 1
      q.appendleft((w, vlvl + 1))
  return lvl

def maxflow(G, source, sink, flow=0):
  levels = lvlbfs(G, source, sink)
  if sink not in levels: return flow
  increase = inf
  while increase > 0:
    increase = fdfs(G,source,sink,levels,inf,set())
    flow += increase
  return maxflow(G, source, sink, flow=flow) 



def redscare_none(G, R, s, t):
    G = {
        v: {u: w for u, w in es.items() if u == s or u == t or u not in R} 
        for v, es in G.items() if v == s or v == t or not v in R
    } 

    dist, path = shortest_path(G, s, t)
    return dist

def redscare_alternate(G, R, s, t):
    G = {
        v: {u: w for u, w in es.items() if (u in R) != (v in R) } 
        for v, es in G.items()
    } 

    return any(shortest_path(G, s, t))

def redscare_few(G, R, s, t):
    G = {
        v: {u: (1 if u in R else 0) for u, w in es.items() }
        for v, es in G.items()
    }
    dist, path = shortest_path(G, s, t)
    return dist

def redscare_many(G, R, s, t):
    G = {
        v: {u: (-1 if u in R else 0) for u, w in es.items() }
        for v, es in G.items()
    }
    dist, path = shortest_path(G, s, t)
    return -dist if dist is not None else None

def redscare_many_brute(G, R, s, t):
    paths = all_paths(G,s,t)
    if paths:
        return max([sum([1 if v in R else 0 for v in path]) for path in all_paths(G,s,t)])
    else:
        return None

def redscare_some(G, R, s, t, N):
  G = {
    v: {u: 1 for u in es.keys() if v in G[u]}
    for v,es in G.items()
  }

  G_Split = defaultdict(dict)

  for i in range(N): 
    G_Split[i][N+i] = 1
    G_Split[N+i][i] = 0

  for v,es in G.items():
    for u in es.keys():
      G_Split[N+v][u] = 1

  G = G_Split

  source = -1
  G[source][s] = 1
  G[s][source] = 0
  G[source][t] = 1
  G[t][source] = 1

  for r in R:
    if maxflow(G, source, r) >= 2:
      return True
  return None



n,m,r = map(int, input().split())
s,t = input().split()

R = set()
V = {}
G = {v: {} for v in range(n)}
UF = UnionFind(n)

for i in range(n):
  id, *red = input().split()
  V[id] = i
  if red: R.add(i)

for i in range(m):
  u, e, v = input().split()
  UF.union(V[u],V[v])
  G[V[u]][V[v]] = 1
  if e != '->': G[V[v]][V[u]] = 1

if len(sys.argv) > 2 and sys.argv[2] == 'large' and len(G) < 500:
    exit()
if len(sys.argv) > 2 and sys.argv[2] == 'small' and len(G) > 200:
    exit()


s,t = V[s], V[t]
# Is s and t in the same component?
if UF.find(s) != UF.find(t): 
  print()
  print(sys.argv[1], end='\t')
  print(n, end='\t')
  print('false', end='\t')
  print('-1', end='\t')
  print('-1', end='\t')
  print('-1', end='\t')
  print('false', end='\t')
  exit()

C = UF.find(s)

# Discard other irrelevant components
G = { 
  # edges INTO s or OUT of t should be ignored
  k: {v: w for v, w in vs.items() if v != s and k != t}
  for k,vs in G.items() if UF.find(k) == C
}

# Adjust R to only relevant component
R = [k for k in G if k in R]
brute_force_limit = 14 # theoretically solvable in order of seconds

alternate = redscare_alternate(G, R, s, t)

few_dist = redscare_few(G, R, s, t)
few = few_dist if few_dist else '-1'

none_dist = redscare_none(G, R, s, t)
none = none_dist if none_dist else '-1'

some = 'true' if redscare_some(G, R, s, t, n) else None

if len(R) == 0:
    many_dist = redscare_none(G, R, s, t)
    many = 0 if many_dist is not None else '-1'
    if some is None: some = 'false'
elif not is_cyclic(G, s):
    many_dist = redscare_many(G, R, s, t)
    many = many_dist if many_dist is not None else '-1'
    if some is None and many_dist is not None: some = 'false' if many_dist == 0 else 'true'
elif len(G) < brute_force_limit: # constant time algorithm, factor 14!
    many_dist = redscare_many_brute(G, R, s, t)
    many = many_dist
    if some is None and many_dist is not None: some = 'false' if many_dist == 0 else 'true'
else:
    many = '?!'
some = '?!' if some is None else some


print()
print(sys.argv[1], end='\t')
print(n, end='\t')
print('true' if alternate else 'false', end='\t')
print(few, end='\t')
print(many, end='\t')
print(none, end='\t')
print(some, end='\t')

