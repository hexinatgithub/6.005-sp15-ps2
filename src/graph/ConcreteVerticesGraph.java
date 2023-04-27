/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of Graph.
 * 
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteVerticesGraph implements Graph<String> {
    
    private final List<Vertex> vertices = new ArrayList<>();
    
    // Abstraction function:
    //   Represent a graph with vertices
    // Representation invariant:
    //   vertices has no same vertex
    //	 vertex in Vertex sources or targets must in vertices
    // Safety from rep exposure:
    //   vertices(), sources(), targets() make a defensive copy
    //	 vertices is private and final
    
    public ConcreteVerticesGraph() {}
    
    // TODO checkRep
    
    @Override public boolean add(String vertex) {
        if (find(vertex) != null) {
			return false;
        }
        vertices.add(new Vertex(vertex));
        return true;
    }
    
    @Override public int set(String source, String target, int weight) {
    	return findOrCreate(source).to(findOrCreate(target), weight);
    }
    
    @Override public boolean remove(String vertex) {
        int i = index(vertex);
        if (i == -1) {
        	return false;
        }
        
        Vertex v = vertices.get(i);
        for (String source : v.sources().keySet()) {
        	Vertex sv = find(source);
        	assert sv != null;
        	sv.to(v, 0);
        }
        for (String target : v.targets().keySet()) {
        	Vertex tv = find(target);
        	assert tv != null;
        	v.to(tv, 0);
        }
        vertices.remove(i);
        return true;
    }
    
    @Override public Set<String> vertices() {
        return new HashSet<>(vertices.stream().map(v -> v.identity()).toList());
    }
    
    @Override public Map<String, Integer> sources(String target) {
        Vertex vertex = find(target);
        return vertex == null ? new HashMap<>() : vertex.sources();
    }
    
    @Override public Map<String, Integer> targets(String source) {
        Vertex vertex = find(source);
        return vertex == null ? new HashMap<>() : vertex.targets();
    }
    
    private int index(String vertex) {
        for (int i = 0; i < vertices.size(); i++)
			if (vertices.get(i).identity().equals(vertex))
				return i;
        return -1;
    }
    
    private Vertex find(String vertex) {
        for (int i = 0; i < vertices.size(); i++)
			if (vertices.get(i).identity().equals(vertex))
				return vertices.get(i);
        return null;
    }
    
    private Vertex findOrCreate(String vertex) {
    	Vertex nv = find(vertex);
    	if (nv != null) {
    		return nv;
    	}
        nv = new Vertex(vertex);
    	vertices.add(nv);
        return nv;
    }
    
    /**
     * Returns a string represent the Graph.
     * Graph will be represent in the follow format, 
     * vertex will appear in insert order:
     * 
     * Vertex to vertex with weight aside represent a edge:
     * 	"1" ---> "2" 1
     * 
     * 
     * Vertex with no arrow represent a vertex with no edge:
     * 	"1"
     * 
     * Empty Graph will be represent by a blank line.
     */
    @Override public String toString() {
    	String result = "";
    	for (Vertex vertex : vertices) {
    		if (vertex.sources().size() == 0 && vertex.targets().size() == 0) {
    			result += vertex + "\n";
    		} else {
    			for (Map.Entry<String, Integer> entry : vertex.targets().entrySet()) {
    				result += "\"%s\" ---> \"%s\" %d\n".formatted(
    						vertex.identity(), entry.getKey(), entry.getValue());
    			}
    		}
    	}
    	return result.isEmpty() ? "\n" : result;
    }
}

/**
 * Represent a Vertex with edges from or to that vertex.
 * Mutable.
 * This class is internal to the rep of ConcreteVerticesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Vertex {
    
	private final String id;
	private final Map<String, Integer> targets = new HashMap<>();
	private final Map<String, Integer> sources = new HashMap<>();
    
    // Abstraction function:
    //   A vertex with source vertex point to it and from that vertex to the targets
    // Representation invariant:
    //   targets and sources weight always > 0
    // Safety from rep exposure:
    //   id, targets, sources are private, final and immutable
	//	 targets, sources map field are immutable
	//	 targets(), sources() make a defensive copy
    
	public Vertex(String id) {
		this.id = id;
	}
    
    // TODO checkRep
    
	public String identity() {
		return id;
	}
	
	/**
	 * Add a edge from that vertex to target, remove edge if weight is zero else
	 * update weight if edge exist.
	 * 
	 * @param target vertex
	 * @param edge weight
	 * @return 0 if edge not exist else return previous weight
	 */
	public int to(Vertex target, int weight) {
		Integer result = null;
		if (weight == 0) {
			result = this.targets.remove(target.id);
			target.sources.remove(id);
		} else {
			result = this.targets.put(target.id, weight);
			target.sources.put(id, weight);
		}
		return result == null ? 0 : result;
	}
	
	/**
	 * Test whether source vertex connected to target.
	 * @param target vertex
	 * @return true if edge exist else return false
	 */
	public boolean connected(Vertex target) {
		return targets.containsKey(target.id);
	}
	
	/**
	 * Get the target vertices with directed edges from a source vertex and the
     * weights of those edges.
     * 
     * @return a map where the key set is the set of labels of vertices such
     *         that this graph includes an edge from source to that vertex, and
     *         the value for each key is the (nonzero) weight of the edge from
     *         source to the key
	 */
	public Map<String, Integer> targets() {
		return new HashMap<>(targets);
	}
	
    /**
     * Get the source vertices with directed edges to a target vertex and the
     * weights of those edges.
     * 
     * @return a map where the key set is the set of labels of vertices such
     *         that this graph includes an edge from that vertex to target, and
     *         the value for each key is the (nonzero) weight of the edge from
     *         the key to target
     */
    public Map<String, Integer> sources() {
    	return new HashMap<>(sources);
    }
    
    /**
     * Returns a string represent the Graph.
     * Graph will be represent in the follow format, 
     * edge will present first, then vertex:
     * 
     * Vertex to vertex with weight aside represent a edge:
     * 	"1" ---> "2" 1
     * 
     * 
     * Vertex with no arrow represent a vertex with no edge:
     * 	"1"
     * 
     * Empty Graph will be represent by a blank line.
     */
    @Override
    public String toString() {
    	return id;
    }
}
