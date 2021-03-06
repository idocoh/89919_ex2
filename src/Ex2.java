/* Ido Cohen	Guy Cohen	203516992	304840283 */
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import InputOutput.DataClass;
import InputOutput.HeldOutModel;
import InputOutput.LidstoneModel;
import InputOutput.Output;

public class Ex2 {

	static String unseenWord = "unseen-word";

	public static void main(String[] args) {

		String devl_inputFile = args[0];
		String test_inputFile = args[1];
		String inputWord = args[2];
		String outputFile = args[3];

		Output outputClass = new Output(outputFile);

		//Output init
		outputClass.writeNames();
		outputClass.writeOutput(devl_inputFile);
		outputClass.writeOutput(test_inputFile);
		outputClass.writeOutput(inputWord);

		outputClass.writeOutput(outputFile);
		outputClass.writeOutput(Output.vocabulary_size);
		outputClass.writeOutput(1.0/Output.vocabulary_size);

		try 
		{
			DataClass devData = new DataClass();
			devData.readInputFile(devl_inputFile);

			// Output 7
			outputClass.writeOutput(devData.getTotalWordsInDocs());

			Map<String, Integer> lidstoneTrainMap = new TreeMap<String, Integer>();
			Map<String, Integer> validationMap  = new TreeMap<String, Integer>();
			devData.splitXPrecentOfDocsWords(0.9, lidstoneTrainMap, validationMap);

			// Output 8
			outputClass.writeOutput(DataClass.wordsTotalAmount(validationMap));

			// Output 9
			outputClass.writeOutput(DataClass.wordsTotalAmount(lidstoneTrainMap));

			// Output 10
			outputClass.writeOutput(lidstoneTrainMap.keySet().size()); 

			// Output 11
			outputClass.writeOutput(getNumberOfOccurences(lidstoneTrainMap, inputWord));

			// Output 12
			outputClass.writeOutput(calcPMle(lidstoneTrainMap, inputWord));

			// Output 13
			outputClass.writeOutput(calcPMle(lidstoneTrainMap, unseenWord));

			double lambda = 0.1;			

			// Output 14
			outputClass.writeOutput(LidstoneModel.CalcPLidstone(lambda, lidstoneTrainMap, inputWord));

			// Output 15
			outputClass.writeOutput(LidstoneModel.CalcPLidstone(lambda, lidstoneTrainMap, unseenWord));

			// Output 16
			outputClass.writeOutput(calculatePerplexityByLidstone(0.01, lidstoneTrainMap, validationMap));

			// Output 17
			outputClass.writeOutput(calculatePerplexityByLidstone(0.10, lidstoneTrainMap, validationMap));

			// Output 18
			outputClass.writeOutput(calculatePerplexityByLidstone(1.00, lidstoneTrainMap, validationMap));

			double bestLambda = getBestLambda(lidstoneTrainMap, validationMap);

			// Output 19
			outputClass.writeOutput(bestLambda);

			// Output 20
			outputClass.writeOutput(calculatePerplexityByLidstone(bestLambda, lidstoneTrainMap, validationMap));

			Map<String, Integer> heldOutTrainMap = new TreeMap<String, Integer>();
			Map<String, Integer> heldOutMap  = new TreeMap<String, Integer>();
			devData.splitXPrecentOfDocsWords(0.5, heldOutTrainMap, heldOutMap);

			// Output 21
			outputClass.writeOutput(DataClass.wordsTotalAmount(heldOutTrainMap));

			// Output 22
			outputClass.writeOutput(DataClass.wordsTotalAmount(heldOutMap));

			// Output 23
			outputClass.writeOutput(HeldOutModel.CalcPHeldOut(heldOutTrainMap, heldOutMap, inputWord));

			// Output 24
			outputClass.writeOutput(HeldOutModel.CalcPHeldOut(heldOutTrainMap, heldOutMap, unseenWord));

			lambda = 0.1;
			LidstoneModel.modelSanityCheck(lambda, lidstoneTrainMap);
			HeldOutModel.modelSanityCheck(heldOutTrainMap, heldOutMap);

			DataClass testData = new DataClass();
			testData.readInputFile(test_inputFile);

			// Output 25
			outputClass.writeOutput(testData.getTotalWordsInDocs());

			double lidstonePerplexity = calculatePerplexityByLidstone(bestLambda, lidstoneTrainMap, testData.getMapTotalDocsWords());

			// Output 26
			outputClass.writeOutput(lidstonePerplexity);

			double heldOutPerplexity = calculatePerplexityByHeldOut(heldOutTrainMap, heldOutMap, testData.getMapTotalDocsWords());

			// output 27
			outputClass.writeOutput(heldOutPerplexity);

			// output 28 
			outputClass.writeOutput(lidstonePerplexity <= heldOutPerplexity ? "L" : "H"); 

			// Output 29
			outputClass.writeOutput("");
			for (int i = 0; i < 10; i++)
			{
				double fr = DataClass.wordsTotalAmount(lidstoneTrainMap) * LidstoneModel.CalcPLidstone(bestLambda, lidstoneTrainMap, i); 
				double fH = DataClass.wordsTotalAmount(heldOutTrainMap) * HeldOutModel.CalcPHeldOut(heldOutTrainMap, heldOutMap, i);
				long Nr = HeldOutModel.calcNr(heldOutTrainMap, i);
				long tr = HeldOutModel.calcTr(heldOutTrainMap, heldOutMap, i);
				outputClass.writeOutputFile("\n"+ i + "\t" + String.format("%.5f", fr) + "\t" + String.format("%.5f", fH) + "\t" + Nr + "\t" + tr + "\t");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}			
	}	

	/*
	 * Returns the Maximum likelihood estimation for the word, by the training map
	 */
	private static double calcPMle(Map<String, Integer> trainMap, String word)
	{
		int wordOccurences = getNumberOfOccurences(trainMap, word);
		long eventsInTraining = DataClass.wordsTotalAmount(trainMap);

		return (double)wordOccurences/eventsInTraining;
	}

	private static int getNumberOfOccurences(Map<String, Integer> map, String word)
	{
		return map.get(word) == null ? 0 : map.get(word);
	}

	/*
	 * Returns model perplexity
	 */
	private static double calculatePerplexityByLidstone(double lambda, Map<String, Integer> trainingMap, Map<String, Integer> validationMap) 
	{		
		double sumPWords = 0;

		long trainingSize = DataClass.wordsTotalAmount(trainingMap);

		for (String word : validationMap.keySet())
		{
			double pWord = LidstoneModel.CalcPLidstone(lambda, trainingMap, trainingSize, word);
			
			// adds the probability to the sum occurrences time (as the number of occurrences in the validation map)
			sumPWords += validationMap.get(word) * Math.log(pWord)/Math.log(2);
		}

		long validationWordsSize = DataClass.wordsTotalAmount(validationMap);
		
		double perplexity = Math.pow(2,(-1.0/validationWordsSize) * sumPWords); 
		return perplexity;
	}

	private static double calculatePerplexityByHeldOut(Map<String, Integer> heldOutTrainMap, Map<String, Integer> heldOutMap, Map<String, Integer> validationMap)
	{
		double sumPWords = 0;

		for (String word : validationMap.keySet())
		{
			double pWord = HeldOutModel.CalcPHeldOut(heldOutTrainMap, heldOutMap, word);
			
			// adds the probability to the sum occurrences time (as the number of occurrences in the validation map)
			sumPWords += validationMap.get(word) * Math.log(pWord)/Math.log(2);
		}

		long validationWordsSize = DataClass.wordsTotalAmount(validationMap);

		double perplexity = Math.pow(2,(-1.0/validationWordsSize) * sumPWords); 
		return perplexity;
	}

	private static double getBestLambda(Map<String, Integer> trainingMap, Map<String, Integer> validationMap)
	{
		int bestLambdaIndex = 0;
		double bestPerplexityValue = calculatePerplexityByLidstone(0, trainingMap, validationMap);

		double perplexity;

		// iterate over the lambdas from 0.1 to 2.0 (1 to 200 divided by 100, for accuracies) 
		for (int lambdaIndex = 1; lambdaIndex <= 200; lambdaIndex++)
		{
			// calculate the perplexity by this lambda
			perplexity = calculatePerplexityByLidstone(lambdaIndex/100.0, trainingMap, validationMap);

			// compare to the best lambda perplexity value thus far
			if (perplexity < bestPerplexityValue)
			{
				bestLambdaIndex = lambdaIndex;
				bestPerplexityValue = perplexity;
			}
		}

		// return the best lambda
		return bestLambdaIndex/100.0;
	}
}
