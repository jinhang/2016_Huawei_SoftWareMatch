package com.routesearch.route;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class NewDFSUtil {
//	private double change = 0.5;//
	private double Change = 0;
	private int AVERAGE;//
	private int factor = 8;
	private int RememberV = -1;
	public static long startTime;
	public static long endTime;
	/*public static long start2Time;
	public static long end2Time;*/
	private int control = 0;
	private int flag =4 ;
	private long starStep = 0;
	private long endStep = 0;
/*	private int Size = 300;//
	private WeightEdge[][] tuRect = new WeightEdge[Size][Size];//
*/	private int[] costs;
	private boolean isPoint[];//
	private int WEIGHT = 0;
	int restNum = 0; 
	
	private double averWeight = 0;//
	private boolean isGo = false;//
	
	private ArrayList<String> PATHLIST = new ArrayList<String>(100);//
	
	private int shortest = 20000;//
	
	private Scanner tuIn; //
	private Scanner paIn;//
	
	private String graphContent;
	private String condition;
	
	//
	public ArrayList<PriorityQueue<WeightEdge>> queues = new  ArrayList<PriorityQueue<WeightEdge>>(100);
	//
	public ArrayList<Integer> pathInfo = new ArrayList<Integer>(50);
	
	//
	public ArrayList<ArrayList<EleEdge>> elePathInfo = new  ArrayList<ArrayList<EleEdge>>(50);
	
	public NewDFSUtil(String graphContent,String condition) throws FileNotFoundException{
		startTime=System.currentTimeMillis();
		
		this.graphContent = graphContent;
		this.condition = condition; //
		tuIn = new Scanner(graphContent);
		tuIn.useDelimiter("[,\n]");
		paIn = new Scanner(condition);
		paIn.useDelimiter("[,|]");
		
		//
		int[] edg = new int[4];
		int temp = -1; int count = 0;
		
		while(tuIn.hasNext()){
			count++;
			for(int i=0;i<4;i++){
				edg[i] = Integer.parseInt(tuIn.next().trim());
			}	
			
			if(queues.size()-1 < edg[1]){
				for(int i=queues.size();i<=edg[1];i++)
					queues.add(i,new PriorityQueue<WeightEdge>());
			}
				
			queues.get(edg[1]).add(new WeightEdge(edg[0],edg[1],edg[2],edg[3]));
		}
		//average weight of one line
		
		tuIn.close();
		
		int t = 0; int tem = 0;
		while(paIn.hasNext()){
			int x = Integer.parseInt(paIn.next().trim());
			
			if(t == 1) tem = x;
			else pathInfo.add(x);
			t++;
		}
		pathInfo.add(tem);
		paIn.close();
		
		isPoint = new boolean[queues.size()];
	}
	
	public String getMinWeightPath(){
		int len = pathInfo.size()-1;
		for(int i=0;i<len;i++){
				getTwoVSPath(pathInfo.get(i));
		}
	
		elePathInfo.add(new ArrayList<EleEdge>());

		Comparator<EleEdge> comparator = new Comparator<EleEdge>(){
			public int compare(EleEdge o1, EleEdge o2) {
				if(o1.weight>o2.weight)
					return 1;
				else if(o1.weight == o2.weight)
					return 0;
				else return -1;
			}
		};
		
		for (ArrayList<EleEdge> ele : elePathInfo) {
			Collections.sort(ele,comparator);
		}
		
		int v = getXB(pathInfo.get(0));
		ArrayList<Integer> searList = new ArrayList<Integer>();
		ArrayList<String> pathList = new ArrayList<String>();
		boolean[] isVisited = new boolean[pathInfo.size()];
		
		Change = Math.pow(pathInfo.size(),3);
		restNum = pathInfo.size();
		
		dfs(v, searList, isVisited,null,pathList);

		String result = "";
		if(shortest > 10000) result = "NA";
		else{
			for (String e : PATHLIST) {
				result += e;
			}
			result = result.substring(1);
		}
		
		return result;
	}
	
	private void getTwoVSPath(int sourceIndex) {
		
		ArrayList<Integer> T = new ArrayList<Integer>();
		T.add(sourceIndex);
		
		int numberOfVerttices = queues.size();
		int[] parent = new int[numberOfVerttices];
		int[] indexs = new int[numberOfVerttices];
		int[] weight = new int[numberOfVerttices];
		
		parent[sourceIndex] = -1;
		
		costs = new int[numberOfVerttices];
		for(int i=0;i<costs.length;i++)
			costs[i] = Integer.MAX_VALUE;
		costs[sourceIndex] = 0;
		
		ArrayList<PriorityQueue<WeightEdge>> queue = deepClone(this.queues);
		
		while(T.size() < numberOfVerttices){
			if(isFinish(T,sourceIndex)) break;
			
			int v = -1;
 			int smallestCost = Integer.MAX_VALUE;
			for(int u: T){
				if(u == -1) continue;
				
				while(!queue.get(u).isEmpty() && T.contains(queue.get(u).peek().v)){
					queue.get(u).remove();
				}
				
				if(queue.get(u).isEmpty())
					continue;
				
				WeightEdge e = queue.get(u).peek();
				
				if(sourceIndex!=pathInfo.get(0) && e.v==pathInfo.get(0)){
					queue.get(u).remove();
					continue;
				}
				
				if(costs[u] >= 10000) continue;
				if(costs[u]+e.weight < smallestCost){
					v = e.v;
					smallestCost = costs[u] + e.weight;
					parent[v] = u;
					indexs[v] = e.index;
					weight[v] = e.weight;
				}
			}
				
			T.add(v);
			if(v!=-1){
				costs[v] = smallestCost;
				if(pathInfo.contains(v))
					queue.get(v).clear();
			} 
		}
		
		ArrayList<EleEdge> prio = new ArrayList<EleEdge>();
		
		for(int x = 0;x<pathInfo.size();x++){
			int t = pathInfo.get(x);
			if(t != sourceIndex && costs[t] < 10000){
				getXuLie(prio,t,parent,indexs,sourceIndex);
			}
		}
		elePathInfo.add(prio);
	}

	private int getXB(int a){
		return pathInfo.indexOf(a);
	}
	private void getXuLie(ArrayList<EleEdge> prio,int  v,int[] parent,int[] roadIDs,int sour) {
		int p = v;
		ArrayList<Integer> points = new ArrayList<Integer>();
		ArrayList<Integer> indexSS = new ArrayList<Integer>();
		
		while(p != sour){
			if(p!=v) points.add(0,p);
			indexSS.add(0,roadIDs[p]);
			p = parent[p];
		}
		prio.add(new EleEdge(0,getXB(sour),getXB(v),costs[v],indexSS,points));
	}

	private boolean isFinish(ArrayList<Integer> T,int sour) {
		if(sour == pathInfo.get(0)){
			for (Integer e : pathInfo) {
				if(!T.contains(e)) return false;
			}
			return true;
		}
		else{
			for (Integer e : pathInfo) {
				if(e!=pathInfo.get(0) && !T.contains(e)) return false;
			}
			return true;
		}
	}
	
	private void dfs(int v,ArrayList<Integer> searList,boolean[] isVisited,EleEdge eledge,ArrayList<String> pathList){
		if(isGo) return;//return digui
		
		endStep++;//
		endTime = System.currentTimeMillis();
		
		if(endTime-startTime > 9000){
			isGo = true;
		}
		
		if(eledge != null){
			for(Integer ele : eledge.passPoints){
				isPoint[ele] = true;
			}	
			pathList.add(eledge.getIndexS());
		}
		searList.add(v);
		isVisited[v] = true;

		int r = 0;
		ArrayList<EleEdge> tempt = elePathInfo.get(v);//llllllllllllll
		int size = tempt.size();
		
		boolean is = true;
		for (boolean e : isVisited) {
			if(!e) is = false;
		}
		
		for(int i=0;i<size;i++){
			
			EleEdge temp = tempt.get(i);
				 r = temp.v;
				if(!isVisited[r]){
					boolean con = false;
					for(Integer ele : temp.passPoints){
						if(isPoint[ele]) con = true;
					}
					if(con) continue;
					
					
					WEIGHT += temp.weight;
					if(WEIGHT >shortest){ 
						WEIGHT -= temp.weight;
						continue;
					}
				
					dfs(r,searList, isVisited,temp,pathList);
					if(isGo) return;
				}
				if(isGo) return;
				
				
		}
		if(is && v == pathInfo.size()-1){
			//end2Time = System.currentTimeMillis();
			starStep = endStep = 0;
			if(WEIGHT < shortest) {
				shortest = WEIGHT;
				PATHLIST = (ArrayList<String>) pathList.clone();
			}
		}
		if(eledge != null){
			for(Integer ele : eledge.passPoints){
				isPoint[ele] = false;
			}
		}
			isVisited[v] = false;
			searList.remove(searList.size()-1);
			if(eledge != null){
				WEIGHT -= eledge.weight;		pathList.remove(searList.size()-1);
			}
	}
	private ArrayList<PriorityQueue<WeightEdge>> deepClone(ArrayList<PriorityQueue<WeightEdge>> queues){
		ArrayList<PriorityQueue<WeightEdge>> copied = new ArrayList<PriorityQueue<WeightEdge>>();
		
		for(int i=0;i<queues.size();i++){
			copied.add(new PriorityQueue<WeightEdge>());
			for(WeightEdge e : queues.get(i))
				copied.get(i).add(e);
		}
		
		return copied;
	}
	
	
	class PathInfo{
		public int[] index;
		public int weight;
		
		public PathInfo(int[] index,int weight){
			this.index = index;
			this.weight = weight;
		}
	}
}


