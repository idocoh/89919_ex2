import java.io.IOException;

import InputOutput.DataClass;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String devl_inputFile = args[0];
	    String test_inputFile = args[1];
	    String inputWord = args[2];
	    String outputFile = args[3];
	    
	    try {
	    	DataClass devData = new DataClass();
			devData.readInputFile(devl_inputFile);
			
			DataClass testData = new DataClass();
			testData.readInputFile(test_inputFile);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}

}
