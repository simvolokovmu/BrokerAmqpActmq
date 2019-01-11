/*
package com.bdr.common;

import com.bdr.netty.httpserver.AHttpServerGetRequest;
import com.bdr.netty.httpserver.AHttpServerResponse;
import com.bdr.tsnet.admin.service.api.ServiceData;
import com.bdr.tsnet.model.entity.PersistentObject;
import com.bdr.tsnet.model.entity.Setting;
import com.bdr.tsnet.model.entity.Teaser;
import com.bdr.tsnet.model.entity.WebMaster;
import com.bdr.tsnet.model.entity.enums.PaymentOption;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.mahout.math.list.IntArrayList;
import org.apache.mahout.math.map.OpenIntDoubleHashMap;
import org.apache.mahout.math.map.OpenIntIntHashMap;
import org.apache.mahout.math.map.OpenIntObjectHashMap;
import org.apache.mahout.math.map.OpenLongObjectHashMap;
import org.apache.mahout.math.set.OpenIntHashSet;
import org.codehaus.jackson.JsonNode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
*/
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * User: AKovylin
 * Date: 09.03.2010
 * Time: 15:51:26
 */
public class Util
{
	private static boolean bPrintLog_ = false;
	
	public static final int MAX_HAZEL_QUEUES = 0;						// KSS 2013/04/29
	public static final int MAX_HAZEL_QUEUE_THREADS = 0;						// KSS 2013/04/29
//	public static final int SAVE_RETARGET_FLOWS_DELAY;				// KSS 2013/04/29
/*
	// KSS 2013/04/29
	static
		{
			int tMAX_HAZEL_QUEUES;
			int tMAX_HAZEL_QUEUE_THREADS;
//			int tSAVE_RETARGET_FLOWS_DELAY;
		
			Properties tmp = new Properties();
			try
				{
					InputStream stream = new FileInputStream(System.getProperty("user.dir") + "/classes/cfg/nserver.properties");
				try
					{
						tmp.load(stream);
						String sTemp = tmp.getProperty("max_hazel_queues");
						tMAX_HAZEL_QUEUES = sTemp == null ? -1 : Integer.parseInt(sTemp);
						
//						sTemp = tmp.getProperty("save_retarget_flows_delay");
//						tSAVE_RETARGET_FLOWS_DELAY = sTemp == null ? -1 : Integer.parseInt(sTemp);
						sTemp = tmp.getProperty("max_hazel_queue_threads");
						tMAX_HAZEL_QUEUE_THREADS = sTemp == null ? -1 : Integer.parseInt(sTemp);
						
					} finally {
						stream.close();
					}
				} catch (IOException ex) {
						System.out.println("initialization is failed. " + "Exception is: " + ex.getMessage()+ ". Max queue threads value is set to 10, Delay of saving retarget flows is set to 1.");
						tMAX_HAZEL_QUEUES = 10;
						tMAX_HAZEL_QUEUE_THREADS = 10;
//						tSAVE_RETARGET_FLOWS_DELAY = 1;
			}
			
			MAX_HAZEL_QUEUES = tMAX_HAZEL_QUEUES;
			MAX_HAZEL_QUEUE_THREADS = tMAX_HAZEL_QUEUE_THREADS;
//			SAVE_RETARGET_FLOWS_DELAY = tSAVE_RETARGET_FLOWS_DELAY;
		}
*/
	
    public static final int INDEX_OF_MARK = 0;						// KSS 2013/04/29
    public static final int INDEX_OF_MARKPOSTFIX = 1;				// KSS 2013/04/29
    public static final int INDEX_OF_VALUE = 2;						// KSS 2013/04/29
	
    public static final String MARK_MAP = "map";					// KSS 2013/04/29
    public static final String MARK_QUEUE = "queue";				// KSS 2013/04/29
    public static final String MARKPOSTFIX_REFERER = "refer";		// KSS 2013/04/29
    public static final String MARKPOSTFIX_MATCHING = "match";		// KSS 2013/04/29
	
