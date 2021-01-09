package apr.autismapp.data;

import java.util.Objects;

public class Task {
    public Task(){}
    public Task(String user, String name, long date) {
        this.user = user;
        this.name = name;
        this.date = date;
        this.check = false;
    }

    @Override
    public String toString() {
        return "Task{" + "user=" + user + ", name=" + name + ", date=" + date + ", check=" + check +'}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.user);
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.date);
        hash = 89 * hash + Objects.hashCode(this.check);
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
        final Task other = (Task) obj;
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.check, other.check)) {
            return false;
        }
        return true;
    }

    public void setCheck(boolean check){
        this.check=check;
    }

    public String user;
    public String name;
    public long date;
    public boolean check;
}
