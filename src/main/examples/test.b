// line comment
/* block
comment */
uint8 a = 10;
uint8 b = 5 + 2 * 3;
{
    uint8 c = 5 * 2 + 3;
}

a = (a + b) * 2 - sqr(3) * rnd() + max(1, a);
/*
+- a =
   +- operator +
      +- operator -
      |  +- operator *
      |  |  +- operator +
      |  |  |  +- read var a
      |  |  |  +- read var b
      |  |  +- literal 2
      |  +- operator *
      |     +- function call sqr
      |     |  +- literal 3
      |     +- function call rnd
      +- function call max
         +- literal 1
         +- read var a
*/