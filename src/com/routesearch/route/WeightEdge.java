package com.routesearch.route;

public class WeightEdge implements Comparable<WeightEdge>{
	
	public int u;
	public int v;
	public int index;
	public int weight;
	
	public WeightEdge(){}
	
	public WeightEdge(int index,int u,int v,int weight){
		this.index = index;
		this.u = u;
		this.v = v;
		this.weight = weight;
	}
	
	public WeightEdge(int index,int weight){
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