import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

enum Keys {
    NUMBER_SQUARE, NUMBER_SIZE, SIZE_NUMBER, DIREITA, ESQUERDA
}

public class Main {

    private static Integer NUM = null;
    private static ArrayList<Integer> ARRAY = new ArrayList<>();
    private static HashMap<Keys, HashMap<Integer, ArrayList<Integer>>> FINAL_AMOUNT = new HashMap<>();
    private static ArrayList<Integer> SQUARES = new ArrayList<>();
    private static HashMap<Keys, HashMap<Integer, Object>> MATCHNUMBERS = new HashMap<>();

    public static void main(String args[]) {
        System.out.println(build(37));
    }

    public static List<Integer> build(int num) {
        NUM = num;

        MATCHNUMBERS.put(Keys.NUMBER_SQUARE, new HashMap<>());
        MATCHNUMBERS.put(Keys.NUMBER_SIZE, new HashMap<>());
        MATCHNUMBERS.put(Keys.SIZE_NUMBER, new HashMap<>());

        FINAL_AMOUNT.put(Keys.DIREITA, new HashMap<>());
        FINAL_AMOUNT.put(Keys.ESQUERDA, new HashMap<>());

        generateArray();
        findSquares();
        matchNumbers();
        matchSizeNumbers();

        return organizeArray();
    }

    public static void generateArray() {
        for (int i = NUM; i > 0; i--) {
            ARRAY.add(i);
        }
    }

    public static void findSquares() {
        int count = 2;
        while (true) {
            int square = (int) Math.pow(count, 2);
            if (square > (NUM + (NUM + 1))) {
                break;
            }
            SQUARES.add(square);
            count++;
        }
    }

    public static void matchNumbers() {
        for (Integer c : ARRAY) {
            ArrayList<Integer> list = new ArrayList<>();
            for (Integer i : SQUARES) {
                if (i > c) {
                    int number = i - c;
                    if (number <= NUM && number != c && ARRAY.contains(number)) {
                        list.add(number);
                    }
                }
            }
            if (!list.isEmpty()) {
                MATCHNUMBERS.get(Keys.NUMBER_SQUARE).put(c, list);
            }
        }
    }

    public static void matchSizeNumbers() {
        for (Integer c : MATCHNUMBERS.get(Keys.NUMBER_SQUARE).keySet()) {
            ArrayList<Integer> cSizeList = (ArrayList<Integer>) MATCHNUMBERS.get(Keys.NUMBER_SQUARE).get(c);
            int sizeList = cSizeList.size();
            if (MATCHNUMBERS.get(Keys.SIZE_NUMBER).get(sizeList) == null) {
                MATCHNUMBERS.get(Keys.SIZE_NUMBER).put(sizeList, new ArrayList<>());
            }
            if (MATCHNUMBERS.get(Keys.NUMBER_SIZE).get(c) == null) {
                MATCHNUMBERS.get(Keys.NUMBER_SIZE).put(c, sizeList);
            }
            ArrayList<Integer> sizeListc = (ArrayList<Integer>) MATCHNUMBERS.get(Keys.SIZE_NUMBER).get(sizeList);
            sizeListc.add(c);
            MATCHNUMBERS.get(Keys.SIZE_NUMBER).put(sizeList, sizeListc);
        }
    }

    public static void verifySizeNumber(Integer num) {
        int sizeList = (int) MATCHNUMBERS.get(Keys.NUMBER_SIZE).get(num);
        ArrayList<Integer> sizeListObj = (ArrayList<Integer>) MATCHNUMBERS.get(Keys.SIZE_NUMBER).get(sizeList);
        sizeListObj.remove(num);
        if (sizeListObj.size() == 0) {
            MATCHNUMBERS.get(Keys.SIZE_NUMBER).remove(sizeList);
        } else {
            MATCHNUMBERS.get(Keys.SIZE_NUMBER).put(sizeList, sizeListObj);
        }
        MATCHNUMBERS.get(Keys.NUMBER_SIZE).put(num, sizeList - 1);
    }

