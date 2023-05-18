import java.util.Objects;

public class Pair {
    int first;
    int second;

    public Pair(int first, int second){
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        Pair pair = (Pair) obj;
        return this.first == pair.first && this.second == pair.second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
