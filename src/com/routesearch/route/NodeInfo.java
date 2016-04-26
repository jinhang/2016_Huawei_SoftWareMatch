package com.routesearch.route;
/**
 * BfsNodeInfo
 * 链表
 * @author jinhang
 *
 */
public class NodeInfo {
	private int node;
	private NodeInfo pNode;
	public int getNode() {
		return node;
	}
	public void setNode(int node) {
		this.node = node;
	}
	public NodeInfo getPreNode() {
		return pNode;
	}
	public void setPreNode(NodeInfo pre) {
		this.pNode = pre;
	}
	public NodeInfo(int node, NodeInfo prenode) {
		super();
		this.node = node;
		this.pNode = prenode;
	}

}
