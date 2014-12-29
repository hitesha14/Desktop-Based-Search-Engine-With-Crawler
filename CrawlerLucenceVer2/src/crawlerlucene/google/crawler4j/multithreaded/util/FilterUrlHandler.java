/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlerlucene.google.crawler4j.multithreaded.util;

import java.util.regex.Pattern;

/**
 *
 * @author sumit
 */
public class FilterUrlHandler {

    private Pattern patternFilteredCriteria;

    public FilterUrlHandler(String pattern) {
        patternFilteredCriteria = Pattern.
                compile(pattern);

    }

    public boolean verifyFilter(String textToVerify) {
        return patternFilteredCriteria.matcher(textToVerify).matches();
    }

    public static boolean verifyFilterForPattern(String pattern,
            String textToVerify) {
        return Pattern.compile(pattern).matcher(textToVerify).matches();
    }

    public void updatePattern(String newPattern) {
        patternFilteredCriteria = Pattern.
                compile(newPattern);
    }
}
