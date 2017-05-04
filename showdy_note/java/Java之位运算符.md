### Java位运算符

| &    | 与运算 | $1600任何二进制位和0进行&运算，结果是0；和1进行&运算结果是原值。|
| :------------- |:-------------| :-----|
|\|  | 或运算    | 任何二进制位和0进行 或 运算，结果是原值；和1进行 或运算结果是1。|
| ^|异或运算     |    任何相同二进制位进行 ^ 运算，结果是0；不相同二进制位 ^ 运算结果是1。|
|~|反码|计算机存储是补码,呈现出来的是原码|
|<<|左移|空位补0，被移除的高位丢弃。|
|>>|右移|被移位的二进制最高位是0，右移后，空缺位补0；最高位是1，最高位补1。|
|>>>|无符号右移|被移位二进制最高位无论是0或者是1，空缺位都用0补。|


### 实例说明

```java

	class Operator {
		public static void main(String[] args) {
			int a = 3;
			int b = 4;
	
			System.out.println(a & b);//0
			System.out.println(a | b);//7
			System.out.println(a ^ b);//7
			System.out.println(~b);//-5
			System.out.println(~a);//-4
			System.out.println(16<<2);//左移64
			System.out.println(16>>2);//右移4
			System.out.println(16>>>2);//无符号右移4
		}
	}

```
运算过程如下:

a=3	=>	00000000 00000000 00000000 00000011

b=4 =>  00000000 00000000 00000000 00000100

```java

	  00000000 00000000 00000000 00000011
	& 00000000 00000000 00000000 00000100
	 ------------------------------------
	  00000000 00000000 00000000 00000000
	
	  00000000 00000000 00000000 00000011
	| 00000000 00000000 00000000 00000100
	 ------------------------------------
	  00000000 00000000 00000000 00000111
	
	  00000000 00000000 00000000 00000011
	^ 00000000 00000000 00000000 00000100
	 ------------------------------------
	  00000000 00000000 00000000 00000111

	b:00000000 00000000 00000000 00000100	
	~ 按位取反，就是针对b这个二进制数据，所有的0变1,1变0。
	补码	11111111 11111111 11111111 11111011
	反码	11111111 11111111 11111111 11111010
	原码	10000000 00000000 00000000 00000101

```

对于反码时有一个规律: ~a=-(a+1);

### 进制之间的互相转换

对于右移>>n表示数字除以2^n,而左移则表示数字乘以2^n,知道这个规律后, 将十进制数字转换为2^n进制时就可以使用位运算了;下面是一段将进制转为2^n进制的代码,当然了2^n有限制.

```java

	private static String toUnsignedString(int i, int shift) {
	  char[] buf = new char[32];
	  int charPos = 32;
	  int radix = 1 << shift;
	  int mask = radix - 1;
	  do {
	      buf[--charPos] = digits[i & mask];
	      i >>>= shift;
	  } while (i != 0);
	  return new String(buf, charPos, (32 - charPos));
	 }

```

### 任意进制之间的转换

由于任意进制的转换,并非所有都满足2^n,此处就不太适合使用位运算了.

```java

	final static char[] digits = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };

    public int toCustomNumeric(String s, int system) {
        char[] buf = new char[s.length()];
        s.getChars(0, s.length(), buf, 0);
        long num = 0;
        for (int i = 0; i < buf.length; i++) {
            for (int j = 0; j < digits.length; j++) {
                if (digits[j] == buf[i]) {
                    num += j * Math.pow(system, buf.length - i - 1);
                    break;
                }
            }
        }
        return (int) num;
    }

    public String toCustomNumericString(int i, int system) {
        long num = 0;
        if (i < 0) {
            num = ((long) 2 * 0x7fffffff) + i + 2;
        } else {
            num = i;
        }
        char[] buf = new char[32];
        int charPos = 32;
        while ((num / system) > 0) {
            buf[--charPos] = digits[(int) (num % system)];
            num /= system;
        }
        buf[--charPos] = digits[(int) (num % system)];
        return new String(buf, charPos, (32 - charPos));
    }

```
