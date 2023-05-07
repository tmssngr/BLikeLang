: printHex4 (v -- )
  0x0f and
  dup 10 >=
  if
    'A' '9' -
    +
  then
  '0' +
  printChar
;

: printHex 8 (v -- )
  dup
  4 <<
  printHex4
  printHex4
;

:main
  ' '
  begin
    dup 0x80 < if
  while
    dup 0x0F and 0 =
    if
      dup printHex8
      ' ' printChar
    then
    dup printChar
    1 +
    dup 0x0F and 0 =
    if
      0x0D printChar
    then
  repeat
;
