package com.socketsprint.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.socketsprint.model.Message;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Configuration
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${app.mongodb.message-ttl-days:90}")
    private int messageTtlDays;

    @Override
    @Bean
    public @org.springframework.lang.NonNull MongoClient reactiveMongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Override
    protected @org.springframework.lang.NonNull String getDatabaseName() {
        return databaseName != null ? databaseName : "socketsprint";
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(reactiveMongoClient(), getDatabaseName());
    }

    @PostConstruct
    public void createIndexes() {
        if (reactiveMongoTemplate() == null) {
            log.warn("MongoTemplate is not available, skipping index creation");
            return;
        }
        ReactiveIndexOperations indexOps = reactiveMongoTemplate().indexOps(Message.class);
        
        // Create TTL index for message expiration
        indexOps.ensureIndex(
            new org.springframework.data.mongodb.core.index.Index()
                .on("expire_at", org.springframework.data.domain.Sort.Direction.ASC)
                .expire(Duration.ofDays(messageTtlDays))
        ).subscribe();

        // Create compound index for common query patterns
        indexOps.ensureIndex(
            new org.springframework.data.mongodb.core.index.CompoundIndexDefinition(
                new Document()
                    .append("channel_id", 1)
                    .append("created_at", -1)
            ).named("channel_created_at_idx")
        ).subscribe();
    }
}
