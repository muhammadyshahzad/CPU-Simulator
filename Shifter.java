public class Shifter {
    public static void LeftShift(Word32 source, int amount, Word32 result) {
        int shift = amount & 31; // use lowest 5 bits

        if (shift == 0) {
            for (int i = 0; i < 32; i++) { //copy all bits
                Bit tmp = new Bit(false);
                source.getBitN(i, tmp); //read source bit
                result.setBitN(i, tmp); //write to result
            }
            return;
        }
        for (int i = 0; i < 32; i++) {
            if (i + shift <= 31) {
                Bit tmp = new Bit(false);
                source.getBitN(i + shift, tmp);
                result.setBitN(i, tmp);
            } else {
                result.setBitN(i, new Bit(false)); // fill with 0
            }
        }
    }

    public static void RightShift(Word32 source, int amount, Word32 result) {
        int shift = amount & 31;
        Bit sign = new Bit(false); // sign bit holder
        source.getBitN(0, sign);

        if (shift == 0) {
            for (int i = 0; i < 32; i++) {
                Bit tmp = new Bit(false);
                source.getBitN(i, tmp);
                result.setBitN(i, tmp);
            }
            return;
        }
        for (int i = 0; i < 32; i++) {
            if (i - shift >= 0) {
                Bit tmp = new Bit(false);
                source.getBitN(i - shift, tmp);
                result.setBitN(i, tmp);
            } else {
                Bit fill =  new Bit(false); // temp fill bit
                sign.and(new Bit(true), fill); //copy sign bit
                result.setBitN(i, fill); //sign extend
            }
        }
    }
}
