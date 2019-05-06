package oo.taxi;

import java.util.ArrayList;

public class DistributeSystem implements Runnable
/**
 * @OVERVIEW : 
 * this class can create a time window and log all taxis driving in the range of req;
 * after the time window, system auto select a taxi with highest credit and if more than one taxis 
 * have highest credit, system will select the taxi which is closest the the req;
 */
{

	private Req awaitReq;
	private TaxiData taxidata;
	private int WINDOWTIME;
	private int TAXINUM;
	private ArrayList<Integer> taxiIdList;
	private static final int SLEEPGAP = 100;
	private String log;
	private long SYSTEMTIME;
	private SafeFile file;
	
	public DistributeSystem(Req _awaitReq, TaxiData _taxidata, int _taxinum, int _WINDOWTIME, SafeFile _file, long _systemtime)
	{
		awaitReq = _awaitReq;
		taxidata = _taxidata;
		TAXINUM = _taxinum;
		WINDOWTIME = _WINDOWTIME;
		taxiIdList = new ArrayList<Integer>();
		log = "请求发出时刻: " + awaitReq.getTime() + "\r\n" + 
				"请求发出位置: " + "(" + awaitReq.getSrcLoc()[0][0] + "," + awaitReq.getSrcLoc()[0][1] + ")" + "\r\n" +
				"请求目标位置: " + "(" + awaitReq.getDstLoc()[0][0] + "," + awaitReq.getDstLoc()[0][1] + ")" + "\r\n";
		SYSTEMTIME = _systemtime;
		file = _file;
	}
	
	public boolean repOK(){
		if (taxidata == null || TAXINUM != 100) return false;
		return true;
	}
	
	/**
	 * 能够实现根据距离和信誉将请求分配给合适的出租车;
	 * @REQUIRES : None;
	 * @MODIFIES : taxidata;
	 * @EFFECTS : 
	 * (\exist Taxi taxi ; taxiloc is in range && taxicredit is highest && taxiloc is closest) ==> taxidata.setTaxiReq(taxiid, Req);
	 */
	public void run(){           
		long startTime = System.currentTimeMillis();
		int i = awaitReq.getSrcLoc()[0][0];
		int j = awaitReq.getSrcLoc()[0][1];
		int choosen = -1;
		while (System.currentTimeMillis() - startTime <= WINDOWTIME){
			for (int k = 0 ; k < TAXINUM ; k++){
				int[][] taxiloc = taxidata.getTaxiLoc(k);
				String taxistat = taxidata.getTaxiStat(k);
				if (!isAdded(taxiIdList, k) && 
					taxistat.equals("WAITING") && 
					(taxiloc[0][0] >= i - 2 && taxiloc[0][0] <= i + 2) && 
					(taxiloc[0][1] >= j - 2 && taxiloc[0][1] <= j + 2)) taxiIdList.add(k);
			}
			try{Thread.sleep(SLEEPGAP);}catch(Exception e){}
		}
		log += "参与抢单的所有出租车信息: " + "\r\n";
		for (int k = 0 ; k < taxiIdList.size() ; k++){
			taxidata.addTaxiCredit(taxiIdList.get(k), 1);
			int[][] taxiloc = taxidata.getTaxiLoc(taxiIdList.get(k));
			int taxiCredit = taxidata.getTaxiCredit(taxiIdList.get(k));
			log += "车辆编号: " + taxiIdList.get(k) + "\r\n";
			log += "车辆位置: " + "(" + taxiloc[0][0] + "," + taxiloc[0][1] + ")" + "\r\n";
			log += "车辆信誉: " + taxiCredit + "\r\n";
			if (choosen == -1) choosen = taxiIdList.get(k);
			else{
				if (taxiCredit > taxidata.getTaxiCredit(choosen)) choosen = taxiIdList.get(k);
				else if (taxiCredit == taxidata.getTaxiCredit(choosen)){
					int[][] Loc_cho = taxidata.getTaxiLoc(choosen);
					if (Math.pow(taxiloc[0][0] - i, 2) + Math.pow(taxiloc[0][1] - j, 2) < Math.pow(Loc_cho[0][0] - i, 2) + Math.pow(Loc_cho[0][1] - j, 2)) choosen = taxiIdList.get(k);
				}
			}
		}
		if (choosen == -1) {
			log += "当前无车响应该请求" + "\r\n";
			file.writeFile(log);
			System.out.println("无出租车响应该请求: " + awaitReq.toString());
		}
		else {
			int[][] taxiloc = taxidata.getTaxiLoc(choosen);
			long sendtime = (System.currentTimeMillis() - SYSTEMTIME)/100;
			log += "被派单出租车信息: " + "\r\n";
			log += "车辆编号: " + choosen + "\r\n";
			log += "派单时车辆坐标: " + "(" + taxiloc[0][0] + "," + taxiloc[0][1] + ")" + "\r\n";
			log += "派单时刻: " + sendtime + "\r\n";
			file.writeFile(log);
			System.out.println("出租车 " + choosen + " 已经接单");
			taxidata.setTaxiReq(choosen, awaitReq);
		}
	}
	
	/**
	 * 能够判断element是否在List数组中
	 * @REQUIRES : None;
	 * @MODIFIES : None;
	 * @EFFECTS :
	 * (\exist int k ; List[k] == element) ==> \result = true;
	 * (\all int k ; List[k] != element) ==> \result = false;
	 */
	private boolean isAdded(ArrayList<Integer> List, int element){
		for (int i = 0 ; i < List.size() ; i++){
			if (List.get(i).equals(element)) return true;
		}
		return false;
	}
}
