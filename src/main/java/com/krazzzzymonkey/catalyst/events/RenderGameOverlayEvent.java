package com.krazzzzymonkey.catalyst.events;

import dev.tigr.simpleevents.event.Event;

import javax.management.relation.RelationNotFoundException;


public class RenderGameOverlayEvent extends ClientEvent {

    public RenderGameOverlayEvent(){
        setName("RenderGameOverlayEvent");
    }

    public static class Pre extends RenderGameOverlayEvent {
        private final net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType elementType;

        public Pre(net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType elementType) {
            this.elementType = elementType;
            setName("RenderGameOverlayEvent.PRE");
        }

        public net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType getType() {
            return this.elementType;
        }

    }

    public static class Post extends RenderGameOverlayEvent {
        public Post(){
            setName("RenderGameOverlayEvent.POST");
        }
    }

    public static class Text extends RenderGameOverlayEvent {
        public Text(){
            setName("RenderGameOverlayEvent.TEXT");
        }
    }
}
