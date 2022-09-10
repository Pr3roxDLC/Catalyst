package com.krazzzzymonkey.catalyst.events;


import com.krazzzzymonkey.catalyst.command.Module;
import dev.tigr.simpleevents.event.Event;

public class MotionEvent extends ClientEvent {

    public MotionEvent() {
        setName("MotionEvent");
    }

    public static class PRE extends ClientEvent {

        public PRE() {
            setName("MotionEvent.PRE");
        }

    }

    public static class POST extends ClientEvent {

        public POST() {
            setName("MotionEvent.POST");
        }
    }

    public static class PREWALK extends ClientEvent {

        public PREWALK() {
            setName("MotionEvent.PREWALK");
        }

    }
}
