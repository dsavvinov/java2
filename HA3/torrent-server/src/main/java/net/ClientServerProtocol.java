package net;

import exceptions.InvalidProtocolException;
import exceptions.WrongArgumentException;
import net.requests.*;
import net.responses.*;

import java.io.*;
import java.net.InetAddress;

/**
 * Class that manages correct format of the interaction
 * between client and server.
 * This logic is intentionally left in this class instead of
 * hiding behind abstract methods in concrete requests(responses),
 * because it's much simpler to keep mutually inverse methods
 * synchronized in this way (even if the whole ClientServerProtocol-class
 * looks bulky).
 *
 * ClientServerProtocol exposes public '(read/write)(Request/Response)' methods,
 * which implemented using private helpers '(read/write)(COMMAND_NAME)(Request/Response)Data'
 * (parenthesis used for clarity).
 *
 * Note that read/write methods are paired together and should be kept synchronized.
 *
 */
public class ClientServerProtocol {
    // TODO: wrap errors in DataStreams in IllegalProtocolExceptions

    /** ================= Requests-stuff ================= **/
    public static Request readRequest(InputStream is) throws  InvalidProtocolException {
        DataInputStream in = new DataInputStream(is);

        try {
            RequestType type = RequestType.forInt(in.readInt(), /* isP2P = */ false);
            Object data;
            switch (type) {
                case LIST:
                    data = readListRequestData(in);
                    break;
                case SOURCES:
                    data = readSourcesRequestData(in);
                    break;
                case UPDATE:
                    data = readUpdateRequestData(in);
                    break;
                case UPLOAD:
                    data = readUploadRequestData(in);
                    break;
                default:
                    throw new InvalidProtocolException("Unknown type of request");
            }
            return new Request(type, data);
        } catch (IOException e) {
            throw new InvalidProtocolException("Format error during reading request: " + e.getMessage());
        }
    }

    public static void writeRequest(Request request, OutputStream os)
            throws IOException, WrongArgumentException {
        DataOutputStream out = new DataOutputStream(os);
        out.writeInt(request.getType().getId());
        switch(request.getType()) {
            case LIST:
                writeListRequestData(out, (ListRequestData) request.getData());
                break;
            case SOURCES:
                writeSourcesRequestData(out, (SourcesRequestData) request.getData());
                break;
            case UPDATE:
                writeUpdateRequestData(out, (UpdateRequestData) request.getData());
                break;
            case UPLOAD:
                writeUploadRequestData(out, (UploadRequestData) request.getData());
                break;
            default:
                throw new WrongArgumentException("Unknown type of request: " + request.getType());
        }
    }

    // ======================================================

    private static void writeUploadRequestData(DataOutputStream out, UploadRequestData data) throws IOException {
        out.writeUTF(data.getName());
        out.writeLong(data.getSize());
    }

    private static UploadRequestData readUploadRequestData(DataInputStream inputStream) throws IOException {
        String name = inputStream.readUTF();
        long size = inputStream.readLong();
        return new UploadRequestData(name, size);
    }

    // ======================================================

    private static void writeUpdateRequestData(DataOutputStream out, UpdateRequestData data) throws IOException {
        out.writeShort(data.getClientPort());
        int[] ids = data.getIds();
        out.writeInt(ids.length);
        for (int id : ids) {
            out.writeInt(id);
        }
    }

    private static UpdateRequestData readUpdateRequestData(DataInputStream inputStream) throws IOException {
        short clientPort = inputStream.readShort();
        int count = inputStream.readInt();
        int[] ids = new int[count];
        for (int i = 0; i < count; i++) {
            ids[i] = inputStream.readInt();
        }

        return new UpdateRequestData(clientPort, ids);
    }

    // ======================================================

    private static void writeSourcesRequestData(DataOutputStream out, SourcesRequestData data) throws IOException {
        out.writeInt(data.getRequestedId());
    }

    private static SourcesRequestData readSourcesRequestData(DataInputStream inputStream) throws IOException {
        int id = inputStream.readInt();
        return new SourcesRequestData(id);
    }

