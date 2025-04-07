import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TaTest2 {

    private ByteArrayOutputStream outStream;

    public String ExpectedOutput(List<Integer> inputList) {
        StringBuilder sb = new StringBuilder();
        int count = inputList.get(0);

        for (int i = 1; i <= count; i+=2) {
            int a = inputList.get(i);
            int b = inputList.get(i+1);
            int result = a % b;
            sb.append(result);
            if (i < count) {
                sb.append(" ");
            }
        }

        return sb.toString().trim();
    }

    public List<Integer> generateRandomTestNumbers() {
        Random random = new Random();
        int pairCount = 5 + random.nextInt(16);
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < pairCount; i++) {
            int first, second;

            if (random.nextDouble() < 0.2) {
                first = random.nextBoolean() ? 0 : 1;
            } else {
                first = 1 + random.nextInt(2_147_483_000 - 1);
            }

            second = 1 + random.nextInt(2_147_483_000 - 1);

            result.add(first);
            result.add(second);
        }

        result.add(0, result.size());
        return result;
    }

    public void baseTest(List<Integer> inputList, String correctResult) {
        Process p;

        try {
            StringBuilder inputBuilder = new StringBuilder();
            for (int num : inputList) {
                inputBuilder.append(num).append("\\n");
            }

            if (inputBuilder.length() >= 2) {
                inputBuilder.setLength(inputBuilder.length() - 2);
            }

            String echoInput = "echo -e \"" + inputBuilder.toString() + "\"";
            String fullCmd = echoInput + " | java -jar lib/rars.jar nc src/main/java/solution.s";

            String[] cmd = {"/bin/bash", "-c", fullCmd};

            p = Runtime.getRuntime().exec(cmd);
            p.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String result = br.readLine().trim();
            br.close();

            assertEquals(correctResult, result);
            p.destroy();

        } catch (Exception e) {
            System.err.println("Execution error: " + e.getMessage());
            fail();
        }
    }

    @Before
    public void initStreams() {
        outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outStream));
    }

    @Test
    public void test2() {
        List<Integer> input = generateRandomTestNumbers();
        String correctOutput = ExpectedOutput(input);
        baseTest(input, correctOutput);
    }
}
