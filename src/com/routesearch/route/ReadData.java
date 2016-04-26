package com.routesearch.route;
import java.awt.List;
import java.awt.geom.PathIterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.parsers.FactoryConfigurationError;

public class ReadData {
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
	
	private double averWeight = 0;//
	private boolean isGo = false;//
	
	private ArrayList<String> PATHLIST = new ArrayList<String>(100);//
	
	private int shortest = 20000;//
	
	private Scanner tuIn; //
	private Scanner paIn;//
	
	private String graphContent;
	private String condition;
	
	//
	public ArrayList<PriorityQueue<WeightEdge1>> queues = new  ArrayList<PriorityQueue<WeightEdge1>>(100);
	//
	public ArrayList<Integer> pathInfo = new ArrayList<Integer>(50);
	
	//
	public ArrayList<ArrayList<EleEdge1>> elePathInfo = new  ArrayList<ArrayList<EleEdge1>>(50);
	
	public ReadData(String graphContent,String condition) throws FileNotFoundException{
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
			edg[3] = 1;
			averWeight += edg[3];
			
			if(queues.size()-1 < edg[1]){
				for(int i=queues.size();i<=edg[1];i++)
					queues.add(i,new PriorityQueue<WeightEdge1>());
			}
				
			queues.get(edg[1]).add(new WeightEdge1(edg[0],edg[1],edg[2],edg[3]));
		}
		//average weight of one line
		
		averWeight = averWeight/count;
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
	
		elePathInfo.add(new ArrayList<EleEdge1>());
		
		int NUM = 0;
		for(ArrayList<EleEdge1> ele : elePathInfo){
			for (EleEdge1 e : ele) {
				AVERAGE += e.weight;
				NUM++;
			}
		}
		AVERAGE = AVERAGE/NUM;
		
		Comparator<EleEdge1> comparator = new Comparator<EleEdge1>(){
			public int compare(EleEdge1 o1, EleEdge1 o2) {
				if(o1.weight>o2.weight)
					return 1;
				else if(o1.weight == o2.weight)
					return 0;
				else return -1;
			}
		};
		
		for (ArrayList<EleEdge1> ele : elePathInfo) {
			Collections.sort(ele,comparator);
		}
		
		int v = getXB(pathInfo.get(0));
		ArrayList<Integer> searList = new ArrayList<Integer>();
		ArrayList<String> pathList = new ArrayList<String>();
		boolean[] isVisited = new boolean[pathInfo.size()];
		
		Change = Math.pow(pathInfo.size(),3);
		
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
		
		ArrayList<PriorityQueue<WeightEdge1>> queue = deepClone(this.queues);
		
		while(T.size() < numberOfVerttices){
			if(isFinish(T,sourceIndex)) break;
			
			int v = -1;
 			int smallestCost = 100000;
			for(int u: T){
				if(u == -1) continue;
				
				while(!queue.get(u).isEmpty() && T.contains(queue.get(u).peek().v)){
					queue.get(u).remove();
				}
				
				if(queue.get(u).isEmpty())
					continue;
				
				WeightEdge1 e = queue.get(u).peek();
				
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
					findGoodLine(parent,v,sourceIndex,indexs,weight,queue,T);
			} 
		}
		
		ArrayList<EleEdge1> prio = new ArrayList<EleEdge1>();
		
