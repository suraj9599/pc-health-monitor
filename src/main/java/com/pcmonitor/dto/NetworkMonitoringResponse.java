
package com.pcmonitor.dto;

public record NetworkMonitoringResponse(

        String interfaceName,
        String displayName,
        String macAddress,
        String ipv4Address,
        String ipv6Address,
        boolean connected,
        long bytesReceived,
        long bytesSent,
        long packetsReceived,
        long packetsSent,
        String status

) {
}