	public static void setPrintLog(boolean bPrintLog)	{		bPrintLog = bPrintLog_;	}
	public static boolean getPrintLog()				{		return bPrintLog_;	}
/*	
	public static void logInfo(Logger log, Object... args) // SMU 2012/09/17
	{
        if (!bPrintLog_ || args == null || args.length == 0)
            return ;
        
        String sLogLine = new String();
        for (Object obj : args)
        	if (obj != null)
        		sLogLine += obj.toString();
        
        if (log != null && !sLogLine.isEmpty())
        	log.info(sLogLine);
	}
*/
	
	public static String GetDateFormatted(long msec, String sFormat, String sLocale)
	{
		Date date = new Date(msec);
		DateFormat formatter = new SimpleDateFormat(sFormat, new Locale(sLocale)); // Mo/Tu/We/Th/Fr/Sa/Su for English
		return formatter.format(date);
	}

	public static Date ParseDateFormatted(String sValueDateTime, String sFormat, String sLocale) throws Exception
	{
//		String target = "Thu Sep 28 20:29:30 JST 2000";
//	    DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", new Locale(sLocale));
	    DateFormat df = new SimpleDateFormat(sFormat, new Locale(sLocale));
	    return df.parse(sValueDateTime); 
	}
	
	public static final byte[] longToBytes(long v) // SMU 2012/09/17 
	{
	    byte[] longBuffer = new byte[ 8 ];

	    longBuffer[0] = (byte)(v >>> 56);
	    longBuffer[1] = (byte)(v >>> 48);
	    longBuffer[2] = (byte)(v >>> 40);
	    longBuffer[3] = (byte)(v >>> 32);
	    longBuffer[4] = (byte)(v >>> 24);
	    longBuffer[5] = (byte)(v >>> 16);
	    longBuffer[6] = (byte)(v >>>  8);
	    longBuffer[7] = (byte)(v >>>  0);

	    return longBuffer;
	}	
	
	public static String priceConvertLoop(String src) // OII 2014/04/25
	{
		String result = "";
		  
		char[] chars = src.toCharArray();
		Deque<Character> tmpChars = new LinkedList<Character>();
		StringBuilder cents = new StringBuilder();
		StringBuilder dollars = new StringBuilder();
		Boolean centsFinded = false;
		Boolean flag = false;

		int length = chars.length;
		for (int i=length-1;i>=0;i--) 
		{
			if (Character.isDigit(chars[i])) 
			{
				tmpChars.addFirst(chars[i]);
			    flag=true;
			} else if ((!centsFinded) && flag && (tmpChars.size()<=2)) 
			{
				cents = new StringBuilder(tmpChars.size());
				for (Character el : tmpChars) 
				{
					cents.append(el);
				}
		    
				tmpChars.clear();
				centsFinded = true;
			}
		} /* end for */
		
		dollars = new StringBuilder(tmpChars.size());
		for (Character el : tmpChars) 
		{
			dollars.append(el);
		}
		
		if (dollars.length()>0) 
			result = dollars.toString()+"."+cents.toString();
		  
		return result;
	}
	 
	public static String priceConvert(String src) // OIA 2013/04/25
	{
		return src;
		/*
		// replace $ symbol
		src = src.replaceAll("\\$", "\\\\\\$"); 
		src = src.replaceAll("\\\\\\$", "dollars"); 
		  
		String result = "";
		  
		Pattern realPtrn = Pattern.compile("(?<real>\\D+\\d{1,2}(\\D+|$))"); // cents
		//Pattern intPtrn =Pattern.compile("(?<int>[\\d][0-9\\s\\D]+\\d{3})"); // dollars
		  
		Matcher m1 = realPtrn.matcher(src); 
		//Matcher m2 = intPtrn.matcher(src); 
		String cents ="";
		String dollars ="";
		  
		if (m1.find()) 
		{
			// cents finded 
			cents = m1.group("real");
			dollars = src.replaceAll(cents, ""); // remove cents from source string
			cents = cents.replaceAll("[^0-9]*", ""); 
			dollars = dollars.replaceAll("[^0-9]*", "");
			result = dollars + "." + cents; 
		} else {
			// in src only dollars
			dollars = src.replaceAll("[^0-9]*", "");
			result = dollars + "." + cents; 
		}
		return result;
		*/
	}
// SMU 2012/07/06
	public static String toTimeFormatMMSS(int nAllSek)
	{
		if (nAllSek <= 0)
			return "00:00";

		int nMin = nAllSek / 60;
		if (nMin >= 100)
			nMin = 99;
		int nSek = nAllSek % 60;
		return new String(nMin < 10 ? "0" : "") + Integer.toString(nMin) + ':' +
			(nSek < 10 ? "0" : "") + Integer.toString(nSek);
	}
	
