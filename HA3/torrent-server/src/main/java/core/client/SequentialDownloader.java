package core.client;

import core.p2p.PeerService;
import database.FileEntity;
import database.client.ClientDatabase;
import exceptions.InvalidProtocolException;
import exceptions.DownloadingException;
import io.Logger;
import net.queries.responses.ListResponse;
import net.queries.responses.SourcesResponse;
import net.queries.responses.StatResponse;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import static utils.Constants.BLOCK_SIZE;

/** Very stupid and simple implementation of Downloader, that:
 *
 *  - Fails if file is not 100% available (i.e. if there is at least one
 *    part that is not seeded by anyone at the moment)
 *  - Makes only one attempt to download part from seed (moving to next
 *    seed in case of fail)
 *  - Fails if at least one part wasn't successfully downloaded
 *  - Downloads everything sequentially
 *  - Re-downloads all parts even if some already present
 *
 */
public class SequentialDownloader extends Observable implements Downloader {
    private final Logger log;
    private final ClientDatabase db;
    private final ServerService serverService;
    private final PeerService peerService;
    private final String rootDir;

    public SequentialDownloader(Logger log, ClientDatabase db, ServerService serverService, PeerService peerService, String rootDir) {
        this.log = log;
        this.db = db;
        this.serverService = serverService;
        this.peerService = peerService;
        this.rootDir = rootDir;
    }

    @Override
    public void downloadFile(int fileID)
            throws IOException, InvalidProtocolException, DownloadingException {
        // Get meta-information of file via List-query to server
        ListResponse list = serverService.list();
        String fileName = null;
        long fileSize = -1;

        for (int i = 0; i < list.size(); i++) {
            ListResponse.ListResponseItem item = list.get(i);
            if (item.id == fileID) {
                fileName = item.name;
                fileSize = item.size;
            }
        }

        if (fileName == null) {
            throw new DownloadingException("Unknown file with id = " + fileID);
        }

        // Then get list of all peers via Source-query
        SourcesResponse sources = serverService.sources(fileID);

        // Now build a list of seeds of each part
        int blocksAmount = (int) (fileSize / BLOCK_SIZE + (fileSize % BLOCK_SIZE == 0 ? 0 : 1));
        List< ArrayList<SourcesResponse.Source> > seedsByPart = new ArrayList<>(blocksAmount);
        for (int i = 0; i < blocksAmount; i++) {
            seedsByPart.add(new ArrayList<>());
        }

        // Query each seed
        for (int i = 0; i < sources.size(); i++) {
            SourcesResponse.Source source = sources.get(i);
            StatResponse stat;
            try {
                stat = peerService.stat(source.getHost(), source.getPort(), fileID);
            } catch (IOException | InvalidProtocolException ignored) {
                continue;
            }
            for (int partID : stat.getParts()) {
                ArrayList<SourcesResponse.Source> seedsOfPart = seedsByPart.get(partID);
                seedsOfPart.add(source);
            }
        }

        // Check that every part has at least one peer
        if (seedsByPart.stream().anyMatch(ArrayList::isEmpty)) {
            throw new DownloadingException("Not 100% available file = " + fileID);
        }

        // Shuffle all lists for PEEEEEERFORMAAAAAAAANCE
        seedsByPart.forEach(Collections::shuffle);

        // Now just download patiently everything
        double progress = 0;
        double progressPerBlock = 100.0 / blocksAmount;
        String downloadPath = Paths.get(rootDir).resolve(fileName).toString();
        for (int i = 0; i < seedsByPart.size(); i++) {
            ArrayList<SourcesResponse.Source> partSeeds = seedsByPart.get(i);

            boolean downloaded = false;
            for (int j = 0; j < partSeeds.size(); j++) {
                SourcesResponse.Source source = partSeeds.get(j);
                try {
                    peerService.get(source.getHost(), source.getPort(), fileID, i, downloadPath, fileSize);
                } catch (IOException ignored) {
                    continue;
                }
                downloaded = true;
                break;
            }

            if (!downloaded) {
                throw new DownloadingException("Can't download part #" + i + " of file " + fileID);
            }

            progress += progressPerBlock;
            setChanged();
            notifyObservers(new Object[] {fileID, progress});
        }

        // Successfully downloaded, add to the DB of seeded files
        FileEntity fileEntity = new FileEntity(fileID, fileName, fileSize);
        fileEntity.setLocalPath(downloadPath);

        db.addFile(fileEntity);
        db.addAllPartsOfFile(fileEntity);
    }

}
