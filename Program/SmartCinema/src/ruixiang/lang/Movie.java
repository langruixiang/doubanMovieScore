package ruixiang.lang;
import java.util.Arrays;

public class Movie implements Comparable<Movie>{
	public long id;
	public String[] name;
	public String[] type;
	public String[] actors;
	public String summary;
	public String[] directors;
	
	public double score = 0.0;
	public String[] date;
	public String[] runtime;
	
	
	@Override
	public String toString() {
		return "Movie [id=" + id + ", name=" + Arrays.toString(name) + ", type=" + Arrays.toString(type) + ", actors="
				+ Arrays.toString(actors) + ", summary=" + summary + ", directors="
				+ Arrays.toString(directors) + ", score=" + score + ", date=" + Arrays.toString(date) + ", runtime="
				+ Arrays.toString(runtime) + "]";
	}


	@Override
	public int compareTo(Movie m) {
		// TODO Auto-generated method stub
		double res = m.score - score;
		if(res > 0){
			return 1;
		}else if(res < 0){
			return -1;
		}
		return 0;
	}	
}
