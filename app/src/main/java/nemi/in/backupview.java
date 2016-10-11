package nemi.in;

import java.util.ArrayList;

/**
 * Created by Aman on 5/27/2016.
 */
class backupview {
    String trnid;
    String item;
    String cname;
    String ccont;
    String qty;
    String price;
    String amount;

    public backupview(String trnid, String item, String price,String qty, String amount, String cname, String ccont) {
        this.trnid = trnid;
        this.item = item;
        this.cname = cname;
        this.ccont = ccont;
        this.qty = qty;
        this.price = price;
        this.amount = amount;
    }

    public String gettrnid() {
        return trnid;
    }

    public void settrnid(String trnid) {
        this.trnid = trnid;
    }

    public String getitem() {
        return item;
    }

    public void setitem(String item) {
        this.item = item;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCcont() {
        return ccont;
    }

    public void setCcont(String ccont) {
        this.ccont = ccont;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getprice() {
        return price;
    }

    public void setprice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
