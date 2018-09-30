import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.scalecube.cluster.Cluster;
import io.scalecube.cluster.ClusterConfig;
import io.scalecube.transport.Address;

public class MetaCluster implements Runnable {
	
	private Address[] seeds;
	private String localEth;
	private int gossipPort;
	private Cluster cluster;
	private static int counter = 0;
	private Future<?> future;
	
	public MetaCluster(Address[] seeds, String localEth, int gossipPort) {
		this.seeds = seeds;
		this.localEth = localEth;
		this.gossipPort = gossipPort;
	}
	
	public void run() {
		ClusterConfig config = ClusterConfig.builder()
		        .seedMembers(seeds)
		        .listenInterface(localEth)
		        .port(gossipPort)
		        .syncGroup("test-cluster")
		        .build();
		this.cluster = Cluster.joinAwait(config);
		
		
		future = Executors.newSingleThreadScheduledExecutor()
		.scheduleAtFixedRate(new Thread(() -> {
			// Update metadata after startup
			cluster.updateMetadataProperty("count", String.valueOf(counter++));
		}), 0, 2, TimeUnit.SECONDS);
	}
	
	public Cluster cluster() {
		return this.cluster;
	}
	
	public void shutdown() {
		this.future.cancel(true);
		this.cluster.shutdown();
	}
}
