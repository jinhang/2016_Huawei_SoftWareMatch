package com.routesearch.route;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.DirectedNeighborIndex;
import org.jgrapht.experimental.permutation.IntegerPermutationIter;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.DepthFirstIterator;


/**
 * 机智的搜索吧！！
 * @author jinhang
 *
 */
public final class Route
{
	public static List<String> allMidNodes;
	public static String src;
	public static String dest;
	public static List<String> vistednode=new ArrayList<String>();
	public static String searchRoute(String graphContent, String condition,String graphFilePath,String conditionFilePath) throws Exception
	{

		/**
		 * 对graphContent 和condition进行分割获得所需要的：
		 * （1）节点集合
		 * （2）边的集合
		 * （3）边的权值
		 * （4）边的LinkID
		 */
		DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> directedGraph = new DefaultDirectedWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class);

		String[] allGraphLine = graphContent.split("\\n");  //所有的行
		HashSet<String> allNode = new HashSet<String>();  //存储节点
		Map<Integer, ArrayList<String>> allEdge = new HashMap<Integer, ArrayList<String>>();  //存储Edge、weight和LinkID，使用LinkID做key，[edge,weight]做value

		for(int i = 0; i < allGraphLine.length; i++)
		{
			// step 1: 创建节点
			String[] everyGrapgLine = allGraphLine[i].split(",");
			if(! allNode.contains(everyGrapgLine[1]))
			{
				allNode.add(everyGrapgLine[1]);
				directedGraph.addVertex(everyGrapgLine[1]);
			}
			if(! allNode.contains(everyGrapgLine[2]))
			{
				allNode.add(everyGrapgLine[2]);
				directedGraph.addVertex(everyGrapgLine[2]);
			}
			//step 2:获得边
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(everyGrapgLine[1]);
			temp.add(everyGrapgLine[2]);
			temp.add(everyGrapgLine[3]);

			// 去除两个相同的顶点具有不同的权值的情况
			boolean addOr = true;
			Set<Map.Entry<Integer, ArrayList<String>>> entryseSet =allEdge.entrySet();  
			for (Entry<Integer, ArrayList<String>> entry : entryseSet) 
			{
				String oldV1 = entry.getValue().get(0);
				String oldV2 = entry.getValue().get(1);
				String newV1 = everyGrapgLine[1];
				String newV2 = everyGrapgLine[2];
				if(oldV1.equals(newV1) && oldV2.equals(newV2))
				{
					//取小的作为权值
					int nowValue = Integer.parseInt(everyGrapgLine[3]);
					int oldValue = Integer.parseInt(entry.getValue().get(2));
					if(nowValue < oldValue)
					{
						//删除原来的记录
						allEdge.remove(entry.getKey());
						break;
					}
					else
					{
						//不加入这条记录
						addOr = false;
						break;
					}
				}
			}
			if(addOr)
			{
				allEdge.put(Integer.parseInt(everyGrapgLine[0]), temp);
			}
		}

		DefaultWeightedEdge[] allEdgePair = new DefaultWeightedEdge[allGraphLine.length];  //所有的边对象
		Iterator<Integer> it = allEdge.keySet().iterator();    
		int start = 0;
		while(it.hasNext()){    
			String tempkey;    
			List<String> tempValue;    
			tempkey = it.next().toString();    
			tempValue = allEdge.get(Integer.parseInt(tempkey));   
			//添加边
			allEdgePair[start] = directedGraph.addEdge(tempValue.get(0), tempValue.get(1));
			//添加权值
			directedGraph.setEdgeWeight(allEdgePair[start], Integer.parseInt(tempValue.get(2)));
			start += 1;       
		} 



