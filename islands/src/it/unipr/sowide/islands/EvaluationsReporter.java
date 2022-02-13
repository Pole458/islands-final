package it.unipr.sowide.islands;

import it.unipr.sowide.islands.content.ReportEvaluations;
import it.unipr.sowide.islands.content.SaveResults;
import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodes.actor.Shutdown;
import it.unipr.sowide.actodes.interaction.Error;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.islands.settings.IslandsSimulationSettings;
import it.unipr.sowide.util.Sets;
import it.unipr.sowide.util.Streams;
import org.math.plot.Plot2DPanel;
import org.math.plot.PlotPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An actor that stores the evaluations of an Island simulation. Can also
 * show a set of real-time graphs about the outgoing performances of the evolved
 * the best individuals for each island.
 */
public class EvaluationsReporter extends ActoDataBaseBehavior {

    private final HashMap<Reference, List<Evaluation>> evaluations = new HashMap<>();
    private final Map<Reference, String> groups = new HashMap<>();
    private final Map<String, Plot2DPanel> plotPanels = new HashMap<>();
    private Map<String, Function<Evaluation, Double>> statistics = null;
    private Map<String, Function<List<Evaluation>, Double>> aggregateStatistics = null;
    private final Set<JFrame> frames = new HashSet<>();
    private JTextArea bestIndividualTextArea = null;
    private double allTimeBestIndividualFitness = Double.MIN_VALUE;
    private final boolean showGui;
    private final static Color[] palette = new Color[]{
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.YELLOW,
            Color.MAGENTA,
            Color.CYAN,
            Color.ORANGE,
            Color.PINK,
    };

    /**
     * A reporter that stores the history of the evaluations.
     *
     * @param showGui true if the GUI with real-time graphs should be shown.
     */
    public EvaluationsReporter(IslandsSimulationSettings settings, boolean showGui) {
        this.settings = settings;
        this.showGui = showGui;
    }

    @Override
    public void actoDataNodeCases(ActoDataCaseFactory c) {
        /*
        Receives a request to add the new evaluations to the histories.
         */
        c.onContentOfType(ReportEvaluations.class, (genericReport, message) -> {

            // variables used to update the TextArea with information about
            // the best overall individual found
            String bestIndividualIsland = "";
            String bestIndividualGroup = "";
            IslandIndividual bestIndividual = null;
            double bestIndividualFitness = Double.MAX_VALUE;

            // finds the best individuals, while updating the histories
            for (Map.Entry<Reference, Evaluation> entry : genericReport.getEvaluations().entrySet()) {

                Reference reference = entry.getKey();
                Evaluation evaluation = entry.getValue();
                double fitness = evaluation.evaluationFitness;
                if (fitness <= bestIndividualFitness) {
                    bestIndividualFitness = fitness;
                    bestIndividualIsland = reference.getName();
                    bestIndividualGroup = groups.getOrDefault(reference, "");
                    bestIndividual = evaluation.individual;
                }
                if (evaluations.containsKey(reference)) {
                    evaluations.get(reference).add(evaluation);
                } else {
                    List<Evaluation> d = new ArrayList<>();
                    d.add(evaluation);
                    evaluations.put(reference, d);
                }
            }

            groups.putAll(genericReport.getGroups());

            // if the best individual is found, update the best individual
            if (bestIndividual != null && allTimeBestIndividualFitness < bestIndividualFitness) {

                setBestIndividual(
                        bestIndividual,
                        bestIndividualIsland,
                        bestIndividualGroup,
                        bestIndividualFitness
                );
            }

            // replots all the graphs with the new data
            if (showGui) {
                replot();
            }

        });

        /*
        Requests to save the current results of the simulation.
         */
        c.onContentOfType(SaveResults.class, (saveResults, message) -> {
            File f = new File(saveResults.getFilePath());

            try (BufferedWriter writer =
                         new BufferedWriter(new FileWriter(f, true))) {

                outputReport(writer, saveResults);

                //replies with a Done.DONE
                done(message);
            } catch (IOException e) {
                e.printStackTrace();
                send(message, Error.FAILEDEXECUTION);
            }
        });

        /*
        This actor can be killed from the outside. Releases eventual resources
        and disposes of the instantiated/rendered swing objects.
         */
        c.define(KILL, (message) -> {
            clean();
            return Shutdown.SHUTDOWN;
        });
    }

