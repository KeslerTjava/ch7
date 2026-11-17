
// LOWEST AND HIGHEST PRICES
// File                 Lowest price  When      Highest price  When
// tiny.txt             $30.00        Mar 2020  $70.00         Jul 2020
// rising.txt           $20.00        Jan 2010  $48.14         Dec 2014
// falling.txt          $30.36        Dec 2014  $100.00        Jan 2010
// zigzag.txt           $35.00        Jul 2010  $65.00         Mar 2010
// aapl_2000_2010.txt   $7.07         Mar 2003  $223.02        Mar 2010
// amzn_2000_2010.txt   $5.97         Sep 2001  $135.91        Nov 2009
// msft_2000_2010.txt   $15.81        Feb 2009  $43.22         Mar 2000
// ibm_2000_2010.txt    $53.01        Sep 2002  $130.32        Dec 2009
// goog_2004_2010.txt   $102.37       Aug 2004  $707.00        Oct 2007
// sbux_2007_2016.txt   $4.09         Nov 2008  $62.16         Oct 2015
// sp500_1925_1945.txt  $4.77         Jun 1932  $31.30         Sep 1929
// sp500_1990_2025.txt  $307.12       Oct 1990  $6,853.03      Dec 2025
//
// Q: MSFT's highest price comes before its lowest. What does that mean for someone who
//    bought in 2000?
// A: They bought close to the peak ($43.22 in Mar 2000) and the price then fell to $15.81
//    by Feb 2009. There was no later month that was a better time to sell than the start,
//    so buying and holding lost money. $10,000 became about $7,237 after ten years.
//
// Q: Does the timing of the lowest month make sense for SBUX and the 1925-1945 S&P 500?
// A: Yes. SBUX bottomed in Nov 2008, right in the 2008 financial crisis (Lehman Brothers
//    failed in Sep 2008 and stocks kept falling into early 2009). The S&P 500 peaked in
//    Sep 1929, just before the Oct 1929 crash, and bottomed in Jun 1932, the depths of the
//    Great Depression, when banks were failing and unemployment was soaring.
//
// Challenge: indexOfBiggestDrop. Worst single month among the company stocks: AAPL,
//    -57.7% in Sep 2000 (Apple warned that profits would miss expectations). Others:
//    AMZN -41.1% (Feb 2001), MSFT -34.4% (Apr 2000), SBUX -32.1% (Nov 2008).
//
//  STEP E: COMPARING THE STRATEGIES
// File                 Buy and hold  Dip buyer   Moving average  My strategy
// tiny.txt             $12,975.00    $14,975.00  $10,000.00      $10,000.00
// rising.txt           $24,031.86    $10,000.00  $20,411.92      $20,411.92
// falling.txt          $3,095.64     $3,159.28   $10,000.00      $10,000.00
// zigzag.txt           $12,101.39    $38,071.99  $9,848.78       $9,848.78
// aapl_2000_2010.txt   $85,865.80    $36,610.49  $114,231.31     $112,480.21
// amzn_2000_2010.txt   $19,886.04    $14,192.52  $38,170.87      $43,084.93
// msft_2000_2010.txt   $7,237.50     $49,273.15  $7,208.23       $8,887.24
// ibm_2000_2010.txt    $12,467.97    $27,998.86  $7,549.75       $6,172.70
// goog_2004_2010.txt   $54,398.54    $13,655.41  $18,706.20      $19,849.52
// sbux_2007_2016.txt   $36,280.60    $15,759.50  $67,500.23      $77,370.85
//
// 1. The dip buyer was best on the most files (tiny, zigzag, msft, ibm). My strategy was
//    best on amzn and sbux, the moving average on aapl (and tied with mine on falling),
//    and buy and hold on rising and goog. No strategy was best on every file.
//
// 2. The dip buyer did badly on aapl_2000_2010 ($36,610 vs $85,866 for buy and hold). It
//    sells as soon as it is up 10%, so it kept cashing out early: it sold at $10.54 in
//    Jul 2003 and then sat in cash until Apr 2005, when it bought again at $36.06. It
//    missed the whole climb from about $10 to $36. Its profits were many small wins
//    capped at 10% (plus $20 in fees per round trip) while Apple's real gains were huge.
//    On rising.txt it never buys at all, because the price never drops two months in a
//    row, so it just sits at $10,000.
//
// 3. The moving average beat buy and hold on aapl_2000_2010 ($114,231 vs $85,866). Buy and
//    hold bought at $25.94 in Jan 2000 and rode the stock down to about $7 in 2003. The
//    moving average only bought in Nov 2001, went back to cash in Jun 2002, bought again
//    at $8.98 in May 2003, then rode the huge growth. It also sold at $113.66 in Sep 2008
//    on the way down in the crash and bought back in Apr 2009. It did well because Apple
//    had long trends in both directions, and following the trend avoided the worst parts.
//
// 4. My strategy (12-month average, buy above it, sell only if below 95% of it) beat buy
//    and hold on aapl, amzn, msft, sbux and falling, but lost to it on rising, zigzag, ibm,
//    goog and tiny. Compared with the plain moving average, the 5% cushion helped on amzn,
//    msft, goog and sbux, because it stopped selling on small dips and paying fees on
//    needless trades. It hurt a little on aapl and ibm. I chose the 5% after trying a few
//    values on these same files, so it is partly tuned to this data and the numbers are
//    probably a bit better than I'd get on stocks I haven't seen. It is still not a
//    guaranteed winner: it can't beat buy and hold on a stock that mostly goes up.
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
 
public class StockTrader {
 
    // Which strategy decide() uses: 0 = buyAndHold, 1 = dipBuyer, 2 = movingAverage, 3 = myStrategy
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
 
    // Formats a number as dollars with exactly two decimal places, e.g. 30 -> "$30.00".
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
 
    // Challenge: index of the month with the largest percentage drop from the month before.
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
 
    // Average of the last `months` prices, including today's. Caller must ensure enough data.
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
 
    // Step D
    // MY RULES ("Patient Trend Follower"): use the 12-month average like movingAverage, but
    // give the stock some breathing room before selling.
    //   1. Fewer than 12 prices of history: hold.
    //   2. Not holding and today's price is above the 12-month average: buy.
    //   3. Holding and today's price falls below 95% of the 12-month average: sell.
    //   4. Otherwise hold.
    // Why: the plain moving average sells the moment the price dips under the average, so a
    // stock that wiggles around its average gets bought and sold over and over, paying a $10
    // fee each time and often selling low and rebuying higher. Requiring a 5% drop below the
    // average means we ignore small wiggles but still get out in a real crash.
    public static String myStrategy(double[] history, boolean holding, double boughtAt) {
        if (history.length < 12) {
            return "HOLD";
        }
        double today = history[history.length - 1];
        double avg = average(history, 12);
        if (!holding && today > avg * BUY_BAND) {
            return "BUY";
        } else if (holding && today < avg * SELL_BAND) {
            return "SELL";
        }
        return "HOLD";
    }
    public static double BUY_BAND = 1.0;
    public static double SELL_BAND = 0.95;
}
 
