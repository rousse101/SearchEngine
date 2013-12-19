/**
 * 
 */
package test;

/**
 * @author Administrator
 *
 */
class test {
	private static int time;
	public void settime(int time){
		this.time = time;
	}
	public int gettime(){
		return time;
	}
	
}
public class testStatic {

	/**
	 * @param args
	 */
	private test test;
	public testStatic(){
		test= new test();
		test.settime(10);
	}
	public void  print(){
		System.out.println("test"+test.gettime());
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testStatic ts = new testStatic();
		ts.print();
		test ts2 = new test();
		System.out.println("test"+ts2.gettime());

	}

}
