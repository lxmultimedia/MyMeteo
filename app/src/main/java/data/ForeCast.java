package data;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by dev_z on 09.04.2017.
 */

public class ForeCast  implements JSONPopulator {
    private Day d0;
    private Day d1;
    private Day d2;
    private Day d3;
    private Day d4;
    private Day d5;
    private Day d6;
    private Day d7;
    private Day d8;
    private Day d9;

    public Day getDay0() {
        return d0;
    }

    public Day getDay1() {
        return d1;
    }

    public Day getDay2() {
        return d2;
    }

    public Day getDay3() {
        return d3;
    }

    public Day getDay4() {
        return d4;
    }

    public Day getDay5() {
        return d5;
    }

    public Day getDay6() {
        return d6;
    }

    public Day getDay7() {
        return d7;
    }

    public Day getDay8() {
        return d8;
    }

    public Day getDay9() {
        return d9;
    }

    public void populate(JSONArray data) {
        d0 = new Day();
        d1 = new Day();
        d2 = new Day();
        d3 = new Day();
        d4 = new Day();
        d5 = new Day();
        d6 = new Day();
        d7 = new Day();
        d8 = new Day();
        d9 = new Day();
        d0.populate(data.optJSONObject(0));
        d1.populate(data.optJSONObject(1));
        d2.populate(data.optJSONObject(2));
        d3.populate(data.optJSONObject(3));
        d4.populate(data.optJSONObject(4));
        d5.populate(data.optJSONObject(5));
        d6.populate(data.optJSONObject(6));
        d7.populate(data.optJSONObject(7));
        d8.populate(data.optJSONObject(8));
        d9.populate(data.optJSONObject(9));
    }

    @Override
    public void populate(JSONObject data) {

    }
}
