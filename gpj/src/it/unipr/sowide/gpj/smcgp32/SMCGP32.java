package it.unipr.sowide.gpj.smcgp32;

import it.unipr.sowide.gpj.core.breeding.KillOldIndividuals;
import it.unipr.sowide.gpj.core.evaluation.SyncParallelPopEvaluator;
import it.unipr.sowide.util.Pair;
import it.unipr.sowide.gpj.core.breeding.PreserveIndividuals;
import it.unipr.sowide.gpj.core.breeding.SequentialBreeding;
import it.unipr.sowide.gpj.core.breeding.mating.Mating;
import it.unipr.sowide.gpj.core.breeding.mating.StocasticMateOperator;
import it.unipr.sowide.gpj.core.breeding.mutation.Mutation;
import it.unipr.sowide.gpj.core.breeding.mutation.StocasticMutationOperator;
import it.unipr.sowide.gpj.core.breeding.selection.EliteSelection;
import it.unipr.sowide.gpj.core.breeding.selection.RandomSelection;
import it.unipr.sowide.gpj.core.breeding.selection.TournamentSelection;
import it.unipr.sowide.gpj.tree.*;
import it.unipr.sowide.util.RandomUtils;
import it.unipr.sowide.util.annotations.Namespace;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Contains various utilities used to create a SMCGP-32 evolution.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Namespace
public class SMCGP32 {

    /**
     * Creates a set of operations for the SMCGP-32 techique.
     *
     * @param random a random generator utility
     * @return a list of operations
     */
    public static List<Operation<Integer, SMC32PatternArguments>>
    createOperationSet(RandomUtils random) {
        BinaryFunction<Integer, SMC32PatternArguments> fAND
                = new BinaryFunction<>("AND", (l1, l2) -> l1 & l2);

        BinaryFunction<Integer, SMC32PatternArguments> fOR
                = new BinaryFunction<>("OR", (l1, l2) -> l1 | l2);

        BinaryFunction<Integer, SMC32PatternArguments> fNAND
                = new BinaryFunction<>("NAND", (l1, l2) -> ~(l1 & l2));

        BinaryFunction<Integer, SMC32PatternArguments> fNOR
                = new BinaryFunction<>("NOR", (l1, l2) -> ~(l1 | l2));

        BinaryFunction<Integer, SMC32PatternArguments> fXOR
                = new BinaryFunction<>("XOR", (l1, l2) -> l1 ^ l2);

        UnaryFunction<Integer, SMC32PatternArguments> fNOT
                = new UnaryFunction<>("NOT", (l) -> ~l);

        UnaryFunction<Integer, SMC32PatternArguments> fSHL1
                = new UnaryFunction<>("SHL1", (l) -> (l << 1) | (l >> 31));

        UnaryFunction<Integer, SMC32PatternArguments> fSHL2
                = new UnaryFunction<>("SHL2", (l) -> (l << 2) | (l >> 30));

        UnaryFunction<Integer, SMC32PatternArguments> fSHL4
                = new UnaryFunction<>("SHL4", (l) -> (l << 4) | (l >> 28));

        UnaryFunction<Integer, SMC32PatternArguments> fSHR1
                = new UnaryFunction<>("SHR1", (l) -> (l >> 1) | (l << 31));

        UnaryFunction<Integer, SMC32PatternArguments> fSHR2
                = new UnaryFunction<>("SHR2", (l) -> (l >> 2) | (l << 30));

        UnaryFunction<Integer, SMC32PatternArguments> fSHR4
                = new UnaryFunction<>("SHR4", (l) -> (l >> 4) | (l << 28));

        Constant<Integer, SMC32PatternArguments> f0
                = new Constant<>(0);

        Constant<Integer, SMC32PatternArguments> f1
                = new Constant<>(1);

        ContextDependentTerminal<Integer, SMC32PatternArguments>
                fPAT1 = new ContextDependentTerminal<>(
                "PAT1", SMC32PatternArguments::getPat1
        );

        ContextDependentTerminal<Integer, SMC32PatternArguments>
                fPAT2 = new ContextDependentTerminal<>(
                "PAT2", SMC32PatternArguments::getPat2
        );

        ContextDependentTerminal<Integer, SMC32PatternArguments>
                fPAT3 = new ContextDependentTerminal<>(
                "PAT3", SMC32PatternArguments::getPat3
        );

        ContextDependentTerminal<Integer, SMC32PatternArguments>
                fPAT4 = new ContextDependentTerminal<>(
                "PAT4", SMC32PatternArguments::getPat4
        );

        LateInitConstant<Integer, SMC32PatternArguments> fERC
                = new LateInitConstant<>(
                () -> (int) Math.abs(random.nextLong())
        );

        LateInitConstant<Integer, SMC32PatternArguments> fERC16
                = new LateInitConstant<>(
                () -> (int) (random.nextDouble() * 16.0)
        );

        return List.of(
                fNAND,
                fNOR,
                fXOR,
                fOR,
                fAND,
                fNOT,
                fSHR1,
                fSHL1,
                fSHR2,
                fSHL2,
                fSHR4,
                fSHL4,
                f0,
                f1,
                fPAT1,
                fPAT2,
                fPAT3,
                fPAT4,
                fERC,
                fERC16
        );

    }


