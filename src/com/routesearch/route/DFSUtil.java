package com.routesearch.route;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * 
 * @author jinhang 
 * @version 1.0
 *
 */
public class DFSUtil {
	//	private double change = 0.5;//跳变概率
	private int AVERAGE;//简化矩阵平均值
	private int factor = 8;
	private int RememberV = -1;
	public static long startTime;
	public static long endTime;
	private int control = 0;
	private int flag =4 ;
	private long starStep = 0;
	private long endStep = 0;
	private long time = 0;
	/*	private int Size = 300;//矩阵大小
	private WeightEdge[][] tuRect = new WeightEdge[Size][Size];//
	 */	private int[] costs;
	 private boolean isPoint[];//
	 private int WEIGHT = 0;
	 private double averWeight = 0;//路径平均权值
	 private boolean isGo = false;//是否强制退出递归
	 public  static ArrayList<String> PATHLIST = new ArrayList<String>(100);//最终路径边
	 public  static ArrayList<String> End_PATHLIST = new ArrayList<String>(100);
	 private int shortest = Integer.MAX_VALUE;//最短权重路径
	 private Scanner tuIn; //图信息文件
	 private Scanner paIn;//题目路径要求文件
	 private String graphContent;
	 private String condition;
	 //使用优先队列保存加权边，以便于以每个顶点开始的边按权重由小到大排序
	 public ArrayList<PriorityQueue<WeightEdge>> queues = new  ArrayList<PriorityQueue<WeightEdge>>(100);
	 //保存题目要求信息
	 public ArrayList<Integer> pathInfo = new ArrayList<Integer>(50);
	 //保存只包含经过顶点
	 public ArrayList<ArrayList<EleEdge>> elePathInfo = new  ArrayList<ArrayList<EleEdge>>(50);

	 public DFSUtil(String graphContent,String condition,long time) throws FileNotFoundException{
		 startTime=System.currentTimeMillis();
         this.time = time;
		 this.graphContent = graphContent;
		 this.condition = condition; //两个要读取的文件,官方已经读取到两个字符串中
		 tuIn = new Scanner(new File(graphContent));
		 tuIn.useDelimiter("[,\n]");
		 paIn = new Scanner(new File(condition));
		 paIn.useDelimiter("[,|]");

		 //创建图
		 int[] edg = new int[4];
		 int temp = -1; 
		 int count = 0;
		 while(tuIn.hasNext()){
			 count++;
			 //读取一行数据,即一条边,添加到图中
			 for(int i=0;i<4;i++){
				 edg[i] = Integer.parseInt(tuIn.next().trim());
			 }	
			 averWeight += edg[3];
			 if(queues.size()-1 < edg[1]){
				 for(int i=queues.size();i<=edg[1];i++)
					 queues.add(i,new PriorityQueue<WeightEdge>());
			 }
			 queues.get(edg[1]).add(new WeightEdge(edg[0],edg[1],edg[2],edg[3]));
		 }
		 averWeight = averWeight/count;
		 tuIn.close();
		 int t = 0; int tem = 0;
		 while(paIn.hasNext()){
			 int x = Integer.parseInt(paIn.next().trim());
			 if(t == 1) tem = x;
			 else 
				 pathInfo.add(x);
			 t++;
		 }
		 pathInfo.add(tem);
		 paIn.close();
		 isPoint = new boolean[queues.size()];
	 }
	 public String getMinWeightPath(){
		 //矩阵存储s,t,v'中2,2组合间的最小权重路径信息
		 int len = pathInfo.size()-1;
		 for(int i=0;i<len;i++){
			 getTwoVSPath(pathInfo.get(i));
		 }
		 elePathInfo.add(new ArrayList<EleEdge>());
		 //计算简化矩阵平均值
		 int NUM = 0;
		 for(ArrayList<EleEdge> ele : elePathInfo){
			 for (EleEdge e : ele) {
				 AVERAGE += e.weight;
				 NUM++;
			 }
		 }
		 AVERAGE = AVERAGE/NUM;
		 //比较器
		 Comparator<EleEdge> comparator = new Comparator<EleEdge>(){
			 public int compare(EleEdge o1, EleEdge o2) {
				 if(o1.weight>o2.weight)
					 return 1;
				 else if(o1.weight == o2.weight)
					 return 0;
				 else return -1;
			 }
		 };
		 //对简化后的图排序
		 for (ArrayList<EleEdge> ele : elePathInfo) {
			 Collections.sort(ele,comparator);
		 }
		 int v = getXB(pathInfo.get(0));
		 ArrayList<Integer> searList = new ArrayList<Integer>();
		 ArrayList<String> pathList = new ArrayList<String>();
		 boolean[] isVisited = new boolean[pathInfo.size()];

		 List<String> answer = dfs(v, searList, isVisited,null,pathList);
		 //System.out.println("****"+answer);
		 //返回结果
		 String result = "";
		 if(shortest > 10000) result = "NA";
		 else{
			 for (String e : answer) {
				 result += e;
			 }
			 result = result.substring(1);
		 }
		 return result;
	 }

