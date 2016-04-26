package com.routesearch.route;

import java.util.ArrayList;
import java.util.List;

public class InfoNode {
	private int preID;
	private int node;
	private List<Edge> list = new ArrayList<>();
	private int weight;
	private int conditionNum;
	private boolean[] dataState = new boolean[FindUtil.allVertex];
	
	public void setState(CloneNode insert) {
		for (int i = 0; i < FindUtil.allVertex; ++i) {
			if (insert.nodeState(i)) {
				dataState[i] = true;
			}
		}
	}
	public InfoNode(InfoNode preNode, int tempV) {
		if (preNode == null) {
			dataState[tempV] = true;
			node = tempV;
			for (int i = 0; i < FindUtil.condition_Vertex2.length; ++i) {
				if (tempV == FindUtil.condition_Vertex2[i]) {
					conditionNum++;
				}
			}
		} else {
			//标记是否访问
			dataState[tempV] = true;
			list.addAll(preNode.getList());
			node = preNode.getNode();
			Edge tempEdge = FindUtil.defaultgraph.getEdge(node, tempV);
			list.add(tempEdge);
			node = tempV;
			weight = preNode.getWeight();
			weight += tempEdge.getCost();
			for (int i = 0; i < FindUtil.allVertex; ++i) {
				if (preNode.testState(i)) {
					dataState[i] = true;
				}
			}
			for (int i = 0; i < FindUtil.condition_Vertex2.length; ++i) {
				if (tempV == FindUtil.condition_Vertex2[i]) {
					conditionNum++;
				}
			}
			conditionNum += preNode.getConditionNum();
		}
	}
	public void setConditionNum(int conditionNum) {
		this.conditionNum = conditionNum;
	}
	public boolean testState(int tempNode) {
		return dataState[tempNode];
	}
	public int getNode() {
		return node;
	}
	public void setNode(int node) {
		this.node = node;
	}
	public int getConditionNum() {
		return conditionNum;
	}
	public List<Edge> getList() {
		return list;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}

}
