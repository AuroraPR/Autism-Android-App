package jmq.uja.org.mygeosensorapp;

import java.util.Objects;

public class CashMovement {

    public String user;
    public long time;
    public float money;
    public String concept;

    public CashMovement(String user, long time, float money, String concept) {
        this.user = user;
        this.time = time;
        this.money = money;
        this.concept = concept;
    }

    public long getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CashMovement that = (CashMovement) o;
        return time == that.time &&
                Float.compare(that.money, money) == 0 &&
                Objects.equals(user, that.user) &&
                Objects.equals(concept, that.concept);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, time, money, concept);
    }

    @Override
    public String toString() {
        return "CashMovement{" +
                "user='" + user + '\'' +
                ", time=" + time +
                ", money=" + money +
                ", concept='" + concept + '\'' +
                '}';
    }
}
