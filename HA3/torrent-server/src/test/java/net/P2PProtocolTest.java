package net;

import net.protocols.Peer2PeerProtocol;
import net.queries.requests.GetRequest;
import net.queries.requests.StatRequest;
import net.queries.responses.StatResponse;
import org.junit.Test;

import static net.PimpedRandom.nextInt;
import static net.PimpedRandom.nextIntArr;
import static net.Util.assertWriteReadIdentity;

public class P2PProtocolTest {
    private Peer2PeerProtocol peer2PeerProtocol = new Peer2PeerProtocol();

    @Test
    public void GetRequestTest() throws Exception {
        GetRequest getRequest = new GetRequest(nextInt(), nextInt());
        assertWriteReadIdentity(peer2PeerProtocol, getRequest, true);
    }

    @Test
    public void StatRequestTest() throws Exception {
        StatRequest statRequest = new StatRequest(nextInt());
        assertWriteReadIdentity(peer2PeerProtocol, statRequest, true);
    }

    @Test
    public void StatResponseTest() throws Exception {
        StatResponse statResponse = new StatResponse(nextIntArr());
        assertWriteReadIdentity(peer2PeerProtocol, statResponse, false);
    }
}
