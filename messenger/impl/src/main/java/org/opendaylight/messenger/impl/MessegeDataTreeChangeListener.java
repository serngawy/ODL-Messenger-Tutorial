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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rev150105.messenger.Messege;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rev150105.MessengerBuilder;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class MessegeDataTreeChangeListener implements DataTreeChangeListener<Messege>, AutoCloseable {

    protected final DataBroker dataBroker;
    private final ListenerRegistration<MessegeDataTreeChangeListener> listener;

    public MessegeDataTreeChangeListener(final DataBroker dataBroker) {
        this.dataBroker = dataBroker;
        final DataTreeIdentifier<Messege> dataTreeIid =
                new DataTreeIdentifier<>(LogicalDatastoreType.CONFIGURATION, MessengerMdsalUtils.getMessegeIid());
        listener = dataBroker.registerDataTreeChangeListener(dataTreeIid, this);
    }

    @Override
    public void onDataTreeChanged(Collection<DataTreeModification<Messege>> changes) {
        for (final DataTreeModification<Messege> change : changes) {
            final InstanceIdentifier<Messege> identifier = change.getRootPath().getRootIdentifier();
            final DataObjectModification<Messege> root = change.getRootNode();
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

    protected void add(InstanceIdentifier<Messege> identifier, Messege add) throws RuntimeException {
        MessengerMdsalUtils.put(dataBroker, LogicalDatastoreType.OPERATIONAL, identifier, add);
        final Messenger messenger = MessengerMdsalUtils.read(dataBroker, LogicalDatastoreType.OPERATIONAL, MessengerMdsalUtils.getMessengerIid());
        final MessengerBuilder messengerBld = new MessengerBuilder(messenger).setLastMessegeDatetime(new Date().toString());
        MessengerMdsalUtils.merge(dataBroker, LogicalDatastoreType.OPERATIONAL, MessengerMdsalUtils.getMessengerIid(), messengerBld.build());
    }

    @Override
    public void close() throws Exception {
        listener.close();
    }
}
