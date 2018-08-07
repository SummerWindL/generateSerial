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
	
	@Test
	public void testGenerate() {
		String num = generateSerialNum();
		System.out.println(num);
	}
	
}
