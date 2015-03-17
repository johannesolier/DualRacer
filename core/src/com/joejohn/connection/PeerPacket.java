package com.joejohn.connection;

import java.io.Serializable;
import java.net.InetAddress;

public class PeerPacket implements Serializable {

	private InetAddress dstAddress;
	private int dstPort;
	
	public PeerPacket(InetAddress dstAddress, int dstPort) {
		this.dstAddress = dstAddress;
		this.dstPort = dstPort;
	}
	
	/**
	 * Get InetAddress.
	 * @return Returns InetAddress.
	 */
	public InetAddress getInetAddress() {
		return this.dstAddress;
	}
	
	/**
	 * Get port reachable on the device.
	 * @return
	 */
	public int getPort() {
		return this.dstPort;
	}
	
}
