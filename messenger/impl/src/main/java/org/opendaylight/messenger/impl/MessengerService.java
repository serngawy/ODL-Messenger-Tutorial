/*
 * Copyright © 2016 me and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.messenger.impl;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rpc.rev150105.GetLastMessegeDatetimeOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rpc.rev150105.GetLastMessegeDatetimeOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rpc.rev150105.MessengerRpcService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rpc.rev150105.SendMessegeInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rpc.rev150105.SendMessegeOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messenger.rpc.rev150105.SendMessegeOutputBuilder;
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
    public Future<RpcResult<GetLastMessegeDatetimeOutput>> getLastMessegeDatetime() {
        final String messDatetime = messProv.getlastMessegeDatatime();
        if (messDatetime != null && !messDatetime.isEmpty()) {
            final GetLastMessegeDatetimeOutput messOutput = new GetLastMessegeDatetimeOutputBuilder()
                                                        .setMessegeDatetime(messDatetime)
                                                        .build();
            return Futures.immediateFuture(RpcResultBuilder.<GetLastMessegeDatetimeOutput> success(messOutput).build());
        } else {
            return Futures.immediateFuture(RpcResultBuilder.<GetLastMessegeDatetimeOutput> failed().build());
        }
    }

    @Override
    public Future<RpcResult<SendMessegeOutput>> sendMessege(SendMessegeInput input) {
        final SettableFuture<RpcResult<SendMessegeOutput>> futureResult = SettableFuture.create();
        if (messProv.sendMessege(input.getMessId(), input.getMessegeSource(),
                input.getMessegeDest(), input.getText())) {
            final SendMessegeOutput messOutput = new SendMessegeOutputBuilder()
                                                        .setMessegeId(input.getMessId())
                                                        .build();
            futureResult.set(RpcResultBuilder.<SendMessegeOutput> success(messOutput).build());
        } else {
            futureResult.set(RpcResultBuilder.<SendMessegeOutput> failed().build());
        }
        return futureResult;
    }
}
