package it.unipr.sowide.gpj.tree;

/**
 * Specifies the method used to build an inital random tree.
 */
public enum TreeInitMethod {
    /**
     * The generated tree is "full" (i.e. terminals are present only at the
     * target height; so some subtrees cannot be higher than others).
     */
    FULL,
    /**
     * There are no constraint on where terminals can appear.
     */
    GROW
}
