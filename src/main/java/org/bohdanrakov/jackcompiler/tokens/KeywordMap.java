package org.bohdanrakov.jackcompiler.tokens;

import java.util.HashMap;
import java.util.Map;

public class KeywordMap {

    private static Map<String, Keyword> keywordMap = new HashMap<>();

    static {
        for (Keyword keyword: Keyword.values()) {
            keywordMap.put(keyword.getStringValue(), keyword);
        }
    }

    public static boolean isPresent(String keyword) {
        return keywordMap.containsKey(keyword);
    }
}
