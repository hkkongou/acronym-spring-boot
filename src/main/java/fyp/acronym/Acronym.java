package fyp.acronym;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class Acronym extends JFrame {
    //dictionary word list
    static ArrayList<String> dictionaryWords = new ArrayList<>();
    //common word list, words that are used in normal daily life, easier to memories
    static ArrayList<String> CommonWords = new ArrayList<>();
    //result, found acronym
    static List<Record> records = new ArrayList<>();

    static Integer acronym_min_length = 3;
    static Integer acronym_max_length = 6;

    //default input when empty
    static String input = "hierarCHical taxonomic classification for viral mEtagEnomic data via deep leaRning";


    public static String TempA = "";
    public static String TempB = "";
    public static JPanel all = new JPanel(new BorderLayout());
    public static JPanel top = new JPanel(new BorderLayout());
    //public static JPanel right = new JPanel(new BorderLayout());
    public static JTextArea TAoutput1 = new JTextArea();
    public static JTextArea TAoutput2 = new JTextArea();
    public static JTextField TA = new JTextField();

    public static void gui_init() {

        Acronym frame = new Acronym();
        frame.setTitle("Acronym!");
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);

        TAoutput1.setEditable(false);
        TAoutput1.setBorder(null);
        TAoutput1.setPreferredSize(new Dimension(300, 400));
        TAoutput1.setOpaque(false);

        TAoutput2.setEditable(false);
        TAoutput2.setBorder(null);
        TAoutput2.setPreferredSize(new Dimension(500, 400));
        TAoutput2.setOpaque(false);

        TA.setPreferredSize(new Dimension(100, 24));
        TA.setText("National Aeronautics and Space Administration");

        JLabel Outputlabel = new JLabel();
        Outputlabel.setFont(new Font("Calibri", Font.PLAIN, 30));
        Outputlabel.setText("Type sentence and press enter for Acyonym!");

        JLabel Consolelable = new JLabel();
        Consolelable.setFont(new Font("Calibri", Font.PLAIN, 20));
        Consolelable.setText("Console: waiting for input");

        top.add(Outputlabel, BorderLayout.NORTH);
        top.add(TA, BorderLayout.SOUTH);

        all.add(top, BorderLayout.NORTH);
        all.add(TAoutput1, BorderLayout.WEST);
        all.add(TAoutput2, BorderLayout.CENTER);
        all.add(Consolelable, BorderLayout.SOUTH);
        frame.add(all);

        frame.revalidate();
        frame.repaint();
        Action enterkey = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Consolelable.setText("Console: loading!");
                frame.revalidate();
                frame.repaint();

                input = TA.getText();
                try {
                    fetchData();
                    findAcronyms();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                // Step 6: Print all record
                for (Record record : records) {
                    System.out.println(record);
                }
                TempA = "";
                TempB = "";
                for (Record record : records) {
                    TempA = TempA + "\n" + record.getField1();
                    TempB = TempB + "\n" + record.getField2();
                }
                TAoutput1.setText(TempA);
                TAoutput2.setText(TempB);


                Consolelable.setText("Console: done!");
                frame.revalidate();
                frame.repaint();

            }
        };
        TA.addActionListener(enterkey);
    }

    //        public static void main(String[] args) throws IOException {
