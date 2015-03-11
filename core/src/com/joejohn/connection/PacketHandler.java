package com.joejohn.connection;

public interface PacketHandler {

    public void playerPacketHandler(PlayerPacket packet);

    public void lobbyPacketHandler(LobbyPacket packet);

    public void peerPacketHandler(PeerPacket packet);

    public void clientPacketHandler(ClientPacket packet);


}
