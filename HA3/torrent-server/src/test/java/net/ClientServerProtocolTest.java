package net;

import exceptions.InvalidProtocolException;
import net.protocols.ClientServerProtocol;
import net.queries.UploadQuery;
import net.queries.requests.ListRequest;
import net.queries.requests.SourcesRequest;
import net.queries.requests.UpdateRequest;
import net.queries.requests.UploadRequest;
import net.queries.responses.ListResponse;
import net.queries.responses.SourcesResponse;
import net.queries.responses.UpdateResponse;
import net.queries.responses.UploadResponse;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Arrays;

import static net.Util.assertWriteReadIdentity;

public class ClientServerProtocolTest {
    private ClientServerProtocol clientServerProtocol = new ClientServerProtocol();

    @Test
    public void ListRequestTest() throws Exception {
        ListRequest listRequest = new ListRequest();
        assertWriteReadIdentity(clientServerProtocol, listRequest, true);
    }

    @Test
    public void SourceRequestTest() throws Exception {
        SourcesRequest sourcesRequest = new SourcesRequest(42);
        assertWriteReadIdentity(clientServerProtocol, sourcesRequest, true);
    }

    @Test
    public void UpdateRequestTest() throws Exception {
        int[] arr = PimpedRandom.nextIntArr();
        Arrays.setAll(arr, operand -> PimpedRandom.nextInt());

        UpdateRequest updateRequest = new UpdateRequest(
                (short) PimpedRandom.nextInt(Short.MAX_VALUE),
                arr
        );

        assertWriteReadIdentity(clientServerProtocol, updateRequest, true);
    }

    @Test
    public void UploadRequestTest() throws Exception {
        UploadRequest uploadRequest = new UploadRequest(PimpedRandom.nextString(), PimpedRandom.nextLong());
        assertWriteReadIdentity(clientServerProtocol, uploadRequest, true);
    }

    @Test
    public void ListResponseTest() throws Exception {
        ListResponse listResponse = new ListResponse();

        int count = PimpedRandom.nextInt(1000);
        for (int i = 0; i < count; i++) {
            ListResponse.ListResponseItem item = new ListResponse.ListResponseItem(
                    PimpedRandom.nextInt(),
                    PimpedRandom.nextString(),
                    PimpedRandom.nextLong()
            );
            listResponse.add(item);
        }

        assertWriteReadIdentity(clientServerProtocol, listResponse, false);
    }

    @Test
    public void SourcesResponseTest() throws Exception {
        SourcesResponse sourcesResponse = new SourcesResponse();

        int count = PimpedRandom.nextInt(1000);
        for (int i = 0; i < count; i++) {
            SourcesResponse.Source item = new SourcesResponse.Source(
                    PimpedRandom.nextShort(),
                    InetAddress.getByAddress(PimpedRandom.nextBytes(4)).getHostAddress()
            );
            sourcesResponse.add(item);
        }

        assertWriteReadIdentity(clientServerProtocol, sourcesResponse, false);
    }

    @Test
    public void UpdateResponseTest() throws Exception {
        UpdateResponse updateResponse = new UpdateResponse(PimpedRandom.nextBoolean());
        assertWriteReadIdentity(clientServerProtocol, updateResponse, false);
    }

    @Test
    public void UploadResponseTest() throws Exception {
        UploadResponse uploadResponse = new UploadResponse(PimpedRandom.nextInt());
        assertWriteReadIdentity(clientServerProtocol, uploadResponse, false);
    }

    @Test(expected = InvalidProtocolException.class)
    public void WrongResponseFormatTest() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream(10000);
        os.write(42);

        InputStream is = new ByteArrayInputStream(os.toByteArray());
        Message readResponse = clientServerProtocol.readResponse(new UploadQuery(), is);
    }

    @Test(expected = InvalidProtocolException.class)
    public void WrongRequestFormatTest() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream(10000);
        os.write(42);

        InputStream is = new ByteArrayInputStream(os.toByteArray());
        Message readRequest = clientServerProtocol.readRequest(is);
    }
}