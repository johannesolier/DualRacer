package com.joejohn.connection;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Config {
	/* These should be not changed */
	public static final int SERVERPORT = 6539;
	public static final int PORT = 5893;
	public static final int TIMEOUT = 1000;
	public static final int NUMBER_OF_LOBBIES = 6;
	public static final int MAX_PLAYERS = 2;
	
	/* These should be changed for your devices */

	public static final String SERVERIP = "192.168.0.12";

	public static byte[] getLocalIPAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						if (inetAddress instanceof Inet4Address) { // fix for Galaxy Nexus. IPv4 is easy to use
							return inetAddress.getAddress();
						}
						//return inetAddress.getHostAddress().toString(); // Galaxy Nexus returns IPv6
					}
				}
			}
		} catch (SocketException ex) {
			//Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
		} catch (NullPointerException ex) {
			//Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
		}
		return null;
	}

	public static String getDottedDecimalIP(byte[] ipAddr) {
		//convert to dotted decimal notation:
		String ipAddrStr = "";
		for (int i=0; i<ipAddr.length; i++) {
			if (i > 0) {
				ipAddrStr += ".";
			}
			ipAddrStr += ipAddr[i]&0xFF;
		}
		return ipAddrStr;
	}



	


}
