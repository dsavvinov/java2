package net;

import exceptions.InvalidProtocolException;
import net.requests.*;
import net.responses.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Arrays;

public class ClientServerProtocolTest {
    private void assertWriteReadIdentity(Request initialRequest) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream(10000);
        ClientServerProtocol.writeRequest(initialRequest, os);

        InputStream is = new ByteArrayInputStream(os.toByteArray());
        Request readRequest = ClientServerProtocol.readRequest(is);

        RequestsComparison.assertRequestsEqual(initialRequest, readRequest);
    }

    private void assertWriteReadIdentity(Response initialResponse) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream(10000);
        ClientServerProtocol.writeResponse(initialResponse, os);

        InputStream is = new ByteArrayInputStream(os.toByteArray());
        Response readResponse = ClientServerProtocol.readResponse(initialResponse.getType(), is);

        ResponseComparison.assertResponsesEqual(initialResponse, readResponse);
    }

    @Test
    public void ListRequestTest() throws Exception {
        ListRequestData requestData = new ListRequestData();
        Request listRequest = new Request(RequestType.LIST, requestData);

        assertWriteReadIdentity(listRequest);
    }

    @Test
    public void SourceRequestTest() throws Exception {
        SourcesRequestData requestData = new SourcesRequestData(42);
        Request r = new Request(RequestType.SOURCES, requestData);

        assertWriteReadIdentity(r);
    }

    @Test
    public void UpdateRequestTest() throws Exception {
        int[] arr = PimpedRandom.nextIntArr();
        Arrays.setAll(arr, operand -> PimpedRandom.nextInt());

        UpdateRequestData requestData = new UpdateRequestData(
                (short) PimpedRandom.nextInt(Short.MAX_VALUE),
                arr
        );

        Request r = new Request(RequestType.UPDATE, requestData);

        assertWriteReadIdentity(r);
    }

    @Test
    public void UploadRequestTest() throws Exception {
        UploadRequestData requestData = new UploadRequestData(PimpedRandom.nextString(), PimpedRandom.nextLong());
        Request r = new Request(RequestType.UPLOAD, requestData);

        assertWriteReadIdentity(r);
    }

    @Test
    public void ListResponseTest() throws Exception {
        ListResponseData responseData = new ListResponseData();
        int count = PimpedRandom.nextInt(1000);
        for (int i = 0; i < count; i++) {
            ListResponseData.ListResponseItem item = new ListResponseData.ListResponseItem(
                    PimpedRandom.nextInt(),
                    PimpedRandom.nextString(),
                    PimpedRandom.nextLong()
            );
            responseData.add(item);
        }

        Response r = new Response(RequestType.LIST, responseData);

        assertWriteReadIdentity(r);
    }

    @Test
    public void SourcesResponseTest() throws Exception {
        SourcesResponseData responseData = new SourcesResponseData();
        int count = PimpedRandom.nextInt(1000);
        for (int i = 0; i < count; i++) {
            SourcesResponseData.Source item = new SourcesResponseData.Source(
                    PimpedRandom.nextShort(),
                    InetAddress.getByAddress(PimpedRandom.nextBytes(4)).getHostAddress()
            );
            responseData.add(item);
        }

        Response r = new Response(RequestType.SOURCES, responseData);

        assertWriteReadIdentity(r);
    }

    @Test
    public void UpdateResponseTest() throws Exception {
        UpdateResponseData responseData = new UpdateResponseData(PimpedRandom.nextBoolean());
        Response r = new Response(RequestType.UPDATE, responseData);
        assertWriteReadIdentity(r);
    }

    @Test
    public void UploadResponseTest() throws Exception {
        UploadResponseData responseData = new UploadResponseData(PimpedRandom.nextInt());
        Response r = new Response(RequestType.UPLOAD, responseData);
        assertWriteReadIdentity(r);
    }

    @Test(expected = InvalidProtocolException.class)
    public void WrongResponseFormatTest() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream(10000);
        os.write(42);

        InputStream is = new ByteArrayInputStream(os.toByteArray());
        Response readResponse = ClientServerProtocol.readResponse(RequestType.UPLOAD, is);
    }

    @Test(expected = InvalidProtocolException.class)
    public void WrongRequestFormatTest() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream(10000);
        os.write(42);

        InputStream is = new ByteArrayInputStream(os.toByteArray());
        Request readRequest = ClientServerProtocol.readRequest(is);
    }
}