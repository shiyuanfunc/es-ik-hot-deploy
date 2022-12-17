package org.wltea.analyzer.dic;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import java.util.List;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.SpecialPermission;
import org.wltea.analyzer.help.DbHelper;
import org.wltea.analyzer.help.ESPluginLoggerFactory;

public class Monitor implements Runnable {

	private static final Logger logger = ESPluginLoggerFactory.getLogger(Monitor.class.getName());

	private static CloseableHttpClient httpclient = HttpClients.createDefault();
	/*
	 * 上次更改时间
	 */
	private String last_modified;
	/*
	 * 资源属性
	 */
	private String eTags;

	/*
	 * 请求地址
	 */
	private String location;

	public Monitor(String location) {
		this.location = location;
		this.last_modified = null;
		this.eTags = null;
	}

	public void run() {
		SpecialPermission.check();
		AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
			logger.info("执行获取热词部署 {}", new Date());
			this.runUnprivileged();
			return null;
		});
	}

	/**
	 * 监控流程：
	 *  ①向词库服务器发送Head请求
	 *  ②从响应中获取Last-Modify、ETags字段值，判断是否变化
	 *  ③如果未变化，休眠1min，返回第①步
	 * 	④如果有变化，重新加载词典
	 *  ⑤休眠1min，返回第①步
	 */

	public void runUnprivileged() {


		List<String> hotWords = DbHelper.getHotWords();
		if (!hotWords.isEmpty()){
			Dictionary.getSingleton().addWords(hotWords);
		}
	}

}
