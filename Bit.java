public class Bit {
    public enum boolValues { FALSE, TRUE }

    private boolean value; //internal bit value

    public Bit(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void assign(boolValues value) { // assign enum value
        if (value == boolValues.TRUE) {
            this.value = true;
        } else {
            this.value = false;
        }
    }

    public void and(Bit b2, Bit result) {//instance and
        and(this, b2, result); // call static and
    }

    public static void and(Bit b1, Bit b2, Bit result) {// static and logic
        if (b1.value){ //check first value
            if (b2.value){ //first true
                result.value = true; //both true
            } else {
                result.value = false; //second false
            }
        } else {
            result.value = false; // first false
        }
    }

    public void or(Bit b2, Bit result) { //instance or
        or(this, b2, result);
    }

    public static void or(Bit b1, Bit b2, Bit result) { //static or logic
        if (b1.value){
            result.value = true;
        } else {
            if (b2.value){
                result.value = true;
            } else {
                result.value = false;
            }
        }
    }

    public void xor(Bit b2, Bit result) {
        xor(this, b2, result);
    }

    public static void xor(Bit b1, Bit b2, Bit result) {
        if (b1.value) {
            if (b2.value) {
                result.value = false;
            } else {
                result.value = true;
            }
        } else {
            if (b2.value) {
                result.value = true;
            } else {
                result.value = false;
            }
        }
    }

    public static void not(Bit b2, Bit result) { //static not
        if (b2.value){
            result.value = false;
        } else {
            result.value = true;
        }
    }

    public void not(Bit result) { //instance not
        not(this, result);
    }

    public String toString() {
        if (value) return "1";
        return "0";
    }
}
