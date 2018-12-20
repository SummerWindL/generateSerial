package com.test.demo;

import java.util.Date;

import org.apache.http.client.utils.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.test.demo.main.StartUpApplication;

/**
 *@date 2018年8月7日-下午9:41:19
 *@author fu yanliang
 *@action(作用)
 *@instruction
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StartUpApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GenerateSerialNumber {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private final static ReentrantLock lock = new ReentrantLock();
	
	public String generateSerialNum() {
		//当天的初始化流水号为1
		Integer serialNo = 1;
		
		//查询当天的下一个流水号
		String date = DateUtils.formatDate(new Date(),"yyyyMMdd");
		String sql ="select max(t.serialno+1) from serialnumber t where t.generatedate = '"+date+"'";
		Integer serialno = jdbcTemplate.queryForObject(sql, Integer.class);
		
		if(serialno!=null) {
			serialNo = serialno;
		}
		
		//将当前序列号保存到数据库
		jdbcTemplate.update("insert into serialnumber (id,serialno,generatedate) values('"+1+"',"+serialNo+",'"+date+"')");
		
		return String.format("%010d", serialNo);
	}
	
	
	/**
	 * 新启事务生成crbc流水号
	 * @author pt-fuyanliang
	 * @throws SQLException 
	 */
	@Override
	public String generateSeqNo() throws SQLException {
		
		lock.lock(); 
		try {
			//删除前一天数据
			jdbcTemplate.update("delete from tfb_seqno_index where date_time = '"+getYesterday()+"'");
			String txDate = jdbcTemplate.queryForObject(sql, String.class).substring(2).trim();
			//当天的初始化流水号为1
			Integer serialNo = 1;
			
			//查询当天的下一个流水号
			String date = DateUtil.format(new Date(), "yyyyMMdd");
			String sql ="select max(t.num+1) from tfb_seqno_index t where t.date_time = '"+date+"'";
			Integer serialno = jdbcTemplate.queryForObject(sql, Integer.class);
			
			if(serialno!=null) {
				serialNo = serialno;
			}
			
			//将当前序列号保存到数据库
			jdbcTemplate.update("insert into tfb_seqno_index (num,date_time) values("+serialNo+",'"+date+"')");
			
			String code = String.format("%010d", serialNo);
			
			flowNo = systemNo+txDate+"00"+code;
			
			log.logInfo("生成流水号[" + flowNo + "]");
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException();
		} finally{
			lock.unlock(); 
		}
		return flowNo;
	}
	
	@Test
	public void testGenerate() {
		String num = generateSerialNum();
		System.out.println(num);
	}
	
}
