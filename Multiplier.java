public class Multiplier {
    public static void multiply(Word32 a, Word32 b, Word32 result) {
        for (int i = 0; i < 32; i++) { // clear result
            result.setBitN(i, new Bit(false)); //set each bit to 0
        }
        Word32 shiftedA = new Word32(); //holds shifted a
        Word32 tempSum = new Word32();// hold addition result
        Bit  multiplierBit = new Bit(false); // current bit of b

        for (int i = 31; i >= 0; i--) {
            b.getBitN(i, multiplierBit); //get bit i from b
            if(multiplierBit.getValue()) {
                int shiftAmount = 31 -i; // compute shift distance
                Shifter.LeftShift(a, shiftAmount, shiftedA); // shift a left
                Adder.add(result, shiftedA, tempSum); // add value

                for (int j = 0; j < 32; j++) { //copy tempSum to result
                    Bit copy = new Bit(false);
                    tempSum.getBitN(j, copy);
                    result.setBitN(j, copy); //write bit to result
                }
            }
        }
    }
}
