package net.responses;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListResponseData {
    private ArrayList<ListResponseItem> items = new ArrayList<>();

    public static class ListResponseItem {
        public final int id;
        public final String name;
        public final long size;

        public ListResponseItem(int id, String name, long size) {
            this.id = id;
            this.name = name;
            this.size = size;
        }

        @Override
        public int hashCode() {
            return (Integer.hashCode(id) * 31 + name.hashCode()) * 31 + Long.hashCode(size);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ListResponseItem)) {
                return false;
            }
            ListResponseItem other = (ListResponseItem) obj;
            return id == other.id && name.equals(other.name) && size == other.size;
        }

        @Override
        public String toString() {
            return id + ", " + name + ", " + size;
        }
    }

    @Override
    public String toString() {
        List<String> files = items
                .stream()
                .map(ListResponseItem::toString)
                .collect(Collectors.toList());
        return String.join("; ", files);
    }

    /** delegate methods **/
    public void trimToSize() {
        items.trimToSize();
    }

    public void ensureCapacity(int minCapacity) {
        items.ensureCapacity(minCapacity);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean contains(Object o) {
        return items.contains(o);
    }

    public int indexOf(Object o) {
        return items.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return items.lastIndexOf(o);
    }

    public Object[] toArray() {
        return items.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return items.toArray(a);
    }

    public ListResponseItem get(int index) {
        return items.get(index);
    }

    public ListResponseItem set(int index, ListResponseItem element) {
        return items.set(index, element);
    }

    public boolean add(ListResponseItem listResponseItem) {
        return items.add(listResponseItem);
    }

    public void add(int index, ListResponseItem element) {
        items.add(index, element);
    }

    public ListResponseItem remove(int index) {
        return items.remove(index);
    }

    public boolean remove(Object o) {
        return items.remove(o);
    }

    public void clear() {
        items.clear();
    }

    public boolean addAll(Collection<? extends ListResponseItem> c) {
        return items.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends ListResponseItem> c) {
        return items.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return items.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return items.retainAll(c);
    }

    public ListIterator<ListResponseItem> listIterator(int index) {
        return items.listIterator(index);
    }

    public ListIterator<ListResponseItem> listIterator() {
        return items.listIterator();
    }

    public Iterator<ListResponseItem> iterator() {
        return items.iterator();
    }

    public List<ListResponseItem> subList(int fromIndex, int toIndex) {
        return items.subList(fromIndex, toIndex);
    }

    public void forEach(Consumer<? super ListResponseItem> action) {
        items.forEach(action);
    }

    public Spliterator<ListResponseItem> spliterator() {
        return items.spliterator();
    }

    public boolean removeIf(Predicate<? super ListResponseItem> filter) {
        return items.removeIf(filter);
    }

    public void replaceAll(UnaryOperator<ListResponseItem> operator) {
        items.replaceAll(operator);
    }

    public void sort(Comparator<? super ListResponseItem> c) {
        items.sort(c);
    }

    public boolean containsAll(Collection<?> c) {
        return items.containsAll(c);
    }

    public Stream<ListResponseItem> stream() {
        return items.stream();
    }

    public Stream<ListResponseItem> parallelStream() {
        return items.parallelStream();
    }

    /** end of delegate methods **/
}
