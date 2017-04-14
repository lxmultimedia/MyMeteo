package data;

import org.json.JSONObject;

/**
 * Created by dev_z on 09.04.2017.
 */

public class Day implements JSONPopulator {
    private String code;
    private String day;
    private String text;
    private String tempLow;
    private String tempHigh;

    public String getCode() {
        return code;
    }
    public String getDay() {
        return day;
    }
    public String getText() {
        return text;
    }

    public String getTempLow() {
        return tempLow;
    }

    public String getTempHigh() {
        return tempHigh;
    }


    @Override
    public void populate(JSONObject data) {
        code = data.optString("code");
        day = data.optString("day");
        text = data.optString("text");
        tempLow = data.optString("low");
        tempHigh = data.optString("high");
    }
}
