package core.preprocess.invertedIndex;

import java.io.Serializable;

/*
 *@decription �ĵ��еĴ���
 * */
public class DocPos implements Serializable{
    //���ڱ����г��ֵ��ܴ���
    private int titleTime;
    //���������г��ֵ��ܴ���
    private int bodyTime;
    public DocPos(){
    	 titleTime = bodyTime =0;
    }
    
	public DocPos(int title,int body){
    	titleTime = title;
    	bodyTime =body;
    }
	public int getTitleTime() {
		return titleTime;
	}
	public void setTitleTime(int titleTime) {
		this.titleTime = titleTime;
	}
	public int getBodyTime() {
		return bodyTime;
	}
	public void setBodyTime(int bodyTime) {
		this.bodyTime = bodyTime;
	}
    
}
