package InputOutput;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class DataClass {

	private static boolean writeToConsole = true;

	private static String folderPath = "C:\\devl\\Java\\89919_ex2\\src\\";
	private boolean skipLine = true;
	private List<Set<Topics>> docsTopicList;
	

	private List<Map<String,Integer>> docsList;
	
	public DataClass(){
		this.docsTopicList = new ArrayList<Set<Topics>>();
		this.docsList = new ArrayList<Map<String,Integer>>();
	}

	public void readInputFile(String inputFile) throws IOException{

		writeConsoleWhenTrue(folderPath+inputFile);
		
	    FileReader fileReader = new FileReader(folderPath+inputFile);
	    BufferedReader bufferedReader = new BufferedReader(fileReader);
	    
	    String docTopicLine;
	    
	    while ((docTopicLine = bufferedReader.readLine()) != null) {
	    	
	    	docsTopicList.add(setTopicFromLine(docTopicLine));

	    	skipEmptyLine(bufferedReader);
	    	
	    	String docTextLine = bufferedReader.readLine();
	        docsList.add(mapWordCount(docTextLine));
	        
	    	skipEmptyLine(bufferedReader);

	    }
	    fileReader.close();
	    
//	    writeConsole(docsList);
	}
	
	private void skipEmptyLine(BufferedReader bufferedReader) {
		try {
			if(skipLine)
				bufferedReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private Set<Topics> setTopicFromLine(String docTopicLine) {
		// TODO Change in EX4
		writeConsoleWhenTrue(docTopicLine);
		return null;
		
	}

	private Map<String, Integer> mapWordCount(String inputLine) {
		
		Map<String, Integer> wordsMap = new TreeMap<String, Integer>();
		String[] words = inputLine.split(" ");
		for(String word : words){
			wordsMap.put(word, wordsMap.get(word) == null ? 1 : wordsMap.get(word)+1);
			writeConsoleWhenTrue(word + "-" + wordsMap.get(word));
		}
		return wordsMap;
	}

	public static void writeOutputFile(String outputFile, String outputLine) throws IOException{

	    FileWriter fileWriter = new FileWriter(folderPath+outputFile,true);
	    PrintWriter out1 = new PrintWriter(fileWriter);
	    out1.write(outputLine);
	    
	    writeConsoleWhenTrue(outputLine);
	    
	    out1.flush();
	    out1.close();
	    fileWriter.close();
	}
	
	public static void writeConsoleWhenTrue(Object Line){
	
		if(writeToConsole){
			System.out.println(Line);
		}
	}
	
	public List<Set<Topics>> getDocsTopicList() {
		return docsTopicList;
	}

	public List<Map<String, Integer>> getDocsList() {
		return docsList;
	}

	public void setDocsList(List<Map<String, Integer>> docsList) {
		this.docsList = docsList;
	}

	public void setDocsTopicList(List<Set<Topics>> docsTopicList) {
		this.docsTopicList = docsTopicList;
	}
}
