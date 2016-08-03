package ruixiang.lang;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

public class SimilarityMeasure {
	private static Directory indexDirectory = null;
	private static IndexSearcher indexSearcher = null;
	private static Analyzer analyzer = null;
	
	public static double getJaccardSimilarity(String[] arr1, String[] arr2){		
		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		
		for(String item : arr1){
			set1.add(item);
		}
		
		for(String item : arr2){
			set2.add(item);
		}
		
		Set<String> res = new HashSet<String>();

		res.addAll(set1);
		res.retainAll(set2);
		int joinSize = res.size();
		
		res.clear();
		res.addAll(set1);
		res.addAll(set2);
		int unionSize = res.size();
		
		return joinSize * 1.0 / unionSize;		
	}
	
	public static int[] getSummarySimilarityIndex(Movie movie){
		QueryParser queryParser = new QueryParser(Version.LUCENE_41, "summary", analyzer);
		int[] ret = null;
		
		try {
			if(movie.summary.length() == 0){
				return new int[0];
			}
			
			Query query = queryParser.parse(movie.summary);
			ScoreDoc[] hits = indexSearcher.search(query, 5).scoreDocs;
			ret = new int[Math.min(ScorePredictModel.topN, hits.length)];
			
			for(int i = 0; i < ret.length; i++){
				Document hitDoc = indexSearcher.doc(hits[i].doc);  
		        ret[i] = Integer.parseInt(hitDoc.get("id"));  
			}
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static void buildIndex(List<Movie> trainData){
		System.out.println("\n============Start to build inverted index==============");
		analyzer = new PaodingAnalyzer();  
		indexDirectory = new RAMDirectory();  
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_41, analyzer);  
        try {
			IndexWriter iwriter = new IndexWriter(indexDirectory, config);
			for(int i = 0; i < trainData.size(); i++){
				Document doc = new Document();
				doc.add(new Field("summary", trainData.get(i).summary, TextField.TYPE_STORED));
				doc.add(new Field("id", "" + i, TextField.TYPE_STORED));
				iwriter.addDocument(doc);
			}
			
			iwriter.close();
			System.out.println("=============Inverted index build complete==================");
			
			indexSearcher = new IndexSearcher(DirectoryReader.open(indexDirectory));			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	public static void main(String[] args){
		String[] arr1 = new String[]{"你", "我", "吗"};
		String[] arr2 = new String[]{"你", "我", "是"};
		
		System.out.println(new SimilarityMeasure().getJaccardSimilarity(arr1, arr2));
	}

}
