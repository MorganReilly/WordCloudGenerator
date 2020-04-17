package ie.gmit.sw.fcl;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class FuzzyController {
	
	/* Fuzzy Control Logic Runner */
	public Variable getFuzzyHeuristic(int titleScore, int h1Score, int h2Score, int h3Score, int pScore) {
//		System.out.println("getFuzzyHeuristic()");
		String fileName = "./res/WordCloud.fcl"; // Set file name
		FIS fis = FIS.load(fileName, true); // Load file
		FunctionBlock fb = fis.getFunctionBlock("wordcloud");

		// Set inputs
		fis.setVariable("title", titleScore);
		fis.setVariable("h1", h1Score);
		fis.setVariable("h2", h2Score);
		fis.setVariable("h3", h3Score);
		fis.setVariable("paragraph", pScore);

		fis.evaluate(); // Fire inference system

		// Show output variable's chart
		Variable score = fb.getVariable("score");
		return score;
	}
}
