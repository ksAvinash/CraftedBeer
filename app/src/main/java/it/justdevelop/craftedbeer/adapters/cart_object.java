package it.justdevelop.craftedbeer.adapters;

public class cart_object {
    private int order_id;
    private String product_name, status;
    private int product_quantity;

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProduct_quantity(int product_quantity) {
        this.product_quantity = product_quantity;
    }

    public int getOrder_id() {

        return order_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getStatus() {
        return status;
    }

    public int getProduct_quantity() {
        return product_quantity;
    }

    public cart_object(int order_id, String product_name, String status, int product_quantity) {

        this.order_id = order_id;
        this.product_name = product_name;
        this.status = status;
        this.product_quantity = product_quantity;
    }
}
