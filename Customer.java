//Burak Yesil 
//I pledge my honor that I have abided by the Stevens Honor System.

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Customer implements Runnable {
    private Bakery bakery;
    private Random rnd;
    private List<BreadType> shoppingCart;
    private int shopTime;
    private int checkoutTime;

    /** 
     * Initialize a customer object and randomize its shopping cart
     */
    public Customer(Bakery bakery) {
        this.bakery = bakery;
        this.rnd = new Random();
        this.shoppingCart = new ArrayList<BreadType>();
        this.fillShoppingCart();
        this.shopTime = rnd.nextInt(4000);
        this.checkoutTime = rnd.nextInt(4000);
    }

    /**
     * Run tasks for the customer
     */
    public void run() {
        // TODO
        //make a for each loop and a try catch 
        System.out.println("Customer " + hashCode() + ": started shopping. Shopping Size: " + shoppingCart.size());

        for (BreadType bread: shoppingCart){
            switch(bread){
                case RYE: //Accessing Rye Bread Shelf
                    try {
                        this.bakery.ryeShelf.acquire();
                        this.bakery.takeBread(BreadType.RYE);
                        System.out.println("Customer " + hashCode() + ": took a Rye Loaf from shelf");
                        this.bakery.ryeShelf.release();
                        break;
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }

                case SOURDOUGH:
                    try {
                        this.bakery.sourdoughShelf.acquire();
                        this.bakery.takeBread(BreadType.SOURDOUGH);
                        System.out.println("Customer " + hashCode() + ": took a Sourdough Loaf from shelf");
                        this.bakery.sourdoughShelf.release();
                        break;
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }

                case WONDER:
                    try {
                        this.bakery.wonderShelf.acquire();
                        this.bakery.takeBread(BreadType.WONDER);
                        System.out.println("Customer " + hashCode() + ": took a Wonder Loaf from shelf");
                        this.bakery.wonderShelf.release();
                        break;
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
            }
        }

        try {
            Thread.sleep(this.shopTime);//sleep for shopping before checking out 
            this.bakery.cashier.acquire();
                Thread.sleep(this.checkoutTime); //Is this ok?
                this.bakery.salesCounter.acquire();
                    this.bakery.addSales(this.getItemsValue());
                    System.out.println("Customer " + hashCode() + ": bought items");
                this.bakery.salesCounter.release();
            this.bakery.cashier.release();
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        System.out.println("Customer " + hashCode() + ": finished shopping");

        return;
    }

    /**
     * Return a string representation of the customer
     */
    public String toString() {
        return "Customer " + hashCode() + ": shoppingCart=" + Arrays.toString(shoppingCart.toArray()) + ", shopTime=" + shopTime + ", checkoutTime=" + checkoutTime;
    }

    /**
     * Add a bread item to the customer's shopping cart
     */
    private boolean addItem(BreadType bread) {
        // do not allow more than 3 items, chooseItems() does not call more than 3 times
        if (shoppingCart.size() >= 3) {
            return false;
        }
        shoppingCart.add(bread);
        return true;
    }

    /**
     * Fill the customer's shopping cart with 1 to 3 random breads
     */
    private void fillShoppingCart() {
        int itemCnt = 1 + rnd.nextInt(3);
        while (itemCnt > 0) {
            addItem(BreadType.values()[rnd.nextInt(BreadType.values().length)]);
            itemCnt--;
        }
    }

    /**
     * Calculate the total value of the items in the customer's shopping cart
     */
    private float getItemsValue() {
        float value = 0;
        for (BreadType bread : shoppingCart) {
            value += bread.getPrice();
        }
        return value;
    }
}