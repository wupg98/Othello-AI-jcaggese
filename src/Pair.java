/**
 * Helper class to store index pairs
 *
 * @author Jacob Caggese
 * @date 4/18/18
 * @course Concepts in Artificial Intelligence
 */
public class Pair {
    private int x;
    private int y;

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

    public boolean equals(Pair comp) {
        if (comp.getY() == y && comp.getX() == x)
            return true;
        return false;
    }

    public String toString() {
        return "{" + x + "," + y + "}";
    }
}
