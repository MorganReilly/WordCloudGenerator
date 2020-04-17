package ie.gmit.sw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ie.gmit.sw.fcl.FuzzyController;
import net.sourceforge.jFuzzyLogic.rule.Variable;

/**
 * == Node Parser ==
 * 
 * This class handles the traversal of nodes which are queried from Jsoup and
 * search through the Best Frist Search
 * 
 * @author morgan
 */
public class NodeParser {

	private MapController mapController = new MapController();
	private static final int MAX_NODES_TO_VISIT = 50;

	/* Heuristic Constants */
	private static final int TITLE_WEIGHT = 50; // Lots of weight -- Found in title
	private static final int H1_WEIGHT = 20; // Significant weight -- Found in H1
	private static final int H2_WEIGHT = 15;
	private static final int H3_WEIGHT = 10;
	private static final int PARAGRAPH_WEIGHT = 5; // Minimal Weight -- Found in body

	/* Wordcloud Search Term */
	private String term; // Store search term to refer back to

	/* Relate Words to Frequencies */
	private Map<String, Integer> frequencyMap = new ConcurrentHashMap<>(); // Total frequency of all words
	private Map<String, Integer> sortedFrequencyMap = new ConcurrentHashMap<>(); // Total frequency of all words
	private Map<String, Integer> frequencyInTitleMap = new ConcurrentHashMap<>(); // Total frequency in title
	private Map<String, Integer> frequencyInH1Map = new ConcurrentHashMap<>(); // Total frequency in h1
	private Map<String, Integer> frequencyInH2Map = new ConcurrentHashMap<>(); // Total frequency in h2
	private Map<String, Integer> frequencyInH3Map = new ConcurrentHashMap<>(); // Total frequency in h3
	private Map<String, Integer> frequencyInPMap = new ConcurrentHashMap<>(); // Total frequency in p

	/* Visited Nodes -- Closed List */
	private Set<String> closedList = new ConcurrentSkipListSet<>();

	/* Active Queue */
	private Queue<DocumentNode> openList = new PriorityQueue<>(Comparator.comparing(DocumentNode::getScore));

	/* Ignore List */
	private List<String> ignoreList = new ArrayList<String>();
	private List<String> concurrentIgnoreList = Collections.synchronizedList(ignoreList);

	public Map<String, Integer> getFrequencyMap() {
		return frequencyMap;
	}

	public void setFrequencyMap(Map<String, Integer> frequencyMap) {
		this.frequencyMap = frequencyMap;
	}

	public Map<String, Integer> getFrequencyInTitleMap() {
		return frequencyInTitleMap;
	}

	public void setFrequencyInTitleMap(Map<String, Integer> frequencyInTitleMap) {
		this.frequencyInTitleMap = frequencyInTitleMap;
	}

	public Map<String, Integer> getFrequencyInH1Map() {
		return frequencyInH1Map;
	}

	public void setFrequencyInH1Map(Map<String, Integer> frequencyInH1Map) {
		this.frequencyInH1Map = frequencyInH1Map;
	}

	public Map<String, Integer> getFrequencyInH2Map() {
		return frequencyInH2Map;
	}

	public void setFrequencyInH2Map(Map<String, Integer> frequencyInH2Map) {
		this.frequencyInH2Map = frequencyInH2Map;
	}

	public Map<String, Integer> getFrequencyInH3Map() {
		return frequencyInH3Map;
	}

	public void setFrequencyInH3Map(Map<String, Integer> frequencyInH3Map) {
		this.frequencyInH3Map = frequencyInH3Map;
	}

	public Map<String, Integer> getFrequencyInPMap() {
		return frequencyInPMap;
	}