	public static String getGUID() // SMU 2012/08/24
	{
		return java.util.UUID.randomUUID().toString();
	}

	public static ArrayList<String> getXmlListObjects (String sSrcXml, String sTag) // SMU 2013/08/02
	{
		if(sSrcXml == null || sSrcXml.isEmpty() || sTag == null || sTag.isEmpty())
			return null;
		
		ArrayList<String> res = new ArrayList<String>();
		String[] arrItems = sSrcXml.split("<" + sTag + ">");
		String sTagClose = "</" + sTag + ">";
		for(int i = 0; i < arrItems.length; i++)
		{
			String item = Util.getStrBefore(arrItems[i], sTagClose);
			if(item != null && !item.isEmpty())
				res.add(item);
		}
		return res.isEmpty() ? null : res;
	}

	public static String formStrForTag (String sTag, String sValue)
	{
		return "<" + sTag + ">" + sValue + "</" + sTag + ">";
	}
	
	public static String getStrForTag (String sSrc, String sTag)
	{
		return getStrBetween (sSrc, "<" + sTag + ">", "</" + sTag + ">");
	}

// SMU 23/04/2012
	public static String getStrBetween (String sSrc, String sPrefix, String sPostfix)
    {
    	if (sSrc == null || sPrefix == null || sPostfix == null)
    		return null;
    	int nStart = sSrc.indexOf(sPrefix);
    	if (nStart == -1)
    		return null;
    	nStart += sPrefix.length();
    	int nFinish = sSrc.indexOf(sPostfix, nStart);
    	if (nFinish == -1)
    		return null;
    	return sSrc.substring(nStart, nFinish);
    }

	public static String getStrAfter (String sSrc, String sPrefix)
    {
    	if (sSrc == null || sPrefix == null)
    		return null;
    	int nStart = sSrc.indexOf(sPrefix);
    	if (nStart == -1)
    		return null;
    	return sSrc.substring(nStart + sPrefix.length());
    }
	
	public static String getStrBefore (String sSrc, String sPostfix)
    {
    	if (sSrc == null || sPostfix == null)
    		return null;
    	int nFinish = sSrc.indexOf(sPostfix);
    	if (nFinish == -1)
    		return null;
    	return sSrc.substring(0, nFinish);
    }

	public static boolean isHavePrefix (String sSrc, String sPrefix) // SMU 2012/07/10
    {
    	if (sSrc == null || sPrefix == null || sPrefix.length() == 0)
    		return false;
    	return sSrc.indexOf(sPrefix) == 0;
    }

	public static boolean isHavePostfix (String sSrc, String sPostfix) // SMU 2012/07/10
    {
    	if (sSrc == null || sPostfix == null || sPostfix.length() == 0 || sSrc.length() < sPostfix.length())
    		return false;
    	return sSrc.indexOf(sPostfix, sSrc.length() - sPostfix.length()) == sSrc.length() - sPostfix.length();
    }
	
	public static int[] parseVersion(String ver)
	{
    	if(ver == null || ver.isEmpty())
    		return null;
    	
		String[] versions = ver.split("\\.");
		int[] ret = new int[]{0,0};
		try {
	    	if(versions.length >= 1 && versions[0] != null)
	    		ret[0] = Integer.parseInt(versions[0]);
	    	if(versions.length >= 2 && versions[1] != null)
		    	ret[1] = Integer.parseInt(versions[1]);
		} catch(Exception e)
		{
			return null;
		}
		return ret;
	}

    public static Map<String, String> getMapUrlParameters(String url)
    {
		Map<String, String> map = new TreeMap<String, String>();
    	try {
    		String sDecode = URLDecoder.decode(url, "UTF-8");
    		String URI = Util.getStrAfter(sDecode, "?");
			if(URI != null)
	    		for(String str : URI.split("&"))
	    		{
	    			String[] split = str.split("=");
	    			if(split.length >= 2)
	    				map.put(split[0],  split[1]);
	    			
	    		}
    	} catch(Exception e)
    	{
    		String s = e.toString(); // for debug
    	}
    	return map;
    }
    
