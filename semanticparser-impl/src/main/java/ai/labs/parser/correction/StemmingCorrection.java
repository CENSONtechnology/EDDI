package ai.labs.parser.correction;

import ai.labs.parser.model.FoundWord;
import ai.labs.parser.model.IDictionary;
import org.tartarus.snowball.SnowballStemmer;

import java.util.*;


/**
 * @author ginccc
 */
public class StemmingCorrection implements ICorrection {
    private boolean lookupIfKnown;
    private String language;
    private SnowballStemmer stemmer;
    private Map<String, List<IDictionary.IFoundWord>> stemmedWordMap = new HashMap<>();

    public StemmingCorrection(String language, boolean lookupIfKnown) {
        this.language = language;
        this.lookupIfKnown = lookupIfKnown;
    }

    @Override
    public void init(List<IDictionary> dictionaries) {
        stemmer = createNewStemmer();
        dictionaries.stream().flatMap(dictionary -> dictionary.getWords().stream()).
                forEach(word -> {
                    stemmer.setCurrent(word.getValue().toLowerCase());
                    stemmer.stem();
                    String stemmedWord = stemmer.getCurrent();
                    if (!this.stemmedWordMap.containsKey(stemmedWord)) {
                        this.stemmedWordMap.put(stemmedWord, new ArrayList<>());
                    }
                    this.stemmedWordMap.get(stemmedWord).add(new FoundWord(word, true, 0.5));
                });
    }

    private SnowballStemmer createNewStemmer() {
        try {
            Class stemClass = Class.forName("org.tartarus.snowball.ext." + language + "Stemmer");
            return (SnowballStemmer) stemClass.newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<IDictionary.IFoundWord> correctWord(String word) {
        stemmer = createNewStemmer();
        stemmer.setCurrent(word.toLowerCase());
        stemmer.stem();
        String stemmedWord = stemmer.getCurrent();

        List<IDictionary.IFoundWord> foundWords = stemmedWordMap.get(stemmedWord);

        if (foundWords != null && !foundWords.isEmpty()) {
            Collections.sort(foundWords);
            return foundWords;
        }

        return IDictionary.NO_WORDS_FOUND;
    }

    @Override
    public boolean lookupIfKnown() {
        return lookupIfKnown;
    }
}
