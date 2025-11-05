package com.huyntd.superapp.gundamshop_mobilefe.models.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateReadMessageRequest {
    int receiverId;
    int conversationId;
}