	public static Integer getIntPrefix(String src, String separator)
	{
    	int index = src.indexOf(separator);
    	if(index > 0)
	    	try{
	    		return Integer.parseInt(src.substring(0, index));
	    	}catch(Exception e){}
		return null;
	}

	public static String getDomain(String sDomainSrc)
    {
    	if(sDomainSrc == null || sDomainSrc.isEmpty())
    		return "";
    	String sDomain = new String(sDomainSrc);
    	int nStart = -1;
        if ((nStart = sDomain.indexOf("//")) >= 0) // http:// or https:// or others
        	sDomain = sDomain.substring(nStart + 2);

        if (sDomain.indexOf("www.") == 0)
        	sDomain = sDomain.substring(4); // SMU domain is string after www.
        int nTemp = -1;
        if ((nTemp = sDomain.indexOf("/")) > 0)
        	sDomain = sDomain.substring(0, nTemp);
        if ((nTemp = sDomain.indexOf(":")) > 0) // SMU del port
        	sDomain = sDomain.substring(0, nTemp);

        return sDomain;
    }
	
	public static String getDomainSecondLevel(String dom)
	{
		if(dom == null || dom.isEmpty())
			return null;
		
    	String[] arrDom = dom.split("\\.");
    	if(arrDom.length == 1) // SMU 2013/04/13
    		return dom;
    	if(arrDom.length < 2)
    		return null;
    		
    	return arrDom[arrDom.length - 2] + "." + arrDom[arrDom.length - 1];
	}

	public static boolean isEquivDomainTwoLevel(String dom1, String dom2)
    {
    	dom1 = getDomain(dom1);
    	dom2 = getDomain(dom2);
    	if(dom1.isEmpty() || dom2.isEmpty())
    		return false;
    	
    	String[] arrDom1 = dom1.split("\\.");
    	String[] arrDom2 = dom2.split("\\.");
    	if(arrDom1.length < 2 || arrDom2.length < 2)
    		return false;
    		
    	return arrDom1[arrDom1.length - 1].equalsIgnoreCase(arrDom2[arrDom2.length - 1]) && // first domain level
    			arrDom1[arrDom1.length - 2].equalsIgnoreCase(arrDom2[arrDom2.length - 2]); // second domain level
    }
    
	public static String toHex(byte md5[])
    {
        StringBuilder result = new StringBuilder(md5.length);
        char hexChar[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'
        };
        for (int i = 0; i < md5.length; i++)
            result.append(hexChar[md5[i] >> 4 & 15]).append(hexChar[md5[i] & 15]);

        return result.toString();
    }

    public static long hex2Long(String hexadecimal)
    {
        char[] chars;
        char c;
        long value = 0;
        int i;
        byte b;

        try {
        	
        	if (hexadecimal == null)
                throw new Exception("hexadecimal is null");

			chars = hexadecimal.toUpperCase().toCharArray();
			if (chars.length != 16)
			    throw new Exception("Incomplete hex value");

			value = 0;
			b = 0;
			for (i = 0; i < 16; i++)
			{
				c = chars[i];
				if (c >= '0' && c <= '9') 
					value = ((value << 4) | (0xff & (c - '0')));
			  	else if (c >= 'A' && c <= 'F')
			  		value = ((value << 4) | (0xff & (c - 'A' + 10)));
			  	else
			  		throw new Exception("Invalid hex character: " + c);
			}
        } catch (Exception e)
        {
        	return 0;
        }
        return value;
    }
/*    	
	public static final long bytesToLong(String sLong) // SMU 2012/09/17 
	{
		try {
			return Long.parseLong(sLong, 16);
		} catch(Exception e){}
		return 0;
	}
*/

    public static byte[] xorWithKey(final byte[] src, final byte[] key) // SMU 2012/10/15
    {
    	byte[] dst = new byte[src.length];
    	for(int i = 0; i < src.length; i++)
    		dst[i] = (byte)(src[i] ^ key[i % key.length]);
    	return dst;
    }
    /*
    public static byte[] gzipCompress(byte[] src) // SMU 2012/10/15
    {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try{
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(src);
            gzipOutputStream.close();
        } catch(IOException e){
            throw new RuntimeException(e);
        }
//            System.out.printf("Compression ratio %f\n", (1.0f * src.length/byteArrayOutputStream.size()));
        return byteArrayOutputStream.toByteArray();
    }
    
    public static byte[] gzipDeCompress(byte[] src) // SMU 2012/10/15
    {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        try{
        	IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(src)), out);
        } catch(IOException e){
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }
    */
    
