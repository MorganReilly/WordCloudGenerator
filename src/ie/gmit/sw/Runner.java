package ie.gmit.sw;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;

import javax.imageio.ImageIO;

import ie.gmit.sw.ai.cloud.LogarithmicSpiralPlacer;
import ie.gmit.sw.ai.cloud.WeightedFont;
import ie.gmit.sw.ai.cloud.WordFrequency;

public class Runner {

	public void go(String file) throws Throwable {
		// Get a handle on some words and their frequencies in decending order
		WordFrequency[] words = new WeightedFont().getFontSizes(getWordFrequencyKeyValue());
		Arrays.sort(words, Comparator.comparing(WordFrequency::getFrequency, Comparator.reverseOrder()));
		Arrays.stream(words).forEach(System.out::println);

		// Spira Mirabilis
		LogarithmicSpiralPlacer placer = new LogarithmicSpiralPlacer(800, 600);
		for (WordFrequency word : words) {
			placer.place(word); // Place each word on the canvas starting with the largest
		}

		BufferedImage cloud = placer.getImage(); // Get a handle on the word cloud graphic
		File outputfile = new File("saved.png");
		ImageIO.write(cloud, "png", outputfile); // Write graphic to file

//		String image = "<img src=\"data:image/png;base64," + encodeToString(cloud) + "\" alt=\"Word Cloud\" />";
//		BufferedImage wordCloud = decodeToImage(image);
	}

	// Need to change
	// Plug in own values
	// A sample array of WordFrequency for demonstration purposes
	private WordFrequency[] getWordFrequencyKeyValue() {
		WordFrequency[] wf = new WordFrequency[32];
		wf[0] = new WordFrequency("Galway", 65476);
		wf[1] = new WordFrequency("Sligo", 43242);
		wf[2] = new WordFrequency("Roscommon", 2357);
		wf[4] = new WordFrequency("Clare", 997);
		wf[5] = new WordFrequency("Donegal", 876);
		wf[17] = new WordFrequency("Armagh", 75);
		wf[6] = new WordFrequency("Waterford", 811);
		wf[7] = new WordFrequency("Tipperary", 765);
		wf[8] = new WordFrequency("Westmeath", 643);
		wf[9] = new WordFrequency("Leitrim", 543);
		wf[10] = new WordFrequency("Mayo", 456);
		wf[11] = new WordFrequency("Offaly", 321);
		wf[12] = new WordFrequency("Kerry", 221);
		wf[13] = new WordFrequency("Meath", 101);
		wf[14] = new WordFrequency("Wicklow", 98);
		wf[18] = new WordFrequency("Antrim", 67);
		wf[3] = new WordFrequency("Limerick", 1099);
		wf[15] = new WordFrequency("Kildare", 89);
		wf[16] = new WordFrequency("Fermanagh", 81);
		wf[19] = new WordFrequency("Dublin", 12);
		wf[20] = new WordFrequency("Carlow", 342);
		wf[21] = new WordFrequency("Cavan", 234);
		wf[22] = new WordFrequency("Down", 65);
		wf[23] = new WordFrequency("Kilkenny", 45);
		wf[24] = new WordFrequency("Laois", 345);
		wf[25] = new WordFrequency("Derry", 7);
		wf[26] = new WordFrequency("Longford", 8);
		wf[27] = new WordFrequency("Louth", 34);
		wf[28] = new WordFrequency("Monaghan", 101);
		wf[29] = new WordFrequency("Tyrone", 121);
		wf[30] = new WordFrequency("Wexford", 144);
		wf[31] = new WordFrequency("Cork", 522);
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
		String file = "/home/morgan/eclipse-workspace/AIProject/res/png";
		new Runner().go(file);
	}
}