	 //搜寻途中从顶点u到其他需要经过顶点v'的权重最小路径
	 private void getTwoVSPath(int sourceIndex) {
		 //好的路径集，好的路径是指例0中的问题,2到1的最短路径经过了3这种路径，
		 //中间经过的v'中的点越多，路径权重越小，越好
		 //已经找到最短路径的顶点集

		 ArrayList<Integer> T = new ArrayList<Integer>();
		 T.add(sourceIndex);

		 int numberOfVerttices = queues.size();
		 int[] parent = new int[numberOfVerttices];
		 int[] indexs = new int[numberOfVerttices];//去到下标值的父亲的边的索引值
		 int[] weight = new int[numberOfVerttices];//去到下标值的父亲的边的权重值

		 parent[sourceIndex] = -1;

		 costs = new int[numberOfVerttices];
		 for(int i=0;i<costs.length;i++)
			 costs[i] = 10000;
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
					 queue.get(u).remove(e);
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

		 ArrayList<EleEdge> prio = new ArrayList<EleEdge>();

		 for(int x = 0;x<pathInfo.size();x++){
			 int t = pathInfo.get(x);
			 if(t != sourceIndex && costs[t] < 10000){
				 getXuLie(prio,t,parent,indexs,sourceIndex);
			 }
		 }
		 elePathInfo.add(prio);
	 }

	 //找到goodLine
	 private void findGoodLine(int[] parent,int v,int sour,int[] indexs,int[] weight,ArrayList<PriorityQueue<WeightEdge>> queue,
			 ArrayList<Integer> T){
		 int p = v;
		 if(parent[v] == sour) return;

		 GoodLine goodLine = new GoodLine();

		 goodLine.points.add(sour);
		 goodLine.u = sour; goodLine.v = v;

		 while(v != sour){	
			 goodLine.weight += weight[v];
			 goodLine.xulie.add(indexs[v]);

			 if(pathInfo.contains(v)){
				 goodLine.points.add(v);
			 }
			 else goodLine.passPoints.add(v);
			 v = parent[v];
		 }

		 /*	double aver = goodLine.weight/goodLine.xulie.size();
		if(goodLine.xulie.size()>size+1 && aver>averWeight){///zhong yao
			costs[p] = 10000;return;
		}*/

		 if(goodLine.points.size() < 3){//说明可达
			 return;
		 }
		 //方案
		 else{
			 //否则采取第二种方案
			 ArrayList<Integer> t = goodLine.points;
			 costs[p] = 10000;
			 /*if(!queue.get(parent[p]).isEmpty() && T.contains(queue.get(parent[p]).peek().v))
				queue.get(parent[p]).remove();
			T.remove(new Integer(p));*/
		 }
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
		 prio.add(new EleEdge(0,getXB(sour),getXB(v),costs[v],indexSS,points));}

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
     /**
      * 深度优先遍历 
      * @param v
      * @param searList
      * @param isVisited
      * @param eledge
      * @param pathList
      * @return
      */
	 private List  dfs(int v,ArrayList<Integer> searList,boolean[] isVisited,EleEdge eledge,ArrayList<String> pathList){
		 if(isGo) return PATHLIST;
		 endStep++;//控制步数,到多少步时回退
		 endTime = System.currentTimeMillis();
		 if(endTime-startTime >time){
			 isGo = true;
			 End_PATHLIST=PATHLIST;
			 return End_PATHLIST;
		 }
		 if(endStep-starStep > factor*Math.pow(pathInfo.size(),3)){
			 factor = pathInfo.size() - searList.size();
			 starStep = endStep = 0;
			 if(flag != pathInfo.size()/4)flag++; 
			 int vStep = 0;
			 if(RememberV != -1) vStep = searList.size()-searList.indexOf(RememberV);//回退到v

			 control = flag+vStep;
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
		 ArrayList<EleEdge> tempt = elePathInfo.get(v);
		 int size = tempt.size();
		 //回退前判断是否已经找到一条路径,即是否已经到达终点
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
					 if(isPoint[ele]) con = true;// 如果a-b中间的点已被访问过
				 }
				 if(con) continue;

				 if(temp.weight > AVERAGE && endStep>300 && Math.random()>0.6) continue;// 跳变化

				 WEIGHT += temp.weight;
				 if(WEIGHT > shortest){//如果到下一个节点WEIGHT值比shortest还大,则不必去到那个点
					 WEIGHT -= temp.weight;
					 continue;
				 }

				 dfs(r,searList, isVisited,temp,pathList);
				 if(isGo) return PATHLIST;
			 }
			 if(isGo) return PATHLIST;

			 if(control != 0){	
				 control--;
				 break;	
			 }
			 else{RememberV = v;}
		 }
		 if(is && v == pathInfo.size()-1){
			 starStep = endStep = 0;
			 //输出所有可能路径,并判断是否合法
			 String s3 = "";
			 for (String ele : pathList) {
				 s3 = s3 + ele;
			 }
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
			 WEIGHT -= eledge.weight;		
			 pathList.remove(pathList.size()-1);
		 }
		return PATHLIST;
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