package ie.gmit.sw;

import ie.gmit.sw.ai.nn.BackpropagationTrainer;
import ie.gmit.sw.ai.nn.NeuralNetwork;
import ie.gmit.sw.ai.nn.Utils;
import ie.gmit.sw.ai.nn.activator.Activator;

public class NNTestRunner {

	public NNTestRunner() throws Exception {
		// 1. Get training data
		// TODO: Create some data based on heuristics and input

		// 2. Configure NN topology
		// TODO: Figure out if want hyperbolic or sigmoidal
		NeuralNetwork nn = new NeuralNetwork(Activator.ActivationFunction.HyperbolicTangent, 2, 2, 1);
//		NeuralNetwork nn = new NeuralNetwork(Activator.ActivationFunction.Sigmoid, 2, 2, 1);
		
		// 3. Train NN
		BackpropagationTrainer trainer = new BackpropagationTrainer(nn);
//		trainer.train(data, expected, alpha, epochs); //TODO: Fill in values
		// If data has a high degree with variablility
		// use this trainer
//		trainer.train(Utils.normalize(double[] vector/data, double lower, double upper), expected, alpha, epochs);
		// Upper / lower values could be anywhere from 0-1 to 0-100 --> Based on what I need
		
		// 4. Test NN
		// TODO: Create some tests
	}

	public static void main(String[] args) throws Exception {
		new NNTestRunner();
	}

}
