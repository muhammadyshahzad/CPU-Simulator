public class L2 {
    private Memory mem; // main memory
    private Word32 address = new Word32();
    private Word32 value = new Word32();
    private final Word32[] startAddress = new Word32[4]; // line starts
    private final Word32[][] lines = new Word32[4][8]; // 4 lines
    private final boolean[] valid = new boolean[4]; //v alid bits
    private int nextLine = 0; // replace spot
    public int cycles = 0; // cycle cost
    public L2(Memory m) {
        mem = m;
        for (int i = 0; i < 4; i++) {
            startAddress[i] = new Word32(); // init addr
            for (int j = 0; j < 8; j++) {
                lines[i][j] = new Word32(); // init word
            }
        }
    }
    public Word32 getAddress() {
        return address;
    }
    public Word32 getValue() {
        return value;
    }
    public void setAddress(Word32 addr) {
        addr.copy(address); // copy address
    }
    public void readInstruction() {
        cycles = 0;
        int addr = addressAsInt(address); // current addr
        int start = (addr / 8) * 8; // line start
        int index = findLine(start); //find line
        if (index != -1) {
            lines[index][addr - start].copy(value); // hit
            cycles += 30;
            return;
        }
        int fillIndex = nextLine; // replace line
        nextLine = (nextLine + 1) % 4;
        valid[fillIndex] = true;
        setWord32FromInt(start, startAddress[fillIndex]);
        for (int i = 0; i < 8; i++) {
            setWord32FromInt(start + i, mem.address);
            mem.read();
            mem.value.copy(lines[fillIndex][i]); // fill line
        }
        lines[fillIndex][addr - start].copy(value); // return word
        cycles += 370;
    }
    public void readData() {
        cycles = 0;
        int addr = addressAsInt(address); // current addr
        int start = (addr / 8) * 8; // line start
        int index = findLine(start); // find line
        if (index != -1) {
            lines[index][addr - start].copy(value); // hit
            cycles += 20;
            return;
        }
        int fillIndex = nextLine;
        nextLine = (nextLine + 1) % 4;
        valid[fillIndex] = true;
        setWord32FromInt(start, startAddress[fillIndex]);
        for (int i = 0; i < 8; i++) {
            setWord32FromInt(start + i, mem.address);
            mem.read();
            mem.value.copy(lines[fillIndex][i]); // fill line
        }
        lines[fillIndex][addr - start].copy(value); // return word
        cycles += 360;
    }
    public void writeData() {
        cycles = 0;
        int addr = addressAsInt(address); //current addr
        int start = (addr / 8) * 8; //line start
        int index = findLine(start); //find line
        if (index != -1) {
            value.copy(lines[index][addr - start]); //update cache
            address.copy(mem.address);
            value.copy(mem.value);
            mem.write(); //write through
            cycles += 20;
            return;
        }
        int fillIndex = nextLine; //replace line
        nextLine = (nextLine + 1) % 4;
        valid[fillIndex] = true;
        setWord32FromInt(start, startAddress[fillIndex]);
        for (int i = 0; i < 8; i++) {
            setWord32FromInt(start + i, mem.address);
            mem.read();
            mem.value.copy(lines[fillIndex][i]); //fill line
        }
        value.copy(lines[fillIndex][addr - start]); //update cache
        address.copy(mem.address);
        value.copy(mem.value);
        mem.write(); //write through
        cycles += 360;
    }
    private int findLine(int start) {
        for (int i = 0; i < 4; i++) {
            if (valid[i] && addressAsInt(startAddress[i]) == start) {
                return i; //found
            }
        }
        return -1;
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