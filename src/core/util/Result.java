package core.util;

public class Result {

	private String title;
	private String content;
	private String url;
	private String date;
	private String view;
	public Result(String title, String content, String url, String date,String view)
	{
		this.title = title;
		this.content = content;
		this.url = url;
		this.date = date;
		this.view = view;
	}
	
	
	public String getView() {
		return view;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getUrl() {
		return url;
	}

	public String getDate() {
		return date;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
