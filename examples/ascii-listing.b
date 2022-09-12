void main() {
	var chr = ' '
	while (chr < 0x80) {
		if (chr & 0x0F == 0) {
			printHex8(chr)
			printChar(' ')
		}
		printChar(chr)
		chr += 1
		if (chr & 0x0F == 0) {
			printChar(0x0D)
		}
	}
}

void printHex8(int value) {
	printHex4(value >> 4)
	printHex4(value)
}

void printHex4(int value) {
	value &= 0x0F
	if value >= 10 {
		value += 'A' - '9' - 1
	}
	value += '0'
	printChar(value)
}
