import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import InputOutput.DataClass;
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
					
			outputClass.writeOutput(devData.wordsTotalAmount(devData.mapTotalDocsWordCount()));
			Map<String, Integer> trainMap = new TreeMap<String, Integer>();
			Map<String, Integer> validationMap  = new TreeMap<String, Integer>();
			devData.splitXPrecentOfDocsWords(0.9,trainMap,validationMap);
			outputClass.writeOutput(devData.wordsTotalAmount(validationMap));
			outputClass.writeOutput(devData.wordsTotalAmount(trainMap));
			
			//from now - not like ex2_dummpy_ouptut.txt
			outputClass.writeOutput(trainMap.keySet().size()); //TODO: check why dummy says 18976 and we 18977
			outputClass.writeOutput(trainMap.get(inputWord) == null ? 0 : trainMap.get(inputWord)); //Ido checked and saw their realy is 11 in the training and 3 in the validation

			
			
			
//			DataClass testData = new DataClass();
//			testData.readInputFile(test_inputFile);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}

}
