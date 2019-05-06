package oo.taxi;

import java.util.HashMap;
import java.util.Map.Entry;

public class FlowMap implements Runnable
/**
 * @OVERVIEW :
 * this class manage flows of each road in the map
 */
{
	private HashMap<String, Integer> flowmap;
	private HashMap<String, Integer> flowmapps;
	private int[][] map;
	private static final int MONITORGAP = 500;

	public FlowMap(int[][] _map){
		this.flowmap = new HashMap<String, Integer>();
		this.flowmapps = new HashMap<String, Integer>();
		this.map = _map;
		initialize();
	}
	
	public boolean repOK(){
		if (flowmap == null || flowmapps == null) return false;
		return true;
	}
	
	/**
	 * @MODIFIES : this.flowmap, this.flowmapps;
	 * @EFFECTS :
	 * clear flowmap, updata flowmapps;
	 */
	public void run()
	{
		while (true){
			for (Entry<String, Integer> entry : flowmap.entrySet()){
				//System.out.println(entry.getKey() + "," + entry.getValue());
				flowmapps.put(entry.getKey(), entry.getValue());
				flowmap.put(entry.getKey(), 0);
			}
			try{Thread.sleep(MONITORGAP);}catch(Exception e){}
		}
	}
	
	/**
	 * @EFFECTS : initialize flowmap;
	 */
	private void initialize()
	{
		for (int i = 0; i < 80; i++) {
			for (int j = 0; j < 80; j++) {
				if (map[i][j] == 1 || map[i][j] == 3) {
					this.flowmap.put(Key(i, j, i, j+1), 0);
					this.flowmap.put(Key(i, j+1 , i, j), 0);
					this.flowmapps.put(Key(i, j, i, j+1), 0);
					this.flowmapps.put(Key(i, j+1 , i, j), 0);
				}
				if (map[i][j] == 2 || map[i][j] == 3) {
					this.	flowmap.put(Key(i, j, i + 1, j), 0);
					this.flowmap.put(Key(i + 1, j , i, j), 0);
					this.	flowmapps.put(Key(i, j, i + 1, j), 0);
					this.flowmapps.put(Key(i + 1, j , i, j), 0);
				}
			}
		}
	}
	
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result = Key;
	 */
	private String Key(int x1,int y1,int x2,int y2)
	{
		return ""+x1+","+y1+","+x2+","+y2;
	}
	
	/**
	 * @EFFECTS : get flow of given road;
	 */
	public synchronized int getFlow(int x1, int y1, int x2, int y2)
	{
		return flowmapps.get(Key(x1,y1,x2,y2));
	}
	
	/**
	 * @MODIFIES: flowmap;
	 * @EFFECTS: flowmap++;
	 */
	public synchronized void passBy(int x1, int y1, int x2, int y2)
	{
		int curFlow = flowmap.get(Key(x1, y1, x2, y2));
		flowmap.put(Key(x1,y1,x2,y2), curFlow + 1);
		flowmap.put(Key(x2,y2,x1,y1), curFlow + 1);
	}
	
	/**
	 * @EFFECTS : directly set flow of given road;
	 */
	public synchronized void setRoadFlow(int x1, int y1, int x2, int y2, int value){
		flowmapps.put(Key(x1,y1,x2,y2), value);
	}
}
