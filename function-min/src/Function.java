public class Function {
    public static final int VARIABLES = 3;
    public static final double MIN = -250;
    public static final double MAX = 250;
    public static final double RANGE = MAX - MIN;
    public static final double MUTATION_SIZE = 0.1;

    public static double clamp(double value) {
        return Math.max(MIN, Math.min(MAX, value));
    }


    public static double function(double[] values) {
        return 0;
    }
}
