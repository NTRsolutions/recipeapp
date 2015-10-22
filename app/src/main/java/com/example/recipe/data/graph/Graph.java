package com.example.recipe.data.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import static com.example.recipe.data.FoodCategoryList.FoodCategory;

// Used to signal violations of preconditions for
// various shortest path algorithms.
class GraphException extends RuntimeException {
    public GraphException(String name) {
        super(name);
    }
}

// Represents an edge in the graph.
class Edge {
    public Vertex dest;   // Second vertex in Edge
    public double cost;   // Edge cost

    public Edge(Vertex d, double c) {
        dest = d;
        cost = c;
    }
}

// Represents an entry in the priority queue for Dijkstra's algorithm.
class Path implements Comparable {
    public Vertex dest;   // w
    public double cost;   // d(w)

    public Path(Vertex d, double c) {
        dest = d;
        cost = c;
    }

    public int compareTo(Object rhs) {
        double otherCost = ((Path) rhs).cost;

        return cost < otherCost ? -1 : cost > otherCost ? 1 : 0;
    }
}

// Represents a vertex in the graph.
class Vertex {
    public String name;   // Vertex name
    public List adj;    // Adjacent vertices
    public double dist;   // Cost
    public Vertex prev;   // Previous vertex on shortest path
    public int scratch;// Extra variable used in algorithm

    public Vertex(String nm) {
        name = nm;
        adj = new LinkedList();
        reset();
    }

    public void reset() {
        dist = Graph.INFINITY;
        prev = null;
        pos = null;
        scratch = 0;
    }

    public PriorityQueue.Position pos;  // Used for dijkstra2 (Chapter 23)
}

// Graph class: evaluate shortest paths.
//
// CONSTRUCTION: with no parameters.
//
// ******************PUBLIC OPERATIONS**********************
// void addEdge( String v, String w, double cvw )
//                              --> Add additional edge
// void printPath( String w )   --> Print path after alg is run
// void unweighted( String s )  --> Single-source unweighted
// void dijkstra( String s )    --> Single-source weighted
// void negative( String s )    --> Single-source negative weighted
// void acyclic( String s )     --> Single-source acyclic
// ******************ERRORS*********************************
// Some error checking is performed to make sure graph is ok,
// and to make sure graph satisfies properties needed by each
// algorithm.  Exceptions are thrown if errors are detected.

public class Graph {
    public static final double INFINITY = Double.MAX_VALUE;
    private Map vertexMap = new HashMap(); // Maps String to Vertex

    private static Graph sInstance;

    public static Graph getsInstance() {
        if (sInstance == null) {
            sInstance = new Graph();
        }

        return sInstance;
    }

    private Graph() {
        constructGraph();
    }
    /**
     * Add a new edge to the graph.
     */

    public void addEdge(String sourceName, String destName) {
        addEdge(sourceName, destName, 0);
    }

    public void addEdge(String sourceName, String destName, double cost) {
        Vertex v = getVertex(sourceName);
        Vertex w = getVertex(destName);
        v.adj.add(new Edge(w, cost));
        w.adj.add(new Edge(v, cost));
    }

    /**
     * Driver routine to handle unreachables and print total cost.
     * It calls recursive routine to print shortest path to
     * destNode after a shortest path algorithm has run.
     */
    public void printPath(String destName) {
        Vertex w = (Vertex) vertexMap.get(destName);
        if (w == null)
            throw new NoSuchElementException("Destination vertex not found");
        else if (w.dist == INFINITY)
            System.out.println(destName + " is unreachable");
        else {
            System.out.print("(Cost is: " + w.dist + ") ");
            printPath(w);
            System.out.println();
        }
    }

    /**
     * If vertexName is not present, add it to vertexMap.
     * In either case, return the Vertex.
     */
    private Vertex getVertex(String vertexName) {
        Vertex v = (Vertex) vertexMap.get(vertexName);
        if (v == null) {
            v = new Vertex(vertexName);
            vertexMap.put(vertexName, v);
        }
        return v;
    }

    /**
     * Recursive routine to print shortest path to dest
     * after running shortest path algorithm. The path
     * is known to exist.
     */
    private void printPath(Vertex dest) {
        if (dest.prev != null) {
            printPath(dest.prev);
            System.out.print(" to ");
        }
        System.out.print(dest.name);
    }

