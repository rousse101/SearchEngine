package core.util;


public class Page {

	private String url;
	private int offset;
	private String connent;
	private String rawName;
	private String rawtime;
	
	
	public Page()
	{
		
	}
	
	public String getUrl() {
		return url;
	}

	public int getOffset() {
		return offset;
	}

	public String getConnent() {
		return connent;
	}

	public String getRawName() {
		return rawName;
	}
	public String getRawtime() {
		return rawtime;
	}

	public Page(String url, int offset, String connent, String rawName)
	{
		this.url = url;
		this.offset = offset;
		this.connent = connent;
		this.rawName = rawName;
	}
	public void setPage(String url, int offset, String connent, String rawName)
	{
		this.url = url;
		this.offset = offset;
		this.connent = connent;
		this.rawName = rawName;
	}
	public void setPage(String url, int offset, String connent, String rawName,String rawtime)
	{
		this.url = url;
		this.offset = offset;
		this.connent = connent;
		this.rawName = rawName;
		this.rawtime = rawtime;
	}

	public void add2DB(DBConnection dbc) {
		if(rawtime !=null){
		String sql = "insert into pageindex(url, connent, offset, raws,pagetime)" +
			" values ('"+url+"', '"+connent+"', '"+offset+"', '"+rawName+"', '"+rawtime+"')";
		dbc.executeUpdate(sql);
		}
		else
		{
			System.out.println("��ҳʱ��û����");
		}
	}
	
}
