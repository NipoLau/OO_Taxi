package oo.taxi;

import java.awt.Point;
import java.util.ArrayList;

import oo.taxi.TrafficLight.LIGHT;

public class Taxi implements Runnable
/**
 * @OVERVIEW :
 * taxi thread is here, manage taxi movement here
 */
{
	private int id;
	private int[][] map;
	private TaxiGUI gui;
	private final static int GVSTAYGAP = 100;
	private final static int DRIVETIME = 500;
	private final static int DOORTIME = 1000;
	private final static int STOPTIME = 1000;
	private final static int STOPGAP = 20000;
	private final static int MAPSIZE = 80;
	private final static int ALITTLESNAP = 20;
	private EventManager manager;
	private FlowMap flowmap;
	private TaxiData taxidata;
	//private long SYSTEMTIME;
	private Shortpath shortpath;
	private SafeFile detail;
	private String log;
	protected ArrayList<String> histry;
	private int type;
	private TrafficLight[][] lightGroup;
	
	public Taxi(int _id, TaxiData _taxidata, TaxiGUI _gui, int[][] _map, Shortpath _shortpath, FlowMap _flowmap, EventManager _manager, SafeFile _detail, TrafficLight[][] _lightGroup, int _type){
		id = _id;
		taxidata = _taxidata;
		gui = _gui;
		map = _map;
		//SYSTEMTIME = _systemtime;
		shortpath = _shortpath;
		flowmap = _flowmap;
		manager = _manager;
		detail = _detail;
		log = "";
		lightGroup = _lightGroup;
		type = _type;
		if (type == 1) histry = new ArrayList<String>();
		INIT();
	}
	
	public boolean repOK(){
		if (id < 0 || id >= 100) return false;
		return true;
	}
	
	/**
	 * @REQUIRES : None;
	 * @MODIFIES : taxidata, gui, flowmap;
	 * @EFFECTS : 
	 * taxi status is stopping do not respond;
	 * taxi status is waiting and taxireq is null continue moving;
	 * taxi status is waiting and \exist taxireq then search shortest path and fetch passenger then taxi status set to serving;
	 * taxi status is serving then search shortest path and send passenger to dst;
	 */
	public void run(){
		boolean flag = true;                //标记是否刚进入等待服务状态
		//long starttime = System.currentTimeMillis();             //刚进入等待服务状态的时间
		long preSub = 0;                       //记录(出租车时间/STOPGAP)的结果，用于判断是否又经过一次STOPGAP
		int[] path = new int[MAPSIZE*MAPSIZE];
		int[][] preloc = new int[1][2]; preloc[0][0] = -1; preloc[0][1] = -1;
		while (true){
			if (flag == false && (taxidata.getTaxiTime(id)/STOPGAP) > preSub){
				int[][] taxiloc = taxidata.getTaxiLoc(id);
				taxidata.setTaxiStat(id, "STOPPING");
				gui.SetTaxiStatus(id, new Point(taxiloc[0][0], taxiloc[0][1]), 0);
				gv.stay(GVSTAYGAP);
				try{Thread.sleep(STOPTIME);}catch(Exception e){}
				taxidata.addTaxiTime(id, STOPTIME);
				taxidata.setTaxiStat(id, "WAITING");
				gui.SetTaxiStatus(id, new Point(taxiloc[0][0], taxiloc[0][1]), 2);
				gv.stay(GVSTAYGAP);
				flag = true;
				preSub = taxidata.getTaxiTime(id)/STOPGAP;
				//starttime = System.currentTimeMillis();
			}
			if (taxidata.getTaxiReq(id) == null){
				flag = false;
				int [][] taxiloc = taxidata.getTaxiLoc(id);
				int [][] nextloc = branch(taxiloc[0][0],taxiloc[0][1]);
				if (preloc[0][0] != -1 && lightGroup[taxiloc[0][0]][taxiloc[0][1]].hasLight()){
					switch (driveDir(preloc, taxiloc, nextloc)){
					case 0 : {
						switch (roadDir(taxiloc, nextloc)){
						case 0 : while (lightGroup[taxiloc[0][0]][taxiloc[0][1]].getlrLight() == LIGHT.RED){try{Thread.sleep(ALITTLESNAP);}catch(Exception e){}};break;
						case 1 : while (lightGroup[taxiloc[0][0]][taxiloc[0][1]].getudLight() == LIGHT.RED){try{Thread.sleep(ALITTLESNAP);}catch(Exception e){}};break;
						default : break;
						}
					}
					case 1 : {
						switch(roadDir(taxiloc, nextloc)){
						case 0 : while (lightGroup[taxiloc[0][0]][taxiloc[0][1]].getlrLight() == LIGHT.RED){try{Thread.sleep(ALITTLESNAP);}catch(Exception e){}}; break;
						case 1 : while (lightGroup[taxiloc[0][0]][taxiloc[0][1]].getudLight() == LIGHT.RED){try{Thread.sleep(ALITTLESNAP);}catch(Exception e){}}; break;
						default : break;
						}
					}
					case 2 : break;
					case 3 : break;
					default : break;
					}
				}
				//System.out.println(taxiloc[0][0] + "," + taxiloc[0][1] + "," + nextloc[0][0] + "," + nextloc[0][1]);
				try{Thread.sleep(DRIVETIME);}catch(Exception e){}
				taxidata.addTaxiTime(id, DRIVETIME);
				if (taxidata.getforceStatus(id) == false){
					flowmap.passBy(taxiloc[0][0], taxiloc[0][1], nextloc[0][0], nextloc[0][1]);
					taxidata.setTaxiLoc(id, nextloc);
					preloc[0][0] = taxiloc[0][0]; preloc[0][1] = taxiloc[0][1];
					gui.SetTaxiStatus(id, new Point(nextloc[0][0], nextloc[0][1]), 2);
					gv.stay(GVSTAYGAP);
				}
				else taxidata.setforceStatusOff(id);
			}
			else{
				//接单后前往乘客位置
				log += "-------------------------------------------" + "\r\n";
				log += id + " 号出租车已接单，请求信息：" + "\r\n";
				log += "请求位置：(" + taxidata.getTaxiReq(id).getSrcLoc()[0][0] + "," + taxidata.getTaxiReq(id).getSrcLoc()[0][1] + ")" + "\r\n";
				log += "目标位置：(" + taxidata.getTaxiReq(id).getDstLoc()[0][0] + "," + taxidata.getTaxiReq(id).getDstLoc()[0][1] + ")" + "\r\n";
				log += "请求时刻：" + taxidata.getTaxiReq(id).getTime() + "\r\n";
				log += "-------------------------------------------" + "\r\n";
				log += id + " 号出租车前往乘客位置" + "\r\n";
				int[][] taxiloc = taxidata.getTaxiLoc(id);
				taxidata.setTaxiStat(id, "SERVING");
				gui.SetTaxiStatus(id, new Point(taxiloc[0][0], taxiloc[0][1]), 1);
				gv.stay(GVSTAYGAP);
				int[][] tarloc = taxidata.getTaxiReq(id).getSrcLoc();
				int distance = shortpath.getShortestPath(taxiloc[0][0], taxiloc[0][1], tarloc[0][0], tarloc[0][1], path);
				int nextlocno = taxiloc[0][0]*MAPSIZE+taxiloc[0][1];
				int tarlocno = tarloc[0][0]*MAPSIZE+tarloc[0][1];
				log += "- 途经 " + "(" + taxiloc[0][0] + "," + taxiloc[0][1] + ")" + "," + "时刻 " + taxidata.getTaxiTime(id) + "\r\n";
				while (nextlocno != tarlocno){
					if (manager.EventIsOn()) {
						System.out.println("道路改变，重新寻路");
						taxiloc = taxidata.getTaxiLoc(id);
						nextlocno = taxiloc[0][0]*MAPSIZE+taxiloc[0][1];
						distance = shortpath.getShortestPath(taxiloc[0][0], taxiloc[0][1], tarloc[0][0], tarloc[0][1], path);
					}
					nextlocno = path[nextlocno];
					//if (nextloc[0][0] == taxiloc[0][0] && nextloc[0][1] == taxiloc[0][1]) continue;
					//System.out.println(taxiloc[0][0] + "," + taxiloc[0][1]);
					//System.out.println(nextlocno/MAPSIZE + "," + nextlocno%MAPSIZE);
					flowmap.passBy(taxiloc[0][0], taxiloc[0][1], nextlocno/MAPSIZE, nextlocno%MAPSIZE);
					int[][] nextloc = new int[1][2]; nextloc[0][0] = nextlocno/MAPSIZE; nextloc[0][1] = nextlocno%MAPSIZE;
					if (preloc[0][0] != -1 && lightGroup[taxiloc[0][0]][taxiloc[0][1]].hasLight()){
						switch (driveDir(preloc, taxiloc, nextloc)){
						case 0 : {
							switch (roadDir(taxiloc, nextloc)){
							case 0 : while (lightGroup[taxiloc[0][0]][taxiloc[0][1]].getlrLight() == LIGHT.RED){try{Thread.sleep(ALITTLESNAP);}catch(Exception e){}};break;
							case 1 : while (lightGroup[taxiloc[0][0]][taxiloc[0][1]].getudLight() == LIGHT.RED){try{Thread.sleep(ALITTLESNAP);}catch(Exception e){}};break;
							default : break;
							}
						}
						case 1 : {
							switch(roadDir(taxiloc, nextloc)){
							case 0 : while (lightGroup[taxiloc[0][0]][taxiloc[0][1]].getlrLight() == LIGHT.RED){try{Thread.sleep(ALITTLESNAP);}catch(Exception e){}}; break;
							case 1 : while (lightGroup[taxiloc[0][0]][taxiloc[0][1]].getudLight() == LIGHT.RED){try{Thread.sleep(ALITTLESNAP);}catch(Exception e){}}; break;
							default : break;
							}
						}
						case 2 : break;
						case 3 : break;
						default : break;
						}
					}
					preloc[0][0] = taxiloc[0][0]; preloc[0][1] = taxiloc[0][1];
					taxiloc[0][0] = nextlocno/MAPSIZE; taxiloc[0][1] = nextlocno%MAPSIZE;
					taxidata.setTaxiLoc(id, taxiloc);
					gui.SetTaxiStatus(id, new Point(taxiloc[0][0], taxiloc[0][1]), 1);
					gv.stay(GVSTAYGAP);
					try{Thread.sleep(DRIVETIME);}catch(Exception e){}
					taxidata.addTaxiTime(id, DRIVETIME);
					log += "- 途经 " + "(" + taxiloc[0][0] + "," + taxiloc[0][1] + ")" + "," + "时刻 " + taxidata.getTaxiTime(id) + "\r\n";
				}
				log += "-------------------------------------------" + "\r\n";
				System.out.println("请求: " + taxidata.getTaxiReq(id).toString() + " : " + "出租车 " + id + " 到达乘客位置, 到达时刻: " + taxidata.getTaxiTime(id));
				//上下车
				try{Thread.sleep(DOORTIME);}catch(Exception e){}
				taxidata.addTaxiTime(id, DOORTIME);
				log += id + " 号出租车前往目的地" + "\r\n";
				//前往目标位置
				tarloc = taxidata.getTaxiReq(id).getDstLoc();
				distance = shortpath.getShortestPath(taxiloc[0][0], taxiloc[0][1], tarloc[0][0], tarloc[0][1], path);
				nextlocno = taxiloc[0][0]*MAPSIZE+taxiloc[0][1];
				tarlocno = tarloc[0][0]*MAPSIZE+tarloc[0][1];
				while (nextlocno != tarlocno){
					if (manager.EventIsOn()) {
						System.out.println("道路改变，重新寻路");
						taxiloc = taxidata.getTaxiLoc(id);
						nextlocno = taxiloc[0][0]*MAPSIZE+taxiloc[0][1];
						distance = shortpath.getShortestPath(taxiloc[0][0], taxiloc[0][1], tarloc[0][0], tarloc[0][1], path);
					}
					nextlocno = path[nextlocno];
					flowmap.passBy(taxiloc[0][0], taxiloc[0][1], nextlocno/MAPSIZE, nextlocno%MAPSIZE);
					int[][] nextloc = new int[1][2]; nextloc[0][0] = nextlocno/MAPSIZE; nextloc[0][1] = nextlocno%MAPSIZE;
					if (preloc[0][0] != -1 && lightGroup[taxiloc[0][0]][taxiloc[0][1]].hasLight()){
						switch (driveDir(preloc, taxiloc, nextloc)){
						case 0 : {
							switch (roadDir(taxiloc, nextloc)){
							case 0 : while (lightGroup[taxiloc[0][0]][taxiloc[0][1]].getlrLight() == LIGHT.RED){try{Thread.sleep(ALITTLESNAP);}catch(Exception e){}};break;
							case 1 : while (lightGroup[taxiloc[0][0]][taxiloc[0][1]].getudLight() == LIGHT.RED){try{Thread.sleep(ALITTLESNAP);}catch(Exception e){}};break;
							default : break;
							}
						}
						case 1 : {
							switch(roadDir(taxiloc, nextloc)){
							case 0 : while (lightGroup[taxiloc[0][0]][taxiloc[0][1]].getlrLight() == LIGHT.RED){try{Thread.sleep(ALITTLESNAP);}catch(Exception e){}}; break;
							case 1 : while (lightGroup[taxiloc[0][0]][taxiloc[0][1]].getudLight() == LIGHT.RED){try{Thread.sleep(ALITTLESNAP);}catch(Exception e){}}; break;
							default : break;
							}
						}
						case 2 : break;
						case 3 : break;
						default : break;
						}
					}
					preloc[0][0] = taxiloc[0][0]; preloc[0][1] = taxiloc[0][1];
					taxiloc[0][0] = nextlocno/MAPSIZE; taxiloc[0][1] = nextlocno%MAPSIZE;
					taxidata.setTaxiLoc(id, taxiloc);
					gui.SetTaxiStatus(id, new Point(taxiloc[0][0], taxiloc[0][1]), 1);
					gv.stay(GVSTAYGAP);
					try{Thread.sleep(DRIVETIME);}catch(Exception e){}
					taxidata.addTaxiTime(id, DRIVETIME);
					log += "- 途经 " + "(" + taxiloc[0][0] + "," + taxiloc[0][1] + ")" + "," + "时刻 " + taxidata.getTaxiTime(id) + "\r\n";
				}
				System.out.println("请求: " + taxidata.getTaxiReq(id).toString() + " : " + "出租车 " + id + " 到达目标位置, 到达时刻: " + taxidata.getTaxiTime(id));
				log += "-------------------------------------------" + "\r\n";
				//上下车
				try{Thread.sleep(DOORTIME);}catch(Exception e){}
				taxidata.addTaxiTime(id, DOORTIME);
				//进入停止状态一秒后再进入服务状态
				taxidata.setTaxiStat(id, "STOPPING");
				gui.SetTaxiStatus(id, new Point(taxiloc[0][0], taxiloc[0][1]), 0);
				gv.stay(GVSTAYGAP);
				try{Thread.sleep(STOPTIME);}catch(Exception e){}
				taxidata.addTaxiTime(id, STOPTIME);
				taxidata.setTaxiStat(id, "WAITING");
				gui.SetTaxiStatus(id, new Point(taxiloc[0][0], taxiloc[0][1]), 2);
				gv.stay(GVSTAYGAP);
				taxidata.resetTaxiReq(id);                  //完成请求
				taxidata.addTaxiCredit(id, 3);
				flag = true;
				if (type == 1){
					histry.add(log);
					//starttime = System.currentTimeMillis();
					detail.writeFile(log);
				}
			}
			/*try{Thread.sleep(DRIVETIME);}catch(Exception e){
				System.out.println("ERROR");
			}*/
		}
	}
	
	/**
	 * @MODIFIES : None;
	 * @EFFECTS : 
	 * for four points around curloc, if connected, then flag set to true;
	 * if flag is true and road flow is lowest, select road;
	 */
	private int[][] branch(int i, int j){
		//分支编号规则
		/*
		 * * 0 *  j
		 * 1 x 2
		 * * 3 *
		 * i
		 * */
		int[] flag = new int[4];
		for (int k = 0 ; k < 4 ; k++){
			switch(k){         //check if connected
				case 0: flag[k] = (i - 1 > 0 && (map[i - 1][j] == 2 || map[i - 1][j] == 3)) ? 1 : 0;break;
				case 1: flag[k] = (j - 1 > 0 && (map[i][j - 1] == 1 || map[i][j - 1] == 3)) ? 1 : 0;break;
				case 2: flag[k] = (j + 1 < 80 && (map[i][j] == 1 || map[i][j] == 3)) ? 1 : 0;break;
				case 3: flag[k] = (i + 1 < 80 && (map[i][j] == 2 || map[i][j] == 3)) ? 1 : 0;break;
			}
		}
		int choose = -1;
		for (int k = 0 ; k < 4 ; k++){          //获取最小值
			if (flag[k] == 0) continue;
			if (choose == -1){ choose = k; continue; }
			int[][] chooseLoc = serializeLoc(choose, i, j);
			int[][] kLoc = serializeLoc(k, i, j);
			int chooseflow = flowmap.getFlow(i, j, chooseLoc[0][0], chooseLoc[0][1]);
			int kflow = flowmap.getFlow(i, j, kLoc[0][0], kLoc[0][1]);
			//System.out.println(chooseflow+ "," +kflow);
			if (chooseflow > kflow) choose = k;
		}
		int[][] chooseLoc = serializeLoc(choose, i, j);
		int chooseflow = flowmap.getFlow(i, j, chooseLoc[0][0], chooseLoc[0][1]);
		for (int k = 0 ; k < 4 ; k++){
			if (flag[k] == 0) continue;
			int[][] kLoc = serializeLoc(k, i, j);
			int kflow = flowmap.getFlow(i, j, kLoc[0][0], kLoc[0][1]);
			if (chooseflow != kflow) flag[k] = 0;
		}
		while (true){            //相同流量随机判断
			choose = (int)(Math.random()*4);
			if (flag[choose] == 1) break;
		}
		return serializeLoc(choose, i , j);
	}
	
	/**
	 * @EFFECTS : initialize taxidata and gui;
	 */
	private void INIT(){
		int[][] _taxiloc = new int[1][2];
		_taxiloc[0][0] = (int)(Math.random()*80);
		_taxiloc[0][1] = (int)(Math.random()*80);
		taxidata.initTaxiCredit(id);
		taxidata.setTaxiLoc(id, _taxiloc);
		taxidata.setTaxiStat(id, "WAITING");
		gui.SetTaxiStatus(id, new Point(_taxiloc[0][0], _taxiloc[0][1]), 2);
		gui.SetTaxiType(id, type);
		gv.stay(GVSTAYGAP);
	}
	
	/**
	 * @EFFECTS : \result = taxiloc;
	 */
	private int[][] serializeLoc(int index, int i, int j){
		int[][] taxiloc = new int[1][2];
		switch(index){
			case 0: taxiloc[0][0] = i - 1; taxiloc[0][1] = j; break;
			case 1: taxiloc[0][0] = i; taxiloc[0][1] = j - 1; break;
			case 2: taxiloc[0][0] = i; taxiloc[0][1] = j + 1; break;
			case 3: taxiloc[0][0] = i + 1; taxiloc[0][1] = j; break;
		}
		return taxiloc;
	}
	/**
	 *     ud
	 *     *
	 * *  *  *       lr
	 *     *
	 */
	private int driveDir(int[][] preloc, int[][] curloc, int[][] nextloc)
	/**
	 * @EFFECTS : identify taxi's drive direction
	 */
	{      //0 - 直行， 1 - 左转， 2 - 右转，3 - 掉头
		if (preloc[0][0] == curloc[0][0]){
			if (preloc[0][1] < curloc[0][1]){
				if (nextloc[0][0] == curloc[0][0]){
					if (nextloc[0][1] > curloc[0][1]) return 0;
					else return 3;
				}
				if (nextloc[0][1] == curloc[0][1]){
					if (nextloc[0][0] > curloc[0][0]) return 2;
					else return 1;
				}
			}else{
				if (nextloc[0][0] == curloc[0][0]){
					if (nextloc[0][1] < curloc[0][1]) return 0;
					else return 3;
				}
				if (nextloc[0][1] == curloc[0][1]){
					if (nextloc[0][0] < curloc[0][0]) return 2;
					else return 1;
				}
			}
		}
		if (preloc[0][1] == curloc[0][1]){
			if (preloc[0][0] < curloc[0][0]){
				if (nextloc[0][0] == curloc[0][0]){
					if (nextloc[0][1] < curloc[0][1]) return 2;
					else return 1;
				}
				if (nextloc[0][1] == curloc[0][1]){
					if (nextloc[0][0] > curloc[0][0]) return 0;
					else return 3;
				}
			}else{
				if (nextloc[0][0] == curloc[0][0]){
					if (nextloc[0][1] < curloc[0][1]) return 1;
					else return 2;
				}
				if (nextloc[0][1] == curloc[0][1]){
					if (nextloc[0][0] > curloc[0][0]) return 3;
					else return 0;
				}
			}
		}
		return -1;
	}
	
	private int roadDir(int[][] curloc, int[][] nextloc)
	/**
	 * @EFFECTS : identify roads' direction
	 */
	{     //0 - lr        1 - ud
		if (curloc[0][0] == nextloc[0][0]) return 0;
		if (curloc[0][1] == nextloc[0][1]) return 1;
		return -1;
	}
}