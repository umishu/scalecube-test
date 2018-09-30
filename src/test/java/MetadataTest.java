import org.junit.Test;

import io.scalecube.cluster.Member;
import io.scalecube.cluster.gossip.GossipProtocolImpl;
import io.scalecube.cluster.membership.MembershipProtocolImpl;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.AfterClass;

public class MetadataTest extends AbstractTest {
	
	@Test
	public void shutdownCluster1() throws Exception {
		//shutdown cluster 7000
		clusters[0].shutdown();
		
		Thread.sleep(3_000);
		
		//get gossip member on cluster 7001
		Field gossipField = clusters[1].cluster().getClass().getDeclaredField("gossip");
		gossipField.setAccessible(true);
		GossipProtocolImpl gossip = (GossipProtocolImpl) gossipField.get(clusters[1].cluster());
		Field memberField = gossip.getClass().getDeclaredField("remoteMembers");
		memberField.setAccessible(true);
		List<Member> members = (List<Member>) memberField.get(gossip);
		System.out.println(members.get(0)); //CD1E84FEF7A049899106@192.168.20.6:7000
		assertEquals(0, members.size());
		
	}
	
	@AfterClass
	public static void waitShutdown() throws Exception {
		Thread.sleep(10_000);
	}

}
