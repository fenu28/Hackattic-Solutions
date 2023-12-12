import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class HelpMeUnpack {
    public static void main(String[] args) throws IOException {

        //Get the problem statement
        String getToken = "https://hackattic.com/challenges/help_me_unpack/problem?access_token=8bb59bafa4706a08";
        URL tokenUrl = new URL(getToken);
        HttpURLConnection connection = (HttpURLConnection) tokenUrl.openConnection();
        connection.setRequestMethod("GET");
        int connectionCode = connection.getResponseCode();
        if (connectionCode == HttpURLConnection.HTTP_OK) {
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String s = input.readLine();
            StringBuffer response = new StringBuffer();
            while (s != null) {
                response.append(s);
                s = input.readLine();
            }
            input.close();

            String responseString = response.toString();
            String base64String = responseString.split("\"")[3];

            //Decode the Base64 string
            String decodedString = Utils.decodeFromBase64(base64String);

            //Get each individual strings
            String intString = Utils.stringToBinary(decodedString.substring(0, 4));
            String unsignedIntString = Utils.stringToBinary(decodedString.substring(4, 8));
            String shortString = Utils.stringToBinary(decodedString.substring(8, 12));
            String floatString = Utils.stringToBinary(decodedString.substring(12, 16));
            String doubleString = Utils.stringToBinary(decodedString.substring(16, 24));
            String bigEndianDoubleString = Utils.stringToBinary(decodedString.substring(24));

            String fullDecodedString = intString + unsignedIntString + shortString + floatString + doubleString + bigEndianDoubleString;

            //Extract bytes from decoded string
            byte[] bytes = Utils.extractBytes(fullDecodedString);

            //Extract int
            byte[] intBytes = new byte[]{bytes[0], bytes[1], bytes[2], bytes[3]};
            int intVal = ByteBuffer.wrap(intBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

            //Extract unsigned int
            byte[] uIntBytes = new byte[]{bytes[4], bytes[5], bytes[6], bytes[7]};
            int uIntValTemp = ByteBuffer.wrap(uIntBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
            long uIntVal = uIntValTemp & 0x00000000FFFFFFFFL;

            //Extract bytes
            byte[] shortBytes = new byte[]{bytes[8], bytes[9], bytes[10], bytes[11]};
            short shortVal = ByteBuffer.wrap(shortBytes).order(ByteOrder.LITTLE_ENDIAN).getShort();

            //Extract float
            byte[] floatBytes = new byte[]{bytes[12], bytes[13], bytes[14], bytes[15]};
            float floatVal = ByteBuffer.wrap(floatBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();

            //Extract double
            byte[] doubleBytes = new byte[]{bytes[16], bytes[17], bytes[18], bytes[19], bytes[20], bytes[21], bytes[22], bytes[23]};
            double doubleVal = ByteBuffer.wrap(doubleBytes).order(ByteOrder.LITTLE_ENDIAN).getDouble();

            //Extract double in BigEndian
            byte[] doubleBEBytes = new byte[]{bytes[24], bytes[25], bytes[26], bytes[27], bytes[28], bytes[29], bytes[30], bytes[31]};
            double doubleValBE = ByteBuffer.wrap(doubleBEBytes).order(ByteOrder.BIG_ENDIAN).getDouble();

            //Creating JSON response
            String jsonResponse = "{\"int\":" + intVal
                    + ",\"uint\":" + uIntVal
                    + ",\"short\":" + shortVal
                    + ",\"float\":" + floatVal
                    + ",\"double\":" + doubleVal
                    + ",\"big_endian_double\":" + doubleValBE
                    + "}";


            String postToken = "https://hackattic.com/challenges/help_me_unpack/solve?access_token=8bb59bafa4706a08";
            URL postUrl = new URL(postToken);
            HttpURLConnection postConnection = (HttpURLConnection) postUrl.openConnection();
            postConnection.setRequestMethod("POST");
            postConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            postConnection.setDoOutput(true);
            postConnection.setDoInput(true);

            //Writing the JSON string in output stream
            OutputStream os = postConnection.getOutputStream();
            os.write(jsonResponse.getBytes());
            os.close();


            //Reading the response received
            BufferedReader postInput = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
            String postLine = postInput.readLine();
            StringBuffer postResponse = new StringBuffer();
            while (postLine != null) {
                postResponse.append(postLine);
                postLine = postInput.readLine();
            }
            postInput.close();
            System.out.println(postResponse.toString());
        }

    }
}
