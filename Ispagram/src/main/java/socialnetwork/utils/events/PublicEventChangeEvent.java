package socialnetwork.utils.events;

import socialnetwork.domain.frequest.FRequestDTO;
import socialnetwork.domain.public_events.PublicEvent;

public class PublicEventChangeEvent extends AbstractEvent{

    private ChangeEventType type;
    private PublicEvent data, oldData;

    public PublicEventChangeEvent(ChangeEventType type, PublicEvent data) {
        this.type = type;
        this.data = data;
    }

    public PublicEventChangeEvent(ChangeEventType type, PublicEvent data, PublicEvent oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public PublicEvent getData() {
        return data;
    }

    public PublicEvent getOldData() {
        return oldData;
    }
}
