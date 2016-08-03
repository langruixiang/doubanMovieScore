package ruixiang.lang;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Main {
	private static HashMap<String, Double> trueValue = new HashMap<String, Double>();
	
	static{		
		trueValue.put("超能陆战队 Big Hero 6", 8.6);
		trueValue.put("扑通扑通我的人生 두근두근 내 인생", 7.1);
		trueValue.put("灰姑娘 Cinderella", 6.8);
		trueValue.put("飓风营救 Taken", 8.2);
		trueValue.put("帕丁顿熊 Paddington", 7.6);
		trueValue.put("木星上行 Jupiter Ascending", 5.4);
		trueValue.put("北京纽约", 4.2);
		trueValue.put("狼图腾", 7.0);
		trueValue.put("冲上云霄 衝上雲霄", 4.4);
		trueValue.put("天将雄师", 6.0);
		trueValue.put("大喜临门 大囍臨門", 5.4);
		trueValue.put("失孤", 6.4);
		trueValue.put("钟馗伏魔：雪妖魔灵", 4.3);
		trueValue.put("爸爸去哪儿2", 4.5);
		trueValue.put("有一个地方只有我们知道", 5.0);
		trueValue.put("澳门风云2 賭城風雲2", 5.7);
		trueValue.put("封门诡影", 4.6);
		trueValue.put("海岛之恋", 3.0);		
		trueValue.put("熊出没之雪岭熊风", 7.2);
		trueValue.put("复仇者联盟2：奥创纪元 Avengers: Age of Ultron", 7.1);
		trueValue.put("念念", 7.0);
		trueValue.put("速度与激情7 Fast & Furious 7", 8.3);
		trueValue.put("高手们 기술자들", 6.3);
		trueValue.put("江南1970 강남 1970", 6.3);
		trueValue.put("赤道", 6.0);
		trueValue.put("西游记之大圣归来", 8.3);
		trueValue.put("何以笙箫默", 3.7);
		trueValue.put("左耳", 5.4);
		trueValue.put("一个勺子", 7.7);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		XMLUtil trainFile = new XMLUtil("./Data/train.xml");
		List<Movie> trainMovies = trainFile.getMoviesFromXML();
		
		XMLUtil predictFile1 = new XMLUtil("./Data/predict1.xml");
		List<Movie> predictMovies1 = predictFile1.getMoviesFromXML();
		
		XMLUtil predictFile2 = new XMLUtil("./Data/predict2.xml");
		List<Movie> predictMovies2 = predictFile2.getMoviesFromXML();
		
		ScorePredictModel model = new ScorePredictModel(trainMovies);
		model.train();
		
		System.out.println("================predict1==================");
		
        predictMovies1 =  model.predict(predictMovies1);
		
	    Collections.sort(predictMovies1);
	    double mae = 0.0;
	    double min = 100.0;
	    double max = 0.0;
	    for(Movie movie : predictMovies1){
	    	System.out.println(movie.name[0] + "  :  " + movie.score);
	    	double abs = Math.abs(trueValue.get(movie.name[0]) - movie.score);
	    	mae += abs;
	    	
	    	min = Math.min(min, abs);
	    	max = Math.max(max, abs);
	    }
	    System.out.println("Mean Error:" + mae / predictMovies1.size() + "\n" +
	                       "Max Error:" + max + "\n" +
	    		           "Min Error:" + min );
	    
		
	    System.out.println("================predict2==================");
	    
		predictMovies2 =  model.predict(predictMovies2);
		
	    Collections.sort(predictMovies2);
	    mae = 0.0;
	    max = 0.0;
	    min = 100.0;
	    for(Movie movie : predictMovies2){
	    	System.out.println(movie.name[0] + "  :  " + movie.score);
	    	double abs = Math.abs(trueValue.get(movie.name[0]) - movie.score);
	    	min = Math.min(min, abs);
	    	max = Math.max(max, abs);
	    	
	    	mae += abs;
	    }
	    
	    System.out.println("Mean Error:" + mae / predictMovies2.size() + "\n" +
                           "Max Error:" + max + "\n" +
		                   "Min Error:" + min );

	}

}
