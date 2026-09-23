import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Processor {
    private Memory mem; //memory
    public List<String> output = new LinkedList<>();
    private final Word32[] registers = new Word32[32];// regs
    private final Stack<Integer> callStack = new Stack<>();
    private final ALU alu = new ALU(); //alu
    private int pc =  0; //pc
    private boolean topHalf = true;// top
    private boolean nextTopHalf = true;//next
    private boolean halted = false; //halt
    private final Word32 instructionWord = new Word32();
    private final Word16 currentInstruction = new Word16(); // instructions
    private final Word32 op1 = new Word32();
    private final Word32 op2 = new Word32();
    private final Word32 result = new Word32(); //result
    private int opcode; // code
    private boolean immediateForm; //imm
    private int field1;
    private int field2;
    private int imm11; //imm11
    private int destReg = -1; // dest
    private int nextPc = 0; // next pc
    private boolean branchTaken = false;
    private boolean didWriteRegister = false; // reg write
    private boolean didWriteMemory = false;
    private final Word32 memAddress = new Word32();
    private final Word32 memWriteValue = new Word32();
    private boolean lastCompareEqual = false; // equal
    private boolean lastCompareLess = false; //less
    private int currentClockCycle = 0; // cycles

    public Processor(Memory m) {
        mem = m;
        for (int i = 0; i < 32; i++) {
            registers[i] = new Word32(); // init reg
        }
    }
    public void run() {
        while (!halted) { // main loop
            fetch();
            decode();
            execute();
            store();
        }
    }
    private void fetch() {
        setWord32FromInt(pc, mem.address); // set addr
        mem.read();
        currentClockCycle += 300; // instruction read
        mem.value.copy(instructionWord);
        if (topHalf) {
            instructionWord.getTopHalf(currentInstruction); // top
        } else {
            instructionWord.getBottomHalf(currentInstruction); //botom
        }
    }
    private void decode() {
        opcode = getUnsigned(currentInstruction, 0, 4); // opcode
        immediateForm = getBit(currentInstruction, 5);
        field1 = getUnsigned(currentInstruction, 6, 10);
        field2 = getUnsigned(currentInstruction, 11, 15);
        imm11 = getSigned(currentInstruction, 5, 15);
        clearWord(op1);
        clearWord(op2);
        clearWord(result);
        destReg = -1;
        nextPc = pc;
        if (topHalf) {
            nextTopHalf = false; // stay word
        } else {
            nextPc = pc + 1; // next word
            nextTopHalf = true;
        }
        branchTaken = false;
        didWriteRegister = false;
        didWriteMemory = false;
        clearWord(memAddress);
        clearWord(memWriteValue);
        if (opcode == 0 || opcode == 8 || opcode == 9 || opcode == 10 || opcode == 12 || opcode == 13 || opcode == 14 || opcode == 15 || opcode == 16 || opcode == 17) {
            return; // control op
        }
        if (opcode == 20) { // copy
            if (immediateForm) {
                setWord32FromInt(signExtend5(field1), op1);
            } else {
                registers[field1].copy(op1);
            }
            destReg = field2;
            return;
        }

        if (opcode == 11) { // compare
            if (immediateForm) {
                setWord32FromInt(signExtend5(field1), op1);
                registers[field2].copy(op2);
            } else {
                registers[field1].copy(op1);
                registers[field2].copy(op2);
            }
            destReg = field2;
            return;
        }

        if (immediateForm) { // alu imm
            registers[field2].copy(op1);
            setWord32FromInt(signExtend5(field1), op2);
        } else {
            registers[field1].copy(op1);
            registers[field2].copy(op2);
        }
        destReg = field2;
    }
    private void execute() {
        switch (opcode) {
            case 0:
                halted = true;
                printClockCycle();
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 11:
                if (opcode == 3) {
                    currentClockCycle += 10; // multiply
                } else {
                    currentClockCycle += 2; // alu op
                }
                currentInstruction.copy(alu.instruction); // load alu
                op1.copy(alu.op1);
                op2.copy(alu.op2);
                alu.doInstruction();
                alu.result.copy(result);
                if (opcode == 11) { // save flags
                    lastCompareEqual = alu.equal.getValue();
                    lastCompareLess = alu.less.getValue();
                } else {
                    didWriteRegister = true;
                }
                break;
            case 8:
                if (imm11 == 0) {
                    printReg();
                } else if (imm11 == 1) {
                    printMem();
                }
                break;
            case 9: // call
                if (topHalf) {
                    callStack.push(pc * 2 + 1); // save ret
                } else {
                    callStack.push((pc + 1) * 2);
                }
                nextPc = pc + imm11; // jump
                nextTopHalf = true;
                branchTaken = true;
                break;
            case 10: // return
                if (callStack.isEmpty()) {
                    throw new IllegalStateException("Return with empty call stack");
                }
                int returnAddress = callStack.pop();
                nextPc = returnAddress / 2;
                nextTopHalf = (returnAddress % 2 == 0);
                branchTaken = true;
                break;
            case 12: // ble
                if (lastCompareLess || lastCompareEqual) {
                    nextPc = pc + imm11;
                    nextTopHalf = true;
                    branchTaken = true;
                }
                break;
            case 13: // blt
                if (lastCompareLess) {
                    nextPc = pc + imm11;
                    nextTopHalf = true;
                    branchTaken = true;
                }
                break;
            case 14: // bge
                if (!lastCompareLess) {
                    nextPc = pc + imm11;
                    nextTopHalf = true;
                    branchTaken = true;
                }
                break;
            case 15: // bgt
                if (!lastCompareLess && !lastCompareEqual) {
                    nextPc = pc + imm11;
                    nextTopHalf = true;
                    branchTaken = true;
                }
                break;
            case 16: // beq
                if (lastCompareEqual) {
                    nextPc = pc + imm11;
                    nextTopHalf = true;
                    branchTaken = true;
                }
                break;
            case 17: // bne
                if (!lastCompareEqual) {
                    nextPc = pc + imm11;
                    nextTopHalf = true;
                    branchTaken = true;
                }
                break;
            case 18: // load
                if (immediateForm) {
                    Word32 offset = new Word32(); // offset
                    setWord32FromInt(signExtend5(field1), offset);
                    Adder.add(registers[field2], offset, mem.address); // base+off
                } else {
                    registers[field1].copy(mem.address);
                }
                mem.read();
                currentClockCycle += 300; //data read
                mem.value.copy(result);
                destReg = field2;
                didWriteRegister = true;
                break;
            case 19: // store
                if (immediateForm) {
                    registers[field2].copy(mem.address);
                    setWord32FromInt(signExtend5(field1), mem.value);
                } else {
                    registers[field2].copy(mem.address);
                    registers[field1].copy(mem.value);
                }
                mem.write();
                currentClockCycle += 300; //data write
                break;
            case 20: // copy
                if (immediateForm) {
                    setWord32FromInt(signExtend5(field1), result);
                } else {
                    registers[field1].copy(result);
                }
                destReg = field2;
                didWriteRegister = true;
                break;
            default:
                throw new IllegalArgumentException("Unknown opcode: " + opcode);
        }
    }
    private void printClockCycle() {
        var line = "clock cycles:" + currentClockCycle;
        output.add(line);
        System.out.println(line);
    }
    private void printReg() {
        for (int i = 0; i < 32; i++) {
            var line = "r" + i + ":" + registers[i];
            output.add(line);
            System.out.println(line);
        }
    }
    private void printMem() {
        for (int i = 0; i < 1000; i++) {// dump mem
            Word32 addr = new Word32();
            Word32 value = new Word32();
            setWord32FromInt(i, addr);
            addr.copy(mem.address);
            mem.read();
            mem.value.copy(value);
            var line = i + ":" + value + "(" + word32ToInt(value) + ")";
            output.add(line);
            System.out.println(line);
        }
    }
    private void store() {
        if (didWriteRegister) {
            result.copy(registers[destReg]); // write reg
        }
        if (didWriteMemory) {
            memAddress.copy(mem.address);
            memWriteValue.copy(mem.value);
            mem.write();
        }
        pc = nextPc; // update pc
        topHalf = nextTopHalf;
    }
    private boolean getBit(Word16 w, int index) {
        Bit b = new Bit(false);
        w.getBitN(index, b);
        return b.getValue();
    }
    private int getUnsigned(Word16 w, int start, int end) {
        int value = 0;
        Bit b = new Bit(false);
        for (int i = start; i <= end; i++) {
            value <<= 1;
            w.getBitN(i, b);
            if (b.getValue()) {
                value |= 1;
            }
        }
        return value;
    }
    private int getSigned(Word16 w, int start, int end) {
        int value = getUnsigned(w, start, end);
        int width = end - start + 1;
        int signMask = 1 << (width - 1);
        if ((value & signMask) != 0) {
            value -= (1 << width); // sign fix
        }
        return value;
    }
    private int signExtend5(int value) {
        if ((value & 0b10000) != 0) {
            return value - 32; // extend
        }
        return value;
    }
    private void setWord32FromInt(int value, Word32 dest) {
        for (int i = 31; i >= 0; i--) {
            int shift = 31 - i;
            boolean bit = ((value >> shift) & 1) == 1;
            dest.setBitN(i, new Bit(bit));
        }
    }
    private int word32ToInt(Word32 w) {
        int value = 0;
        Bit b = new Bit(false);
        for (int i = 0; i < 32; i++) {
            value <<= 1;
            w.getBitN(i, b);
            if (b.getValue()) {
                value |= 1;
            }
        }
        return value;
    }
    private void clearWord(Word32 w) {
        for (int i = 0; i < 32; i++) {
            w.setBitN(i, new Bit(false)); // clear
        }
    }
}