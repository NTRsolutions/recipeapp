package com.example.recipe.data.graph;

/******************************************************************************
 *  Compilation:  javac Graph.java
 *  Execution:    java Graph
 *  Dependencies: ST.java SET.java In.java StdOut.java
 *
 *  Undirected graph data type implemented using a symbol table
 *  whose keys are vertices (String) and whose values are sets
 *  of neighbors (SET of Strings).
 *
 *  Remarks
 *  -------
 *   - Parallel edges are not allowed
 *   - Self-loop are allowed
 *   - Adjacency lists store many different copies of the same
 *     String. You can use less memory by interning the strings.
 *
 ******************************************************************************/

import com.example.recipe.data.FoodCategoryList.FoodCategory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 *  The <tt>Graph</tt> class represents an undirected graph of vertices
 *  with string names.
 *  It supports the following operations: add an edge, add a vertex,
 *  get all of the vertices, iterate over all of the neighbors adjacent
 *  to a vertex, is there a vertex, is there an edge between two vertices.
 *  Self-loops are permitted; parallel edges are discarded.
 *  <p>
 *  For additional documentation, see <a href="http://introcs.cs.princeton.edu/45graph">Section 4.5</a> of
 *  <i>Introduction to Programming in Java: An Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 */
public class TagsGraph {
    private static TagsGraph sInstance;

    public static TagsGraph getsInstance() {
        if (sInstance == null) {
            sInstance = new TagsGraph();
            sInstance.constructGraph();
        }

        return sInstance;
    }
    // symbol table: key = string vertex, value = set of neighboring vertices
    private ST<String, SET<String>> st;

    // number of edges
    private int E;

    /**
     * Create an empty graph with no vertices or edges.
     */
    private TagsGraph() {
        st = new ST<String, SET<String>>();
    }

    /**
     * Create an graph from given input stream using given delimiter.
     */
//    public TagsGraph(In in, String delimiter) {
//        st = new ST<String, SET<String>>();
//        while (in.hasNextLine()) {
//            String line = in.readLine();
//            String[] names = line.split(delimiter);
//            for (int i = 1; i < names.length; i++) {
//                addEdge(names[0], names[i]);
//            }
//        }
//    }

    /**
     * Number of vertices.
     */
    public int V() {
        return st.size();
    }

    /**
     * Number of edges.
     */
    public int E() {
        return E;
    }

    // throw an exception if v is not a vertex
    private void validateVertex(String v) {
        if (!hasVertex(v)) throw new IllegalArgumentException(v + " is not a vertex");
    }

    /**
     * Degree of this vertex.
     */
    public int degree(String v) {
        validateVertex(v);
        return st.get(v).size();
    }

    /**
     * Add edge v-w to this graph (if it is not already an edge)
     */
    public void addEdge(String v, String w) {
        if (!hasVertex(v)) addVertex(v);
        if (!hasVertex(w)) addVertex(w);
        if (!hasEdge(v, w)) E++;
        st.get(v).add(w);
        st.get(w).add(v);
    }

    /**
     * Add vertex v to this graph (if it is not already a vertex)
     */
    public void addVertex(String v) {
        if (!hasVertex(v)) st.put(v, new SET<String>());
    }


    /**
     * Return the set of vertices as an Iterable.
     */
    public Iterable<String> vertices() {
        return st;
    }

    /**
     * Return the set of neighbors of vertex v as in Iterable.
     */
    public Iterable<String> adjacentTo(String v) {
        validateVertex(v);
        return st.get(v);
    }

    /**
     * Is v a vertex in this graph?
     */
    public boolean hasVertex(String v) {
        return st.contains(v);
    }

    /**
     * Is v-w an edge in this graph?
     */
    public boolean hasEdge(String v, String w) {
        validateVertex(v);
        validateVertex(w);
        return st.get(v).contains(w);
    }

