/* Ido Cohen	Guy Cohen	203516992	304840283 */
package InputOutput;

import java.util.*;

public class LidstoneModel 
{
	static String unseenWord = "unseen-word";

	public static double CalcPLidstone(double lambda, Map<String, Integer> trainMap, String inputWord)
	{
		long eventsInTraining = DataClass.wordsTotalAmount(trainMap);

		return CalcPLidstone(lambda, trainMap, eventsInTraining, inputWord);
	}

	/*
	 * Returns Lidstone smoothing, by the formula shown in class
	 */
	public static double CalcPLidstone(double lambda, Map<String, Integer> map, long mapSize, String inputWord)
	{
		int occurences = map.get(inputWord) == null ? 0 : map.get(inputWord);

		return (occurences + lambda)/(mapSize + lambda*Output.vocabulary_size); 
	}

	/*
	 * Returns Lidstone smoothing, by the formula shown in class
	 */
	public static double CalcPLidstone(double lambda, Map<String, Integer> trainMap, int occurences)
	{
		long eventsInTraining = DataClass.wordsTotalAmount(trainMap);

		return (occurences + lambda)/(eventsInTraining + lambda*Output.vocabulary_size); 
	}

	/*
	 * Checks the sum of p words is 1
	 */
	public static void modelSanityCheck(double lambda, Map<String, Integer> trainMap)
	{
		long N0 = Output.vocabulary_size - trainMap.keySet().size();
		long trainingSize = DataClass.wordsTotalAmount(trainMap);

		double sum = 0;

		// Contribution of all words that don't appear in the training set (unseen events)
		sum += N0 * CalcPLidstone(lambda, trainMap, trainingSize, unseenWord);

		// add contribution of each word
		for (String word : trainMap.keySet())
		{
			sum += LidstoneModel.CalcPLidstone(lambda, trainMap, trainingSize, word);
		}

		// Prevent inaccuracies by java double calculations
		double epsilon = 0.000000000000002;
		if (Math.abs(1 - sum) < epsilon)
		{
			Output.writeConsoleWhenTrue("Lidstone is GOOD!");
		}
		else
		{
			Output.writeConsoleWhenTrue("Lidstone is BAD. Value: " + sum);
		}

	}
}
