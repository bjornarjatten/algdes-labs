
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



n,m,r = map(int, input().split())
s,t = input().split()

if n > 1000: exit()

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




import sys
print()
print(sys.argv[1])

s,t = V[s], V[t]
# Is s and t in the same component?
if UF.find(s) != UF.find(t): 
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

if len(R) == 0:
    many_dist = redscare_none(G, R, s, t)
    some = 'false'
    many = 0 if many_dist is not None else '-1'
elif not is_cyclic(G, s):
    many_dist = redscare_many(G, R, s, t)
    many = many_dist if many_dist is not None else '-1'
    some = 'true' if many_dist is not None and many_dist > 0 else 'false'
elif len(G) < brute_force_limit: # constant time algorithm, factor 14!
    many_dist = redscare_many_brute(G, R, s, t)
    many = many_dist
    some = 'true' if many_dist is not None and many_dist > 0 else 'false'
else:
    many = '?!'
    some = '?!'

print('true' if alternate else 'false', end='\t')
print(few, end='\t')
print(many, end='\t')
print(none, end='\t')
print(some, end='\t')

