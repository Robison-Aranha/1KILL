import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    enum KeysMatchNumbers {
        NUMBER_SQUARE, NUMBER_SIZE, SIZE_NUMBER, DIREITA, ESQUERDA, IS_IN_AMOUNT
    }

    public static void main(String args[]) {
        long tempoInicial = System.currentTimeMillis();
        System.out.println(organizeArray(1400) == null ? false : true);
        long tempoFinal = System.currentTimeMillis();
        System.out.println("Tempo final: " + (tempoFinal - tempoInicial) + " ms");
        System.out.println("-=-=-=-==-=-=-==-==-=-=--==-");
    }

    public static List<Integer> organizeArray(Integer number) {
        List<Integer> edges = null;
        List<List<Integer>> tmpEdge = new ArrayList<>();
        while (true) {
            edges = generateMaterial(number, edges);
            if (edges.size() == number) {
                return edges;
            }
            if (tmpEdge.contains(edges) || edges.size() == 0) {
                return null;
            }
            tmpEdge.add(edges);
        }
    }

    public static List<Integer> generateMaterial(Integer number, List<Integer> edges) {
        Amount amount = generateAmount(number);
        if (amount == null) {
            return new ArrayList<>();
        }
        if (edges != null) {
            amount.populate(edges);
        }
        while (true) {
            Integer num = amount.findNextNum();
            Integer proxNum = amount.findProxNum(num);
            if (num != null && proxNum != null) {
                amount.updateCollection(num, proxNum);
            } else {
                if (amount.FINAL_ARRAY != null) {
                    return amount.FINAL_ARRAY;
                }
                if (amount.EDGES.size() >= 2) {
                    return amount.EDGES.subList(0, 2);
                } else {
                    return amount.EDGES;
                }

            }
            if (amount.EDGES.size() > 2 && amount.FINAL_ARRAY == null) {
                return amount.EDGES.subList(0, 2);
            }
        }
    }

    public static Amount generateAmount(Integer number) {
        Amount amount = new Amount(number);
        amount.findSquares();
        amount.matchNumbers();
        if (amount.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).size() == 0) {
            return null;
        }
        return amount;
    }

    public static class Amount {

        private Integer NUM;
        private HashMap<KeysMatchNumbers, HashMap<Integer, List<Integer>>> AMOUNT = new HashMap<>();
        private List<Integer> SQUARES = new ArrayList<>();
        private HashMap<KeysMatchNumbers, HashMap<Integer, Object>> MATCHNUMBERS = new HashMap<>();
        private List<Integer> FINAL_ARRAY = null;
        private List<Integer> EDGES = new ArrayList<Integer>();

        public Amount(Integer num) {
            this.NUM = num;
            this.MATCHNUMBERS.put(KeysMatchNumbers.NUMBER_SQUARE, new HashMap<>());
            this.MATCHNUMBERS.put(KeysMatchNumbers.NUMBER_SIZE, new HashMap<>());
            this.MATCHNUMBERS.put(KeysMatchNumbers.SIZE_NUMBER, new HashMap<>());
            this.MATCHNUMBERS.put(KeysMatchNumbers.IS_IN_AMOUNT, new HashMap<>());
            this.AMOUNT.put(KeysMatchNumbers.DIREITA, new HashMap<>());
            this.AMOUNT.put(KeysMatchNumbers.ESQUERDA, new HashMap<>());
        }

        public void populate(List<Integer> edges) {
            for (Integer c : edges) {
                Integer proxNum = this.findProxNum(c);
                if (proxNum != null) {
                    this.updateCollection(c, proxNum);
                }
            }
        }

        public void findSquares() {
            int count = 2;
            while (true) {
                int square = (int) Math.pow(count, 2);
                if (square > (this.NUM + (this.NUM + 1))) {
                    break;
                }
                this.SQUARES.add(square);
                count++;
            }
        }

        public Boolean isSquare(Integer num, Integer proxNum) {
            if (num > proxNum) {
                int number = num - proxNum;
                if (number <= this.NUM && number != proxNum && number > 0 && number <= this.NUM) {
                    return true;
                }
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        public void matchNumbers() {
            for (int c = this.NUM; c > 0; c--) {
                List<Integer> list = new ArrayList<>();
                for (Integer i : this.SQUARES) {
                    Boolean result = this.isSquare(i, c);
                    if (result) {
                        list.add(i - c);
                    }
                }
                if (!list.isEmpty()) {
                    this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).put(c, list);
                    int sizeList = list.size();
                    if (this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(sizeList) == null) {
                        this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).put(sizeList, new ArrayList<>());
                    }
                    if (this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(c) == null) {
                        this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).put(c, sizeList);
                    }
                    ((ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(sizeList)).add(c);
                }
                this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).put(c, false);
            }
        }

        @SuppressWarnings("unchecked")
        public void verifySizeNumber(Integer num) {
            int sizeList = (int) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(num);
            List<Integer> sizeListObj = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER)
                    .get(sizeList);
            sizeListObj.remove(num);
            if (sizeListObj.size() == 0) {
                this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).remove(sizeList);
            } else {
                this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).put(sizeList, sizeListObj);
            }
            this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).put(num, sizeList - 1);
        }

        @SuppressWarnings("unchecked")
        public void deleteNumberAssociations(Integer num, Integer proxNum) {
            verifySizeNumber(num);
            int sizeList = (int) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(num);
            if (sizeList != 0) {
                if (this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(sizeList) == null) {
                    this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).put(sizeList, new ArrayList<Integer>());
                }
                ((ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(sizeList)).add(num);
                ((ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(num)).remove(proxNum);
            } else {
                deleteNumber(num);
            }
        }

        public void deleteNumber(Integer num) {
            this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).remove(num);
            this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).remove(num);
            if (this.AMOUNT.get(KeysMatchNumbers.DIREITA).containsKey(num)
                    || this.AMOUNT.get(KeysMatchNumbers.ESQUERDA).containsKey(num)) {
                this.EDGES.add(num);
            }
        }

        @SuppressWarnings("unchecked")
        public void removeNumbers(Integer num, Integer proxNum) {
            List<Integer> list = Arrays.asList(num, proxNum);
            List<Boolean> listVerification = new ArrayList<>();
            for (Integer c : list) {
                List<Integer> direita = this.AMOUNT.get(KeysMatchNumbers.DIREITA).get(c);
                List<Integer> esquerda = this.AMOUNT.get(KeysMatchNumbers.ESQUERDA).get(c);
                Boolean isInAmount = (Boolean) this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).get(c);
                if (direita == null && esquerda == null && isInAmount) {
                    listVerification.add(true);
                } else {
                    listVerification.add(false);
                }
            }
            for (int h = 0; h < listVerification.size(); h++) {
                Integer selected = list.get(h);
                Boolean value = listVerification.get(h);
                if (value) {
                    List<Integer> squaresc = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE)
                            .get(selected);
                    if (squaresc != null) {
                        for (int i = 0; i < squaresc.size(); i++) {
                            int numberSquarec = squaresc.get(i);
                            List<Integer> squaresi = (ArrayList<Integer>) this.MATCHNUMBERS
                                    .get(KeysMatchNumbers.NUMBER_SQUARE).get(numberSquarec);
                            if (squaresi != null) {
                                if (squaresi.contains(selected)) {
                                    deleteNumberAssociations(numberSquarec, selected);
                                }
                            }
                        }
                    }
                    if (squaresc != null) {
                        verifySizeNumber(selected);
                        deleteNumber(selected);
                    }
                    if (Collections.frequency(listVerification, true) == 1) {
                        return;
                    }
                } else {
                    deleteNumberAssociations(selected, selected == num ? proxNum : num);
                }
            }
        }

        @SuppressWarnings("unchecked")
        public Integer findNextNum() {
            Set<Integer> keySet = this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).keySet();
            if (keySet.isEmpty()) {
                return null;
            }
            int minKey = Integer.MAX_VALUE;
            for (int key : keySet) {
                if (key < minKey) {
                    minKey = key;
                }
            }
            List<Integer> nums = (List<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(minKey);
            for (Integer num : nums) {
                if (!(boolean) this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).get(num)) {
                    return num;
                }
            }
            return nums.get(0);
        }

        @SuppressWarnings("unchecked")
        public Integer findProxNum(Integer num) {
            List<Integer> squareObj = (List<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(num);
            if (squareObj == null || squareObj.isEmpty()) {
                return null;
            }
            Integer minSize = Integer.MAX_VALUE;
            Integer proxNum = null;
            for (Integer candidate : squareObj) {
                Integer size = (Integer) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(candidate);
                if (size < minSize) {
                    minSize = size;
                    proxNum = candidate;
                }
            }
            return proxNum;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public void updateCollection(Integer num, Integer proxNum) {
            Integer senseProxNum = -1;
            Integer senseNum = -1;
            List<Integer> sequenceProxNum = new ArrayList<>();
            List<Integer> sequenceNum = new ArrayList<>();
            List<Integer> newSequence = new ArrayList<>();
            Boolean inFinalAmountNum = true;
            Boolean inFinalAmountProxNum = true;
            for (int i = 0; i < 2; i++) {
                Integer sense = -1;
                List<Integer> sequence = new ArrayList<>();
                Boolean inFinalAmout = true;
                Integer number = i == 0 ? num : proxNum;
                if (this.AMOUNT.get(KeysMatchNumbers.DIREITA).get(number) != null) {
                    sense = -1;
                    sequence = this.AMOUNT.get(KeysMatchNumbers.DIREITA).get(number);
                } else if (this.AMOUNT.get(KeysMatchNumbers.ESQUERDA).get(number) != null) {
                    sense = 0;
                    sequence = this.AMOUNT.get(KeysMatchNumbers.ESQUERDA).get(number);
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
                        this.AMOUNT.get(verificar ? KeysMatchNumbers.DIREITA : KeysMatchNumbers.ESQUERDA)
                                .remove(verificar
                                        ? i == 1 ? sequenceProxNum.get(sequenceProxNum.size() - 1)
                                                : sequenceNum.get(sequenceNum.size() - 1)
                                        : i == 1 ? sequenceProxNum.get(0) : sequenceNum.get(0));
                    }
                }
            }
            if (senseNum == senseProxNum) {
                Collections.reverse(sequenceProxNum);
            }
            Stream streamNewSequence = senseNum == -1 ? Stream.concat(sequenceNum.stream(), sequenceProxNum.stream())
                    : Stream.concat(sequenceProxNum.stream(), sequenceNum.stream());
            newSequence = (ArrayList<Integer>) streamNewSequence.collect(Collectors.toList());
            if (newSequence.size() == this.NUM) {
                this.FINAL_ARRAY = newSequence;
                return;
            }
            Integer newSequenceDireita = newSequence.get(newSequence.size() - 1);
            Integer newSequenceEsquerda = newSequence.get(0);
            this.AMOUNT.get(KeysMatchNumbers.DIREITA).put(newSequenceDireita, (ArrayList<Integer>) newSequence);
            this.AMOUNT.get(KeysMatchNumbers.ESQUERDA).put(newSequenceEsquerda, (ArrayList<Integer>) newSequence);
            this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).put(newSequenceDireita, true);
            this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).put(newSequenceEsquerda, true);
            this.removeNumbers(num, proxNum);
            List<Integer> squareObjDireita = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE)
                    .get(newSequenceDireita);
            List<Integer> squareObjEsquerda = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE)
                    .get(newSequenceEsquerda);
            squareObjDireita = squareObjDireita == null ? new ArrayList<>() : squareObjDireita;
            squareObjEsquerda = squareObjEsquerda == null ? new ArrayList<>() : squareObjEsquerda;
            if (squareObjDireita.contains(newSequenceEsquerda)) {
                deleteNumberAssociations(newSequenceDireita, newSequenceEsquerda);
            }
            if (squareObjEsquerda.contains(newSequenceDireita)) {
                deleteNumberAssociations(newSequenceEsquerda, newSequenceDireita);
            }
        }
    }
}