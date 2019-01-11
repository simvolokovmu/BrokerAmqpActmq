import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class Utils 
{
	public static String getUtf8(String src)
	{
/*		
		try {
			if(src == null || src.isEmpty())
				return src;
			return new String (src.getBytes("UTF-8"), StandardCharsets.UTF_8);
		}catch(Exception e)
		{}
*/		
		return src;
	}
	
	public static String getAnswerForRequest(String myURL, String userName, String password)
	{
		String sRptRes = null;
		try {
			URL url = new URL(myURL);
			String nullFragment = null;
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(uri); // myURL);
//			httpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(ConfigLocal.userName_, ConfigLocal.password_), "UTF-8", false));
			httpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(userName, password), "UTF-8", false));
		
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			
			sRptRes = EntityUtils.toString(entity);
		} catch(Exception e)
		{
			Core.log_.info ("Error for request: " + myURL);
			Core.log_.error(e);
		}
		return sRptRes;
	}

	public static String getAnswerForRequestOAuth(String myURL, String oauth)
	{
		String sRptRes = null;
		try {
			URL url = new URL(myURL);
			String nullFragment = null;
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(uri); // myURL);
//			httpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(ConfigLocal.userName_, ConfigLocal.password_), "UTF-8", false));
			httpGet.addHeader("Accept", "application/json");
			httpGet.addHeader("Content-Type", "application/json");
			httpGet.addHeader("Authorization", ""); 
		
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			
			sRptRes = EntityUtils.toString(entity);
		} catch(Exception e)
		{
			Core.log_.info ("Error for request: " + myURL);
			Core.log_.error(e);
		}
		return sRptRes;
	}
/*	
	public static String SaveJsonToStore(String json, String sFNPrefix) throws IOException
	{
		byte[] utf8 = json.getBytes("UTF-8"); 
		
		String sFN = sFNPrefix + (new Long(System.currentTimeMillis()).toString()) + ".json";
		String sPFjson = ConfigLocal.dirStore_ + '/' + sFN;
		FileUtils.writeByteArrayToFile(new File(sPFjson), utf8);
		return sPFjson;
	}
*/
	public static String getFormatString(String str, int nFmt, boolean addSpaceToBegining)
	{
		String sDummy = "                    ";
		if(nFmt <= str.length() || nFmt >= sDummy.length())
			return str;
		
		if(addSpaceToBegining)
			return sDummy.substring(0, nFmt - str.length()) + str;
		
		return str + sDummy.substring(0, nFmt - str.length());
	}
	
	public static void SaveUrl2File(String sPFUrl, String sPFForSave) throws IOException
	{
/*
		Path path = Paths.get(sPFUrl);
		byte[] data = Files.readAllBytes(path);
		FileUtils.writeByteArrayToFile(new File(sPFForSave), data);
*/
		File file = new File(sPFForSave);
    	FileUtils.copyURLToFile(new URL(sPFUrl), file);
	}
	
	public static String getJSonValue(String src, String sKey)
	{
		String sTag = (String)"\"" + sKey + "\":";
		int nIndexStart = src.indexOf(sTag);
		if(nIndexStart == -1)
			return "";
		String sAfter = src.substring(nIndexStart + sTag.length()); // 2 is ":
		
		int nIndexEnd = sAfter.indexOf(",");
		if(nIndexEnd == -1)
			return "";
		String sRes = sAfter.substring(0, nIndexEnd);
//		Core.log_.info(sKey + " : " + sRes);

		return sRes;
	}	

	public static void save2File(String sPFSave, String sContent) throws IOException
	{
		byte[] utf8 = sContent.getBytes("UTF-8"); 
		FileUtils.writeByteArrayToFile(new File(sPFSave), utf8);
	}

	public static String readFileAsB64(String sPF) throws IOException
	{
		return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(new File(sPF)));
	}

}