    /**
     * Return a string representation of the graph.
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (String v : st) {
            s.append(v + ": ");
            for (String w : st.get(v)) {
                s.append(w + " ");
            }
            s.append("\n");
        }
        return s.toString();
    }

    public ArrayList<String> findRelated(String startNode) {
        //breathFirstTraversal
        ArrayList<String> result = new ArrayList<>();

        Queue<String> queue = new LinkedList<>();
        Set<String> processed = new HashSet<>();
        queue.add(startNode);

        while (!queue.isEmpty()) {
            String node = queue.remove();
            processed.add(node);
            System.out.println(node);
            result.add(node);
            Iterable<String> list = adjacentTo(node);
            for (String element : list) {
                if (!processed.contains(element) && !queue.contains(element)) {
                    queue.add(element);
                }
            }
        }

        result.remove(startNode);
        return result;
    }

    private void constructGraph() {
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.CHICKEN.getValue());
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.MUTTON.getValue());
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.LAMB.getValue());
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.FISH.getValue());
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.EGG.getValue());
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.PRAWN.getValue());
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.PORK.getValue());
        addEdge(FoodCategory.NON_VEGETARIAN.getValue(), FoodCategory.BEEF.getValue());

        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.PANEER.getValue());
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.CHUTNEY.getValue());
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.BEVERAGE.getValue());
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.DRINKS.getValue());
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.MILKSHAKES.getValue());
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.SAUCE.getValue());
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.SALAD.getValue());
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.PARATHA.getValue());
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.HEALTHY.getValue());
        addEdge(FoodCategory.VEGETARIAN.getValue(), FoodCategory.SOUTH_INDIAN.getValue());

        addEdge(FoodCategory.BREAKFAST.getValue(), FoodCategory.SANDWHICH.getValue());
        addEdge(FoodCategory.BREAKFAST.getValue(), FoodCategory.DRINKS.getValue());
        addEdge(FoodCategory.BREAKFAST.getValue(), FoodCategory.BEVERAGE.getValue());

        addEdge(FoodCategory.SOUP.getValue(), FoodCategory.VEGETARIAN.getValue());
        addEdge(FoodCategory.SOUP.getValue(), FoodCategory.NON_VEGETARIAN.getValue());

        addEdge(FoodCategory.RAJASTHANI.getValue(), FoodCategory.VEGETARIAN.getValue());
        addEdge(FoodCategory.RAJASTHANI.getValue(), FoodCategory.NON_VEGETARIAN.getValue());

        addEdge(FoodCategory.BENGALI.getValue(), FoodCategory.VEGETARIAN.getValue());
        addEdge(FoodCategory.BENGALI.getValue(), FoodCategory.FISH.getValue());
        addEdge(FoodCategory.BENGALI.getValue(), FoodCategory.NON_VEGETARIAN.getValue());

        addEdge(FoodCategory.PUNJABI.getValue(), FoodCategory.VEGETARIAN.getValue());
        addEdge(FoodCategory.PUNJABI.getValue(), FoodCategory.NON_VEGETARIAN.getValue());

        addEdge(FoodCategory.GUJRATI.getValue(), FoodCategory.VEGETARIAN.getValue());
        addEdge(FoodCategory.GUJRATI.getValue(), FoodCategory.NON_VEGETARIAN.getValue());

        addEdge(FoodCategory.KERALA.getValue(), FoodCategory.VEGETARIAN.getValue());
        addEdge(FoodCategory.KERALA.getValue(), FoodCategory.NON_VEGETARIAN.getValue());

        addEdge(FoodCategory.BAKED.getValue(), FoodCategory.VEGETARIAN.getValue());
        addEdge(FoodCategory.BAKED.getValue(), FoodCategory.NON_VEGETARIAN.getValue());

    }
    public static void main(String[] args) {
        System.out.println("Bredth First Traversal");
        TagsGraph.getsInstance().findRelated(FoodCategory.PARATHA.getValue());

        // print out graph
//        System.out.println(G);

//        // print out graph again by iterating over vertices and edges
//        for (String v : G.vertices()) {
//            System.out.print(v + ": ");
//            for (String w : G.adjacentTo(v)) {
//                System.out.print(w + " ");
//            }
//            System.out.println();
//        }

    }

}
