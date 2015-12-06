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
		outputClass.writeOutput(outputClass.vocabulary_size);
		outputClass.writeOutput(1.0/outputClass.vocabulary_size);
		
	    
	    try {
	    	DataClass devData = new DataClass();
			devData.readInputFile(devl_inputFile);
					
			outputClass.writeOutput(devData.getTotalWordsInDocs());
			Map<String, Integer> trainMap = new TreeMap<String, Integer>();
			Map<String, Integer> validationMap  = new TreeMap<String, Integer>();
			devData.splitXPrecentOfDocsWords(0.9,trainMap,validationMap);
			outputClass.writeOutput(DataClass.wordsTotalAmount(validationMap));
			
			long numberOfEventsInTrainingSet = DataClass.wordsTotalAmount(trainMap);
			outputClass.writeOutput(numberOfEventsInTrainingSet);
			
			outputClass.writeOutput(trainMap.keySet().size()); 
			
			int inputWordOccurencesOnTraining = trainMap.get(inputWord) == null ? 0 : trainMap.get(inputWord);
			outputClass.writeOutput(inputWordOccurencesOnTraining);

			outputClass.writeOutput((double)inputWordOccurencesOnTraining/numberOfEventsInTrainingSet);
			
			
			int unseenWordOccurencesInTraining = trainMap.get(unseenWord) == null ? 0 : trainMap.get(unseenWord);
			outputClass.writeOutput((double)unseenWordOccurencesInTraining/numberOfEventsInTrainingSet);

			double lambda = 0.1;			
			double pLidstoneInputWord = LidstoneModel.CalcPLidstone(lambda, trainMap, inputWord);
			outputClass.writeOutput(pLidstoneInputWord);
			
			double pLidstoneUnseenWord = LidstoneModel.CalcPLidstone(lambda, trainMap, unseenWord);
			outputClass.writeOutput(pLidstoneUnseenWord);
			
			double perplexity = calculatePerplexity(0.01, validationMap);
			outputClass.writeOutput(perplexity);
			
			perplexity = calculatePerplexity(0.10, validationMap);
			outputClass.writeOutput(perplexity);
			
			perplexity = calculatePerplexity(1.00, validationMap);
			outputClass.writeOutput(perplexity);
			
			//Long func - after fixing it, use the mock while testing
			double bestLambda = GetBestLambda(validationMap);
			outputClass.writeOutput(bestLambda);
			outputClass.writeOutput(calculatePerplexity(bestLambda, validationMap));
//			outputClass.writeOutput("Mock"); //TODO: delete
//			outputClass.writeOutput("Mock"); //TODO: delete


			
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

	private static double calculatePerplexity(double lambda, Map<String, Integer> validationMap) 
	{		
		double sumPWords = 0;
		for (String word : validationMap.keySet())
		{
			double pWord = LidstoneModel.CalcPLidstone(lambda, validationMap, word);
			sumPWords += Math.log(pWord);
		}
		
		long wordsInTrainingSet = DataClass.wordsTotalAmount(validationMap);
		double perplexity = Math.exp(-1.0/wordsInTrainingSet * sumPWords); //Ido: shoud be pow(2,..) and not exp it think. and actually i think in my notes its a different formula - look at wiki also
		return perplexity;
	}

	private static double GetBestLambda(Map<String, Integer> validationMap)
	{
		double bestLambda = 0.0;
		double bestPerplexityValue = calculatePerplexity(0, validationMap);
		
		double perplexity;
		
		for (double lambda = 0.01; lambda <= 2; lambda += 0.01)
		{
			perplexity = calculatePerplexity(lambda, validationMap);
			
			if (perplexity < bestPerplexityValue)
			{
				bestLambda = lambda;
				bestPerplexityValue = perplexity;
			}
		}
		
		return bestLambda;
	}
}
