import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.BeforeClass;

import io.scalecube.transport.Address;

public class AbstractTest {

	private static String localIp;
	private static String localEth;
	static {
		try {
			detectNetworkInterface();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static Address[] seeds = new Address[] { 
		Address.create(localIp, 7000), 
		Address.create(localIp, 7001)
	};
	protected static MetaCluster[] clusters = new MetaCluster[] { 
		new MetaCluster(seeds, localEth, 7000), 
		new MetaCluster(seeds, localEth, 7001)
	};
	
	@BeforeClass
	public static void setup() throws Exception {
		System.setProperty("log4j.configurationFile", "conf/log4j2.xml");
		ExecutorService exec = Executors.newFixedThreadPool(2);
		Arrays.asList(clusters).forEach(cluster -> {
			exec.submit(cluster);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {}
		});
		Thread.sleep(1000);
	}
	
	private static void detectNetworkInterface() throws Exception {
		Enumeration<NetworkInterface> ethList = NetworkInterface.getNetworkInterfaces();
		while (ethList.hasMoreElements() && localEth == null) {
			NetworkInterface eth = ethList.nextElement();
			Enumeration<InetAddress> addrs = eth.getInetAddresses();
			while (addrs.hasMoreElements()) {
				InetAddress addr = addrs.nextElement();
				String hostAddr = addr.getHostAddress();
				if (hostAddr.startsWith("192") && !hostAddr.startsWith("192.168.56")) { //192.168.56=VirtufalBox
					System.out.println("LocalIp:" + hostAddr);
					localIp = hostAddr;
					localEth = eth.getName();
					break;
				}
			}
		}
	}
}
