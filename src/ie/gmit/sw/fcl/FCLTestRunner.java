package ie.gmit.sw.fcl;

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

	public double getScore(int titleScore, int h1Score, int h2Score, int h3Score, int pScore) {
//		String fileName = "./res/WordCloud.fcl"; // Set file name
		String fileName = "./res/WordCloudSueg.fcl";
		FIS fis = FIS.load(fileName, true); // Load file
//		FunctionBlock fb = fis.getFunctionBlock("wordcloud");
		FunctionBlock fb = fis.getFunctionBlock("wordcloudsugeno");

		// Set inputs
		fis.setVariable("title", titleScore);
		fis.setVariable("h1", h1Score);
		fis.setVariable("h2", h2Score);
		fis.setVariable("h3", h3Score);
		fis.setVariable("paragraph", pScore);

		JFuzzyChart.get().chart(fb);

		fis.evaluate(); // Fire inference system

		Variable score = fb.getVariable("score");
		JFuzzyChart.get().chart(score, score.getDefuzzifier(), true);

		return fb.getVariable("score").defuzzify();
	}

	public void runTest() {
		// Testset A -- Ruleset A
		System.out.println(new FCLTestRunner().getScore(20, 10, 0, 0, 0));
	}

	public static void main(String[] args) {
		new FCLTestRunner().runTest();
	}

}
