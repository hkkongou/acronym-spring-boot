package fyp.acronym;

import java.util.ArrayList;

import static fyp.acronym.Acronym.acronym_max_length;
import static fyp.acronym.Acronym.acronym_min_length;
import static fyp.acronym.multithread_subsequence.root;

// Java implementation of search and insert operations
// on Trie
public class Trie_aho {
    public static ArrayList<String> words = new ArrayList<String>();

    static boolean search(String key) {
        int level;
        int length = key.length();
        int index;
        multithread_subsequence.TrieNode pCrawl = root;
        String sentence = "";
        StringBuilder updateString = new StringBuilder(key);
        for (level = 0; level < length; level++) {
            index = key.charAt(level) - 'a';
            if (pCrawl.children[index] == null) {
            } else {
                pCrawl = pCrawl.children[index];
                sentence = sentence + key.charAt(level);
                updateString.setCharAt(level, Character.toUpperCase(key.charAt(level)));
            }
        }
        //System.out.println(pCrawl.isEndOfWord);
        if (pCrawl.isEndOfWord) {
            if (sentence.length() >= acronym_min_length && sentence.length() <= acronym_max_length) {
                words.add(sentence);
            }
        } else {
            sentence = "";
        }
        return (pCrawl.isEndOfWord);
    }

    static void multisearch(String key) {
        for (int i = 0; i < key.length(); i++) {
            search(key.substring(i, key.length()));
        }
    }
}