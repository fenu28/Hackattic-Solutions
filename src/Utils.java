public class Utils {

    //Mapping as per RFC 4648
    private static final char[] base64Mapping = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static String convertToBinary(char c) {
        int val = c;
        String binary = "";
        while (val != 0) {
            int rem = val % 2;
            val = val / 2;
            binary = binary + String.valueOf(rem);
        }

        int j = binary.length() - 1;
        String reversedString = "";
        while (j >= 0) {
            reversedString = reversedString + binary.charAt(j);
            j--;
        }
        return reversedString;
    }

    public static int convertToInt(String binary) {
        int j = binary.length() - 1;
        int ans = 0;
        int pow = 0;
        while (j >= 0) {
            if (binary.charAt(j) == '1')
                ans = ans + (int) Math.pow(2.0, pow);
            pow++;
            j--;
        }

        return ans;
    }

    public static String encodeToBase64(String s) {
        String encodedString = "";
        String binary = "";
        for (int i = 0; i < s.length(); i++) {
            String tempBinary = "";
            tempBinary = tempBinary + convertToBinary(s.charAt(i));
            if (tempBinary.length() != 8) {
                int rem = 8 - tempBinary.length();
                String zeros = "";
                for (int j = 0; j < rem; j++) {
                    zeros = zeros + "0";
                }
                tempBinary = zeros + tempBinary;
            }
            binary = binary + tempBinary;
        }

        int equalToAppend = 0;
        for (int i = 0; i < binary.length(); i += 6) {
            String binaryTemp = "";
            if (i + 6 > binary.length()) {
                if (binary.length() - i == 2) {
                    binaryTemp = binary.substring(i, i + 2);
                    binaryTemp = binaryTemp + "0000";
                    equalToAppend = 2;
                } else {
                    binaryTemp = binary.substring(i, i + 4);
                    binaryTemp = binaryTemp + "00";
                    equalToAppend = 1;
                }
            } else {
                binaryTemp = binary.substring(i, i + 6);
            }
            int num = convertToInt(binaryTemp);
            encodedString = encodedString + String.valueOf(base64Mapping[num]);
            if (equalToAppend == 1) {
                encodedString = encodedString + "=";
            } else if (equalToAppend == 2) {
                encodedString = encodedString + "==";
            }
        }
        return encodedString;
    }

    public static String decodeFromBase64(String encodedBase64) {
        String decodedString = "";
        String binary = "";
        for (int i = 0; i < encodedBase64.length(); i++) {
            if (encodedBase64.charAt(i) == '=')
                continue;
            String tempBinary = "";
            tempBinary = tempBinary + convertToBinary((char) getKeyFromBase64Map(encodedBase64.charAt(i)));
            if (tempBinary.length() != 6) {
                int rem = 6 - tempBinary.length();
                String zeros = "";
                for (int j = 0; j < rem; j++) {
                    zeros = zeros + "0";
                }
                tempBinary = zeros + tempBinary;
            }
            binary = binary + tempBinary;
        }
        //System.out.println(binary);
        if (binary.length() % 8 == 2) {
            binary = binary.substring(0, binary.length() - 2);
        } else if (binary.length() % 8 == 4) {
            binary = binary.substring(0, binary.length() - 4);
        }
        for (int i = 0; i < binary.length(); i += 8) {
            String binaryTemp = "";
            binaryTemp = binary.substring(i, i + 8);
            //System.out.println(binaryTemp);
            int num = convertToInt(binaryTemp);
            decodedString = decodedString + (char) num;
        }

        return decodedString;
    }

    public static int getKeyFromBase64Map(char c) {
        for (int i = 0; i < 64; i++) {
            if (base64Mapping[i] == c) {
                return i;
            }
        }
        return -1;
    }

    public static int binaryToDecimal(String s) {
        byte b = 0;
        int pow = 0;
        for (int i = 7; i >= 0; i--) {
            if (s.charAt(i) == '1')
                b = (byte) (b + (byte) (Math.pow(2.0, pow)));
            pow++;
        }
        return b;
    }


    public static String stringToBinary(String s) {
        String binary = "";
        for (int i = 0; i < s.length(); i++) {
            String tempBinary = "";
            tempBinary = tempBinary + convertToBinary(s.charAt(i));
            if (tempBinary.length() != 8) {
                int rem = 8 - tempBinary.length();
                String zeros = "";
                for (int j = 0; j < rem; j++) {
                    zeros = zeros + "0";
                }
                tempBinary = zeros + tempBinary;
            }
            binary = binary + tempBinary;
        }
        return binary;
    }

    public static byte[] extractBytes(String s) {
        byte[] bytes = new byte[32];
        int k = 0;
        String temp = "";
        for (int i = 0; i < s.length(); i++) {
            temp = temp + s.charAt(i);
            if (temp.length() == 8) {
                byte b = (byte) binaryToDecimal(temp);
                bytes[k] = b;
                k++;
                temp = "";
            }
        }
        return bytes;
    }

    public static String convertByteToBinary(byte b){
        String binary = "";
        while(b!=0){
            if(b%2==0)
                binary = binary + "0";
            else
                binary = binary + "1";
            b= (byte) (b/(byte)2);
        }

        //reversing the string
        String tempBinary = "";
        for(int i = binary.length()-1;i>=0;i--){
            tempBinary = tempBinary + binary.charAt(i);
        }
        binary = tempBinary;

        if(binary.length()!=8){
            int rem = 8 - binary.length();
            for(int i = 0;i<rem;i++){
                binary = "0"+binary;
            }
        }
        return binary;
    }
}
