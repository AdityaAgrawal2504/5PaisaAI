package com.omqs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OfflineMessageQueueingServiceApplication_OMQS1 {

    public static void main(String[] args) {
        SpringApplication.run(OfflineMessageQueueingServiceApplication_OMQS1.class, args);
    }

}
```
```java
// src/main/java/com/omqs/config/SchedulingConfig_OMQS1.java