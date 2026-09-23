public class ALU {
    public Word16 instruction = new Word16();
    public Word32 op1 = new Word32();
    public Word32 op2 = new Word32();
    public Word32 result = new Word32();
    public Bit less = new Bit(false);
    public Bit equal = new Bit(false);

    public void doInstruction(){
        less.assign(Bit.boolValues.FALSE); //reset less
        equal.assign(Bit.boolValues.FALSE); // reset equal
        int opcode = getOpcode();
        if (opcode == 1) { //Add
            Adder.add(op1, op2, result);
        }
        else if (opcode == 2) { //And
            andWords(op1, op2, result);
        }
        else if (opcode == 3) { //Multiply
            Multiplier.multiply(op1, op2, result);
        }
        else if (opcode == 4) {// Left Shift
            int amt = getShiftAmount();
            Shifter.LeftShift(op1, amt, result);
        }
        else if (opcode == 5) { //Subtract
            Adder.subtract(op1, op2, result);
        }
        else if (opcode == 6) { //Or
            orWords(op1, op2, result);
        }
        else if (opcode == 7) { //Right Shift
            int amt = getShiftAmount();
            Shifter.RightShift(op1, amt, result);
        }
        else if (opcode == 11) { //Compare
            doCompare();
        }
        else {
            throw new IllegalArgumentException("Invalid ALU opcode: " + opcode);
        }
    }
    private void doCompare() {
        Word32 temp = new Word32();
        Adder.subtract(op1, op2, temp);
        temp.copy(result);
        if (isZero(temp)) { //check zero
            equal.assign(Bit.boolValues.TRUE);
        }
        if (isNegative(temp)) { //check negatove
            less.assign(Bit.boolValues.TRUE);
        }
    }
    private int getOpcode() {
        int value = 0;
        Bit temp = new Bit(false);
        for (int i = 0; i < 5; i++) { //read 5 bits
            value *= 2; //shift value
            instruction.getBitN(i, temp);
            if (temp.getValue()) {
                value += 1;
            }
        }
        return value;
    }
    private int getShiftAmount() {
        int value = 0;
        Bit temp = new Bit(false);
        for (int i = 27; i < 32; i++) { //last 5 bits
            value *= 2;
            op2.getBitN(i, temp);
            if (temp.getValue()) {
                value += 1;
            }
        }
        return value;
    }
    private boolean isZero(Word32 w) {
        Bit temp = new Bit(false);
        for (int i = 0; i < 32; i++) {
            w.getBitN(i, temp);
            if (temp.getValue()) { //if bit is 1
                return false; //not zero
            }
        }
        return true; //all zero
    }
    private boolean isNegative(Word32 w) {
        Bit sign = new Bit(false);
        w.getBitN(0, sign);
        return sign.getValue();
    }
    private void andWords(Word32 a, Word32 b, Word32 result) {
        for (int i = 0; i < 32; i++) {
            Bit x = new Bit(false);
            Bit y = new Bit(false);
            Bit r = new Bit(false);
            a.getBitN(i, x);
            b.getBitN(i, y);
            x.and(y,r); // compute and
            result.setBitN(i, r); //store bit
        }
    }
    private void orWords(Word32 a, Word32 b, Word32 result) {
        for (int i = 0; i < 32; i++) {
            Bit x = new Bit(false);
            Bit y = new Bit(false);
            Bit r = new Bit(false);
            a.getBitN(i, x);
            b.getBitN(i, y);
            x.or(y,r); //compute or
            result.setBitN(i, r);
        }
    }
}
