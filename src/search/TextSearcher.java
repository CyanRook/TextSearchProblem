package search;

import javafx.util.Pair;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TextSearcher {

	// Keep all the tokens
	private ArrayList<String> tokens;
	// Create a searchable map with all tokens as hash values and an array of indices on where they occur
	private HashMap<String, ArrayList<Integer>> contextMap;

	/**
	 * Initializes the text searcher with the contents of a text file.
	 * The current implementation just reads the contents into a string 
	 * and passes them to #init().  You may modify this implementation if you need to.
	 * 
	 * @param f Input file.
	 * @throws IOException
	 */
	public TextSearcher(File f) throws IOException {
		FileReader r = new FileReader(f);
		StringWriter w = new StringWriter();
		char[] buf = new char[4096];
		int readCount;
		
		while ((readCount = r.read(buf)) > 0) {
			w.write(buf,0,readCount);
		}
		
		init(w.toString());
	}
	
	/**
	 *  Initializes any internal data structures that are needed for
	 *  this class to implement search efficiently.
	 */
	protected void init(String fileContents) {
		// Tokenize All Strings
        // RegEx is for all alphanumeric characters with apostrophes.
		TextTokenizer tokenizer = new TextTokenizer(fileContents, "[a-zA-Z0-9']+");
		// Push it to a list & a map
		tokens = new ArrayList<>();
		contextMap = new HashMap<>();
		// Keep track of index
		int index = -1;
		while (tokenizer.hasNext()) {
			index += 1;
			String token = tokenizer.next();
			// Add Each Token to a list
			tokens.add(token);
			// If the token is a valid word, create a map entry for it
			if (tokenizer.isWord(token)) {
			    // If an entry exists, add the index to the list
                // If not, create a new list with this index
                // Use .toLowerCase to make the search case insensitive
				if (contextMap.containsKey(token.toLowerCase())) {
					contextMap.get(token.toLowerCase()).add(index);
				} else {
					contextMap.put(token.toLowerCase(), new ArrayList<>(Arrays.asList(index)));
				}
			}
		}
	}
	
	/**
	 * 
	 * @param queryWord The word to search for in the file contents.
	 * @param contextWords The number of words of context to provide on
	 *                     each side of the query word.
	 * @return One context string for each time the query word appears in the file.
	 */
	public String[] search(String queryWord,int contextWords) {
	    // If the words was found, it is in the context map
        // Else return nothing
		if (contextMap.containsKey(queryWord.toLowerCase())){
		    // Grab all the matched words by index
			ArrayList<Integer> indices = contextMap.get(queryWord.toLowerCase());
			// Create a new container for holding the result strings
			ArrayList<String> contextStrings = new ArrayList<>();
			// Iterate over each match
			for (int index : indices){
			    // Create a string builder for adding context
			    String stringBuilder;
			    // Offsets are by context * 2. This is because for each word, there is one index of punctuation
			    // The start index is the index - context * 2.
			    int startIndex = index - contextWords*2;
			    // The stop index is the index + context * 2
			    int stopIndex = index + contextWords*2 ;
			    // Example text: The answer is always hashmap.
                // List form: ["The", " ", "answer", " ", "is", " ", "always", " ", "hashmap", "."]
                // Example query: "is"
                // Index: 4
                // Say context is 2
                // Start word is "The" at index 0
                // End Word is "hashmap" at index 8

                // If it would go before the start, reset index to 0
			    if (startIndex < 0){
			        startIndex = 0;
                }
                // If it would go past the end, reset index to last index of list
                if (stopIndex >= tokens.size()){
                    stopIndex = tokens.size()-1;
                }

                // Iterate over the indices and add them to the string builder
                // Everyone loves lambdas
                stringBuilder = IntStream
                        .rangeClosed(startIndex, stopIndex)
                        .mapToObj(i -> tokens.get(i))
                        .collect(Collectors.joining());

                // Create the string and add it to the output list
                contextStrings.add(stringBuilder);
            }
            // Convert from ArrayList to fixed Array
            return contextStrings.toArray(new String[0]);
		}
		else {
            return new String[0];
        }
	}
}

// Any needed utility classes can just go in this file

