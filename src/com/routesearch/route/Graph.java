package com.routesearch.route;

import java.util.Iterator;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Graph {

	private Vertex vertexList[]; 
	private int adjMat[][]; 

	private int nVerts;
	private static int MAX_VERTS; // n个点

	int i = 0;
	int j = 0;

	public Vertex[] getVertexList() {
		return vertexList;
	}

	public int[][] getAdjMat() {
		return adjMat;
	}

	public int getN() {
		return MAX_VERTS;
	}

	public Graph(int N,Set<String> V,Set<DefaultWeightedEdge> E) {
		//V  {"1","2","3"}
		//E  {"1:2","3:1"}
		this.MAX_VERTS = N;
		adjMat = new int[MAX_VERTS][MAX_VERTS]; // 邻接矩阵
		vertexList = new Vertex[MAX_VERTS]; // 顶点数组
		nVerts = 0;

		for (i = 0; i < MAX_VERTS; i++) {
			for (j = 0; j < MAX_VERTS; j++) {
				adjMat[i][j] = 0;
			}
		}
		for(String a:V){
			addVertex(a);
		}
        // for(String a: V)
		//for(String a: E)
		Iterator<DefaultWeightedEdge> it1=E.iterator(); 
		while(it1.hasNext()){  
			DefaultWeightedEdge a=it1.next(); 
			String [] s_d = a.toString().split(":"); 
			s_d[0] = String.valueOf(s_d[0].trim().substring(1));
			s_d[1] = String.valueOf(s_d[1].trim().substring(0,s_d[1].trim().length()-1));
		
			addEdge(Integer.parseInt(s_d[0]), Integer.parseInt(s_d[1]));
		}
	}

	private void delEdge(int start, int end) {
		adjMat[start][end] = 0;
	}

	private void addEdge(int start, int end) {
		adjMat[start][end] = 1;
	}

	public void addVertex(String lab) {
		vertexList[nVerts++] = new Vertex(lab);
	}

	public String displayVertex(int i) {
		return vertexList[i].getLabel();
	}

	public boolean displayVertexVisited(int i) {
		return vertexList[i].WasVisited();
	}
}