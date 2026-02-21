import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ShamirSecretSharing {

    static class Point {
        BigInteger x;
        BigInteger y;

        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static BigInteger reconstructSecret(List<Point> points, int k) {
        BigInteger secret = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger xi = points.get(i).x;
            BigInteger yi = points.get(i).y;

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i == j) continue;

                BigInteger xj = points.get(j).x;

                numerator = numerator.multiply(xj.negate());
                denominator = denominator.multiply(xi.subtract(xj));
            }

            BigInteger term = yi.multiply(numerator).divide(denominator);
            secret = secret.add(term);
        }

        return secret;
    }

    public static void main(String[] args) throws Exception {

        String content = new String(Files.readAllBytes(Paths.get("input.json")));

        java.util.regex.Pattern kPattern = java.util.regex.Pattern.compile("\"k\"\\s*:\\s*(\\d+)");
        java.util.regex.Matcher kMatcher = kPattern.matcher(content);

        int k;
        if (kMatcher.find()) {
            k = Integer.parseInt(kMatcher.group(1));
        } else {
            throw new RuntimeException("k value not found in JSON");
        }

        List<Point> points = new ArrayList<>();

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            "\"(\\d+)\"\\s*:\\s*\\{\\s*\"base\"\\s*:\\s*\"(\\d+)\"\\s*,\\s*\"value\"\\s*:\\s*\"([^\"]+)\""
        );
        java.util.regex.Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String xStr = matcher.group(1);
            int base = Integer.parseInt(matcher.group(2));
            String value = matcher.group(3);

            BigInteger x = new BigInteger(xStr);
            BigInteger y = new BigInteger(value, base);

            points.add(new Point(x, y));
        }

        points.sort(Comparator.comparing(p -> p.x));
        List<Point> selected = points.subList(0, k);

        BigInteger secret = reconstructSecret(selected, k);
        System.out.println("Secret (f(0)) = " + secret);

       
    }
}