package com.FAuth.proauth.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection="system_metric")
@Data
public class SystemMetrics {
    @Id
    private ObjectId id;
    private LocalDateTime timestamp;
    private double cpuUsage;
    private double memoryUsage;
    private long responseTime;
    private long requestCount;
    private int activeThreads;
    private String status;
}
