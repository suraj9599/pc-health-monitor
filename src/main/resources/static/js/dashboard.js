
const API = {
    health: "/api/monitoring/health",
    network: "/api/monitoring/network",
    system: "/api/system/info",
    alerts: "/api/alerts"
};

// Helper: safely update text
function setText(id, value) {
    const element = document.getElementById(id);

    if (element) {
        element.textContent = value ?? "--";
    }
}

// Helper: format bytes into GiB
function formatGiB(bytes) {
    if (bytes == null) return "--";

    return (bytes / (1024 ** 3)).toFixed(2) + " GiB";
}

// Helper: update metric cards
function updateMetric(name, value, status) {
    const percentage = Number(value) || 0;
    const safePercentage = Math.min(100, Math.max(0, percentage));

    setText(`${name}-value`, `${percentage.toFixed(2)}%`);
    setText(`${name}-status`, status);

    const progress = document.getElementById(`${name}-progress`);

    if (progress) {
        progress.style.width = `${safePercentage}%`;

        if (status === "CRITICAL") {
            progress.style.background = "#f06464";
        } else if (status === "WARNING") {
            progress.style.background = "#f5b942";
        } else {
            progress.style.background = "#22c58b";
        }
    }

    const statusElement = document.getElementById(`${name}-status`);

    if (statusElement) {
        statusElement.className = `metric-status status-${String(status).toLowerCase()}`;
    }
}

// Fetch health information
async function loadHealth() {
    const response = await fetch(API.health, { cache: "no-store" });

    if (!response.ok) {
        throw new Error("Health API request failed");
    }

    const data = await response.json();

    updateMetric("cpu", data.cpuUsagePercent, data.cpuStatus);
    updateMetric("ram", data.ramUsagePercent, data.ramStatus);
    updateMetric("disk", data.diskUsagePercent, data.diskStatus);

    setText("overall-status", data.overallStatus);

    const overall = document.getElementById("overall-status");

    if (overall) {
        overall.className = `status-${String(data.overallStatus).toLowerCase()}`;
    }

    setText("ram-used", `${data.ramUsedGB} GB`);
    setText("ram-available", `${data.ramAvailableGB} GB`);
    setText("disk-free", `${data.diskFreeGB} GB`);
    setText("disk-detail-usage", `${data.diskUsagePercent}%`);
}

// Fetch network information
async function loadNetwork() {
    const response = await fetch(API.network, { cache: "no-store" });

    if (!response.ok) {
        throw new Error("Network API request failed");
    }

    const networks = await response.json();
    const container = document.getElementById("network-container");

    container.replaceChildren();

    if (!networks.length) {
        container.textContent = "No network adapters found.";
        return;
    }

    networks.forEach(network => {
        const card = document.createElement("div");
        card.className = "network-card";

        const title = document.createElement("h3");
        title.textContent = network.displayName || network.interfaceName || "Network Adapter";

        const details = [
            ["Interface", network.interfaceName],
            ["IPv4 Address", network.ipv4Address],
            ["Connection", network.connected ? "Connected" : "Disconnected"],
            ["Status", network.status],
            ["Received", formatGiB(network.bytesReceived)],
            ["Sent", formatGiB(network.bytesSent)]
        ];

        card.appendChild(title);

        details.forEach(([label, value]) => {
            const paragraph = document.createElement("p");
            paragraph.textContent = `${label}: ${value ?? "--"}`;
            card.appendChild(paragraph);
        });

        container.appendChild(card);
    });
}

// Fetch system information
async function loadSystemInfo() {
    const response = await fetch(API.system, { cache: "no-store" });

    if (!response.ok) {
        throw new Error("System information API request failed");
    }

    const data = await response.json();
    const container = document.getElementById("system-info-container");

    container.replaceChildren();

    const details = [
        ["Computer Name", data.computerName],
        ["Manufacturer", data.manufacturer],
        ["Model", data.model],
        ["Operating System", data.operatingSystem],
        ["OS Version", data.osVersion],
        ["Architecture", data.architecture],
        ["Processor", data.processorName],
        ["Physical Cores", data.physicalCores],
        ["Logical Cores", data.logicalCores],
        ["Total RAM", formatGiB(data.totalMemoryBytes)],
        ["Total Disk", formatGiB(data.totalDiskBytes)],
        ["Java Version", data.javaVersion]
    ];

    details.forEach(([label, value]) => {
        const paragraph = document.createElement("p");

        const strong = document.createElement("strong");
        strong.textContent = `${label}: `;

        paragraph.appendChild(strong);
        paragraph.appendChild(document.createTextNode(value ?? "--"));

        container.appendChild(paragraph);
    });
}


