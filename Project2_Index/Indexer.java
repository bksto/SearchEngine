import java.io.*;
import java.util.*;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer { 
	
	public static void main(String[] args) throws CorruptIndexException, IOException{
        String outputDir = args[0];
        final File folder = new File("./parse_html10000");
        index(folder, outputDir);
	}
	
	 public static void index(File folder, String outputDir){
		String title; 
		String url; 
		String body; 
		float score = 0; 
		
		File index = new File(outputDir);
		//delete(index); 
		if(index.exists()){
			return; 
		}
		
		else{
			System.out.println("Indexing to directory '" + outputDir + "'...");
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            index(folder, outputDir);
	        } else {
	        	title = fileEntry.getName(); 
	        	title = title.substring(title.indexOf(' ')+1, title.length()-5); 
	        	url = getURL(fileEntry); 
	        	body = getBody(fileEntry); 

	        	WebDocument page = new WebDocument(title, body, url, score); 	
				IndexWriter writer = null;

				try {	
					IndexWriterConfig indexConfig = new IndexWriterConfig(Version.LUCENE_34, new StandardAnalyzer(Version.LUCENE_35));
					writer = new IndexWriter(FSDirectory.open(index), indexConfig);

					Document luceneDoc = new Document();	
					luceneDoc.add(new Field("text", page.body, Field.Store.YES, Field.Index.ANALYZED));
					luceneDoc.add(new Field("url", page.url, Field.Store.YES, Field.Index.NO));
					luceneDoc.add(new Field("title", page.title, Field.Store.YES, Field.Index.ANALYZED));
					writer.addDocument(luceneDoc);	

				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					if (writer !=null){
						try {
							writer.close();
						} catch (CorruptIndexException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}	
				}
	        }
	    }
		}
	}

	public static List<WebDocument> search_index (String queryString, int topk, String outputDir) throws CorruptIndexException, IOException {
		
		@SuppressWarnings("deprecation")
		IndexReader indexReader = IndexReader.open(FSDirectory.open(new File(outputDir)), true);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		QueryParser queryparser = new QueryParser(Version.LUCENE_34, "text", new StandardAnalyzer(Version.LUCENE_34));
		List<WebDocument> doc_list = new ArrayList<WebDocument>(); 
		String tmpurl, tmptitle, tmpbody;
		float score; 
		
		try {
			StringTokenizer strtok = new StringTokenizer(queryString, " ~`!@#$%^&*()_-+={[}]|:;'<>,./?\"\'\\/\n\t\b\f\r");
			String querytoparse = "";
			while(strtok.hasMoreElements()) {
				String token = strtok.nextToken();
				querytoparse += "text:" + token + "^1" + "title:" + token+ "^1.5";
				//querytoparse += "text:" + token;
			}		
			Query query = queryparser.parse(querytoparse);
			//System.out.println(query.toString());
			TopDocs results = indexSearcher.search(query, topk);
			
			//System.out.println(results.scoreDocs.length);
			for(int i = 0; i < topk; i++){
				tmptitle = indexSearcher.doc(results.scoreDocs[i].doc).getFieldable("title").stringValue(); 
				tmpbody = indexSearcher.doc(results.scoreDocs[i].doc).getFieldable("text").stringValue(); 
				tmpurl = indexSearcher.doc(results.scoreDocs[i].doc).getFieldable("url").stringValue(); 
				score = results.scoreDocs[i].score;
				WebDocument tmpdoc = new WebDocument(tmptitle, tmpbody, tmpurl, score); 
				//System.out.println(indexSearcher.doc(results.scoreDocs[i].doc).getFieldable("url").stringValue());
				doc_list.add(tmpdoc); 
			}	
		
			return doc_list;			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indexSearcher.close();
		}
		return null;
	}

	public static String getURL(final File f){
		String text = ""; 
		try{
			BufferedReader br = new BufferedReader(new FileReader(f));
			text = br.readLine(); 
			br.close();
		}catch(Exception e){
			System.out.println("Error getting URL"); 
		}
		return text.substring(4,text.length()-3); 
	}

	public static String getBody(final File f){
		String text = ""; 
		StringBuilder sb = new StringBuilder(); 
		String body = ""; 

		try{
			BufferedReader br = new BufferedReader(new FileReader(f));
			br.readLine(); 
			while(text != null){
				text = br.readLine(); 
				sb.append(text); 
				sb.append(System.lineSeparator()); 
			}
			body = sb.toString(); 
			br.close();
		}catch(Exception e){
			System.out.println("Error getting URL"); 
		}
		return body; 
	}
	
}


