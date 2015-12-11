import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import InputOutput.DataClass;
import InputOutput.HeldOutModel;
import InputOutput.LidstoneModel;
import InputOutput.Output;

public class MainEx2 {
	
	static String unseenWord = "unseen-word";

	public static void main(String[] args) {
		
		String devl_inputFile = args[0];
	    String test_inputFile = args[1];
	    String inputWord = args[2];
	    String outputFile = args[3];
	    
	    Output outputClass = new Output(outputFile);
		
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
			
			Map<String, Integer> trainMap = new TreeMap<String, Integer>();
			Map<String, Integer> validationMap  = new TreeMap<String, Integer>();
			devData.splitXPrecentOfDocsWords(0.9, trainMap, validationMap);
			
			// Output 8
			outputClass.writeOutput(DataClass.wordsTotalAmount(validationMap));
			
			// Output 9
			outputClass.writeOutput(DataClass.wordsTotalAmount(trainMap));
			
			// Output 10
			outputClass.writeOutput(trainMap.keySet().size()); 
			
			// Output 11
			outputClass.writeOutput(getNumberOfOccurences(trainMap, inputWord));

			// Output 12
			outputClass.writeOutput(calcPMle(trainMap, inputWord));
			
			// Output 13
			outputClass.writeOutput(calcPMle(trainMap, unseenWord));

			double lambda = 0.1;			

			// Output 14
			outputClass.writeOutput(LidstoneModel.CalcPLidstone(lambda, trainMap, inputWord));
			
			// Output 15
			outputClass.writeOutput(LidstoneModel.CalcPLidstone(lambda, trainMap, unseenWord));
			
			// Output 16
			outputClass.writeOutput(calculatePerplexity(0.01, trainMap, validationMap));
			
			// Output 17
			outputClass.writeOutput(calculatePerplexity(0.10, trainMap, validationMap));
			
			// Output 18
			outputClass.writeOutput(calculatePerplexity(1.00, trainMap, validationMap));
			
			double bestLambda = getBestLambda(trainMap, validationMap);
			
			// Output 19
			outputClass.writeOutput(bestLambda);
			
			// Output 20
			outputClass.writeOutput(calculatePerplexity(bestLambda, trainMap, validationMap));

			Map<String, Integer> trainHalfMap = new TreeMap<String, Integer>();
			Map<String, Integer> heldOutMap  = new TreeMap<String, Integer>();
			devData.splitXPrecentOfDocsWords(0.5,trainHalfMap,heldOutMap);
			outputClass.writeOutput(DataClass.wordsTotalAmount(trainHalfMap));
			outputClass.writeOutput(DataClass.wordsTotalAmount(heldOutMap));
			
			double pHeldOutInputWord = HeldOutModel.CalcPHeldOut(trainHalfMap,heldOutMap,inputWord,devData.getMapTotalDocsWords());
			outputClass.writeOutput(pHeldOutInputWord);
			double pHeldOutUnseenWord = HeldOutModel.CalcPHeldOut(trainHalfMap,heldOutMap,unseenWord,devData.getMapTotalDocsWords());
			outputClass.writeOutput(pHeldOutUnseenWord);
			
			//Long func - after fixing it, ignore it
			lambda = 0.1;
			checkCode(devData.getMapTotalDocsWords(),lambda,trainMap,inputWord,trainHalfMap,heldOutMap);
			
//			DataClass testData = new DataClass();
//			testData.readInputFile(test_inputFile);
			
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}

	private static void checkCode(Map<String, Integer> mapTotalDocsWordCount, double lambda, Map<String, Integer> trainMap, String inputWord, Map<String, Integer> trainHeldOutMap, Map<String, Integer> heldOutMap) {
		long N0 = Output.vocabulary_size - mapTotalDocsWordCount.keySet().size();
		
		double addP = 0;
		for( String word : mapTotalDocsWordCount.keySet()){
			addP += LidstoneModel.CalcPLidstone(lambda, trainMap, word);
		}
		
		double sumToOne = N0*LidstoneModel.CalcPLidstone(lambda, trainMap, unseenWord) + addP;
		if( sumToOne == 1){
			Output.writeConsoleWhenTrue("Lidstone is GOOD!");
		}
		else{
			Output.writeConsoleWhenTrue("Lidstone is BAD. Value-" + sumToOne);
		}
		
		addP = 0;
		for( String word : mapTotalDocsWordCount.keySet()){
			addP += HeldOutModel.CalcPHeldOut(trainHeldOutMap, heldOutMap, word,mapTotalDocsWordCount);
		}
		
		sumToOne = N0*HeldOutModel.CalcPHeldOut(trainHeldOutMap, heldOutMap, unseenWord,mapTotalDocsWordCount) + addP;
		if(sumToOne ==1){
			Output.writeConsoleWhenTrue("HeldOut is GOOD!");
		}
		else{
			Output.writeConsoleWhenTrue("HeldOut is BAD. Value-" + sumToOne);
		}
		
	}

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
	
	private static double calculatePerplexity(double lambda, Map<String, Integer> trainingMap, Map<String, Integer> validationMap) 
	{		
		double sumPWords = 0;
		
		long trainingSize = DataClass.wordsTotalAmount(trainingMap);
		
		for (String word : validationMap.keySet())
		{
			double pWord = LidstoneModel.CalcPLidstone(lambda, trainingMap, trainingSize, word);
			sumPWords += Math.log(pWord);
		}
		
		long validationWordsSize = validationMap.keySet().size();
		
		// TODO: DoubleCheck this, look at wiki
		double perplexity = Math.exp(-1.0/validationWordsSize * sumPWords); 
		return perplexity;
	}

	private static double getBestLambda(Map<String, Integer> trainingMap, Map<String, Integer> validationMap)
	{
		double bestLambda = 0.0;
		double bestPerplexityValue = calculatePerplexity(0, trainingMap, validationMap);
		
		double perplexity;
		
		for (double lambda = 0.01; lambda <= 2; lambda += 0.01)
		{
			perplexity = calculatePerplexity(lambda, trainingMap, validationMap);
			
			if (perplexity < bestPerplexityValue)
			{
				bestLambda = lambda;
				bestPerplexityValue = perplexity;
			}
		}
		
		return bestLambda;
	}
}
