package ro.ucv.ids.smarthotel.utils;

public class TimeFunction {
	
	public static String mergeTimes (long time1, long time2) {
	    String aux1 = null;
	    String aux2 = null;
	    
	    aux1 = Long.toString(time1);
	    aux2 = Long.toString(time2);
	    aux1 = aux1.concat(aux2);
	    return aux1;
	  }

	  public static boolean checkTime (String times, long timeToCheck) {
	    String aux1 = null;
	    String aux2 = null;
	    long time1;
	    long time2;
	    int mid;
	    
	    mid = times.length() / 2;
	    aux1 = times.substring(0,mid);
	    aux2 = times.substring(mid);
	    
	    time1 =Long.valueOf(aux1).longValue();
	    time2 =Long.valueOf(aux2).longValue();
	    
	    if (timeToCheck >= time1 && timeToCheck <= time2) 
	      return true;
	    return false;
	  }
}