class NewWeightEdge implements Comparable<WeightEdge>{
	
	public int u;
	public int v;
	public int index;
	public int weight;
	
	public NewWeightEdge(){}
	
	public NewWeightEdge(int index,int u,int v,int weight){
		this.index = index;
		this.u = u;
		this.v = v;
		this.weight = weight;
	}
	
	public NewWeightEdge(int index,int weight){
		this.index = index;
		this.weight = weight;
	}
	@Override
	public int compareTo(WeightEdge o) {
		// TODO Auto-generated method stub
		if(weight > o.weight) return 1;
		else if(weight == o.weight)
			return 0;
		else 
			return -1;
	}
}

class NewEleEdge extends WeightEdge{

	public ArrayList<Integer> xulie;
	public ArrayList<Integer> passPoints;
	
	public NewEleEdge(){}
	public NewEleEdge(int index,int u,int v,int weight,ArrayList<Integer> xulie,ArrayList<Integer> passPoints){
		super(index,u,v,weight);
		
		this.xulie = xulie;
		this.passPoints = passPoints;
	}
	
	public int compareTo(EleEdge o) {
		return super.compareTo(o);
	}
	
	public String getIndexS(){
		String s = "";
		
		for (Integer ele : xulie) {
			s = s+"|"+ele;
		}
		return s;
	}
}

class NewGoodLine extends EleEdge{
	public ArrayList<Integer> points = new ArrayList<Integer>();
	public NewGoodLine(){
		this.xulie = new ArrayList<Integer>();
	}
	
}
