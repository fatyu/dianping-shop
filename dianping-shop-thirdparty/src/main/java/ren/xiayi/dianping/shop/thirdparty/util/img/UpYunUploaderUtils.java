package ren.xiayi.dianping.shop.thirdparty.util.img;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import main.java.com.UpYun;
import main.java.com.UpYun.PARAMS;

public class UpYunUploaderUtils {

	public static final String FORM_API_URL_AUTO = "http://v0.api.upyun.com/";
	public static final String IMG_SERVER_URL = "http://img.wangyuhudong.com/";

	// 运行前先设置好以下三个参数
	public static final String BUCKET_NAME = "wymaster";
	private static final String OPERATOR_NAME = "master";
	private static final String OPERATOR_PWD = "miquwy888";
	private static UpYun upyun = new UpYun(BUCKET_NAME, OPERATOR_NAME, OPERATOR_PWD);

	/** 根目录 */
	private static final String DIR_ROOT = "/";

	/**
	 * 上传文件
	 */
	public static boolean uploadImg(File file, String path, String fileName) {
		String filePath = genFilePath(path, fileName);

		// 设置待上传文件的 Content-MD5 值
		// 如果又拍云服务端收到的文件MD5值与用户设置的不一致，将回报 406 NotAcceptable 错误
		try {
			upyun.setContentMD5(UpYun.md5(file));
			// 上传文件，并自动创建父级目录（最多10级）
			boolean result = upyun.writeFile(filePath, file, true);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 上传文件
	 */
	public static String uploadImgBinary(byte[] datas, String path, String fileName) {
		String filePath = genFilePath(path, fileName);

		// 设置待上传文件的 Content-MD5 值
		// 如果又拍云服务端收到的文件MD5值与用户设置的不一致，将回报 406 NotAcceptable 错误
		//upyun.setContentMD5(UpYun.md5(new String(datas)));
		// 上传文件，并自动创建父级目录（最多10级）
		boolean result = upyun.writeFile(filePath, datas, true);
		if (result) {
			return filePath;
		}
		return null;
	}

	/**
	 * 图片做缩略图
	 * <p>
	 * 注意：若使用了缩略图功能，则会丢弃原图
	 * @param size eg:400x400
	 */
	public static boolean thumbImg(File file, String path, String fileName, String size) {
		String filePath = genFilePath(path, fileName);
		// 设置缩略图的参数
		Map<String, String> params = new HashMap<String, String>();
		// 设置缩略图类型，必须搭配缩略图参数值（KEY_VALUE）使用，否则无效
		params.put(PARAMS.KEY_X_GMKERL_TYPE.getValue(), PARAMS.VALUE_FIX_BOTH.getValue());
		// 设置缩略图参数值，必须搭配缩略图类型（KEY_TYPE）使用，否则无效
		params.put(PARAMS.KEY_X_GMKERL_VALUE.getValue(), size);
		// 设置缩略图的质量，默认 95
		params.put(PARAMS.KEY_X_GMKERL_QUALITY.getValue(), "80");
		// 设置缩略图的锐化，默认锐化（true）
		params.put(PARAMS.KEY_X_GMKERL_UNSHARP.getValue(), "true");
		// 若在 upyun 后台配置过缩略图版本号，则可以设置缩略图的版本名称
		// 注意：只有存在缩略图版本名称，才会按照配置参数制作缩略图，否则无效
		params.put(PARAMS.KEY_X_GMKERL_THUMBNAIL.getValue(), "small");
		// 上传文件，并自动创建父级目录（最多10级）
		boolean result;
		try {
			result = upyun.writeFile(filePath, file, true, params);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static String genFilePath(String path, String fileName) {
		String filePath;
		if (StringUtils.isEmpty(path) || StringUtils.equals(DIR_ROOT, path)) {
			filePath = DIR_ROOT + fileName;
		} else {
			// 要传到upyun后的文件路径
			filePath = DIR_ROOT + path + DIR_ROOT + fileName;
		}
		return filePath;
	}

	/**
	 * 替换文件后缀
	 */
	public static String changeExtension(String url, String extension) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		url = StringUtils.substring(url, 0, StringUtils.lastIndexOf(url, ".") + 1) + extension;
		return url;
	}

}
