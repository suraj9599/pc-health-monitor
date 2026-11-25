
package com.pcmonitor.service;

import com.pcmonitor.dto.SystemInfoResponse;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

@Service
public class SystemInfoService {

    private final SystemInfo systemInfo;

    public SystemInfoService() {
        this.systemInfo = new SystemInfo();
    }

    public SystemInfoResponse getSystemInformation() {

        // Access hardware and operating system
        HardwareAbstractionLayer hardware =
                systemInfo.getHardware();

        OperatingSystem os =
                systemInfo.getOperatingSystem();

        // Computer details
        String computerName =
                os.getNetworkParams().getHostName();

        String manufacturer =
                hardware.getComputerSystem().getManufacturer();

        String model =
                hardware.getComputerSystem().getModel();

        // Operating system details
        String operatingSystem = os.getFamily();

        String osVersion =
                os.getVersionInfo().getVersion();

        String architecture =
                System.getProperty("os.arch");

        // Processor details
        CentralProcessor processor =
                hardware.getProcessor();

        String processorName =
                processor.getProcessorIdentifier().getName();

        int physicalCores =
                processor.getPhysicalProcessorCount();

        int logicalCores =
                processor.getLogicalProcessorCount();

        // RAM information
        GlobalMemory memory =
                hardware.getMemory();

        long totalMemoryBytes =
                memory.getTotal();

        long availableMemoryBytes =
                memory.getAvailable();

        // Disk information
        FileSystem fileSystem =
                os.getFileSystem();

        long totalDiskBytes = 0;
        long freeDiskBytes = 0;

        for (OSFileStore store : fileSystem.getFileStores()) {

            totalDiskBytes += store.getTotalSpace();
            freeDiskBytes += store.getUsableSpace();
        }

        // System uptime
        long uptimeSeconds =
                os.getSystemUptime();

        // Java version
        String javaVersion =
                System.getProperty("java.version");

        return new SystemInfoResponse(
                computerName,
                manufacturer,
                model,
                operatingSystem,
                osVersion,
                architecture,
                processorName,
                physicalCores,
                logicalCores,
                totalMemoryBytes,
                availableMemoryBytes,
                totalDiskBytes,
                freeDiskBytes,
                uptimeSeconds,
                javaVersion
        );
    }
}
