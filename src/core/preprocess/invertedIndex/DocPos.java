package core.preprocess.invertedIndex;
/*
 *@decription �ĵ��еĴ���
 * */
public class DocPos{
	//���ڸ��ĵ��е��ܴ���
    private int totalTime;
    //���ڱ����г��ֵ��ܴ���
    private int titleTime;
    //���������г��ֵ��ܴ���
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
