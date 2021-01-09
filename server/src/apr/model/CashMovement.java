package apr.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexes;
import java.util.Objects;
import org.bson.types.ObjectId;

@Entity
@Indexes({@Index(fields= {@Field("time"), @Field("user")})})
public class CashMovement {
    @Id
     private ObjectId id;
    
    public String user;
    public long time;
    public float money;
    public String concept;

    public CashMovement(){}
    public CashMovement(String user, long time, float money, String concept) {
        this.user = user;
        this.time = time;
        this.money = money;
        this.concept = concept;
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