    /*
    public static byte[] gzipDeflaterCompress(byte[] src) throws IOException // SMU 2012/10/15
    {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    	DeflaterOutputStream gzipOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
        gzipOutputStream.write(src);
        gzipOutputStream.flush();
        gzipOutputStream.close();

//            System.out.printf("Compression ratio %f\n", (1.0f * src.length/byteArrayOutputStream.size()));
        return byteArrayOutputStream.toByteArray();
    }
    
    public static byte[] gzipDeflaterDeCompress(byte[] src) throws IOException // SMU 2012/10/15
    {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(new InflaterInputStream(new ByteArrayInputStream(src)), out);
//            IOUtils.copy(new DeflaterInputStream(new ByteArrayInputStream(src), ), out);
//          IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(src)), out);
        return out.toByteArray();
    }
    */
    
    /**
     * Compresses a zlib compressed file.
     */
    public static byte[] compressData(byte[] src)
            throws IOException
    {
    	InputStream in = new ByteArrayInputStream(src);
        ByteArrayOutputStream btOut = new ByteArrayOutputStream();
        OutputStream out = new DeflaterOutputStream(btOut);
        
        shovelInToOut(in, out);
        in.close();
        out.close();
        return btOut.toByteArray();
    }

	/**
	 * Decompresses a zlib compressed file.
	 */
	public static byte[] decompressData(byte[] gzip) throws IOException
	{
	    InputStream in = new InflaterInputStream(new ByteArrayInputStream(gzip));
	    ByteArrayOutputStream btOut = new ByteArrayOutputStream();
	
		shovelInToOut(in, btOut);
		in.close();
		btOut.close();           
	       
		return btOut.toByteArray();
	}
        
	/**
	 * Shovels all data from an input stream to an output stream.
	 */
	private static void shovelInToOut(InputStream in, OutputStream out)
	    throws IOException
	{
	    byte[] buffer = new byte[1000];
	    int len;
	    while((len = in.read(buffer)) > 0)
	        out.write(buffer, 0, len);
	}

/*
	public static void logPrint(Logger log, Object... args) // SMU 2012/09/17
	{
        if (!bPrintLog_ || args == null || args.length == 0)
            return ;
        
        String sLogLine = new String();
        for (Object obj : args)
        	if (obj != null)
        		sLogLine += obj.toString();
        
        if (log != null && !sLogLine.isEmpty())
        	log.info(sLogLine);
	}
*/
	
