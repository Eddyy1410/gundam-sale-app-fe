package com.huyntd.superapp.gundamshop_mobilefe.models.response;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    private int conversationId;
    private String customerName;
    private String latestMessageContent;
    private Date latestMessageSentAt;
    private int customerId;
    private int lastestSenderId;
}
