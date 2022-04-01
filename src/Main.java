import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.QuickChart;
import com.xeiam.xchart.SwingWrapper;

import java.io.IOException;
import java.util.Arrays;

import static java.lang.Math.sqrt;

public class Main {
    static double LEFTMOST_ENDPOINT = 0.0;
    static double RIGHTMOST_ENDPOINT = 2.0;
    static double STEP_COUNT = 20.0;
    static double STEP_SIZE = (RIGHTMOST_ENDPOINT - LEFTMOST_ENDPOINT) / STEP_COUNT;
    static double SUBINTERVAL_PROBABILITY = 1 / STEP_COUNT;
    static double left_endpoint = LEFTMOST_ENDPOINT;
    static double right_endpoint = RIGHTMOST_ENDPOINT;
    static double A = 1;
    static double B = -4;
    static double c;
    public static void main(String[] args) throws IOException {
        buildNumber(getCollectionOfRandomNumbers((int) STEP_COUNT));
    }


    public static void buildNumber(double[] x) throws IOException {
        double[] f_theor = new double[x.length];  //# теоритическая функция распределения
        double[] f_exp = new double[x.length];    //# экспериментальная функция распределения
        double av_sum = Arrays.stream(x).sum();
        double curr_sum = 0.0;
        double test = 0.0;
        for (int i = 0; x.length > i; i++) {
            curr_sum += x[i];
            //нормирование чтоб меньше 1 были
            test = (curr_sum / av_sum);
            f_exp[i] = test;
            f_theor[i] = 0.5 * x[i];
        }
        Chart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", x, f_exp);
        Chart chart1 = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", x, f_theor);

        calmagorovP(f_theor, f_exp);
// Show it
        new SwingWrapper(chart).displayChart();
        new SwingWrapper(chart1).displayChart();

// Save it
        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.PNG);

// or save it in high-res
        BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapEncoder.BitmapFormat.PNG, 300);

    }


    public static double[] getIntervals() {
        double[] intervals = new double[(int) STEP_COUNT + 1];
        intervals[0] = 0.0;
        for (int i = 1; STEP_COUNT > i; i++) {
            c = 4 * left_endpoint + 4 * SUBINTERVAL_PROBABILITY - Math.pow(left_endpoint, 2);
            right_endpoint = solveTheQuadraticEquation(A, B, c);
            left_endpoint = right_endpoint;
            intervals[i] = left_endpoint;
        }
        return intervals;
    }


    public static Double solveTheQuadraticEquation(double a, double b, double c) {
        double discriminant = b * b - 4 * a * c;
        double left_root = (-b - sqrt(discriminant)) / (2 * a);
        double right_root = (-b + sqrt(discriminant)) / (2 * a);
        return Math.min(left_root, right_root);
    }

    public static double[] getCollectionOfRandomNumbers(int interval_count) {
        double[] s = getIntervals();
        double[] n = new double[(int) STEP_COUNT];
        double xi;
        n[0] = 0.0;
        for (int i = 1; interval_count > i; i++) {
            xi = Math.random();
            n[i] = (s[i] + (s[i + 1] - s[i]) * xi);
            if (i > 2) {
                if (n[i] < n[i - 1]) {
                    n[i] = s[i];
                }
            }
        }
        return n;
    }

    public static void calmagorovP(double[] teor, double[] exp) {
        double maximum = 0.0;
        double dif;

        for (int i = 0; teor.length > i; i++) {
            dif = Math.abs(teor[i] - exp[i]);
            if (dif > maximum) {
                maximum = dif;
            }
        }
        System.out.println("Значение критерия Колмагорова-Смирнова: " + maximum);
    }


}
