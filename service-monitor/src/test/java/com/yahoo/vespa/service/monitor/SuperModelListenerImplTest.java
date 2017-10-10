// Copyright 2017 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.vespa.service.monitor;

import com.yahoo.config.model.api.ApplicationInfo;
import com.yahoo.config.model.api.SuperModel;
import com.yahoo.config.model.api.SuperModelProvider;
import com.yahoo.config.provision.Zone;
import com.yahoo.vespa.service.monitor.internal.ServiceMonitorMetrics;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SuperModelListenerImplTest {
    @Test
    public void sanityCheck() {
        SlobrokMonitorManager slobrokMonitorManager = mock(SlobrokMonitorManager.class);
        ServiceMonitorMetrics metrics = mock(ServiceMonitorMetrics.class);
        ModelGenerator modelGenerator = mock(ModelGenerator.class);
        SuperModelListenerImpl listener = new SuperModelListenerImpl(
                slobrokMonitorManager,
                metrics,
                modelGenerator);

        SuperModelProvider superModelProvider = mock(SuperModelProvider.class);
        SuperModel superModel = mock(SuperModel.class);
        when(superModelProvider.snapshot(listener)).thenReturn(superModel);

        ApplicationInfo application1 = mock(ApplicationInfo.class);
        ApplicationInfo application2 = mock(ApplicationInfo.class);
        List<ApplicationInfo> applications = Stream.of(application1, application2)
                .collect(Collectors.toList());
        when(superModel.getAllApplicationInfos()).thenReturn(applications);

        listener.start(superModelProvider);
        verify(slobrokMonitorManager).applicationActivated(superModel, application1);
        verify(slobrokMonitorManager).applicationActivated(superModel, application2);

        Zone zone = mock(Zone.class);
        List<String> configServers = new ArrayList<>();
        ServiceModel serviceModel = listener.createServiceModelSnapshot(zone, configServers);
        verify(modelGenerator).toServiceModel(superModel, zone, configServers, slobrokMonitorManager);
    }
}