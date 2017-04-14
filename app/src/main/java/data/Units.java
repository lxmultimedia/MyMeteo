package data;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by dev_z on 09.04.2017.
 */

public class Units implements JSONPopulator {
    private String temperature;

    public String getTemperature() {
        return temperature;
    }

    @Override
    public void populate(JSONObject data) {
        temperature = data.optString("temperature");
    }

}
