package oo.taxi;

import java.util.ArrayList;

public class Reqqueue 
/**
 * @OVERVIEW : 
 * Reqqueue use Queue to store all Req
 */
{
	
	private ArrayList<Req> ReqList;
	
	public Reqqueue(){
		ReqList = new ArrayList<Req>();
	}
	
	public boolean repOK(){
		if (ReqList.size() < 0) return false;
		return true;
	}
	
	/**
	 * @EFFECTS : enQueue;
	 */
	public synchronized void enQueue(Req req){
		ReqList.add(req);
	}
	
	/**
	 * @EFFECTS : deQueue;
	 */
	public synchronized Req deQueue(){
		Req req = ReqList.get(0);
		ReqList.remove(0);
		return req;
	}
	
	/**
	 * @EFFECTS : \result = ReqList.size;
	 */
	public synchronized int size(){
		return ReqList.size();
	}
}
