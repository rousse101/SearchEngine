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

	//倒排索引类
	private InvertedIndex invertedIndex;
	//返回结果列表
	private ArrayList<Result> results;
	
	//分词器
	private DictSegment dictSeg;
	
	private ResultGenerator resultGenerator;
	
	public Response()
	{
		invertedIndex = new InvertedIndex();
//		invertedIndex.createInvertedIndex();
		dictSeg = new DictSegment();
		resultGenerator = new ResultGenerator();
	}
	
	public ArrayList<Result> getResponse(String request)
	{
		doQuery(request);
		return results;
	}
	
	//查询过程：
	//1. 关键词分词、剔除停用词，并对分词结果进行查找对应的结果
	//2. 合并各个分词的结果，返回初步的网页URL信息
	//3. 根据URL通过数据库获得网页所在位置，从而在RAWs中获得网页内容
	//4. 整合网页内容，剔除TAG等标签信息，创建该网页的Result对象
	//5. 在JSP页面中显示结果列表，做出适当的分页
	//6. 完成快照功能
	//注意点：
	//1. 考虑性能的问题，如果网页库比较大，很可能回到只查询的缓慢和资源的大量消耗
	//2. 考虑网页的排名问题
	private void doQuery(String request) {
		
		//1. 关键词分词、剔除停用词，并对分词结果进行查找对应的结果
		//2. 合并各个分词的结果，返回初步的网页URL信息
		results = new ArrayList<Result>();
		ArrayList<String> keyWords = dictSeg.cutIntoWord(request,true);
		
		System.out.println("查询关键字被分为");
		for(String keyWord : keyWords)
			System.out.println(keyWord);
		System.out.println("分词结果显示结束啦 \n");
		
		HashMap<String,Double> resultUrl = new HashMap<String,Double>();
		HashMap<String,Double> resultTemp = new HashMap<String,Double>();
		
		for(String keyWord : keyWords){
			//调用索引类计算文档得分，方法是idf*tf值
			resultTemp = invertedIndex.DocScore(keyWord);			
			if(resultTemp != null){
				resultUrl = mergeResultURL(resultUrl, resultTemp);
			}	
		}
		
		try{
		
			if(resultUrl.size() != 0){
				System.out.println("查询结果的URL返回如下：");
				// 3. 根据URL通过数据库获得网页所在位置，从而在RAWs中获得网页内容
				//对已经计算得分的文档排序
				//TODO 后续可以考虑控制返回文档的量级。
				ArrayList<String>temp = invertedIndex.SortDoc(resultUrl);
				for(String url : temp){
					Result tempR = resultGenerator.generateResult(url);
					if(tempR == null)
						System.out.println(url + "对应的result为空！！！");
					else
					{
						System.out.println(url + "对应的result不为空。。。。");
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
		//如果第一次执行，那么resultUrl还是空的，直接返回resultTemp就可以
		if(resultUrl.size() == 0)
			return resultTemp;
		
		//否则需要合并两者的公共部分,并且把文档得分相加
		HashMap<String,Double> RESULT = new HashMap<String,Double>();
		for (Iterator iter = resultTemp.entrySet().iterator(); iter.hasNext();) 
		{
			Map.Entry entry = (Map.Entry) iter.next(); // map.entry 同时取出键值对
			String url = (String) entry.getKey();
			double score =(Double)entry.getValue();
			
			if(resultUrl.containsKey(url))
			{
				double totalscore  = resultUrl.get(url)+score;
				RESULT.put(url, totalscore);
			}
		}
		return RESULT;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Response response = new Response();
		ArrayList<Result> results = response.getResponse("教育");
		
		System.out.println("返回结果如下：");
		for(Result result : results)
		{
			System.out.println(result.getTitle());
			System.out.println(result.getContent());
			System.out.println(result.getUrl() + "  " + result.getDate());
		}
		
	}

}
