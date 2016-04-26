package com.routesearch.route;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
/**
 * 蚁群
 * @author jinhang
 *
 */
public class SolutionWithAnt {
	static long start_time;
	private final int MaxNode = 600;
	final double ALPHA = 1.0; //启发因子，信息素的重要程度
	final double BETA = 0.6;   //期望因子，城市间距离的重要程度
	final double ROU = 0.89; //信息素残留参数
	final int N_ANT_COUNT = 20; //蚂蚁数量
	final int N_IT_COUNT = 20000; //迭代次数
	public static int N_CITY_COUNT; //城市数量
	final double DBQ = 50.0; //总信息素
	final static double DB_MAX = 10e9;
	static int st;//起点和终点
	static int en;
	final int WHITE= 0;  
	final int GRAY =1;  
	final int BLACK= 2; 
	final int ThresholdTime_ms =9000;
	final int ThresholdNode =101;
	ArrayList<EdgeNode> adjlist;
	static ArrayList<Integer> TubaVec=new ArrayList<Integer>();
	static int num_node;
	static double g_Trial[][];
	static double g_Distance[][];
	public  int rnd(int nLow, int nUpper)
	{

		return (int) Math.round(Math.random()*(nUpper-nLow)+nLow);
	}

	public  double rnd(double dbLow, double dbUpper)
	{
		Random ra =new Random();



		return ra.nextDouble() * (dbUpper-dbLow)+dbLow;
	}

	public  double ROUND(double dbA)
	{
		return (double)((int)(dbA + 0.5));
	}

	public static  ArrayList<ArrayList<EdgeNode>> buildGraphAdjlist(String topo, int edge_num,int num_node){

		ArrayList<ArrayList<EdgeNode>> adj_vec = new ArrayList<ArrayList<EdgeNode>>(600);
		for(int i = 0; i < 600; i++ ){
			adj_vec.add(new ArrayList<EdgeNode>());
		}
		String[] allLines = topo.split("\\n");
		ArrayList<EdgeNode> temp;
		for (int i = 0; i < edge_num; i++){
			boolean insert = false;
			String[] node_info = allLines[i].split(",");
			temp=null;
			EdgeNode pedgnode = new EdgeNode();
			pedgnode.edg_no = Integer.parseInt(node_info[0]);
			pedgnode.no = Integer.parseInt(node_info[2]);
			pedgnode.cost = Integer.parseInt(node_info[3]);

			temp= adj_vec.get(Integer.parseInt(node_info[1]));

			for (int j = 0; j < temp.size(); j++) {
				EdgeNode e = temp.get(j);
				if (e.no == pedgnode.no) {
					if (e.cost > pedgnode.cost) {
						temp.remove(e);
						insert = false;
						break;
					} else {
						if (Integer.parseInt(node_info[2]) > num_node) {
							num_node = Integer.parseInt(node_info[2]);
						}
						insert = true;
						break;
					}
				}
				if (e.cost > pedgnode.cost) {
					temp.add(temp.indexOf(e), pedgnode);
					if (Integer.parseInt(node_info[2]) > num_node) {
						num_node = Integer.parseInt(node_info[2]);
					}
					insert = true;
					break;
				}
			}
			if(! insert){
				adj_vec.get(Integer.parseInt(node_info[1])).add(pedgnode);
				if (Integer.parseInt(node_info[2]) > num_node) {
					num_node = Integer.parseInt(node_info[2]);
				}
			}
		}
		num_node++;


		SolutionWithAnt.num_node=num_node;
		return adj_vec;
	}
	public static  ArrayList<Integer> analysisDemandVec(String demandfile) throws Exception
	{
		ArrayList<Integer> pathInfo = new ArrayList<Integer>(50);
		Scanner paIn;
		paIn = new Scanner(new File(demandfile));
		paIn.useDelimiter("[,|\n]");
		int t = 0; int tem = 0;
		while(paIn.hasNext()){
			int x = Integer.parseInt(paIn.next().trim());

			if(t == 1) tem = x;
			else pathInfo.add(x);
			t++;
		}
		pathInfo.add(tem);

		st = pathInfo.get(0);
		en = pathInfo.get(pathInfo.size()-1);
		pathInfo.remove(0);
		pathInfo.remove(pathInfo.size()-1);
		paIn.close();
		return pathInfo;
	}
	
