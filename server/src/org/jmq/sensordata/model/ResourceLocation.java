package org.jmq.sensordata.model;

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
@Indexes({@Index(fields= {@Field("name"), @Field("lat"), @Field("lon")})})
public class ResourceLocation{
	@Id
	 private ObjectId id;

        
       
    public ResourceLocation(){}
    public ResourceLocation(String name, float lat, float lon) {
        this.name=name;        
        this.lat=lat;
        this.lon=lon;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.name);
        hash = 17 * hash + Objects.hashCode(this.lat);
        hash = 17 * hash + Objects.hashCode(this.lon);
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
        final ResourceLocation other = (ResourceLocation) obj;
        if (!Objects.equals(this.name, other.name)) {
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

   
  
    


    public String name;
    public Float lat;
    public Float lon;
    
}