    /**
     * Releases eventual resources and disposes of the instantiated/rendered
     * swing objects.
     */
    private void clean() {
        this.groups.clear();
        this.plotPanels.clear();
        this.frames.forEach(frame -> {
            frame.setVisible(false);
            frame.dispatchEvent(new WindowEvent(
                    frame,
                    WindowEvent.WINDOW_CLOSING
            ));
        });
        this.frames.clear();
        this.evaluations.clear();
        this.statistics = null;
        this.aggregateStatistics = null;
        this.bestIndividualTextArea = null;
        this.allTimeBestIndividualFitness = Double.MAX_VALUE;
    }

    private static final DecimalFormat percentageFormatter = new DecimalFormat("##.###%");
    private static final DecimalFormat doubleFormatter = new DecimalFormat("#0.000");

    private static int counter = 0;
    private static int successes = 0;
    private static int totalGenerations = 0;
    private static int totalFitnessFunctionCalls = 0;

    private final IslandsSimulationSettings settings;

    /**
     * Saves writes to a buffer the last evaluations of all islands, togheter
     * with the best individuals, and other statistics and methadata.
     *
     * @param bufferedWriter the buffer
     * @param saveResults    the message that requested the save
     * @throws IOException if there are problems during the writing
     */
    private void outputReport(
            BufferedWriter bufferedWriter,
            SaveResults saveResults
    ) throws IOException {

        class myWriter {
            void write(String w) throws IOException {
                System.out.println(w);
                bufferedWriter.write(w + "\n");
            }
        }
        myWriter w = new myWriter();

//        w.write("*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");
//        w.write("Computation Started at " + saveResults.getStartTime());
//        w.write("Computation Ended at " + saveResults.getEndTime());
        Duration duration = Duration.between(
                saveResults.getStartTime(),

                saveResults.getEndTime()
        );
        String durationString = String.format(
                "%dh, %02dm, %02ds",
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart()
        );
//        w.write("Duration: " + durationString);
//        w.write("*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");
//        w.write("*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");

        List<Map.Entry<Reference, List<Evaluation>>> entries =
                evaluations.entrySet().stream()
                        .sorted((entry1, entry2) -> {
                            var evaluations1 = entry1.getValue();
                            var evaluations2 = entry2.getValue();
                            var last1 = evaluations1.get(evaluations1.size() - 1);
                            var last2 = evaluations2.get(evaluations2.size() - 1);
                            return -Double.compare(
                                    last1.evaluationFitness,
                                    last2.evaluationFitness
                            );
                        }).collect(Collectors.toList());

        int runFitnessFunctionCalls = entries.stream()
                .map(entry -> entry.getValue().stream().mapToInt(e -> e.fitnessFunctionsCalls).max())
                .mapToInt(o -> o.isPresent() ? o.getAsInt() : 0).sum();

        OptionalInt opt = entries.stream().mapToInt(e->e.getValue().size()).max();
        int runGenerations = opt.isPresent() ? opt.getAsInt() : entries.get(0).getValue().size();

        synchronized (EvaluationsReporter.class) {
            counter++;
            if (runGenerations != settings.maxGenerations) {
                successes++;
                totalGenerations += runGenerations;
                totalFitnessFunctionCalls += runFitnessFunctionCalls;
            }

            if (counter == settings.numberOfRuns) {

                w.write("Runs: " + counter);
                w.write("Successes: " + percentageFormatter.format(successes / (double) counter));

                if (successes != 0) {
                    double genAverage = totalGenerations / (double) successes;
                    double fitnessFunctionCallsAverage = totalFitnessFunctionCalls / (double) successes;
                    w.write("Succeses Average Generations : " + doubleFormatter.format(genAverage));
                    w.write("Succeses Average Fitness Function Calls: " + doubleFormatter.format(fitnessFunctionCallsAverage));
                }

                System.exit(0);
            }
        }

//        for (var entry : entries) {
//            if (saveResults.getForOnlyGroup() != null
//                    && !groups.get(entry.getKey()).equals(
//                    saveResults.getForOnlyGroup())) {
//                continue;
//            }
//            Reference islandRef = entry.getKey();
//            List<Evaluation> evaluations = entry.getValue();
//            Evaluation last = evaluations.get(evaluations.size() - 1);
//            w.write("ISLAND: " + islandRef.getName() + " (" + groups.get(islandRef) + ")");
//            w.write("Generations: " + evaluations.size());
//            w.write("Fitness: " + last.evaluationFitness);
//            w.write("Generation: " + last.individual.getBirthGeneration());
//            w.write("Total Fitness Function Call: " + Evolution.NUM_EVALUTATIONS);
//            w.write("Best Individual:\n" + last.individual.toString());
//            w.write("*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");
//        }

        if (aggregateStatistics != null) {
            w.write("*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");
            w.write("*=*=*=*=*=*=*=*= GROUPS STATISTICS *=*=*=*=*=*=*=*=");
            w.write("*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");

            for (var statistic : aggregateStatistics.entrySet()) {
                var statisticName = statistic.getKey();
                var statisticFunc = statistic.getValue();
                w.write("STATISTIC: " + statisticName);
                Iterator<String> iterator = groups.values().stream()
                        .filter(gname -> {
                            return saveResults.getForOnlyGroup() == null
                                    || saveResults.getForOnlyGroup().equals(
                                    gname);
                        })
                        .distinct().sorted().iterator();
                while (iterator.hasNext()) {
                    String groupName = iterator.next();
                    ArrayList<ArrayList<Evaluation>> groupHistory = getGroupHistory(groupName);
                    var last = groupHistory.get(groupHistory.size() - 1);
                    w.write(groupName + ": " + statisticFunc.apply(last));
                }
                w.write("*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");
            }
        }

//        w.write("*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");
//        w.write("*=*=*=*=*=*=*=*=*=*=*         =*=*=*=*=*=*=*=*=*=*=");
//        w.write("*=*=*=*=*=*=*=*=*=*=*   END   =*=*=*=*=*=*=*=*=*=*=");
//        w.write("*=*=*=*=*=*=*=*=*=*=*         =*=*=*=*=*=*=*=*=*=*=");
//        w.write("*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");
    }

