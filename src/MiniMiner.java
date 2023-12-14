import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import org.json.*;


public class MiniMiner {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        //Receiving the JSON
        String problemURL = "https://hackattic.com/challenges/mini_miner/problem?access_token=8bb59bafa4706a08";
        String response = Utils.getProblemStatement(problemURL);
        System.out.println(response);

        //Creating JSON from response String
        JSONObject jsonObject = new JSONObject(response);
        System.out.println(jsonObject);
        JSONObject block = (JSONObject) jsonObject.get("block");
        System.out.println(block.toString());

        int difficulty = (Integer) jsonObject.get("difficulty");

        //Obtaining the nonce from current block
        int nonce = calculateNonce(block, difficulty);

        //Sending the nonce as solution
        String jsonResponse = "{\"nonce\":" + nonce + "}";

        String postToken = "https://hackattic.com/challenges/mini_miner/solve?access_token=8bb59bafa4706a08";
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

    public static int calculateNonce(JSONObject block, int difficulty) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        int nonce = 0;
        block.put("nonce", nonce);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = md.digest(block.toString().getBytes("UTF-8"));
        while (getZerosFromBytes(bytes) != difficulty) {
            nonce++;
            block.put("nonce", nonce);
            bytes = md.digest(block.toString().getBytes("UTF-8"));
        }
        return nonce;
    }

    public static int getZerosFromBytes(byte[] bytes) {
        int n = bytes.length;
        String bitString = "";

        for (int i = 0; i < n; i++) {
            byte b = bytes[i];
            String binary = Utils.convertByteToBinary(b);
            bitString = bitString + binary;
        }

        int zeros = 0;
        for (int i = 0; i < bitString.length(); i++) {
            if (bitString.charAt(i) == '1')
                break;
            zeros++;
        }

        return zeros;
    }
}
