package it.unipr.sowide.gpj.tree;

import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.util.Pair;
import it.unipr.sowide.util.RandomUtils;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * An individual that is a program represented by a tree of operations.
 *
 * @param <T> the type of the result of the evaluation of the tree
 * @param <C> the type of the "context" in which the evaluation of the tree is
 *            done; it can be used as a way to create a mutable state of
 *            computation as well as a way to pass input values.
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class Tree<T, C> implements Individual {
    private Operation<T, C> operation;
    private Tree<T, C> arg1 = null;
    private Tree<T, C> arg2 = null;
    private int birthGeneration = 0;
    private Double fitness = null;
    private boolean inHallOfFame = false;

    /**
     * {@inheritDoc}
     **/
    @Override
    public Comparator<Individual> comparator() {
        return MINIMIZE;
    }

    /**
     * Creates a tree composed by a Terminal.
     *
     * @param terminal the terminal
     */
    public Tree(Terminal<T, C> terminal) {
        operation = terminal;
    }

    /**
     * Creates a tree composed by an unary function and a subtree that
     * represents the argument.
     *
     * @param function1 the unary function
     * @param arg1      the argument expression
     */
    public Tree(Function1<T, C> function1, Tree<T, C> arg1) {
        operation = function1;
        this.arg1 = arg1;
    }

    /**
     * Creates a tree composed by a binary function and two subtrees that
     * represent the arguments
     *
     * @param function2 the binary function
     * @param arg1      the first argument expression
     * @param arg2      the second argument expression
     */
    public Tree(Function2<T, C> function2, Tree<T, C> arg1, Tree<T, C> arg2) {
        operation = function2;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    private Tree(Operation<T, C> operation, Tree<T, C> arg1, Tree<T, C> arg2) {
        this.operation = operation;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    static <T, C> Tree<T, C> treeNode(
            Operation<T, C> operation,
            Tree<T, C> arg1,
            Tree<T, C> arg2
    ) {
        return new Tree<>(operation, arg1, arg2);
    }

    private String indent(String s) {
        StringBuilder result = new StringBuilder();
        AtomicBoolean first = new AtomicBoolean(true);
        s.lines().forEach(x -> {
            if (first.get()) {
                result.append("\n").append("|-").append(x);
            } else {
                result.append("\n").append("| ").append(x);
            }
            first.set(false);
        });

        return result.toString();
    }

    private String dumpStructure() {
        var result =
                "" + operation.getName();
        if (arg1 != null) {
            result += indent(arg1.dumpStructure());
        }
        if (arg2 != null) {
            result += indent(arg2.dumpStructure());
        }
        return result;
    }

    @Override
    public String toString() {
        return "(SIZE: " + size() + ")\n" + dumpStructure();
    }

    /**
     * Evaluates the tree using the input context.
     *
     * @param context the context
     * @return the result value
     */
    public T eval(C context) {
        if (operation instanceof Terminal) {
            return ((Terminal<T, C>) operation).eval(context);
        } else if (operation instanceof Function1) {
            Function1<T, C> function1 = (Function1<T, C>) operation;
            if (arg1 != null) {
                return function1.apply(arg1.eval(context), context);
            } else if (arg2 != null) {
                return function1.apply(arg2.eval(context), context);
            } else {
                throw new RuntimeException(
                        "Cannot apply unary function to no arguments"
                );
            }
        } else if (operation instanceof Function2) {
            Function2<T, C> function2 = (Function2<T, C>) operation;
            if (arg1 != null && arg2 != null) {
                return function2.apply(
                        arg1.eval(context),
                        arg2.eval(context),
                        context
                );
            } else {
                throw new RuntimeException(
                        "Cannot apply binary function to less than 2 arguments"
                );
            }
        } else {
            throw new RuntimeException(
                    "Invalid operation"
            );
        }
    }

    /**
     * Creates a structural copy of the tree, without copying any metadata
     *
     * @return the copied tree
     */
    public Tree<T, C> copyTree() {
        var op = operation.copy();
        Tree<T, C> a1 = null;
        Tree<T, C> a2 = null;

        if (arg1 != null) {
            a1 = arg1.copyTree();
        }
        if (arg2 != null) {
            a2 = arg2.copyTree();
        }


        return new Tree<>(op, a1, a2);
    }

    /**
     * Creates a structural copy of the tree, copying the metadata too.
     *
     * @return the copied tree
     */
    public Tree<T, C> copyTreeWithMetadata() {
        var result = copyTree();

        result.setBirthGeneration(this.getBirthGeneration());

        var optFitness = this.getFitness();

        if (optFitness.isPresent()) {
            result.setFitness(optFitness.get());
        } else {
            result.resetFitness();
        }
        return result;
    }


    /**
     * {@inheritDoc}
     **/
    @Override
    public Promise<Individual, Throwable> copyIndividual() {
        return Promises.immediatelyResolve(copyTreeWithMetadata());
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public int getBirthGeneration() {
        return this.birthGeneration;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void setBirthGeneration(int gen) {
        this.birthGeneration = gen;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public Optional<Double> getFitness() {
        return Optional.ofNullable(fitness);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void setFitness(double value) {
        this.fitness = value;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void resetFitness() {
        this.fitness = null;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void kill() {
        //does nothing
    }


    /**
     * {@inheritDoc}
     **/
    @Override
    public boolean isInHallOfFame() {
        return inHallOfFame;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void setInHallOfFame(boolean inHallOfFame) {
        this.inHallOfFame = inHallOfFame;
    }


    /**
     * Computes the height of the tree (i.e. the depth of the expression).
     *
     * @return the height
     */
    public int height() {
        if (arg1 == null) {
            if (arg2 == null) {
                return 1;
            } else {
                return 1 + arg2.height();
            }
        } else {
            if (arg2 == null) {
                return 1 + arg1.height();
            } else {
                return 1 + Math.max(arg1.height(), arg2.height());
            }
        }
    }

    /**
     * Computes the size of the tree (i.e. the total number of nodes in the
     * tree).
     *
     * @return the size of the tree
     */
    public int size() {
        if (arg1 == null) {
            if (arg2 == null) {
                return 1;
            } else {
                return 1 + arg2.size();
            }
        } else {
            if (arg2 == null) {
                return 1 + arg1.size();
            } else {
                return 1 + arg1.size() + arg2.size();
            }
        }
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tree) {
            return Objects.equals(this.operation, ((Tree<?, ?>) obj).operation)
                    && Objects.equals(this.arg1, ((Tree<?, ?>) obj).arg1)
                    && Objects.equals(this.arg2, ((Tree<?, ?>) obj).arg2);
        }
        return super.equals(obj);
    }

    static class Cutpoint<T, C> {
        private final Tree<T, C> currentSubTree;
        private final Consumer<Tree<T, C>> treeConsumer;

        public Cutpoint(
                Tree<T, C> currentSubTree,
                Consumer<Tree<T, C>> treeConsumer
        ) {
            this.currentSubTree = currentSubTree;
            this.treeConsumer = treeConsumer;
        }

        public Tree<T, C> getCurrentSubTree() {
            return currentSubTree;
        }

        public Consumer<Tree<T, C>> getTreeConsumer() {
            return treeConsumer;
        }
    }

    List<Cutpoint<T, C>> selectCutpoints(int atHeight) {
        int myHeight = this.height();
        if (atHeight < 1 || atHeight >= myHeight) {
            return List.of();
        } else if (atHeight + 1 == myHeight) {
            if (operation instanceof Terminal) {
                return List.of();
            } else if (operation instanceof Function1) {
                return List.of(new Cutpoint<>(arg1, t -> {
                    arg1 = t;
                }));
            } else {
                return List.of(
                        new Cutpoint<>(arg1, t -> arg1 = t),
                        new Cutpoint<>(arg2, t -> arg2 = t)
                );
            }
        } else {
            List<Cutpoint<T, C>> result = new ArrayList<>();
            if (arg1 != null) {
                result.addAll(arg1.selectCutpoints(atHeight));
            }
            if (arg2 != null) {
                result.addAll(arg2.selectCutpoints(atHeight));
            }
            return result;
        }
    }

    /**
     * Performs a crossover operation between two trees to produce a new pair
     * of trees.
     *
     * @param random    the random generator utility
     * @param parent1   the first parent
     * @param parent2   the secodn parent
     * @param maxHeight the maximum height of the resulting trees
     * @param <T>       the type of the result of the evaluation of the tree
     * @param <C>       the type of the "context" in which the evaluation of
     *                  the tree is performed
     * @return the resulting trees
     */
    public static <T, C> Pair<Tree<T, C>, Tree<T, C>> crossover(
            RandomUtils random,
            Tree<T, C> parent1,
            Tree<T, C> parent2,
            int maxHeight
    ) {


        var child1 = parent1.copyTreeWithMetadata();
        var child2 = parent2.copyTreeWithMetadata();

        var height1 = child1.height();
        var height2 = child2.height();
        if (height1 <= 1 || height2 <= 1) {
            // cannot crossover: small trees
            return Pair.pair(parent1, parent2);
        }


        int selH1;
        int selH2;
        if (maxHeight <= 0) {
            selH1 = random.randomInt(1, height1);
            selH2 = random.randomInt(1, height2);
        } else if (height1 >= maxHeight || height2 >= maxHeight) {
            // one of the two trees is at maximum height already.
            // in this case, the two subtrees exchanged must be of the same size
            if (height1 >= height2) {
                selH2 = selH1 = random.randomInt(1, height1);
            } else {
                selH1 = selH2 = random.randomInt(1, height2);
            }
        } else {
            try {
                if (height1 >= height2) {
                    selH1 = random.randomInt(
                            1,
                            Math.min(height1 - 1, maxHeight - height2 + 1) + 1
                    );
                    selH2 = random.randomInt(
                            1,
                            Math.min(
                                    height2 - 1,
                                    maxHeight - height1 + selH1
                            ) + 1
                    );
                } else {
                    selH2 = random.randomInt(
                            1,
                            Math.min(height2 - 1, maxHeight - height1 + 1) + 1
                    );
                    selH1 = random.randomInt(
                            1,
                            Math.min(
                                    height1 - 1,
                                    maxHeight - height2 + selH2
                            ) + 1
                    );
                }
            } catch (IllegalArgumentException e) {
                System.out.println("height1   = " + height1);
                System.out.println("height2   = " + height2);
                System.out.println("maxHeight = " + maxHeight);
                throw e;
            }
        }

        List<Cutpoint<T, C>> cutpoints1 = child1.selectCutpoints(selH1);
        List<Cutpoint<T, C>> cutpoints2 = child2.selectCutpoints(selH2);
        Cutpoint<T, C> cp1 = random.pick(new ArrayList<>(cutpoints1));
        Cutpoint<T, C> cp2 = random.pick(new ArrayList<>(cutpoints2));
        if (cp1 != null && cp2 != null) {
            cp1.getTreeConsumer().accept(cp2.getCurrentSubTree());
            cp2.getTreeConsumer().accept(cp1.getCurrentSubTree());
        }
        return Pair.pair(child1, child2);
    }

    /**
     * Creates a new randomly generated tree.
     *
     * @param random     the random generation utility
     * @param operations the set of operations used to build the tree
     * @param maxDepth   the maximum depth (height) of the resulting tree.
     * @param method     see {@link TreeInitMethod}
     * @return the generated tree.
     */
    public static <T, C> Tree<T, C> randomTree(
            RandomUtils random,
            List<Operation<T, C>> operations,
            int maxDepth,
            TreeInitMethod method
    ) {
        if (maxDepth <= 1
                || method == TreeInitMethod.GROW && random.nextBoolean()) {
            Terminal<T, C> term = random.pick(operations.stream()
                    .filter(op -> op.arity() == 0)
                    .filter(op -> op instanceof Terminal)
                    .map(op -> (Terminal<T, C>) op)
                    .collect(Collectors.toList()));
            if (term != null) {
                term = (Terminal<T, C>) term.transformInit();
            }
            return new Tree<>(term);
        } else {
            Operation<T, C> func = random.pick(operations.stream()
                    .filter(op -> op.arity() > 0)
                    .collect(Collectors.toList()));
            Tree<T, C> a1 = null;
            Tree<T, C> a2 = null;

            if (func != null) {
                func = func.transformInit();
            }

            if (func != null) {
                if (func.arity() >= 1) {
                    a1 = randomTree(random, operations, maxDepth - 1, method);
                }
                if (func.arity() >= 2) {
                    a2 = randomTree(random, operations, maxDepth - 1, method);
                }
            }
            return Tree.treeNode(func, a1, a2);
        }
    }

    public static <T, C> Tree<T, C> mutateNodeSameArity(
            RandomUtils random,
            Tree<T, C> tree,
            List<Operation<T, C>> candidateOps
    ) {
        int height = tree.height();
        if (height <= 1) {
            return randomTree(random, candidateOps, 1, TreeInitMethod.FULL);
        }
        Tree<T, C> copy = tree.copyTreeWithMetadata();
        int selH = random.nextInt(height - 1) + 2;
        List<Cutpoint<T, C>> cutpoints = copy.selectCutpoints(selH);
        Cutpoint<T, C> cutpoint = random.pick(new ArrayList<>(cutpoints));
        if (cutpoint != null) {
            Tree<T, C> subtree = cutpoint.getCurrentSubTree()
                    .copyTreeWithMetadata();
            Operation<T, C> newOp = random.pick(candidateOps.stream()
                    .filter(co -> co.arity() == subtree.operation.arity())
                    .collect(Collectors.toList()));
            if (newOp != null) {
                subtree.operation = newOp;
            }
            cutpoint.getTreeConsumer().accept(subtree);
        }
        return copy;
    }

    /**
     * Performs a mutation that "shuffles" the structure of the tree, without
     * introducing new operations.
     *
     * @param random         the random generation utility
     * @param tree           the input tree
     * @param maxFinalHeight the maximum height of the resulting tree
     * @return the mutated tree
     */
    public static <T, C> Tree<T, C> structuralMutation(
            RandomUtils random,
            Tree<T, C> tree,
            int maxFinalHeight
    ) {
        return crossover(random, tree, tree, maxFinalHeight).get1();
    }

    /**
     * Performs a mutation of a tree by performing a cross-over with a randomly
     * generated tree.
     *
     * @param random           the random generation utility
     * @param tree             the input tree
     * @param operations       the set of operations used to generate the mate
     * @param maxSubtreeHeight the maximum height of the subtree added to the
     *                         input tree
     * @param maxFinalHeight   the maximum height of the resulting tree
     * @return the mutated tree.
     */
    public static <T, C> Tree<T, C> headlessChickenMutation(
            RandomUtils random,
            Tree<T, C> tree,
            List<Operation<T, C>> operations,
            int maxSubtreeHeight,
            int maxFinalHeight
    ) {
        return crossover(
                random,
                tree,
                randomTree(
                        random,
                        operations,
                        maxSubtreeHeight + 1,
                        TreeInitMethod.GROW
                ),
                maxFinalHeight
        ).get1();
    }

    /**
     * A mutation that replaces a subtree with a randomly generated one.
     *
     * @param random            the random generation utility
     * @param tree              the input tree
     * @param operations        the set of operation used to generate the
     *                          subtree
     * @param maxMutationHeight the maximum height at which the mutation can
     *                          occur
     * @param maxSubtreeHeight  the maximum height of the generated subtree
     * @param maxFinalHeight    the maximum height of the final tree
     * @return the mutated tree
     */
    public static <T, C> Tree<T, C> changeSubTreeMutation(
            RandomUtils random,
            Tree<T, C> tree,
            List<Operation<T, C>> operations,
            int maxMutationHeight,
            int maxSubtreeHeight,
            int maxFinalHeight
    ) {
        int height = tree.height();
        if (height <= 1) {
            return tree;
        }
        var mutatedTree = tree.copyTreeWithMetadata();
        int mutationHeight =
                random.randomInt(1, Math.min(maxMutationHeight, height));
        List<Cutpoint<T, C>> cutpoints =
                mutatedTree.selectCutpoints(mutationHeight);

        if (cutpoints.isEmpty()) {
            return tree;
        }
        Cutpoint<T, C> cutpoint = random.pick(new ArrayList<>(cutpoints));

        var maxHeight2 = maxFinalHeight - height;
        Tree<T, C> subtree = randomTree(
                random,
                operations,
                Math.min(maxSubtreeHeight, maxHeight2) + 1,
                TreeInitMethod.GROW
        );
        if (cutpoint != null) {
            cutpoint.getTreeConsumer().accept(subtree);
        }
        return mutatedTree;
    }

}
