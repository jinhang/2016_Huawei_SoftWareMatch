package com.routesearch.route;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CloneNode {
	private int node;
	private List<Edge> edgelist = new ArrayList<>();
	private int cost;
	private int pathnum;
	private int inoutnum;
	private boolean[] datavisited = new boolean[FindUtil.allVertex];
	public boolean[] getDataState() {
		return datavisited;
	}
	public int getdegree() {
		return inoutnum;
	}
	public void setdegree(int degree) {
		this.inoutnum = degree;
	}
	public void setVisited(int e) {
		datavisited[e] = true;
	}
	public CloneNode(CloneNode n, int v) {
		if (n == null) {
			datavisited[v] = true;
			node = v;
			for (int i = 0; i < FindUtil.condition_Vertex.length; ++i) {
				if (v == FindUtil.condition_Vertex[i]) {
					pathnum++;
					inoutnum += (18-FindUtil.degreeNumber[FindUtil.condition_Vertex[i]])*(FindUtil.condition_Vertex.length-pathnum+1);
				}
			}
		} else {
			datavisited[v] = true;
			edgelist.addAll(n.getpath());
			node = n.getNode();
			Edge tempEdge = FindUtil.defaultgraph.getEdge(node, v);
			edgelist.add(tempEdge);
			node = v;
			cost = n.getCost();
			cost += tempEdge.getCost();
			datavisited[edgelist.get(0).getSt()] = true;
			for (Edge edge : edgelist) {
				datavisited[edge.geten()] = true;
			}
			for (int i = 0; i < FindUtil.condition_Vertex.length; ++i) {
				if (v == FindUtil.condition_Vertex[i]) {
					pathnum++;
					inoutnum += FindUtil.degreeNumber[FindUtil.condition_Vertex[i]];
				}
			}
			pathnum += n.getNum();
		}
	}
	
	public boolean connection(int condition, boolean[] visited) {
		boolean[] tempvisited = new boolean[visited.length];
		for (int i = 0; i < visited.length; ++i) {
			tempvisited[i] = visited[i];
		}
		tempvisited[node] = false;
		Queue<Integer> bfsQueue = new LinkedBlockingQueue<Integer>();
		bfsQueue.add(node);
		/**
		 * 弹出
		 */
		while (!bfsQueue.isEmpty()) {
			Integer pollNode = bfsQueue.poll();
			if (tempvisited[pollNode]) {
				continue;
			}
			tempvisited[pollNode] = true;
			for (int i = 0; i < FindUtil.allVertex; ++i) {
				if (FindUtil.data[pollNode][i] != 0 && !tempvisited[i]) {
					if (i == condition) {
						return true;
					}
					bfsQueue.add(i);
				}
			}
		}
		return false;
	}
	
	public void setNum(int conditionNum) {
		this.pathnum = conditionNum;
	}
	public boolean nodeState(int tempNode) {
		return datavisited[tempNode];
	}
	public int getNode() {
		return node;
	}
	public void setNode(int node) {
		this.node = node;
	}
	public int getNum() {
		return pathnum;
	}
	public List<Edge> getpath() {
		return edgelist;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int weight) {
		this.cost = weight;
	}
}
class SearchTask implements Runnable {  
    private BlockingQueue<File> queue;  
    private String keyword;  
  
    public SearchTask(BlockingQueue<File> queue, String keyword) {  
        this.queue = queue;  
        this.keyword = keyword;  
    }  
    public void run() {  
        /*try {  
            boolean done = false;  
            while (!done) {  
                //取出队首元素，如果队列为空，则阻塞  
                File file = queue.take();  
                if (file == FileEnumerationTask.DUMMY) {  
                    //取出来后重新放入，好让其他线程读到它时也很快的结束  
                    queue.put(file);  
                    done = true;  
                } else  
                    search(file);  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (InterruptedException e) {  
        }*/  
    }  
    public void search(File file) throws IOException {  
        Scanner in = new Scanner(new FileInputStream(file));  
        int lineNumber = 0;  
        while (in.hasNextLine()) {  
            lineNumber++;  
            String line = in.nextLine();  
            if (line.contains(keyword))  
                System.out.printf("%s:%d:%s%n", file.getPath(), lineNumber,  
                        line);  
        }  
        in.close();  
    }  
}  