	public static void demandVecHasNoTuba(ArrayList<ArrayList<EdgeNode>> adj_vec,ArrayList<Integer> deman_vec, ArrayList<Integer> tubaVec,int num_node) {
		while(true)
		{
			int count = 0;
			for(int i=0;i<deman_vec.size();i++){
				if(adj_vec.get(deman_vec.get(i)).size() == 1){
					count++;
					EdgeNode adg_it = adj_vec.get(deman_vec.get(i)).get(0);
					int NodeNo = adg_it.no;
					tubaVec.add(deman_vec.get(i));
					tubaVec.add(NodeNo);
					deman_vec.set(i,NodeNo);
				}
			}
			if(count == 0) return;
		}
	}

	private static void preProcess(ArrayList<ArrayList<EdgeNode>> adj_vec, int num_node, ArrayList<Integer> tubaVec) {

		for (int i = 0; i < tubaVec.size(); i += 2)
		{
			int OutNo = tubaVec.get(i);
			int InNo = tubaVec.get(i+1);

			for (int j = 0; j < num_node; j++)
			{
				if (j == OutNo)
					continue;

				EdgeNode adj_it;
				ArrayList<EdgeNode> point = adj_vec.get(j);

				for(int k=0;k<point.size();k++)//一个点到InNo的边,然后删除
				{
					adj_it = point.get(k);
					if (adj_it.no == InNo)
					{
						point.remove(k);
					}
				}
			}
		}

		
	}
	public static ArrayList<Integer> search_route(String graphcontent,String graphcontentfile,String conditionfile) throws Exception
	{
		start_time = System.currentTimeMillis();
		num_node =0;
		ArrayList<ArrayList<EdgeNode>> adj_vec = buildGraphAdjlist(graphcontent, graphcontent.split("\\n").length,num_node);	
		ArrayList<Integer> deman_vec = analysisDemandVec(conditionfile);
		demandVecHasNoTuba(adj_vec, deman_vec, TubaVec, num_node);
		preProcess(adj_vec, num_node, TubaVec);
		
		for (int i = 0; i < num_node; i++)
		{
			if (adj_vec.get(i).size() == 0)
			{

				if (deman_vec.contains(i))
				{
					System.out.println("NA");
					return null;

				}
			}
		}
		g_Trial= new double[num_node][num_node];
		g_Distance = new double[num_node][num_node];
		Solution2Tsp tsp = new Solution2Tsp();
		tsp.InitData(num_node, adj_vec, g_Distance, g_Trial); 
		tsp.Search(deman_vec, adj_vec, g_Distance, g_Trial); 

		if (tsp.m_cBestAnt.m_dbPathLength == DB_MAX)
		{
			System.out.println("***********"+"NA");
			return null;
		}
		ArrayList<Integer> answer = new ArrayList<Integer>(); 
		for (int i = 0; i<tsp.m_cBestAnt.m_nMovedCityCount - 1; i++)
			answer.add(tsp.m_cBestAnt.m_nPath[i]);
		answer.add(tsp.m_cBestAnt.m_nPath[tsp.m_cBestAnt.m_nMovedCityCount - 1]);
	
		return answer;
	}
}
class EdgeNode {
	int no;
	int edg_no;
	int cost;
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public int getEdg_no() {
		return edg_no;
	}
	public void setEdg_no(int edg_no) {
		this.edg_no = edg_no;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
}
class Path {
	double cost;
	ArrayList<Integer> node_name;
	ArrayList<Integer> edg_name;

	public int compareTo(Path p1) {
		// TODO Auto-generated method stub
		if(cost > p1.cost) return 1;
		else if(cost == p1.cost)
			return 0;
		else 
			return -1;
	}
}
class Ant extends SolutionWithAnt{


	public  double m_dbPathLength; 

	public int m_nDemanCity[]; 

	public int m_nPath[]; 	

	public int m_nAllowedCity[]; 

	public int m_nCurCityNo;

	public int m_nMovedCityCount; 


