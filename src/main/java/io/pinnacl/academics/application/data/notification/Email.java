package io.pinnacl.academics.application.data.notification;

import java.util.List;
import java.util.UUID;

public record Email(String text, String about, List<DeliveryRecipient> recipients)
                   implements DeliveryChannelMessage {

    @Override
    public UUID id() {
        return null;
    }

    @Override
    public List<DeliveryRecipient> ccRecipients() {
        return List.of();
    }
}
