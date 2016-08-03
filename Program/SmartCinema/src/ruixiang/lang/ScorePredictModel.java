package ruixiang.lang;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;

public class ScorePredictModel {
	
	private List<Movie> trainData = new LinkedList<Movie>();
	private String trainArff = "train.arff";
	private String testArff = "test.arff";
	public static int topN = 20;
	private LinearRegression lr = null;
	
	private DataSource train_data;
	
	public ScorePredictModel(List<Movie> trainData){
		this.trainData = trainData;
		SimilarityMeasure.buildIndex(trainData);
	}
	
	public List<Movie> predict(List<Movie> targetMovies){
		System.out.println("\n==============Predicting Start================");
		generatePredictFile(targetMovies);
		
		try {
			DataSource predict_data = new DataSource(this.testArff);
			Instances insTest = predict_data.getDataSet();
			insTest.setClassIndex(insTest.numAttributes() - 1);
			
			Evaluation eval = new Evaluation(insTest);  
	        double[] predictScore = eval.evaluateModel(lr, insTest); 
	        
	        for(int i = 0; i < predictScore.length; i++){
	        	targetMovies.get(i).score = predictScore[i];
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("=================Predicting End===============");
		return targetMovies;
	}
	
	public LinearRegression train(){
		System.out.println("\n============Training Start===================");
		generateTrainFile();
		try {
			train_data = new DataSource(this.trainArff);
			Instances insTrain = train_data.getDataSet(); 
			insTrain.setClassIndex(insTrain.numAttributes()-1);
			lr = new LinearRegression();
			
			SelectedTag  selectedTag=new SelectedTag(LinearRegression.SELECTION_M5,LinearRegression.TAGS_SELECTION);
			lr.setAttributeSelectionMethod(selectedTag);
			lr.setDebug(false);
			lr.setEliminateColinearAttributes(true);
//			lr.setMinimal(false);
			lr.setRidge(1e-2);
			
			lr.buildClassifier(insTrain);
			
			Evaluation eval=new Evaluation(insTrain);  

			eval.evaluateModel(lr, insTrain);

			double mean = eval.meanAbsoluteError();

			String model=lr.toString();
			
			System.out.println("Train Mean Absolute Error:" + mean);
			System.out.println("Train Result：" + model);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//读训练数据 
		System.out.println("============Training Success=================");
		
		return lr;
	}
	
	public double predict(Movie predictMovie){
		return 0.0;
	}
	
	private void generateTrainFile(){
		String relation = "@relation scoretable";
		String actorSimialrity = "@attribute actorScore numeric";
		String directorSimilarity = "@attribute directorScore numeric";
		String typeSimilarity = "@attribute typeScore numeric";
		String summarySimilarity = "@attribute summaryScore numeric";
		
		String scoreLine = "@attribute score numeric";
		
		try {
			BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.trainArff), "utf-8"));
			fileWriter.write(relation + "\n");
			fileWriter.write(actorSimialrity + "\n");
			fileWriter.write(directorSimilarity + "\n");
			fileWriter.write(typeSimilarity + "\n");
			fileWriter.write(summarySimilarity + "\n");
			fileWriter.write(scoreLine + "\n");
			fileWriter.write("@data" + "\n");
			
			for(int i = 0; i < trainData.size(); i++){
				double actorScore = getActorMeanScore(i);
				double directorScore = getDirectorMeanScore(i);
				double typeScore = getTypeMeanScore(i);
				double summaryScore = getSummaryMeanScore(i);
				double score = trainData.get(i).score;
				
				fileWriter.write(actorScore + "," + directorScore + "," +
				                 typeScore + "," + summaryScore + "," + score + "\n");
			}
			
			fileWriter.flush();
			fileWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void generatePredictFile(List<Movie> list){
		String relation = "@relation predicttable";
		String actorSimialrity = "@attribute actorScore numeric";
		String directorSimilarity = "@attribute directorScore numeric";
		String typeSimilarity = "@attribute typeScore numeric";	
		String summarySimilarity = "@attribute summaryScore numeric";
		String scoreLine = "@attribute score numeric";
		
		BufferedWriter fileWriter;
		try {
			fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.testArff), "utf-8"));
			fileWriter.write(relation + "\n");
			fileWriter.write(actorSimialrity + "\n");
			fileWriter.write(directorSimilarity + "\n");
			fileWriter.write(typeSimilarity + "\n");
			fileWriter.write(summarySimilarity + "\n");
			fileWriter.write(scoreLine + "\n");
			fileWriter.write("@data" + "\n");
			
			for(int i = 0; i < list.size(); i++){
				double actorScore = getActorMeanScore(list.get(i));
				double directorScore = getDirectorMeanScore(list.get(i));
				double typeScore = getTypeMeanScore(list.get(i));
				double summaryScore = getSummaryMeanScore(list.get(i));
				
				fileWriter.write(actorScore + "," + directorScore + "," + 
				                 typeScore + "," + summaryScore + "," + "0.0" + "\n");
			}
			
			fileWriter.flush();
			fileWriter.close();
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private double getActorMeanScore(int targetPosition){
		Movie targetMovie = trainData.get(targetPosition);
		
		Queue<SimilarityIndex> queue = new PriorityQueue<SimilarityIndex>();
		for(int i = 0; i < trainData.size(); i++){
			if(i != targetPosition){
				Movie ite = trainData.get(i);
				double similarity = SimilarityMeasure.getJaccardSimilarity(targetMovie.actors, ite.actors);
				queue.add(new SimilarityIndex(similarity, i));
			}
		}
		
		double sum = 0.0;
		for(int i = 0; i < topN; i++){
			Movie movie = trainData.get(queue.poll().movieIndex);
			sum += movie.score;
		}
		
		return sum / topN;
	}
	
	private double getActorMeanScore(Movie predictMovie){
		Queue<SimilarityIndex> queue = new PriorityQueue<SimilarityIndex>();
		for(int i = 0; i < trainData.size(); i++){
			Movie ite = trainData.get(i);
			double similarity = SimilarityMeasure.getJaccardSimilarity(predictMovie.actors, ite.actors);
			queue.add(new SimilarityIndex(similarity, i));
		}
		
		double sum = 0.0;
		for(int i = 0; i < topN; i++){
			Movie movie = trainData.get(queue.poll().movieIndex);
			sum += movie.score;
		}
		
		return sum / topN;
	}
	
	private double getDirectorMeanScore(int targetPosition){
		Movie targetMovie = trainData.get(targetPosition);
		
		Queue<SimilarityIndex> queue = new PriorityQueue<SimilarityIndex>();
		for(int i = 0; i < trainData.size(); i++){
			if(i != targetPosition){
				Movie ite = trainData.get(i);
				double similarity = SimilarityMeasure.getJaccardSimilarity(targetMovie.directors, ite.directors);
				queue.add(new SimilarityIndex(similarity, i));
			}
		}
		
		double sum = 0.0;
		for(int i = 0; i < topN; i++){
			Movie movie = trainData.get(queue.poll().movieIndex);
			sum += movie.score;
		}
		
		return sum / topN;
	}
	
	private double getDirectorMeanScore(Movie predictMovie){
		Queue<SimilarityIndex> queue = new PriorityQueue<SimilarityIndex>();
		for(int i = 0; i < trainData.size(); i++){
			Movie ite = trainData.get(i);
			double similarity = SimilarityMeasure.getJaccardSimilarity(predictMovie.directors, ite.directors);
			queue.add(new SimilarityIndex(similarity, i));
		}
		
		double sum = 0.0;
		for(int i = 0; i < topN; i++){
			Movie movie = trainData.get(queue.poll().movieIndex);
			sum += movie.score;
		}
		
		return sum / topN;
	}
	
	private double getTypeMeanScore(int targetPosition){
		Movie targetMovie = trainData.get(targetPosition);
		
		Queue<SimilarityIndex> queue = new PriorityQueue<SimilarityIndex>();
		for(int i = 0; i < trainData.size(); i++){
			if(i != targetPosition){
				Movie ite = trainData.get(i);
				double similarity = SimilarityMeasure.getJaccardSimilarity(targetMovie.type, ite.type);
				queue.add(new SimilarityIndex(similarity, i));
			}
		}
		
		double sum = 0.0;
		for(int i = 0; i < topN; i++){
			Movie movie = trainData.get(queue.poll().movieIndex);
			sum += movie.score;
		}
		
		return sum / topN;	
	}
	
	private double getTypeMeanScore(Movie predictMovie){
		Queue<SimilarityIndex> queue = new PriorityQueue<SimilarityIndex>();
		for(int i = 0; i < trainData.size(); i++){
			Movie ite = trainData.get(i);
			double similarity = SimilarityMeasure.getJaccardSimilarity(predictMovie.type, ite.type);
			queue.add(new SimilarityIndex(similarity, i));
		}
		
		double sum = 0.0;
		for(int i = 0; i < topN; i++){
			Movie movie = trainData.get(queue.poll().movieIndex);
			sum += movie.score;
		}
		
		return sum / topN;
	}
	
	private double getSummaryMeanScore(int targetPosition){
		Movie targetMovie = trainData.get(targetPosition);
		int[] index = SimilarityMeasure.getSummarySimilarityIndex(targetMovie);
		
		System.out.println(Arrays.toString(index));
		
		double sum = 0.0;

		for(int i = 0; i < Math.min(topN, index.length); i++){
			Movie movie = trainData.get(index[i]);
			sum += movie.score;
		}
		
		return sum / topN;
		
	}
	
	private double getSummaryMeanScore(Movie targetMovie){
		int[] index = SimilarityMeasure.getSummarySimilarityIndex(targetMovie);
		
		double sum = 0.0;
		for(int i = 0; i < Math.min(index.length,topN); i++){
			Movie movie = trainData.get(index[i]);
			sum += movie.score;
		}
		
		return sum / topN;
	}
	
	
}
