package com.example.fetchconversationsapi_v1.service;

import com.example.fetchconversationsapi_v1.dto.FetchConversationsRequestDto_FCA1;
import com.example.fetchconversationsapi_v1.dto.PaginatedConversationsResponseDto_FCA1;
import com.example.fetchconversationsapi_v1.exception.UnauthorizedException_FCA1;
import com.example.fetchconversationsapi_v1.mapper.ConversationMapper_FCA1;
import com.example.fetchconversationsapi_v1.model.ConversationEntity_FCA1;
import com.example.fetchconversationsapi_v1.repository.ConversationRepository_FCA1;
import com.example.fetchconversationsapi_v1.security.SecurityUtil_FCA1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationService_FCA1Test {

    @Mock
    private ConversationRepository_FCA1 conversationRepository;

    @Mock
    private ConversationMapper_FCA1 conversationMapper;

    @Mock
    private SecurityUtil_FCA1 securityUtil;

    @InjectMocks
    private ConversationService_FCA1 conversationService;

    private UUID testUserId;
    private FetchConversationsRequestDto_FCA1 requestDto;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        requestDto = new FetchConversationsRequestDto_FCA1(); // default values
    }

    @Test
    void fetchConversations_shouldSucceed_whenUserIsAuthenticated() {
        when(securityUtil.getCurrentUserId()).thenReturn(Optional.of(testUserId));

        Page<ConversationEntity_FCA1> page = new PageImpl<>(Collections.emptyList());
        when(conversationRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PaginatedConversationsResponseDto_FCA1 expectedResponse = PaginatedConversationsResponseDto_FCA1.builder().build();
        when(conversationMapper.toPaginatedResponseDto(page, testUserId)).thenReturn(expectedResponse);

        PaginatedConversationsResponseDto_FCA1 actualResponse = conversationService.fetchConversations(requestDto);

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(conversationRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(conversationMapper).toPaginatedResponseDto(eq(page), eq(testUserId));
    }

    @Test
    void fetchConversations_shouldThrowUnauthorizedException_whenUserIsNotAuthenticated() {
        when(securityUtil.getCurrentUserId()).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException_FCA1.class, () -> {
            conversationService.fetchConversations(requestDto);
        });
    }
}
```
```xml
<!-- Maven POM file -->
<!-- File: pom.xml -->
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>fetchconversationsapi_v1</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>fetchconversationsapi_v1</name>
    <description>Fetch Conversations API Implementation</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j2</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.5.0</version>
        </dependency>

        <!-- Jackson for structured logging -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>


        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```
```xml
<!-- Log4j2 Configuration -->
<!-- File: src/main/resources/log4j2.xml -->