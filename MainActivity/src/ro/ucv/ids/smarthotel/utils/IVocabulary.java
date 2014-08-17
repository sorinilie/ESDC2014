package ro.ucv.ids.smarthotel.utils;

import ro.ucv.ids.smarthotel.connections.URLS;

public interface IVocabulary extends URLS{
	final int SET_DOOR = 1;
	final int SET_LIGHT = 3;
	final int SET_SPON = 4;
	final int SET_SPOFF = 5;
	final int GET_SPSTS = 6;
	final int GET_SPMON = 7;
	final String USER = "user";
	final String PASSWORD = "password";
	final String PREFS_SMARTHOTEL = "smarthotel";
	final String error = "Error";
	
	/*String URLServer = URL;
	String URLLight = URLServer + "/setlight.jsp";
	String URLspon = URLServer + "/spon.jsp";
	String URLspoff = URLServer + "/spoff.jsp";
	String URLspsts= URLServer + "/spstatus.jsp";
	String URLmon = URLServer + "/spmon.jsp";
	String URLopndoor = URLServer + "/dooropn.jsp";*/
}
