package ch.ffhs.esa.mymeteo.data;

import org.json.JSONObject;

/**
 * Created by dev_z on 09.04.2017.
 */

public class Channel implements JSONPopulator {
    private Item item;
    private Units units;

    public Item getItem() {
        return item;
    }

    public Units getUnits() {
        return units;
    }

    @Override
    public void populate(JSONObject data) {
        units = new Units();
        units.populate(data.optJSONObject("units"));

        item = new Item();
        item.populate(data.optJSONObject("item"));

    }
}