//
//        // Step 1: Read in sentence
//        System.out.println("write some text");
//        //Scanner scanner1 = new Scanner(System.in);
//        //String input = scanner1.nextLine();  // Read user input
//
//        // Step 1.5: Read in length
//        System.out.println("min acronym length? ");
//        //Integer acronym_min_length = Integer.parseInt(scanner1.nextLine());  // Read user input
//
//        System.out.println("max acronym length? ");
//        //Integer acronym_max_length = Integer.parseInt(scanner1.nextLine());  // Read user input
//
//        fetchData();
//        findAcronyms();
//
//        // Step 6: Print all record
//        for (Record record : records) {
//            System.out.println(record);
//        }
//    }
//    public static void main(String args[]) {
//        gui_init();
//
//    }

    public static void findAcronyms() {
        String longText = input.toLowerCase().replace(" ", "").replaceAll("[^A-Za-z]", "");
        //Iterate through the ArrayList and check containment
        if (longText.length()==0)
            return;
        for (String word : dictionaryWords) {
            if (word.length() >= acronym_min_length && word.length() <= acronym_max_length) {
                boolean containsAllCharacters = true;
                for (char c : word.toCharArray()) {
                    if (longText.indexOf(c) == -1) {
                        containsAllCharacters = false;
                        break; // No need to continue checking if one character is not found
                    }
                }
                if (containsAllCharacters && containsCharactersInOrder(longText, word)) {
                    String ProcessedSentence = capitalizeSubstring(input.toLowerCase(), word);
                    records.add(new Record(word, ProcessedSentence, Integer.sum(calculateScore(ProcessedSentence), checkOnList(word))));
                }
            }
        }

        // Step 5: Sort all acronym base on score
        Collections.sort(records, new Comparator<Record>() {
            @Override
            public int compare(Record record1, Record record2) {
                // Compare based on the integer field , descending order
                //return Integer.compare(record1.getIntegerValue(), record2.getIntegerValue());
                return Integer.compare(record2.getIntegerValue(), record1.getIntegerValue());
            }
        });

    }

    public static void fetchData() throws IOException {
        // Step 2: Create an ArrayList of dictionary words
        URL url = new URL("https://raw.githubusercontent.com/dwyl/english-words/master/words_alpha.txt");
        Scanner sc = new Scanner(url.openStream());

        // Step 2.5: Filter out roman numbers in database automatically
        String pattern = "^m{0,4}(cm|cd|d?c{0,3})(xc|xl|l?x{0,3})(ix|iv|v?i{0,3})$";

        while (sc.hasNext()) {
            String dictionary_word = sc.next().toLowerCase(); //.replaceAll("[^A-Za-z]", "");
            if (!dictionary_word.matches(pattern) && dictionary_word.length()>2) {
                dictionaryWords.add(dictionary_word);
            }
        }

        // Step 3: Get common word list
        URL url2 = new URL("https://www.mit.edu/~ecprice/wordlist.10000");
        Scanner sc2 = new Scanner(url2.openStream());
        while (sc2.hasNext()) {
            String common_dictionary_word = sc2.next().toLowerCase().replaceAll("[^A-Za-z]", "");
            if (common_dictionary_word.length() > 3) {
                CommonWords.add(common_dictionary_word);
            }
        }

    }

    public static boolean containsCharactersInOrder(String word1, String word2) {
        int i = 0; // Pointer for word1
        for (char c : word2.toCharArray()) {
            // Find the next occurrence of the character from word2 in word1
            i = word1.indexOf(c, i);
            if (i == -1) {
                // Character not found in word1
                return false;
            }
            i++; // Move the pointer in word1 to the next position
        }
        return true;
    }

    public static String capitalizeSubstring(String word1, String word2) {
        char[] word1Chars = word1.toCharArray();
        char[] word2Chars = word2.toCharArray();

        int word2Index = 0;

        for (int i = 0; i < word1Chars.length; i++) {
            if (word2Index < word2Chars.length && word1Chars[i] == word2Chars[word2Index]) {
                word1Chars[i] = Character.toUpperCase(word1Chars[i]);
                word2Index++;
            }
        }

        return new String(word1Chars);
    }

    /**
     * For each capitalized letter in the acronym:
     * 10 points if first letter in a word
     * 3 point if second or last letter in a word
     * 1 point otherwise
     * 2 bonus points if immediately following another capitalizd letter
     *
     * @param word is the received acronym with capped character
     * @return score
     */


    public static Integer calculateScore(String word) {
        Integer score = 0;
        char[] word_char = word.toCharArray();

        for (int i = 0; i < word_char.length - 1; i++) {
            //trigger when cap letter
            if (Character.isUpperCase(word_char[i])) {
                //first letter in a word
                if (i == 0) {
                    score = score + 10;
                }
                //first letter in a word
                else if (i > 0 && word_char[i - 1] == ' ') {
                    score = score + 10;
                }
                //3 point if second letter in a word
                else if (i > 1 && word_char[i - 2] == ' ') {
                    score = score + 3;
                }
                //3 point if last letter in a word
                else if (i < word_char.length - 2 && word_char[i + 1] == ' ') {
                    score = score + 3;
                }
                //else score = score + 1;
                //bonus, immediately following another capitalizd letter
//                if (i > 0 && Character.isUpperCase(word_char[i - 1])) {
//                    score = score + 2;
//                }
            }
        }

        return score;
    }

    public static Integer checkOnList(String word) {
        if (CommonWords.contains(word)) {
            return 5;
        }
        return 0;
    }
}






