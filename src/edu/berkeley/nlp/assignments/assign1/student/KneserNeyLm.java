package edu.berkeley.nlp.assignments.assign1.student;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import edu.berkeley.nlp.langmodel.NgramLanguageModel;
import edu.berkeley.nlp.langmodel.EnglishWordIndexer;
import edu.berkeley.nlp.util.StringIndexer;

public class KneserNeyLm implements NgramLanguageModel {

    // Start symbol to be prepended to all sentences
    static final String START = NgramLanguageModel.START;
    // Start symbol to be appended to all sentences
    static final String STOP = NgramLanguageModel.STOP;

    // discounting factor
    double d = 0.75;
    // probability
    double unkown_prop = 0.0001;

    static final int UNIGRAM_COUNT = 495172;//1000000;//495172; //Add 10000 for errors
    static final int BIGRAM_COUNT = 8374230;
    static final int TRIGRAM_COUNT = 41627672;

    static final int BITSPERWORD = 20;
    static final long BITMASK = (1 << BITSPERWORD) - 1;

    // Raw counts ngrams
    /*
    OpenHash trigramCounts = new OpenHash(TRIGRAM_COUNT);
    OpenHash bigramCounts = new OpenHash(BIGRAM_COUNT);
    long[] unigramCounts = new long[UNIGRAM_COUNT];
    OpenHash bigramFertility = new OpenHash(BIGRAM_COUNT);;
    long[] unigramFertility = new long[UNIGRAM_COUNT];
    long totalFertility = 0;

    OpenHash w1w2x = new OpenHash(BIGRAM_COUNT);;
    long[] w1x = new long[UNIGRAM_COUNT];

    // Denominators for all ngrams
    long[] bigramNormalizer = new long[UNIGRAM_COUNT];
    */

    private OpenHash trigramCounts = new OpenHash(TRIGRAM_COUNT);
    private OpenHash bigramCounts = new OpenHash(BIGRAM_COUNT);
    private long[] unigramCounts = new long[UNIGRAM_COUNT];

    // Fertility Counts
    private OpenHash bigramFertility = new OpenHash(BIGRAM_COUNT);
    ;
    private long[] unigramFertility = new long[UNIGRAM_COUNT];
    private long totalFertility = 0;

    private OpenHash w1w2x = new OpenHash(BIGRAM_COUNT);
    ;
    private long[] w1x = new long[UNIGRAM_COUNT];

    // Denominators for all ngrams
    private long[] bigramNormalizer = new long[UNIGRAM_COUNT];

    /**
     * Initializes the KneserNeyLm by populating all the counts and tables above
     *
     * @param sentenceCollection has a list of sentences for the language model
     */
    public KneserNeyLm(Iterable<List<String>> sentenceCollection) {

        System.out.println("Building KneserNey LanguageModel...");

        int sent = 0;
        for (List<String> sentence : sentenceCollection) {
            sent++;
            if (sent % 1000000 == 0) System.out.println("On sentence " + sent);
            List<String> stoppedSentence = new ArrayList<String>(sentence);
            stoppedSentence.add(0, START);
            stoppedSentence.add(STOP);

            // Convert all sentences to indexed numbers
            int[] indexedSentence = new int[stoppedSentence.size()];
            for (int i = 0; i < stoppedSentence.size(); i++) {
                indexedSentence[i] = EnglishWordIndexer.getIndexer().addAndGetIndex(stoppedSentence.get(i));
            }

            // Iterate sentence to compute counts
            for (int i = 2; i < stoppedSentence.size(); i++) {
                if (i == 2) {
                    //First two words of sentence
                    unigramCounts[indexedSentence[0]]++;
                    unigramCounts[indexedSentence[1]]++;
                    bigramCounts.put(bitIndexer(indexedSentence, 2, 0), 1);
                }

                // Add counts for the ith word
                unigramCounts[indexedSentence[i]]++;
                bigramCounts.put(bitIndexer(indexedSentence, 2, i - 1), 1);
                trigramCounts.put(bitIndexer(indexedSentence, 3, i - 2), 1);
            }
        }

        // Set BigramFertilityCounts
        Iterator it = trigramCounts.getIterator();
        while (it.hasNext()) {
            long key = (long) it.next();
            bigramFertility.put(bitExtracted(key, 2, 1), 1);
            w1w2x.put(bitExtracted(key, 2, 0), 1);
        }

        // Set Bigram Normalizer
        it = bigramFertility.getIterator();
        while (it.hasNext()) {
            long key = (long) it.next();
            bigramNormalizer[(int) bitExtracted(key, 1, 0)] += bigramFertility.get(key);
        }

        // Set UnigramFertilityCounts
        it = bigramCounts.getIterator();
        while (it.hasNext()) {
            long key = (long) it.next();
            unigramFertility[(int) bitExtracted(key, 1, 1)]++;
            w1x[(int) bitExtracted(key, 1, 0)]++;
            totalFertility++;
        }

        System.out.println("Done building KneserNey LanguageModel.");
        System.out.println(bigramCounts.size());
        System.out.println(trigramCounts.size());

        System.out.println(unigramFertility[149228]);
        printNGram(new int[] {149228},0,1);

    }

