package io.pinnacl.academics.application.data.notification;

import java.util.List;
import java.util.UUID;

public interface DeliveryChannelMessage {
    UUID id();

    String text();

    String about();

    List<DeliveryRecipient> recipients();

    List<DeliveryRecipient> ccRecipients();
}
