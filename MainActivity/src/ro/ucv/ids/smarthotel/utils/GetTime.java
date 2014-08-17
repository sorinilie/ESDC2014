package ro.ucv.ids.smarthotel.utils;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.net.time.TimeTCPClient;


public final class GetTime {
  
  public static Date currentDate;

    public static long ReturnTime() {
        try {
            TimeTCPClient client = new TimeTCPClient();
            try {
                client.setDefaultTimeout(60000);
                client.connect("nist.netservicesgroup.com");
                currentDate = client.getDate();
                return currentDate.getTime()/1000;
            } finally {
                client.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}