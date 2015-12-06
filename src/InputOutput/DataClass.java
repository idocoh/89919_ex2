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


	private List<Map<String,Integer>> docsList;

	public DataClass(){
		this.docsTopicList = new ArrayList<Set<Topics>>();
		this.docsList = new ArrayList<Map<String,Integer>>();
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
			Map<String, Integer> currentDocMap = this.docsList.get(index++);
			long numWordsInDoc = wordsTotalAmount(currentDocMap);
			if(count+numWordsInDoc<=numFirstXPrecent){
				joinMaps(firstXPrecentWordsMap,currentDocMap);	
				count+=numWordsInDoc;
			}
			else{
				// TODO: Fix this case
				// Run over the doc in which we pass the X percent by the order of the words in the doc
				// not by their ABC order (currentDocMap.keySet is ordered alphabetically)
				for(String word : currentDocMap.keySet()){
					if(xPrecentPasted || count==numFirstXPrecent){
						joinMapValues(lastXPrecentWordsMap,currentDocMap,word);
						xPrecentPasted = true;
					}
					else{
						if(count+currentDocMap.get(word)<=numFirstXPrecent){
							count+=currentDocMap.get(word);
							joinMapValues(firstXPrecentWordsMap,currentDocMap,word);
							
						}
						else{
							//TODO: check no word appears more than Integer.MAX_VALUE number of times in all docs together
							int numWordsFirstXPrecent = (int) (numFirstXPrecent-count);
							int numWordsLastXPrecent = currentDocMap.get(word) - numWordsFirstXPrecent;

							firstXPrecentWordsMap.put(word, firstXPrecentWordsMap.get(word) == null ? numWordsFirstXPrecent : firstXPrecentWordsMap.get(word)+numWordsFirstXPrecent);
							Output.writeConsoleWhenTrue(word + "-" + firstXPrecentWordsMap.get(word));
							count += numWordsFirstXPrecent;

							xPrecentPasted = true;
							lastXPrecentWordsMap.put(word, lastXPrecentWordsMap.get(word) == null ? numWordsLastXPrecent : lastXPrecentWordsMap.get(word) + numWordsLastXPrecent);
							Output.writeConsoleWhenTrue(word + "-" + lastXPrecentWordsMap.get(word));

						}
					}
				}
			}
		}
		while(index<this.docsList.size()){
			Map<String, Integer> currentDocMap = this.docsList.get(index++);
			joinMaps(lastXPrecentWordsMap,currentDocMap);	
		}
	}

	public Map<String, Integer> mapTotalDocsWordCount() {

		return listMapToMapTotalWordCount(this.docsList);
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
		return docsList;
	}

	public void setDocsList(List<Map<String, Integer>> docsList) {
		this.docsList = docsList;
	}

	public void setDocsTopicList(List<Set<Topics>> docsTopicList) {
		this.docsTopicList = docsTopicList;
	}
}
