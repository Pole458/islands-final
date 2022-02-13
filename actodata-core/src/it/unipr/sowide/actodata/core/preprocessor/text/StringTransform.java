package it.unipr.sowide.actodata.core.preprocessor.text;

import it.unipr.sowide.util.IterableArray;
import it.unipr.sowide.util.annotations.Namespace;

import java.util.List;
import java.util.function.Function;

/**
 * Set of utility functions to apply transformations to strings. The functions
 * are always in the form of java{@link Function}s; this is preferred
 * since they can be used as arguments of most of Java 8 'map' methods.
 */
@Namespace
public class StringTransform {
    private StringTransform() {
    } // do not instantiate


    /**
     * Creates an operator on strings that applies in sequence all the provided
     * operators.
     *
     * @param functions the functions
     * @return the composition of the functions
     */
    @SafeVarargs
    public static Function<String, String> all(
            Function<String, String>... functions
    ) {
        return all(IterableArray.iterate(functions));
    }

    /**
     * Creates an operator on strings that applies in sequence all the provided
     * operators.
     *
     * @param functions the functions
     * @return the composition of the functions
     */
    public static Function<String, String> all(
            Iterable<? extends Function<String, String>> functions
    ) {
        return s -> {
            String cur = s;
            for (Function<String, String> function : functions) {
                cur = function.apply(cur);
            }
            return cur;
        };
    }

    /**
     * Creates a {@link StringReplacer} that represent a replacement operation.
     *
     * @param regex       the regex to match the substrings to be replaced
     * @param replacement the string to be applied as replacement
     * @return the resulting operation
     */
    public static StringReplacer replace(String regex, String replacement) {
        return new StringReplacer(regex, replacement);
    }

    public static final Function<String, String> toLower
            = String::toLowerCase;

    public static final StringReplacer removeHashtags
            = replace("#\\S+", " ");

    public static final StringReplacer removeNonASCII
            = replace("[^\\x00-\\x7F]", "");

    public static final Function<String, String> removeRedundantBlanks =
            s -> s.replaceAll("[ ]+", " ").trim();

    public static final List<StringReplacer> expandContractions = List.of(
            replace("'d ", " would "),
            replace("'re ", " are "),
            replace("'ve ", " have "),
            replace(" I'm ", " I am "),
            replace("'ll ", " will "),
            replace(" it's ", " it is "),
            replace("It's ", " It is "),
            replace(" that's ", " that is "),
            replace("That's ", " That is "),
            replace(" what's ", " what is "),
            replace("What's ", " What is ")
    );

