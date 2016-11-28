/*
 * Copyright © 2016 me and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.messenger.impl;

import java.util.Collection;
import java.util.Date;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rev150105.Messenger;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rev150105.messenger.Message;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rev150105.MessengerBuilder;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class MessageDataTreeChangeListener implements DataTreeChangeListener<Message>, AutoCloseable {

    protected final DataBroker dataBroker;
    private final ListenerRegistration<MessageDataTreeChangeListener> listener;

    public MessageDataTreeChangeListener(final DataBroker dataBroker) {
        this.dataBroker = dataBroker;
        final DataTreeIdentifier<Message> dataTreeIid =
                new DataTreeIdentifier<>(LogicalDatastoreType.CONFIGURATION, MessengerMdsalUtils.getMessageIid());
        listener = dataBroker.registerDataTreeChangeListener(dataTreeIid, this);
    }

    @Override
    public void onDataTreeChanged(Collection<DataTreeModification<Message>> changes) {
        for (final DataTreeModification<Message> change : changes) {
            final InstanceIdentifier<Message> identifier = change.getRootPath().getRootIdentifier();
            final DataObjectModification<Message> root = change.getRootNode();
            switch (root.getModificationType()) {
                case DELETE:
                    //To Do
                    break;
                case SUBTREE_MODIFIED:
                    //To Do
                    break;
                case WRITE:
                    add(identifier, root.getDataAfter());
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled modification type "
                            + root.getModificationType());
            }
        }
    }

    protected void add(InstanceIdentifier<Message> identifier, Message add) throws RuntimeException {
        MessengerMdsalUtils.put(dataBroker, LogicalDatastoreType.OPERATIONAL, identifier, add);
        final Messenger messenger = MessengerMdsalUtils.read(dataBroker, LogicalDatastoreType.OPERATIONAL, MessengerMdsalUtils.getMessengerIid());
        final MessengerBuilder messengerBld = new MessengerBuilder(messenger).setLastMessageDatetime(new Date().toString());
        MessengerMdsalUtils.merge(dataBroker, LogicalDatastoreType.OPERATIONAL, MessengerMdsalUtils.getMessengerIid(), messengerBld.build());
    }

    @Override
    public void close() throws Exception {
        listener.close();
    }
}