	public Ant(){

	}
	public void Init(ArrayList<Integer> deman_vec, ArrayList<ArrayList<EdgeNode>> adj_vec)
	{

		m_nPath = new int[N_CITY_COUNT];

		m_nAllowedCity = new int[N_CITY_COUNT];
		m_nDemanCity =  new int[N_CITY_COUNT];

		for (int i = 0; i<N_CITY_COUNT; i++)
		{
			m_nAllowedCity[i] = 1; 
			m_nPath[i] = 0; 
		}

		for (int i = 0; i < N_CITY_COUNT; i++)
		{
			if((adj_vec.get(i).size()==0)&&(i!=en))
				m_nAllowedCity[i] = 0;
		}

		for (int i = 0; i<N_CITY_COUNT; i++)
		{
			m_nDemanCity[i] = 0; 
		}
		for (int i = 0; i < deman_vec.size(); i++)
		{
			m_nDemanCity[deman_vec.get(i)] = 1; 
		}
		m_dbPathLength = 0.0;
		m_nCurCityNo = st;
		m_nPath[0] = m_nCurCityNo;
		m_nAllowedCity[m_nCurCityNo] = 0;
		m_nMovedCityCount = 1;

	}
	/**
	 * 选择下一个城市
	 * @param g_Distance
	 * @param g_Trial
	 * @param deman_count
	 * @param deman_node_count
	 * @return
	 */
	public int ChooseNextCity(double g_Distance[][], double g_Trial[][], int deman_count, int deman_node_count)
	{

		int nSelectedCity = -1; 
		double dbTotal = 0.0;
		double prob[] = new double[N_CITY_COUNT];
		double Total = 0.0;

		for (int i = 0; i <N_CITY_COUNT; i++)
		{
			if ((m_nAllowedCity[i] == 1) && (g_Distance[m_nCurCityNo][i] != DB_MAX)) 
			{
				Total += Math.pow(g_Trial[m_nCurCityNo][i], ALPHA)*Math.pow(1.0 / g_Distance[m_nCurCityNo][i],BETA);
			}
		}

		for (int i = 0; i<N_CITY_COUNT; i++)
		{
			if ((m_nAllowedCity[i] == 1) && (g_Distance[m_nCurCityNo][i] != DB_MAX)) 
			{
				if (m_nDemanCity[i] == 1)
				{
					prob[i] = 0.8;
				}

				else
				{
					prob[i] = Math.pow(g_Trial[m_nCurCityNo][i], ALPHA)*Math.pow(1.0 / g_Distance[m_nCurCityNo][i], BETA); 
				}
				if ((deman_count != deman_node_count) && (i == en))
					prob[i] = 0.0;

				dbTotal = dbTotal + prob[i]; 
			}
			else 
			{
				prob[i] = 0.0;
			}
		}
		double dbTemp = 0.0;
		if (dbTotal > 0.0) 
		{
			dbTemp = rnd(0.0, dbTotal); 
			for (int i = 0; i<N_CITY_COUNT; i++)
			{
				if ((m_nAllowedCity[i] == 1) && (g_Distance[m_nCurCityNo][i] !=DB_MAX)) 
				{
					dbTemp = dbTemp - prob[i];
					if (dbTemp < 0.0) 
					{
						nSelectedCity = i;
						break;
					}
				}
			}
		}
		if (nSelectedCity == -1)
		{
			for (int i = 0; i<N_CITY_COUNT; i++)
			{
				if ((m_nAllowedCity[i] == 1) && (g_Distance[m_nCurCityNo][i] != DB_MAX)) //可去的并且没有去过的城市
				{
					nSelectedCity = i;
					break;
				}
			}
		}
	
		return nSelectedCity;
	}

	/**
	 * 蚂蚁在城市间移动
	 * @param g_Distance
	 * @param g_Trial
	 * @param deman_count
	 * @param deman_node_count
	 * @return
	 */
	public int Move(double g_Distance[][], double g_Trial[][], int deman_count, int deman_node_count)
	{
		int nCityNo = ChooseNextCity(g_Distance, g_Trial, deman_count, deman_node_count); //选择下一个城市

		if (nCityNo == -1)
		{
			return 0;
		}

		m_nPath[m_nMovedCityCount] = nCityNo; 
		m_nAllowedCity[nCityNo] = 0;
		m_nCurCityNo = nCityNo; 
		m_nMovedCityCount++; 
		return 1;
	}
	public void Search(ArrayList<Integer> deman_vec, ArrayList<ArrayList<EdgeNode>> adj_vec, double g_Distance[][], double g_Trial[][])
	{
		Init(deman_vec, adj_vec);
		int deman_node_count = deman_vec.size();
		int deman_count = 0;
		while ((m_nCurCityNo != en) || (deman_count != deman_node_count))
		{
			int rt = Move(g_Distance, g_Trial, deman_count, deman_node_count);
			if (rt == 0)
			{
				if (deman_vec.contains(Integer.valueOf(m_nCurCityNo)))
				{
					m_dbPathLength = DB_MAX;
					return;
				}

				m_nCurCityNo = m_nPath[m_nMovedCityCount - 2];
				m_nPath[m_nMovedCityCount-1] = 0;
				m_nMovedCityCount--;

				if (deman_vec.contains(Integer.valueOf(m_nCurCityNo)))
				{
					deman_count--;
				}
				continue;

			}
			if (deman_vec.contains(Integer.valueOf(m_nCurCityNo)))
			{
				deman_count++;
			}


		}
		CalPathLength(g_Distance);
	}
	public void CalPathLength(double g_Distance[][])
	{

		m_dbPathLength = 0.0;
		int m = 0;
		int n = 0;

		for (int i = 1; i<m_nMovedCityCount; i++)
		{
			m = m_nPath[i];
			n = m_nPath[i - 1];
			m_dbPathLength = m_dbPathLength + g_Distance[n][m];
		}
	}

}


class Solution2Tsp extends SolutionWithAnt{