    public static String md5(String from)
    {
        try
        {
            return toHex(MessageDigest.getInstance("MD5").digest(from.getBytes()));
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public static byte[] fromHex(String hex)
    {
        byte buf[] = new byte[hex.length() / 2];
        for (int i = 0; i < buf.length; i++)
            buf[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);

        return buf;
    }

    public static String buildIdsList(List<Integer> rolesIds)
    {
        StringBuilder sb = new StringBuilder();
        for (Integer i : rolesIds)
            sb.append(i).append(',');
        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Default buffer size for use in
     * {@link #copy(java.io.InputStream , java.io.OutputStream , boolean)}.
     */
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    public static long copy(InputStream in, OutputStream out, boolean close) throws IOException
    {
        return copy(in, out, close, new byte[DEFAULT_BUFFER_SIZE]);
    }

    public static long copy(InputStream in, OutputStream out, boolean close, byte[] buffer) throws IOException
    {
        try
        {
            long total = 0;
            for (; ;)
            {
                int res = in.read(buffer);
                if (res == -1)
                {
                    break;
                }
                if (res > 0)
                {
                    total += res;
                    if (out != null)
                    {
                        out.write(buffer, 0, res);
                    }
                }
            }
            if (out != null)
            {
                if (close)
                {
                    out.close();
                } else
                {
                    out.flush();
                }
                out = null;
            }
            in.close();
            in = null;
            return total;
        } finally
        {
            if (close && in != null)
            {
                try
                {
                    in.close();
                } catch (Throwable t)
                {
                    /* Ignore me */
                }
            }
            if (close && out != null)
            {
                try
                {
                    out.close();
                } catch (Throwable t)
                {
                    /* Ignore me */
                }
            }
        }
    }

    /**
     * This convenience method allows to read a
     * content into a string. The platform's default character encoding
     * is used for converting bytes into characters.
     *
     * @param pStream The input stream to read.
     * @return The streams contents, as a string.
     * @throws IOException An I/O error occurred.
     * @see #asString(InputStream, String)
     */
    public static String asString(InputStream pStream) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(pStream, baos, true);
        return baos.toString();
    }

    /**
     * This convenience method allows to read a
     * content into a string, using the given character encoding.
     *
     * @param pStream   The input stream to read.
     * @param pEncoding The character encoding, typically "UTF-8".
     * @return The streams contents, as a string.
     * @throws IOException An I/O error occurred.
     * @see #asString(InputStream)
     */
    public static String asString(InputStream pStream, String pEncoding)
            throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(pStream, baos, true);
        return baos.toString(pEncoding);
    }

    private static char[] DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static char[][] HEX;

    static
    {
        HEX = new char[256][];
        for (int i = 0; i <= 255; i++)
        {
            HEX[i] = new char[]{
                    DIGITS[(i >> 4) & 0x0f],
                    DIGITS[i & 0x0f]
            };
        }
    }

    public static String bytesToHex(byte[] data)
    {
        if (data == null)
        {
            return null;
        } else
        {
            int len = data.length;
            StringBuilder str = new StringBuilder(len);
            for (int i = 0; i < len; i++)
            {
                str.append(HEX[data[i] & 0xFF]);
            }
            return str.toString();
        }
    }

    public static String formatAmount(Integer amount)
    {
        if (amount == null)
            return "";
        String s1 = String.valueOf(amount / 100);
        String s2 = String.valueOf(Math.abs(amount % 100));
        while (s2.length() < 2)
            s2 = "0" + s2;
        return s1 + "." + s2;
    }

/*    public static String escapeHtml(String s)
    {
        if (s == null) return s;
        if (s.indexOf('&') != -1) s = s.replace("&", "&amp;");
        if (s.indexOf('\'') != -1) s = s.replace("\'", "&#39;");
        if (s.indexOf('"') != -1) s = s.replace("\"", "&#34;");
        if (s.indexOf('<') != -1) s = s.replace("<", "&lt;");
        if (s.indexOf('>') != -1) s = s.replace(">", "&gt;");
        return s;
    }*/

    public static String toString(Map parameterMap)
    {
        StringBuilder sb = new StringBuilder(1024);
        sb.append('(');
        if (parameterMap != null)
            for (Object o : parameterMap.keySet())
            {
                if (sb.length() > 1)
                    sb.append(',');
                sb.append(o).append('=');
                Object value = parameterMap.get(o);
                if (value instanceof Object[])
                    sb.append(Arrays.toString((Object[]) value));
                else
                    sb.append(value);
            }
        sb.append(')');
        return sb.toString();
    }

    public static Map<String, String> getParamsFromURL(String url, boolean sorted) throws UnsupportedEncodingException
    {
        int i = url.indexOf('?');
        String queryStr = i == -1 ? url : url.substring(i + 1);
        String[] pairs = queryStr.split("&");
        Map<String, String> res = sorted ? new TreeMap<String, String>() : new HashMap<String, String>(30, 0.75f);
        for (String p : pairs)
        {
            i = p.indexOf('=');
            res.put(p.substring(0, i), URLDecoder.decode(p.substring(i + 1), "UTF-8"));
        }
        return res;
    }
/*
    static public void respond(String msg, AHttpServerGetRequest request)
    {
        final AHttpServerResponse resp = new AHttpServerResponse(request.getHttpVersion(), HttpResponseStatus.OK);
        resp.setContentString(msg, "text/plain", "ISO-8859-1");
        request.sendResponse(resp);
    }

    static public OpenIntObjectHashMap intMap(int sz)
    {
        return new OpenIntObjectHashMap(sz, 0.33f, 0.75f);
    }

    static public OpenIntObjectHashMap intMap()
    {
        return intMap(4);
    }

    static public OpenLongObjectHashMap longMap(int sz)
    {
        return new OpenLongObjectHashMap(sz, 0.33f, 0.75f);
    }

    static public OpenLongObjectHashMap longMap()
    {
        return longMap(4);
    }

    static public OpenIntDoubleHashMap intDoubleMap(int sz)
    {
        return new OpenIntDoubleHashMap(sz, 0.33f, 0.75f);
    }

    static public OpenIntIntHashMap intIntMap(int sz)
    {
        return new OpenIntIntHashMap(sz, 0.33f, 0.75f);
    }

    private static final Logger l = Logger.getLogger("TST");

    static public <T extends PersistentObject> T fromId(Class<T> cl, int id)
    {
        Util.logInfo(l,"fromId " + id + " start " + cl.getName());
        try
        {
            final T res = cl.newInstance();
            res.setId(id);
            Util.logInfo(l,"fromId regular exit");
            return res;
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        Util.logInfo(l,"fromId error exit");
        return null;
    }

    public static Query transQuery(Session s, String qTxt)
    {
        return s.createQuery(qTxt).setReadOnly(true);
    }

    public static double bid(PaymentOption po, double show, double click)
    {
        return po == PaymentOption.PER_1K_SHOW ? show : click;
    }

    public static double bid(Teaser t)
    {
        return bid(t, t.getCampaign().getPaymentOption());
    }

    public static double bid(Teaser t, PaymentOption po)
    {
        return bid(po, t.getShow1kBid(), t.getClickBid());
    }

    public static double payRate(WebMaster w, PaymentOption po)
    {
        return bid(po, w.getShowPayRate(), w.getClickPayRate());
    }

    public static void logNull(Logger l, String marker, Object o)
    {
        Util.logInfo(l, marker + (o == null ? " null" : " not null"));
    }

    public static ServiceData.Setting fetch(Setting s)
    {
        final ServiceData.Setting res = new ServiceData.Setting();
        if(s != null)
        {
            final Marshaller m = new Marshaller(s.getValue());
            res.show = m.getInt();
            res.click = m.getInt();
        }
        return res;
    }

    public static String valAsText(JsonNode parent, String name)
    {
        final JsonNode n = parent.findValue(name);
        return n == null ? null : n.getTextValue();
    }

    public static int valAsInt(JsonNode parent, String name)
    {
        final JsonNode n = parent.findValue(name);
        return n == null ? 0 : n.getIntValue();
    }

    public static int[] asIntArr(JsonNode node)
    {
        if(node != null && node.isArray())
        {
            int[] res = new int[node.size()];
            int index = 0;
            for(Iterator<JsonNode> i = node.iterator(); i.hasNext(); ++index)
                res[index] = i.next().getIntValue();
            return res;
        }

        return null;
    }

    public static class StringGenerator
    {
        public String generate(int count)
        {
            final char[] out = new char[count];
            for(int i = 0; i < count; ++i)
                out[i] = base_[rnd_.nextInt(base_.length)];
            return new String(out);
        }

//      private static final char[] base_ = (new String("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")).toCharArray(); // SMU 2013/06/09 was
      private static final char[] base_ = (new String("0123456789abcdefghijklmnopqrstuvwxyz")).toCharArray();
        private Random rnd_ = new Random();
    }

    public static OpenIntHashSet fill(final OpenIntHashSet s, IntArrayList lst)
    {
        for(int i = 0, count = lst.size(); i < count; ++i)
            s.add(lst.get(i));
        return s;
    }

    public static String getRequest (String sRequest, Logger log) // SMU 2013/08/02
    {
    	String sRet = new String();
    	try {
	    	URL url = new URL(sRequest);
	    	URLConnection urlConn = url.openConnection();
	        BufferedReader in = new BufferedReader(
	                                    new InputStreamReader(
	                                    urlConn.getInputStream()));
	        String inputLine;
	
	        while ((inputLine = in.readLine()) != null) 
	        	sRet += inputLine;
	        in.close();
    	} catch(Exception exc)
	    {
    		if(log != null)
    			log.error("getRequest Exc: " + exc.toString());
	    }
    	return sRet;
    }
*/
}
