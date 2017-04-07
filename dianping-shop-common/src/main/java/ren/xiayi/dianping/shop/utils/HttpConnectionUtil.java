package ren.xiayi.dianping.shop.utils;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.config.SocketConfig;
import org.apache.http.config.SocketConfig.Builder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;

public class HttpConnectionUtil {
	public final static int MAX_TOTAL_CONNECTIONS = 1000;
	public final static int WAIT_TIMEOUT = 60000;
	public final static int MAX_ROUTE_CONNECTIONS = 500;
	public final static int CONNECT_TIMEOUT = 1000;
	public final static int READ_TIMEOUT = 1000;

	private static SocketConfig socketConfig;
	private static HttpClientConnectionManager connectionManager;
	private static PoolingHttpClientConnectionManager poolConn;

	// 代理服务器信息
	final static String proxyHost = "proxy.abuyun.com";
	final static int proxyPort = 9020;

	// 代理验证信息
	final static String passport = "H3Q4VF69PG1L4G2D";
	final static String key = "495F09979E5D2961";

	// IP切换协议头
	final static String switchIpHeaderKey = "Proxy-Switch-Ip";
	final static String switchIpHeaderVal = "yes";

	private static CredentialsProvider credsProvider = null;

	static {
		Builder customBuilder = SocketConfig.custom();
		customBuilder.setSoKeepAlive(true);
		customBuilder.setSoTimeout(1000);
		customBuilder.setTcpNoDelay(true);
		socketConfig = customBuilder.build();
		poolConn = new PoolingHttpClientConnectionManager();
		poolConn.setMaxTotal(MAX_TOTAL_CONNECTIONS);
		poolConn.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
		poolConn.setDefaultSocketConfig(socketConfig);
		credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
				new UsernamePasswordCredentials(passport, key));
	}

	public static CloseableHttpClient getHttpClient() {
		//		CookieStore cookieStore = new BasicCookieStore();
		//		BasicClientCookie cookie = new BasicClientCookie("Hm_lvt_468da1745644257fb767d26158ded893", "1440581448");
		//		cookie.setDomain("dianping.com");
		//		cookie.setPath("/");
		//		cookieStore.addCookie(cookie);
		Collection<Header> defaultHeaders = new ArrayList<Header>();
		Header header = new BasicHeader(HttpHeaders.USER_AGENT,
				"Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A405 Safari/8536.25");
		defaultHeaders.add(new BasicHeader("Accept", "*//*"));
		//		defaultHeaders.add(new BasicHeader("Host", "dianping.com"));
		defaultHeaders.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
		defaultHeaders.add(new BasicHeader("Connection", "keep-alive"));
		defaultHeaders.add(new BasicHeader("Proxy-Connection", "keep-alive"));
		defaultHeaders.add(new BasicHeader("Accept-Language", "zh-Hans, en-us"));
		defaultHeaders.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
		defaultHeaders.add(header);
		CloseableHttpClient result = HttpClients.custom()//.setDefaultCookieStore(cookieStore)
				.setConnectionManager(connectionManager).setDefaultHeaders(defaultHeaders).build();
		return result;
	}

	public static CloseableHttpClient getProxyHttpClient() {
		Collection<Header> defaultHeaders = new ArrayList<Header>();
		Header header = new BasicHeader(HttpHeaders.USER_AGENT,
				"Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A405 Safari/8536.25");
		defaultHeaders.add(new BasicHeader("Accept", "*//*"));
		//		defaultHeaders.add(new BasicHeader("Host", "dianping.com"));
		defaultHeaders.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
		defaultHeaders.add(new BasicHeader("Connection", "keep-alive"));
		defaultHeaders.add(new BasicHeader("Proxy-Connection", "keep-alive"));
		defaultHeaders.add(new BasicHeader("Accept-Language", "zh-Hans, en-us"));
		defaultHeaders.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
		defaultHeaders.add(new BasicHeader(switchIpHeaderKey, switchIpHeaderVal));
		defaultHeaders.add(header);
		CloseableHttpClient result = HttpClients.custom()//.setDefaultCookieStore(cookieStore)
				.setConnectionManager(poolConn).setDefaultHeaders(defaultHeaders)
				.setProxy(new HttpHost(proxyHost, proxyPort)).setDefaultCredentialsProvider(credsProvider).build();
		return result;
	}

}