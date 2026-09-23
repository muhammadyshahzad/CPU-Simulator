public class Word16 {
    private final Bit[] bits;

    public Word16() {
        bits = new Bit[16];
        for (int i = 0; i < 16; i++) { // loop through bits
            bits[i] = new Bit(false); //initialize to zero
        }
    }

    public Word16(Bit[] in) {
        bits = new Bit[16];
        for (int i = 0; i < 16; i++) {
            bits[i]  = new Bit(false);
        }
        if (in != null) { //check input array
            int limit = in.length < 16 ? in.length : 16; //determoine copy limit
            for (int i = 0; i < limit; i++) { //loop through input
                if (in[i] != null) { //check for null
                    setBitN(i, in[i]); //copy bit value
                }
            }
        }
    }

    public void copy(Word16 result) {// sets the values in "result" to be the same as the values in this instance; use "bit.assign"
        for (int i = 0; i < 16; i++) {
            if (bits[i].getValue()) {
                result.bits[i].assign(Bit.boolValues.TRUE); //assign true
            } else {
                result.bits[i].assign(Bit.boolValues.FALSE); //assign false
            }
        }
    }

    public void setBitN(int n, Bit source) { // sets the nth bit of this word to "source"
        if (source.getValue()) { //check source valkue
            bits[n].assign(Bit.boolValues.TRUE);
        } else {
            bits[n].assign(Bit.boolValues.FALSE);
        }
    }

    public void getBitN(int n, Bit result) { // sets result to be the same value as the nth bit of this word
        if (bits[n].getValue()) {
            result.assign(Bit.boolValues.TRUE);
        } else {
            result.assign(Bit.boolValues.FALSE);
        }
    }

    public boolean equals(Word16 other) { // is other equal to this
        return equals(this, other);
    }

    public static boolean equals(Word16 a, Word16 b) {
        for (int i = 0; i < 16; i++) {
            boolean av = a.bits[i].getValue();
            boolean bv = b.bits[i].getValue();
            if (av) {
                if (!bv) return false; //mismatch found
            } else {
                if (bv) return false;
            }
        }
        return true; //all bits equal
    }

    public void and(Word16 other, Word16 result) {
        and(this, other, result);
    }

    public static void and(Word16 a, Word16 b, Word16 result) {
        for (int i = 0; i < 16; i++) {
            Bit.and(a.bits[i], b.bits[i], result.bits[i]);
        }
    }

    public void or(Word16 other, Word16 result) {
        or(this, other, result);
    }

    public static void or(Word16 a, Word16 b, Word16 result) {
        for (int i = 0; i < 16; i++) {
            Bit.or(a.bits[i], b.bits[i], result.bits[i]);
        }
    }

    public void xor(Word16 other, Word16 result) {
        xor(this, other, result);
    }

    public static void xor(Word16 a, Word16 b, Word16 result) {
        for (int i = 0; i < 16; i++) {
            Bit.xor(a.bits[i], b.bits[i], result.bits[i]);
        }
    }

    public void not( Word16 result) {
        not(this, result);
    }

    public static void not(Word16 a, Word16 result) {
        for (int i = 0; i < 16; i++) {
            Bit.not(a.bits[i], result.bits[i]);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Bit bit : bits) {
            sb.append(bit.toString());
            sb.append(",");
        }
        return sb.toString();
    }
}