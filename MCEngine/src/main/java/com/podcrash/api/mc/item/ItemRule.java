package com.podcrash.api.mc.item;

/**
 * Will this be used..?
 */
public enum ItemRule {
    PICKUP_IGNORE(0),
    PICKUP_TARGET(1);

    private int id;
    ItemRule(int id) {
        this.id = id;
    }
}
