package com.sample.user.snapshot;

import org.axonframework.eventsourcing.AggregateSnapshotter;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sample.user.UserAggregate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserSnapshotter {

    @Autowired
    private AggregateSnapshotter snapshotter;

    @Autowired
    private EventStore store;

    public void createSnapshot(String aggregateIdentifier) {

        snapshotter.scheduleSnapshot(UserAggregate.class, aggregateIdentifier);
        log.info("SCHEDULED A SNAPSHOT!");

    }

}
