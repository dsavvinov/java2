package net.protocols;

import exceptions.InvalidProtocolException;
import net.AbstractProtocol;
import net.Query;
import net.queries.GetQuery;
import net.queries.StatQuery;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static utils.Constants.GET_ID;
import static utils.Constants.STAT_ID;

public class Peer2PeerProtocol extends AbstractProtocol {
    @Override
    public Query parseQueryType(InputStream is) throws InvalidProtocolException {
        DataInputStream in = new DataInputStream(is);
        int typeID;

        try {
            typeID = in.readInt();
        } catch (IOException e) {
            throw new InvalidProtocolException("Format error during reading request: " + e.getMessage());
        }

        switch (typeID) {
            case STAT_ID: return new StatQuery();
            case GET_ID: return new GetQuery();
            default: throw new InvalidProtocolException("Unknown query id: " + typeID);
        }
    }
}
