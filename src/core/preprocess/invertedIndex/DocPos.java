package core.preprocess.invertedIndex;

public class DocPos {
    private String url=null; //�ĵ�����
    private int num=0; //�ĵ��г��ֵĴ���
    public void addTime(){
    	num++;   //���ֵĴ�����1
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
