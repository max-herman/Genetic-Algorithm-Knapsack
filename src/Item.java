package src;

/**
 * Struct containing weight,value for an item in a knapsack
 */
public class Item {
    double value;
    double weight;

    Item(double value, double weight) {
        this.value = value;
        this.weight = weight;
    }

    Item(Item it) {
        this.value = it.getValue();
        this.weight = it.getWeight();
    }

    double getWeight() {
        return weight;
    }

    double getValue() {
        return value;
    }
}
