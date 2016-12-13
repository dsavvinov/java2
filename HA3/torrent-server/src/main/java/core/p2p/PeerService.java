package core.p2p;

import exceptions.InvalidProtocolException;
import net.queries.responses.StatResponse;

import java.io.IOException;

/**
 * Generic facade for interactions with remote peer.
 *
 * It is implementors responsibility to maintain consistent state of the
 * client, e.g. correctly add downloaded parts to list of seeded parts, etc.
 */
public interface PeerService {
    void get(String peerAddress, short peerPort, int fileID, int partID, String downloadPath, long totalSize) throws IOException;
    StatResponse stat(String peerAddress, short peerPort, int fileID) throws IOException, InvalidProtocolException;
}
