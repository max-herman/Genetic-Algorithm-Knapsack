package src;

import java.util.Comparator;

/**
 * Struct containing genetic sequence (binary array representing knapsack contents)
 */
public class Individual{
    int[] genome;
    int length;
    double fitness;

    Individual(int[] genome, double fit) {
        this.genome = genome;
        length = genome.length;
        fitness = fit;
    }

    Individual() {}

    /**
     * Sub-class used to sort population (List<Individual>)
     */
    class SortByFitness implements Comparator<Individual> {
        @Override
        public int compare(Individual o1, Individual o2) {
            return (int) Math.round(Math.round(o1.fitness) - Math.round(o2.fitness));
        }
    }
    

}
