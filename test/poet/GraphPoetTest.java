/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package poet;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import graph.Graph;

/**
 * Tests for GraphPoet.
 */
public class GraphPoetTest {
    
    /**	
     * Testing strategy
     * 
     * GraphPoet(File corpus), poem():
     * 	partition on words: 0, 1, >1
     * 
     * GraphPoet(File corpus):
     * 	partition on acyclic:
     * 		no cycle
     *		contain self loop
     *		contain cycle
     *	partition on adjacencies:
     *		0, 1, >1
     *	partition on file readable
     *	partition on file found
     *	partition on case: input contain upper-case word
     *
     * poem():
     * 	partition on bridge word insert: 0, >0
     * 	partition on two-edge-long path: 
     * 		no such path
     * 		has one
     * 		more than two but different weight
     * 		more than two but equal weight
     * 	partition on outcome: input contain upper-case word and 
     * 		bridge word insert is lower case
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /**
     * cover file unreadable
     */
    @Test
    public void testConstructorUnreadable() {
    	final File file = new File("test/poet/unreadable.txt");
    	file.setReadable(false);
    	assertThrows(IOException.class, () -> new GraphPoet(file));
    }
    
    /**
     * cover file not found
     */
    @Test
    public void testConstructorNotFound() {
    	final File file = new File("test/poet/not-exist.txt");
    	assertThrows(IOException.class, () -> new GraphPoet(file));
    }
    
    /**
     * cover words 1
     * 	adjacencies 0
     */
    @Test
    public void testConstructorOneWord() {
    	final File file = new File("test/poet/oneword.txt");
    	GraphPoet poet = graphPoetNoIOExpection(file);
    	Graph<String> graph = poet.getGraph();
    	assertEquals("expected graph", Set.of("hello"), graph.vertices());
    	assertEquals("expected target", 0, graph.sources("hello").size());
    	assertEquals("expected target", 0, graph.targets("hello").size());
    }
    
    /**
     * cover words >1
     * 	no cycle
     * 	adjacencies 1
     * 	input contain upper-case word
     */
    @Test
    public void testConstructorNoCycle() {
    	final File file = new File("test/poet/no-repeated.txt");
    	final GraphPoet poet = graphPoetNoIOExpection(file);
    	Graph<String> graph = poet.getGraph();
    	assertEquals("expected target", Map.of("is", 1), graph.targets("this"));
    	assertEquals("expected target", Map.of("a", 1), graph.targets("is"));
    	assertEquals("expected target", Map.of("test", 1), graph.targets("a"));
    	assertEquals("expected target", Map.of("of", 1), graph.targets("test"));
    	assertEquals("expected target", Map.of("the", 1), graph.targets("of"));
    	assertEquals("expected target", Map.of("mugar", 1), graph.targets("the"));
    	assertEquals("expected target", Map.of("omni", 1), graph.targets("mugar"));
    	assertEquals("expected target", Map.of("theater", 1), graph.targets("omni"));
    	assertEquals("expected target", Map.of("sound", 1), graph.targets("theater"));
    	assertEquals("expected target", Map.of("system.", 1), graph.targets("sound"));
    	assertEquals("expected target", Map.of(), graph.targets("system."));
    }
    
    /**
     * cover words >1
     * 	contain self loop
     * 	adjacencies >1
     * 	input contain upper-case word
     */
    @Test
    public void testConstructorAdjacencies() {
    	File file = new File("test/poet/hello.txt");
    	GraphPoet poet = graphPoetNoIOExpection(file);
    	Graph<String> graph = poet.getGraph();
    	assertEquals("expected target", Map.of("hello,", 2, "goodbye!", 1), graph.targets("hello,"));
    	assertEquals("expected target", Map.of(), graph.targets("goodbye!"));
    }
    
    /**
     * cover bridge word insert 0
     * 	no two-edge-long path
     */
    @Test
    public void testPoemNoEffect() {
    	File file = new File("test/poet/no-repeated.txt");
    	GraphPoet poet = graphPoetNoIOExpection(file);
    	assertEquals("expected poem", "Sun is shining", poet.poem("Sun is shining"));
    }
    
    /**
     * cover bridge word insert >0
     * 	has one two-edge-long path
     * 	input contain upper-case word and bridge word insert is lower case
     */
    @Test
    public void testPoemNoMorePath() {
    	File file = new File("test/poet/where-no-man-has-gone-before.txt");
    	GraphPoet poet = graphPoetNoIOExpection(file);
    	assertEquals("expected poem", 
    			"Seek to explore strange new life and exciting synergies!", 
    			poet.poem("Seek to explore new and exciting synergies!"));
    }
    
    /**
     * cover bridge word insert >0
     * 	has more than two but different weight
     * 	input contain upper-case word and bridge word insert is lower case
     */
    @Test
    public void testPoemMaximumPath() {
    	File file = new File("test/poet/turn.txt");
    	GraphPoet poet = graphPoetNoIOExpection(file);
    	assertEquals("expected poem", "Turn this around", poet.poem("Turn around"));
    }
    
    /**
     * cover bridge word insert >0
     * 	has more than two but equal weight
     */
    @Test
    public void testPoemAlphabeticalOrder() {
    	File file = new File("test/poet/meet.txt");
    	GraphPoet poet = graphPoetNoIOExpection(file);
    	assertEquals("expected poem", "meet ours requirement", poet.poem("meet requirement"));
    }
    
    private GraphPoet graphPoetNoIOExpection(File file) {
    	try {
        	GraphPoet poet = new GraphPoet(file);
        	return poet;
    	} catch (IOException e) {
			assertThrows(Test.None.class, () -> {throw e;});
			return null;
		}
    }
}