    public static void deleteNumberAssociations(Integer num, Integer proxNum) {
        verifySizeNumber(num);
        int sizeList = (int) MATCHNUMBERS.get(Keys.NUMBER_SIZE).get(num);
        if (sizeList != 0) {
            if (MATCHNUMBERS.get(Keys.SIZE_NUMBER).get(sizeList) == null) {
                MATCHNUMBERS.get(Keys.SIZE_NUMBER).put(sizeList, new ArrayList<Integer>());
            }
            ArrayList<Integer> sizeObj = (ArrayList<Integer>) MATCHNUMBERS.get(Keys.SIZE_NUMBER).get(sizeList);
            sizeObj.add(num);
            MATCHNUMBERS.get(Keys.SIZE_NUMBER).put(sizeList, sizeObj);
            ArrayList<Integer> squareObj = (ArrayList<Integer>) MATCHNUMBERS.get(Keys.NUMBER_SQUARE).get(num);
            squareObj.remove(proxNum);
            MATCHNUMBERS.get(Keys.NUMBER_SQUARE).put(num, squareObj);
        } else {
            deleteNumber(num);
        }
    }

    public static void deleteNumber(Integer num) {
        MATCHNUMBERS.get(Keys.NUMBER_SIZE).remove(num);
        MATCHNUMBERS.get(Keys.NUMBER_SQUARE).remove(num);
    }

    public static void removeNumbers(Integer num, Integer proxNum) {
        List<Integer> list = Arrays.asList(num, proxNum);
        ArrayList<Boolean> listVerification = new ArrayList<>();
        for (Integer c : list) {
            ArrayList<Integer> direita = FINAL_AMOUNT.get(Keys.DIREITA).get(c);
            ArrayList<Integer> esquerda = FINAL_AMOUNT.get(Keys.ESQUERDA).get(c);
            if (direita == null && esquerda == null) {
                listVerification.add(true);
            } else {
                listVerification.add(false);
            }
        }
        for (int h = 0; h < listVerification.size(); h++) {
            Integer selected = list.get(h);
            Boolean value = listVerification.get(h);
            if (value) {
                ArrayList<Integer> squaresc = (ArrayList<Integer>) MATCHNUMBERS.get(Keys.NUMBER_SQUARE).get(selected);
                if (squaresc != null) {
                    for (Integer i : squaresc) {
                        ArrayList<Integer> squaresi = (ArrayList<Integer>) MATCHNUMBERS.get(Keys.NUMBER_SQUARE).get(i);
                        if (squaresi != null) {
                            if (squaresi.contains(selected)) {
                                deleteNumberAssociations(i, selected);
                            }
                        }
                    }
                }
                verifySizeNumber(selected);
                deleteNumber(selected);
                if (Collections.frequency(listVerification, true) == 1) {
                    return;
                }
            } else {
                deleteNumberAssociations(selected, selected == num ? proxNum : num);
            }
        }
    }

