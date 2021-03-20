package socialnetwork.utils.events;

import socialnetwork.domain.friendship.FriendDTO;

public class FriendshipChangeEvent extends AbstractEvent{

    private ChangeEventType type;
    private FriendDTO data, oldData;

    public FriendshipChangeEvent(ChangeEventType type, FriendDTO data) {
        this.type = type;
        this.data = data;
    }

    public FriendshipChangeEvent(ChangeEventType type, FriendDTO data, FriendDTO oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public FriendDTO getData() {
        return data;
    }

    public FriendDTO getOldData() {
        return oldData;
    }
}
