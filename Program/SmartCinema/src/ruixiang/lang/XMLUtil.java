package ruixiang.lang;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XMLUtil {
	
	String fileName;
	List<Movie> movieList = new LinkedList<Movie>();
	
	public XMLUtil(String fileName){
		this.fileName = fileName;
	}
	
	public List<Movie> getMoviesFromXML(){

        try {
			SAXReader reader = new SAXReader();  
			//读取文件 转换成Document  
			Document document = reader.read(new File(fileName));  
			//获取根节点元素对象  
			Element root = document.getRootElement();  
			//遍历  
			listNodes(root);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
        return movieList;
	}
	
	private Movie getMovie(Element node){
		Movie ret = new Movie();
		
		try {
			List<Element> idList = node.element("movieid").elements("value");
			for(Element e : idList){
				ret.id = Long.valueOf(e.getStringValue());
			}
			
			List<Element> nameList = node.element("name").elements("value");
			ret.name = new String[nameList.size()];
			int index = 0;
			for(Element e : nameList){
				ret.name[index++] = e.getStringValue();
			}
			
			List<Element> typeList = node.element("movietype").elements("value");
			ret.type = new String[typeList.size()];
			index = 0;
			for(Element e : typeList){
				ret.type[index++] = e.getStringValue();
			}
			
			List<Element> actorList = node.element("actor").elements("value");
			ret.actors = new String[actorList.size()];
			index = 0;
			for(Element e : actorList){
				ret.actors[index++] = e.getStringValue();
			}
			
			List<Element> summaryList = node.element("summary").elements("value");
			ret.summary = "";
			for(Element e : summaryList){
				ret.summary += e.getStringValue();
				ret.summary += " ";
			}
			
			List<Element> directorList = node.element("director").elements("value");
			ret.directors = new String[directorList.size()];
			index = 0;
			for(Element e : directorList){
				ret.directors[index++] = e.getStringValue();
			}
			
			List<Element> scoreList = node.element("score").elements("value");
			for(Element e : scoreList){
				String score = e.getStringValue();
				if(score.length() > 0){
					ret.score = Double.valueOf(score);
				}			
			}
			
			List<Element> dateList = node.element("date").elements("value");
			ret.date = new String[dateList.size()];
			index = 0;
			for(Element e : dateList){
				ret.date[index++] = e.getStringValue();
			}
			
			List<Element> runtimeList = node.element("runtime").elements("value");
			ret.runtime = new String[runtimeList.size()];
			index = 0;
			for(Element e : runtimeList){
				ret.runtime[index++] = e.getStringValue();
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return  null;
		}
		
		return ret;
	}
	
	private void listNodes(Element node){ 
        System.out.println("=============Reading file begin...==============");
        Iterator<Element> iterator = node.elementIterator();  
        while(iterator.hasNext()){  
            Element e = iterator.next();  
            Movie movie = getMovie(e);
            if(movie != null){
            	movieList.add(movie);
            }
            
//            System.out.println(movie);
        }
        
        System.out.println("=============Reading file success!=============");
	}
	
	public static void main(String[] args){
		XMLUtil xmlUtil = new XMLUtil("./Data/train.xml");
		List<Movie> list = xmlUtil.getMoviesFromXML();
	}

}