		/***************************寻找最短路径的算法***********************************************/
		String[] arr =condition.split(",");
		src = arr[0];
		dest = arr[1];
		String[] midNodes = arr[2].substring(0, arr[2].length()-1).split("\\|");
		allMidNodes = new ArrayList<String>();
		for(String a:midNodes)
		{
			allMidNodes.add(a);
		}
		int[] midNodesArray = new int[allMidNodes.size()];
		for(int i = 0; i < midNodesArray.length; i++){
			midNodesArray[i] = Integer.parseInt(midNodes[i]);
		}
		//无中间节点
		if(allMidNodes.size() == 0)
		{
			//直接用Dijkstra算法
			List answer = DijkstraShortestPath.findPathBetween(directedGraph, src, dest);
			if(answer == null){
				return "NA";
			}else{
				//格式化输出
				ArrayList<String> returnAnswer = new ArrayList<String>();
				for(int i = 0; i < answer.size(); i++)
				{
					String tempAnswer = answer.get(i).toString();
					String vertex1 = tempAnswer.split(":")[0].substring(1).trim();
					String vertex2 = tempAnswer.split(":")[1].substring(0, tempAnswer.split(":")[1].length()-1).trim();
					//格式化答案
					Iterator<Integer> edgeIt = allEdge.keySet().iterator();
					while(edgeIt.hasNext())
					{
						String tempkey;    
						List<String> tempValue;    
						tempkey = edgeIt.next().toString();    
						tempValue = allEdge.get(Integer.parseInt(tempkey));
						if(vertex1.equals(tempValue.get(0)) && vertex2.equals(tempValue.get(1)))
						{
							returnAnswer.add(tempkey);
							break;
						}
					}
				}
				String sT1 = "";
				for(String s:returnAnswer)
				{
					sT1 += s;
					sT1 += "|";
				}
				return sT1.substring(0, sT1.length()-1);
			}

		}
		//top 1-2
		else if(allMidNodes.size()>0&&allMidNodes.size()<=3){
			String End_Answer = Solution_1(src,dest,allMidNodes,directedGraph,allEdge,midNodesArray);
			return End_Answer;
		}
		// 3 4
		else if(allMidNodes.size()>3&&allMidNodes.size()<9){
			String End_Answer = Solution_5(src,dest,allMidNodes,directedGraph,allEdge,midNodesArray);
			return End_Answer;

		}
		//top 5
		else if(allMidNodes.size()==9){
			ArrayList<Integer> resultStr =SolutionWithAnt.search_route(graphContent,graphFilePath, conditionFilePath);
			ArrayList<String> result = new ArrayList<String>();
			for(Integer a: resultStr){
				result.add(String.valueOf(a));
			}
			return format(result, allEdge);
		}
		// 6 7 8
		else if(allMidNodes.size()>=10&&allMidNodes.size()<=15){
			NewDFSUtil readData = null;
			try {
				readData = new NewDFSUtil(graphContent,condition);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//获取相对最优路径
			String rString = readData.getMinWeightPath();	
			return rString;
		}
		// 10 15  其实都是 17
		else if(allMidNodes.size()>=16&&allMidNodes.size()<=17){
			NewDFSUtil readData = null;
			try {
				readData = new NewDFSUtil(graphContent,condition);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//获取相对最优路径
			String rString = readData.getMinWeightPath();	
			return rString;
		}
		// 9
		else if(allMidNodes.size()>=18&&allMidNodes.size()<=20){
			NewDFSUtil readData = null;
			try {
				readData = new NewDFSUtil(graphContent,condition);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//获取相对最优路径
			String rString = readData.getMinWeightPath();	
			return rString;
		}

		// 12
		else if(allMidNodes.size()>=21&&allMidNodes.size()<=22){
			String End_Answer = null;
			try {
				End_Answer = Solution_6(graphFilePath,conditionFilePath,10000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return End_Answer;
		}

		// 13
		else if(allMidNodes.size()>=23&&allMidNodes.size()<=25){
			String End_Answer = null;
			try {
				End_Answer = Solution_6(graphFilePath,conditionFilePath,9000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return End_Answer;
		}
		//top14
		else if(allMidNodes.size()>=26&&allMidNodes.size()<=30){
			//System.out.println("开始");
//			ArrayList<Integer> resultStr =SolutionWithAnt.search_route(graphContent,graphFilePath, conditionFilePath);
//			ArrayList<String> result = new ArrayList<String>();
//			for(Integer a: resultStr){
//				result.add(String.valueOf(a));
//			}
//			return format(result, allEdge);
			//return GetAnswer.pathFind(graphContent, condition);
			return FindUtil.find(graphContent, condition);
		}
		//top11
		else if(allMidNodes.size()>30){
			String End_Answer = Solution_7(graphContent,condition);
			return End_Answer;
		}
		
		return "NA";
	}
	private static String Solution_7(String graphContent, String condition) throws FileNotFoundException {
		ReadData a= new ReadData(graphContent, condition);
		return a.getMinWeightPath();
	}
	private static String Solution_6(String graphFilePath,String conditionFilePath,long time) throws Exception {

		DFSUtil readData  = new DFSUtil(graphFilePath,conditionFilePath,time);
		String rString = readData.getMinWeightPath();
		//System.out.println("**path***"+rString);
		return rString;
	}
	/**
	 * 中间节点大于某个数 只要有路就返回
	 * @param src
	 * @param dest
	 * @param allMidNodes
	 * @param directedGraph
	 * @param allEdge
	 * @param midNodesArray
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String Solution_4(String src,String dest,List<String> allMidNodes,
			DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> directedGraph,Map<Integer, ArrayList<String>> allEdge,int[] midNodesArray) {
		if(Connected(allMidNodes,directedGraph)){
			Map<Integer, List> A_min=null;
			Map<Integer, List> B_min=null;
			Map<Integer, List> C_min=null;
			Map<Integer, List> ANWSER=null;
			int perlen = allMidNodes.size();
			IntegerPermutationIter ipi = new IntegerPermutationIter(midNodesArray);
			int[] curPer = null;
			while(ipi.hasNext()){
				curPer = ipi.getNext();
				A_min =getAdjPathAndCost(src,String.valueOf(curPer[0]),directedGraph);

				C_min =getAdjPathAndCost(String.valueOf(curPer[perlen-1]),dest,directedGraph);

				int[] a = curPer;
				B_min = FindMidPermutaionsPath(a,directedGraph);

				if(B_min==null){

					continue;
				}
				ANWSER = CombinePath(A_min,B_min,C_min);
				if(ANWSER!=null){
					break;
				}

			}
			for(Map.Entry<Integer, List> a:ANWSER.entrySet()){
				return formatResult(a.getValue(),allEdge);
			}
		}
		return "NA";
	}
	/**
	 * 分治暴力法
	 * @param src
	 * @param dest
	 * @param allMidNodes
	 * @param directedGraph
	 * @param allEdge
	 * @param midNodesArray
	 * @return
	 */
	private static String Solution_1(String src,String dest,List<String> allMidNodes,
			DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> directedGraph,Map<Integer, ArrayList<String>> allEdge,int[] midNodesArray) {

		if(Connected(allMidNodes,directedGraph)){
			//System.out.println("中间节点的连通");
			//2. 获取中间节点序列全排列  (可能太多)
			IntegerPermutationIter ipi = new IntegerPermutationIter(midNodesArray);
			Map<Integer, List> A_min=null;
			Map<Integer, List> B_min=null;
			Map<Integer, List> C_min=null;
			Map<Integer, List> ANWSER=null;
			List<Map<Integer, List>> All_ANWSER = new ArrayList<Map<Integer, List>>();
			int perlen = allMidNodes.size();
			int[] curPer = null;
			while(ipi.hasNext()){

				curPer = ipi.getNext();
				A_min =getAdjPathAndCost1(src,String.valueOf(curPer[0]),directedGraph);
				C_min =getAdjPathAndCost1(String.valueOf(curPer[perlen-1]),dest,directedGraph);
				int[] a = curPer;

				B_min = FindMidPermutaionsPath1(a,directedGraph);
				ANWSER = CombinePath(A_min,B_min,C_min);
				All_ANWSER.add(ANWSER);
			}
			//6.全局筛选
			List<Map<Integer, List>> nullArr = new ArrayList<Map<Integer, List>>();
			nullArr.add(null);
			All_ANWSER.removeAll(nullArr);
			if(All_ANWSER.size()!=0){	
				List answer = null;
				answer = minCost(All_ANWSER);
				//格式化输出
				return formatResult(answer,allEdge);
			}else{
				return "NA";
			}
		}
		else
		{
			return "NA";
		}
	}
	private static Map<Integer, List> getAdjPathAndCost1(String src, String dest,DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> directedGraph){
		List A = new ArrayList<String>();
		A.add(src);
		A.add(dest);

		Map<Integer, List> result_test = pathNode1(A,directedGraph);

		return result_test;
	}
	private static Map<Integer, List> FindMidPermutaionsPath1(int[] permutations,DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> directedGraph) {
		Map<Integer, List> e = new HashMap<Integer, List>();
		Map<Integer, List> B_min = new HashMap<Integer, List>();;
		String src;
		String dest;
		List<Map<Integer, List>> cost_path_List=new ArrayList<Map<Integer, List>>();


		int len = permutations.length;
		for(int i=0;i<len-1;i++){
			src = String.valueOf(permutations[i]);
			dest = String.valueOf(permutations[i+1]);
			e = getAdjPathAndCost(src,dest,directedGraph);
			cost_path_List.add(e);
		}
		if(cost_path_List.size()==1){
			return cost_path_List.get(0);
		}
		Map<Integer, List> an = new HashMap<Integer, List>();
		int cost_an= 0;
		List path_an = new ArrayList<String>();
		List path_tmp = new ArrayList<String>();
		for(int i = 0 ;i<cost_path_List.size();i++){
			//合并size个 map

			for (Map.Entry<Integer, List> entry : cost_path_List.get(i).entrySet()) {  

				// System.out.println("cost = " + entry.getKey() + ", path = " + entry.getValue());  
				cost_an += entry.getKey();
				for(int j =0; j< entry.getValue().size();j++){
					path_an.add(entry.getValue().get(j));
				}
			}  	
		}
		Iterator<DefaultWeightedEdge> it=path_an.iterator();  
		while(it.hasNext()){  

			DefaultWeightedEdge a=it.next();  
			if(path_tmp.contains(a)){  
				return null;  
			}  
			else{  
				path_tmp.add(a);  
			}  
		}
		//判断环
		if(isLoop(path_tmp)){
			return null;
		}else{
			an.put(cost_an, path_tmp);
			return an;
		}
	}
	/**

	 * 剪枝排列数 有点儿问题（）
	 * @param src
	 * @param dest
	 * @param allMidNodes
	 * @param directedGraph
	 * @param allEdge
	 * @param midNodesArray
	 * @return
	 */
	private static String Solution_3(String src, String dest, List<String> allMidNodes,
			DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> directedGraph,Map<Integer, ArrayList<String>> allEdge, int[] midNodesArray) {

		IntegerPermutationIter ipi = new IntegerPermutationIter(midNodesArray);
		List PATH_LIST=null; 
		List visted = new ArrayList<String>();
		double possible = 0.6;
		int steps= allMidNodes.size();

		Map<Integer, List> A_min=null;
		Map<Integer, List> B_min=null;
		Map<Integer, List> C_min=null;
		Map<Integer, List> ANWSER=null;
		List<Map<Integer, List>> All_ANWSER = new ArrayList<Map<Integer, List>>();
		List<Map<Integer, List>> A_ANWSER = new ArrayList<Map<Integer, List>>();
		List<Map<Integer, List>> B_ANWSER = new ArrayList<Map<Integer, List>>();
		int perlen = allMidNodes.size();
		int[] curPer = null;
		int flag=0;
		//小平均+小
		if(flag == 0){
			for(int i=0;i<perlen;i++){
				A_min =getAdjPathAndCost(src,String.valueOf(midNodesArray[i]),directedGraph);

				A_ANWSER.add(A_min);
			}
			List<List> a_min = meanCost(A_ANWSER);
			for(int i = 0;i< a_min.size();i++){
				PATH_LIST = new ArrayList<DefaultWeightedEdge>();
				for(Object a: a_min.get(i)){
					PATH_LIST.add(a);
				}
				int v1_node_len=a_min.get(i).get(a_min.get(i).size()-1).toString().trim().split(":")[1].trim().length();
				String v1= a_min.get(i).get(a_min.get(i).size()-1).toString().trim().split(":")[1].trim().substring(0, v1_node_len-1);
				A_ANWSER=null;
				B_ANWSER=null;
				B_min = null;
				A_min=null;
				allMidNodes.remove(v1);
				List<Map<Integer, List>> test =null;
				int count=allMidNodes.size();
				String node=v1;
				String node_str="";
				List test1;
				for(int k=0;k<count;k++){
					test = new ArrayList<Map<Integer, List>>();
					for(int j=0;j<allMidNodes.size();j++){

						B_min =getAdjPathAndCost(node,String.valueOf(allMidNodes.get(j)),directedGraph);

						if(B_min!=null)
							test.add(B_min);

					}
					test1 = minCost(test);
					if(test1==null) {continue;}
					for(Object a: test1){


						PATH_LIST.add(a);



					}
					test =null;
					//
					int node_len=test1.get(test1.size()-1).toString().trim().split(":")[1].trim().length();
					node_str=test1.get(test1.size()-1).toString().trim().split(":")[1].trim().substring(0, node_len-1);
					//visted.add(node_str);
					allMidNodes.remove(node_str);
					node=node_str;
				}
				Map<Integer, List> end_ege= getAdjPathAndCost(node,dest,directedGraph);
				for(Map.Entry<Integer, List> a:end_ege.entrySet()){
					for(Object e:a.getValue()){
						PATH_LIST.add(e);
					}
				}
				if(isLoop(PATH_LIST)!=true){

					return formatResult(PATH_LIST, allEdge);
				}else{
					flag =1;
				}

				PATH_LIST = null;
				for(int a: midNodesArray){
					allMidNodes.add(String.valueOf(a));
				}
			}
		}
		A_ANWSER =null;
		A_min=null;
		//贪心失败  大平均 +大
		if(flag == 1){
			A_ANWSER = new ArrayList<Map<Integer, List>>();
			for(int i=0;i<perlen;i++){
				A_min =getAdjPathAndCost(src,String.valueOf(midNodesArray[i]),directedGraph);
				A_ANWSER.add(A_min);
			}
			List<List> a_min = maxmeanCost(A_ANWSER);
			for(int i = 0;i< a_min.size();i++){
				PATH_LIST = new ArrayList<DefaultWeightedEdge>();
				for(Object a: a_min.get(i)){
					PATH_LIST.add(a);
				}
				int v1_node_len=a_min.get(i).get(a_min.get(i).size()-1).toString().trim().split(":")[1].trim().length();
				String v1= a_min.get(i).get(a_min.get(i).size()-1).toString().trim().split(":")[1].trim().substring(0, v1_node_len-1);
				A_ANWSER=null;
				B_ANWSER=null;
				B_min = null;
				A_min=null;
				allMidNodes.remove(v1);
				List<Map<Integer, List>> test =null;
				int count=allMidNodes.size();
				String node=v1;
				String node_str="";
				List test1;
				for(int k=0;k<count;k++){
					test = new ArrayList<Map<Integer, List>>();
					for(int j=0;j<allMidNodes.size();j++){

						B_min =getAdjPathAndCost(node,String.valueOf(allMidNodes.get(j)),directedGraph);
						if(B_min!=null)
							test.add(B_min);
					}
					test1 = maxCost(test);
					if(test1==null) {continue;}
					for(Object a: test1){
						PATH_LIST.add(a);
					}

					test =null;
					int node_len=test1.get(test1.size()-1).toString().trim().split(":")[1].trim().length();
					node_str=test1.get(test1.size()-1).toString().trim().split(":")[1].trim().substring(0, node_len-1);
					allMidNodes.remove(node_str);
					node=node_str;
				}
				Map<Integer, List> end_ege= getAdjPathAndCost(node,dest,directedGraph);
				for(Map.Entry<Integer, List> a:end_ege.entrySet()){
					for(Object e:a.getValue()){
						PATH_LIST.add(e);
					}
				}
				if(isLoop(PATH_LIST)!=true){
					return formatResult(PATH_LIST, allEdge);
				}else{
					flag =3;
				}
				PATH_LIST = null;
				for(int a: midNodesArray){
					allMidNodes.add(String.valueOf(a));
				}
			}
		}
		//小 随机
		A_ANWSER = null;
		A_min =null;
		if(flag==3){
			A_ANWSER = new ArrayList<Map<Integer, List>>();
			for(int i=0;i<perlen;i++){
				A_min =getAdjPathAndCost(src,String.valueOf(midNodesArray[i]),directedGraph);
				A_ANWSER.add(A_min);
			}
			List a_min = minCost(A_ANWSER);
			for(int i = 0;i< a_min.size();i++){
				PATH_LIST = new ArrayList<DefaultWeightedEdge>();
				for(Object a: a_min){
					PATH_LIST.add(a);
				}
				int v1_node_len=a_min.get(a_min.size()-1).toString().trim().split(":")[1].trim().length();
				String v1= a_min.get(a_min.size()-1).toString().trim().split(":")[1].trim().substring(0, v1_node_len-1);
				A_ANWSER=null;
				B_ANWSER=null;
				B_min = null;
				A_min=null;
				allMidNodes.remove(v1);
				List<Map<Integer, List>> test =null;
				int count=allMidNodes.size();
				String node=v1;
				String node_str="";
				List test1;
				for(int k=0;k<count;k++){
					test = new ArrayList<Map<Integer, List>>();
					for(int j=0;j<allMidNodes.size();j++){
						B_min =getAdjPathAndCost(node,String.valueOf(allMidNodes.get(j)),directedGraph);
						if(B_min!=null)
							test.add(B_min);
					}

					test1 = propossibleCost(test);
					if(test==null) {continue;}
					if(test1==null) {continue;}
					for(Object a: test1){
						PATH_LIST.add(a);
					}
					test =null;
					int node_len=test1.get(test1.size()-1).toString().trim().split(":")[1].trim().length();
					node_str=test1.get(test1.size()-1).toString().trim().split(":")[1].trim().substring(0, node_len-1);
					allMidNodes.remove(node_str);
					node=node_str;
				}
				Map<Integer, List> end_ege= getAdjPathAndCost(node,dest,directedGraph);
				for(Map.Entry<Integer, List> a:end_ege.entrySet()){
					for(Object e:a.getValue()){
						PATH_LIST.add(e);
					}
				}
				if(isLoop(PATH_LIST)!=true){
					return formatResult(PATH_LIST, allEdge);
				}else{
					flag =4;
				}
				PATH_LIST = null;
				for(int a: midNodesArray){
					allMidNodes.add(String.valueOf(a));
				}
			}

		}
		//大+ 随机
		A_ANWSER = null;
		A_min =null;
		if(flag==3){
			A_ANWSER = new ArrayList<Map<Integer, List>>();
			for(int i=0;i<perlen;i++){
				A_min =getAdjPathAndCost(src,String.valueOf(midNodesArray[i]),directedGraph);
				A_ANWSER.add(A_min);
			}
			List a_min = maxCost(A_ANWSER);
			for(int i = 0;i< a_min.size();i++){
				PATH_LIST = new ArrayList<DefaultWeightedEdge>();
				for(Object a: a_min){
					PATH_LIST.add(a);
				}
				int v1_node_len=a_min.get(a_min.size()-1).toString().trim().split(":")[1].trim().length();
				String v1= a_min.get(a_min.size()-1).toString().trim().split(":")[1].trim().substring(0, v1_node_len-1);
				A_ANWSER=null;
				B_ANWSER=null;
				B_min = null;
				A_min=null;
				allMidNodes.remove(v1);
				List<Map<Integer, List>> test =null;
				int count=allMidNodes.size();
				String node=v1;
				String node_str="";
				List test1;
				for(int k=0;k<count;k++){
					test = new ArrayList<Map<Integer, List>>();
					for(int j=0;j<allMidNodes.size();j++){
						B_min =getAdjPathAndCost(node,String.valueOf(allMidNodes.get(j)),directedGraph);
						if(B_min!=null)
							test.add(B_min);
					}
					test1 = propossibleCost(test);
					for(Object a: test1){
						PATH_LIST.add(a);
					}
					test =null;
					int node_len=test1.get(test1.size()-1).toString().trim().split(":")[1].trim().length();
					node_str=test1.get(test1.size()-1).toString().trim().split(":")[1].trim().substring(0, node_len-1);
					allMidNodes.remove(node_str);
					node=node_str;
				}
				Map<Integer, List> end_ege= getAdjPathAndCost(node,dest,directedGraph);
				for(Map.Entry<Integer, List> a:end_ege.entrySet()){
					for(Object e:a.getValue()){
						PATH_LIST.add(e);
					}
				}
				if(isLoop(PATH_LIST)!=true){
					return formatResult(PATH_LIST, allEdge);
				}else{
					flag =4;
				}
				PATH_LIST = null;
				for(int a: midNodesArray){
					allMidNodes.add(String.valueOf(a));
				}
			}

		}

		A_ANWSER = null;
		A_min =null;

		if(PATH_LIST==null){
			do{
				PATH_LIST = new ArrayList<DefaultWeightedEdge>();
				A_ANWSER = new ArrayList<Map<Integer, List>>();
				for(int i=0;i<perlen;i++){
					A_min =getAdjPathAndCost(src,String.valueOf(midNodesArray[i]),directedGraph);
					A_ANWSER.add(A_min);
				}

				List a_min = propossibleCost(A_ANWSER);
				for(int i = 0;i< 1;i++){

					for(Object a: a_min){
						PATH_LIST.add(a);
					}
					int v1_node_len=a_min.get(a_min.size()-1).toString().trim().split(":")[1].trim().length();
					String v1= a_min.get(a_min.size()-1).toString().trim().split(":")[1].trim().substring(0, v1_node_len-1);
					A_ANWSER=null;
					B_ANWSER=null;
					B_min = null;
					A_min=null;
					allMidNodes.remove(v1);
					List<Map<Integer, List>> test =null;
					int count=allMidNodes.size();
					String node=v1;
					String node_str="";
					List test1;
					for(int k=0;k<count;k++){
						test = new ArrayList<Map<Integer, List>>();
						for(int j=0;j<allMidNodes.size();j++){
							B_min =getAdjPathAndCost(node,String.valueOf(allMidNodes.get(j)),directedGraph);
							if(B_min!=null)
								test.add(B_min);
						}
						test1 = propossibleCost(test);
						for(Object a: test1){
							PATH_LIST.add(a);
						}
						if(isLoop(PATH_LIST)){
							continue;
						}
						test =null;
						int node_len=test1.get(test1.size()-1).toString().trim().split(":")[1].trim().length();
						node_str=test1.get(test1.size()-1).toString().trim().split(":")[1].trim().substring(0, node_len-1);
						allMidNodes.remove(node_str);
						node=node_str;
					}
					Map<Integer, List> end_ege= getAdjPathAndCost(node,dest,directedGraph);
					for(Map.Entry<Integer, List> a:end_ege.entrySet()){
						for(Object e:a.getValue()){
							PATH_LIST.add(e);
						}
					}
					allMidNodes.clear();
					for(int c =0;c<midNodesArray.length;c++){
						allMidNodes.add(String.valueOf(midNodesArray[c]));
					}

				}
				steps++;
				if(steps>1000){
					break;
				}
			}while(isLoop(PATH_LIST)); 
			return formatResult(PATH_LIST, allEdge);
		}
		return "NA";

	}
	private static int[] shuffle(int[]s) {
		Random  random = new Random();

		Set<Integer> set = new LinkedHashSet<Integer>();

		// redistribute the  index
		while(true){
			int t =random.nextInt(s.length);
			set.add(t);
			if(set.size()== s.length)
				break;
		}

		int [] out = new int[s.length];

		int count = 0;

		for(Iterator<Integer> iterator = set.iterator(); iterator.hasNext();){
			out[count] = s[iterator.next()];
			count++;
		}

		return out;

	}
	/**
	 * 求出点周围 未被访问点 《=权重 的路径
	 * @param directedGraph
	 * @param start
	 * @return
	 */
	private static List<List> DFS(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> directedGraph,String start) {

		List<Map<Integer, List>> tmp = new ArrayList<Map<Integer, List>>();
		Map<Integer, List> min=null;
		int perlen = allMidNodes.size();
		DirectedNeighborIndex neighborIndex= new DirectedNeighborIndex<String, DefaultWeightedEdge>(directedGraph);
		for(int i=0;i<vistednode.size()-1;i++){
			if(vistednode.get(i).equals(start)){
				return null;
			}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      

		}

		for(int i=1;i<vistednode.size();i++){
			if(vistednode.get(i).equals(start)==false){
				vistednode.add(start);
			}

		}

		Set neighboNodeset = neighborIndex.successorsOf(start);

		List<String>neighboNodeList = new ArrayList<String>(neighboNodeset);

		ConnectivityInspector  a= new ConnectivityInspector<String,DefaultWeightedEdge>(directedGraph);

		//System.out.println("%%%%%%%%%%%%%%%"+a.pathExists("18","19")); // 有路但是没有 直接连
		//System.out.println("%%%%%%%%%%%%%%%"+neighborIndex.); // 有路但是没有 直接连

		//		BellmanFordShortestPath.findPathBetween(directedGraph, "7", "13",1);
		//		for(int k  = 0;k<neighboNodeList.size();k++){
		//
		//			for(int j = 0;j<vistednode.size();j++){
		//				if(neighboNodeList.get(k).equals(vistednode.get(j))){
		//					neighboNodeList.remove(vistednode.get(j));
		//				}
		//			}
		//		}
		for(int i=0;i<neighboNodeList.size();i++){
			if(a.pathExists(start,neighboNodeList.get(i))==false){
				neighboNodeList.remove(i);
			}
		}
		//System.out.println(neighboNodeList);
		for(int i=0;i<neighboNodeList.size();i++){
			if(allMidNodes.contains(neighboNodeList.get(i))){
				min =getAdjPathAndCost(start,neighboNodeList.get(i),directedGraph);
				tmp.add(min);
				break;
			}
			min =getAdjPathAndCost(start,neighboNodeList.get(i),directedGraph);
			tmp.add(min);
		}

		List<List> search = meanCost(tmp);

		int v1_node_len=search.get(0).toString().trim().split(":")[1].trim().length();
		String v1= search.get(0).toString().trim().split(":")[1].trim().substring(0, v1_node_len-2);
		vistednode.add(v1);

		for(int i=1;i<vistednode.size();i++){
			if(vistednode.get(i).equals(v1)==false){
				vistednode.add(v1);
			}

		}
		for ( int i = 0 ; i < vistednode.size() - 1 ; i ++ ) {  
			for ( int j = vistednode.size() - 1 ; j > i; j -- ) {  
				if (vistednode.get(j).equals(vistednode.get(i))) {  
					vistednode.remove(j);  
				}   
			}   
		}

		return search.get(0);
	}


	/**
	 *  选出 路径 经过v‘就行的路 有点儿慢
	 * @param src
	 * @param dest
	 * @param allMidNodes
	 * @param directedGraph
	 * @param allEdge
	 * @param midNodesArray
	 * @return
	 */
	private static String Solution_5(String src,String dest,List<String> allMidNodes,
			DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> directedGraph,Map<Integer, ArrayList<String>> allEdge,int[] midNodesArray) {

		List<String> path = AFUtil.havaAllPath(src, dest, directedGraph, allMidNodes);
		//System.out.println(path);

		return format(path, allEdge);



	}
	public static String format(List<String> toInsert,Map<Integer, ArrayList<String>> allEdge) {
		String answer = "";
		// 遍历toInsert
		ArrayList<DefaultWeightedEdge> edges = new ArrayList<DefaultWeightedEdge>();
		for (int i = 0; i < toInsert.size() - 1; i++) {
			String src = String.valueOf(toInsert.get(i));
			String des = String.valueOf(toInsert.get(i+1));
			for (int t : allEdge.keySet()) {
				String compareSrc = allEdge.get(t).get(0);
				String compareDes = allEdge.get(t).get(1);
				if (src.equals(compareSrc) && des.equals(compareDes)) {
					answer += String.valueOf(t);
					answer += "|";
					break;
				}
			}
		}
		return answer.substring(0, answer.length() - 1);
	}


	private static List maxmeanCost(List<Map<Integer, List>> all_ANWSER) {
		Map<Integer, List> re=new TreeMap<Integer, List>();
		int mean =0;
		List<List>meanList = new ArrayList<List>();
		for(Map<Integer, List> a:all_ANWSER){
			if(a!=null){
				for(Map.Entry<Integer, List> s : a.entrySet()){
					re.put(s.getKey(), s.getValue());
					mean += s.getKey();
				}

			}
		}
		mean = (int)mean / re.size();

		List obj = null;

		for (Map.Entry<Integer, List> s :re.entrySet()) {
			obj = s.getValue();

			if (obj != null) {
				if(s.getKey()>mean){
					meanList.add(s.getValue());
				}
			}
		}
		return meanList;
	}

	private static String formatResult(List answer,Map<Integer, ArrayList<String>> allEdge) {
		ArrayList<String> returnAnswer = new ArrayList<String>();
		for(int i = 0; i < answer.size(); i++)
		{
			//					DefaultWeightedEdge tempEdge = (DefaultWeightedEdge) answer.get(i);
			String tempAnswer = answer.get(i).toString();
			String vertex1 = tempAnswer.split(":")[0].substring(1).trim();
			String vertex2 = tempAnswer.split(":")[1].substring(0, tempAnswer.split(":")[1].length()-1).trim();
			//格式化答案
			Iterator<Integer> edgeIt = allEdge.keySet().iterator();
			while(edgeIt.hasNext())
			{
				String tempkey;    
				List<String> tempValue;    
				tempkey = edgeIt.next().toString();    
				tempValue = allEdge.get(Integer.parseInt(tempkey));
				//			    	     System.out.println(tempValue.get(0));
				//			    	     System.out.println(tempValue.get(1));
				//			    	     System.out.println(vertex1.equals(tempValue.get(0)));
				//			    	     System.out.println(vertex2.equals(tempValue.get(1)));
				if(vertex1.equals(tempValue.get(0)) && vertex2.equals(tempValue.get(1)))
				{
					returnAnswer.add(tempkey);
					break;
				}
			}
		}
		String sT = "";
		for(String s:returnAnswer)
		{
			sT += s;
			sT += "|";
		}
		return sT.substring(0, sT.length()-1);
	}
	private static List propossibleCost(List<Map<Integer, List>> all_ANWSER) {
		Map<Integer, List> re=new TreeMap<Integer, List>();
		int mean =0;
		if(all_ANWSER.size()==0){
			return null;
		}
		List<List>meanList = new ArrayList<List>();
		for(Map<Integer, List> a:all_ANWSER){
			if(a!=null){
				for(Map.Entry<Integer, List> s : a.entrySet()){
					re.put(s.getKey(), s.getValue());
					mean += s.getKey();
				}
			}
		}
		mean = (int)mean / re.size();
		List obj = null;
		int rand = (int)(1+Math.random()*(re.size()-1+1));

		int index = 0;
		for (Map.Entry<Integer, List> s :re.entrySet()) {
			obj = s.getValue();
			++index;
			if (rand==index&&obj != null) {
				break;
			}
		}

		return obj;
	}
	private static List meanCost(List<Map<Integer, List>> all_ANWSER) {
		Map<Integer, List> re=new TreeMap<Integer, List>();
		int mean =0;
		List<List>meanList = new ArrayList<List>();
		for(Map<Integer, List> a:all_ANWSER){
			if(a!=null){
				for(Map.Entry<Integer, List> s : a.entrySet()){
					re.put(s.getKey(), s.getValue());
					mean += s.getKey();
				}

			}
		}
		mean = (int)mean / re.size();
		List obj = null;

		for (Map.Entry<Integer, List> s :re.entrySet()) {
			obj = s.getValue();

			if (obj != null) {

				if(s.getKey() <=mean){
					meanList.add(s.getValue());

				}
			}
		}
		return meanList;

	}
	private static Map sortCost(List<Map<Integer, List>> all_ANWSER) {
		Map<Integer, List> re=new TreeMap<Integer, List>();
		for(Map<Integer, List> a:all_ANWSER){
			if(a!=null){
				for(Map.Entry<Integer, List> s : a.entrySet()){
					re.put(s.getKey(), s.getValue());
				}

			}
		}

		return re;

	}
	private static List minCost(List<Map<Integer, List>> all_ANWSER) {
		Map<Integer, List> re=new TreeMap<Integer, List>();

		for(Map<Integer, List> a:all_ANWSER){
			if(a!=null){
				for(Map.Entry<Integer, List> s : a.entrySet()){
					re.put(s.getKey(), s.getValue());
				}

			}
		}
		List<DefaultWeightedEdge> obj = null;
		for (Map.Entry<Integer, List> s :re.entrySet()) {
			obj = s.getValue();
			if (obj != null) {
				break;
			}
		}
		return obj;

	}
	private static List maxCost(List<Map<Integer, List>> all_ANWSER) {
		Map<Integer, List> re=new TreeMap<Integer, List>();
		for(Map<Integer, List> a:all_ANWSER){
			if(a!=null){
				for(Map.Entry<Integer, List> s : a.entrySet()){
					re.put(s.getKey(), s.getValue());
				}

			}
		}
		int index =0;
		List obj = null;
		for (Map.Entry<Integer, List> s :re.entrySet()) {
			obj = s.getValue();

			index++;
			if(index==re.size()-1&&obj != null)
			{
				break;
			}
		}
		return obj;

	}
	private static List midCost(List<Map<Integer, List>> all_ANWSER) {
		Map<Integer, List> re=new TreeMap<Integer, List>();
		for(Map<Integer, List> a:all_ANWSER){
			if(a!=null){
				for(Map.Entry<Integer, List> s : a.entrySet()){
					re.put(s.getKey(), s.getValue());
				}

			}
		}
		int index =0;
		List obj = null;
		for (Map.Entry<Integer, List> s :re.entrySet()) {
			obj = s.getValue();

			index++;
			if(index==((re.size())/2)&&obj != null)
			{
				break;
			}
		}
		return obj;

	}
	private static Integer getMinKey(List<Map<Integer, List>> list) {
		Integer min = (Integer) list.get(0).keySet().toArray()[0];
		for(int i=1;i<list.size();i++){
			Integer a = (Integer) list.get(0).keySet().toArray()[0];
			if(a < min){
				min = a;
			}
		}
		return min;
	}

	private static List<String> havePermutationsEnd(String[] permutations) {
		List<String> re = new ArrayList<>();
		for(int i = 0;i< permutations.length;i++){
			re.add(String.valueOf(permutations[i].charAt(permutations[i].length()-1)));
		}		
		return re;
	}

	private static List<String> havePermutationsFirst(String[] permutations) {
		List<String> re = new ArrayList<>();
		for(int i = 0;i< permutations.length;i++){
			re.add(String.valueOf(permutations[i].charAt(0)));
		}		
		return re;
	}
	public static List <String> somenodes = new ArrayList<String>();
	private static Map<Integer, List>  FindMidPermutaionsPath(int[] permutations,DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> directedGraph) {
		Map<Integer, List> e = new HashMap<Integer, List>();
		Map<Integer, List> B_min = new HashMap<Integer, List>();;
		String src;
		String dest;
		List<Map<Integer, List>> cost_path_List=new ArrayList<Map<Integer, List>>();


		int len = permutations.length;

		for(int i=0;i<len-1;i++){
			src = String.valueOf(permutations[i]);
			dest = String.valueOf(permutations[i+1]);
			e = getAdjPathAndCost(src,dest,directedGraph);
			if(e==null){
				return null;
			}
			for(Map.Entry<Integer, List> a : e.entrySet()){
				if(a.getValue().size()!=1){
					for(int n = 1;n<=a.getValue().size()-1;n++){
						int first_node_len=a.getValue().get(n).toString().trim().split(":")[0].trim().length();
						String fisrt_node_str=a.getValue().get(n).toString().trim().split(":")[0].trim().substring(1, first_node_len);

						if(somenodes.contains(fisrt_node_str)){
							return null;
						}
						else{
							somenodes.add(fisrt_node_str);

						}
					}
				}
			}
			//如果找到的e 中 有前面路径中的点 返回null
			cost_path_List.add(e);
		}
		//清除 cost_path_List的null
		List<Map<Integer, List>> nullArr = new ArrayList<Map<Integer, List>>();
		nullArr.add(null);
		cost_path_List.removeAll(nullArr);

		if(cost_path_List.size()==1){
			return cost_path_List.get(0);
		}
		Map<Integer, List> an = new HashMap<Integer, List>();
		int cost_an= 0;
		List path_an = new ArrayList<String>();
		List path_tmp = new ArrayList<String>();
		for(int i = 0 ;i<cost_path_List.size();i++){
			//合并size个 map
			for (Map.Entry<Integer, List> entry : cost_path_List.get(i).entrySet()) {  
				cost_an += entry.getKey();
				for(int j =0; j< entry.getValue().size();j++){
					path_an.add(entry.getValue().get(j));
				}
			}  	
		}
		//判断环
		if(isLoop(path_an)){
			return null;
		}else{
			an.put(cost_an, path_an);
			return an;
		}
	}

	private static Map<Integer, List>  CombinePath(Map<Integer, List> a_min, Map<Integer, List> b_min, Map<Integer, List> c_min) {
		Map<Integer, List> an = new HashMap<Integer, List>();
		int cost_an= 0;
		List path_an = new ArrayList<String>();
		List path_tmp = new ArrayList<String>();
		if(a_min!=null&&b_min!=null&&c_min!=null){
			for (Map.Entry<Integer, List> entry : a_min.entrySet()) {  

				// System.out.println("cost = " + entry.getKey() + ", path = " + entry.getValue());  
				cost_an += entry.getKey();
				for(int i =0; i< entry.getValue().size();i++){
					path_an.add(entry.getValue().get(i));
				}


			}  
			for (Map.Entry<Integer, List> entry : b_min.entrySet()) {  

				//System.out.println("cost = " + entry.getKey() + ", path = " + entry.getValue());  
				cost_an += entry.getKey();
				for(int i =0; i< entry.getValue().size();i++){
					path_an.add(entry.getValue().get(i));
				}

			}

			for (Map.Entry<Integer, List> entry : c_min.entrySet()) {  

				// System.out.println("cost = " + entry.getKey() + ", path = " + entry.getValue());  
				cost_an += entry.getKey();
				//System.out.println(entry.getValue().get(0));
				for(int i =0; i< entry.getValue().size();i++){
					path_an.add(entry.getValue().get(i));
				}
			}
			Iterator<DefaultWeightedEdge> it=path_an.iterator();  
			while(it.hasNext()){  

				DefaultWeightedEdge a=it.next();  
				if(path_tmp.contains(a)){  
					return null;  
				}  
				else{  
					path_tmp.add(a);  
				}  
			}
			//判断环
			if(isLoop(path_tmp)){
				return null;
			}else{
				an.put(cost_an, path_tmp);
				return an;
			}

		}else{
			return null;
		}
	}
	private static List<String> edgeList2vertexsList(List path_tmp){
		List<String> vertexsList=new ArrayList<String>();
		if(path_tmp==null){
			return null;
		}
		Iterator<DefaultWeightedEdge> it1=path_tmp.iterator(); 
		while(it1.hasNext()){  
			DefaultWeightedEdge a=it1.next(); 

			String [] s_d = a.toString().split(":"); 
			s_d[0] = String.valueOf(s_d[0].trim().substring(1));
			s_d[1] = String.valueOf(s_d[1].trim().substring(0,s_d[1].trim().length()-1));
			vertexsList.add(s_d[0]);
			vertexsList.add(s_d[1]);
		}
		return vertexsList;
	}
	public static boolean isLoop(List path_tmp) {
		//构建图 复用！！！！！！！！！！！！
		DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> directedGraph = new DefaultDirectedWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Iterator<DefaultWeightedEdge> it=path_tmp.iterator();  
		List<String> v = new ArrayList<String>();
		while(it.hasNext()){  
			DefaultWeightedEdge a=it.next(); 

			String [] s_d = a.toString().split(":"); 
			s_d[0] = String.valueOf(s_d[0].trim().substring(1));
			s_d[1] = String.valueOf(s_d[1].trim().substring(0,s_d[1].trim().length()-1));
			v.add(s_d[0]);
			v.add(s_d[1]);


		}
		Set<String> v_set = new HashSet<String>(v);
		for(String a: v_set){
			directedGraph.addVertex(a);
		}

		Iterator<DefaultWeightedEdge> it1=path_tmp.iterator(); 
		while(it1.hasNext()){  
			DefaultWeightedEdge a=it1.next(); 

			String [] s_d = a.toString().split(":"); 
			s_d[0] = String.valueOf(s_d[0].trim().substring(1));
			s_d[1] = String.valueOf(s_d[1].trim().substring(0,s_d[1].trim().length()-1));
			directedGraph.addEdge(s_d[0], s_d[1]);

		}

		CycleDetector cycleDetector = new CycleDetector<String, DefaultWeightedEdge>(directedGraph);
		if(cycleDetector.detectCycles()){

			return true;
		}
		return false;
	}
	public static String transMapToString(Map map){  
		java.util.Map.Entry entry;  
		StringBuffer sb = new StringBuffer();  
		for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext();)  
		{  
			entry = (java.util.Map.Entry)iterator.next();  
			sb.append(entry.getKey().toString()).append( "|" ).append(null==entry.getValue()?"":  
				entry.getValue().toString()).append (iterator.hasNext() ? "^" : "");  
		}  
		return sb.toString();  
	}  

	/**
	 * 最优路径和Cost
	 * @param a
	 * @param b
	 * @return
	 */
	private static Map<Integer, List> minPathAndCost(Map<Integer, List> a,Map<Integer, List> b){

		int cost1 =0;
		int cost2= 0;
		List path1 = new ArrayList<String>();
		List path2 = new ArrayList<String>();
		for (Map.Entry<Integer, List> entry : a.entrySet()) {  

			// System.out.println("cost = " + entry.getKey() + ", path = " + entry.getValue());  
			cost1 = entry.getKey();
			path1 = entry.getValue();
		}  
		for (Map.Entry<Integer, List> entry : b.entrySet()) {  

			// System.out.println("cost = " + entry.getKey() + ", path = " + entry.getValue());  
			cost2 = entry.getKey();
			path2 = entry.getValue();
		} 
		int re_cost = Math.max(cost1, cost2);
		List re_path = new ArrayList<String>();
		if(re_cost == cost1){
			re_path = path1;
		}else{
			re_path = path2;
		}
		Map<Integer, List> result = new HashMap<Integer, List>();
		result.put(re_cost, re_path);
		return result;
	}

	/**
	 * 相邻节点的 路径和Cost
	 * @param src
	 * @param dest
	 * @param directedGraph
	 * @return
	 */
	private static Map<Integer, List> getAdjPathAndCost(String src, String dest,DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> directedGraph){
		List A = new ArrayList<String>();
		A.add(src);
		A.add(dest);

		Map<Integer, List> result_test = pathNode(A,directedGraph);

		return result_test;
	}
	private static Map<Integer,List> pathNode1(List<String> nodes,
			DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> directedGraph) {
		Map<Integer, List> map = new HashMap<Integer, List>();
		int dist=0;

		//直连
		//		if(Edge(nodes[0],nodes[1])== true){
		//			dist(Vi,Vi+1) = 1;//Cost
		//		}else{
		//			dist(Vi,Vi+1) = Dijstra(Vi,Vi+1);//Cost
		//		}
		List<DefaultWeightedEdge> re =  DijkstraShortestPath.findPathBetween(directedGraph, nodes.get(0),nodes.get(1));
		for(DefaultWeightedEdge e : re){
			dist += directedGraph.getEdgeWeight(e);
		}
		map.put(dist, re);
		return map;
	}
	private static Map<Integer,List> pathNode(List<String> nodes,
			DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> directedGraph) {
		Map<Integer, List> map = new HashMap<Integer, List>();
		int dist=0;
		List<DefaultWeightedEdge> re =  DijkstraShortestPath.findPathBetween(directedGraph, nodes.get(0),nodes.get(1));
		if(re !=null){
			List<String> vertexsList = edgeList2vertexsList(re);
			Set<String>vertexSet = new HashSet<String>(vertexsList);
			List<String> list = new ArrayList<String>(vertexSet);
			int vertexslist_len = list.size();
			for(int i =0;i<list.size();i++){
				if(list.get(i).equals(src)||list.get(i).equals(dest)){
					list.remove(list.get(i));
				}
			}
			for(int i =0;i<allMidNodes.size();i++){

				list.remove(allMidNodes.get(i));

			}

			if(list.size()>=0){
				if(re == null){
					return null;
				}
				for(DefaultWeightedEdge e : re){
					dist += directedGraph.getEdgeWeight(e);
				}
				map.put(dist, re);
				return map;
			}
		}

		return null;

	}

	private static boolean Connected(List<String> allMidNodes,
			DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> directedGraph) {
		/**
		 * 深度优先遍历
		 */
		Iterator<String> dfs = new DepthFirstIterator<String, DefaultWeightedEdge>(directedGraph);
		String actual = "";
		while (dfs.hasNext()) {
			String v = dfs.next();
			actual += v;
		}
		for(String a:allMidNodes){
			if(actual.contains(a)==false){
				return false;
			}
		}
		return true;
	}

	/**
	 * 全排列
	 * @param orginal list [2,3,11]
	 * @return
	 */
	private static List<Integer[]> permutation(List<String> orginal){
		List<Integer[]> re=null;
		LinkedList<Integer[]> a=null;
		int len=orginal.size();
		int[] arr=new int[len];
		if(len<=8){
			for(int i=0;i<len;i++){
				arr[i]=Integer.parseInt(orginal.get(i));
			}
			int n = len;
			Arrays.sort(arr);
			List<Integer> result=new LinkedList<Integer>();
			re = new LinkedList<Integer[]>();
			for(int s:arr){
				result.add(s);
			}
			while(lowPos(arr)!=-1){
				int i=lowPos(arr);
				int j=minInMaxThanPos(arr, i);
				swap(arr, i, j);
				reverseAfterI(arr, i);
				for(int s:arr){
					result.add(s);
				}
			}

			int count = 0;
			Integer[] e = new Integer[n];
			for(int i =0;i<result.size();i++){
				e[i%n]=result.get(i);
				count++;
				if(count%n==0){
					re.add(e);
					e = null;
					e = new Integer[n];
				}
			}   
			return re;
		}else{
			a = new LinkedList<Integer[]>();
			List <Integer>list =null;
			Integer[] s = new Integer[orginal.size()];

			for(int i=0;i<len;i++){
				s[i]=Integer.parseInt(orginal.get(i));
			}
			for(int k = 0;k<4000;k++){
				list = new ArrayList();  
				for(int i = 0;i < len;i++){  
					list.add(s[i]);  
				}
				s = null;
				s = new Integer[orginal.size()];
				Collections.shuffle(list);  
				for(int i = 0;i < len;i++){  
					s[i] = (Integer) list.get(i);  
				}
				a.add(s);
			}

			return a;
		}
	}

	private static int lowPos(int[] arr){

		int i;
		for(i=arr.length-1;i>=1;i--)
			if(arr[i-1]<arr[i])
				break;
		return i-1;

	}
	private static int minInMaxThanPos(int[] arr,int index){

		Hashtable< Integer,Integer> ht=new Hashtable< Integer,Integer>();
		ArrayList<Integer> tmp =new ArrayList<Integer>();
		int j=0;
		for(int i=index+1;i<arr.length;i++)
			if(arr[i]>arr[index])
			{
				ht.put(arr[i],i);
				tmp.add(arr[i]);

			}
		if( ht.isEmpty())
			return -1;

		Collections.sort(tmp);
		return  ht.get(tmp.get(0));

	}
	private static void swap(int[] arr,int i,int j){

		int tmp=arr[i];
		arr[i]=arr[j];
		arr[j]=tmp;
	}
	private static void reverseAfterI(int[] arr,int index){
		int j=arr.length-1;
		int len=arr.length-1-(index+1)+1;
		for(int i=index+1;i<=index+len/2;i++)
			swap(arr,i,j--);
	}
}
