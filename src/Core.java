import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Core 
{
	static public Logger log_ = Logger.getLogger(Core.class.getName());
	static final ConfigLocal config_ = new ConfigLocal ();

	private static void initializeLogger()
	{
		Properties logProperties = new Properties();
		try {
			// load our log4j properties / configuration file
			logProperties.load(new FileInputStream(ConfigLocal.LOG_PROPERTIES_FILE));
			PropertyConfigurator.configure(logProperties);
			log_.info("Logging initialized.");
		} catch(IOException e) {
			throw new RuntimeException("Unable to load logging property " + ConfigLocal.LOG_PROPERTIES_FILE);
		}
	}

	public static void initDir(String path) throws Exception
	{
		if (!Files.exists(Paths.get(path)) && !((new File(path)).mkdirs()))
			throw new Exception("Faild create dir: " + path);
	}
	
	public static void stop()
	{
		Core.log_.info("Proccess correctly stopped");
		System.exit(0);
	}
	
	public static void initialize () throws Exception 
    {
    	initializeLogger();
    	
    	try {
	    	System.setProperty("file.encoding","UTF-8");
	    	Field charset = Charset.class.getDeclaredField("defaultCharset");
	    	charset.setAccessible(true);
	    	charset.set(null,null);
	    	Core.log_.info("Кодировка изменена");
    	} catch(Exception e)
    	{}
    	
    	config_.Load(ConfigLocal.CFG_PROPERTIES_FILE);

    	initDir(ConfigLocal.dirStoreFromRabb_);
    	initDir(ConfigLocal.dirInboxFromRabb_);
    	initDir(ConfigLocal.dirTroubleFromRabb_);
    }

	public static boolean moveFile(String srcPF, String dstDir) throws Exception
	{	
		return null != Files.move (Paths.get(srcPF), Paths.get(dstDir), StandardCopyOption.REPLACE_EXISTING);
	}

	public static void Run()
	{
		try {
		 Runnable threadMainByPeriod = new Runnable() 
		 {
			 public void run() 
			 {
				 ExecutorService service = Executors.newSingleThreadExecutor();
				 try {
					 Runnable threadSlaveWithInterrupted = new Runnable() {
						 @Override
						 public void run() 
						 {
							final String uuid = UUID.randomUUID().toString();
							long time0 = System.currentTimeMillis();
							log_.info("Start " + uuid);
							
							RunReadFromRabbit_WriteToActiveMq ();
							
							long time1 = System.currentTimeMillis();
							log_.info("Finish " + uuid + " time(sec): " + (time1 - time0)/1000.);
				        }
				    };

				    Future<?> f = service.submit(threadSlaveWithInterrupted);
				    f.get(Core.config_.nTimerSeconds_, TimeUnit.SECONDS);     // attempt the task for n seconds
				 }	catch (final InterruptedException e)	{
					// The thread was interrupted during sleep, wait or join
					log_.info("ExecutorService: The thread was interrupted during sleep, wait or join");
				 } catch (final TimeoutException e) {
					// Took too long!
					log_.info("ExecutorService: Took too long!");
				 }	catch (final ExecutionException e) {
					 // An exception from within the Runnable task
					 log_.info("ExecutorService: An exception from within the Runnable task");
				 }	finally {
					 log_.info("ExecutorService: ShutDown");
					 service.shutdown();
				 }
			 }
		 };
		    
		 ScheduledExecutorService service = Executors
	                .newSingleThreadScheduledExecutor();
		 service.scheduleAtFixedRate(threadMainByPeriod, 0, Core.config_.nTimerSeconds_ + 1, TimeUnit.SECONDS);
		} catch (Exception e)
		{
		    log_.error("ThreadMain : " + e.getMessage());
		}
    }
	
	private static void RunReadFromRabbit_WriteToActiveMq ()
	{
		Rabbit.ReceiveMessage(); 
		ActiveMQ.SendMessages();
	}

	public static String GetSavedDocPF (String sDir, String sFN, String sBody) throws Exception
	{
		if(sBody == null)
			throw new Exception("Body is empty");

		Core.initDir(sDir);
		String sPF = sDir + '/' + sFN;
		byte[] utf8 = sBody.getBytes("UTF-8"); 

		FileUtils.writeByteArrayToFile(new File(sPF), utf8);
		return sPF;
	}	

	static String readFile(String sPF) throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(sPF));
	  return new String(encoded, StandardCharsets.UTF_8);
	}

	static void writeFile(String content, String sPF) throws IOException 
	{
		Path logFile = Paths.get(sPF);
		BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8);
		writer.write(content);
		writer.close();
	}
	
}
    