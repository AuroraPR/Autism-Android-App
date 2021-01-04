package org.jmq.sensordata;

import java.util.Objects;

import org.bson.types.ObjectId;
import static dev.morphia.aggregation.experimental.expressions.DateExpressions.dateFromString;


import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexes;
import java.util.Date;
@Entity
@Indexes({@Index(fields= {@Field("timestamp"), @Field("user"), @Field("lat"), @Field("lon")})})
public class TimeLocation{
	@Id
	 private ObjectId id;

        
       
    public TimeLocation(){}
    public TimeLocation(String user,  long timestamp, float lat, float lon) {
        this.user = user;
        this.timestamp=timestamp;        
        this.lat=lat;
        this.lon=lon;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.user);
        hash = 53 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
        hash = 53 * hash + Objects.hashCode(this.lat);
        hash = 53 * hash + Objects.hashCode(this.lon);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TimeLocation other = (TimeLocation) obj;
        if (this.timestamp != other.timestamp) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.lat, other.lat)) {
            return false;
        }
        if (!Objects.equals(this.lon, other.lon)) {
            return false;
        }
        return true;
    }

  
    


    public String user;
    public long timestamp;
    public Float lat;
    public Float lon;
    
}
