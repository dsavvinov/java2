package net.queries;

import net.Message;
import net.Query;
import net.queries.requests.UploadRequest;
import net.queries.responses.UploadResponse;

import static utils.Constants.UPLOAD_ID;

public class UploadQuery implements Query {
    @Override
    public Message getEmptyRequest() {
        return new UploadRequest();
    }

    @Override
    public Message getEmptyResponse() {
        return new UploadResponse();
    }

    @Override
    public int getId() {
        return UPLOAD_ID;
    }
}