    /**
     * Initializes the vertex output info prior to running
     * any shortest path algorithm.
     */
    private void clearAll() {
        for (Iterator itr = vertexMap.values().iterator(); itr.hasNext(); )
            ((Vertex) itr.next()).reset();
    }

    /**
     * Single-source unweighted shortest-path algorithm.
     */
    public void unweighted(String startName) {
        clearAll();

        Vertex start = (Vertex) vertexMap.get(startName);
        if (start == null)
            throw new NoSuchElementException("Start vertex not found");

        LinkedList q = new LinkedList();
        q.addLast(start);
        start.dist = 0;

        while (!q.isEmpty()) {
            Vertex v = (Vertex) q.removeFirst();

            for (Iterator itr = v.adj.iterator(); itr.hasNext(); ) {
                Edge e = (Edge) itr.next();
                Vertex w = e.dest;
                if (w.dist == INFINITY) {
                    w.dist = v.dist + 1;
                    w.prev = v;
                    q.addLast(w);
                }
            }
        }
    }

    /**
     * Single-source weighted shortest-path algorithm.
     */
    public void dijkstra(String startName) {
        PriorityQueue pq = new BinaryHeap();

        Vertex start = (Vertex) vertexMap.get(startName);
        if (start == null)
            throw new NoSuchElementException("Start vertex not found");

        clearAll();
        pq.insert(new Path(start, 0));
        start.dist = 0;

        int nodesSeen = 0;
        while (!pq.isEmpty() && nodesSeen < vertexMap.size()) {
            Path vrec = (Path) pq.deleteMin();
            Vertex v = vrec.dest;
            if (v.scratch != 0)  // already processed v
                continue;

            v.scratch = 1;
            nodesSeen++;

            for (Iterator itr = v.adj.iterator(); itr.hasNext(); ) {
                Edge e = (Edge) itr.next();
                Vertex w = e.dest;
                double cvw = e.cost;

                if (cvw < 0)
                    throw new GraphException("Graph has negative edges");

                if (w.dist > v.dist + cvw) {
                    w.dist = v.dist + cvw;
                    w.prev = v;
                    pq.insert(new Path(w, w.dist));
                }
            }
        }
    }

    /**
     * Single-source weighted shortest-path algorithm using pairing heaps.
     */
    public void dijkstra2(String startName) {
        PriorityQueue pq = new PairingHeap();

        Vertex start = (Vertex) vertexMap.get(startName);
        if (start == null)
            throw new NoSuchElementException("Start vertex not found");

        clearAll();
        start.pos = pq.insert(new Path(start, 0));
        start.dist = 0;

        while (!pq.isEmpty()) {
            Path vrec = (Path) pq.deleteMin();
            Vertex v = vrec.dest;

            for (Iterator itr = v.adj.iterator(); itr.hasNext(); ) {
                Edge e = (Edge) itr.next();
                Vertex w = e.dest;
                double cvw = e.cost;

                if (cvw < 0)
                    throw new GraphException("Graph has negative edges");

                if (w.dist > v.dist + cvw) {
                    w.dist = v.dist + cvw;
                    w.prev = v;

                    Path newVal = new Path(w, w.dist);
                    if (w.pos == null)
                        w.pos = pq.insert(newVal);
                    else
                        pq.decreaseKey(w.pos, newVal);
                }
            }
        }
    }

    /**
     * Single-source negative-weighted shortest-path algorithm.
     */
    public void negative(String startName) {
        clearAll();

        Vertex start = (Vertex) vertexMap.get(startName);
        if (start == null)
            throw new NoSuchElementException("Start vertex not found");

        LinkedList q = new LinkedList();
        q.addLast(start);
        start.dist = 0;
        start.scratch++;

        while (!q.isEmpty()) {
            Vertex v = (Vertex) q.removeFirst();
            if (v.scratch++ > 2 * vertexMap.size())
                throw new GraphException("Negative cycle detected");

            for (Iterator itr = v.adj.iterator(); itr.hasNext(); ) {
                Edge e = (Edge) itr.next();
                Vertex w = e.dest;
                double cvw = e.cost;

                if (w.dist > v.dist + cvw) {
                    w.dist = v.dist + cvw;
                    w.prev = v;
                    // Enqueue only if not already on the queue
                    if (w.scratch++ % 2 == 0)
                        q.addLast(w);
                    else
                        w.scratch--;  // undo the enqueue increment
                }
            }
        }
    }

