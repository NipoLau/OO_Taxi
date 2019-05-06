package oo.taxi;

import java.awt.Point;

public class RoadCtr 
/**
 * @OVERVIEW :
 * RoadCtr controls road status and change it in the map
 */
{
	private int[][] map;
	private TaxiGUI gui;
	private EventManager manager;
	public RoadCtr(int[][] _map, TaxiGUI _gui, EventManager _manager){
		map = _map;
		gui = _gui;
		manager = _manager;
	}
	
	public boolean repOK(){
		return true;
	}
	
	/**
	 * @MODIFIES : map, gui;
	 * @EFFECTS : set road status and updata gui;
	 */
	public synchronized void setRoadStatus(int x1, int y1, int x2, int y2, int status){
	/*
	 * * 0 *  j
	 * 1 x 2
	 * * 3 *
	 * i
	 * */
		gui.SetRoadStatus(new Point(x1, y1), new Point(x2,y2), status);
		int sub_x = x2 - x1;
		int sub_y = y2 - y1;
		if (status == 0){
			if (sub_x == 0){
				switch (sub_y){
					case -1 : {
						switch (map[x2][y2]){
							case 1 : map[x2][y2] = 0; break;
							case 3 : map[x2][y2] = 1; break;
						}
						break;
					}
					case 1 : {
						switch (map[x1][y1]){
							case 1 : map[x1][y1] = 0; break;
							case 3 : map[x1][y1] = 1; break;
						}
						break;
					}
				}
			}
			if (sub_y == 0){
				switch (sub_x){
					case -1 : {
						switch (map[x2][y2]){
							case 2 : map[x2][y2] = 0; break;
							case 3 : map[x2][y2] = 1; break;
						}
						break;
					}
					case 1 : {
						switch (map[x1][y1]){
							case 2 : map[x1][y1] = 0;break;
							case 3 : map[x1][y1] = 1;break;
						}
						break;
					}
				}
			}
		}
		if (status == 1){
			if (sub_x == 0){
				switch (sub_y){
					case -1 : {
						switch (map[x2][y2]){
							case 0 : map[x2][y2] = 1; break;
							case 2 : map[x2][y2] = 3; break;
						}
						break;
					}
					case 1 : {
						switch (map[x1][y1]){
							case 0 : map[x1][y1] = 1; break;
							case 2 : map[x1][y1] = 3; break;
						}
						break;
					}
				}
			}
			if (sub_y == 0){
				switch (sub_x){
					case -1 : {
						switch (map[x2][y2]){
							case 1 : map[x2][y2] = 3; break;
							case 0 : map[x2][y2] = 2; break;
						}
						break;
					}
					case 1 : {
						switch (map[x1][y1]){
							case 1 : map[x1][y1] = 3; break;
							case 0 : map[x1][y1] = 2; break;
						}
						break;
					}
				}
			}
		}
		//开启200ms的通知时间，将ROAD_CHG_EVENT值设为true，200ms后重新设置为false
		(new Thread(new EventMnger(manager))).start();
	}
}

class EventMnger implements Runnable{
	private EventManager manager;
	private static final int WINDOW = 1000;
	public EventMnger(EventManager _manager){
		manager = _manager;
	}
	/**
	 * @EFFECTS : set event on after window set event off;
	 */
	public void run(){
		long curtime = System.currentTimeMillis();
		manager.eventOn();
		while (System.currentTimeMillis() - curtime < WINDOW){}
		manager.eventOff();
	}
}
