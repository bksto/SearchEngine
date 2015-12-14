import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.net.URL; 


public class Crawler_thread implements Runnable{ 

    Crawl crawler; 

    public Crawler_thread(Crawl c){
        crawler = c; 
    }; 

    public void run(){
        Document doc; 
        Elements links; 
        String url_string; 
        
        try{
            doc = Jsoup.connect(crawler.url).get();
            links = doc.select("a[href]"); 
            for(Element link: links){
                url_string = link.attr("abs:href"); 
                url_string = clean(url_string);
                //check if in set 
                if(crawler.temp_share.isUnique(url_string) && url_string != null){
                    //System.out.println(url_string);
                    Crawl c_temp = new Crawl(crawler.depth++, url_string, crawler.out_file, crawler.temp_share, crawler.max_depth);
                    if(c_temp.depth < c_temp.max_depth){
                        //output_File.append(c_temp.url + '\n' + c_temp.depth); 
                        crawler.temp_share.add_into_queue(c_temp); 
                        crawler.temp_share.add_into_set(url_string);        
                    }
                }
            }
        }catch(Exception e) {
            //System.out.println("Error while reading file line by line: " + e.getMessage());
        }
    }

    //parse HTML/normalize
    public String clean(String link_string){
        if(link_string.indexOf("https") > -1)
        {
            link_string = null; 
            return null; 
        }
        if(link_string.indexOf("http") == -1)
        {
            return null;
        }
        if(link_string.indexOf('#') > -1)
        {
            link_string = link_string.substring(0, link_string.indexOf('#')); 
            return link_string; 
        }
        return link_string;
    }


    public void start()
    {
        Thread temp = new Thread(this);
        temp.start();
    }


}



