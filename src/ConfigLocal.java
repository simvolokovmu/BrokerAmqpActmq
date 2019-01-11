import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class ConfigLocal 
{
	public static final String LOG_PROPERTIES_FILE = "./Log4J.properties";
	public static final String CFG_PROPERTIES_FILE = "./config.properties";

	public static final String dirInboxFromRabb_ = "./InboxFromRabb";
	public static final String dirStoreFromRabb_ = "./StoreFromRabb";
	public static final String dirTroubleFromRabb_ = "./TroubleFromRabb";

	public static SimpleDateFormat timeStampRptFormat_ = new SimpleDateFormat("dd.MM");
	public static SimpleDateFormat timeStampSaveImgFormat_ = new SimpleDateFormat("YYYYMMdd_hhmmss");

	public static String amqpHost_;
	public static String amqpPort_;
	public static String amqpUser_;
	public static String amqpPwd_;
	public static String amqpQueueName_;

	public static String rabbitHost_;
	public static Integer rabbitPort_;
	public static String rabbitUser_;
	public static String rabbitPwd_;
	public static Integer rabbitConnTimeoutSek_;
	public static String rabbitQueueName_;
	
	public static Integer nTimerSeconds_;
	
	public String GetPropertyCtrl (Properties prop, String key) throws Exception
	{
		String sVal = prop.getProperty(key).trim();
		if(sVal == null)
			throw new Exception ("Property not found : " + key);
		return sVal;
	}

	public static String encodeUnicodeEscapesSingle(String s) 
	{
		int len= s.length();
		StringBuffer sb= new StringBuffer(len);
		for (int k=0; k<len; k++) 
		{
			char c= s.charAt(k);
			if (c=='\\' && k+6<=len && s.charAt(k+1)=='u') 
			{
				sb.append("\\uu"); 
				k++;
			} else if (c>=128) {
				sb.append("\\u" + StringUtils.leftPad(Integer.toHexString(c).toUpperCase(), 4, '0'));
			} else {
				sb.append(c);
			}
		}
	   return sb.toString();
	}
	
	public void Load(String pathFile) throws Exception
	{
		Properties prop = new Properties();
		InputStream in = null;
		in = new FileInputStream(pathFile); 

		try {
			prop.load(in);
			in.close(); 

			amqpHost_ = GetPropertyCtrl(prop, "amqpHost");
			amqpPort_ = GetPropertyCtrl(prop, "amqpPort");
			amqpUser_ = GetPropertyCtrl(prop, "amqpUser");
			amqpPwd_ = GetPropertyCtrl(prop, "amqpPwd");
			amqpQueueName_ = GetPropertyCtrl(prop, "amqpQueueName");

			rabbitHost_ = GetPropertyCtrl(prop, "rabbitHost");
			rabbitPort_ = Integer.parseInt(GetPropertyCtrl(prop, "rabbitPort"));
			rabbitUser_ = GetPropertyCtrl(prop, "rabbitUser");
			rabbitPwd_ = GetPropertyCtrl(prop, "rabbitPwd");
			rabbitConnTimeoutSek_ = Integer.parseInt(GetPropertyCtrl(prop, "rabbitConnTimeoutSek"));
			rabbitQueueName_ = GetPropertyCtrl(prop, "rabbitQueueName");

			nTimerSeconds_ = Integer.parseInt(GetPropertyCtrl(prop, "nTimerSeconds"));
		
		} catch (Exception e) {
			if(in != null)
			    in.close(); 
			throw new Exception ("Config file has errors: " + pathFile + " Errors: " + e.getMessage());
		}
		
	}
}
