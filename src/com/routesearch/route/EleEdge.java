package com.routesearch.route;

import java.util.ArrayList;

public class EleEdge extends WeightEdge{

	public ArrayList<Integer> xulie;
	public ArrayList<Integer> passPoints = new ArrayList<Integer>(300);
	
	public EleEdge(){}
	public EleEdge(int index,int u,int v,int weight,ArrayList<Integer> xulie,ArrayList<Integer> passPoints){
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