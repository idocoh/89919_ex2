package InputOutput;

import java.util.*;

public class LidstoneModel 
{
	public static double CalcPLidstone(double lambda, Map<String, Integer> trainMap, String inputWord)
	{
		long occurences = trainMap.get(inputWord) == null ? 0 : trainMap.get(inputWord);
		long eventsInTraining = DataClass.wordsTotalAmount(trainMap);

		// TODO: brings almost 1 in codeCheck (is 1.000000000000069 ok?)?
		return (occurences + lambda)/(eventsInTraining + lambda*Output.vocabulary_size); 
	}

}
