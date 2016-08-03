package ruixiang.lang;
import java.util.Comparator;


class SimilarityIndex implements Comparable<SimilarityIndex>{
		public double similarity;
		public int movieIndex;
		
		public SimilarityIndex(double similarity, int movieIndex) {
			this.similarity = similarity;
			this.movieIndex = movieIndex;
		}

		@Override
		public int compareTo(SimilarityIndex o) {
			// TODO Auto-generated method stub
			double res = o.similarity - similarity;
			if(res > 0){
				return 1;
			}else if(res < 0){
				return -1;
			}else{
				return 0;
			}
		}		
}