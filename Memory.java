public class Memory {
    public Word32 address= new Word32();
    public Word32 value = new Word32();

    private final Word32[] dram= new Word32[1000];

    public int addressAsInt() {
        int retVal = 0; //final value
        int val = 1; //place value
        Bit cur = new Bit(false);

        for (int i = 31; i > 0; i--) {
            address.getBitN(i, cur);
            if(cur.getValue()) {
                retVal += val; // add value
             }
            val *= 2; //next power
        }
        if (retVal < 0 || retVal >= 1000) {
            throw new IndexOutOfBoundsException("Address out of range: " + retVal);
        }
        return retVal; //return index
    }


    public Memory() {
        for (int i = 0; i < 1000; i++) { // fill memory
            dram[i] = new Word32();
        }
    }

    public void read() {
        dram[addressAsInt()].copy(value); // copy to value
    }

    public void write() {
        value.copy(dram[addressAsInt()]); // copy to memory
    }

    public void load(String[] data) {
        if (data.length > 1000) { //size check
            throw new IllegalArgumentException("Too much data");
        }
        for (int i = 0; i < data.length; i++) {
            if (data[i].length() != 32) { //length check
                throw new IllegalArgumentException("Each string must be exactly 32 characters long");
            }
            for (int j = 0; j < 32; j++) { //loop chars
                char c = data[i].charAt(j); // get chars
                if ( c == '1') {
                    dram[i].setBitN(j, new Bit(true));
                } else if (c == '0') {
                    dram[i].setBitN(j, new Bit(false));
                } else {
                    throw new IllegalArgumentException("Invalid character: " + c);
                }
            }
        }
    }
}
