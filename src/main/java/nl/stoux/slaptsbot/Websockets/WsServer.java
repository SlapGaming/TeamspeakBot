package nl.stoux.slaptsbot.Websockets;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.stoux.slaptsbot.Globals;
import nl.stoux.slaptsbot.Teamspeak.Models.ServerOverview;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Created by Stoux on 27/10/2015.
 */
public class WsServer extends WebSocketServer {

    private boolean disabled = true;

    private Gson gson;

    public WsServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        try {
            gson = new GsonBuilder().create();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Globals.getEventBus().register(this);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("New socket: " + webSocket.getRemoteSocketAddress().getHostString());
        webSocket.send(gson.toJson(Globals.getOverview()));
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println("Socket left: " + webSocket.getRemoteSocketAddress().getHostString());
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        switch (s.split(" ")[0].toUpperCase()) {
            case "FORCE_UPDATE":
                //TODO: Force an update
                break;
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Subscribe
    public void onNewOverview(ServerOverview overview) {
        String json = gson.toJson(overview);
        for (WebSocket webSocket : connections()) {
            webSocket.send(json);
        }
    }

}
