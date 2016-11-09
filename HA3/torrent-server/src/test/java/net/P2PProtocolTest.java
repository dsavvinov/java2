package net;

import net.requests.GetRequestData;
import net.requests.Request;
import net.requests.RequestType;
import net.requests.StatRequestData;
import net.responses.Response;
import net.responses.StatResponseData;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static net.PimpedRandom.nextInt;
import static net.PimpedRandom.nextIntArr;

public class P2PProtocolTest {
    private void assertReadWriteIdentity(Request initialRequest) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream(10000);
        Peer2PeerProtocol.writeRequest(initialRequest, os);

        InputStream is = new ByteArrayInputStream(os.toByteArray());
        Request readRequest = Peer2PeerProtocol.readRequest(is);

        RequestsComparison.assertRequestsEqual(initialRequest, readRequest);
    }

    private void assertReadWriteIdentity(Response initialResponse) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream(10000);
        Peer2PeerProtocol.writeResponse(initialResponse, os);

        InputStream is = new ByteArrayInputStream(os.toByteArray());
        Response readResponse = Peer2PeerProtocol.readResponse(initialResponse.getType(), is);

        ResponseComparison.assertResponsesEqual(initialResponse, readResponse);
    }

    @Test
    public void GetRequestTest() throws Exception {
        GetRequestData data = new GetRequestData(nextInt(), nextInt());
        Request getRequest = new Request(RequestType.GET, data);

        assertReadWriteIdentity(getRequest);
    }

    @Test
    public void StatRequestTest() throws Exception {
        StatRequestData data = new StatRequestData(nextInt());
        Request statRequest = new Request(RequestType.STAT, data);

        assertReadWriteIdentity(statRequest);
    }

    @Test
    public void StatResponseTest() throws Exception {
        StatResponseData data = new StatResponseData(nextIntArr());
        Response statResponse = new Response(RequestType.STAT, data);

        assertReadWriteIdentity(statResponse);
    }
}
