package android.com.openoc;

import org.json.JSONException;
import org.json.JSONObject;


public interface SignalingHandler {
    void execute(String peerId, JSONObject payload) throws JSONException;
}