	Ant m_cAntAry[]=new Ant[N_ANT_COUNT]; 
	Ant m_cBestAnt;
	public Solution2Tsp(){
		for(int i=0;i< N_ANT_COUNT;i++){
			m_cAntAry[i]=new Ant();
		}
		m_cBestAnt=new Ant(); 
	}
	public void InitData(int num_node, ArrayList<ArrayList<EdgeNode>> adj_vec, double g_Distance[][], double g_Trial[][])
	{
		m_cBestAnt.m_dbPathLength = DB_MAX;
		N_CITY_COUNT = num_node;
		double dbTemp = 0.0;
		for (int i = 0; i< N_CITY_COUNT; i++)
		{
			for (int j = 0; j< N_CITY_COUNT; j++)
			{
				g_Distance[i][j] = DB_MAX;
			}
		}

		for (int i = 0; i < num_node; i++)
		{	

			for(EdgeNode a:adj_vec.get(i)){
				g_Distance[i][a.no] = a.cost;
			}

		}

		for (int i = 0; i< N_CITY_COUNT; i++)
		{
			for (int j = 0; j< N_CITY_COUNT; j++)
			{
				g_Trial[i][j] = 1.0;
			}
		}

	}
	public void UpdateTrial(double g_Trial[][])
	{
		double dbTempAry[][] = new double[N_CITY_COUNT][N_CITY_COUNT];
		for (int i = 0; i < N_CITY_COUNT; i++)
			for (int j = 0; j < N_CITY_COUNT; j++)
				dbTempAry[i][j] = 0;

		int m = 0;
		int n = 0;
		for (int i = 0; i<N_ANT_COUNT; i++) 
		{
			for (int j = 1; j<m_cAntAry[i].m_nMovedCityCount; j++)
			{
				m = m_cAntAry[i].m_nPath[j];
				n = m_cAntAry[i].m_nPath[j - 1];
				dbTempAry[n][m] = dbTempAry[n][m] + DBQ / m_cAntAry[i].m_dbPathLength;
			}
		}
		for (int i = 0; i<N_CITY_COUNT; i++)
		{
			for (int j = 0; j<N_CITY_COUNT; j++)
			{
				g_Trial[i][j] = g_Trial[i][j] * ROU + dbTempAry[i][j];
			}
		}


	}


	public void Search(ArrayList<Integer> deman_vec, ArrayList<ArrayList<EdgeNode>> adj_vec, double g_Distance[][], double g_Trial[][])
	{
		for (int i = 0; i<N_IT_COUNT; i++)
		{
			int dead_ants = 0;
			for (int j = 0; j<N_ANT_COUNT; j++)
			{
				m_cAntAry[j]= new Ant();
				m_cAntAry[j].Search(deman_vec, adj_vec, g_Distance, g_Trial);
				if (m_cAntAry[j].m_dbPathLength == DB_MAX)
					dead_ants++;
			}
			for (int j = 0; j<N_ANT_COUNT; j++)
			{
				if (m_cAntAry[j].m_dbPathLength < m_cBestAnt.m_dbPathLength)
				{
					m_cBestAnt = m_cAntAry[j];
				}
			}
			long end = System.currentTimeMillis();
			long out_ms = end-start_time;
			if (out_ms > ThresholdTime_ms)
				return;
			UpdateTrial(g_Trial);
		}

	}

}