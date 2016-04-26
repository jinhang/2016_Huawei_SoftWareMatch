package com.routesearch.route;

import java.util.ArrayList;

public class Vertex {

	boolean wasVisited; // 是否遍历过
	public String label; // 节点名称
	ArrayList<Integer> allVisitedList;// 节点已访问过的顶点

	public void setAllVisitedList(ArrayList<Integer> allVisitedList) {
		this.allVisitedList = allVisitedList;
	}

	public ArrayList<Integer> getAllVisitedList() {
		return allVisitedList;
	}

	public boolean WasVisited() {
		return wasVisited;
	}

	public void setWasVisited(boolean wasVisited) {
		this.wasVisited = wasVisited;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Vertex(String lab) // constructor
	{
		label = lab;
		wasVisited = false;
	}

	public void setVisited(int j) {
		allVisitedList.set(j, 1);

	}

}
