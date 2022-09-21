package bruteForce;

import java.util.Set;

public class EnigmaDictionary {

    private Set<String> words;
    private String excludeChars;

    public EnigmaDictionary(Set<String> words, String excludeChars) {
        this.words = words;
        this.excludeChars = excludeChars;
    }

    public Set<String> getWords() {
        return words;
    }

    public void setWords(Set<String> words) {
        this.words = words;
    }

    public String getExcludeChars() {
        return excludeChars;
    }

    public void setExcludeChars(String excludeChars) {
        this.excludeChars = excludeChars;
    }
}
