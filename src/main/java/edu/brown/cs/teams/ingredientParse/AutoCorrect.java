package edu.brown.cs.teams.ingredientParse;


import java.io.File;
import java.io.FileNotFoundException;

import java.util.*;

/**
     * Class to create autocorrect suggestions given input.
     * Adapted from Autocorrect from lab-git-testing written by cs32 course staff
     */
     class AutoCorrect {

        private Trie trie;
        private boolean prefix;
        private boolean whitespace;
        private int led;

        public AutoCorrect(String files, boolean prefixIn, boolean whitespaceIn) {
            trie = new Trie();
            trie.insertAll(parseCorpus(files));

            prefix = prefixIn;
            whitespace = whitespaceIn;
            led = 0;
        }

        /**
         * Takes in a string with file names and reads and parses the corpus.
         *
         * @param files: String with filenames separated by commas.
         * @return List of strings parsed from files.
         */
        private static List<String> parseCorpus(String files) {

            List<String> fileNames = new ArrayList<String>(Arrays.asList(files.split(",")));;
            List<String> words = new ArrayList<String>();

            for (String file : fileNames) {
                Scanner in = null;
                try {
                    in = new Scanner(new File(file));
                } catch (FileNotFoundException e) {
                    // If file does not exist, go to next file.
                    continue;
                }
                while (in.hasNextLine()) {
                    // Regex to make file consistent.
                    String nextLine = in.nextLine().toLowerCase().replaceAll("[^a-z ]", " ");
                    Scanner lineReader = new Scanner(nextLine);
                    while (lineReader.hasNext()) {
                        words.add(lineReader.next());
                    }
                    lineReader.close();
                }
                in.close();
            }
            return words;
        }

        /**
         * Given phrase input by user, gives autocorrect suggestions.
         *
         * @param phrase: String input by user
         * @return Set of strings representing suggestions
         */
        public PriorityQueue<String> suggest(String phrase) {
            // Regex to make input consistent.
            String query = phrase.toLowerCase().replaceAll("[^a-z ]", " ").trim().replaceAll(" +", " ");
            List<String> words = new ArrayList<String>(Arrays.asList(query.split(" ")));

            // Search trie for each type of flag set and add resulting words.
            Set<String> trieOutput = new TreeSet<String>();
            if (words.size() > 0) {
                String acWord = words.get(words.size() - 1);
                if (prefix) {
                    trieOutput.addAll(trie.findAllWithPrefix(acWord, acWord));
                }
                if (whitespace) {
                    trieOutput.addAll(trie.whiteSpace(acWord));
                }
                if (led > 0) {
                    trieOutput.addAll(trie.findLedWithinRoot(acWord, led));
                }
            }

            // Append suggestions to earlier part of phrase.
            List<String> trieOutputAsList = new ArrayList<String>(trieOutput);
            ledComparator comp = new ledComparator();
            comp.setDest(query);
            PriorityQueue<String> suggestions = new PriorityQueue<String>(comp);
                for (int i = 0; i < Math.min(5, trieOutputAsList.size()); i++) {
                    suggestions.add(trieOutputAsList.get(i));
                }

            return suggestions;
        }

}
