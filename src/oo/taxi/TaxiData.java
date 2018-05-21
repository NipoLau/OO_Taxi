package oo.taxi;

import java.util.ArrayList;

public class TaxiData
/**
 * @OVERVIEW : 
 * stores all taxi's data and keep it thread-safe
 */
{
	private String[] taxistat;/*WAITING SERVING STOPPING*/
	private ArrayList<int[][]> taxiloc;
	private int[] taxicredit;
	private Req[] taxiReq;
	private boolean[] forceStatus;
	private long[] taxitime;
	
	public TaxiData(int TAXINUM){
		taxistat = new String[TAXINUM];
		taxiloc = new ArrayList<int[][]>();
		taxicredit = new int[TAXINUM];
		taxiReq = new Req[TAXINUM];
		forceStatus = new boolean[TAXINUM]; for (int i = 0 ; i < TAXINUM ; i++) forceStatus[i] = false;
		taxitime = new long[TAXINUM];
	}
	
	public boolean repOK(){
		try{
			for (int i = 0 ; i < 100 ; i++){
				if ((taxistat[i].equals("WAITING") && taxistat[i].equals("STOPPING") && taxistat[i].equals("SERVING")) || 
						taxicredit[i] < 0 || taxitime[i] < 0) return false;
			}
		}catch(Exception e){return false;}
		return true;
	}
	
	/**
	 * @EFFECTS : settaxistat;
	 */
	public synchronized void setTaxiStat(int taxiId, String _taxistat){
		taxistat[taxiId] = _taxistat;
	}
	
	/**
	 * @EFFECTS : \result = taxistat[taxiId];
	 */
	public synchronized String getTaxiStat(int taxiId){
		return taxistat[taxiId];
	}
	
	/**
	 * @EFFECTS : settaxiloc;
	 */
	public synchronized void setTaxiLoc(int taxiId, int[][] _taxiloc){
		if (taxiloc.size() <= taxiId) taxiloc.add(_taxiloc);
		else taxiloc.set(taxiId, _taxiloc);
	}
	
	/**
	 * @EFFECTS : \result = taxilo[taxiId];
	 */
	public synchronized int[][] getTaxiLoc(int taxiId){
		return taxiloc.get(taxiId);
	}
	
	/**
	 * @EFFECTS : set taxi credit;
	 */
	public synchronized void initTaxiCredit(int taxiId){
		taxicredit[taxiId] = 0;
	}
	
	/**
	 * @EFFECTS : taxicredit[taxiId]++;
	 */
	public synchronized void addTaxiCredit(int taxiId, int _taxicredit){
		taxicredit[taxiId] += _taxicredit;
	}
	
	/**
	 * @EFFECTS : \result = taxicredit[taxiId];
	 */
	public synchronized int getTaxiCredit(int taxiId){
		return taxicredit[taxiId];
	}
	
	/**
	 * @EFFECTS : set taxiReq;
	 */
	public synchronized void setTaxiReq(int taxiId, Req _req){
		taxiReq[taxiId] = _req;
	}
	
	/**
	 * @EFFECTS : \result = taxiReq[taxiId];
	 */
	public synchronized Req getTaxiReq(int taxiId){
		return taxiReq[taxiId];
	}
	
	/**
	 * @EFFECTS : taxiReq[taxiId] = null;
	 */
	public synchronized void resetTaxiReq(int taxiId){
		taxiReq[taxiId] = null;
	}
	
	/**
	 * @EFFECTS : forceStatus[taxiId] set to true;
	 */
	public synchronized void setforceStatusOn(int taxiId){
		forceStatus[taxiId] = true;
	}
	
	/**
	 * @EFFECTS : forceStatus[taxiId] set to false;
	 */
	public synchronized void setforceStatusOff(int taxiId){
		forceStatus[taxiId] = false;
	}
	
	/**
	 * @EFFECTS : \result = forceStatus[taxiId];
	 */
	public synchronized boolean getforceStatus(int taxiId)
	{
		return forceStatus[taxiId];
	}
	
	public synchronized void addTaxiTime(int taxiId, long time)
	/**
	 * @EFFECTS : taxitime += time
	 */
	{
		taxitime[taxiId] += time;
	}
	
	public synchronized long getTaxiTime(int taxiId)
	/**
	 * @EFFECTS : \result = taxitime[taxiId]
	 */
	{
		return taxitime[taxiId];
	}
}