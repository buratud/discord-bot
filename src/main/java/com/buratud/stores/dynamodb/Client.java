package com.buratud.stores.dynamodb;

import com.buratud.Env;
import lombok.Getter;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class Client {
    private static volatile Client instance;
    @Getter
    private final DynamoDbClient db;
    @Getter
    private final DynamoDbEnhancedClient edb;

    private Client() {
        db = DynamoDbClient.builder()
                .region(Region.of(Env.AWS_REGION))
                .credentialsProvider(() -> AwsBasicCredentials.create(Env.AWS_ACCESS_KEY, Env.AWS_SECRET_ACCESS_KEY))
                .build();

        edb = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(db)
                .build();
    }

    public static Client getInstance() {
        if (instance == null) {
            synchronized (Client.class) {
                if (instance == null) {
                    instance = new Client();
                }
            }
        }
        return instance;
    }
}
