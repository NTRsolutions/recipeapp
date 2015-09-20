package com.mrp.datamining;

import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.lang3.StringUtils;

public class FuzzyWordMatcher {
	public static float getProbabilityByAlgo1(String word1,String word2) {
		int distance = StringUtils.getLevenshteinDistance(word1, word2);
		float average_lenght = ((word1.length()+word2.length())/2);
		if(distance>average_lenght)
			return 0;
		else
			return (1-distance/average_lenght);
	}
	
	public static float getProbabilityByAlgo2(String word1,String word2) {
		float probability = 0;
		try {
			//returns between 0 to 4
			int similarityIndex = Soundex.US_ENGLISH.difference(word1, word2);
			probability = (float)similarityIndex/4f;
		} catch (Exception e) {
			probability = 0;
		}
		
		return probability;
	}
	
	public static float getProbabilityByAlgo3(String word1,String word2) {
		Metaphone m = new Metaphone();
		return m.isMetaphoneEqual(word1,word2) ? 1 : 0;
		
	}
	
	public static String[] getTestWords() {
		String[] wordList = new String[]{
		"piza","pizzaa",
		"pizaa","pizza",
		"faluda","fluda",
		"chapati","chapathi",
		"roll","role",
		"roll","rol",
		"kadai","kadhai",
		"paneer","panir",
		"paneer","pnir",
		"paneer","pnir",
		"paneer","roti",
		"paneer","ghobhi",
		"pizza","ghobhi",
		"pizza","french"
		};
		return wordList;
	}
	
	
	public static void test() {
		String[] testWords = getTestWords();
		
		for(int i=0;i<testWords.length;i+=2) {
			String word1 = testWords[i];
			String word2 = testWords[i+1];
			float p1 = getProbabilityByAlgo1(word1,word2);
			float p2 = getProbabilityByAlgo2(word1,word2);
			float p3 = getProbabilityByAlgo3(word1,word2);
			getProbabilityByAlgo2(word1,word2);
			System.out.println("probability of words("+word1+","+word2+")"+"being same with algo1="+p1);
			System.out.println("probability of words("+word1+","+word2+")"+"being same with algo2="+p2);
			System.out.println("probability of words("+word1+","+word2+")"+"being same with algo3="+p3);
			System.out.println("\n\n");
			
		}
		
	}
	
	public static float matchFoundInSentence(String inputSentence, String pattern) {
		if(inputSentence == null || inputSentence.equalsIgnoreCase(""))
			return -1;
		
		inputSentence = inputSentence.replace("  ", " "); // double space to single.
		int patternWordCount = pattern.split(" ").length;
		float maxPossibleMatch = -1;
		String[] words = inputSentence.split(" ");
		for(int i=0 ; i<words.length; i++){
			String word = "";
			if(i+patternWordCount-1<words.length){
				for(int j=0; j<patternWordCount; j++){
					word = word +" "+ words[i+j];
				}
			}
			float match = getProbabilityByAlgo2(word, pattern);
			if(match>maxPossibleMatch){maxPossibleMatch = match;}
		}
		return maxPossibleMatch;
		
	}
}
