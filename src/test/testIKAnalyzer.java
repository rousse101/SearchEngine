/**
 * 
 */
package test;
import java.io.IOException;
import java.io.StringReader;

import org.wltea.analyzer.core.IKSegmenter;  
import org.wltea.analyzer.core.Lexeme;  
/**
 * @author Administrator
 *
 */
public class testIKAnalyzer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String text= "����������һ����ͬ־����<>���ǺͶ�������789��";
		StringReader sr = new StringReader(text);
		IKSegmenter ik=new IKSegmenter(sr, true); 
		Lexeme lex=null; 
		try {
			while((lex=ik.next())!=null){
				System.out.println(lex.getLexemeText()+"\t");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
