package utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {

	public static LinkedHashMap<String, ?> sortMap(Map<String, ?> pMap) {
		return pMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,
				Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

}