    // ======================================================

    private static ListRequestData readListRequestData(DataInputStream inputStream) throws IOException {
        return new ListRequestData();
    }

    private static void writeListRequestData(DataOutputStream outputStream, ListRequestData data) { }

    // ======================================================


    /** ===================== Responses-stuff ================= **/
    public static Response readResponse(RequestType type, InputStream is)
            throws WrongArgumentException, InvalidProtocolException {
        DataInputStream in = new DataInputStream(is);
        try {
            switch (type) {
                case LIST:
                    return readListResponse(in);
                case SOURCES:
                    return readSourcesResponse(in);
                case UPDATE:
                    return readUpdateResponse(in);
                case UPLOAD:
                    return readUploadResponse(in);
                default:
                    throw new WrongArgumentException("Unknown request type: " + type.toString());
            }
        } catch (IOException e) {
            throw new InvalidProtocolException("Protocol error during reading response: " + e.getMessage());
        }
    }

    public static void writeResponse(Response response, OutputStream os)
            throws IOException, WrongArgumentException {
        DataOutputStream out = new DataOutputStream(os);
        switch (response.getType()) {
            case LIST:
                writeListResponseData(out, (ListResponseData) response.getData());
                break;
            case SOURCES:
                writeSourcesResponseData(out, (SourcesResponseData) response.getData());
                break;
            case UPDATE:
                writeUpdateResponseData(out, (UpdateResponseData) response.getData());
                break;
            case UPLOAD:
                writeUploadResponseData(out, (UploadResponseData) response.getData());
                break;
            default:
                throw new WrongArgumentException("Unknown request type: " + response.getType());
        }
    }

    // ======================================================

    private static void writeUploadResponseData(DataOutputStream out, UploadResponseData data)
            throws IOException {
        out.writeInt(data.getId());
    }

    private static Response readUploadResponse(DataInputStream in) throws IOException {
        int id = in.readInt();

        UploadResponseData data = new UploadResponseData(id);
        return new Response(RequestType.UPLOAD, data);
    }

    // ======================================================

    private static void writeUpdateResponseData(DataOutputStream out, UpdateResponseData data)
            throws IOException {
        out.writeBoolean(data.getStatus());
    }

    private static Response readUpdateResponse(DataInputStream in) throws IOException {
        boolean status = in.readBoolean();

        UpdateResponseData data = new UpdateResponseData(status);
        return new Response(RequestType.UPDATE, data);
    }

    // ======================================================

    private static void writeSourcesResponseData(DataOutputStream out, SourcesResponseData data)
            throws IOException {
        out.writeInt(data.size());

        for (int i = 0; i < data.size(); i++) {
            SourcesResponseData.Source item = data.get(i);
            out.writeUTF(item.getHost());

            out.writeShort(item.getPort());
        }
    }

    private static Response readSourcesResponse(DataInputStream in) throws IOException {
        SourcesResponseData data = new SourcesResponseData();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            // address: ByteByteByteByte
            byte[] addressBytes = new byte[4];

            String address = in.readUTF();

            // clientPort: Short
            short clientPort = in.readShort();

            data.add(new SourcesResponseData.Source(clientPort, address));
        }

        return new Response(RequestType.SOURCES, data);
    }

    // ======================================================
    private static void writeListResponseData(DataOutputStream out, ListResponseData data)
            throws IOException {
        out.writeInt(data.size());

        for (int i = 0; i < data.size(); i++) {
            ListResponseData.ListResponseItem item = data.get(i);
            out.writeInt(item.id);
            out.writeUTF(item.name);
            out.writeLong(item.size);
        }
    }

    private static Response readListResponse(DataInputStream in) throws IOException {
        ListResponseData data = new ListResponseData();

        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            int id = in.readInt();
            String name = in.readUTF();
            long size = in.readLong();

            data.add(new ListResponseData.ListResponseItem(id, name, size));
        }

        return new Response(RequestType.LIST, data);
    }
}
