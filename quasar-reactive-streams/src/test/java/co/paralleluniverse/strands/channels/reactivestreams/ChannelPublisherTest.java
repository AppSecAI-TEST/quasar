/*
 * Copyright (c) 2013-2015, Parallel Universe Software Co. All rights reserved.
 * 
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *  
 *   or (per the licensee's choosing)
 *  
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.strands.channels.reactivestreams;

import co.paralleluniverse.strands.channels.Channels;
import co.paralleluniverse.strands.channels.Channels.OverflowPolicy;
import static co.paralleluniverse.strands.channels.reactivestreams.TestHelper.*;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.*;

public class ChannelPublisherTest extends PublisherVerification<Integer> {
    private static final long DEFAULT_TIMEOUT_MILLIS = 300L;
    private static final long PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS = 500L;
    private static final int DELAY_AMOUNT = 20;

    private final int buffer;
    private final OverflowPolicy overflowPolicy;
    private final boolean delay;

    @Factory(dataProvider = "params")
    public ChannelPublisherTest(int buffer, OverflowPolicy overflowPolicy, boolean delay) {
//        super(new TestEnvironment());
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS), PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS);

        this.buffer = buffer;
        this.overflowPolicy = overflowPolicy;
        this.delay = delay;
    }

    @DataProvider(name = "params")
    public static Object[][] data() {
        return new Object[][]{
            // {5, OverflowPolicy.THROW, false},
            // {5, OverflowPolicy.THROW, true},
            {5, OverflowPolicy.BLOCK, false},
            {5, OverflowPolicy.BLOCK, true},
            {-1, OverflowPolicy.THROW, false},
            {-1, OverflowPolicy.THROW, true},
            // {5, OverflowPolicy.DISPLACE, false},
            // {5, OverflowPolicy.DISPLACE, true},
            {0, OverflowPolicy.BLOCK, false},
            {0, OverflowPolicy.BLOCK, true},
            {1, OverflowPolicy.BLOCK, false},
            {1, OverflowPolicy.BLOCK, true}
        };
    }

    @Override
    public long maxElementsFromPublisher() {
        return Long.MAX_VALUE - 1;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }

    @Override
    public Publisher<Integer> createPublisher(final long elements) {
        return ReactiveStreams.toPublisher(startPublisherFiber(Channels.<Integer>newChannel(buffer, overflowPolicy), delay ? DELAY_AMOUNT : 0, elements));
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return ReactiveStreams.toPublisher(startFailedPublisherFiber(Channels.<Integer>newChannel(buffer, overflowPolicy), delay ? DELAY_AMOUNT : 0));
    }

    @Test
    public void testNothing() {
    }
}
