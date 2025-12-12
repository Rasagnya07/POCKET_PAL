//EMICalculator
// CO Mapping: CO1, CO2, CO4
public class EMICalculator {
    public static double calculateEMI(double principal, double annualRatePct, int months) {
        if (months<=0) return 0;
        double r = annualRatePct/12.0/100.0;
        if (r==0) return principal/months;
        double pow = Math.pow(1+r, months);
        return (principal*r*pow)/(pow-1);
    }
}
