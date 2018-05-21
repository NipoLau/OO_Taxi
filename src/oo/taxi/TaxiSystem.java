package oo.taxi;

import java.awt.Point;
import java.util.ArrayList;

public class TaxiSystem implements Runnable
/**
 * @OVERVIEW :
 * when receives Req, taxisystem will use distributesystem to choose a taxi
 * to accomplish req
 */
{
	
	private TaxiData taxidata;
	private Reqqueue reqQueue;
	private static final int SLEEPGAP = 100;
	private int TAXINUM;
	private static final int WINDOWTIME = 7500;
	private static final int GVSTAYGAP = 100;
	private TaxiGUI gui;
	private SafeFile file;
	private long SYSTEMTIME;
	
	public TaxiSystem(TaxiData _taxidata, Reqqueue _reqQueue, int _taxinum, TaxiGUI _gui, SafeFile _file, long _systemtime){
		taxidata = _taxidata;
		reqQueue = _reqQueue;
		TAXINUM = _taxinum;
		gui = _gui;
		file = _file;
		SYSTEMTIME = _systemtime;
	}
	
	public boolean repOK(){
		if (reqQueue.size() < 0 || TAXINUM != 100) return false;
		return true;
	}
	
	/**
	 * @EFFECTS : Distribute Req to best taxi;
	 */
	public void run(){
		ArrayList<Req> reqList = new ArrayList<Req>();        //存储待处理请求
		while (true){
			if (reqQueue.size() == 0) continue;
			while (reqQueue.size() > 0) reqList.add(reqQueue.deQueue());
			for (int i = 0 ; i < reqList.size() ; i++) {
				gui.RequestTaxi(new Point(reqList.get(i).getSrcLoc()[0][0],reqList.get(i).getSrcLoc()[0][1]), new Point(reqList.get(i).getDstLoc()[0][0],reqList.get(i).getDstLoc()[0][1]));
				gv.stay(GVSTAYGAP);
				(new Thread(new DistributeSystem(reqList.get(i), taxidata, TAXINUM, WINDOWTIME, file, SYSTEMTIME))).start();
				reqList.remove(i);
				i--;
			}
			try{
				Thread.sleep(SLEEPGAP);
			}catch(Exception e){}
		}
	}
}
