import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.*;
import java.util.Iterator;
import java.util.List;

public class Crawl{

	public int depth; 
	public int max_depth; 
	public String url; 
	public String out_file;
	public Thread_share temp_share; 

	public Crawl(int x, String y, String z, Thread_share w, int m){
		depth = x; 
		url = y; 
		out_file = z;
		temp_share = w; 
		max_depth = m; 
	}

	public void print(){
		System.out.println("Depth: " + depth + ", URL: " + url + " Queue: "); 
		temp_share.print_queue(); 
	}
}