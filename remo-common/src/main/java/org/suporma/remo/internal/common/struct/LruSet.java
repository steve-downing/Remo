package org.suporma.remo.internal.common.struct;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LruSet<T> implements Set<T> {
    private final Set<T> backingSet;
    private final int maxSize;
    
    private class LruMap<U, V> extends LinkedHashMap<U, V> {
        private static final long serialVersionUID = -7042107506822133926L;

        protected boolean removeEldestEntry(Map.Entry<U, V> eldest) {
            return size() > maxSize;
        }
    }
    
    public LruSet(int maxSize) {
        this.maxSize = maxSize;
        this.backingSet =
                Collections.synchronizedSet(Collections.newSetFromMap(new LruMap<T, Boolean>()));
    }

    public int size() { return backingSet.size(); }
    public boolean isEmpty() { return backingSet.isEmpty(); }
    public boolean contains(Object o) { return backingSet.contains(o); }
    public Iterator<T> iterator() { return backingSet.iterator(); }
    public Object[] toArray() { return backingSet.toArray(); }
    public <U> U[] toArray(U[] a) { return backingSet.toArray(a); }
    public void clear() { backingSet.clear(); }
    public boolean add(T e) { return backingSet.add(e); }
    public boolean remove(Object o) { return backingSet.remove(o); }
    public boolean containsAll(Collection<?> c) { return backingSet.containsAll(c); }
    public boolean addAll(Collection<? extends T> c) { return backingSet.addAll(c); }
    public boolean retainAll(Collection<?> c) { return backingSet.retainAll(c); }
    public boolean removeAll(Collection<?> c) { return backingSet.removeAll(c); }
}
