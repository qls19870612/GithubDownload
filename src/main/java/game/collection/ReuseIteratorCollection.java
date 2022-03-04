package game.collection;


public interface ReuseIteratorCollection<E> {

    public ReusableIterator<E> iterator();

    public E getFirst();
}