    public static List<Integer> organizeArray() {
        try {
            while (true) {
                List<Integer> keys = MATCHNUMBERS.get(Keys.SIZE_NUMBER).keySet().stream().collect(Collectors.toList());
                Integer num = ((ArrayList<Integer>) MATCHNUMBERS.get(Keys.SIZE_NUMBER).get(Collections.min(keys))).get(0);
                Integer senseProxNum = -1;
                Integer senseNum = -1;
                ArrayList<Integer> list = new ArrayList<>();
                Integer proxNum;
                ArrayList<Integer> squareObj = (ArrayList<Integer>) MATCHNUMBERS.get(Keys.NUMBER_SQUARE).get(num);
                for (Integer c : squareObj) {
                    Integer number = (Integer) MATCHNUMBERS.get(Keys.NUMBER_SIZE).get(c);
                    list.add(number);
                }
                Integer minSize = Collections.min(list);
                proxNum = squareObj.stream().filter(f -> MATCHNUMBERS.get(Keys.NUMBER_SIZE).get(f) == minSize).collect(Collectors.toList()).get(0);
                ArrayList<Integer> sequenceProxNum = new ArrayList<>();
                ArrayList<Integer> sequenceNum = new ArrayList<>();
                ArrayList<Integer> newSequence = new ArrayList<>();
                Boolean inFinalAmountNum = true;
                Boolean inFinalAmountProxNum = true;
                for (int i = 0; i < 2; i++) {
                    Integer sense = -1;
                    ArrayList<Integer> sequence = new ArrayList<>();
                    Boolean inFinalAmout = true;
                    Integer number = i == 0 ? num : proxNum;
                    if (FINAL_AMOUNT.get(Keys.DIREITA).get(number) != null) {
                        sense = -1;
                        sequence = FINAL_AMOUNT.get(Keys.DIREITA).get(number);
                    } else if (FINAL_AMOUNT.get(Keys.ESQUERDA).get(number) != null) {
                        sense = 0;
                        sequence = FINAL_AMOUNT.get(Keys.ESQUERDA).get(number);
                    } else {
                        inFinalAmout = false;
                        sequence.add(number);
                    }
                    if (i == 0) {
                        senseNum = sense;
                        sequenceNum = sequence;
                        inFinalAmountNum = inFinalAmout;
                    } else {
                        senseProxNum = sense;
                        sequenceProxNum = sequence;
                        inFinalAmountProxNum = inFinalAmout;
                    }
                }
                List<Boolean> listVerification = Arrays.asList(inFinalAmountNum, inFinalAmountProxNum);
                for (int i = 0; i < listVerification.size(); i++) {
                    Boolean value = listVerification.get(i);
                    if (value) {
                        Boolean verificar = null;
                        Integer sense = i == 0 ? senseNum : senseProxNum;
                        for (int k = 0; k < 2; k++) {
                            verificar = k != 0 ? !verificar : sense == -1 ? true : false;
                            FINAL_AMOUNT.get(verificar ? Keys.DIREITA : Keys.ESQUERDA).remove(verificar ? i == 1 ? sequenceProxNum.get(sequenceProxNum.size() - 1) : sequenceNum.get(sequenceNum.size() - 1) : i == 1 ? sequenceProxNum.get(0) : sequenceNum.get(0));
                        }
                    }
                }
                if (senseNum == senseProxNum) {
                    Collections.reverse(sequenceProxNum);
                }
                Stream streamNewSequence = senseNum == -1 ? Stream.concat(sequenceNum.stream(), sequenceProxNum.stream()) : Stream.concat(sequenceProxNum.stream(), sequenceNum.stream());
                newSequence = (ArrayList<Integer>) streamNewSequence.collect(Collectors.toList());
                if (newSequence.size() == NUM) {
                    return newSequence;
                }
                Integer newSequenceDireita = newSequence.get(newSequence.size() - 1);
                Integer newSequenceEsquerda = newSequence.get(0);
                FINAL_AMOUNT.get(Keys.DIREITA).put(newSequenceDireita, newSequence);
                FINAL_AMOUNT.get(Keys.ESQUERDA).put(newSequenceEsquerda, newSequence);
                removeNumbers(num, proxNum);
                ArrayList<Integer> squareObjDireita = (ArrayList<Integer>) MATCHNUMBERS.get(Keys.NUMBER_SQUARE).get(newSequenceDireita);
                ArrayList<Integer> squareObjEsquerda = (ArrayList<Integer>) MATCHNUMBERS.get(Keys.NUMBER_SQUARE).get(newSequenceEsquerda);
                squareObjDireita = squareObjDireita == null ? new ArrayList<>() : squareObjDireita;
                squareObjEsquerda = squareObjEsquerda == null ? new ArrayList<>() : squareObjEsquerda;
                if (squareObjDireita.contains(newSequenceEsquerda)) {
                    deleteNumberAssociations(newSequenceDireita, newSequenceEsquerda);
                }
                if (squareObjEsquerda.contains(newSequenceDireita)) {
                    deleteNumberAssociations(newSequenceEsquerda, newSequenceDireita);
                }
            }
        } catch (Exception e) {
            return null;
        }
    }
}
