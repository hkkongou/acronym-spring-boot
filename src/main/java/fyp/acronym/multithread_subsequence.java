package fyp.acronym;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

import static fyp.acronym.Acronym.acronym_max_length;
import static fyp.acronym.Acronym.acronym_min_length;


// Java implementation of search and insert operations
// on Trie
public class multithread_subsequence {


    // Alphabet size (# of symbols)
    static final int ALPHABET_SIZE = 26;

    // trie node
    static class TrieNode {
        TrieNode[] children = new TrieNode[ALPHABET_SIZE];

        // isEndOfWord is true if the node represents
        // end of a word
        boolean isEndOfWord;

        TrieNode() {
            isEndOfWord = false;
            for (int i = 0; i < ALPHABET_SIZE; i++)
                children[i] = null;
        }
    }

    ;

    static TrieNode root;

    // If not present, inserts key into trie
    // If the key is prefix of trie node,
    // just marks leaf node
    static void insert(String key) {
        int level;
        int length = key.length();
        int index;

        TrieNode pCrawl = root;

        for (level = 0; level < length; level++) {
            index = key.charAt(level) - 'a';
            if (pCrawl.children[index] == null)
                pCrawl.children[index] = new TrieNode();

            pCrawl = pCrawl.children[index];
        }

        // mark last node as leaf
        pCrawl.isEndOfWord = true;
    }

    // Returns true if key presents in trie, else false
    static boolean search(String key) {
        int level;
        int length = key.length();
        int index;
        TrieNode pCrawl = root;

        for (level = 0; level < length; level++) {
            index = key.charAt(level) - 'a';

            if (pCrawl.children[index] == null)
                return false;

            pCrawl = pCrawl.children[index];
        }

        return (pCrawl.isEndOfWord);
    }

    // Driver
    //public static void main(String args[]) throws IOException {
    public static void construct_trie() throws IOException {
        root = new TrieNode();
        URL url = new URL("https://raw.githubusercontent.com/dwyl/english-words/master/words.txt");
        Scanner sc = new Scanner(url.openStream());
        String temp = "";
        while (sc.hasNext()) {
            temp = sc.next().toLowerCase().replaceAll("[^A-Za-z]", "");
            if (temp.length() > 1) {
                insert(temp);
            }
        }
    }

//        String input = "hierarchicaltaxonomicclassificationforviralmetagenomicdataviadeeplearning"; // Replace this with your input string
//        List<String> subsequences = generateSubsequences(input);
//        System.out.println("Subsequences of " + input + " with length between " + MIN_LENGTH +
//                " and " + MAX_LENGTH + " are: " + subsequences + subsequences.size());
//        for (String each: subsequences) {
//            if(search(each) == true){
//                System.out.println(each);
//            }
//
//        }





    public static List<String> generateSubsequences(String input) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        return forkJoinPool.invoke(new SubsequenceTask(input, 0, input.length()));
    }

    private static class SubsequenceTask extends RecursiveTask<List<String>> {
        private final String input;
        private final int start;
        private final int end;

        public SubsequenceTask(String input, int start, int end) {
            this.input = input;
            this.start = start;
            this.end = end;
        }

        @Override
        protected List<String> compute() {
            List<String> subsequences = new ArrayList<>();
            if (end - start <= 30) {
                for (int mask = 1; mask < (1 << (end - start)); mask++) {
                    StringBuilder combination = new StringBuilder();
                    int length = 0;
                    for (int i = 0; i < end - start; i++) {
                        if ((mask & (1 << i)) != 0) {
                            combination.append(input.charAt(start + i));
                            length++;
                        }
                    }
                    if (length >= acronym_min_length && length <= acronym_max_length) {
                        subsequences.add(combination.toString());
                    }
                }
            } else {
                int mid = (start + end) / 2;
                SubsequenceTask leftTask = new SubsequenceTask(input, start, mid);
                SubsequenceTask rightTask = new SubsequenceTask(input, mid, end);
                leftTask.fork();
                List<String> rightResult = rightTask.compute();
                List<String> leftResult = leftTask.join();
                subsequences.addAll(leftResult);
                subsequences.addAll(rightResult);
            }
            return subsequences;
        }
    }

}