    /**
     * Updates the graphs in the GUI.
     */
    private void replot() {

        Map<String, Color> groupColors = getGroupColors();


        for (var statistic : statistics.entrySet()) {
            var plot = plotPanels.get(statistic.getKey());
            if (plot != null) {
                plot.removeAllPlots();
                evaluations.keySet().stream()
                        .sorted((r1, r2) ->
                                String.CASE_INSENSITIVE_ORDER.compare(
                                        r1.getName(), r2.getName()))
                        .forEach(reference -> {
                            String group = groups.getOrDefault(reference, "");
                            Color color = groupColors.getOrDefault(
                                    group,
                                    Color.gray
                            );
                            plot.addLinePlot(
                                    reference.getName() + " (" + group + ")",
                                    color,
                                    evaluations.get(reference).stream()
                                            .mapToDouble(ev -> statistic
                                                    .getValue()
                                                    .apply(ev)
                                            ).toArray()
                            );
                        });
            }
        }
        Map<String, ArrayList<ArrayList<Evaluation>>> histories =
                new HashMap<>();

        groups.values().forEach(group -> {
            ArrayList<ArrayList<Evaluation>> groupHistory = getGroupHistory(group);
            histories.put(group, groupHistory);
        });

        for (var statistic : aggregateStatistics.entrySet()) {
            var plot = plotPanels.get(statistic.getKey());
            if (plot != null) {
                plot.removeAllPlots();
                Function<List<Evaluation>, Double> operator = statistic.getValue();

                groups.values().stream().distinct().sorted().forEach(group -> {
                    var groupHistory = histories.get(group);
                    if (groupHistory != null) {
                        double[] groupStatistics = groupHistory.stream()
                                .mapToDouble(operator::apply)
                                .toArray();
                        if (groupStatistics.length > 0) {
                            plot.addLinePlot(
                                    group,
                                    groupColors.getOrDefault(group, Color.GRAY),
                                    groupStatistics
                            );
                        }
                    }
                });
            }
        }


    }