    /**
     * Single-source negative-weighted acyclic-graph shortest-path algorithm.
     */
    public void acyclic(String startName) {
        Vertex start = (Vertex) vertexMap.get(startName);
        if (start == null)
            throw new NoSuchElementException("Start vertex not found");

        clearAll();
        LinkedList q = new LinkedList();
        start.dist = 0;

        // Compute the indegrees
        Collection vertexSet = vertexMap.values();
        for (Iterator vsitr = vertexSet.iterator(); vsitr.hasNext(); ) {
            Vertex v = (Vertex) vsitr.next();
            for (Iterator witr = v.adj.iterator(); witr.hasNext(); )
                ((Edge) witr.next()).dest.scratch++;
        }

        // Enqueue vertices of indegree zero
        for (Iterator vsitr = vertexSet.iterator(); vsitr.hasNext(); ) {
            Vertex v = (Vertex) vsitr.next();
            if (v.scratch == 0)
                q.addLast(v);
        }

        int iterations;
        for (iterations = 0; !q.isEmpty(); iterations++) {
            Vertex v = (Vertex) q.removeFirst();

            for (Iterator itr = v.adj.iterator(); itr.hasNext(); ) {
                Edge e = (Edge) itr.next();
                Vertex w = e.dest;
                double cvw = e.cost;

                if (--w.scratch == 0)
                    q.addLast(w);

                if (v.dist == INFINITY)
                    continue;

                if (w.dist > v.dist + cvw) {
                    w.dist = v.dist + cvw;
                    w.prev = v;
                }
            }
        }

        if (iterations != vertexMap.size())
            throw new GraphException("Graph has a cycle!");
    }

    /**
     * Process a request; return false if end of file.
     */
    public static boolean processRequest(BufferedReader in, Graph g) {
        String startName = null;
        String destName = null;
        String alg = null;

        try {
            System.out.print("Enter start node:");
            if ((startName = in.readLine()) == null)
                return false;
            System.out.print("Enter destination node:");
            if ((destName = in.readLine()) == null)
                return false;
            System.out.print(" Enter algorithm (u, d, n, a ): ");
            if ((alg = in.readLine()) == null)
                return false;

            if (alg.equals("u"))
                g.unweighted(startName);
            else if (alg.equals("d")) {
                g.dijkstra(startName);
                g.printPath(destName);
                g.dijkstra2(startName);
            } else if (alg.equals("n"))
                g.negative(startName);
            else if (alg.equals("a"))
                g.acyclic(startName);

            g.printPath(destName);
        } catch (IOException e) {
            System.err.println(e);
        } catch (NoSuchElementException e) {
            System.err.println(e);
        } catch (GraphException e) {
            System.err.println(e);
        }
        return true;
    }

