package oo.taxi;

import java.util.Iterator;

public class TestCode {
	private TrackTaxi[] tracktaxi;
	private static final int taxiId = 1;
	private Iterator<String> ite;
	public TestCode(TrackTaxi[] _tracktaxi){
		tracktaxi = _tracktaxi;
		ite = tracktaxi[taxiId].terms();
	}
	public synchronized void reachdata(){
		while (ite.hasNext()){
			System.out.println(ite.next());
		}
	}
}