	public void setFrequencyInPMap(Map<String, Integer> frequencyInPMap) {
		this.frequencyInPMap = frequencyInPMap;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public void setIgnoreList(List<String> ignoreList) {
		this.ignoreList = ignoreList;
	}

	public Map<String, Integer> getSortedFrequencyMap() {
		return sortedFrequencyMap;
	}

	public void setSortedFrequencyMap(Map<String, Integer> sortedFrequencyMap) {
		this.sortedFrequencyMap = sortedFrequencyMap;
	}

	/* Constructor */
	public NodeParser(String url, String searchTerm) throws Exception {
		this.concurrentIgnoreList = new IgnoreList().getIgnoreList();
		setTerm(searchTerm);

		System.out.println("Search Term Found: " + this.term + "\nParsing...\n");
		Document doc = Jsoup.connect(url).get(); // Gives starting node -- Connect to webpage/url
		int score = getHeuristicScore(doc); // hueristically score web page
		closedList.add(url); // Track where we are -- fire visited url onto closed list
		openList.offer(new DocumentNode(doc, score)); // Fire doc and score onto priority queue
		bfs(); // Start bfs
		sortedFrequencyMap = mapController.sortFrequencyMap(frequencyMap); // Sort all of the words that were found
	}

	/* Best First Search */
	public void bfs() throws Exception {
		while (!openList.isEmpty() && closedList.size() < MAX_NODES_TO_VISIT) {
			DocumentNode node = openList.poll(); // Poll next element from front of queue
			Document jsoupDoc = node.getDocument(); // Get document from node
			Elements edges = jsoupDoc.select("a[href]"); // Get elements by: hyperlinks [href]
			// Loop over -- for each hyperlink/edge
			for (Element e : edges) {
				String link = e.absUrl("href"); // Get absolute URL
				// Null check && max check && link is not on closed listg
				if (link != null && closedList.size() < MAX_NODES_TO_VISIT && !closedList.contains(link)) {
					try {
						closedList.add(link); // Don't want to re-visit -- add to closed list
						Document child = Jsoup.connect(link).get(); // cnnect to it -- html doc with link
						int score = getHeuristicScore(child); // score it
						openList.offer(new DocumentNode(child, score)); // get doc and score -- fire onto priorty queue
					} catch (IOException ex) {
					}
				}
			}
		}
	}

	/*
	 * Get Heuristic Score
	 * 
	 * This is done by scraping, parsing and storing all of the page words. Then by
	 * iterating through each store to see if the term occurs.
	 */
	private int getHeuristicScore(Document doc) throws Exception {
		int score = 0;
		System.out.println("Generating Heuristic...");
		try {
			System.out.println();
			/* Scrape Text */
			scrapeText(doc);
			score = calculateHeuristic();
			System.out.println("\nPage Score: " + score);

			System.out.println("Closed List Size: " + closedList.size() + "\n");
			return score;
		} catch (NullPointerException ex) {
		}
		return score;
	}

	/*
	 * Scrape Text
	 * 
	 * This class scrapes the text from each required element checks if it is not in
	 * the ignoreList and adds them to their own list for sorting and scoring.
	 */
	private void scrapeText(Document doc) {
		System.out.println("Scraping Text...");

		String[] title, h1, h2, h3, p;
		int i;
		Elements body, h1Headings, h2Headings, h3Headings;

		List<String> scrapedList = new ArrayList<String>(); // Using this for all of the words
		List<String> titleList = new ArrayList<String>();
		List<String> h1List = new ArrayList<String>();
		List<String> h2List = new ArrayList<String>();
		List<String> h3List = new ArrayList<String>();
		List<String> pList = new ArrayList<String>();

		/* Title */
		title = doc.title().toLowerCase().split("[^A-Za-z0-9]");
		for (i = 0; i < title.length; i++) {
			if (title != null) {
				title[i] = title[i].trim();
				if (!this.ignoreList.contains(title[i]) && title[i] != null && !title[i].isEmpty()) {
					titleList.add(title[i]);
					scrapedList.add(title[i]);
				}
			}
		}
		/* Heading -- H1 */
		h1Headings = doc.select("h1"); // Walk jsoup tree
		if (h1Headings != null) {
			for (Element heading : h1Headings) {
				h1 = heading.text().toLowerCase().split("[^A-Za-z0-9]");
				for (i = 0; i < h1.length; i++) {
					h1[i] = h1[i].trim();
					if (!this.ignoreList.contains(h1[i]) && h1[i] != null && !h1[i].isEmpty()) {
						h1List.add(h1[i]);
						scrapedList.add(h1[i]);
					}
				}
			}

		}
		/* Heading -- H2 */
		h2Headings = doc.select("h2"); // Walk jsoup tree
		if (h2Headings != null) {
			for (Element heading : h2Headings) {
				h2 = heading.text().toLowerCase().split("[^A-Za-z0-9]");
				for (i = 0; i < h2.length; i++) {
					h2[i] = h2[i].trim();
					if (!this.ignoreList.contains(h2[i]) && h2[i] != null && !h2[i].isEmpty()) {
						h2List.add(h2[i]);
						scrapedList.add(h2[i]);
					}
				}
			}
		}
		/* Heading -- H3 */
		h3Headings = doc.select("h3"); // Walk jsoup tree
		if (h3Headings != null) {
			for (Element heading : h3Headings) {
				h3 = heading.text().toLowerCase().split("[^A-Za-z0-9]");
				for (i = 0; i < h3.length; i++) {
					h3[i] = h3[i].trim();
					if (!this.ignoreList.contains(h3[i]) && h3[i] != null && !h3[i].isEmpty()) {
						h3List.add(h3[i]);
						scrapedList.add(h3[i]);
					}
				}
			}
		}
		/* Body -- Paragraph */
		body = doc.select("p");
		if (body != null) {
			for (Element bdy : body) {
				p = bdy.text().toLowerCase().split("[^A-Za-z0-9]");
				for (i = 0; i < p.length; i++) {
					p[i] = p[i].trim();
					if (!this.ignoreList.contains(p[i]) && p[i] != null && !p[i].isEmpty()) {
						pList.add(p[i]);
						scrapedList.add(p[i]);
					}
				}
			}
		}
		System.out.println("Text Scraped\nGenerating Frequency Maps...");
		Map<String, Integer> freqMap = mapController.countFrequencies(scrapedList);
		Map<String, Integer> freqTitleMap = mapController.countFrequencies(titleList);
		Map<String, Integer> freqH1Map = mapController.countFrequencies(h1List);
		Map<String, Integer> freqH2Map = mapController.countFrequencies(h2List);
		Map<String, Integer> freqH3Map = mapController.countFrequencies(h3List);
		Map<String, Integer> freqPMap = mapController.countFrequencies(pList);
		setFrequencyInTitleMap(freqTitleMap);
		setFrequencyInH1Map(freqH1Map);
		setFrequencyInH2Map(freqH2Map);
		setFrequencyInH3Map(freqH3Map);
		setFrequencyInPMap(freqPMap);
		System.out.println("Frequency Maps Generated");

		// Put all found entries into unsorted frequency map
		for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
			String word = entry.getKey();
			Integer frequency = entry.getValue();

			this.frequencyMap.merge(word, frequency, Integer::sum);
		}
	}

