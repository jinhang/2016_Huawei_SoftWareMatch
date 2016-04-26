package com.routesearch.route;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;


public class AFUtil {
	
	public static List<String> havaAllPath(String start,String end,DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> directedGraph,
			List<String> allMidNodes){
		Route route = new Route();
		
		Graph graph= InitGraph(directedGraph);
		
		AF operation = new AF(graph, Integer.parseInt(start),  Integer.parseInt(end),allMidNodes);
		List path =operation.getResult();		
		
		return path;
	}
    
	public static Graph InitGraph(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> directedGraph) {
		
		return new Graph(directedGraph.vertexSet().size(),directedGraph.vertexSet(),directedGraph.edgeSet());
	}  
}
