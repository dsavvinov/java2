package net.responses;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourcesResponseData {
    private final ArrayList<Source> sources = new ArrayList<>();

    public static class Source {
        private final short port;
        private final String host;

        public short getPort() {
            return port;
        }

        public String getHost() {
            return host;
        }

        public Source(short port, String host) {
            this.port = port;
            this.host = host;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Source source = (Source) o;

            if (port != source.port) return false;
            return host != null ? host.equals(source.host) : source.host == null;

        }

        @Override
        public int hashCode() {
            int result = (int) port;
            result = 31 * result + (host != null ? host.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return port + ", " + host;
        }
    }

    @Override
    public String toString() {
        List<String> srcs = sources
                .stream()
                .map(Source::toString)
                .collect(Collectors.toList());
        return String.join("; ", srcs);
    }

    /** delegate methods **/


    public void trimToSize() {
        sources.trimToSize();
    }

    public void ensureCapacity(int minCapacity) {
        sources.ensureCapacity(minCapacity);
    }

    public int size() {
        return sources.size();
    }

    public boolean isEmpty() {
        return sources.isEmpty();
    }

    public boolean contains(Object o) {
        return sources.contains(o);
    }

    public int indexOf(Object o) {
        return sources.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return sources.lastIndexOf(o);
    }

    public Object[] toArray() {
        return sources.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return sources.toArray(a);
    }

    public Source get(int index) {
        return sources.get(index);
    }

    public Source set(int index, Source element) {
        return sources.set(index, element);
    }

    public boolean add(Source source) {
        return sources.add(source);
    }

    public void add(int index, Source element) {
        sources.add(index, element);
    }

    public Source remove(int index) {
        return sources.remove(index);
    }

    public boolean remove(Object o) {
        return sources.remove(o);
    }

    public void clear() {
        sources.clear();
    }

    public boolean addAll(Collection<? extends Source> c) {
        return sources.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends Source> c) {
        return sources.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return sources.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return sources.retainAll(c);
    }

    public ListIterator<Source> listIterator(int index) {
        return sources.listIterator(index);
    }

    public ListIterator<Source> listIterator() {
        return sources.listIterator();
    }

    public Iterator<Source> iterator() {
        return sources.iterator();
    }

    public List<Source> subList(int fromIndex, int toIndex) {
        return sources.subList(fromIndex, toIndex);
    }

    public void forEach(Consumer<? super Source> action) {
        sources.forEach(action);
    }

    public Spliterator<Source> spliterator() {
        return sources.spliterator();
    }

    public boolean removeIf(Predicate<? super Source> filter) {
        return sources.removeIf(filter);
    }

    public void replaceAll(UnaryOperator<Source> operator) {
        sources.replaceAll(operator);
    }

    public void sort(Comparator<? super Source> c) {
        sources.sort(c);
    }

    public boolean containsAll(Collection<?> c) {
        return sources.containsAll(c);
    }

    public Stream<Source> stream() {
        return sources.stream();
    }

    public Stream<Source> parallelStream() {
        return sources.parallelStream();
    }
    /** end of delegate methods **/
}
