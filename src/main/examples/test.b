var a = 10_u8;
var b = 5 + 2 * 3;

int sqr(int a) {
    return a * a;
}

i8 rnd() {
    return 1;
}

int max(int a, int b) {
    return a; // if
}

int main() {
    a = (a + b) * 2 - sqr(3) * rnd() + max(1, a);
}