    public static final List<StringReplacer> emoticonsToWords = List.of(
            //angel, innocent
            replace(
                    "O:\\-\\)|0:\\-3|0:3|0:\\-\\)|0:\\)|0;\\^\\)|\\(\\-:O|" +
                            "\\(\\-:0|\\(:O|\\(:0|\\(\\^:0|\\(\\^:O",
                    " smileAngel "
            ),
            //evil
            replace(
                    ">:\\)|>;\\)|>:\\-\\)|\\}:\\-\\)|}:\\)|3:\\-\\)|3:\\)|" +
                            "\\(:<|\\(;<|\\(\\-:<|\\(\\-;<|\\(\\-:\\{|\\(:\\{",
                    " smileEvil "
            ),
            //happy
            replace(
                    ":\\-\\)|:\\)|:D|:o\\)|:O\\)|:\\]|:3|:c\\)|:>|=\\]|8\\)|=\\)|" +
                            ":\\}|:\\^\\)|\\^_\\^|=:\\)|\\(:|\\(\\-:|\\(o:|\\(O:|" +
                            "\\[:|<:|\\[=|\\(8|\\(=|\\{:|\\(\\^:",
                    " smileHappy "
            ),
            //laugh
            replace(
                    ":\\-D|8\\-D|8D|x\\-D|xD|X\\-D|XD|=\\-D|=D|=\\-3|=3|B\\^D|" +
                            ":\\-\\)\\)|:'\\-\\)|:'\\)",
                    " smileLaugh "
            ),
            //angry
            replace(
                    ":\\-\\|\\||:@|>:\\(|\\|\\|\\-:|@:|\\):<",
                    " smileAngry "
            ),
            //sad
            replace(
                    ">:\\[|:\\-\\(|:\\(|:\\-c|:c|:\\-<|:\\-\\[|:\\[|:\\{|<\\\\3|" +
                            "\\]:<|\\)\\-:|\\):|>\\-:|\\]\\-:|\\]:|\\}:",
                    " smileSad "
            ),
            //crying
            replace(
                    ";\\(|:'\\-\\(|:'\\(|\\);|\\)':|\\)\\-':",
                    " smileCrying "
            ),
            //horror, disgust
            replace(
                    "D:<|D:|D8|D;|D=|DX|v\\.v|D\\-':",
                    " smileFear "
            ),
            //kiss
            replace(
                    ":\\*|:\\-\\*|:\\^\\*|\\( '\\}\\{' \\)|<3|\\*:|\\*\\-:|\\*\\^:",
                    " smileKiss "
            ),
            //winking
            replace(
                    ";\\-\\)|;\\)|\\*\\-\\)|\\*\\)|;\\-\\]|;\\]|;D|;\\^\\)|:\\-,|" +
                            "\\(;|\\(\\-;|\\[;|\\[\\-;|\\(\\^;",
                    " smileWink "
            ),
            //tongue sticking out
            replace(
                    ">:P|:\\-P|:P|X\\-P|x\\-p|xp|XP|:\\-p|:p|=p|:\\-Þ|:Þ|:þ|:\\-þ|" +
                            ":\\-b|:b|d:",
                    " smileTongueStickingOut "
            ),
            //skeptical
            replace(
                    ">:\\\\|>:/|:\\-/|:\\-\\.|:/|:\\\\|=/|=\\\\|:L|=L|:S|:s|" +
                            ">\\.<|\\-_\\-|/:<|\\\\:<|\\\\\\-:|/\\-:|\\.\\-:|/:|\\\\:|" +
                            "/=|\\\\=|S:|s:",
                    " smileSkeptical "
            ),
            //straight face
            replace(
                    ":\\||:\\-\\||\\|:|\\|\\-:",
                    " smileStraightFace "
            )
    );

    public static final List<StringReplacer> slang = List.of(
            replace(" gotta ", " got to "),
            replace(" let's ", " let us "),
            replace(" c'mon ", " come on "),
            replace("Lets ", " let us "),
            replace("C'mon ", " come on "),
            replace(" tnx ", " thanks "),
            replace(" thx ", " thanks "),
            replace(" kk ", " ok "),
            replace(" lulz ", " lol "),
            replace(" sry ", " sorry "),
            replace(" l8 ", " late "),
            replace(" w8 ", " wait "),
            replace(" m8 ", " mate "),
            replace(" gr8 ", " great "),
            replace(" plz ", " please "),
            replace(" pls ", " please "),
            replace(" 2moro ", " tomorrow "),
            replace(" somthin ", " something "),
            replace(" srsly ", " seriously "),
            replace(" sht ", " shit "),
            replace(" imho ", " imo "),
            replace(" omfg ", " omg "),
            replace(" bros ", " brother "),
            replace(" bro ", " brother "),
            replace(" nope ", " no "),
            replace(" thw ", " the "),
            replace(" u ", " you "),
            replace(" yrs ", " years ")
    );

    public static final Function<String, String> removeURLs = all(List.of(
            replace("(http|https):\\S+", " "),
            replace("www\\S+", " ")
    ));

    public static final Function<String, String> cleanTwitterText = all(List.of(
            replace("pic.twitter.com\\S+", " "),
            replace("@\\S+", " "),
            replace("(RT )", " ")
    ));

}
