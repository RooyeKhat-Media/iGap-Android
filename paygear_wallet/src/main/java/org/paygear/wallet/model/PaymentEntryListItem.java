package org.paygear.wallet.model;

/**
 * Created by Software1 on 9/14/2017.
 */

public class PaymentEntryListItem {
    public String title1;
    public String title2;
    public String value;
    public boolean isSelectable;
    public Object data;

    public PaymentEntryListItem(String title1, String title2, String value, boolean isSelectable) {
        this.title1 = title1;
        this.title2 = title2;
        if (value != null)
            this.value = value;
        this.isSelectable = isSelectable;
    }
}
