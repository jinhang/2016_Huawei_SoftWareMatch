package com.routesearch.route;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Edge extends DefaultWeightedEdge {

	public Edge() {
	}

	private int index;
	private int st;
	private int end;
	private int cost;

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getSt() {
		return st;
	}

	public void setSt(int start) {
		this.st = start;
	}

	public int geten() {
		return end;
	}

	public void seten(int end) {
		this.end = end;
	}

	public int getid() {
		return index;
	}
	public void setid(int index) {
		this.index = index;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
