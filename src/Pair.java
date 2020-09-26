/**
 * Helper class to store index pairs
 *
 * @author Jacob Caggese
 * @date 4/18/18
 * @course Concepts in Artificial Intelligence
 */
public class Pair {
    private Integer x;
    private Integer y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == getClass()) {
            if (((Pair) obj).getY() == y && ((Pair) obj).getX() == x)
                return true;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Integer.parseInt((x.toString() + y.toString()));
    }

    @Override
    public String toString() {
        return "{" + x + "," + y + "}";
    }
}
