package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import src.Individual;

/**
 * Generate a random List<Individual> and in each generation, modify population using various selection algorithms
 * and approach an optimal solution to the knapsack problem.
 */
public class Population {

    List<Individual> population;
    Knapsack k;
    int poolSize;
    double carryFit;
    double carryWeak;
    int nMutations;

    Population(Knapsack k, int poolSize, double carryFit, double carryWeak, int nMut) {
        this.k = k;
        this.poolSize = poolSize;
        this.carryWeak = carryWeak;
        this.nMutations = nMut;
        this.population = new ArrayList<Individual>();
    }

    /**
     * Create each individual by randomly selecting 0 or 1 for each item
     * @param genomeSize size of an individuals genome
     */
    void initialize(int genomeSize) {

        Random r = new Random();
        
        for(int i = 0; i < poolSize; i++) {
            int[] b = new int[genomeSize];

            for(int j = 0; j < genomeSize; j++) {
                b[j] = r.nextInt(2);
            }

            population.add(new Individual(b, fitness(b)));
        }
    }

    /**
     * Best Fit Breeding selection algorithm: Breed every other individual with the top performing individual
     * @return List<Individual>: next generation of population
     */
    List<Individual> bestFitBreedingSelection() {

        // find best fit genome
        Individual bestFit = this.getTopFitness();

        // Breed all individuals with bestFit
        List<Individual> nextGen = new ArrayList<Individual>();
        for(int i = 0; i < poolSize; i++) {
            nextGen.add(crossover(bestFit, population.get(i)));
        }

        return nextGen;
    }

    /**
     * Roulete Wheel Selection Algorithm: Generate n of each individual, where n='individuals fitness proportinal to the population', then randomly breed from the wheel
     * @param n: number of top performers to immediately save for next generation
     * @return List<Individual>: next generation of population
     */
    List<Individual> rouleteWheelSelection(double n) {

        Random r = new Random();

        List<Individual> nextGen = new ArrayList<Individual>();

        // Sort population and generate n of each individual
        population.sort((new Individual()).new SortByFitness());
        for(int i = 0; i < (int) (poolSize * n); i++) {
            nextGen.add(population.get(i));
        }

        // Find total fitness
        double totalFit = 1.0;
        for(Individual b: population) {
            totalFit += fitness(b.genome);
        }

        // build roulette wheel
        List<Individual> wheel = new ArrayList<Individual>();

        for(Individual b: population) {
            double f = fitness(b.genome);

            if(f != 0.0) {
                int prop = (int) (totalFit / f);
            
                for(int i = 0; i < prop; i++) {
                    wheel.add(b);
                }
            } else {
                // Keep Individualtic diversity in by including some 0 fitness Individuals
                int prop = (int) (totalFit * carryWeak) - 1;
                wheel.add(b);
                
                for(int i = 0; i < prop; i++) {
                    wheel.add(b);
                }
            }
        }

        // mate
        for(int i = 0; i < poolSize - (int) (poolSize * n); i++) {
            int aIndex = r.nextInt(wheel.size());
            int bIndex = r.nextInt(wheel.size());

            while(bIndex == aIndex) {
                bIndex = r.nextInt(wheel.size());
            }

            nextGen.add(crossover(wheel.get(aIndex), wheel.get(bIndex)));
        }

        return nextGen;
    }

    /**
     * Given two individuals, select a psuedo-random halfway point and splice the two genomes, then mutate childs genome
     * @param a Individual: parent a
     * @param b Individual: parent b
     * @return Individual: child
     */
    Individual crossover(Individual a, Individual b) {
        Random r = new Random();

        int split = (int) (a.genome.length * 0.2) + r.nextInt((int) (a.genome.length * 0.6));
        int[] child = new int[a.length];

        // splice parents at split
        child = Arrays.copyOfRange(a.genome, 0, split);
        child = (int[]) Stream.concat(Arrays.stream(child).boxed(), Arrays.stream(Arrays.copyOfRange(b.genome, split, b.length)).boxed())
                              .mapToInt(Integer::intValue).toArray();
                        
        child = mutate(child);

        return new Individual(child, fitness(child));
    }

