package socialnetwork.utils.events;

import socialnetwork.domain.frequest.FRequest;
import socialnetwork.domain.frequest.FRequestDTO;
import socialnetwork.domain.friendship.FriendDTO;

public class RequestChangeEvent extends AbstractEvent{


    private ChangeEventType type;
    private FRequestDTO data, oldData;

    public RequestChangeEvent(ChangeEventType type, FRequestDTO data) {
        this.type = type;
        this.data = data;
    }

    public RequestChangeEvent(ChangeEventType type, FRequestDTO data, FRequestDTO oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public FRequestDTO getData() {
        return data;
    }

    public FRequestDTO getOldData() {
        return oldData;
    }
}
