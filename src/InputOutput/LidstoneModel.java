package InputOutput;

import java.util.*;

public class LidstoneModel 
{
	public static double CalcPLidstone(double lambda, Map<String, Integer> trainMap, String inputWord)
	{
		long eventsInTraining = DataClass.wordsTotalAmount(trainMap);

		return CalcPLidstone(lambda, trainMap, eventsInTraining, inputWord);
	}

	public static double CalcPLidstone(double lambda, Map<String, Integer> map, long mapSize, String inputWord)
	{
		long occurences = map.get(inputWord) == null ? 0 : map.get(inputWord);

		// TODO: brings almost 1 in codeCheck (is 1.000000000000069 ok?)?
		return (occurences + lambda)/(mapSize + lambda*Output.vocabulary_size); 
	}
}
