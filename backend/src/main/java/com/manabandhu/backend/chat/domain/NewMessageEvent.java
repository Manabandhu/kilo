package com.manabandhu.backend.chat.domain;

import org.springframework.context.ApplicationEvent;

public class NewMessageEvent extends ApplicationEvent {

    private final Object messageId;
    private final Object conversationId;
    private final Object senderId;

    public NewMessageEvent(Object source, Object messageId, Object conversationId, Object senderId) {
        super(source);
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderId = senderId;
    }

    public Object getMessageId() { return messageId; }
    public Object getConversationId() { return conversationId; }
    public Object getSenderId() { return senderId; }
}
