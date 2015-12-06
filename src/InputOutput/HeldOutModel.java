package InputOutput;

import java.io.Console;
import java.util.*;

public class HeldOutModel 
{
	//TODO: check if right

	public static double CalcPHeldOut(Map<String, Integer> trainMap, Map<String, Integer> heldOutMap, String inputWord, Map<String, Integer> mapTotalDocsWordCount)
	{
		int occurences = trainMap.get(inputWord) == null ? 0 : trainMap.get(inputWord);

		if(occurences !=0 ){
//			long Tr = CalcTrInHeldOut(trainMap,heldOutMap,occurences);
//			long Nr = CalcNrInTrain(trainMap,occurences);
			List<Long> pairTrNr = calc_TrInHeldOut_NrInTrain(trainMap,heldOutMap,occurences);
			long eventsInHeldOut = DataClass.wordsTotalAmount(heldOutMap);

			return (double)(pairTrNr.get(0))/(pairTrNr.get(1)*eventsInHeldOut); 
		}
		else{
			//TODO: Find what to do here, i couldn't find it in my notes from class. its something like this, but need to choose big R some how...?
			long N0 = Output.vocabulary_size - mapTotalDocsWordCount.keySet().size();
			double addP = 0;
			for( String word : trainMap.keySet()){
				addP += mapTotalDocsWordCount.get(word)*HeldOutModel.CalcPHeldOut(trainMap, heldOutMap, word,null);
			}
			
			return (double)(1-addP)/N0;
		}
	}

	private static List<Long> calc_TrInHeldOut_NrInTrain(Map<String, Integer> trainMap, Map<String, Integer> heldOutMap, Integer r){

		long Tr = 0;
		long Nr = 0;
		for (String word : trainMap.keySet()){
			if(trainMap.get(word).equals(r)){
				Tr += (heldOutMap.get(word) == null? 0 : heldOutMap.get(word)); 
				Nr += r; 
			}
		}
		
		List<Long> pair = new ArrayList<Long>();
		pair.add(0, Tr);
		pair.add(1, Nr);
		return pair;
	}
	
//	private static long CalcTrInHeldOut(Map<String, Integer> trainMap, Map<String, Integer> heldOutMap, Integer r){
//
//		long count = 0;
//		for (String word : trainMap.keySet()){
//			if(trainMap.get(word) == r){
//				count += (heldOutMap.get(word) == null? 0 : heldOutMap.get(word)); 
//			}
//		}
//
//		return count;
//	}
//
//	private static long CalcNrInTrain(Map<String, Integer> trainMap, Integer r){
//
//		long count = 0;
//		for (String word : trainMap.keySet()){
//			if(trainMap.get(word) == r){
//				count += r; 
//			}
//		}
//
//		return count;
//	}

}
