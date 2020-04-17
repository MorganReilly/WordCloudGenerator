package ie.gmit.sw;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.Map;

import javax.imageio.ImageIO;

import ie.gmit.sw.ai.cloud.LogarithmicSpiralPlacer;
import ie.gmit.sw.ai.cloud.WeightedFont;
import ie.gmit.sw.ai.cloud.WordFrequency;

/* 
 * Runner
 * 
 * This class is used to run the application,
 * and also to test and prepare before applying to ServiceHandler.java
 */
public class Runner {

	public void go(String fileName, String url, String search) throws Throwable {
		NodeParser np = new NodeParser(url, search);
		// Get a handle on some words and their frequencies in decending order
		WordFrequency[] words = new WeightedFont().getFontSizes(getWordFrequencyKeyValue(np.getSortedFrequencyMap()));
		Arrays.sort(words, Comparator.comparing(WordFrequency::getFrequency, Comparator.reverseOrder()));
		Arrays.stream(words).forEach(System.out::println);

		// Spira Mirabilis
		LogarithmicSpiralPlacer placer = new LogarithmicSpiralPlacer(800, 600);
		for (WordFrequency word : words) {
			placer.place(word); // Place each word on the canvas starting with the largest
		}

		BufferedImage cloud = placer.getImage(); // Get a handle on the word cloud graphic
		File outputfile = new File("img/" + fileName + ".png");
		try {
			ImageIO.write(cloud, "png", outputfile); // Write graphic to file
		} catch (IOException e) {
//			log.error("Could not create image: {}", e.getMessage());
			e.printStackTrace();
		}

//		String image = "<img src=\"data:image/png;base64," + encodeToString(cloud) + "\" alt=\"Word Cloud\" />";
//		BufferedImage wordCloud = decodeToImage(image);
	}

	private WordFrequency[] getWordFrequencyKeyValue(Map<String, Integer> map) {
		int MAX = 30;
		WordFrequency[] wf = new WordFrequency[MAX];

		int i = 0;
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			if (i == MAX)
				break;
			else {
				String word = entry.getKey();
				Integer frequency = entry.getValue();
				System.out.println("word: " + " freq: " + frequency);
				wf[i] = new WordFrequency(word, frequency);
				i++;
			}
		}

		return wf;
	}

	private String encodeToString(BufferedImage image) {
		String s = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			ImageIO.write(image, "png", bos);
			byte[] bytes = bos.toByteArray();

			Base64.Encoder encoder = Base64.getEncoder();
			s = encoder.encodeToString(bytes);
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}

	private BufferedImage decodeToImage(String imageString) {
		BufferedImage image = null;
		byte[] bytes;
		try {
			Base64.Decoder decoder = Base64.getDecoder();
			bytes = decoder.decode(imageString);
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			image = ImageIO.read(bis);
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	public static void main(String[] args) throws Throwable {
		String file = "saved";
		String testurl = "https://jsoup.org/cookbook/input/parse-document-from-string";
		String testterm = "Jsoup";

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter a query: ");
		String term = reader.readLine();

		System.out.println("Please enter a url to search query: ");
		String url = reader.readLine();

		new Runner().go(file, url, term);
	}
}
