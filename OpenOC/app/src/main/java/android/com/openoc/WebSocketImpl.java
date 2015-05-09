package android.com.openoc;


import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketImpl {
    private WebSocketClient mWebSocketClient;
    private ConnMgmt messageHandler;
    private int connected = 0;

    public WebSocketImpl(ConnMgmt handler){
        this.messageHandler = handler;
    }

    public void connectWebSocket(String serverip) {
        URI uri;
        try {
            uri = new URI(serverip);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                connected = 1;
                messageHandler.setWebClientOK(WebSocketImpl.this);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                Log.e("WebSocketImpl", "receive msg:"+s);
                try {
                    JSONObject jsonData = new JSONObject(message);
                    messageHandler.process(jsonData.getString("eventName"), jsonData.optString("data") );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("WebSocketImpl", "Closed " + s);
                connected = 0;
            }

            @Override
            public void onError(Exception e) {
                Log.i("WebSocketImpl", "Error " + e.getMessage());
                e.printStackTrace();
            }
        };
        mWebSocketClient.connect();
    }

    public void sendMessage(String event, JSONObject payload)  {
        JSONObject message = new JSONObject();

        if(connected == 0)
            return;

        try {
            message.put("eventName", event);
            message.put("data", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("WebSocketImpl", "send msg: "+message.toString());
        mWebSocketClient.send(message.toString());
    }
}
