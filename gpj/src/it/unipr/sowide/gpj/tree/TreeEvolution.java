package it.unipr.sowide.gpj.tree;

import it.unipr.sowide.gpj.core.Evolution;
import it.unipr.sowide.gpj.core.breeding.BreedingStrategy;
import it.unipr.sowide.gpj.core.breeding.FillingReproduction;
import it.unipr.sowide.gpj.core.breeding.PartitionedBreeding;
import it.unipr.sowide.gpj.core.breeding.SequentialBreeding;
import it.unipr.sowide.gpj.core.breeding.mating.Mating;
import it.unipr.sowide.gpj.core.breeding.mutation.Mutation;
import it.unipr.sowide.gpj.core.breeding.selection.TournamentSelection;
import it.unipr.sowide.gpj.core.evaluation.PopulationEvaluator;
import it.unipr.sowide.gpj.core.evaluation.SyncParallelPopEvaluator;
import it.unipr.sowide.util.RandomUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/**
 * The evolution of a population of trees.
 *
 * @param <T> the type of the result of the evaluation of the tree
 * @param <C> the type of the "context" in which the evaluation of the tree is
 *            done; it can be used as a way to create a mutable state of
 *            computation as well as a way to pass input values.
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class TreeEvolution<T, C> extends Evolution<Tree<T, C>> {

    /**
     * @param random              the random generator utility
     * @param popSize             the population size
     * @param initialMinHeight    the minimum height of the initially generated
     *                            trees
     * @param initialMaxHeight    the maximum height of the initially generated
     *                            trees
     * @param operationSet        the set of the operations used to build the
     *                            trees
     * @param populationEvaluator a system that updates the fitnesses of the
     *                            individuals
     * @param breedingStrategy    a breeding strategy
     */
    public TreeEvolution(
            RandomUtils random,
            int popSize,
            int initialMinHeight,
            int initialMaxHeight,
            List<Operation<T, C>> operationSet,
            PopulationEvaluator<Tree<T, C>> populationEvaluator,
            BreedingStrategy<Tree<T, C>> breedingStrategy
    ) {
        super(
                new RandomTreeGenerator<>(
                        random,
                        initialMinHeight,
                        initialMaxHeight,
                        operationSet
                ),
                populationEvaluator,
                breedingStrategy,
                random,
                popSize
        );
    }

    /**
     * Evolves a population of trees that tries to approximate the function
     * {@code f(x) = x^2 + x + 1}. When started, press enter to execute 1000
     * generations of the evolution.
     */
    public static void main(String[] argv) {
        Scanner sc = new Scanner(System.in);
        RandomUtils random = new RandomUtils(12L);

        List<Operation<Double, HashMap<String, Double>>> operations = List.of(
                new EphemeralRandomDouble<>(random, -5.0, 5.0),
                new NamedVariable<>("X"),
                new BinaryFunction<>("SUM", Double::sum),
                new BinaryFunction<>("SUB", (x, y) -> x - y),
                new BinaryFunction<>("MUL", (x, y) -> x * y),
                new BinaryFunction<>("DIV", (x, y) -> y == 0.0 ? 1.0 : x / y)
        );

        HashMap<String, Double> context = new HashMap<>();

        Function<Double, Double> functionToApproximate = (x) -> {
            return x * x + x + 1;
        };

        double cxPb = 0.70;
        double mutPb = 0.10;
        double repPb = 0.20;
        TreeEvolution<Double, HashMap<String, Double>>
                evolution = new TreeEvolution<>(
                random,
                1000, 5,
                7,
                operations,
                new SyncParallelPopEvaluator<>(individual -> {
                    var errorIntegral = 0.0;
                    double interval = 0.1;
                    for (double x = -1.0; x <= 1.0; x += interval) {
                        context.put("X", x);
                        errorIntegral += Math.abs(
                                individual.eval(context)
                                        - functionToApproximate.apply(x)
                        ) * interval;
                    }
                    return errorIntegral;
                }),
                new PartitionedBreeding<Tree<Double, HashMap<String, Double>>>(
                ).addPartition(
                        "crossover",
                        new SequentialBreeding<Tree<Double, HashMap<String, Double>>>()
                                .addBreedingPhase(new TournamentSelection<>(7))
                                .addBreedingPhase(new Mating<>(new TreeCrossover<>(15))),
                        cxPb
                ).addPartition(
                        "reproduction",
                        new SequentialBreeding<Tree<Double, HashMap<String, Double>>>()
                                .addBreedingPhase(new TournamentSelection<>(7))
                                .addBreedingPhase(new FillingReproduction<>()),
                        repPb
                ).addPartition(
                        "mutation",
                        new Mutation<>(
                                (evoInterf, individual) -> Tree.headlessChickenMutation(
                                        evoInterf.getRandom(),
                                        individual,
                                        operations,
                                        6,
                                        10
                                )),
                        mutPb
                )
        );

        evolution.addLogbookOutputListener(System.out::println);

        evolution.initialize();

        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                sc.nextLine();
                evolution.evolveSync(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}