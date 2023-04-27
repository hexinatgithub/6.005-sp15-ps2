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
import java.util.TreeSet;

/**
 * An implementation of Graph.
 * 
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteEdgesGraph implements Graph<String> {
    
    private final Set<String> vertices = new HashSet<>();
    private final List<Edge> edges = new ArrayList<>();
    
    // Abstraction function:
    //   Represent a Graph with vertices and edges
    // Representation invariant:
    //   edges vertex all contain in vertices
    //	 no more than one edge in edges that source and target is the same
    // Safety from rep exposure:
    //   vertices() make defensive copies
    //	 sources(), targets() return Map only contain immutable object
    
    public ConcreteEdgesGraph() {}
    
    public void checkRep() {
    	for (Edge edge : edges) {
			assert vertices.contains(edge.getSource());
			assert vertices.contains(edge.getTarget());
		}
    }
    
    @Override public boolean add(String vertex) {
        boolean result = vertices.add(vertex);
        checkRep();
        return result;
    }
    
    @Override public int set(String source, String target, int weight) {
    	boolean finded = false;
    	int result = 0;    	
    	vertices.addAll(List.of(source, target));
    	
    	int i = 0;
    	while (i < edges.size()) {
    		Edge edge = edges.get(i);
			if (edge.connected(source, target)) {
				result = edge.getWeight();
				finded = true;
				
				if (weight == 0) {
					edges.remove(i);
				} else {
					edges.set(i, edge.setWeight(weight));
				}
				
				break;
			}
			
			i++;
    	}
    	
    	if (weight > 0 && !finded) {
    		edges.add(new Edge(source, target, weight));
    	}
    	checkRep();
    	return result;
    }
    
    @Override public boolean remove(String vertex) {
    	// vertex not exist, no edge will be remove
    	if(!vertices.remove(vertex)) {
    		return false;
    	}
    	
    	int i = 0;
    	while (i < edges.size()) {
    		Edge edge = edges.get(i);
			if (edge.getSource().equals(vertex) || 
					edge.getTarget().equals(vertex)) {
				edges.remove(i);
			} else {
				i++;
			}
    	}
    	return true;
    }
    
    @Override public Set<String> vertices() {
        return new HashSet<>(vertices);
    }
    
    @Override public Map<String, Integer> sources(String target) {
        if (!vertices.contains(target)) {
        	return new HashMap<>();
        }
        
        Map<String, Integer> result = new HashMap<>();
        for (Edge edge : edges) {
			if (edge.getTarget().equals(target)) {
				result.put(edge.getSource(), edge.getWeight());
			}
		}
        return result;
    }
    
    @Override public Map<String, Integer> targets(String source) {
        if (!vertices.contains(source)) {
        	return new HashMap<>();
        }
        
        Map<String, Integer> result = new HashMap<>();
        for (Edge edge : edges) {
			if (edge.getSource().equals(source)) {
				result.put(edge.getTarget(), edge.getWeight());
			}
		}
        return result;
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
    	String result = "";
    	Set<String> vertices = vertices();
    	
        for (Edge edge : edges) {
        	result += edge + "\n";
        	vertices.removeAll(List.of(edge.getSource(), 
        			edge.getTarget()));
		}
        
        for (String vertex : new TreeSet<>(vertices)) {
        	result += "\"%s\"".formatted(vertex) + "\n";
		}
        return result.isEmpty() ? "\n" : result;
    }
    
}

/**
 * Represent a edge in ConcreteEdgesGraph from source 
 * to target with weight.
 * Immutable.
 * This class is internal to the rep of ConcreteEdgesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Edge {
    
	private final String source;
	private final String target;
	private final int weight;
    
    // Abstraction function:
    //   represent a edge from source to target with weight
    // Representation invariant:
    //   weight > 0
    // Safety from rep exposure:
    //   all fields are private, final and immutable
	//	 setWeight return a new Edge
    
	public Edge(String source, String target, int weight) {
		this.source = source;
		this.target = target;
		this.weight = weight;
	}
    
	public void checkRep() {
        assert weight > 0;
	}
    
	public String getSource() {
        return source;
	}
	
	public String getTarget() {
        return target;
	}
	
	public boolean connected(String source, String target) {
        return this.source.equals(source) && this.target.equals(target);
	}
	
	public int getWeight() {
        return weight;
	}
	
	public Edge setWeight(int weight) {
        return new Edge(this.source, this.target, weight);
	}
    
	@Override
    public String toString() {
        return "\"%s\" ---> \"%s\" %d".formatted(
        		source, target, weight);
    }
}
