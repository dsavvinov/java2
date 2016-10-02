package gut;

import java.io.Serializable;

/**
 * Abstraction of branch in VCS.
 * <p>
 * Essentially, it's just a named pointer to the head-commit.
 */
public class Branch implements Serializable {
    private String name;
    private Commit head;

    public Branch(String name, Commit head) {
        this.name = name;
        this.head = head;
    }

    public String getName() {
        return name;
    }

    public Commit getHead() {
        return head;
    }

    public void setHead(Commit head) {
        this.head = head;
    }

    /* hashCode() and equals() are overrided because
       we distinguish branches solely by the name
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Branch) {
            Branch that = (Branch) obj;
            return name.equals(that.name);
        }
        return false;
    }
}