    /**
     * Transposes the matrices of saved data about the evaluations. It actually
     * returns the "evaluation" history of a specific group of islands.
     *
     * @param group the group of islands
     * @return the history of the group
     */
    private ArrayList<ArrayList<Evaluation>> getGroupHistory(String group) {
        return transpose(evaluations.entrySet().stream()
                .filter(e -> groups.get(e.getKey()).equals(group))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList())
        );
    }


    private Map<String, Color> getGroupColors() {
        Map<String, Color> groupColors = new HashMap<>();


        Streams.streamWithIndex(groups.values().stream()
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList())
                )
                .forEach(pair -> pair.biConsume((index, group) -> {
                    Color color = palette[index % palette.length];
                    groupColors.put(group, color);
                }));
        return groupColors;
    }

    private void setBestIndividual(
            IslandIndividual bestIndividual,
            String islandName,
            String islandGroup,
            double fitness
    ) {
        allTimeBestIndividualFitness = fitness;
        if (bestIndividualTextArea != null) {
            bestIndividualTextArea.setText(
                    "Island: " + islandName + " (" + islandGroup + ")\n" +
                            "Birth Generation: " + bestIndividual
                            .getBirthGeneration() +
                            "Fitness: " + fitness + "\n" +
                            bestIndividual
            );
        }
    }

    /**
     * Transposes a matrix in the form of a list of lists.
     * <p></p>
     * <b>NOTE: this assumes that all sublists in input are of same size.</b>
     */
    private static <T> ArrayList<ArrayList<T>> transpose(
            List<? extends List<T>> input
    ) {
        if (input.isEmpty()) {
            return new ArrayList<>();
        }
        var size = input.get(0).size();
        ArrayList<ArrayList<T>> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ArrayList<T> l = new ArrayList<>();
            for (List<T> ts : input) {
                l.add(ts.get(i));
            }
            result.add(l);
        }

        return result;

    }

    /**
     * Sets up the graphical user interface with all the real-time graphs.
     */
    private void setupGui() {
        if (showGui) {
            statistics = Map.of(
                    "Evaluation Fitness", evaluation -> evaluation.evaluationFitness,
                    "Training Fitness", evaluation -> (evaluation.individual.getFitness().isPresent() ? evaluation.individual.getFitness().get() : 0.0)
            );

            aggregateStatistics = Map.of(
                    "Fitness_avg", (ls) -> ls.stream()
                            .mapToDouble(evaluation -> evaluation.evaluationFitness)
                            .average()
                            .orElse(0.0)
            );

            Sets.union(
                    statistics.keySet(),
                    aggregateStatistics.keySet()
            ).forEach((key) -> {
                JFrame windowFrame = new JFrame(key);
                windowFrame.setMinimumSize(new Dimension(200, 200));
                Plot2DPanel plot = new Plot2DPanel();
                plot.addLegend(PlotPanel.EAST);
                windowFrame.setContentPane(plot);
                windowFrame.setVisible(true);
                plotPanels.put(key, plot);
                frames.add(windowFrame);
            });

            JFrame bestIndividualWindow = new JFrame(getReference().getName() + " - Best Individual");
            bestIndividualTextArea = new JTextArea(" Best Individual");
            JScrollPane scrollPane = new JScrollPane(bestIndividualTextArea);
            bestIndividualTextArea.setEditable(false);
            frames.add(bestIndividualWindow);

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridwidth = GridBagConstraints.REMAINDER;

            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1.0;
            c.weighty = 1.0;
            panel.add(scrollPane, c);

            c.fill = GridBagConstraints.HORIZONTAL;
            panel.add(bestIndividualTextArea, c);

            bestIndividualWindow.add(panel);
            bestIndividualWindow.pack();
            bestIndividualWindow.setVisible(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        setupGui();
    }
}
