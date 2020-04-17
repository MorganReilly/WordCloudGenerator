package ie.gmit.sw;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapController {

	/*
	 * Count Word Frequency
	 * 
	 * Adapted From:
	 * https://www.geeksforgeeks.org/count-occurrences-elements-list-java/
	 */
	public Map<String, Integer> countFrequencies(List<String> list) {
		Map<String, Integer> hm = new ConcurrentHashMap<>();
		for (String i : list) {
			Integer j = hm.get(i);
			hm.put(i, (j == null) ? 1 : j + 1);
		}
		return hm;
	}

	/*
	 * Sort Frequency Map
	 */
	public Map<String, Integer> sortFrequencyMap(Map<String, Integer> frequencyMap) {
		Map<String, Integer> hm1 = sortByValue(frequencyMap);
		return hm1;
	}

	/*
	 * Sort HashMap by Value
	 * 
	 * Adapted From:
	 * https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values/
	 */
	public static HashMap<String, Integer> sortByValue(Map<String, Integer> inputMap) {
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(inputMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

}