// ========================================
// ALERT MANAGEMENT
// ========================================

async function loadAlerts() {

    const container = document.getElementById("alerts-container");
    const count = document.getElementById("alert-count");

    if (!container) return;

    const metricType =
        document.getElementById("alert-metric-filter")?.value || "";

    const status =
        document.getElementById("alert-status-filter")?.value || "";

    const params = new URLSearchParams();

    if (metricType) params.append("metricType", metricType);
    if (status) params.append("status", status);

    const url = `${API.alerts}/filter?${params.toString()}`;

    container.textContent = "Loading alerts...";

    try {

        const response = await fetch(url, { cache: "no-store" });

        if (!response.ok) {
            throw new Error("Unable to fetch alerts");
        }

        const alerts = await response.json();

        container.replaceChildren();

        if (count) {
            count.textContent = `${alerts.length} alerts found`;
        }

        if (alerts.length === 0) {
            container.textContent = "No alerts found for the selected filters.";
            return;
        }

        alerts.forEach(alert => {

            const row = document.createElement("div");
            row.className = "alert-row";

            const content = document.createElement("div");

            const message = document.createElement("p");
            message.className = "alert-message";
            message.textContent =
                `${alert.metricType}: ${alert.message}`;

            const time = document.createElement("span");
            time.className = "alert-time";
            time.textContent =
                new Date(alert.createdAt).toLocaleString();

            const value = document.createElement("span");
            value.className = "alert-time";

            value.textContent = alert.metricValue == null
                ? "Metric value: N/A"
                : `Value: ${alert.metricValue}%`;

            content.appendChild(message);
            content.appendChild(value);
            content.appendChild(document.createElement("br"));
            content.appendChild(time);

            const badge = document.createElement("span");
            badge.className = `alert-badge ${alert.status}`;
            badge.textContent = alert.status;

            row.appendChild(content);
            row.appendChild(badge);

            container.appendChild(row);
        });

    } catch (error) {

        console.error("Alert management error:", error);

        container.textContent = "Unable to load alerts.";

        if (count) {
            count.textContent = "Error";
        }
    }
}


// Apply filters
document.getElementById("apply-alert-filters")
    ?.addEventListener("click", loadAlerts);


// Reset filters
document.getElementById("reset-alert-filters")
    ?.addEventListener("click", () => {

        document.getElementById("alert-metric-filter").value = "";
        document.getElementById("alert-status-filter").value = "";

        loadAlerts();
    });


// Update dashboard connection status
function setConnectionStatus(connected) {
    const element = document.getElementById("connection-status");

    if (!element) return;

    element.textContent = connected
        ? "● Connected"
        : "● Connection Error";

    element.style.color = connected ? "#22c58b" : "#f06464";
}

// Load frequently changing information
async function refreshDashboard() {
    const results = await Promise.allSettled([
        loadHealth(),
        loadNetwork(),
        loadAlerts()
    ]);

    const healthLoaded = results[0].status === "fulfilled";
    const networkLoaded = results[1].status === "fulfilled";
    const alertsLoaded = results[2].status === "fulfilled";

    setConnectionStatus(
        healthLoaded && networkLoaded && alertsLoaded
    );

    if (results.some(result => result.status === "rejected")) {
        console.error("Some dashboard APIs failed:", results);
    }

    if (healthLoaded) {
        setText("last-updated", `Last updated: ${new Date().toLocaleTimeString()}`);
    }
}

// Initialize dashboard
async function initializeDashboard() {
    try {
        await loadSystemInfo();
    } catch (error) {
        console.error("System information error:", error);
        setText("system-info-container", "Unable to load system information.");
    }

    await refreshDashboard();

    // Refresh live monitoring every 10 seconds
    setInterval(refreshDashboard, 10000);
}

document.addEventListener("DOMContentLoaded", initializeDashboard);



// ========================================
// HISTORICAL PERFORMANCE CHART
// ========================================

let performanceChart = null;

