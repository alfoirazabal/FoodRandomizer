package com.alfoirazaballevy.foodrandomizer.domains;

public class Food implements Comparable<Food> {

    private long id;
    private String name;
    private String description;
    private byte sinTACC;
    private byte foodType;

    public Food(long id, String name, String description, byte sinTACC, byte foodType) {
        this.setId(id);
        this.setName(name);
        this.setDescription(description);
        this.setSinTACC(sinTACC);
        this.setFoodType(foodType);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte getSinTACC() {
        return sinTACC;
    }

    public void setSinTACC(byte sinTACC) {
        this.sinTACC = sinTACC;
    }

    public byte getFoodType() {
        return foodType;
    }

    public void setFoodType(byte foodType) {
        this.foodType = foodType;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int compareTo(Food o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }
}
