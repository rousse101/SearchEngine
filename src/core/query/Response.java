package core.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import core.preprocess.DictSegment;
import core.preprocess.invertedIndex.InvertedIndex;
import core.util.Result;
import core.util.ResultGenerator;

public class Response {

	//����������
	private InvertedIndex invertedIndex;
	//���ؽ���б�
	private ArrayList<Result> results;
	
	//�ִ���
	private DictSegment dictSeg;
	
	private ResultGenerator resultGenerator;
	private int num;
	public Response()
	{
		invertedIndex = new InvertedIndex();
//		invertedIndex.ReaderIndex();
		dictSeg = new DictSegment();
		num =0;
		resultGenerator = new ResultGenerator();
	}
	public int getResultSize(){
		return num;
	}
	public ArrayList<Result> getResponse(String request,int n)
	{
		doQuery(request,n);
		return results;
	}
	
	//��ѯ���̣�
	//1. �ؼ��ʷִʡ��޳�ͣ�ôʣ����Էִʽ�����в��Ҷ�Ӧ�Ľ��
	//2. �ϲ������ִʵĽ�������س�������ҳURL��Ϣ
	//3. ����URLͨ�����ݿ�����ҳ����λ�ã��Ӷ���RAWs�л����ҳ����
	//4. ������ҳ���ݣ��޳�TAG�ȱ�ǩ��Ϣ����������ҳ��Result����
	//5. ��JSPҳ������ʾ����б������ʵ��ķ�ҳ
	//6. ��ɿ��չ���
	//ע��㣺
	//1. �������ܵ����⣬�����ҳ��Ƚϴ󣬺ܿ��ܻص�ֻ��ѯ�Ļ�������Դ�Ĵ�������
	//2. ������ҳ����������
	private void doQuery(String request,int N) {
		
		//1. �ؼ��ʷִʡ��޳�ͣ�ôʣ����Էִʽ�����в��Ҷ�Ӧ�Ľ��
		//2. �ϲ������ִʵĽ�������س�������ҳURL��Ϣ
		results = new ArrayList<Result>();
		System.out.println(request);
		ArrayList<String> keyWords = dictSeg.cutIntoWord(request,true);
		
		System.out.println("��ѯ�ؼ��ֱ���Ϊ");
		for(String keyWord : keyWords)
			System.out.println(keyWord);
		System.out.println("�ִʽ����ʾ������ \n");
		
		HashMap<String,Double> resultUrl = new HashMap<String,Double>();
		for(String keyWord : keyWords){
			//��������������ĵ��÷֣�������idf*tfֵ
			HashMap<String,Double> resultTemp = invertedIndex.DocScore(keyWord);			
			if(resultTemp != null){
				resultUrl = mergeResultURL(resultUrl, resultTemp);
			}
		}
		try{
		
			if(resultUrl.size() != 0){
				System.out.println("��ѯ�����URL�������£�");
				// 3. ����URLͨ�����ݿ�����ҳ����λ�ã��Ӷ���RAWs�л����ҳ����
				//���Ѿ�����÷ֵ��ĵ�����
				//TODO �������Կ��ǿ��Ʒ����ĵ���������
				ArrayList<String>temp = invertedIndex.SortDoc(resultUrl);
				this.num = temp.size();
				int end = N*10 >temp.size() ? temp.size() : N*10;
				for(int i =(N-1)*10 ;i <end; i ++){
					String url = temp.get(i);
					Result tempR = resultGenerator.generateResult(url,keyWords);
					if(tempR == null){
//						System.out.println(url + "��Ӧ��resultΪ�գ�����");
					}
					else
					{
//						System.out.println(url + "��Ӧ��result��Ϊ�ա�������");
						results.add(tempR);	
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
			
	}

	private HashMap<String,Double> mergeResultURL(HashMap<String,Double> resultUrl,
			HashMap<String,Double> resultTemp) {
		//�����һ��ִ�У���ôresultUrl���ǿյģ�ֱ�ӷ���resultTemp�Ϳ���
		if(resultUrl.size() == 0)
		{
			return resultTemp;
		}
		//������Ҫ�ϲ����ߵĹ�������,���Ұ��ĵ��÷����
		for (Iterator iter = resultTemp.entrySet().iterator(); iter.hasNext();) 
		{
			Map.Entry entry = (Map.Entry) iter.next(); // map.entry ͬʱȡ����ֵ��
			String url = (String) entry.getKey();
			double score =(Double)entry.getValue();
			
			if(resultUrl.containsKey(url))
			{
				double totalscore  = resultUrl.get(url)+score;
				resultUrl.put(url, totalscore);
			}else{
				resultUrl.put(url, score);
			}
		}
		return resultUrl;
	}

	/**
	 * @param args
	 * ���ڽ�������������Listneer�У�����Ŀǰ�ò����޷�ʹ�á�
	 */
	public static void main(String[] args) {

		Response response = new Response();
		for(int i =0;i<10;i++){
		ArrayList<Result> results = response.getResponse("�й�",1);
		
		System.out.println("���ؽ�����£�");
		for(Result result : results)
		{
			System.out.println(result.getTitle());
			System.out.println(result.getContent());
			System.out.println(result.getUrl() + "  " + result.getDate());
		}
		}
	}

}
