package it.justdevelop.craftedbeer.adapters;

public class beer_object {
    private int id, ibu;
    private String name, style;
    private double abv, ounces;

    public beer_object(int id, String name, String style, double abv,  int ibu, double ounces) {
        this.id = id;
        this.ibu = ibu;
        this.name = name;
        this.style = style;
        this.abv = abv;
        this.ounces = ounces;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setIbu(int ibu) {
        this.ibu = ibu;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setAbv(double abv) {
        this.abv = abv;
    }

    public void setOunces(double ounces) {
        this.ounces = ounces;
    }

    public int getId() {

        return id;
    }

    public int getIbu() {
        return ibu;
    }

    public String getName() {
        return name;
    }

    public String getStyle() {
        return style;
    }

    public double getAbv() {
        return abv;
    }

    public double getOunces() {
        return ounces;
    }
}
