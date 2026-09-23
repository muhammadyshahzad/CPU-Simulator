public class InstructionCache {
    private Memory mem; // main memory
    private L2 l2; // l2 cache
    private Word32 address = new Word32();
    private Word32 value = new Word32();
    private final Word32 startAddress = new Word32(); // line address
    private final Word32[] line = new Word32[8]; // cace line
    private boolean valid = false; //cache valid
    public int cycles = 0; //cycle cost
    public InstructionCache(Memory m) {
        mem = m;
        for (int i = 0; i < 8; i++) {
            line[i] = new Word32(); // init words
        }
    }
    public InstructionCache(Memory m, L2 l2Cache) {
        mem = m;
        l2 = l2Cache;
        for (int i = 0; i < 8; i++) {
            line[i] = new Word32();
        }
    }
    public Word32 getAddress() {
        return address;
    }
    public Word32 getValue() {
        return value;
    }
    public void setAddress(Word32 addr) {
        addr.copy(address); //copy address
    }
    public void read() {
        cycles = 0;
        int addr = addressAsInt(address); // read addr
        int start = (addr / 8) * 8; //  line start

        if (valid && addressAsInt(startAddress) == start) {
            line[addr - start].copy(value); // cache hit
            cycles += 10;
            return;
        }
        valid = true;
        setWord32FromInt(start, startAddress); // set line addr

        if (l2 != null) {
            for (int i = 0; i < 8; i++) {
                Word32 tempAddr = new Word32();
                setWord32FromInt(start + i, tempAddr);
                l2.setAddress(tempAddr);
                l2.readInstruction();
                l2.getValue().copy(line[i]); // fill from l2
            }
            cycles += l2.cycles;
        } else {
            for (int i = 0; i < 8; i++) {
                setWord32FromInt(start + i, mem.address);
                mem.read();
                mem.value.copy(line[i]); // fill from memory
            }
            cycles += 350;
        }

        line[addr - start].copy(value); // return word
    }
    private int addressAsInt(Word32 w) {
        int retVal = 0;
        int val = 1;
        Bit cur = new Bit(false);

        for (int i = 31; i > 0; i--) {
            w.getBitN(i, cur);
            if (cur.getValue()) {
                retVal += val;
            }
            val *= 2;
        }
        return retVal;
    }
    private void setWord32FromInt(int value, Word32 dest) {
        for (int i = 31; i >= 0; i--) {
            int shift = 31 - i;
            boolean bit = ((value >> shift) & 1) == 1;
            dest.setBitN(i, new Bit(bit));
        }
    }
}