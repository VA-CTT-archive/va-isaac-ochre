/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.vha.isaac.ochre.collections;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 *
 * @author kec
 * @param <E> Type of {@code Object} that are the values of the map.
 */
public class ConcurrentSequenceObjectMap<E> {

    private static final int SEGMENT_SIZE = 1280;
    ReentrantLock lock = new ReentrantLock();

    CopyOnWriteArrayList<AtomicReferenceArray<E>> objectListList = new CopyOnWriteArrayList<>();
    AtomicInteger maxSequence = new AtomicInteger(0);

    public ConcurrentSequenceObjectMap() {
        objectListList.add(new AtomicReferenceArray<>(SEGMENT_SIZE));
    }
    
    public void clear() {
        objectListList.clear();
        objectListList.add(new AtomicReferenceArray<>(SEGMENT_SIZE));
        maxSequence.set(0);
    }

    /**
     * Provides no range or null checking. For use with a stream that already
     * filters out null values and out of range sequences.
     *
     * @param sequence
     * @return
     */
    private E getQuick(int sequence) {
        int segmentIndex = sequence / SEGMENT_SIZE;
        int indexInSegment = sequence % SEGMENT_SIZE;
        return (E) objectListList.get(segmentIndex).get(indexInSegment);

    }

    public boolean containsKey(int sequence) {
        int segmentIndex = sequence / SEGMENT_SIZE;
        int indexInSegment = sequence % SEGMENT_SIZE;
        if (segmentIndex >= objectListList.size()) {
            return false;
        }
        return objectListList.get(segmentIndex).get(indexInSegment) != null;
    }

    public Optional<E> get(int sequence) {

        int segmentIndex = sequence / SEGMENT_SIZE;
        if (segmentIndex >= objectListList.size()) {
            return Optional.empty();
        }
        int indexInSegment = sequence % SEGMENT_SIZE;

        return Optional.ofNullable((E) objectListList.get(segmentIndex).get(indexInSegment));
    }

    public E put(int sequence, E value) {
        maxSequence.set(Math.max(sequence, maxSequence.get()));
        int segmentIndex = sequence / SEGMENT_SIZE;

        if (segmentIndex >= objectListList.size()) {
            lock.lock();
            try {
                while (segmentIndex >= objectListList.size()) {
                    objectListList.add(new AtomicReferenceArray<>(SEGMENT_SIZE));
                }
            } finally {
                lock.unlock();
            }
        }
        int indexInSegment = sequence % SEGMENT_SIZE;
        if (objectListList.get(segmentIndex).compareAndSet(indexInSegment, (E) null, value)) {
            return value;
        }
        return objectListList.get(segmentIndex).get(indexInSegment);
    }

    public IntStream getSequences() {
        int maxSize = maxSequence.get();
        IntStream.Builder builder = IntStream.builder();
        for (int i = 0; i < maxSize; i++) {
            int segmentIndex = i / SEGMENT_SIZE;
            int indexInSegment = i % SEGMENT_SIZE;
            if (objectListList.get(segmentIndex).get(indexInSegment) != null) {
                builder.accept(i);
            }
        }
        return builder.build();
    }


}
