package InputOutput;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class DataClass {


	private boolean skipLine = true;
	private List<Set<Topics>> docsTopicList;


	private List<Map<String,Integer>> docsMapList;
	private List<String> docsStringList;

	public DataClass(){
		this.docsTopicList = new ArrayList<Set<Topics>>();
		this.docsMapList = new ArrayList<Map<String,Integer>>();
		this.docsStringList = new ArrayList<String>();
	}

	public void readInputFile(String inputFile) throws IOException{

		Output.writeConsoleWhenTrue(Output.folderPath+inputFile);

		FileReader fileReader = new FileReader(Output.folderPath+inputFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		String docTopicLine;

		while ((docTopicLine = bufferedReader.readLine()) != null) {

			docsTopicList.add(setTopicFromLine(docTopicLine));

			skipEmptyLine(bufferedReader);

			String docTextLine = bufferedReader.readLine();
			docsMapList.add(mapWordCount(docTextLine));
			docsStringList.add(docTextLine);

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
			e.printStackTrace();
		}		
	}

	private Set<Topics> setTopicFromLine(String docTopicLine) {
		// TODO Change in EX4
		Output.writeConsoleWhenTrue(docTopicLine);
		return null;

	}

	private Map<String, Integer> mapWordCount(String inputLine) {

		Map<String, Integer> wordsMap = new TreeMap<String, Integer>();
		String[] words = inputLine.split(" ");
		for(String word : words){
			word = word.toLowerCase();
			wordsMap.put(word, wordsMap.get(word) == null ? 1 : wordsMap.get(word)+1);
			Output.writeConsoleWhenTrue(word + "-" + wordsMap.get(word));
		}
		return wordsMap;
	}



	private Map<String, Integer> listMapToMapTotalWordCount(List<Map<String,Integer>> docsList) {
		Map<String, Integer> wordsCountMap = new TreeMap<String, Integer>();
		for(Map<String, Integer> docMap : docsList){
			joinMaps(wordsCountMap,docMap);		
		}
		return wordsCountMap;
	}

	private void joinMaps(Map<String, Integer> srcMap, Map<String, Integer> otherMap){
		for (String word : otherMap.keySet()){
			joinMapValues(srcMap,otherMap,word);
		}
	}

	private void joinMapValues(Map<String, Integer> srcMap, Map<String, Integer> otherMap, String key){
		srcMap.put(key, srcMap.get(key) == null ? otherMap.get(key) : srcMap.get(key)+otherMap.get(key));
		Output.writeConsoleWhenTrue(key + "-" + srcMap.get(key));
		if(key.equals("houston")){
		}
	}

	public void splitXPrecentOfDocsWords(double d, Map<String, Integer> firstXPrecentWordsMap, Map<String, Integer> lastXPrecentWordsMap) {

		if(d<0||d>1){
			System.out.println("Precent should be between 0 to 1");
			return;
		}

		long numberOfWordsInDocs = wordsTotalAmount(mapTotalDocsWordCount());	
		long numFirstXPrecent = Math.round(d*numberOfWordsInDocs); //TODO: check if to use round or floor
		Output.writeConsoleWhenTrue("Precent of the words is "+numFirstXPrecent);

		long count=0;
		int index=0;
		boolean xPrecentPasted = false;
		while(count<numFirstXPrecent && !xPrecentPasted){
			//			if(index>=1839){
			//				System.out.println("DEBUG");
			//			}
			Map<String, Integer> currentDocMap = this.docsMapList.get(index);
			long numWordsInDoc = wordsTotalAmount(currentDocMap);
			if(count+numWordsInDoc<=numFirstXPrecent){
				joinMaps(firstXPrecentWordsMap,currentDocMap);	
				count+=numWordsInDoc;
			}
			else{
				// If splitting the doc is needed, run over the doc in which we pass the X percent by the order of the words in the doc
				// and not by their ABC order (currentDocMap.keySet is ordered alphabetically)
				String currentDocString = this.docsStringList.get(index);
				String[] words = currentDocString.split(" ");
				for(String word : words){
					word = word.toLowerCase();
					if(!xPrecentPasted && count<numFirstXPrecent){
						firstXPrecentWordsMap.put(word, firstXPrecentWordsMap.get(word) == null ? 1 : firstXPrecentWordsMap.get(word)+1);
						Output.writeConsoleWhenTrue(word + "-" + firstXPrecentWordsMap.get(word));
						count++;
					}
					else{
						xPrecentPasted = true;
						lastXPrecentWordsMap.put(word, lastXPrecentWordsMap.get(word) == null ? 1 : lastXPrecentWordsMap.get(word)+1);
						Output.writeConsoleWhenTrue("AfterXPrecent: " + word + "-" + lastXPrecentWordsMap.get(word));
					}
				}
			}

			index++;
		}
		
		//After X precent
		while(index<this.docsMapList.size()){
			Map<String, Integer> currentDocMap = this.docsMapList.get(index++);
			joinMaps(lastXPrecentWordsMap,currentDocMap);	
		}
	}

	public Map<String, Integer> mapTotalDocsWordCount() {

		return listMapToMapTotalWordCount(this.docsMapList);
	}


	public static long wordsTotalAmount(Map<String, Integer> wordsCountMap)
	{
		int count=0;

		for(int value :  wordsCountMap.values())
		{
			count +=value;
		}

		return count;
	}


	public List<Set<Topics>> getDocsTopicList() {
		return docsTopicList;
	}

	public List<Map<String, Integer>> getDocsList() {
		return docsMapList;
	}

	public void setDocsList(List<Map<String, Integer>> docsList) {
		this.docsMapList = docsList;
	}

	public void setDocsTopicList(List<Set<Topics>> docsTopicList) {
		this.docsTopicList = docsTopicList;
	}
}
