void main() {
    var i = 0
    var s = 0
    while (true) {
        printChar('W')
        printChar('e')
        printChar('r')
        printChar('t')
        printChar(':')
        printChar(0x0d)
        var a = readInt()
        if a < 0 {
            break
        }
        i += 1
        s += a
        printChar('S')
        printChar('u')
        printChar('m')
        printChar('m')
        printChar('e')
        printChar('=')
        printInt(s)
        printChar(0x0d)
        printChar('M')
        printChar('i')
        printChar('t')
        printChar('t')
        printChar('e')
        printChar('l')
        printChar('=')
        printInt(s / i)
        printChar(0x0d)
    }
}