    public static final int N_CLASSES = 10;
    public static final int N_BIT = 32;
    public static final double STARTING_FITNESS = 10000000000.0;

    /**
     * The chosen class which this evolution is training classifiers for
     */
    public static final int selectedClassifier = 0;


    public static final double maxFitness = 10.0;

    /**
     * Computes the fitness of a tree in the context of a SMCGP-32 evolution
     *
     * @param individual the tree whose fitness is being computed
     * @param data       the dataset used to test the individual's ability to
     *                   classify correctly
     * @return the fitness
     */
    public static double fitnessSMCTree(
            Tree<Integer, SMC32PatternArguments> individual,
            SMCGP32DataSet data
    ) {
        int[][] freq = new int[N_BIT][N_CLASSES];
        int[] frtot = new int[N_BIT];
        int[] indmin = new int[N_CLASSES];

        double[] fmin = new double[N_CLASSES];

        for (int i = 0; i < N_CLASSES; i++) {
            fmin[i] = STARTING_FITNESS;
        }

        int ind_hits = 0;
        double ind_r_fitness;

        SMC32PatternArguments c = new SMC32PatternArguments();

        for (int i = 0; i < data.size(); i++) {
            c.setPat1(data.getPattern1(i));
            c.setPat2(data.getPattern2(i));
            c.setPat3(data.getPattern3(i));
            c.setPat4(data.getPattern4(i));

            int index = data.getClass(i);

            int risp = individual.eval(c);

            for (int j = 0; j < N_BIT; j++) {
                if ((risp & (1 << j)) != 0) {
                    freq[j][index]++;
                    frtot[j]++;
                }
            }
        }

        for (int i = 0; i < N_BIT; i++) {
            int fp = frtot[i] - freq[i][selectedClassifier];
            int fn = data.getCaseCountForClass(selectedClassifier)
                    - freq[i][selectedClassifier];
            double fit2 = fp * fp + fn * fn;
            if (fit2 < fmin[selectedClassifier]) {
                fmin[selectedClassifier] = fit2;
                indmin[selectedClassifier] = i;
            }
        }

        ind_hits += freq[indmin[selectedClassifier]][selectedClassifier];

        ind_r_fitness = fmin[selectedClassifier];

        ind_r_fitness += fmin[selectedClassifier];

        ind_r_fitness *= 50.0;

        ind_r_fitness = Math.sqrt(ind_r_fitness / (data.size() * data.size()));

        ind_r_fitness = ind_r_fitness + 0.000001 * individual.size();

        double ind_s_fitness = ind_r_fitness / maxFitness;
        double ind_a_fitness = 1.0 / (1.0 + ind_s_fitness);

        return ind_r_fitness;
    }


