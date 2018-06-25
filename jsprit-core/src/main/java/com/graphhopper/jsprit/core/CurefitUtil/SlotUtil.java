package com.graphhopper.jsprit.core.CurefitUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class SlotUtil {

    static HashMap<String, ArrayList<String> > slots = new HashMap<String, ArrayList<String>>(){{
        this.put("BREAKFAST", new ArrayList<String>(){{
            this.add("B1");
            this.add("BREAKFAST1");
            this.add("BREAKFAST2");
            this.add("BREAKFAST3");
            this.add("KIOSK_BREAKFAST1");
        }});

        this.put("LUNCH", new ArrayList<String>(){{
            this.add("LUNCH2");
            this.add("LUNCH4");
            this.add("LUNCH5");
            this.add("L4");
            this.add("KIOSK_LUNCH");
        }});

        this.put("DINNER", new ArrayList<String>(){{
            this.add("DINNER1");
            this.add("DINNER2");
            this.add("DINNER3");
            this.add("DINNER4");
            this.add("KIOSK_DINNER");
        }});

        this.put("SNACKS", new ArrayList<String>(){{
            this.add("SNACKS1");
            this.add("SNACKS2");
            this.add("SNACKS3");
            this.add("KIOSK_SNACKS");
        }});
    }};
    public SlotUtil() {
    }

    public static ArrayList<String> getSlots(String mealSlot) {
        return slots.get(mealSlot);
    }
}
