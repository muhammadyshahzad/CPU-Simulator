public class Word32 {
    private final Bit[] bits;

    public Word32() {
        bits = new Bit[32];
        for (int i = 0; i < 32; i++) {
            bits[i] = new Bit(false);
        }
    }

    public Word32(Bit[] in) {
        bits = new Bit[32];
        for (int i = 0; i < 32; i++) {
            bits[i] = new Bit(false);
        }
        if (in != null) {
            int limit = in.length < 32 ? in.length : 32;
            for (int i = 0; i < limit; i++) {
                if (in[i] != null) {
                    setBitN(i, in[i]);
                }
            }
        }
    }

    public void getTopHalf(Word16 result) { // sets result = bits 0-15 of this word. use bit.assign
        for (int i = 0; i < 16; i++) { //loop through top half
            if (bits[i].getValue()) {
                Bit temp = new Bit(false); //create temp bit
                temp.assign(Bit.boolValues.TRUE);
                result.setBitN(i, temp); // set result bit
            } else {
                Bit temp = new Bit(false);
                temp.assign(Bit.boolValues.FALSE);
                result.setBitN(i, temp);
            }
        }
    }

    public void getBottomHalf(Word16 result) { // sets result = bits 16-31 of this word. use bit.assign
        for (int i = 0; i < 16; i++) { //loop through bottom half
            if (bits[i + 16].getValue()) {
                Bit temp = new Bit(false);
                temp.assign(Bit.boolValues.TRUE);
                result.setBitN(i, temp);
            } else {
                Bit temp = new Bit(false);
                temp.assign(Bit.boolValues.FALSE);
                result.setBitN(i, temp);
            }
        }
    }

    public void copy(Word32 result) { // sets result's bit to be the same as this. use bit.assign
        for (int i = 0; i < 32; i++) {
            if (bits[i].getValue()) {
                result.bits[i].assign(Bit.boolValues.TRUE);
            } else {
                result.bits[i].assign(Bit.boolValues.FALSE);
            }
        }
    }

    public boolean equals(Word32 other) {
        return equals(this, other); //call static equals
    }

    public static boolean equals(Word32 a, Word32 b) {
        for (int i = 0; i < 32; i++) {
            boolean av = a.bits[i].getValue();
            boolean bv = b.bits[i].getValue();
            if (av) {
                if (!bv) return false;
            } else {
                if (bv) return false;
            }
        }
        return true; //all bits equal
    }

    public void getBitN(int n, Bit result) { // use bit.assign
        if (bits[n].getValue()) {
            result.assign(Bit.boolValues.TRUE);
        } else {
            result.assign(Bit.boolValues.FALSE);
        }
    }

    public void setBitN(int n, Bit source) { //  use bit.assign
        if (source.getValue()) {
            bits[n].assign(Bit.boolValues.TRUE);
        } else  {
            bits[n].assign(Bit.boolValues.FALSE);
        }
    }

    public void and(Word32 other, Word32 result) {
        and(this, other, result); //call static and
    }

    public static void and(Word32 a, Word32 b, Word32 result) {
        for (int i = 0; i < 32; i++) {
            Bit.and(a.bits[i], b.bits[i], result.bits[i]);
        }
    }

    public void or(Word32 other, Word32 result) {
        or(this, other, result);
    }

    public static void or(Word32 a, Word32 b, Word32 result) {
        for (int i = 0; i < 32; i++) {
            Bit.or(a.bits[i], b.bits[i], result.bits[i]);
        }
    }

    public void xor(Word32 other, Word32 result) {
        xor(this, other, result);
    }

    public static void xor(Word32 a, Word32 b, Word32 result) {
        for (int i = 0; i < 32; i++) {
            Bit.xor(a.bits[i], b.bits[i], result.bits[i]);
        }
    }

    public void not( Word32 result) {
        not(this, result);
    }

    public static void not(Word32 a, Word32 result) {
        for (int i = 0; i < 32; i++) {
            Bit.not(a.bits[i], result.bits[i]); //not individual bits
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
