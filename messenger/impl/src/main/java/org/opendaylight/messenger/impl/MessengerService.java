/*
 * Copyright © 2016 me and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.messenger.impl;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rpc.rev150105.GetLastMessageDatetimeOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rpc.rev150105.GetLastMessageDatetimeOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rpc.rev150105.MessengerRpcService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rpc.rev150105.SendMessageInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rpc.rev150105.SendMessageOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rpc.rev150105.SendMessageOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;

public class MessengerService implements MessengerRpcService {

    private final MessengerProvider messProv;
    
    public MessengerService(final MessengerProvider messengerProvider) {
        messProv = messengerProvider;
    }

    @Override
    public Future<RpcResult<GetLastMessageDatetimeOutput>> getLastMessageDatetime() {
        final String messDatetime = messProv.getlastMessageDatatime();
        if (messDatetime != null && !messDatetime.isEmpty()) {
            final GetLastMessageDatetimeOutput messOutput = new GetLastMessageDatetimeOutputBuilder()
                                                        .setMessageDatetime(messDatetime)
                                                        .build();
            return Futures.immediateFuture(RpcResultBuilder.<GetLastMessageDatetimeOutput> success(messOutput).build());
        } else {
            return Futures.immediateFuture(RpcResultBuilder.<GetLastMessageDatetimeOutput> failed().build());
        }
    }

    @Override
    public Future<RpcResult<SendMessageOutput>> sendMessage(SendMessageInput input) {
        final SettableFuture<RpcResult<SendMessageOutput>> futureResult = SettableFuture.create();
        if (messProv.sendMessage(input.getMessId(), input.getMessageSource(),
                input.getMessageDest(), input.getText())) {
            final SendMessageOutput messOutput = new SendMessageOutputBuilder()
                                                        .setMessageId(input.getMessId())
                                                        .build();
            futureResult.set(RpcResultBuilder.<SendMessageOutput> success(messOutput).build());
        } else {
            futureResult.set(RpcResultBuilder.<SendMessageOutput> failed().build());
        }
        return futureResult;
    }
}
