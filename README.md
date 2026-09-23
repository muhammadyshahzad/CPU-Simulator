# ICSI 404 Cache Project

## Overview

This project is a Java processor simulator that shows how cache memory can improve performance.

The processor includes:

* 32 registers
* ALU
* Main memory
* Instruction cache
* L2 cache
* Assembler
* Clock cycle counter

## Cache

### Instruction Cache

* Holds 8 words
* Cache hit: 10 cycles
* Cache miss: loads data from memory or L2

### L2 Cache

* 4 cache lines
* 8 words per line
* Used for instructions and data
* Uses round-robin replacement

## Test Programs

The project tests three programs:

1. Sum numbers from 1 to 100
2. Sum an array of 100 numbers
3. Sum values stored in a linked list

Each program should produce:

```text
5050
```

## Main Files


Processor.java
Memory.java
InstructionCache.java
L2.java
Assembler.java
ALU.java
Word16.java
Word32.java
Bit.java
CacheTest.java
```

## Running the Project

1. Open the project in IntelliJ IDEA.
2. Make sure Java and JUnit 5 are installed.
3. Open `CacheTest.java`.
4. Run the tests.

## Purpose

The purpose of this project is to compare processor clock cycles with and without cache memory.

Programs that reuse instructions or access nearby memory locations can run faster because the processor does not need to access main memory every time.

The array usually benefits more from cache than the linked list because array values are stored next to each other in memory.
