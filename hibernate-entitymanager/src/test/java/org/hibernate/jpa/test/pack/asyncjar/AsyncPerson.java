package org.hibernate.jpa.test.pack.asyncjar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "async_person")
public class AsyncPerson {
    private Integer id;
    private String name;

    public AsyncPerson() {
    }

    public AsyncPerson(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AsyncPerson{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
