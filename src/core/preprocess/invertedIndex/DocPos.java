package core.preprocess.invertedIndex;

import java.io.Serializable;

/*
 *@decription 文档中的词域
 * */
public class DocPos implements Serializable{
    //词在标题中出现的总次数
    private int titleTime;
    //词在正文中出现的总次数
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
