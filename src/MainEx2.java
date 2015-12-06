import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import InputOutput.DataClass;
import InputOutput.LidstoneModel;
import InputOutput.Output;

public class MainEx2 {

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
					
			outputClass.writeOutput(DataClass.wordsTotalAmount(devData.mapTotalDocsWordCount()));
			Map<String, Integer> trainMap = new TreeMap<String, Integer>();
			Map<String, Integer> validationMap  = new TreeMap<String, Integer>();
			devData.splitXPrecentOfDocsWords(0.9,trainMap,validationMap);
			outputClass.writeOutput(DataClass.wordsTotalAmount(validationMap));
			
			long numberOfEventsInTrainingSet = DataClass.wordsTotalAmount(trainMap);
			outputClass.writeOutput(numberOfEventsInTrainingSet);
			
			//from now - not like ex2_dummpy_ouptut.txt
			outputClass.writeOutput(trainMap.keySet().size()); //TODO: check why dummy says 18976 and we 18977
			
			int inputWordOccurencesOnTraining = trainMap.get(inputWord) == null ? 0 : trainMap.get(inputWord);
			outputClass.writeOutput(inputWordOccurencesOnTraining); //Ido checked and saw their realy is 11 in the training and 3 in the validation

			outputClass.writeOutput((double)inputWordOccurencesOnTraining/numberOfEventsInTrainingSet);
			
			String unseenWord = "unseen-word";
			int unseenWordOccurencesInTraining = trainMap.get(unseenWord) == null ? 0 : trainMap.get(unseenWord);
			outputClass.writeOutput((double)unseenWordOccurencesInTraining/numberOfEventsInTrainingSet);

			double lambda = 0.1;			
			double pLidstoneInputWord = LidstoneModel.CalcPLidstone(lambda, trainMap, inputWord, outputClass.vocabulary_size);
			outputClass.writeOutput(pLidstoneInputWord);
			
			double pLidstoneUnseenWord = LidstoneModel.CalcPLidstone(lambda, trainMap, unseenWord, outputClass.vocabulary_size);
			outputClass.writeOutput(pLidstoneUnseenWord);
			
			double perplexity = calculatePerlexity(0.01, outputClass, validationMap);
			outputClass.writeOutput(perplexity);
			
			perplexity = calculatePerlexity(0.10, outputClass, validationMap);
			outputClass.writeOutput(perplexity);
			
			perplexity = calculatePerlexity(1.00, outputClass, validationMap);
			outputClass.writeOutput(perplexity);
			
			double bestLambda = GetBestLambda(validationMap, outputClass);
			outputClass.writeOutput(bestLambda);
			
//			DataClass testData = new DataClass();
//			testData.readInputFile(test_inputFile);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}

	private static double calculatePerlexity(double lambda, Output outputClass, Map<String, Integer> validationMap) 
	{		
		double sumPWords = 0;
		for (String word : validationMap.keySet())
		{
			double pWord = LidstoneModel.CalcPLidstone(lambda, validationMap, word, outputClass.vocabulary_size);
			sumPWords += Math.log(pWord);
		}
		
		long wordsInTrainingSet = DataClass.wordsTotalAmount(validationMap);
		double perplexity = Math.exp(-1.0/wordsInTrainingSet * sumPWords);
		return perplexity;
	}

	private static double GetBestLambda(Map<String, Integer> validationMap, Output outputClass)
	{
		double bestLambda = 0.0;
		double bestPerplexityValue = calculatePerlexity(0, outputClass, validationMap);
		
		double perplexity;
		
		for (double lambda = 0.01; lambda <= 2; lambda += 0.01)
		{
			perplexity = calculatePerlexity(lambda, outputClass, validationMap);
			
			if (perplexity < bestPerplexityValue)
			{
				bestLambda = lambda;
				bestPerplexityValue = perplexity;
			}
		}
		
		return bestLambda;
	}
}
