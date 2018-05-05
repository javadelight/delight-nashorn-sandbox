package delight.nashornsandbox.providers;

//used for providers, when they have to give 2 parameters
public class Pair<A,B> {
    private A first;
    private B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public B getSecond() {
        return second;
    }

    public A getFirst() {
        return first;
    }
}
