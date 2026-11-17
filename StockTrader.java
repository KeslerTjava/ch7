import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class StockTrader {

    // the stratergy: 0 = buyAndHold, 1 = dipBuyer, 2 = movingAverage, 3 = myStrategy
    public static int strategy = 0;

    public static void main(String[] args) throws FileNotFoundException {
        String fileName = args.length > 0 ? args[0] : "tiny.txt";
        if (args.length > 1) {
            strategy = Integer.parseInt(args[1]);
        }

        Scanner in = new Scanner(new File(fileName));
        String symbol = in.next();
        int n = in.nextInt();

        int[] years = new int[n];
        int[] months = new int[n];
        double[] prices = new double[n];

        loadData(in, years, months, prices);
        printData(years, months, prices);

        int low = indexOfLowest(prices);
        int high = indexOfHighest(prices);
        System.out.println(symbol + " lowest: " + money(prices[low]) + " in "
            + monthName(months[low]) + " " + years[low]);
        System.out.println(symbol + " highest: " + money(prices[high]) + " in "
            + monthName(months[high]) + " " + years[high]);

        int drop = indexOfBiggestDrop(prices);
        if (drop > 0) {
            double pct = (prices[drop] - prices[drop - 1]) / prices[drop - 1] * 100;
            double roundedPct = Math.round(pct * 10) / 10.0;
            System.out.println(symbol + " worst month: " + roundedPct + "% in "
                + monthName(months[drop]) + " " + years[drop]);
        }

        double result = simulate(years, months, prices);
        System.out.println("Final value: " + moneyWithCommas(result));
    }

    // ---------- Part 1 ----------

    public static void loadData(Scanner in, int[] years, int[] months, double[] prices) {
        for (int i = 0; i < years.length; i++) {
            years[i] = in.nextInt();
            months[i] = in.nextInt();
            prices[i] = in.nextDouble();
        }
    }

    public static String monthName(int month) {
        String[] names = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return names[month - 1];
    }

    public static void printData(int[] years, int[] months, double[] prices) {
        for (int i = 0; i < prices.length; i++) {
            System.out.println(monthName(months[i]) + " " + years[i] + ": " + money(prices[i]));
        }
    }

   
    public static String money(double amount) {
        long cents = Math.round(amount * 100);
        long dollars = cents / 100;
        long rest = cents % 100;
        String pennies = "" + rest;
        if (rest < 10) {
            pennies = "0" + rest;
        }
        return "$" + dollars + "." + pennies;
    }

    // Same as money, but with commas in the dollars, e.g. 12975 -> "$12,975.00".
    public static String moneyWithCommas(double amount) {
        String text = money(amount);               // "$12975.00"
        int dot = text.indexOf(".");
        String dollars = text.substring(1, dot);   // "12975"
        String withCommas = "";
        for (int i = 0; i < dollars.length(); i++) {
            if (i > 0 && (dollars.length() - i) % 3 == 0) {
                withCommas += ",";
            }
            withCommas += dollars.charAt(i);
        }
        return "$" + withCommas + text.substring(dot);
    }

    // ---------- Part 2 ----------

    // Strict < means a tie never replaces the earlier index.
    public static int indexOfLowest(double[] prices) {
        int lowest = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] < prices[lowest]) {
                lowest = i;
            }
        }
        return lowest;
    }

    public static int indexOfHighest(double[] prices) {
        int highest = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[highest]) {
                highest = i;
            }
        }
        return highest;
    }

    public static int indexOfBiggestDrop(double[] prices) {
        int worst = 0;
        double worstChange = 0.0;
        for (int i = 1; i < prices.length; i++) {
            double change = (prices[i] - prices[i - 1]) / prices[i - 1];
            if (change < worstChange) {
                worstChange = change;
                worst = i;
            }
        }
        return worst;
    }

    // ---------- Part 3 ----------

    public static final double START_CASH = 10000.0;
    public static final double FEE = 10.0;

    public static double simulate(int[] years, int[] months, double[] prices) {
        double cash = START_CASH;
        int shares = 0;
        double boughtAt = 0.0;

        for (int i = 0; i < prices.length; i++) {
            double[] history = Arrays.copyOf(prices, i + 1);
            String action = decide(history, shares > 0, boughtAt);

            if (action.equals("BUY") && shares == 0) {
                int canBuy = (int) ((cash - FEE) / prices[i]);
                if (canBuy > 0) {
                    shares = canBuy;
                    cash = cash - shares * prices[i] - FEE;
                    boughtAt = prices[i];
                    System.out.printf("%s %d: BUY %d shares at $%.2f%n",
                        monthName(months[i]), years[i], shares, prices[i]);
                }
            } else if (action.equals("SELL") && shares > 0) {
                cash = cash + shares * prices[i] - FEE;
                System.out.printf("%s %d: SELL %d shares at $%.2f%n",
                    monthName(months[i]), years[i], shares, prices[i]);
                shares = 0;
                boughtAt = 0.0;
            }
        }
        return cash + shares * prices[prices.length - 1];
    }

    public static String decide(double[] history, boolean holding, double boughtAt) {
        if (strategy == 1) {
            return dipBuyer(history, holding, boughtAt);
        } else if (strategy == 2) {
            return movingAverage(history, holding, boughtAt);
        } else if (strategy == 3) {
            return myStrategy(history, holding, boughtAt);
        }
        return buyAndHold(history, holding, boughtAt);
    }

    // Step A
    public static String buyAndHold(double[] history, boolean holding, double boughtAt) {
        return "BUY";
    }

    // Step B
    public static String dipBuyer(double[] history, boolean holding, double boughtAt) {
        int n = history.length;
        double today = history[n - 1];
        if (!holding) {
            if (n >= 3 && today < history[n - 2] && history[n - 2] < history[n - 3]) {
                return "BUY";
            }
        } else if (today >= boughtAt * 1.10) {
            return "SELL";
        }
        return "HOLD";
    }

    public static double average(double[] history, int months) {
        double sum = 0.0;
        for (int i = history.length - months; i < history.length; i++) {
            sum += history[i];
        }
        return sum / months;
    }

    // Step C
    public static String movingAverage(double[] history, boolean holding, double boughtAt) {
        if (history.length < 12) {
            return "HOLD";
        }
        double today = history[history.length - 1];
        double avg = average(history, 12);
        if (!holding && today > avg) {
            return "BUY";
        } else if (holding && today < avg) {
            return "SELL";
        }
        return "HOLD";
    }