function formatLocalDateTime(date) {
    const pad = value => String(value).padStart(2, "0");

    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
        + `T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
}

async function loadPerformanceChart() {

    const rangeSelect = document.getElementById("chart-range");
    const message = document.getElementById("chart-message");

    if (!rangeSelect || !message) return;

    const hours = Number(rangeSelect.value);

    const to = new Date();
    const from = new Date(to.getTime() - hours * 60 * 60 * 1000);

    const url = `/api/metrics/history?from=${encodeURIComponent(formatLocalDateTime(from))}&to=${encodeURIComponent(formatLocalDateTime(to))}`;

    message.textContent = "Loading historical data...";

    try {

        const response = await fetch(url, { cache: "no-store" });

        if (!response.ok) {
            throw new Error("Unable to fetch historical metrics");
        }

        const records = await response.json();

        if (!records.length) {
            message.textContent = "No historical data available for this period.";

            if (performanceChart) {
                performanceChart.destroy();
                performanceChart = null;
            }

            return;
        }

        const labels = records.map(record =>
            new Date(record.recordedAt).toLocaleString()
        );

        const cpuData = records.map(record => record.cpuUsage);
        const ramData = records.map(record => record.ramUsage);
        const diskData = records.map(record => record.diskUsage);

        const canvas = document.getElementById("performance-chart");

        if (performanceChart) {
            performanceChart.destroy();
        }

        performanceChart = new Chart(canvas, {
            type: "line",

            data: {
                labels: labels,

                datasets: [
                    {
                        label: "CPU",
                        data: cpuData,
                        borderColor: "#4d9cff",
                        backgroundColor: "rgba(77, 156, 255, 0.10)",
                        fill: false,
                        tension: 0.35,
                        pointRadius: 0,
                        borderWidth: 2
                    },
                    {
                        label: "RAM",
                        data: ramData,
                        borderColor: "#f5b942",
                        backgroundColor: "rgba(245, 185, 66, 0.10)",
                        fill: false,
                        tension: 0.35,
                        pointRadius: 0,
                        borderWidth: 2
                    },
                    {
                        label: "Disk",
                        data: diskData,
                        borderColor: "#22c58b",
                        backgroundColor: "rgba(34, 197, 139, 0.10)",
                        fill: false,
                        tension: 0.35,
                        pointRadius: 0,
                        borderWidth: 2
                    }
                ]
            },

            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: {
                    intersect: false,
                    mode: "index"
                },

                plugins: {
                    legend: {
                        labels: {
                            color: "#edf2fa",
                            usePointStyle: true,
                            padding: 20
                        }
                    },

                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return `${context.dataset.label}: ${Number(context.raw).toFixed(2)}%`;
                            }
                        }
                    }
                },

                scales: {
                    x: {
                        ticks: {
                            color: "#91a0b8",
                            maxTicksLimit: 8,
                            maxRotation: 0
                        },
                        grid: {
                            color: "rgba(145, 160, 184, 0.08)"
                        }
                    },

                    y: {
                        min: 0,
                        max: 100,

                        ticks: {
                            color: "#91a0b8",
                            callback: value => value + "%"
                        },

                        grid: {
                            color: "rgba(145, 160, 184, 0.10)"
                        }
                    }
                }
            }
        });

        message.textContent =
            `Showing ${records.length} historical monitoring records.`;

    } catch (error) {

        console.error("Performance chart error:", error);

        message.textContent =
            "Unable to load historical performance data.";
    }
}

// Load charts when the page initializes
document.addEventListener("DOMContentLoaded", () => {

    const rangeSelect = document.getElementById("chart-range");

    if (rangeSelect) {
        rangeSelect.addEventListener("change", () => {
            loadPerformanceChart();
            loadNetworkTrafficChart();
        });

        loadPerformanceChart();
        loadNetworkTrafficChart();
    }

});


// ========================================
// NETWORK TRAFFIC HISTORY
// ========================================

let networkChart = null;

async function loadNetworkTrafficChart() {

    const message = document.getElementById("network-chart-message");
    const rangeSelect = document.getElementById("chart-range");

    if (!message || !rangeSelect) return;

    const hours = Number(rangeSelect.value);

    const to = new Date();
    const from = new Date(to.getTime() - hours * 60 * 60 * 1000);

    const url = `/api/metrics/history?from=${encodeURIComponent(formatLocalDateTime(from))}&to=${encodeURIComponent(formatLocalDateTime(to))}`;

    message.textContent = "Loading network history...";

    try {

        const response = await fetch(url, { cache: "no-store" });

        if (!response.ok) {
            throw new Error("Network history API request failed");
        }

        const records = await response.json();

        if (records.length < 2) {
            message.textContent =
                "At least two historical records are needed to calculate network speed.";

            setText("download-speed", "-- Mbps");
            setText("upload-speed", "-- Mbps");

            if (networkChart) {
                networkChart.destroy();
                networkChart = null;
            }

            return;
        }

        // History API returns records in chronological order.
        const downloadData = [];
        const uploadData = [];
        const labels = [];

        for (let i = 1; i < records.length; i++) {

            const previous = records[i - 1];
            const current = records[i];

            const previousTime = new Date(previous.recordedAt).getTime();
            const currentTime = new Date(current.recordedAt).getTime();

            const elapsedSeconds =
                (currentTime - previousTime) / 1000;

            if (elapsedSeconds <= 0) continue;

            const receivedBytes =
                Math.max(0,
                    current.networkBytesReceived -
                    previous.networkBytesReceived
                );

            const sentBytes =
                Math.max(0,
                    current.networkBytesSent -
                    previous.networkBytesSent
                );

            // Convert bytes per second to megabits per second.
            const downloadMbps =
                (receivedBytes * 8) / elapsedSeconds / 1_000_000;

            const uploadMbps =
                (sentBytes * 8) / elapsedSeconds / 1_000_000;

            downloadData.push(downloadMbps);
            uploadData.push(uploadMbps);

            labels.push(
                new Date(current.recordedAt).toLocaleTimeString()
            );
        }

        if (downloadData.length === 0) {
            message.textContent = "No valid network intervals found.";
            return;
        }

        // Display the latest calculated transfer speeds.
        const latestDownload = downloadData[downloadData.length - 1];
        const latestUpload = uploadData[uploadData.length - 1];

        setText("download-speed", `${latestDownload.toFixed(3)} Mbps`);
        setText("upload-speed", `${latestUpload.toFixed(3)} Mbps`);

        const canvas = document.getElementById("network-chart");

        if (networkChart) {
            networkChart.destroy();
        }

        networkChart = new Chart(canvas, {

            type: "line",

            data: {
                labels: labels,

                datasets: [
                    {
                        label: "Download",
                        data: downloadData,
                        borderColor: "#4d9cff",
                        backgroundColor: "rgba(77, 156, 255, 0.10)",
                        borderWidth: 2,
                        pointRadius: 0,
                        tension: 0.35
                    },
                    {
                        label: "Upload",
                        data: uploadData,
                        borderColor: "#22c58b",
                        backgroundColor: "rgba(34, 197, 139, 0.10)",
                        borderWidth: 2,
                        pointRadius: 0,
                        tension: 0.35
                    }
                ]
            },

            options: {
                responsive: true,
                maintainAspectRatio: false,

                interaction: {
                    intersect: false,
                    mode: "index"
                },

                plugins: {
                    legend: {
                        labels: {
                            color: "#edf2fa",
                            usePointStyle: true,
                            padding: 20
                        }
                    },

                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return `${context.dataset.label}: ${context.raw.toFixed(3)} Mbps`;
                            }
                        }
                    }
                },

                scales: {
                    x: {
                        ticks: {
                            color: "#91a0b8",
                            maxTicksLimit: 8,
                            maxRotation: 0
                        },
                        grid: {
                            color: "rgba(145, 160, 184, 0.08)"
                        }
                    },

                    y: {
                        beginAtZero: true,

                        ticks: {
                            color: "#91a0b8",
                            callback: value => value + " Mbps"
                        },

                        grid: {
                            color: "rgba(145, 160, 184, 0.10)"
                        }
                    }
                }
            }
        });

        message.textContent =
            `Showing ${downloadData.length} calculated network intervals.`;

    } catch (error) {

        console.error("Network chart error:", error);

        message.textContent =
            "Unable to load network traffic history.";
    }
}


// ========================================
// CSV REPORT EXPORT
// ========================================

// Convert data into downloadable CSV
function downloadCSV(filename, headers, rows) {

    function escapeCSV(value) {
        const text = value == null ? "" : String(value);

        return `"${text.replace(/"/g, '""')}"`;
    }

    const csvContent = [
        headers.map(escapeCSV).join(","),
        ...rows.map(row => row.map(escapeCSV).join(","))
    ].join("\r\n");

    // BOM helps Excel recognize UTF-8 correctly.
    const blob = new Blob(
        ["\uFEFF" + csvContent],
        { type: "text/csv;charset=utf-8;" }
    );

    const url = URL.createObjectURL(blob);

    const link = document.createElement("a");
    link.href = url;
    link.download = filename;

    document.body.appendChild(link);
    link.click();
    link.remove();

    URL.revokeObjectURL(url);
}


