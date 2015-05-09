package android.com.openoc;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnMgmt {
    private WebSocketImpl webClient;
    private String localId;
    private List<String> conns = new ArrayList<String>();
    private HashMap<String, SignalingHandler> commandMap = new HashMap<String, SignalingHandler>();
    P2PVideo video1;
    public ConnMgmt(P2PVideo video, String url){
        webClient = new WebSocketImpl( ConnMgmt.this);
        webClient.connectWebSocket(url);
        video1 = video;
    }

    public void registerHandler(String cmd, SignalingHandler handler){
        commandMap.put(cmd, handler);
    }

    public void setWebClientOK(WebSocketImpl client){
        start();
    }

    public void process(String event, String data){
        try {
            if(data == null)
                return;

            JSONObject jsonData = new JSONObject(data);
            if(event.equals("_peers"))
                add_existing_peers(jsonData);
            else if(event.equals("_new_peer"))
                add_new_peer(jsonData);
            else if(event.equals("_remove_peer"))
                remove_peer(jsonData);
            else
                commandMap.get(event).execute(jsonData.getString("socketId"), jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void add_new_peer(JSONObject jsonData) throws JSONException {
        conns.add(jsonData.getString("socketId"));
    }

    private void receive_ice(JSONObject jsonData) throws JSONException {

    }

    private void remove_peer(JSONObject jsonData) throws JSONException {

    }

    private void recive_answer(JSONObject jsonData) throws JSONException {

    }

    private void add_existing_peers(JSONObject jsonData) throws JSONException {
        int i = 0;
        localId = jsonData.getString("you");
        conns.clear();
        JSONArray jsonArray = jsonData.getJSONArray("connections");
        for (i = 0; i < jsonArray.length(); i++) {
            conns.add(jsonArray.optString(i));
            commandMap.get("_peers").execute(jsonArray.optString(i), null);
        }
    }

    public void start(){
        joinGroup();
        video1.start();
    }

    public void joinGroup(){
        JSONObject payload = new JSONObject();
        try {
            payload.put("room", "");
            payload.put("app", "mobile");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webClient.sendMessage("__join", payload);
    }

    public void sendoffer(SessionDescription sdp){
        JSONObject payload = new JSONObject();
        try {
            payload.put("sdp", sdp.description);
            payload.put("socketId", localId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webClient.sendMessage("__offer", payload);
    }

    public void sendanswer(SessionDescription sdp){
        JSONObject payload = new JSONObject();
        try {
            payload.put("sdp", sdp.description);
            payload.put("socketId", localId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webClient.sendMessage("__answer", payload);
    }

    public void sendcandidate(String peerid, IceCandidate candidate){
        JSONObject payload = new JSONObject();
        try {
            payload.put("label", candidate.sdpMLineIndex);
            payload.put("socketId", peerid);
            payload.put("id", candidate.sdpMid);
            payload.put("candidate", candidate.sdp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webClient.sendMessage("__ice_candidate", payload);
    }

    public void sendMessage(String event, JSONObject payload){
        webClient.sendMessage(event, payload);
    }

    public void sendSDP(String event,String peerid, SessionDescription sdp){
        JSONObject payload = new JSONObject();
        JSONObject jsonsdp = new JSONObject();
        try {
            jsonsdp.put("sdp", sdp.description);
            jsonsdp.put("type", sdp.type.canonicalForm());
            payload.put("sdp", jsonsdp);
            payload.put("socketId", peerid);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        event = "__"+event;

        webClient.sendMessage(event, payload);
    }
}




