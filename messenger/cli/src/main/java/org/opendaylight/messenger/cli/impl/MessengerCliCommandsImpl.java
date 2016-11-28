/*
 * Copyright © 2016 me and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messenger.cli.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opendaylight.messenger.cli.api.MessengerCliCommands;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rev150105.MessengerConnection;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rev150105.MessengerConnectionBuilder;

public class MessengerCliCommandsImpl implements MessengerCliCommands {

    private static final Logger LOG = LoggerFactory.getLogger(MessengerCliCommandsImpl.class);
    private final DataBroker dataBroker;
    private final NotificationPublishService notificationSrv;

    public MessengerCliCommandsImpl(final DataBroker db, final NotificationPublishService notificationSrv) {
        this.dataBroker = db;
        this.notificationSrv = notificationSrv;
        LOG.info("MessengerCliCommandImpl initialized");
    }

    @Override
    public Object testCommand(Object testArgument) {
        MessengerConnection messConn;
        if (testArgument.equals("connect")) {
            messConn = new MessengerConnectionBuilder().setConnected(true).build();
            notificationSrv.offerNotification(messConn);
            return "Messenger connected";
        } else if (testArgument.equals("disconnect")) {
            messConn = new MessengerConnectionBuilder().setConnected(false).build();
            notificationSrv.offerNotification(messConn);
            return "Messenger disconnected";
        }
        return "Not vaild status";
    }
}