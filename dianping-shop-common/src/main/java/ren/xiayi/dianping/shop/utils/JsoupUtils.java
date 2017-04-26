package ren.xiayi.dianping.shop.utils;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * jsoup相关处理方法
 * 备注:代理使用的阿布云代理
 * @author fatyu
 *
 */
public class JsoupUtils {

	private JsoupUtils() {
		super();
	}

	// 代理隧道验证信息
	final static String ProxyUser = "H3Q4VF69PG1L4G2D";
	final static String ProxyPass = "495F09979E5D2961";

	// 代理服务器
	final static String ProxyHost = "proxy.abuyun.com";
	final static Integer ProxyPort = 9020;

	public static Proxy getProxy() {
		Authenticator.setDefault(new Authenticator() {
			@Override
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(ProxyUser, ProxyPass.toCharArray());
			}
		});

		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ProxyHost, ProxyPort));
		return proxy;
	}

	/**
	 * @param url 请求地址
	 * @param timeout 超时时间 单位毫秒
	 * @param host 主机域名
	 * @return
	 */
	public static Connection getDirectConnection(String url, int timeout, String host) {
		Connection connect = Jsoup.connect(url);
		Map<String, String> header = new HashMap<String, String>();
		header.put("Host", host);
		header.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
		header.put("Accept", "	text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		header.put("Accept-Language", "zh-cn,zh;q=0.5");
		header.put("Accept-Charset", "en,zh-CN;q=0.8,zh;q=0.6");
		header.put("Connection", "keep-alive");
		connect = connect.data(header);
		return connect.timeout(timeout);
	}

	/**
	 * @param url 请求地址
	 * @param timeout 超时时间 单位毫秒
	 * @param host 主机域名
	 * @return
	 */
	public static Connection getProxyConnection(String url, int timeout, String host) {
		Connection connect = Jsoup.connect(url);
		Map<String, String> header = new HashMap<String, String>();
		header.put("Host", host);
		header.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
		header.put("Accept", "	text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		header.put("Accept-Language", "zh-cn,zh;q=0.5");
		header.put("Accept-Charset", "en,zh-CN;q=0.8,zh;q=0.6");
		header.put("Connection", "keep-alive");
		connect.proxy(getProxy());
		connect = connect.data(header);
		connect = connect.timeout(timeout);
		return connect;
	}
}
