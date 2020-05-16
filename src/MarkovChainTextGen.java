import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.LinkedList;
import java.io.*;
import java.util.List;

/** 
 * Given source text input, will train markov chain to generate similar random word string.
 */
public class MarkovChainTextGen{
	private LinkedList<ListNode> wordList;
	private String firstWord;
	private String sourceText;
	
	// The random number generator
	private Random rng;
	
	public MarkovChainTextGen(Random rng)
	{
		this.wordList = new LinkedList<>();
		this.firstWord = "";
		this.rng = rng;
	}
	/** Generate Markov chains with words. */
	public void train() {
		train(this.sourceText);
	}
	/** Generate Markov chains with words. */
	public void train(String sourceText)
	{
		String[] words = sourceText.split(" +");
		firstWord = words[0];

		int wordCount = 0;
		for (String word : words) {
			// is it a new word?
			if (!findWord(this.wordList, word)) {
				// new root word
				ListNode newNode = new ListNode(word);
				if (words.length > wordCount + 1) { newNode.addNextWord(words[wordCount + 1]); } else {newNode.addNextWord(firstWord);}
				wordList.add(newNode);
			} else {
				for (ListNode node : wordList) {
					if (node.getWord().equals(word)) {
						if (words.length > wordCount + 1) { node.addNextWord(words[wordCount + 1]); } else {node.addNextWord(firstWord);}
					}
				}
			}
			wordCount++;
		}
	}

	/**
	 * Search world list for a given word as root.
	 * @param wordList list of root words
	 * @param word word to search list roots for
	 * @return boolean representing whether a match was found
	 */
	private boolean findWord(LinkedList<ListNode> wordList, String word) {
		for (ListNode node : wordList) {
			if (node.getWord().equals(word)) return true;
		}
		return false;
	}
	/** 
	 * Generate the give number of words requested.
	 */
	public String generateText(int numWords) {
	    ListNode node = wordList.get(0);
	    StringBuilder s = new StringBuilder(node.getWord());

		for (int i=0; i<numWords; i++) {
			assert node != null;
			String newWord = node.getRandomNextWord(rng);
			s.append(" ").append(newWord);
	    	node = getNode(this.wordList, newWord);
		}
		return s.toString();
	}

	/**
	 * Search world list for a given word as root.
	 * @param wordList list of root words
	 * @param word word to search list roots for
	 * @return node representing the found match
	 */
	private ListNode getNode(LinkedList<ListNode> wordList, String word) {
		for (ListNode node : wordList) {
			if (node.getWord().equals(word)) return node;
		}
		return null;
	}

	@Override
	public String toString()
	{
		StringBuilder toReturn = new StringBuilder();
		for (ListNode n : wordList)
		{
			toReturn.append(n.toString());
		}
		return toReturn.toString();
	}
	
	/** erase source text from world list to regenerate markov chains */
	public void retrain(String sourceText)
	{
		wordList.clear();
		train(sourceText);
	}

	/** Given filepath loads string representation into sourceText.
	 * @param file filepath
	 * @throws IOException
	 */
	public void loadFileAsString(String file) throws IOException {
		String text;
		text = new String(Files.readAllBytes(Paths.get(file)));
		this.sourceText = text;
	}

	/**
	 * This method runs some simple tests on the Markov chain gen.
	 */
	public static void main(String[] args) throws IOException {
		MarkovChainTextGen mctg = new MarkovChainTextGen(new Random(137));
		mctg.loadFileAsString("data/testWords.txt");
		mctg.train();
		System.out.println(mctg);
		System.out.println(mctg.generateText(33));
	}

}

/** Links a word to the next words in the list 
 * You should use this class in your implementation. */
class ListNode
{
    // The word that is linking to the next words
	private String word;
	
	// The next words that could follow it
	private List<String> nextWords;
	
	ListNode(String word)
	{
		this.word = word;
		nextWords = new LinkedList<>();
	}
	
	public String getWord()
	{
		return word;
	}

	public void addNextWord(String nextWord)
	{
		nextWords.add(nextWord);
	}
	
	public String getRandomNextWord(Random generator)
	{

		int index = generator.nextInt(this.nextWords.size());
		return this.nextWords.get(index);
	}

	public String toString()
	{
		StringBuilder toReturn = new StringBuilder(word + ": ");
		for (String s : nextWords) {
			toReturn.append(s).append("=>");
		}
		toReturn.append("\n");
		return toReturn.toString();
	}
}


