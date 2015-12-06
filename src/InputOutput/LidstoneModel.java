package InputOutput;

import java.util.*;

public class LidstoneModel 
{
	public static double CalcPLidstone(double lambda, Map<String, Integer> trainMap, String inputWord ,long vocabularySize)
	{
		long occurences = trainMap.get(inputWord) == null ? 0 : trainMap.get(inputWord);
		long eventsInTraining = DataClass.wordsTotalAmount(trainMap);
		return (occurences + lambda)/(eventsInTraining + vocabularySize);
	}

}
