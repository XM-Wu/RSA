import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class RSA {
    private BigInteger p;
    private BigInteger q;
    private BigInteger n, phiN, e, d, noCare;

    private Key publicKey, privateKey;


    RSA() {
        this(128);
    }

    RSA(int bitLength) {
        // 循环是为了防止d小于0
        do {
            p = generateKey(bitLength);

            q = generateKey(bitLength);
            while (q.equals(p))
                q = generateKey(bitLength);

            n = p.multiply(q);


            phiN = q.subtract(BigInteger.valueOf(1)).multiply(p.subtract(BigInteger.valueOf(1)));


            do {
                Random random = new Random();
                e = new BigInteger((int) (Math.random() * 126 + 1), random);
//                e = new BigInteger(126, random);
//                System.out.println("e：尝试随机数: " + e);
            } while (!isPrimeToEachOther(e, phiN));


            exgcd(e, phiN);

            publicKey = new Key(e, n);
            privateKey = new Key(d, n);
        } while (d.compareTo(BigInteger.ZERO) <= 0);
    }

    private BigInteger generateKey(int bitlength) {
        Random r = new Random();
        BigInteger bigInteger = BigInteger.probablePrime(bitlength, r);
        while (!bigInteger.isProbablePrime(256)) {
            bigInteger = BigInteger.probablePrime(bitlength, r);
        }

        return bigInteger;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getPhiN() {
        return phiN;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getD() {
        return d;
    }

    public Key getPublicKey() {
        return publicKey;
    }

    public Key getPrivateKey() {
        return privateKey;
    }

    public BigInteger getNoCare() {
        return noCare;
    }

    public static String getBinary(BigInteger bi) {
        return bi.toString(2);
    }

    // 辗转相除法
    public boolean isPrimeToEachOther(BigInteger n, BigInteger m) {
        BigInteger a, b;
        if (n.compareTo(m) > 0) {
            a = n;
            b = m;
        } else {
            a = m;
            b = n;
        }

        while (!b.equals(BigInteger.valueOf(0))) {
            BigInteger i = a.mod(b);
            a = b;
            b = i;
        }
//        System.out.println("最大公约数为" + a);

        return a.equals(BigInteger.valueOf(1));
    }

    // 扩展的欧几里得算法
    public BigInteger exgcd(BigInteger E, BigInteger PHI) {
        if (PHI.equals(BigInteger.valueOf(0))) {
            d = BigInteger.valueOf(1);
            noCare = BigInteger.valueOf(0);
            return E;
        }
        BigInteger r = exgcd(PHI, E.mod(PHI));
        BigInteger tmp = noCare;
        noCare = d.subtract(E.divide(PHI).multiply(noCare));
        d = tmp;
        return r;
    }

}

class Key {
    public BigInteger num;
    public BigInteger n;

    public Key(BigInteger num, BigInteger n) {
        this.num = num;
        this.n = n;
    }

    public ArrayList<String> encode(String text) {

        ArrayList<String> standard = standardize(text);

        ArrayList<String> encoded = new ArrayList<>();

        for (String s : standard) {
            encoded.add(exp_mod(new BigInteger(s), num, n).toString());
        }

        return encoded;
    }

    public String decode(ArrayList<String> code) {
        StringBuilder sb = new StringBuilder();
        for (String s : code) {
            BigInteger bi = exp_mod(new BigInteger(s), num, n);
            StringBuilder source = new StringBuilder(bi.toString());
            while (source.length() < 4) {
                source.insert(0, "0");
            }

            sb.append(numString2char(source.charAt(0) + "" + source.charAt(1)));
            sb.append(numString2char(source.charAt(2) + "" + source.charAt(3)));
        }
        return sb.toString();
    }

    // 去除特殊符号，并将字符转化成数字
    public static ArrayList<String> standardize(String text) {
        System.out.println("Source：\t\t\t" + text);
        text = text.replaceAll("[-\u00AD0123456789.,:;!?\"‘’'—–_(){} \n\t]", "");


        int len = text.length();
        ArrayList<String> strList = new ArrayList<>();
        for (int i = 0; i < len; i += 2) {
            StringBuilder sb1 = new StringBuilder();
            sb1.append(char2numString(text.charAt(i)));

            if (i + 1 == len) {
                sb1.append("00"); // 缺一个字母补上a
                strList.add(sb1.toString());
                break;
            }

            sb1.append(char2numString(text.charAt(i + 1)));
            strList.add(sb1.toString());
        }

        System.out.print("standardized:\t\t");
        System.out.println(strList);

        return strList;
    }

    public static String char2numString(char a) {
        if (a >= 97) {
            if (a <= 106) return "0" + ((int) (a) - 97);
            return ((int) a - 97) + "";
        }

        return ((int) a - 65 + 26) + "";
    }

    public static char numString2char(String s) {
        int i = Integer.parseInt(s);

        if (i <= 25) return (char) (i + 97);
        return (char) (i - 26 + 65);
    }

    // 读取文件
    public static String txt2String(String path) {
        FileReader fileReader;
        BufferedReader bufferedReader;
        StringBuilder sb = new StringBuilder();

        try {
            fileReader = new FileReader(path);
            bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();

            while (line != null) {
                sb.append(line);
                line = bufferedReader.readLine();
            }

            bufferedReader.close();
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return sb.toString();
    }

    // 大数模指数运算
    BigInteger exp_mod(BigInteger a, BigInteger n, BigInteger b) {
        BigInteger t;
        if (n.equals(BigInteger.ZERO)) return BigInteger.ONE.mod(b);
        if (n.equals(BigInteger.ONE)) return a.mod(b);
        t = exp_mod(a, n.divide(BigInteger.TWO), b);
        t = t.multiply(t).mod(b);
        if ((n.and(BigInteger.ONE)).equals(BigInteger.ONE)) t = t.multiply(a).mod(b);
        return t;
    }
}