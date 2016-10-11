package nemi.in;

import java.util.ArrayList;

/**
 * Created by Aman on 5/27/2016.
 */
class BillItems {
    String id;
    String item;
    int qty = 0;
    int price;

    public BillItems(String id, String item, int qty, int price) {
        this.id = id;
        this.item = item;
        this.qty = qty;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public int getQty() {
        return qty;
    }

    public int getPrice() {
        return price;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    @Override
    public String toString() {
        return "BillItems{" +
                "id='" + id + '\'' +
                ", item='" + item + '\'' +
                ", qty=" + qty +
                ", price=" + price +
                '}';
    }
}
