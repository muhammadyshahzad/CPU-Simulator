import java.util.HashMap;
import java.util.LinkedList;

public class Assembler {
    private static final HashMap<String, Integer> OPCODES = new HashMap<>();
    private static final HashMap<String, Integer> REGISTERS = new HashMap<>();
    static { //opcode
        OPCODES.put("halt", 0);
        OPCODES.put("add", 1);
        OPCODES.put("and", 2);
        OPCODES.put("multiply", 3);
        OPCODES.put("leftshift", 4);
        OPCODES.put("subtract", 5);
        OPCODES.put("or", 6);
        OPCODES.put("rightshift", 7);
        OPCODES.put("syscall", 8);
        OPCODES.put("call", 9);
        OPCODES.put("return", 10);
        OPCODES.put("compare", 11);
        OPCODES.put("ble", 12);
        OPCODES.put("blt", 13);
        OPCODES.put("bge", 14);
        OPCODES.put("bgt", 15);
        OPCODES.put("beq", 16);
        OPCODES.put("bne", 17);
        OPCODES.put("load", 18);
        OPCODES.put("store", 19);
        OPCODES.put("copy", 20);
        for (int i = 0; i < 32; i++) {
            REGISTERS.put("r" + i, i); //map register
        }
    }
    public static String[] assemble(String[] input) {
        if (input == null) {
            return new String[0];
        }
        LinkedList<String> output = new LinkedList<>();
        for (String line : input) {
            if (line == null) {
                continue; //next line
            }
            line = normalize(line); //normalize input
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");//sp;it tokenns
            String instruction = parts[0];
            Integer opcode = OPCODES.get(instruction); // lookup opcode
            if (opcode == null) {
                throw new IllegalArgumentException("Unknown instruction: " + instruction);
            }
            if (instruction.equals("halt")) {
                if (parts.length != 1) { // invalid args
                    throw new IllegalArgumentException("Halt takes no operands");
                }
                output.add(toUnsignedBinary(opcode, 5) + "00000000000"); // build instruction
            }
            else if (instruction.equals("return")) {
                if (parts.length != 1) {
                    throw new IllegalArgumentException("Return takes no operands");
                }
                output.add(toUnsignedBinary(opcode, 5) + "00000000000");
            }
            else if (uses11BitImmediate(instruction)) {
                if (parts.length != 2) {
                    throw new IllegalArgumentException(instruction + " requires one operand");
                }
                int value = Integer.parseInt(parts[1]); //parse value
                if (instruction.equals("call")) {
                    output.add(toUnsignedBinary(opcode, 5) + toUnsignedBinary(value, 11));
                } else {
                    output.add(toUnsignedBinary(opcode, 5) + toSignedBinary(value, 11));
                }
            }
            else if (instruction.equals("compare")) {
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Compare requires two operands");
                }
                String arg1 = parts[1];
                String arg2 = parts[2];
                if (isRegister(arg1) && isRegister(arg2)) {
                    output.add(toUnsignedBinary(opcode, 5) + "0" + toUnsignedBinary(REGISTERS.get(arg1), 5) + toUnsignedBinary(REGISTERS.get(arg2), 5));
                }
                else if (!isRegister(arg1) && isRegister(arg2)) {
                    int imm = Integer.parseInt(arg1);
                    output.add(toUnsignedBinary(opcode, 5) + "1" + toSignedBinary(imm, 5) + toUnsignedBinary(REGISTERS.get(arg2), 5));
                }
                else {
                    throw new IllegalArgumentException("Invalid operands for compare: " + line);
                }
            }
            else {
                if (parts.length != 3) {
                    throw new IllegalArgumentException(instruction + " requires two operands");
                }
                String arg1 = parts[1]; //first operand
                String arg2 = parts[2]; // second operand
                if (isRegister(arg1) && isRegister(arg2)) { //2R formart
                    output.add(toUnsignedBinary(opcode, 5) + "0" + toUnsignedBinary(REGISTERS.get(arg1), 5) + toUnsignedBinary(REGISTERS.get(arg2), 5));
                }
                else if (!isRegister(arg1) && isRegister(arg2)) {
                    int imm = Integer.parseInt(arg1);
                    output.add(toUnsignedBinary(opcode, 5) + "1" + toSignedBinary(imm, 5) + toUnsignedBinary(REGISTERS.get(arg2), 5));
                }
                else {
                    throw new IllegalArgumentException("Invalid operands for: " + line);
                }
            }
        }

        return output.toArray(new String[0]);
    }
    public static String[] finalOutput(String[] input) {
        if (input == null || input.length == 0) {
            return new String[0];
        }
        LinkedList<String> result = new LinkedList<>();
        int len = input.length;
        boolean odd = (len % 2 != 0); //odd check
        int total = odd ? len + 1 : len; //asjudt size
        String[] temp = new String[total];
        for (int i = 0; i < len; i++) {
            if (input[i] == null || input[i].length() != 16) {
                throw new IllegalArgumentException("Each input line must be 16 bits");
            }
            temp[i] = input[i]; // copy line
        }
        if (odd) {
            temp[total - 1] = "0000000000000000"; // add halt
        }
        for (int i = 0; i < total; i += 2) {
            result.add(temp[i] + temp[i + 1]); // merge lines
        }
        return result.toArray(new String[0]);
    }
    private static boolean uses11BitImmediate(String instruction) { //check type
        return instruction.equals("syscall")
                || instruction.equals("call")
                || instruction.equals("ble")
                || instruction.equals("blt")
                || instruction.equals("bge")
                || instruction.equals("bgt")
                || instruction.equals("beq")
                || instruction.equals("bne");
    }
    private static boolean isRegister(String s) {
        return REGISTERS.containsKey(s);
    }
    private static String normalize(String line) {
        line = line.trim().toLowerCase();
        line = line.replace("left shift", "leftshift"); // fix format
        line = line.replace("right shift", "rightshift");
        return line;
    }
    private static String toUnsignedBinary(int value, int bits) {
        int max = (1 << bits) - 1; // max value
        if (value < 0 || value > max) {
            throw new IllegalArgumentException("Value out of range for " + bits + " bits: " + value);
        }
        String binary = Integer.toBinaryString(value);
        while (binary.length() < bits) { // pad zeroes
            binary = "0" + binary;
        }
        return binary;
    }
    private static String toSignedBinary(int value, int bits) {
        int min = -(1 << (bits - 1));
        int max = (1 << (bits - 1)) - 1;
        if (value < min || value > max) {
            throw new IllegalArgumentException("Value out of range for " + bits + " bits: " + value);
        }
        int masked = value & ((1 << bits) - 1);
        String binary = Integer.toBinaryString(masked); //covert
        while (binary.length() < bits) {
            binary = "0" + binary;
        }
        return binary; // return result
    }
}