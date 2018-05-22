package oo.taxi;

import java.util.Iterator;

public class TrackTaxi extends Taxi implements Runnable
/**
 * @OVERVIEW: extends Taxi class, construct a new iterator to reach data in histry
 */
{
	public TrackTaxi(int _id, TaxiData _taxidata, TaxiGUI _gui, int[][] _map, Shortpath _shortpath, FlowMap _flowmap, EventManager _manager, SafeFile _detail, TrafficLight[][] _lightGroup, int _type){
		super(_id, _taxidata, _gui, _map, _shortpath, _flowmap, _manager, _detail, _lightGroup, _type);
	}
	
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: Completely uses super class's method;
	 */
	public void run(){
		super.run();
	}
	
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: return new created iterator;
	 * 
	 */
	public Iterator<String> terms() {return new TaxiGen(this);}
	
	private static class TaxiGen implements Iterator<String>{
		private TrackTaxi taxi;
		private int n;
		public TaxiGen(TrackTaxi _taxi){
			taxi = _taxi;
			n = 0;
		}
		/**
		 * @EFFECTS: \result = (n < taxi.histry.size) ? true : false;
		 */
		public synchronized boolean hasNext(){return n < taxi.histry.size();}
		/**
		 * @EFFECTS: \result = (n > 0) ? true : false;
		 */
		public synchronized boolean hasPre(){return n > 0;}
		/**
		 * @EFFECTS: \result = next element of histry, if none elements exist, return "-1"
		 */
		public synchronized String next(){
			for (int e = n ; e < taxi.histry.size() ; e++){
				if (taxi.histry.get(e) != null){
					n = e + 1;
					return new String(taxi.histry.get(e));
				}
			}
			return "-1";
		}
		/**
		 *@EFFECTS: \result = previous element of histry, if none exists, return "-1";
		 */
		public synchronized String previous(){
			for (int e = n ; e >= 0 ; e--){
				if (taxi.histry.get(e) != null){
					n = e - 1;
					return new String(taxi.histry.get(e));
				}
			}
			return "-1";
		}
	}
}
