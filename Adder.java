public class Adder {
    public static void subtract(Word32 a, Word32 b, Word32 result) {
        Bit carry = new Bit(true); //start carry = 1

        for (int i = 31; i >= 0; i--) {
            Bit x = new Bit(false); //bit from a
            Bit y = new Bit(false); // bit from b

            a.getBitN(i, x); //get bit i from a
            b.getBitN(i, y); //get bit i from b

            Bit notY = new Bit(false); //inverted b bit
            y.not(notY); //compute ~b

            Bit xXorY = new Bit(false); // temp xor
            x.xor(notY, xXorY); // x^ ~b

            Bit diff = new Bit(false); // difference bit
            xXorY.xor(carry, diff); // (x ^ ~b) ^ carry

            Bit xAndY = new Bit(false); // temp and
            x.and(notY, xAndY); // x & ~b

            Bit xXorYAndCarry = new Bit(false); // temp carry and
            xXorY.and(carry, xXorYAndCarry); // (x^~b) ^ carry

            Bit newCarry = new Bit(false); // nect carry
            xAndY.or(xXorYAndCarry, newCarry);// full adder carry

            result.setBitN(i, diff); //store result bit
            carry = newCarry; //update carry
        }
    }

    public static void add(Word32 a, Word32 b, Word32 result) {
        Bit carry = new Bit(false); //start carry = 0

        for (int i = 31; i >= 0; i--) {
            Bit x = new Bit(false);
            Bit y = new Bit(false);

            a.getBitN(i, x);
            b.getBitN(i, y);

            Bit xXorY = new Bit(false);
            x.xor(y, xXorY); // x ^ y

            Bit sum = new Bit(false);
            xXorY.xor(carry, sum); //(x ^ y) ^ carru

            Bit xAndY = new Bit(false);
            x.and(y, xAndY); // x & y

            Bit xXorYAndCarry = new Bit(false);
            xXorY.and(carry, xXorYAndCarry); // ( x ^ y) & carry

            Bit newCarry = new Bit(false);
            xAndY.or(xXorYAndCarry, newCarry);

            result.setBitN(i, sum); //store sum bit
            carry = newCarry;
        }
    }
}
