/**
 * 
 */
package core.preprocess.invertedIndex;

/**
 * @author Administrator
 *
 */
//type��1��ʾ�����ڱ��⡣0��ʾ������body�С�
public class WordFiled {
	private String word;
	private int type;
	public WordFiled(String word,int type){
		this.word = word;
		this.type = type;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
