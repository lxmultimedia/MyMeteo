package ch.ffhs.esa.mymeteo.data;

import org.json.JSONObject;

/**
 * Created by dev_z on 09.04.2017.
 */

public class Item  implements JSONPopulator {
    private Condition condition;
    private ForeCast forecast;

    public Condition getCondition() {
        return condition;
    }

    public ForeCast getForeCast() {
        return forecast;
    }

    @Override
    public void populate(JSONObject data) {
        condition = new Condition();
        condition.populate(data.optJSONObject("condition"));
        forecast = new ForeCast();
        forecast.populate(data.optJSONArray("forecast"));
    }
}
