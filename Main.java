public class Main {
    public static void main(String args[]) {
        Amount amount = new Amount(23);
        amount.generateArray();
        amount.findSquares();
        amount.matchNumbers();
        amount.matchSizeNumbers();
        System.out.println(amount.organizeArray());
    }
}
