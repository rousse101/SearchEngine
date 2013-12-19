package core.preprocess.invertedIndex;
/*
 *@decription 文档中的词域
 * */
public class DocPos{
	//词在该文档中的总次数
    private int totalTime;
    //词在标题中出现的总次数
    private int titleTime;
    //词在正文中出现的总次数
    private int bodyTime;
    public DocPos(){
    	totalTime = titleTime = bodyTime =0;
    }
    public DocPos(int title,int body){
    	titleTime = title;
    	bodyTime =body;
    	totalTime = title+body;
    }
	public int getTotalTime() {
		return totalTime;
	}
	public int getTitleTime() {
		return titleTime;
	}
	public void setTitleTime(int titleTime) {
		this.titleTime = titleTime;
		this.totalTime = this.bodyTime+this.titleTime;
	}
	public int getBodyTime() {
		return bodyTime;
	}
	public void setBodyTime(int bodyTime) {
		this.bodyTime = bodyTime;
		this.totalTime = this.bodyTime+this.titleTime;
	}
    
}
