package oo.taxi;

public class EventManager 
/**
 * @OVERVIEW :
 * this class mainly manage the event called ROAD_CHG_EVENT
 * to keep thread-safe
 * other functions will use it accordingly
 */
{
	private static boolean ROAD_CHG_EVENT;
	public EventManager(){
		ROAD_CHG_EVENT = false;
	}
	/**
	 * @MODIFIES : ROAD_CHG_EVENT;
	 * @EFFECTS : 
	 * ROAD_CHG_EVENT = true;
	 */
	public synchronized void eventOn(){
		ROAD_CHG_EVENT = true;
	}
	/**
	 * @MODIFIES : ROAD_CHG_EVENT;
	 * @EFFECTS : 
	 * ROAD_CHG_EVENT = false;
	 */
	public synchronized void eventOff(){
		ROAD_CHG_EVENT = false;
	}
	/**
	 * @EFFECTS : 
	 * return ROAD_CHG_EVENT;
	 */
	public synchronized boolean EventIsOn(){
		return ROAD_CHG_EVENT;
	}
	
	public boolean repOK(){
		return true;
	}
}
