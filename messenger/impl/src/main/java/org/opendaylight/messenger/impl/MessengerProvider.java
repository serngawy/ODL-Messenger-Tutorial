/*
 * Copyright © 2016 me and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.messenger.impl;

import java.util.List;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.NotificationService;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rev150105.Messenger;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rev150105.MessengerBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rev150105.MessengerConnection;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rev150105.MessengerListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rev150105.messenger.Message;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rev150105.messenger.MessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rev150105.messenger.MessageKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessengerProvider implements MessengerListener {

    private static final Logger LOG = LoggerFactory.getLogger(MessengerProvider.class);
    private static final String MESSENGER_DATA_TREE = "Messenger:1";
    private final MessageDataTreeChangeListener datatree;

    private final DataBroker dataBroker;

    public MessengerProvider(final DataBroker dataBroker, final NotificationService notificationSrv) {
        this.dataBroker = dataBroker;
        notificationSrv.registerNotificationListener(this);
        datatree = new MessageDataTreeChangeListener(dataBroker);
    }

    /**
     * Method called when the blueprint container is created.
     */
    public void init() {
        LOG.info("MessengerProvider Session Initiated");
        initializeMessengerDataTree();
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
        LOG.info("MessengerProvider Closed");
        try {
            datatree.close();
        } catch (Exception e) {
            LOG.error("data tree close ", e);
        }
    }

    private void initializeMessengerDataTree() {
        if (MessengerMdsalUtils.read(dataBroker, LogicalDatastoreType.CONFIGURATION, MessengerMdsalUtils.getMessengerIid()) == null) {
            final Messenger messengerData = new MessengerBuilder().setId(MESSENGER_DATA_TREE).build();
            MessengerMdsalUtils.initalizeDatastore(LogicalDatastoreType.CONFIGURATION, dataBroker, MessengerMdsalUtils.getMessengerIid(), messengerData);
            MessengerMdsalUtils.initalizeDatastore(LogicalDatastoreType.OPERATIONAL, dataBroker, MessengerMdsalUtils.getMessengerIid(), messengerData);
        }
    }

    public String getlastMessageDatatime() {
        final Messenger messenger = MessengerMdsalUtils.read(dataBroker, LogicalDatastoreType.OPERATIONAL, MessengerMdsalUtils.getMessengerIid());
        return messenger.getLastMessageDatetime();
    }

    public String getMessage(String MessageId) {
        final Messenger messenger = MessengerMdsalUtils.read(dataBroker, LogicalDatastoreType.CONFIGURATION, MessengerMdsalUtils.getMessengerIid());
        for (Message mess : messenger.getMessage()) {
            if (mess.getMessId().equals(MessageId)) {
                return mess.getText();
            }
        }
        return null;
    }

    public boolean isConnected() {
        final Messenger messenger = MessengerMdsalUtils.read(dataBroker, LogicalDatastoreType.OPERATIONAL, MessengerMdsalUtils.getMessengerIid());
        return messenger.isConnected();
    }

    public List<Message> getMessages() {
        final Messenger messenger = MessengerMdsalUtils.read(dataBroker, LogicalDatastoreType.CONFIGURATION, MessengerMdsalUtils.getMessengerIid());
        return messenger.getMessage();
    }

    public boolean sendMessage(String id, String source, String dest, String txt) {
        if (isConnected()) {
            final MessageKey messKey = new MessageKey(id);
            final Message mess = new MessageBuilder().setKey(messKey)
                                        .setMessageDest(dest)
                                        .setMessageSource(source)
                                        .setMessId(id)
                                        .setText(txt)
                                        .build();
            return MessengerMdsalUtils.put(dataBroker, LogicalDatastoreType.CONFIGURATION, MessengerMdsalUtils.getMessageIid(messKey), mess);
        }
        LOG.info("Messenger has been disconnected");
        return false;
    }

    @Override
    public void onMessengerConnection(MessengerConnection notification) {
        LOG.info("Notification to change the messenger connection.");
        final Messenger messenger = MessengerMdsalUtils.read(dataBroker, LogicalDatastoreType.OPERATIONAL, MessengerMdsalUtils.getMessengerIid());
        final MessengerBuilder messengerBld = new MessengerBuilder(messenger).setConnected(notification.isConnected());
        MessengerMdsalUtils.merge(dataBroker, LogicalDatastoreType.OPERATIONAL, MessengerMdsalUtils.getMessengerIid(), messengerBld.build());
    }

}