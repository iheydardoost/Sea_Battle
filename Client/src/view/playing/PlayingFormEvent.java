package view.playing;

import java.util.EventObject;

public class PlayingFormEvent extends EventObject {
    private PlayingReqType reqType;

    public PlayingFormEvent(Object source, PlayingReqType reqType) {
        super(source);
        this.reqType = reqType;
    }

    public PlayingReqType getReqType() {
        return reqType;
    }
}
