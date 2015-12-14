public class WebDocument {
	public String title;
	public String body;
	public String url;	
	public float score; 

	public WebDocument(String t, String b, String u, float s) {
		this.title = t;
		this.body = b;
		this.url = u;
		this.score = s; 
	}
}