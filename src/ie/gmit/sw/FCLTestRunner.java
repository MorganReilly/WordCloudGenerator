package ie.gmit.sw;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

/*
 * References: https://www.cs.princeton.edu/courses/archive/fall07/cos436/HIDDEN/Knapp/fuzzy004.htm
 * This class has the sole purpose of testing the FCL
 * 
 * Approach to the rule set was a Brute Force to get all possible combinations of rules.
 * Unsure which way was the best to devise a rule set so picked the best outcome of all values.
 */
public class FCLTestRunner {
	
	// TODO: Options for Seugno
//	private int option; // For test use with options on JSP
	
	public double getScore(double title, double headings, int body) {
		String fileName = "./res/WordCloud.fcl"; // Set file name
		FIS fis = FIS.load(fileName, true); // Load file
		FunctionBlock fb = fis.getFunctionBlock("wordcloud");
		
		fis.setVariable("title", title);
		fis.setVariable("headings", headings);
		fis.setVariable("body", body);
		
		JFuzzyChart.get().chart(fb);
		
		fis.evaluate(); // Fire inference system
		
		Variable score = fb.getVariable("score");
		JFuzzyChart.get().chart(score, score.getDefuzzifier(), true);
		
		return fb.getVariable("score").defuzzify();
	}
	
	public void runTest() {
		// Testset A -- Ruleset A
		System.out.println(new FCLTestRunner().getScore(10, 5, 4)); // PPP 1
//		System.out.println(new FCLTestRunner().getScore(10, 5, 21)); // PPE 2
//		System.out.println(new FCLTestRunner().getScore(10, 25, 4)); // PGP 3
//		System.out.println(new FCLTestRunner().getScore(10, 25, 21)); // PGE 4
//		System.out.println(new FCLTestRunner().getScore(10, 45, 4)); // PEP 5
//		System.out.println(new FCLTestRunner().getScore(10, 45, 21)); // PEE 6
		
		// Testset B -- Ruleset B
//		System.out.println(new FCLTestRunner().getScore(50, 6, 4)); // GPP 7 
//		System.out.println(new FCLTestRunner().getScore(50, 6, 21)); // GPE 8 
//		System.out.println(new FCLTestRunner().getScore(50, 25, 4)); // GGP 9
//		System.out.println(new FCLTestRunner().getScore(50, 25, 21)); // GGE 10
//		System.out.println(new FCLTestRunner().getScore(50, 45, 4)); // GEP 11
//		System.out.println(new FCLTestRunner().getScore(50, 45, 21)); // GEE 12
		
		// Testset C -- Ruleset C
//		System.out.println(new FCLTestRunner().getScore(50, 5, 4)); // EPP 13
//		System.out.println(new FCLTestRunner().getScore(90, 5, 21)); // EPE 14
//		System.out.println(new FCLTestRunner().getScore(90, 25, 4)); // EGP 15
//		System.out.println(new FCLTestRunner().getScore(90, 25, 21)); // EGE 16 
//		System.out.println(new FCLTestRunner().getScore(90, 45, 4)); // EEP 17
//		System.out.println(new FCLTestRunner().getScore(90, 45, 21)); // EEE 18
	}
	
	public static void main(String[] args) {
		new FCLTestRunner().runTest();
	}

}
