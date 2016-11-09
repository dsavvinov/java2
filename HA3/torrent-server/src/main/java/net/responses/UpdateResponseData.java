package net.responses;

public class UpdateResponseData {
    public boolean getStatus() {
        return status;
    }

    private final boolean status;

    public UpdateResponseData(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return Boolean.toString(status);
    }
}
