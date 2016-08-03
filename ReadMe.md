##Contents
* **Output** my output result files
* **Program** my program directory, which is developed by java with Eclipse
* **Slides** my catalog presentation slides and demo presentation slides
* **ReadMe** this file

##Function Introduction
Predict the douban score of a new movie.

##Data
* **Progeam/Data/train.xml** is movie information I crawled from Douban, including information of 460 movies.
* **Progeam/Data/predict1.xml** is movie information without score
* **Progeam/Data/predict2.xml** is similar to predict1.xml

##Referenced Library
* **dom4j** is used for reading xml file
* **weka** is used for linear regression model
* **lucene** is used for computing the similarity of movie summary
* **paoding-analysis** is used for chinese word segmentation
* **commons-logging** is a library referenced by **paoding-analysis**
* **dic** is the dictionary of Chinese word segmentation referenced by **paoding-analysis** 

##Class
* **ruixiang.lang.Main** is the entrance of this project
* **ruixiang.lang.Movie** is model of movie
* **ruixiang.lang.ScorePredictModel** is linear regression model
* **ruixiang.lang.SimilarityIndex** is an assistive class
* **ruixiang.lang.SimilarityMeasure** is used to compute the similarity of movie information
* **ruixiang.lang.XMLUtil** is used to read xml file

##Predict Result
See my **DemoPresentation.pptx** in Slides or see the images in Output