// Export historical monitoring metrics
async function exportMetricsCSV() {

    const button = document.getElementById("export-metrics-btn");

    try {

        button.disabled = true;
        button.textContent = "Exporting...";

        const hours = Number(
            document.getElementById("chart-range").value
        );

        const to = new Date();
        const from = new Date(
            to.getTime() - hours * 60 * 60 * 1000
        );

        const url =
            `/api/metrics/history?from=${encodeURIComponent(formatLocalDateTime(from))}` +
            `&to=${encodeURIComponent(formatLocalDateTime(to))}`;

        const response = await fetch(url, { cache: "no-store" });

        if (!response.ok) {
            throw new Error("Unable to retrieve metric history.");
        }

        const records = await response.json();

        if (records.length === 0) {
            alert("No monitoring records available for this period.");
            return;
        }

        const headers = [
            "ID",
            "Recorded At",
            "CPU Usage (%)",
            "RAM Usage (%)",
            "Disk Usage (%)",
            "RAM Used (GB)",
            "RAM Available (GB)",
            "Disk Free (GB)",
            "Network Bytes Received",
            "Network Bytes Sent",
            "Overall Status"
        ];

        const rows = records.map(record => [
            record.id,
            record.recordedAt,
            record.cpuUsage,
            record.ramUsage,
            record.diskUsage,
            record.ramUsedGb,
            record.ramAvailableGb,
            record.diskFreeGb,
            record.networkBytesReceived,
            record.networkBytesSent,
            record.overallStatus
        ]);

        downloadCSV(
            `PC_Monitor_Metrics_${new Date().toISOString().slice(0, 10)}.csv`,
            headers,
            rows
        );

    } catch (error) {

        console.error("Metrics export error:", error);
        alert("Unable to export monitoring metrics.");

    } finally {

        button.disabled = false;
        button.textContent = "↓ Export Metrics";
    }
}