    /**
     * A main routine that:
     * 1. Reads a file containing edges (supplied as a command-line parameter);
     * 2. Forms the graph;
     * 3. Repeatedly prompts for two vertices and
     * runs the shortest path algorithm.
     * The data file is a sequence of lines of the format
     * source destination.
     */
    public static void main(String[] args) {
        Graph g = new Graph();
        try {
            FileReader fin = new FileReader(args[0]);
            BufferedReader graphFile = new BufferedReader(fin);

            // Read the edges and insert
            String line;
            while ((line = graphFile.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);

                try {
                    if (st.countTokens() != 3) {
                        System.err.println("Skipping ill-formatted line " + line);
                        continue;
                    }
                    String source = st.nextToken();
                    String dest = st.nextToken();
                    int cost = Integer.parseInt(st.nextToken());
                    g.addEdge(source, dest, cost);
                } catch (NumberFormatException e) {
                    System.err.println("Skipping ill-formatted line " + line);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }

        System.out.println("File read...");
        System.out.println(g.vertexMap.size() + " vertices");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (processRequest(in, g))
            ;
    }

    private void constructGraph() {
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.CHICKEN.getValue(), 1000);
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.MUTTON.getValue(), 1000);
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.LAMB.getValue(), 1000);
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.FISH.getValue(), 1000);
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.EGG.getValue(), 1500);
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.PRAWN.getValue(), 1000);
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.PORK.getValue(), 1000);
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.BEEF.getValue(), 1000);

        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.PANEER.getValue(), 1000);
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.CHUTNEY.getValue(), 1000);
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.BEVERAGE.getValue(), 1000);
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.DRINKS.getValue(), 1000);
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.MILKSHAKES.getValue(), 1000);
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.SAUCE.getValue(), 1200);
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.SALAD.getValue(), 1000);
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.PARATHA.getValue(), 1200);
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.HEALTHY.getValue(), 1000);
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.SOUTH_INDIAN.getValue(), 1200);

        addEdge(FoodCategory.BREAKFAST.getValue(), FoodCategory.SANDWHICH.getValue(), 1000);
        addEdge(FoodCategory.BREAKFAST.getValue(), FoodCategory.DRINKS.getValue(), 1000);
        addEdge(FoodCategory.BREAKFAST.getValue(), FoodCategory.BEVERAGE.getValue(), 1500);

        addEdge(FoodCategory.DESSERTS.getValue(), FoodCategory.VEGETARIAN.getValue(), 1000);
        addEdge(FoodCategory.DESSERTS.getValue(), FoodCategory.NON_VEGETARIAN.getValue(), 1500);
        addEdge(FoodCategory.DESSERTS.getValue(), FoodCategory.EGG.getValue(), 1200);

        addEdge(FoodCategory.SOUP.getValue(), FoodCategory.VEGETARIAN.getValue(), 1500);
        addEdge(FoodCategory.SOUP.getValue(), FoodCategory.NON_VEGETARIAN.getValue(), 1500);

        addEdge(FoodCategory.RAJASTHANI.getValue(), FoodCategory.VEGETARIAN.getValue(), 1500);
        addEdge(FoodCategory.RAJASTHANI.getValue(), FoodCategory.NON_VEGETARIAN.getValue(), 1500);

        addEdge(FoodCategory.BENGALI.getValue(), FoodCategory.VEGETARIAN.getValue(), 1500);
        addEdge(FoodCategory.BENGALI.getValue(), FoodCategory.FISH.getValue(), 500);
        addEdge(FoodCategory.BENGALI.getValue(), FoodCategory.NON_VEGETARIAN.getValue(), 1200);

        addEdge(FoodCategory.PUNJABI.getValue(), FoodCategory.VEGETARIAN.getValue(), 1500);
        addEdge(FoodCategory.PUNJABI.getValue(), FoodCategory.NON_VEGETARIAN.getValue(), 1000);

        addEdge(FoodCategory.GUJRATI.getValue(), FoodCategory.VEGETARIAN.getValue(), 1000);
        addEdge(FoodCategory.GUJRATI.getValue(), FoodCategory.NON_VEGETARIAN.getValue(), 1500);

        addEdge(FoodCategory.KERALA.getValue(), FoodCategory.VEGETARIAN.getValue(), 1500);
        addEdge(FoodCategory.KERALA.getValue(), FoodCategory.FISH.getValue(), 500);
        addEdge(FoodCategory.KERALA.getValue(), FoodCategory.NON_VEGETARIAN.getValue(), 1000);

        addEdge(FoodCategory.BAKED.getValue(), FoodCategory.VEGETARIAN.getValue(), 1500);
        addEdge(FoodCategory.BAKED.getValue(), FoodCategory.NON_VEGETARIAN.getValue(), 1500);
    }

    public ArrayList<String> findRelated(String startNode) {
        //breathFirstTraversal
        ArrayList<Vertex> resultVertext = new ArrayList<>();

        Vertex startVertext = (Vertex) vertexMap.get(startNode);
        Queue<Vertex> queue = new LinkedList<>();
        Set<Vertex> processed = new HashSet<>();
        queue.add(startVertext);

        while (!queue.isEmpty()) {
            Vertex node = queue.remove();
            processed.add(node);
            System.out.println(node);
            resultVertext.add(node);
            LinkedList<Edge> list = (LinkedList) node.adj;
            TreeMap<String, Edge> treeMap = new TreeMap<>();
            for (Edge element : list) {
                treeMap.put(element.cost + element.dest.name, element);
            }

            for (String key : treeMap.keySet()) {
                Edge element  = treeMap.get(key);
                if (!processed.contains(element.dest) && !queue.contains(element.dest)) {
                    queue.add(element.dest);
                }
            }
        }

        resultVertext.remove(startNode);
        ArrayList<String> resultList = new ArrayList<>();
        for (Vertex vertx : resultVertext) {
            resultList.add(vertx.name);
        }

        resultList.remove(startNode);
        return resultList;
    }
}