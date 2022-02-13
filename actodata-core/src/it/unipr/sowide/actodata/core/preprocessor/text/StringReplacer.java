package it.unipr.sowide.actodata.core.preprocessor.text;

import java.util.function.Function;

/**
 * A {@link Function} that transforms a {@link String} by replacing portions
 * of text.
 */
public class StringReplacer implements Function<String, String> {
    private final String regex;
    private final String replacement;

    /**
     * A replacer that replaces all the occurrences of substrings that match the
     * provided regex with the provided {@code replacement}
     *
     * @param regex       the regex to match the substrings to be replaced
     * @param replacement the string to be applied as replacement
     */
    public StringReplacer(String regex, String replacement) {

        this.regex = regex;
        this.replacement = replacement;
    }

    @Override
    public String apply(String s) {
        return s.replaceAll(regex, replacement);
    }

    /**
     * @return the regex to match the substrings to be replaced
     */
    public String getRegex() {
        return regex;
    }

    /**
     * @return the string to be applied as replacement
     */
    public String getReplacement() {
        return replacement;
    }


}