	/*
	 * Calculate Heuristic
	 * 
	 * This is done by checking if the search term has appeared in any of the
	 * frequency maps. If so, it multiplies the frequency of appearance by a weight
	 * for where it was found.
	 */
	private int calculateHeuristic() {
		System.out.println("Calculating Heuristic...");

		Map<String, Integer> freqTitleMap = getFrequencyInTitleMap();
		Map<String, Integer> freqH1Map = getFrequencyInH1Map();
		Map<String, Integer> freqH2Map = getFrequencyInH2Map();
		Map<String, Integer> freqH3Map = getFrequencyInH3Map();
		Map<String, Integer> freqPMap = getFrequencyInPMap();

		int score, titleScore, h1Score, h2Score, h3Score, pScore;
		score = titleScore = h1Score = h2Score = h3Score = pScore = 0;

		String term = getTerm().toLowerCase(); // Get the search term

		/* Fing in Title */
		for (Map.Entry<String, Integer> entry : freqTitleMap.entrySet()) {
			String word = entry.getKey();
			Integer frequency = entry.getValue();

			if (word.equals(term)) {
				score += frequency * TITLE_WEIGHT;
				titleScore += frequency * TITLE_WEIGHT;
				System.out.println("Title Score --> " + titleScore);
			}
		}

		/* Find in H1 */
		for (Map.Entry<String, Integer> entry : freqH1Map.entrySet()) {
			String word = entry.getKey();
			Integer frequency = entry.getValue();

			if (word.equals(term)) {
				score += frequency * H1_WEIGHT;
				h1Score += frequency * H1_WEIGHT;
				System.out.println("H1 score --> " + h1Score);
			}
		}

		/* Find in H2 */
		for (Map.Entry<String, Integer> entry : freqH2Map.entrySet()) {
			String word = entry.getKey();
			Integer frequency = entry.getValue();

			if (word.equals(term)) {
				score += frequency * H2_WEIGHT;
				h2Score += frequency * H2_WEIGHT;
				System.out.println("H2 score --> " + h2Score);
			}
		}

		/* Find in H3 */
		for (Map.Entry<String, Integer> entry : freqH3Map.entrySet()) {
			String word = entry.getKey();
			Integer frequency = entry.getValue();

			if (word.equals(term)) {
				score += frequency * H3_WEIGHT;
				h3Score += frequency * H3_WEIGHT;
				System.out.println("H3 Score --> " + h3Score);
			}
		}

		/* Find in Paragraph */
		for (Map.Entry<String, Integer> entry : freqPMap.entrySet()) {
			String word = entry.getKey();
			Integer frequency = entry.getValue();

			if (word.equals(term)) {
				score += frequency * PARAGRAPH_WEIGHT;
				pScore += frequency * PARAGRAPH_WEIGHT;
				System.out.println("Paragraph Score --> " + pScore);
			}
		}

		/* Term Not Found */
		if (score == 0) {
			System.out.println("Term: '" + term + "' Not Found On Page");
		} else {
			/* Fire Fuzzy Inference System */
			Variable fuzzyScore = new FuzzyController().getFuzzyHeuristic(titleScore, h1Score, h2Score, h3Score,
					pScore);
			System.out.println("FUZZY OUTPUT: " + fuzzyScore);

			score = (int) fuzzyScore.getValue();
		}

		return score;
	}

	/*
	 * DocumentNode
	 * 
	 * Handle the Documents Visited
	 */
	private class DocumentNode {
		private Document document;
		private int score;

		public DocumentNode(Document d, int score) {
			super();
			this.document = d;
			this.score = score;
		}

		public Document getDocument() {
			return document;
		}

		public int getScore() {
			return score;
		}
	}

	/* Test Class */
	public static void main(String[] args) throws Exception {
		// Start
		new NodeParser("https://jsoup.org/cookbook/input/parse-document-from-string", "Jsoup");
	}

}
