
package com.pcmonitor.service;

import com.pcmonitor.dto.NetworkMonitoringResponse;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class NetworkMonitoringService {

    private final SystemInfo systemInfo;

    public NetworkMonitoringService() {
        this.systemInfo = new SystemInfo();
    }

    public List<NetworkMonitoringResponse> getNetworkInformation() {

        List<NetworkMonitoringResponse> results =
                new ArrayList<>();

        List<NetworkIF> networkInterfaces =
                systemInfo.getHardware().getNetworkIFs();

        for (NetworkIF network : networkInterfaces) {

            // Exclude virtual and duplicate filter interfaces
            if (!isRelevantInterface(network)) {
                continue;
            }

            network.updateAttributes();

            String interfaceName = network.getName();

            String displayName = network.getDisplayName();

            String macAddress = network.getMacaddr();

            String[] ipAddresses = network.getIPv4addr();

            String ipv4Address =
                    ipAddresses.length > 0
                            ? ipAddresses[0]
                            : "Not Available";

            String[] ipv6Addresses = network.getIPv6addr();

            String ipv6Address =
                    ipv6Addresses.length > 0
                            ? ipv6Addresses[0]
                            : "Not Available";

            boolean connected =
                    network.getIfOperStatus()
                            == NetworkIF.IfOperStatus.UP;

            long bytesReceived = network.getBytesRecv();

            long bytesSent = network.getBytesSent();

            long packetsReceived = network.getPacketsRecv();

            long packetsSent = network.getPacketsSent();

            String status = connected ? "UP" : "DOWN";

            results.add(new NetworkMonitoringResponse(
                    interfaceName,
                    displayName,
                    macAddress,
                    ipv4Address,
                    ipv6Address,
                    connected,
                    bytesReceived,
                    bytesSent,
                    packetsReceived,
                    packetsSent,
                    status
            ));
        }

        return results;
    }

    private boolean isRelevantInterface(NetworkIF network) {

        String name = network.getName()
                .toLowerCase(Locale.ROOT);

        String displayName = network.getDisplayName()
                .toLowerCase(Locale.ROOT);

        // Exclude Windows filter and virtual adapters
        if (displayName.contains("wfp")
                || displayName.contains("qos packet scheduler")
                || displayName.contains("native wifi filter driver")
                || displayName.contains("wi-fi direct")
                || displayName.contains("wifi direct")
                || displayName.contains("bluetooth device (personal area network)")
                || displayName.contains("loopback")
                || displayName.contains("virtual wifi filter")
        ) {

            return false;
        }

        // Exclude loopback interfaces by name
        return !name.equals("lo")
                && !name.startsWith("loopback");
    }
}
