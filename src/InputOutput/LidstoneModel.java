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

	public static double CalcPLidstone(double lambda, Map<String, Integer> map, long mapSize, String inputWord)
	{
		int occurences = map.get(inputWord) == null ? 0 : map.get(inputWord);

		// TODO: brings almost 1 in codeCheck (is 1.000000000000069 ok?)?
		return (occurences + lambda)/(mapSize + lambda*Output.vocabulary_size); 
	}

	public static double CalcPLidstone(double lambda, Map<String, Integer> trainMap, int occurences)
	{
		long eventsInTraining = DataClass.wordsTotalAmount(trainMap);

		return (occurences + lambda)/(eventsInTraining + lambda*Output.vocabulary_size); 
	}

	public static void modelSanityCheck(double lambda, Map<String, Integer> trainMap)
	{
		long N0 = Output.vocabulary_size - trainMap.keySet().size();
		long trainingSize = DataClass.wordsTotalAmount(trainMap);

		double sum = 0;

		sum += N0 * CalcPLidstone(lambda, trainMap, trainingSize, unseenWord);

		for (String word : trainMap.keySet())
		{
			sum += LidstoneModel.CalcPLidstone(lambda, trainMap, trainingSize, word);
		}

		// Prevent inaccuracies by java double calculations
		// TODO: why 0.000000000000001 doesn't work in lidstone
		double epsilon = 0.000000000000002;
		if (Math.abs(1 - sum) < epsilon)
		{
			Output.writeConsoleWhenTrue("Lidstone is GOOD!");
		}
		else
		{
			Output.writeConsoleWhenTrue("Lidstone is BAD. Value: " + sum);
		}

		//		//This gives a smaller error, but still not 1
		//		BigDecimal sum = new BigDecimal(0);
		//
		//		sum = sum.add(BigDecimal.valueOf(N0 * CalcPLidstone(lambda, trainMap, trainingSize, unseenWord)));
		//
		//		for (String word : trainMap.keySet())
		//		{
		//			sum = sum.add(BigDecimal.valueOf(LidstoneModel.CalcPLidstone(lambda, trainMap, trainingSize, word)));
		//		}
		//
		//		if (sum.equals(1))
		//		{
		//			Output.writeConsoleWhenTrue("Lidstone is GOOD!");
		//		}
		//		else
		//		{
		//			Output.writeConsoleWhenTrue("Lidstone is BAD. Value: " + sum);
		//		}
		//	}
	}
}
