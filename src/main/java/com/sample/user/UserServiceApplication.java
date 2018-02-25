package com.sample.user;

import org.axonframework.eventsourcing.AggregateFactory;
import org.axonframework.eventsourcing.AggregateSnapshotter;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.spring.eventsourcing.SpringPrototypeAggregateFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.sample.user.crypto.PBKDF2SaltedPasswordHashGenerator;
import com.sample.user.crypto.PasswordHashGenerator;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    //@ConditionalOnMissingBean
    public PasswordHashGenerator passwordHashGenerator() {
        return new PBKDF2SaltedPasswordHashGenerator();
    }

    @Bean
    public AggregateFactory<UserAggregate> userAggregateFactory() {

        SpringPrototypeAggregateFactory<UserAggregate> aggregateFactory = new SpringPrototypeAggregateFactory<>();
        aggregateFactory.setPrototypeBeanName("userAggregate");

        return aggregateFactory;

    }

    @Bean
    public AggregateSnapshotter snapShotter(EventStore eventStore,
                                            AggregateFactory<UserAggregate> bankAccountAggregateFactory) {

        return new AggregateSnapshotter(eventStore, userAggregateFactory());

    }
}
