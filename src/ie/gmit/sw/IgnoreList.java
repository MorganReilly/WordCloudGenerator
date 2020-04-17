package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IgnoreList {

	private String ignoreListFile = "./res/ignorewords.txt";
	
	public List<String> getConcurrentIgnoreList() {
		return concurrentIgnoreList;
	}

	public void setConcurrentIgnoreList(List<String> concurrentIgnoreList) {
		this.concurrentIgnoreList = concurrentIgnoreList;
	}

	private List<String> ignoreList = new ArrayList<String>();
	private List<String> concurrentIgnoreList = Collections.synchronizedList(ignoreList);

	public List<String> getIgnoreList() {
		return ignoreList;
	}

	public void setIgnoreList(List<String> ignoreList) {
		this.ignoreList = ignoreList;
	}
	
	public IgnoreList() throws IOException {
		setConcurrentIgnoreList((generateIgnoreList(this.ignoreListFile))); 
	}

	/* Adapted from: https://stackoverflow.com/a/5278070/8883485 */
	public List<String> generateIgnoreList(String file) throws IOException {
		List<String> list = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				list.add(line);

			}
		} finally {
			reader.close();
		}

//		 Debugging
//	    for (String i : list) {
//	    	System.out.println(i);
//	    }
		return list;
	}

}
