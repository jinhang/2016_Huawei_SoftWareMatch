package com.routesearch.route;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public final class FindUtil
{
	/**
	 * info
	 */
	protected static int st;
	protected static int en;
	protected static int start_VV;
	protected static int endVv;
	private static CloneNode bfsNode;
	protected static int[] condition_Vertex;
	protected static int[] condition_Vertex2;
	protected static boolean[] state_withbfs;
	private static int conditionCount;
	private static int edgeNum = 0;
	protected static int[] degreeNumber = new int[600];
	private static Map<String, List<List<Edge>>> EdgeInfo = new HashMap<String, List<List<Edge>>>();
	private static int[] condiVeSta = new int[600];
	private static int[][] MATRIX = new int [600][600];
	private static Map<String, List<Edge>> edgeList = new HashMap<>();
	protected static int[][] data  = new int[600][600];
	private static List<Integer> listpath = new ArrayList<Integer>();
	private static List<List<Integer>> path_info = new ArrayList<List<Integer>>();
	private static Map<Integer, List<Integer>> resultGraph_re = new HashMap<Integer, List<Integer>>();
	protected static int allVertex;
	private static long star_tTime;
	private static long end_Time;
	protected static DefaultDirectedWeightedGraph<Integer, Edge> defaultgraph;
	private static List<Edge> temp_path = new ArrayList<>();
	private static int minCost = Integer.MAX_VALUE;
	private static int aCost = 0;
	private static List<Edge> a_pathedge = new ArrayList<>();
	private static boolean flag = true;
	private static int[] cutNum = new int[600];
	/**
	 * find
	 * @param graphContent
	 * @param condition
	 * @return
	 */
	public static String find(String graphContent, String condition)
	{
		int len1 =condition_Vertex.length;
		star_tTime = System.currentTimeMillis();
		build(graphContent, condition);
		String re = "";
		boolean findhuan=false;
		if(findhuan == true){
			
		}
		for (int i = 0; i < len1; ++i) {
			if(findhuan == true){
				List<List<Edge>> ll = new ArrayList<List<Edge>>();
				Solution_BFS(st, condition_Vertex[i], ll);
				EdgeInfo.put(st+","+condition_Vertex[i], ll);
			}
			List<List<Edge>> ll = new ArrayList<List<Edge>>();
			Solution_BFS(st, condition_Vertex[i], ll);
			EdgeInfo.put(st+","+condition_Vertex[i], ll);
			ll = new ArrayList<List<Edge>>();
			if(findhuan == true){
				Solution_BFS(condition_Vertex[i], en, ll);
				EdgeInfo.put(condition_Vertex[i]+","+en, ll);
			}
			Solution_BFS(condition_Vertex[i], en, ll);
			EdgeInfo.put(condition_Vertex[i]+","+en, ll);
		}
		int tex = 0;
		for (int i = 0; i < len1; ++i) {
			if(findhuan == true){
				break;
			}
			for (int j = i+1; j < len1; ++j) {
				if(findhuan == true){
					listpath.clear();
					break;
				}
				listpath.clear();
				for(int q = 0;q<tex;q++){
					if(findhuan == true){
					
						break;
					}
					ArrayList<List<Edge>> mnode = new ArrayList<List<Edge>>();
					if(findhuan == true){
						Solution_BFS(condition_Vertex[j], condition_Vertex[i], mnode);
						break;
					}
					Solution_BFS(condition_Vertex[j], condition_Vertex[i], mnode);
				}
				List<List<Edge>> pa = new ArrayList<List<Edge>>();
				if(findhuan == true){
					Solution_BFS(condition_Vertex[i], condition_Vertex[j], pa);
					EdgeInfo.put(condition_Vertex[i]+","+condition_Vertex[j], pa);
					break;
				}
				Solution_BFS(condition_Vertex[i], condition_Vertex[j], pa);
				EdgeInfo.put(condition_Vertex[i]+","+condition_Vertex[j], pa);
				if(tex==Integer.MAX_VALUE){
					if(findhuan == true){
						break;
					}
					Solution_BFS(condition_Vertex[j], condition_Vertex[i], pa);
					EdgeInfo.put(condition_Vertex[j]+","+condition_Vertex[i], pa);
					for (int p = 0; i < len1; ++i) {
						List<List<Edge>> ll = new ArrayList<List<Edge>>();
						if(findhuan == true){
							condiVeSta[st] ++;
							Dfsprocess(st);
						}
						Solution_BFS(st, condition_Vertex[i], ll);
						EdgeInfo.put(st+","+condition_Vertex[i], ll);
						ll = new ArrayList<List<Edge>>();
						Solution_BFS(condition_Vertex[i], en, ll);
						EdgeInfo.put(condition_Vertex[i]+","+en, ll);
					}
				}
				//condiVeSta[st] ++;
				pa = new ArrayList<List<Edge>>();
				Solution_BFS(condition_Vertex[j], condition_Vertex[i], pa);
				if(findhuan == true){
					condiVeSta[st] ++;
					Dfsprocess(st);
				}
				EdgeInfo.put(condition_Vertex[j]+","+condition_Vertex[i], pa);
			}
		}
		condiVeSta[st] ++;
		Dfsprocess(st);
		/**
		 * NA
		 */
		if (a_pathedge.size() <= 0) {
			re= "NA";
		}
		/**
		 * 要求格式
		 */
		StringBuffer str_an = new StringBuffer();
		str_an.append(a_pathedge.get(0).getid());
		int len2 =  a_pathedge.size();
		for (int j = 1; j < len2; ++j) {
			str_an.append("|"+a_pathedge.get(j).getid());
		}
		re = str_an.toString();
		return re;
	}

	public static void Solution_BFS(int s, int e, List<List<Edge>> pathList) {
		
		boolean[] tempState = new boolean[allVertex];
		for (int jj = 0; jj < condition_Vertex.length; ++jj) {
			tempState[condition_Vertex[jj]] = true;
		}
		boolean visitedfor_st = true;
		tempState[e] = false;

		Queue<NodeInfo> bfsQueue = new LinkedList<>();
		bfsQueue.add(new NodeInfo(s, null));
		while (!bfsQueue.isEmpty()) {
			NodeInfo pollNode = bfsQueue.poll();
			tempState[pollNode.getNode()] = true;
			for (int i = 0; i < FindUtil.allVertex; ++i) {
				while(visitedfor_st==false){
					if (i == e) {
						List<Edge> tempList = new ArrayList<>();
						List<Integer> tempNodeList = new ArrayList<>();
						tempNodeList.add(i);
					}
					bfsQueue.add(new NodeInfo(i, pollNode));
				}
				if (FindUtil.data[pollNode.getNode()][i] != 0 && !tempState[i]) {
					if (i == e) {
						List<Edge> tempList = new ArrayList<>();
						List<Integer> tempNodeList = new ArrayList<>();
						
						tempNodeList.add(i);
						NodeInfo tempNode = pollNode;
						while (tempNode.getPreNode() != null) {
							tempNodeList.add(tempNode.getNode());
							tempNode = tempNode.getPreNode();
						}
						tempNodeList.add(s);
						for (int j = tempNodeList.size(); j > 1; --j) {
							while(visitedfor_st==false){
								if (i == e) {
									tempNodeList.add(i);
								}
								bfsQueue.add(new NodeInfo(i, pollNode));
							}
							tempList.add(defaultgraph.getEdge(tempNodeList.get(j-1), tempNodeList.get(j-2)));
						}
						pathList.add(tempList);
					}
					bfsQueue.add(new NodeInfo(i, pollNode));
				}
			}
		}
	}
	public static InfoNode a_bfsprocess(CloneNode insert, int s, int e) {
		start_VV = s;
		endVv = e;
//		Map<Integer, ArrayList<String>> allEdge = new HashMap<Integer, ArrayList<String>>();  //存储Edge、weight和LinkID，使用LinkID做key，[edge,weight]做value
//
//		for(int i = 0; i < allGraphLine.length; i++)
//		{
//			
//			String[] everyGrapgLine = allGraphLine[i].split(",");
//			if(! allNode.contains(everyGrapgLine[1]))
//			{
//				allNode.add(everyGrapgLine[1]);
//				directedGraph.addVertex(everyGrapgLine[1]);
//			}
//			if(! allNode.contains(everyGrapgLine[2]))
//			{
//				allNode.add(everyGrapgLine[2]);
//				directedGraph.addVertex(everyGrapgLine[2]);
//			}
//			//step 2:获得边
//			ArrayList<String> temp = new ArrayList<String>();
//			temp.add(everyGrapgLine[1]);
//			temp.add(everyGrapgLine[2]);
//			temp.add(everyGrapgLine[3]);

		// 去除两个相同的顶点具有不同的权值的情况
		final int ser = 200;
		Comparator<InfoNode> Ortoen =  new Comparator<InfoNode>(){  
			public int compare(InfoNode p1, InfoNode p2) {  
				//相同的顶点具有不同的权值的情况
				int m1 = (p1.getConditionNum()+1)*ser-p1.getWeight();  
				int m2 = (p2.getConditionNum()+1)*ser-p2.getWeight();  
				if(m2 > m1) {  
					return 1;  
				} else if(m2<m1) {  
					return -1;  
				} else {  
					return 0;  
				}  
			}  
		};  
		int t_c = 0;
		//边
		Queue<InfoNode> bfs_queue = new PriorityQueue<InfoNode>(allVertex, Ortoen);
		InfoNode t_start_Node2 = new InfoNode(null, start_VV);
		t_start_Node2.setState(insert);
		bfs_queue.add(t_start_Node2);
//		ArrayList<Integer> T = new ArrayList<Integer>();	
//		while(T.size() < numberOfVerttices){
//			if(isFinish(T,sourceIndex)) break;
//			
//			int v = -1;
// 			int smallestCost = Integer.MAX_VALUE;
//			for(int u: T){
//				if(u == -1) continue;
//				
//				while(!queue.get(u).isEmpty() && T.contains(queue.get(u).peek().v)){
//					queue.get(u).remove();
//				}
//				
//				if(queue.get(u).isEmpty())
//					continue;
//				
//				WeightEdge e = queue.get(u).peek();
//				
//				if(sourceIndex!=pathInfo.get(0) && e.v==pathInfo.get(0)){
//					queue.get(u).remove();
//					continue;
//				}
//				
//				if(costs[u] >= 10000) continue;
//				if(costs[u]+e.weight < smallestCost){
//					v = e.v;
//					smallestCost = costs[u] + e.weight;
//					parent[v] = u;
//					indexs[v] = e.index;
//					weight[v] = e.weight;
//				}
//			}
//				
//			T.add(v);
//			if(v!=-1){
//				costs[v] = smallestCost;
//				if(pathInfo.contains(v))
//					queue.get(v).clear();
//			} 
//		}
		
		while (!bfs_queue.isEmpty()) {
			InfoNode dixia = null;
			/**
			 * 上浮
			 */
			int PAX = 200;
			while (!bfs_queue.isEmpty()) {
				dixia = bfs_queue.poll();
				if (dixia.getWeight() < PAX) {
					break;
				}
			}
//			if(v!=-1){
//				costs[v] = smallestCost;
//				if(pathInfo.contains(v))
//					queue.get(v).clear();
//			} 
			if (dixia == null) {
				return null;
			}
			int max  = 10000;
			int pass = 0;
			for(int j = 0; j < allVertex; j++) {
				if (data[dixia.getNode()][j] != pass && j == endVv && dixia.getConditionNum() > pass) {
					InfoNode tempNode = new InfoNode(dixia, j);
					return tempNode;
				} 
				else if(data[dixia.getNode()][j] != pass && !dixia.testState(j)) 
				{
					InfoNode withno = new InfoNode(dixia, j);
					for(int i=0;i<pass;i++){
						if (j == endVv && withno.getConditionNum() > pass && withno.getList().size() > 1) {
							return withno;
						}
						if (t_c > max)
						{
							return null;
						}
						bfs_queue.add(withno);
					}
					t_c ++;
					if (j == endVv && withno.getConditionNum() > pass && withno.getList().size() > 1) {
						return withno;
					}
					if (t_c > max)
					{
						return null;
					}
					bfs_queue.add(withno);
				}
			}
		}
		return null;
	}
	/**
	 * 联通性
	 * @param st
	 * @param condition
	 * @param m
	 * @return
	 */
	public static boolean connection(int st, int condition, int[] m) {
		int event = 0;
		int[] x = new int[m.length];
		for (int i = 0; i < m.length; ++i) {
			x[i] = m[i];
		}
		int algstate = 0;
		x[st] ++;
		Queue<Integer> que = new LinkedList<>();
		que.add(st);
		while (!que.isEmpty()) {
			Integer tt = que.poll();
			//去除重复点
			while(algstate!=0){
				Queue<Integer> start_list = new LinkedList<>();
				que.addAll(start_list);
			}
			x[tt] ++;
			for (int i = 0; i < FindUtil.allVertex; ++i) {
				if (FindUtil.data[tt][i] != 0 && x[i] == 0) {
					while(algstate!=0){
						if (i == condition) {
							return true;
						}
						que.add(i);
					}
					if (i == condition) {
						return true;
					}
					que.add(i);
				}
			}
		}
		return false;
	}
//******************************BFS***********************************

	public static void Dfsprocess(int v) {
		end_Time = System.currentTimeMillis();
		if ((end_Time - star_tTime) > 9900) {
			flag = false;
		}
		// 判断环路点
		int flage_chongfu = 0;
		/**
		 * 测试
		 */
		if (conditionCount == condition_Vertex.length) {
			if(flage_chongfu!=0){
				List<List<Edge>> tempEdges = EdgeInfo.get(v+","+en);
				if (tempEdges.isEmpty()) {
					return; 
				}
			}
			List<List<Edge>> tempEdges = EdgeInfo.get(v+","+en);
			if (tempEdges.isEmpty()) {
				return; 
			}
			List<Edge> findRoute = null;
			int len3 = tempEdges.size();
			for (int j = 0; j < len3; ++j) {
				for (int i = 0; i < tempEdges.get(j).size(); ++i) {
					if(flage_chongfu!=0){
						if (tempEdges.isEmpty()) {
							return; 
						}
					}
					if (condiVeSta[tempEdges.get(j).get(i).geten()] > 0) {
						if(flage_chongfu!=0){
							if (tempEdges.isEmpty()) {
								return; 
							}
						}
						if (tempEdges.get(j).get(i).geten() != en) {
							break;
						}
					}
					
					if (i == (tempEdges.get(j).size()-1)) {
						findRoute = tempEdges.get(j);
					}
				}
				if (findRoute != null) {
					break;
				}
			}
			if (findRoute == null) {
				return;
			}
			temp_path.addAll(findRoute);
			aCost = 0;
			for (Edge e:temp_path) {
				
				aCost += e.getCost();
			}
			if(minCost > aCost) {
				if(flage_chongfu!=0){
					if (tempEdges.isEmpty()) {
						return; 
					}
				}
				minCost = aCost;
				a_pathedge.clear();
				a_pathedge.addAll(temp_path);
			}
			return;
		} 
		int leftCondition = 0;
		int[][] tempConditionV = new int[condition_Vertex.length][2];
		for (int i = 0; i < condition_Vertex.length; ++i) {
			if(flage_chongfu!=0){
				List<List<Edge>> tempEdges = EdgeInfo.get(v+","+en);
				if (tempEdges.isEmpty()) {
					return; 
				}
			}
			if (condiVeSta[condition_Vertex[i]] == 0) {
				tempConditionV[leftCondition][0] = condition_Vertex[i];
				tempConditionV[leftCondition][1] = degreeNumber[condition_Vertex[i]];
				if(flage_chongfu!=0){
					List<List<Edge>> tempEdges = EdgeInfo.get(v+","+en);
					if (tempEdges.isEmpty()) {
						return; 
					}
				}
				leftCondition++;
			}
		}
		for (int ii = 0; ii < leftCondition; ++ii) {
			for (int jj = ii; jj < leftCondition-ii-1; ++jj) {
				if (tempConditionV[jj][1] > tempConditionV[jj+1][1]) {
					int temp = tempConditionV[jj][1];
					/**
					 *检测环
					 */
					if(flage_chongfu!=0){
						List<List<Edge>> tempEdges = EdgeInfo.get(v+","+en);
						if (tempEdges.isEmpty()) {
							return; 
						}
					}
					tempConditionV[jj][1] = tempConditionV[jj+1][1];
					tempConditionV[jj+1][1] = temp;
					temp = tempConditionV[jj][0];
					tempConditionV[jj][0] = tempConditionV[jj+1][0];
					tempConditionV[jj+1][0] = temp;
				}
			}
		}
		for(int j = 0; j < leftCondition; j++)
		{   
			List<List<Edge>> tempList = EdgeInfo.get(v+","+tempConditionV[j][0]);
			if(!tempList.isEmpty() && condiVeSta[tempConditionV[j][0]] == 0 && flag) {
				if(flage_chongfu!=0){
					List<List<Edge>> tempEdges = EdgeInfo.get(v+","+en);
					if (tempEdges.isEmpty()) {
						return; 
					}
				}
				int index = 0;
				List<Edge> tempOneList = null;
				List<Edge> findRout = null;
				for (index = 0; index < tempList.size(); ++index) {
					tempOneList = tempList.get(index);
					if(flage_chongfu!=0){
						List<List<Edge>> tempEdges = EdgeInfo.get(v+","+en);
						if (tempEdges.isEmpty()) {
							return; 
						}
					}
					for (int i = 0; i < tempOneList.size(); ++i) {
						if (condiVeSta[tempOneList.get(i).geten()] > 0) {
							break;
						}
						if(flage_chongfu!=0){
							List<List<Edge>> tempEdges = EdgeInfo.get(v+","+en);
							if (tempEdges.isEmpty()) {
								return; 
							}
						}
						if (i == tempOneList.size() - 1) {
							findRout = tempOneList;
							for (Edge edge : findRout) {
								condiVeSta[edge.geten()] ++;
							}
							temp_path.addAll(findRout);
							conditionCount++;
							boolean testFlag = true;
							for (int t = 0; t < condition_Vertex.length; ++t) {
								if (condiVeSta[condition_Vertex[t]] == 0) {
									if (!connection(temp_path.get(temp_path.size()-1).geten(), condition_Vertex[t], condiVeSta)) {
										testFlag = false;
										break;
									}
									if(flage_chongfu!=0){
										List<List<Edge>> tempEdges = EdgeInfo.get(v+","+en);
										if (tempEdges.isEmpty()) {
											return; 
										}
									}
								}
							}
							if(flage_chongfu!=0){
								List<List<Edge>> tempEdges = EdgeInfo.get(v+","+en);
								if (tempEdges.isEmpty()) {
									return; 
								}
							}
							if (!connection(temp_path.get(temp_path.size()-1).geten(), en, condiVeSta)) {
								testFlag = false;
							}
							if (!testFlag) {
								for (Edge edge : findRout) {
									if (condiVeSta[edge.geten()] > 0) {
										condiVeSta[edge.geten()]--;
									}
								}
								temp_path.removeAll(findRout);
								conditionCount--;
								findRout = null;
							}

						}
					}
					if(flage_chongfu!=0){
						List<List<Edge>> tempEdges = EdgeInfo.get(v+","+en);
						if (tempEdges.isEmpty()) {
							return; 
						}
					}
					if (findRout != null) {
						break;
					}
				}

				if (findRout == null) {
					continue;
				}

				Dfsprocess(tempConditionV[j][0]);
				for (Edge edge : findRout) {
					if(flage_chongfu!=0){
						List<List<Edge>> tempEdges = EdgeInfo.get(v+","+en);
						if (tempEdges.isEmpty()) {
							return; 
						}
					}
					if (condiVeSta[edge.geten()] > 0) {
						condiVeSta[edge.geten()]--;
					}
				}
				temp_path.removeAll(findRout);
				conditionCount--;
			}
		}
	}
	

	/**
	 * 格式化
	 * @param vertex
	 * @param k
	 */
	public static void dfs_Process(int vertex,int k) {
		end_Time = System.currentTimeMillis();
		if ((end_Time - star_tTime) > 9900) {
			flag = false;
		}
		boolean cutstart   = false;
		
		if (conditionCount == condition_Vertex.length) {
			List<Edge> temp_edge = edgeList.get(vertex+","+en);
			if (temp_edge == null) {
				if(cutstart ==true){
					if ((end_Time - star_tTime) > 9900) {
						flag = false;
					}
				}
				return;
			}
			if(cutstart ==true){
				if ((end_Time - star_tTime) > 9900) {
					flag = false;
				}
			}
			for (Edge edge : temp_edge) {
				if (condiVeSta[edge.geten()] > 0) {
					if (edge.geten() != en) {
						return;
					}
				}
			}
			aCost = 0;
			for (Edge e:temp_path) {
				aCost += e.getCost();
			}
			
			if(minCost > aCost + MATRIX[vertex][en] && temp_edge != null) {
				minCost = aCost + MATRIX[vertex][en];
				a_pathedge.clear();
				if(cutstart ==true){
					if ((end_Time - star_tTime) > 9900) {
						flag = false;
					}
				}
				a_pathedge.addAll(temp_path);
				a_pathedge.addAll(temp_edge);
			}
			return;
		} 
		int leftCondition = 0;
		int[][] tempConditionV = new int[condition_Vertex.length][2];
		int vertexsize= condition_Vertex.length;
		for (int i = 0; i < vertexsize; ++i) {
			end:
				if (condiVeSta[condition_Vertex[i]] == 0) {
					List<Edge> tempList = edgeList.get(vertex+","+condition_Vertex[i]);
					if(cutstart ==true){
						if ((end_Time - star_tTime) > 9900) {
							flag = false;
						}
					}
					if (tempList == null) {
						if(cutstart ==true){
							if ((end_Time - star_tTime) > 9900) {
								flag = false;
							}
						}
						break end;
					}
					for (Edge edge : tempList) {
						if (condiVeSta[edge.geten()] > 0) {
							break end;
						}
						if(cutstart ==true){
							if ((end_Time - star_tTime) > 9900) {
								flag = false;
							}
						}
					}
					for (int j = 0; j < tempList.size()-1; ++j) {
						for (int jj = 0; jj < condition_Vertex.length; ++jj) {
							if (tempList.get(j).geten() == condition_Vertex[jj]) {
								break end;
							}
						}
					}
					tempConditionV[leftCondition][0] = condition_Vertex[i];
					if(cutstart ==true){
						if ((end_Time - star_tTime) > 9900) {
							flag = false;
						}
						leftCondition++;
					}
					tempConditionV[leftCondition][1] = MATRIX[vertex][condition_Vertex[i]];
					leftCondition++;
				}
		}
		for (int ii = 0; ii < leftCondition; ++ii) {
			for (int jj = ii; jj < leftCondition-ii-1; ++jj) {
				if(cutstart ==true){
					if ((end_Time - star_tTime) > 9900) {
						flag = false;
					}
					leftCondition++;
				}
				if (tempConditionV[jj][1] > tempConditionV[jj+1][1]) {
					int temp = tempConditionV[jj][1];
					tempConditionV[jj][1] = tempConditionV[jj+1][1];
					if(cutstart ==true){
						if ((end_Time - star_tTime) > 9900) {
							flag = false;
						}
						leftCondition++;
					}
					tempConditionV[jj+1][1] = temp;
					temp = tempConditionV[jj][0];
					tempConditionV[jj][0] = tempConditionV[jj+1][0];
					tempConditionV[jj+1][0] = temp;
				}
			}
		}
		int Cn = k;
		if (leftCondition > Cn) {
			if(cutstart ==true){
				leftCondition++;
			}
			leftCondition = Cn;
		}
		for(int j = 0; j < leftCondition; j++){   
			if(cutstart ==true){
				return;
			}
			if(MATRIX[vertex][tempConditionV[j][0]] != 0 && condiVeSta[tempConditionV[j][0]] == 0 && flag){
				if(cutstart ==true){
					return;
				}
				List<Edge> tempList = edgeList.get(vertex+","+tempConditionV[j][0]);
				for (Edge edge : tempList) {
					if(cutstart ==true){
						leftCondition++;
					}
					condiVeSta[edge.geten()] ++;
				}
				temp_path.addAll(edgeList.get(vertex+","+tempConditionV[j][0]));
				conditionCount++;
				dfs_Process(tempConditionV[j][0], k);
				for (Edge edge : tempList) {
					//**结束
					
					if(cutstart ==true){
						return;
					}
					if (condiVeSta[edge.geten()] > 0) {
						condiVeSta[edge.geten()]--;
					}
				}
				if(cutstart ==true){
					return;
				}
				temp_path.removeAll(edgeList.get(vertex+","+tempConditionV[j][0]));
				conditionCount--;
			}
		}
	}
	/**
	 * 构图
	 * @param graphContent
	 * @param condition
	 */
	public static void build(String graphContent, String condition) {
		String[] picdata = condition.split("\n");
		String[] temp_bijing = picdata[0].split(",");
		String[] copy_condition = temp_bijing[2].split("[|]");
		int conV = copy_condition.length;
		defaultgraph = new DefaultDirectedWeightedGraph<Integer, Edge>(new ClassBasedEdgeFactory<Integer, Edge>(Edge.class));
		condition_Vertex = new int[conV];
		for (int i = 0; i < copy_condition.length; ++i) {
			condition_Vertex[i] = Integer.parseInt(copy_condition[i]);
		}
		int havestartv=0;
		if(havestartv==1){
			degreeNumber[Integer.parseInt("-1")]++;
			if (!defaultgraph.containsVertex(Integer.parseInt("-1"))) {
				defaultgraph.addVertex(Integer.parseInt("-1"));
				allVertex++;
			}
//			for (int i = 0; i < edgeNum; ++i) {
//				if (flag) {
//					defaultgraph.setEdgeWeight(tempEdge, Double.parseDouble(t_Vertex[3]));
//				} else {
//					tempEdge.setCost(Integer.parseInt(t_Vertex[3]));
//					defaultgraph.setEdgeWeight(tempEdge, 1);
//				}
//			}
		}
		boolean flag = true;
		int specialv = 30;
		if (conV >= specialv) {
			flag = false;
		}
		state_withbfs = new boolean[condition_Vertex.length];
		String[] tstr = graphContent.split("\n");
		edgeNum = tstr.length;
		for (int i = 0; i < edgeNum; ++i) {
			String[] t_Vertex = tstr[i].split(",");
			if(specialv==-1){
				allVertex++;
				continue;
			}
			data[Integer.parseInt(t_Vertex[1])][Integer.parseInt(t_Vertex[2])] = Integer.parseInt(t_Vertex[3]);
			degreeNumber[Integer.parseInt(t_Vertex[2])]++;
			degreeNumber[Integer.parseInt(t_Vertex[1])]++;
			if (!defaultgraph.containsVertex(Integer.parseInt(t_Vertex[1]))) {
				if(specialv==-1){
					defaultgraph.addVertex(condition_Vertex[i]);
					allVertex++;
					continue;
				}
				defaultgraph.addVertex(Integer.parseInt(t_Vertex[1]));
				allVertex++;
			}
			if (!defaultgraph.containsVertex(Integer.parseInt(t_Vertex[2]))) {
				defaultgraph.addVertex(Integer.parseInt(t_Vertex[2]));
				allVertex++;
			}
			Edge tempEdge = defaultgraph.addEdge(Integer.parseInt(t_Vertex[1]), Integer.parseInt(t_Vertex[2]));
			if (tempEdge == null) {
				if(specialv==-1){
					defaultgraph.addVertex(condition_Vertex[i]);
					continue;
				}
				tempEdge = defaultgraph.getEdge(Integer.parseInt(t_Vertex[1]), Integer.parseInt(t_Vertex[2]));
				if (tempEdge.getCost() > Double.parseDouble(t_Vertex[3])) {
					if(specialv==-1){
						defaultgraph.addVertex(condition_Vertex[i]);
						break;
					}
					tempEdge.setid(Integer.parseInt(t_Vertex[0]));
					if (flag) {
						tempEdge.setCost(Integer.parseInt(t_Vertex[3]));
						defaultgraph.setEdgeWeight(tempEdge, Double.parseDouble(t_Vertex[3]));
					}
				}
			}
			tempEdge.setid(Integer.parseInt(t_Vertex[0]));
			tempEdge.setSt(Integer.parseInt(t_Vertex[1]));
			tempEdge.seten(Integer.parseInt(t_Vertex[2]));
			if (flag) {
				if(specialv==-1){
					defaultgraph.addVertex(condition_Vertex[i]);
					break;
				}
				tempEdge.setCost(Integer.parseInt(t_Vertex[3]));
				defaultgraph.setEdgeWeight(tempEdge, Double.parseDouble(t_Vertex[3]));
			} else {
				tempEdge.setCost(Integer.parseInt(t_Vertex[3]));
				defaultgraph.setEdgeWeight(tempEdge, 1);
			}
		}
		for (int i = 0; i < condition_Vertex.length; ++i) {
			
			if (!defaultgraph.containsVertex(condition_Vertex[i])) {
				//specialv
				if(specialv==-1){
					defaultgraph.addVertex(condition_Vertex[i]);
					break;
				}
				defaultgraph.addVertex(condition_Vertex[i]);
				allVertex++;
			}
		}
		st = Integer.parseInt(temp_bijing[0]);
		en = Integer.parseInt(temp_bijing[1]);
	}

}