    /**
     * Evaluates a classifier
     *
     * @param classifier the classifier being evaluated
     * @param clas       the class the classifier tries to classify
     * @param testSet    the dataset used as test set
     * @return an object with the results
     */
    public static SMCGP32SimpleEvaluation evaluateClassifier(
            Tree<Integer, SMC32PatternArguments> classifier,
            int clas,
            SMCGP32DataSet testSet
    ) {
        int[] tp = new int[N_BIT];
        int[] tn = new int[N_BIT];
        int[] fp = new int[N_BIT];
        int[] fn = new int[N_BIT];

        int pdata = 0;
        int ndata = 0;

        SMC32PatternArguments c = new SMC32PatternArguments();
        for (int j = 0; j < testSet.size(); j++) {
            c.setPat1(testSet.getPattern1(j));
            c.setPat2(testSet.getPattern2(j));
            c.setPat3(testSet.getPattern3(j));
            c.setPat4(testSet.getPattern4(j));

            int label = testSet.getClass(j);
            if (label == clas) {
                pdata++;
            } else {
                ndata++;
            }

            Integer treeResult = classifier.eval(c);
            for (int i = 0; i < N_BIT; i++) {
                int resultBit = (1 << i) & treeResult;

                if (label == clas) {
                    if (resultBit != 0) {
                        tp[i]++;
                    } else {
                        fn[i]++;
                    }
                } else {
                    if (resultBit != 0) {
                        fp[i]++;
                    } else {
                        tn[i]++;
                    }
                }
            }

        }

        final double dataSum = pdata + ndata;
        List<Double> fitVector = IntStream.range(0, N_BIT)
                .parallel()
                .mapToObj(i -> Pair.pair(fp[i], fn[i]))
                .map(pair -> {
                    int a = pair.get1();
                    int b = pair.get2();
                    return Math.sqrt(50.0 * (a * a + b * b) /
                            (dataSum * dataSum) + classifier.size() * 0.000001);
                }).collect(Collectors.toList());

        int bestBit = 0;
        double bestFitness = STARTING_FITNESS;
        for (int i = 1; i < N_BIT; i++) {
            if (fitVector.get(i) < bestFitness) {
                bestBit = i;
                bestFitness = fitVector.get(i);
            }
        }

        int tpBest = tp[bestBit];
        int tnBest = tn[bestBit];
        int fpBest = fp[bestBit];
        int fnBest = fn[bestBit];

        double TPR = (double) tpBest / (double) pdata;
        double TNR = (double) tnBest / (double) ndata;
        double FPR = (double) fpBest / (double) pdata;
        double FNR = (double) fnBest / (double) ndata;

        return new SMCGP32SimpleEvaluation(
                classifier,
                FPR,
                FNR,
                TPR,
                TNR,
                bestBit,
                bestFitness
        );

    }


    public static void main(String[] argv) throws IOException {
        String trainPath32;
        String testPath32;
        if (argv.length != 2) {
            System.out.println("Expected exactly 2 arguments.");
            System.out.println("Usage: <jvm-command> <train-set> <test-set>");
            System.exit(1);
            return;
        } else {
            trainPath32 = argv[0];
            testPath32 = argv[1];
        }

        SMCGP32DataSet trainData = new SMCGP32DataSet();
        SMCGP32DataSet testData = new SMCGP32DataSet();

        File trainSetFile = new File(trainPath32);
        File testSetFile = new File(testPath32);

        trainData.parseInstancesFromFile(trainSetFile);
        testData.parseInstancesFromFile(testSetFile);


        System.out.println("#(train instances): " + trainData.size());

        RandomUtils random = new RandomUtils(1);
        var operations = createOperationSet(random);

        double mutPb = 0.03;
        double cxPb = 0.80;
        int maxTreeHeight = 12;
        int tournSize = 7;
        TreeEvolution<Integer, SMC32PatternArguments> evolution;
        evolution = new TreeEvolution<>(
                random,
                1000,
                5,
                7,
                operations,
                new SyncParallelPopEvaluator<>(individual -> fitnessSMCTree(
                        individual,
                        trainData
                )),
                new KillOldIndividuals<>(
                        new PreserveIndividuals<>(
                                1,
                                new EliteSelection<>(),
                                new RandomSelection<>(),
                                new SequentialBreeding<Tree<Integer, SMC32PatternArguments>>(
                                ).addBreedingPhase(
                                        new TournamentSelection<>(
                                                tournSize
                                        )
                                ).addBreedingPhase(
                                        new Mating<>(
                                                new StocasticMateOperator<>(
                                                        cxPb,
                                                        new TreeCrossover<>(
                                                                maxTreeHeight
                                                        )
                                                )
                                        )
                                ).addBreedingPhase(
                                        new Mutation<>(
                                                new StocasticMutationOperator<>(
                                                        mutPb,
                                                        (evoInterf, individual) -> {
                                                            return Tree.changeSubTreeMutation(
                                                                    evoInterf.getRandom(),
                                                                    individual,
                                                                    operations,
                                                                    9,
                                                                    6,
                                                                    maxTreeHeight
                                                            );
                                                        }
                                                )
                                        )
                                )
                        )
                )
        );

        evolution.addLogbookOutputListener(System.out::println);

        evolution.initialize();

        try {
            evolution.evolveSync(4, 0.005);

            var hallOfFame = evolution.getHallOfFame();

            var bestIndividual = hallOfFame.bestIndividual();

            SMCGP32SimpleEvaluation evaluation = evaluateClassifier(
                    bestIndividual, 0, testData);

            System.out.println(evaluation.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