    /**
     * @returns Maximum order of n-gram that will be scored by the model
     ***/
    public int getOrder() {
        return 3;
    }

    /**
     * Scores the sequence of words in the ngram array over the subrange
     * Usage e.g. getNgramLogProbability([17,15,18],1,3) returns log P(w_i=18 | w_{i-1} = 15)
     *
     * @param ngram - array of integers where each word maps to String via EnglishWordIndexer.getIndexer()
     * @param from  - starting index of subrange of ngram array
     * @param to    - ending range of subrange of ngram array
     * @return log probability in the range (-infinity, 0].
     * (Double.NEGATIVE_INFINITY or Double.NaN. not allowed)
     */
    public double getNgramLogProbability(int[] ngram, int from, int to) {
        if (ngram[to - 1] >= unigramFertility.length)
            return -100;
        double a = Math.log(getNgramProbability(ngram, from, to));
        if (a < -100) {
            //System.out.println(a);
            //printNGram(ngram, from, to);
        }

        return a;
    }

    public double getNgramProbability(int[] ngram, int from, int to) {

        if (to - from > 3) {
            System.out.println("WARNING: to - from > 3 for Trigram Model");
        }
        /*
        if(to-from == 3)
        {
            long trigramIndex = bitIndexer(ngram, 3, from);
            long bigramIndex = bitIndexer(ngram, 2, from);

            double denominator = bigramCounts.get(bigramIndex);
            if(denominator <= 0) {
                return getNgramProbability(ngram,from+1,to);
            }
            else {

            }

        }


        */
        if (to - from == 3) {
            // Handle Trigram probability
            long trigramIndex = bitIndexer(ngram, 3, from);
            long bigramIndex = bitIndexer(ngram, 2, from);

            double denominator = bigramCounts.get(bigramIndex);

            return (denominator <= 0) ?
                    getNgramProbability(ngram, from + 1, to) :
                    //P_continuation
                    Math.max(trigramCounts.get(trigramIndex) - d, 0) / denominator +
                            //BackOff - alpha*P_backoff
                            ((double) w1w2x.get(bigramIndex) * d / denominator) * getNgramProbability(ngram, from + 1, to);
        } else if (to - from == 2) {
            long bigramIndex = bitIndexer(ngram, 2, from);
            return (bigramNormalizer.length > ngram[from] && bigramNormalizer[ngram[from]] > 0) ?
                    //P_continuation
                    (Math.max(bigramFertility.get(bigramIndex) - d, 0) / (double) bigramNormalizer[ngram[from]]) +
                            //BackOff - alpha*P_backoff
                            ((double) w1x[ngram[from]] * d / (double) bigramNormalizer[ngram[from]]) * getNgramProbability(ngram, from + 1, to)
                    : getNgramProbability(ngram, from + 1, to);
        } else if (to - from == 1 && ngram[from] < unigramFertility.length && ngram[from] >= 0) {
            return (double) unigramFertility[ngram[from]] / (double) totalFertility;
        } else return unkown_prop;

    }

    /**
     * @param ngram - an array of word indexes
     * @return count of an n-gram if within getOrder length.
     */
    public long getCount(int[] ngram) {
        switch (ngram.length) {
            case 1:
                if (ngram[0] > 0 && ngram[0] < unigramCounts.length)
                    return unigramCounts[ngram[0]];
            case 2:
                return bigramCounts.get(bitIndexer(ngram, 2, 0));
            case 3:
                return trigramCounts.get(bitIndexer(ngram, 3, 0));
        }
        return 0;
    }

    // Packs the word index of the ngram into ngram_order*BITSPERWORD
    public long bitIndexer(int[] ngram, int ngram_order, int offset) {
        long index = 0;
        for (int i = 0; i < ngram_order; i++) {
            index += (ngram[i + offset] & (BITMASK)) << (i * BITSPERWORD);
        }
        return index;
    }

    // Extract k words from pth word of packed number
    public long bitExtracted(long number, int k, int p) {
        k *= BITSPERWORD;
        p *= BITSPERWORD;
        return (((1L << k) - 1) & (number >> p));
    }

    private static void printNGram(int[] ngram, int from, int to)
    {
        String[] words = new String[to - from];
        for (int i = from; i < to; i++)
        {
            if (ngram[i] != -1) {
                words[i] = EnglishWordIndexer.getIndexer().get(ngram[i]);
            }
            else {
                words[i] = "<UNK>";
            }
        }
        System.out.println("---------------------");
        System.out.println(Arrays.toString(ngram));
        System.out.println(Arrays.toString(words));
        System.out.println("---------------------");
    }
}
