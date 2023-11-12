package com.community.weare.Models;



import javax.persistence.*;

@Entity
@Table(name = "category_skills")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_category_id")
    private Integer id;

    @Column
    private String name;


    public Category(String category) {
        this.name=category;
    }
    public Category() {
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String category) {
        this.name = category;
    }

}
