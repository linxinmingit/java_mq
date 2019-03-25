package kafka;

import java.util.Properties;

public class Producer {
	//分区
	private static int partition = 3;
	//副本(备份)
	private static int repilca = 3;
	private Properties properties;
	final static String topic = "Kloss";
}
