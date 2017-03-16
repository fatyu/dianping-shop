package ren.xiayi.dianping.shop.config;

public class SystemConfig {

	private String imgServerDomain = "http://img.wangyuhudong.com/";//图片服务地址前缀
	public String environment = "dev";//环境配置

	public String getImgServerDomain() {
		return imgServerDomain;
	}

	public void setImgServerDomain(String imgServerDomain) {
		this.imgServerDomain = imgServerDomain;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

}
