package src;

import java.util.ArrayList;
import java.util.List;

/**
 * Struct containing all possible items an Individual genome can represent
 */
public class Knapsack {
    double capacity;
    static List<Item> items = new ArrayList<Item>();

    Knapsack(double c) {
        capacity = c;
    }

    double getCapacity() {
        return capacity;
    }

    void addItem(Item it) {
        items.add(it);
    }

    static Item getItem(int index) {
        return items.get(index);
    }

    /**
     * Overwrote toString to make printing items more convenient
     */
    @Override
    public String toString() {
        String out = "";
        for(Item i: items) {
            out += "Value: " + i.getValue() + ", Weight: " + i.getWeight() + "\n";
        }

        return out;
    }
}
