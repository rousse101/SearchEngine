package core.preprocess.invertedIndex;

public class DocPos {
    private String url=null; //文档名称
    private int num=0; //文档中出现的次数
    public void addTime(){
    	num++;   //出现的次数加1
    }
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getNum() {
		return num;
	}
}