    /**
     * Randomly flip n genes for more genetic diversity in population
     * @param child: genome being modified
     * @return int[] genome
     */
    int[] mutate(int[] child) {
        Random r = new Random();

        for(int i = 0; i < nMutations; i++) {
            if(r.nextBoolean()) {
                int index = r.nextInt(child.length);
                child[index] = child[index] == 1 ? 0 : 1;
            }
        }

        return child;
    }

    /**
     * Compute the fitness (strength as an individual) of a genome
     * @param genome int[] genetic sequence of individual (items selected from knapsack)
     * @return double: value of genome or 0 if overweight
     */
    double fitness(int[] genome) {
        double value = getValue(genome);
        double weight = getWeight(genome);

        if(weight > k.getCapacity()) {
            return 0.0;
        }

        return value;
    }

    /**
     * Aggregate weights from items genome selected
     * @param genome: int[]
     * @return float: total weight of genome
     */
    double getWeight(int[] genome) {

        double weight = 0;

        for(int i = 0; i < genome.length; i++) {
            if(genome[i] == 1) {
                Item it = Knapsack.getItem(i);
                weight += it.getWeight();
            }
        }

        return weight;
    }

    /**
     * Aggregate values from items genome selected
     * @param genome: int[]
     * @return float: total value of genome
     */
    double getValue(int[] genome) {

        double value = 0;

        for(int i = 0; i < genome.length; i++) {
            if(genome[i] == 1) {
                Item it = Knapsack.getItem(i);
                value += it.getValue();
            }
        }

        return value;
    }

    /**
     * Find the best fit individual from a population
     * @return Individual
     */
    Individual getTopFitness() {

        // Sort population, return final (best) individual
        population.sort((new Individual()).new SortByFitness());
        return population.get(population.size() - 1);
    }

    /**
     * Overwrote toString to print individuals more cleanly
     */
    @Override
    public String toString() {
        String out = "";
        for(Individual b: population) {
            out += String.format("%s %f %f\n", Arrays.toString(b.genome), fitness(b.genome), getWeight(b.genome));
        }
        return out;
    }

    /**
     * Iterate across n generations, using various selection algorithms
     * @param pop List<Individual>: population
     * @param generations int: how many generations to iterate over
     */
    void runGenerations(Population pop, int generations) {
        for(int i = 0; i < generations; i++) {
            // List<Individual> nextGen = pop.bestFitBreedingSelection();
            List<Individual> nextGen = pop.rouleteWheelSelection(carryFit);

            this.population = new ArrayList<Individual>(nextGen);
        }

        Individual topId = pop.getTopFitness();
        System.out.println(Arrays.toString(topId.genome) + " " + topId.fitness + " " + pop.getWeight(topId.genome));
    }

    public static void main(String[] args) {

        // Read in command line arguments
        int genomeSize = Integer.parseInt(args[0]);
        int populationSize = Integer.parseInt(args[1]);
        int numGenerations = Integer.parseInt(args[2]);
        double carryFit = Double.parseDouble(args[3]);
        double carryWeak = Double.parseDouble(args[4]);
        int numMutations = Integer.parseInt(args[5]);

        // Cleanly assign values used for Random numbers later
        double minVal = 0.1;
        double maxVal = 0.9;
        double minWeight = 0.1;
        double maxWeight = 0.9;
        double maxCapacity = 2;

        Knapsack k = new Knapsack(maxCapacity);
        Random r = new Random();

        // Fill knapsack with random items
        for(int i = 0; i < genomeSize; i++) {
            Item it = new Item(minVal + (maxVal - minVal) * r.nextDouble(), minWeight + (maxWeight - minWeight) * r.nextDouble());
            k.addItem(it);
        }
        System.out.println(k);

        // Initialize population
        Population pop = new Population(k, populationSize, carryFit, carryWeak, numMutations);
        pop.initialize(genomeSize);
        System.out.println(pop);

        // Find best genome
        pop.runGenerations(pop, numGenerations);
    }
}
