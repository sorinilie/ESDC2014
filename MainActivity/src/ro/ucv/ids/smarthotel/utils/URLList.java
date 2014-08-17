package ro.ucv.ids.smarthotel.utils;

public class URLList {
	
	private String URLServer;
	private String URLLight;
	private String URLspon;
	private String URLspoff;
	private String URLspsts;
	private String URLmon;
	private String URLopndoor;
	
	public URLList (String galileoIP) {
		this.URLServer = galileoIP + "/BrainServer1";
		 this.URLLight = URLServer + "/setlight.jsp";
		 this.URLspon = URLServer + "/spon.jsp";
		 this.URLspoff = URLServer + "/spoff.jsp";
		 this.URLspsts= URLServer + "/spstatus.jsp";
		 this.URLmon = URLServer + "/spmon.jsp";
		 this.URLopndoor = URLServer + "/dooropn.jsp";
	}

	public String getURL_Light() {
		return URLLight;
	}

	public String getURL_spon() {
		return URLspon;
	}

	public String getURL_spoff() {
		return URLspoff;
	}

	public String getURL_spsts() {
		return URLspsts;
	}

	public String getURL_mon() {
		return URLmon;
	}

	public String getURL_opndoor() {
		return URLopndoor;
	}

}