// Export alert history using the currently selected filters
async function exportAlertsCSV() {

    const button = document.getElementById("export-alerts-btn");

    try {

        button.disabled = true;
        button.textContent = "Exporting...";

        const metricType =
            document.getElementById("alert-metric-filter").value;

        const status =
            document.getElementById("alert-status-filter").value;

        const params = new URLSearchParams();

        if (metricType) {
            params.append("metricType", metricType);
        }

        if (status) {
            params.append("status", status);
        }

        const url = `${API.alerts}/filter?${params.toString()}`;

        const response = await fetch(url, { cache: "no-store" });

        if (!response.ok) {
            throw new Error("Unable to retrieve alert history.");
        }

        const alerts = await response.json();

        if (alerts.length === 0) {
            alert("No alerts available for the selected filters.");
            return;
        }

        const headers = [
            "ID",
            "Created At",
            "Component",
            "Status",
            "Message",
            "Metric Value",
            "Threshold Value"
        ];

        const rows = alerts.map(alert => [
            alert.id,
            alert.createdAt,
            alert.metricType,
            alert.status,
            alert.message,
            alert.metricValue,
            alert.thresholdValue
        ]);

        downloadCSV(
            `PC_Monitor_Alerts_${new Date().toISOString().slice(0, 10)}.csv`,
            headers,
            rows
        );

    } catch (error) {

        console.error("Alerts export error:", error);
        alert("Unable to export alert history.");

    } finally {

        button.disabled = false;
        button.textContent = "↓ Export Alerts";
    }
}


// Register export button events
document.addEventListener("DOMContentLoaded", () => {

    document.getElementById("export-metrics-btn")
        ?.addEventListener("click", exportMetricsCSV);

    document.getElementById("export-alerts-btn")
        ?.addEventListener("click", exportAlertsCSV);

});
