import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        RSA rsa = new RSA();
        System.out.println("p:\t\t\t\t\t" + rsa.getP() + "\t binary: " + RSA.getBinary(rsa.getP()));
        System.out.println("q:\t\t\t\t\t" + rsa.getQ() + "\t binary: " + RSA.getBinary(rsa.getQ()));

        System.out.println("n=p*q:\t\t\t\t" + rsa.getN());
        System.out.println("φ(n)=(p-1)*(q-1):\t" + rsa.getPhiN());
        System.out.println("e:\t\t\t\t\t" + rsa.getE());
        System.out.println("d:\t\t\t\t\t" + rsa.getD());

        System.out.println();

        Key pu = rsa.getPublicKey();
        Key pr = rsa.getPrivateKey();
        String text = Key.txt2String("./src/plaintext.txt");

        ArrayList<String> encoded = pu.encode(text);
        /* 编码的时候，因为文章单词长度为奇数，最后一个字母凑不成分组，所以会添加一个a */
        System.out.println("encoded:\t\t\t" + encoded);
        String decoded = pr.decode(encoded);
        System.out.println("decoded:\t\t\t" + decoded);

        /* 证明相等 */
        String textWithoutSpeicalChar = text.replaceAll("[-\u00AD0123456789.,:;!?\"‘’'—–_(){} \n\t]", "");
        if(textWithoutSpeicalChar.length() % 2 != 0) textWithoutSpeicalChar += "a";
        System.out.println("decode text == source text? " + decoded.equals(textWithoutSpeicalChar));
    }

}
