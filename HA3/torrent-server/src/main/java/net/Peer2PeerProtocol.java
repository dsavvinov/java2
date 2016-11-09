package net;

import exceptions.InternalError;
import exceptions.InvalidProtocolException;
import exceptions.WrongArgumentException;
import net.requests.GetRequestData;
import net.requests.Request;
import net.requests.RequestType;
import net.requests.StatRequestData;
import net.responses.GetResponseData;
import net.responses.Response;
import net.responses.StatResponseData;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Peer2PeerProtocol {
    public static Request readRequest(InputStream is) throws InvalidProtocolException {
        DataInputStream in = new DataInputStream(is);

        try {
            RequestType type = RequestType.forInt(in.readInt(), /* isP2P = */ true);
            Object data;
            switch(type) {
                case STAT:
                    data = readStatRequestData(in);
                    break;
                case GET:
                    data = readGetRequestData(in);
                    break;
                default:
                    throw new InvalidProtocolException("Wrong request type for P2P server");
            }
            return new Request(type, data);
        } catch (IOException e) {
            throw new InvalidProtocolException("Error during reading request: " + e.getMessage());
        }
    }

    public static void writeRequest(Request r, OutputStream os)
            throws IOException, WrongArgumentException {
        DataOutputStream out = new DataOutputStream(os);
        out.writeInt(r.getType().getId());
        switch (r.getType()) {
            case STAT:
                writeStatRequestData(out, (StatRequestData) r.getData());
                break;
            case GET:
                writeGetRequestData(out, (GetRequestData) r.getData());
                break;
            default:
                throw new WrongArgumentException("Wrong request type for P2P server: " + r.getType().getId());
        }
    }

    // ======================================================

    static void writeStatRequestData(DataOutputStream out, StatRequestData data) throws IOException {
        out.writeInt(data.getId());
    }

    static StatRequestData readStatRequestData(DataInputStream in) throws IOException {
        int id = in.readInt();
        return new StatRequestData(id);
    }

    // ======================================================

    static GetRequestData readGetRequestData(DataInputStream in) throws IOException {
        int id = in.readInt();
        int part = in.readInt();
        return new GetRequestData(id, part);
    }

    static void writeGetRequestData(DataOutputStream out, GetRequestData data) throws IOException {
        out.writeInt(data.getId());
        out.writeInt(data.getPart());
    }

    // ======================================================


    /** ===================== Responses-stuff ================= **/
    public static Response readResponse(RequestType type, InputStream is)
            throws WrongArgumentException, InvalidProtocolException {
        try {
        DataInputStream in = new DataInputStream(is);
            switch (type) {
                case STAT:
                    return readStatResponse(in);
                case GET:
                    throw new InternalError("Get response should be read manually!");
                default:
                    throw new WrongArgumentException("Wrong request type for P2P-server: " + type);
            }
        } catch (IOException e) {
            throw new InvalidProtocolException("Protocol error: " + e.getMessage());
        }
    }

    public static void writeResponse(Response r, OutputStream os)
            throws WrongArgumentException, InvalidProtocolException {
        DataOutputStream out = new DataOutputStream(os);
        try {
            switch (r.getType()) {
                case STAT:
                    writeStatResponseData(out, (StatResponseData) r.getData());
                    break;
                case GET:
                    throw new InternalError("Get response should be wrote manually!");
                default:
                    throw new WrongArgumentException("Wrong response type for P2P-server: " + r.getType());
            }
        } catch (IOException e) {
            throw new InvalidProtocolException("Protocol error: " + e.getMessage());
        }
    }

    // ======================================================

    // ======================================================

    private static void writeStatResponseData(DataOutputStream out, StatResponseData data) throws IOException {
        int[] parts = data.getParts();
        out.writeInt(parts.length);
        for (int part : parts) {
            out.writeInt(part);
        }
    }

    private static Response readStatResponse(DataInputStream in) throws IOException {
        int count = in.readInt();
        int[] parts = new int[count];

        for (int i = 0; i < count; i++) {
            parts[i] = in.readInt();
        }

        return new Response(RequestType.STAT, new StatResponseData(parts));
    }
}
