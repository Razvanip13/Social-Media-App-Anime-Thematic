package socialnetwork.utils.events;

import socialnetwork.domain.friendship.FriendDTO;
import socialnetwork.domain.message.Message;

public class MessageEventChange extends AbstractEvent{


    private ChangeEventType type;
    private Message data, oldData;

    public MessageEventChange(ChangeEventType type, Message data) {
        this.type = type;
        this.data = data;
    }

    public MessageEventChange(ChangeEventType type, Message data, Message oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Message getData() {
        return data;
    }

    public Message getOldData() {
        return oldData;
    }
}
