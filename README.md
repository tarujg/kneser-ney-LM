# Kneser–Ney Language Model

Kneser–Ney is smoothing method for building a n-gram model and it is used to compute the probability distribution based on the frequency counts.

It is effective in smoothing due to
- Absolute discounting: subtracts a fixed count from the probability of lower-order terms to remove n-grams that are less frequent.
- It can also be viewed as an interpolation method for the same and works well with higher and lower order n-grams.

### Setup and Running
- Download the data from the following link [Download](https://drive.google.com/file/d/1bf1nEJ3OugegTuUqOsHMYVkpivz7nxZL/view?usp=sharing)
- Extract the following file in the code/ folder and put files in data/

### Running

Sanity check for Running
```sh
$ java -cp assign1.jar edu.berkeley.nlp.Test
Output: Test PASSED.
```

For building source
```sh
$ ant -f build_assign1.xml
```

Running the Language Model
```sh
$ java -cp assign1.jar:output.jar -server -mx500m edu.berkeley.nlp.assignments.assign1.LanguageModelTester -path ./data -lmType STUB
```
- Modify the -lmType flag to UNIGRAM/TRIGRAM to change to language models
- Optional flags
    - -noprint: to remove the printing of the translations
    - -sanityCheck: to run the program on a tiny portion of the data

#### References
[Kneser Ney explained](http://www.foldl.me/2014/kneser-ney-smoothing/)

#### License
MIT
