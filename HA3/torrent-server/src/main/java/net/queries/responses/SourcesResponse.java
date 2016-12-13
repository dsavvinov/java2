package net.queries.responses;

import exceptions.InvalidProtocolException;
import net.Message;
import net.MessageHandler;
import net.Query;
import net.queries.SourcesQuery;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourcesResponse implements Message {
    private final ArrayList<Source> sources = new ArrayList<>();

    public SourcesResponse() { }

    public SourcesResponse(List<Source> sources) {
        this.sources.clear();
        this.sources.addAll(sources);
    }

    @Override
    public String toString() {
        List<String> srcs = sources
                .stream()
                .map(Source::toString)
                .collect(Collectors.toList());
        return String.join("; ", srcs);
    }

    @Override
    public void readFrom(InputStream is) throws InvalidProtocolException {
        DataInputStream in = new DataInputStream(is);
        try {
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                String address = in.readUTF();
                short clientPort = in.readShort();

                sources.add(new SourcesResponse.Source(clientPort, address));
            }
        } catch (IOException e) {
            throw new InvalidProtocolException("Format error during reading request: " + e.getMessage());
        }
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = new DataOutputStream(os);
        out.writeInt(sources.size());

        for (Source item : sources) {
            out.writeUTF(item.getHost());
            out.writeShort(item.getPort());
        }
    }

    @Override
    public Query getQuery() {
        return new SourcesQuery();
    }

    @Override
    public <T> T dispatch(MessageHandler<T> handler) {
        return handler.handle(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourcesResponse that = (SourcesResponse) o;

        return sources != null ? sources.equals(that.sources) : that.sources == null;
    }

    @Override
    public int hashCode() {
        return sources != null ? sources.hashCode() : 0;
    }

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

    /** Auto-generated methods: delegate ArrayList by items **/
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
}
