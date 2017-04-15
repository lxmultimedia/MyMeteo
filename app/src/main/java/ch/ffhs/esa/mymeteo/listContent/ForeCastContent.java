package ch.ffhs.esa.mymeteo.listContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample icon for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ForeCastContent {

    public static final List<ForeCastItem> ITEMS = new ArrayList<ForeCastItem>();

    public static final Map<String, ForeCastItem> ITEM_MAP = new HashMap<String, ForeCastItem>();

    // 9 days in yahoo forecast
    private static final int COUNT = 9;

    public static void addItem(ForeCastItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore label information here.");
        }
        return builder.toString();
    }

    public static void clear() {
        ForeCastContent.ITEM_MAP.clear();
        ForeCastContent.ITEMS.clear();
    }

    /**
     * Item Class
     */
    public static class ForeCastItem {
        public final String id;
        public final String icon;
        public final String label;

        public ForeCastItem(String id, String icon, String label) {
            this.id = id;
            this.icon = icon;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
