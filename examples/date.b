int calculate(int day, int month, int year) {
  var h = year / 100;
  var v = year / 400;
  var z = (year + year/4 - h + v + 1) % 7;
  var i = year % 4;
  h = year % 100;
  v = year % 400;
  if (h == 0) {
    i += 1;
  }
  if (v == 0) {
    i -= 1;
  }
  var d = -30;
  var b = 1;
  var a = 0;
  if (i > 0) {
    a = 1;
  }
  if (month > 2) {
    d = d - 1 - a;
  }
  if (month > 8) {
    b = 2;
  }
  while (true) {
    d = d + 30 + (month + b) % 2;
    month = month - 1;
    if (month <= 0) {
      break;
    }
  }
  return ((d + day + z + a + 3) % 7);
}

void main() {
  printInt(calculate(18, 9, 2022)) // sunday -> 6
  printChar(13)
}
