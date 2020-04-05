package ie.gmit.sw;

import java.io.IOException;
import java.util.Comparator;
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

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

/**
 * == Node Parser == Idea of this class is to handle the incomming search
 * results from DuckDuckGo To then parse them into nodes Can go from there
 * 
 * @author morgan
 * 
 * 
 *         Best First Search -- Sorted Queue --> Priority Queue
 *
 */
public class NodeParser {
	private static final int MAX = 100; // visit 100 pages, then stop

	// heuristic creation -- example
	// TODO: Implement heuristics to match with DOM Tree -- Scrap below
	private static final int TITLE_WEIGHT = 50; // Lots of weight -- Found in title
	private static final int H1_WEIGHT = 30; // Significant weight -- Found in H1
	private static final int A_WEIGHT = 10; // Minimal Weight -- Found in body

	// Relate words to frequencies
	private Map<String, Integer> frecquencyMap = new ConcurrentHashMap<>(); // TODO: Map this to WordFrequency Array --
																			// All
	// wordcloud needs
	private String term; // Store search term to refer back to

	// Store nodes visited -- Concurrent version of TreeSet
	private Set<String> closedList = new ConcurrentSkipListSet<>();

	// Sorting by Heristic value
	// and also
	// Sorting based on score
	// :: => Method reference
	// TODO: can change this to a ConcurrentLinkList -- Can then push to front (DFS)
	private Queue<DocumentNode> queue = new PriorityQueue<>(Comparator.comparing(DocumentNode::getScore));

	// Constructor
	public NodeParser(String url, String searchTerm) throws IOException {
//		System.out.println("NodeParser()");
		this.term = searchTerm; // Get starting term
//		System.out.println("Search Term: " + this.term);
		Document doc = Jsoup.connect(url).get(); // Gives starting node -- Connect to webpage
//		System.out.println("Document | Start Node: " + doc);
		int score = getHeuristicScore(doc); // score web page
		System.out.println("Heuristic Score: " + score);
		closedList.add(url); // Track where we are -- fire onto close list
//		System.out.println("Close List: " + closedList.toString());
		queue.offer(new DocumentNode(doc, score)); // Fire doc and score onto priority queue
//		System.out.println("Open Queue: " + queue.toString());
		process(); // Start the ball rolling
	}

	// Start best first search
	public void process() {
		System.out.println("process()");
		// While the queue isn't empty and have not gone over max nodes
		while (!queue.isEmpty() && closedList.size() <= MAX) {
			DocumentNode node = queue.poll(); // Poll next element from front of queue
			Document doc = node.getDocument(); // Get document from node
			Elements edges = doc.select("a[href]"); // Get elements by: hyperlinks [href]

			// Loop over -- for each hyperlink
			for (Element e : edges) {
				// for each edge
				String link = e.absUrl("href"); // get absoute url

				// Null check && max check && link is not on closed listg
				if (link != null && closedList.size() <= MAX && !closedList.contains(link)) {
					try {
						closedList.add(link); // Don't want to re-visit -- add to closed list
						// Get child node and fire into the queue
						Document child = Jsoup.connect(link).get(); // cnnect to it -- html doc with link
						int score = getHeuristicScore(child); // score it
						queue.offer(new DocumentNode(child, score)); // get doc and score -- fire onto priorty queue
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	// Associate overall heuristic score to html doc
	// TODO: Track for nulls!
	// TODO: Can check for a lot of things here!! simple / complex heuristics
	// can check distance between words ... etc
	// this controlls how good the search will be
	private int getHeuristicScore(Document doc) {
		System.out.println("getHeuristicScore()");
		int score = 0;
		// Title
		String title = doc.title();
		int titleScore = getFrequency(title) * TITLE_WEIGHT; // Calculate heuristic
		System.out.println(closedList.size() + " --> " + title);

		// Heading
		// TODO: check for heading list
		int headingScore = 0;
		Elements headings = doc.select("h1"); // Walk jsoup tree
		for (Element heading : headings) {
			String h1 = heading.text(); // Gives text inside heading
//			System.out.println("\t" + h1);
			headingScore += getFrequency(h1) * H1_WEIGHT; // Calculate heuristic
		}

		// TODO: Uncomment and check for null so doesn't bomb out
//		String body = doc.body().text(); // TODO: check for null!!
//		body += getFrequency(body) * P_WEIGHT; // Calculate heuristic
//		int bodyScore = getFrequency(body) * P_WEIGHT;

		score = getFuzzyHeuristic(titleScore, headingScore, 0); // TODO: include bodyScore -- fix first (will bomb out)

		// TODO: Fix this! -- Watch video for reminder
//		if (score > MAX)
//			index(title, headings, body);
		return score; // return score after computation
	}

	// Want to look at title, heading, body
	// Then say does search term appear in them.
	private int getFrequency(String s) {
		System.out.println("getFreqeuncy()");
		// check for term (search term) in s
		//
		return 0;
	}

	// Assume Fuzzy Inference System is complete here
	// Asset strip lab
	// TODO: Do this in tandem with FIS file in ./res
	// TODO: Figure out return type!
	// TODO: Figure out heuristic values!
	private int getFuzzyHeuristic(int title, int headings, int body) {
		System.out.println("getFuzzyHeuristic()");
		String fileName = "./res/WordCloud.fcl"; // Set file name
		FIS fis = FIS.load(fileName, true); // Load file
		FunctionBlock fb = fis.getFunctionBlock("wordcloud");

		// Set inputs
		fis.setVariable("title", title);
		fis.setVariable("headings", headings);
		fis.setVariable("body", body);

		fis.evaluate(); // Fire inference system

		// Show output variable's chart
		Variable score = fb.getVariable("score");
		System.out.println("Fuzzy Heuristic Score: ");

		/*
		 * TODO: If fuzzy score is high -> call index on title, headings, body.
		 */

		// Print ruleset
		// System.out.println(fis);
		return 1;
	}

	// TODO: Can plug in NN in exact same way as FuzzyHeuristic

	// Indexing based on a score
	private void index(String... text) {
		System.out.println("index()");
		// Iterate over relevant text
		for (String s : text) {
			// Extract each word from string and add to map after filtering with ignore
			// words
		}
	}

	// Inner class to handle the documents visited
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

		public void setScore(int score) {
			this.score = score;
		}

	}

	// Code from Project Doc -- JSoup
	// Don't use this, use above approach
//	public void searchForQuery() throws IOException {
//		String query = "Forex Trading";
//		Document doc = Jsoup.connect("https://duckduckgo.com/html/?q=" + query).get();
//		Elements res = doc.getElementById("links").getElementsByClass("results_links");
//		for (Element r : res) {
//			Element title = r.getElementsByClass("links_main").first().getElementsByTag("a").first();
//			System.out.println("URL:\t" + title.attr("href"));
//			System.out.println("Title:\t" + title.text());
//			System.out.println("Text:\t" + r.getElementsByClass("result__snippet").first().wholeText());
//		}
//	}

	public static void main(String[] args) throws IOException {
		// Start
		new NodeParser("https://jsoup.org/cookbook/input/parse-document-from-string", "Java");
	}

}
