# Naive Bayes Classification in Java
## Robert Heumüller & Sebastian Mai

Simply start the java program. You will be prompted to select the data file.

* The Constructor of NBayes computes all the needed propabilties based on the Training-Set (data[0])
* The function classify uses those propabilties to classify an instance
* The function crossvalidate Computes the Classification-Rate based on the Validation-Set (data[1])
* The function printConfusionMatrix prints a confusion Matrix based on the Validation-Set

The Classification-Rate in 100 runs is about 85%.
