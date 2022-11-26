# B-like Lang

This is my first approach of writing a primitive compiler from a sub-set of the C-language to Z8 assembly.
It can be used together with [Z8ASM](https://github.com/tmssngr/z8asm) to compile to binary.

The syntax is close to C (or Java), semicolons are optional.
At the moment there is only support for 16 bit signed integer (`int`) variables - like the built-in Tiny-Basic of the UB8830 processor, an east-german derivative of the Zilog Z8, used in the [JU+TE computer](https://github.com/boert/JU-TE-Computer).
But you also may use `var` instead.

This project is not meant to be a ready-to-use project, but rather for Java developers.
The main class is [Compiler](src/main/src/de/regnis/b/Compiler.java).
It accepts a source file and writes `output.asm` in the same directory as the source file.
The generated asm file will start with `.org %8000`, so the program will start be compiled to address `0x8000`.

To be able to interact with the operating system, some methods are known by default:
- `void printChar(int character)`
- `void printInt(int value)`
- `int getMem(int address)` - reads the byte from `address`
- `void setMem(int address, int value)` - writes the lower byte of `value` to `address`
- `int readInt()`

These methods are also configured in the `Compiler` class as `builtInFunctions`.

## Features ;)
- `var`
- optional ;
- optional braces for the `if` expression

## Limitations
- just 16-bit signed integer
- just a single source file (no includes)
- no array, no string support
