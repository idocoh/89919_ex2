package InputOutput;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class Output {
	
	public static boolean writeToConsole = true;

	//TODO: change folder path!!!
	public static String folderPath = "C:\\devl\\Java\\89919_ex2\\src\\";

	public int countOutput = 1;
	public String outputFile;
	public int vocabulary_size = 300000; //TODO: check if this is what they ment
	
	public Output(String outputFile){
		this.outputFile = folderPath + outputFile;
	}
	
	public void writeNames(){
		//TODO: change guys id!!!
		writeOutputFile("#Students\tIdo Cohen\tGuy Cohen\t203516992\t123456789",false);
	}
	
	public void writeOutput(String output){
		writeOutputFile("\n#Output" + countOutput++ + "\t" + output);
	}
	
	public void writeOutput(Number output){
		//TODO: check Number doesn't round the value
		writeOutput(output.toString());
	}
	
	public void writeOutputFile(String outputLine) {
		writeOutputFile(outputLine,true);
	}
	
	public void writeOutputFile(String outputLine, Boolean toAppend) {

		try{
			FileWriter fileWriter = new FileWriter(outputFile,toAppend);

			PrintWriter out1 = new PrintWriter(fileWriter);
			out1.write(outputLine);

			writeConsoleWhenTrue(outputLine);

			out1.flush();
			out1.close();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeConsoleWhenTrue(Object Line){

		if(writeToConsole){
			System.out.println(Line);
		}
	}
}
