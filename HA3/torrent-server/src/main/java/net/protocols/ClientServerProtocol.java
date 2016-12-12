package net.protocols;

import exceptions.InvalidProtocolException;
import exceptions.WrongArgumentException;
import net.AbstractProtocol;
import net.Protocol;
import net.Query;
import net.queries.ListQuery;
import net.queries.SourcesQuery;
import net.queries.UpdateQuery;
import net.queries.UploadQuery;
import net.queries.requests.*;

import java.io.*;

import static utils.Constants.*;

/**
 * Class that manages correct format of the interaction
 * between client and server.
 *
 */
public class ClientServerProtocol extends AbstractProtocol implements Protocol {
    @Override
    public Query parseQueryType(InputStream is) throws InvalidProtocolException {
        try {
            DataInputStream in = new DataInputStream(is);
            int type = in.readInt();

            switch (type) {
                case LIST_ID:
                    return new ListQuery();
                case UPLOAD_ID:
                    return new UploadQuery();
                case SOURCES_ID:
                    return new SourcesQuery();
                case UPDATE_ID:
                    return new UpdateQuery();
                default:
                    throw new InvalidProtocolException("Unknown type of request");
            }
        }
        catch (IOException e) {
            throw new InvalidProtocolException("Format error during reading request: " + e.getMessage());
        }
    }
}
