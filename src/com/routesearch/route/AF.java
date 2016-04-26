package com.routesearch.route;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AF {

	boolean isAF = true;
	Graph graph;
	int n;
	int start, end;
	Stack<Integer> theStack;

	private ArrayList<Integer> tempList;
	private String counterexample;
	List<String> allMidNodes;
	public AF(Graph graph, int start, int end,List<String> allMidNodes) {
		this.graph = graph;
		this.start = start;
		this.end = end;
		this.allMidNodes = allMidNodes;
	}

	public List<String>  getResult() {
		//graph.printGraph();
		n = graph.getN();
		theStack = new Stack<Integer>();
		List<String> path = null;
		if (!isConnectable(start, end)) {
			isAF = false;
			counterexample = "节点之间没有通路";
		} 
		else {
			for (int j = 0; j < n; j++) {
				tempList = new ArrayList<Integer>();
				for (int i = 0; i < n; i++) {
					tempList.add(0);
				}
				graph.getVertexList()[j].setAllVisitedList(tempList);
			}

			path = af(start, end,allMidNodes);
		}

		return path;
	}

	private List  af(int start, int end,List<String> allMidNodes) {

		List<String> path = new ArrayList<String>();
		graph.getVertexList()[start].setWasVisited(true); // mark it
		theStack.push(start); // push it
		List<String> list2=null;
		while (!theStack.isEmpty()) {

			int v = getAdjUnvisitedVertex(theStack.peek());
			if (v == -1) // if no such vertex,
			{
				tempList = new ArrayList<Integer>();
				for (int j = 0; j < n; j++) {
					tempList.add(0);
				}
				graph.getVertexList()[theStack.peek()]
						.setAllVisitedList(tempList);// 把栈顶节点访问过的节点链表清空
				theStack.pop();
			} else // if it exists,
			{
				theStack.push(v); // push it
			}

			if (!theStack.isEmpty() && end == theStack.peek()) {
				graph.getVertexList()[end].setWasVisited(false); // mark it
				// theStack  转换成List 判断是不是环  是不是 包含 V‘
				
				if(isResult(theStack, allMidNodes)){
					//可以优化成最优的
					//System.out.println(theStack);
					return theStack;
				}
				//printTheStack(theStack);
			
				theStack.pop();
			}
			
		}
		
		return null;
	}

	private boolean isResult(Stack<Integer> theStack2,List<String> allMidNodes) {
		
		List<String> an =new ArrayList<String>();
		for(Integer a:theStack2){
			for(String b:allMidNodes){
				if(String.valueOf(a).equals(b)){
					an.add(String.valueOf(a));
				}
			}
		}
		if(allMidNodes.size()==an.size()){
			return true;
		}
		
		return false;
	}

	// 判断连个节点是否能连通
	private boolean isConnectable(int start, int end) {
		ArrayList<Integer> queue = new ArrayList<Integer>();
		ArrayList<Integer> visited = new ArrayList<Integer>();
		queue.add(start);
		while (!queue.isEmpty()) {
			for (int j = 0; j < n; j++) {
				if (graph.getAdjMat()[start][j] == 1 && !visited.contains(j)) {
					queue.add(j);
				}
			}
			if (queue.contains(end)) {
				return true;
			} else {
				visited.add(queue.get(0));
				queue.remove(0);
				if (!queue.isEmpty()) {
					start = queue.get(0);
				}
			}
		}
		return false;
	}

	public String counterexample() {
		for (Integer integer : theStack) {
			counterexample += graph.displayVertex(integer);
			if (integer != theStack.peek()) {
				counterexample += "-->";
			}
		}

		return counterexample;
	}

	// 与节点v相邻，并且这个节点没有被访问到，并且这个节点不在栈中
	public int getAdjUnvisitedVertex(int v) {
		ArrayList<Integer> arrayList = graph.getVertexList()[v]
				.getAllVisitedList();
		for (int j = 0; j < n; j++) {
			if (graph.getAdjMat()[v][j] == 1 && arrayList.get(j) == 0
					&& !theStack.contains(j)) {
				graph.getVertexList()[v].setVisited(j);
				return j;
			}
		}
		return -1;
	} // end getAdjUnvisitedVertex()

	public void printTheStack(Stack<Integer> theStack2) {
		for (Integer integer : theStack2) {
			System.out.print(graph.displayVertex(integer));
			if (integer != theStack2.peek()) {
				System.out.print("-->");
			}
		}
	}

}
