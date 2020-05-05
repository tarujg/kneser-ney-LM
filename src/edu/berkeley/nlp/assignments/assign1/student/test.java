package edu.berkeley.nlp.assignments.assign1.student;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class test {
    public static void main(String a[])
    {
        List<List<String>> testInputs = new ArrayList<List<String>>();

        ArrayList<String> list1 = new ArrayList<String>();
        list1.add("a");
        list1.add("cat");
        list1.add("jumped");
        testInputs.add(list1);

        ArrayList<String> list2 = new ArrayList<String>();
        list2.add("the");
        list2.add("cat");
        list2.add("jumped");
        testInputs.add(list2);

        ArrayList<String> list3 = new ArrayList<String>();
        list3.add("a");
        list3.add("dog");
        list3.add("jumped");
        testInputs.add(list3);
/*
        ListIterator<List<String>> testIterator = testInputs.listIterator();
        KneserNeyLm ngramLM = new KneserNeyLm(testInputs);

        int word1 = ngramLM.wordIndexer.indexOf("a");
        int word2 = ngramLM.wordIndexer.indexOf("dog");
        int word3 = ngramLM.wordIndexer.indexOf("jumped");
        int word4 = ngramLM.wordIndexer.indexOf("ran");
        int[] input = {word1, word2, word3};
        System.out.println(ngramLM.getCount(input));
        System.out.println(ngramLM.getNgramProbability(input, 0,3));


        Iterator it = ngramLM.trigramCounts.getIterator();
        for(long key = (long) it.next();it.hasNext();key = (long) it.next()){
            String w1 = ngramLM.wordIndexer.get((int)ngramLM.bitExtracted(key, 1, 0));
            String w2 = ngramLM.wordIndexer.get((int)ngramLM.bitExtracted(key, 1, 1));
            String w3 = ngramLM.wordIndexer.get((int)ngramLM.bitExtracted(key, 1, 2));
                    System.out.println(w1+" "+w2+" "+w3+" - "+ngramLM.trigramCounts.get(key));
        }

*/

    }
}