		for(int x = 0;x<pathInfo.size();x++){
			int t = pathInfo.get(x);
			if(t != sourceIndex && costs[t] < 10000){
				getXuLie(prio,t,parent,indexs,sourceIndex);
			}
		}
		elePathInfo.add(prio);
	}

	private void findGoodLine(int[] parent,int v,int sour,int[] indexs,int[] weight,ArrayList<PriorityQueue<WeightEdge1>> queue,
			ArrayList<Integer> T){
		int p = v;
		if(parent[v] == sour) return;
		
		GoodLine1 goodLine = new GoodLine1();
		
		goodLine.points.add(sour);
		goodLine.u = sour; goodLine.v = v;
		
		while(v != sour){	
			goodLine.weight += weight[v];
			goodLine.xulie.add(indexs[v]);
			
			if(pathInfo.contains(v)){
				goodLine.points.add(v);
			}
			v = parent[v];
		}
		
		double aver = goodLine.weight/goodLine.xulie.size();
		if(goodLine.xulie.size() > 50 && aver > averWeight){///zhong yao
			costs[p] = 10000;return;
		}
		
		if(goodLine.points.size() < 3)return;
		else{
			ArrayList<Integer> t = goodLine.points;
			costs[p] = 10000;
			if(!queue.get(parent[p]).isEmpty() && T.contains(queue.get(parent[p]).peek().v))
				queue.get(parent[p]).remove();
			T.remove(new Integer(p));
		}
	}

	private int getXB(int a){
		return pathInfo.indexOf(a);
	}
	private void getXuLie(ArrayList<EleEdge1> prio,int  v,int[] parent,int[] roadIDs,int sour) {
		int p = v;
		ArrayList<Integer> points = new ArrayList<Integer>();
		ArrayList<Integer> indexSS = new ArrayList<Integer>();
		
		while(p != sour){
			if(p!=v) points.add(0,p);
			indexSS.add(0,roadIDs[p]);
			p = parent[p];
		}
		prio.add(new EleEdge1(0,getXB(sour),getXB(v),costs[v],indexSS,points));
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
	
	private void dfs(int v,ArrayList<Integer> searList,boolean[] isVisited,EleEdge1 eledge,ArrayList<String> pathList){
		if(isGo) return;//return digui
		
		endStep++;//
		endTime = System.currentTimeMillis();
		
		if(endTime-startTime > 9000){
			isGo = true;
		}
		
		/*if(endStep-starStep > Change){
			factor = pathInfo.size() - searList.size();
			//System.out.println(endStep+"	"+starStep+" "+(endStep-starStep));
			starStep = endStep = 0;
			
			if(flag < pathInfo.size()/4)flag++; 
			int vStep = 0;
			if(RememberV != -1) vStep = searList.size()-searList.indexOf(RememberV);
			
			control = flag+vStep;
			//System.out.println("hui tui"+flag+"  "+factor+"	"+pathInfo.size()+"	"+searList.size());
		}*/
		
		if(eledge != null){
			for(Integer ele : eledge.passPoints){
				isPoint[ele] = true;
			}	
			pathList.add(eledge.getIndexS());
		}
		searList.add(v);
		isVisited[v] = true;

		int r = 0;
		ArrayList<EleEdge1> tempt = elePathInfo.get(v);//llllllllllllll
		int size = tempt.size();
		
		boolean is = true;
		for (boolean e : isVisited) {
			if(!e) is = false;
		}
		
		for(int i=0;i<size;i++){
			
			EleEdge1 temp = tempt.get(i);
				 r = temp.v;
				if(!isVisited[r]){
					boolean con = false;
					for(Integer ele : temp.passPoints){
						if(isPoint[ele]) con = true;
					}
					if(con) continue;
					
					//if(temp.weight > AVERAGE && endStep>300 && Math.random()>0.6) continue;
					
					WEIGHT += temp.weight;
					if(WEIGHT >shortest){ 
						WEIGHT -= temp.weight;
						continue;
					}
				
					dfs(r,searList, isVisited,temp,pathList);
					if(isGo) return;
				}
				if(isGo) return;
				
				if(control != 0 && v!=0){	
					control--;
					break;	
				}//hui tui
				else{RememberV = v;}
				
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
	private ArrayList<PriorityQueue<WeightEdge1>> deepClone(ArrayList<PriorityQueue<WeightEdge1>> queues){
		ArrayList<PriorityQueue<WeightEdge1>> copied = new ArrayList<PriorityQueue<WeightEdge1>>();
		
		for(int i=0;i<queues.size();i++){
			copied.add(new PriorityQueue<WeightEdge1>());
			for(WeightEdge1 e : queues.get(i))
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


class WeightEdge1 implements Comparable<WeightEdge1>{
	
	public int u;
	public int v;
	public int index;
	public int weight;
	
	public WeightEdge1(){}
	
	public WeightEdge1(int index,int u,int v,int weight){
		this.index = index;
		this.u = u;
		this.v = v;
		this.weight = weight;
	}
	
	public WeightEdge1(int index,int weight){
		this.index = index;
		this.weight = weight;
	}
	@Override
	public int compareTo(WeightEdge1 o) {
		// TODO Auto-generated method stub
		if(weight > o.weight) return 1;
		else if(weight == o.weight)
			return 0;
		else 
			return -1;
	}
}

class EleEdge1 extends WeightEdge1{

	public ArrayList<Integer> xulie;
	public ArrayList<Integer> passPoints;
	
	public EleEdge1(){}
	public EleEdge1(int index,int u,int v,int weight,ArrayList<Integer> xulie,ArrayList<Integer> passPoints){
		super(index,u,v,weight);
		
		this.xulie = xulie;
		this.passPoints = passPoints;
	}
	
	public int compareTo(EleEdge1 o) {
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

class GoodLine1 extends EleEdge1{
	public ArrayList<Integer> points = new ArrayList<Integer>();
	public GoodLine1(){
		this.xulie = new ArrayList<Integer>();
	}
	
}
