import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Amount  {

    private Integer NUM;
    private ArrayList<Integer> ARRAY = new ArrayList<>();
    private HashMap<Keys, HashMap<Integer, ArrayList<Integer>>> FINAL_AMOUNT = new HashMap<>();
    private ArrayList<Integer> SQUARES = new ArrayList<>();
    private HashMap<Keys, HashMap<Integer, Object>> MATCHNUMBERS = new HashMap<>();
    private ArrayList<Integer> DELETADOS = new ArrayList<>();

    enum Keys {
        NUMBER_SQUARE, NUMBER_SIZE, SIZE_NUMBER, DIREITA, ESQUERDA, DELETADOS
    }

    public Amount(Integer num) {
        this.NUM = num;
        this.MATCHNUMBERS.put(Keys.NUMBER_SQUARE, new HashMap<>());
        this.MATCHNUMBERS.put(Keys.NUMBER_SIZE, new HashMap<>());
        this.MATCHNUMBERS.put(Keys.SIZE_NUMBER, new HashMap<>());
        this.FINAL_AMOUNT.put(Keys.DIREITA, new HashMap<>());
        this.FINAL_AMOUNT.put(Keys.ESQUERDA, new HashMap<>());
    }

    public Amount(Integer num, HashMap<Keys, HashMap<Integer, Object>> matchNumbers, HashMap<Keys, HashMap<Integer, ArrayList<Integer>>> finalAmount) {
        this.NUM = num;
        for (HashMap.Entry<Keys, HashMap<Integer, Object>> entry : matchNumbers.entrySet()) { this.MATCHNUMBERS.put(entry.getKey(), copyMatchNumber(entry.getValue()));}
        for (HashMap.Entry<Keys, HashMap<Integer, ArrayList<Integer>>> entry : finalAmount.entrySet()) { this.FINAL_AMOUNT.put(entry.getKey(), copyFinalAmount(entry.getValue()));}
    }

    public HashMap<Integer, Object> copyMatchNumber(HashMap<Integer, Object> hash) {
        HashMap<Integer, Object> copy = new HashMap<>();
        for (HashMap.Entry<Integer, Object> entry : hash.entrySet()) {
            Integer tmpI = null;
            ArrayList<Integer> tmpA = null;
            try {
                tmpI = new Integer((Integer) entry.getValue());
            } catch (Exception e) {
                tmpA = new ArrayList<>((ArrayList) entry.getValue());
            }
            copy.put(entry.getKey(),  tmpI != null ? tmpI : tmpA);
        }
        return copy;
    }

    public HashMap<Integer, ArrayList<Integer>> copyFinalAmount(HashMap<Integer, ArrayList<Integer>> hash) {
        HashMap<Integer, ArrayList<Integer>> copy = new HashMap<>();
        for (HashMap.Entry<Integer, ArrayList<Integer>> entry : hash.entrySet()) { copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));}
        return copy;
    }

    public void generateArray() {
        for (int i = NUM; i > 0; i--) {
            this.ARRAY.add(i);
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

    public void matchNumbers() {
        for (Integer c : this.ARRAY) {
            ArrayList<Integer> list = new ArrayList<>();
            for (Integer i : this.SQUARES) {
                if (i > c) {
                    int number = i - c;
                    if (number <= this.NUM && number != c && this.ARRAY.contains(number)) {
                        list.add(number);
                    }
                }
            }
            if (!list.isEmpty()) {
                this.MATCHNUMBERS.get(Keys.NUMBER_SQUARE).put(c, list);
            }
        }
    }

    public void matchSizeNumbers() {
        for (Integer c : this.ARRAY) {
            ArrayList<Integer> cSizeList = (ArrayList<Integer>) this.MATCHNUMBERS.get(Keys.NUMBER_SQUARE).get(c);
            int sizeList = cSizeList.size();
            if (this.MATCHNUMBERS.get(Keys.SIZE_NUMBER).get(sizeList) == null) {
                this.MATCHNUMBERS.get(Keys.SIZE_NUMBER).put(sizeList, new ArrayList<>());
            }
            if (this.MATCHNUMBERS.get(Keys.NUMBER_SIZE).get(c) == null) {
                this.MATCHNUMBERS.get(Keys.NUMBER_SIZE).put(c, sizeList);
            }
            ArrayList<Integer> sizeListc = (ArrayList<Integer>) this.MATCHNUMBERS.get(Keys.SIZE_NUMBER).get(sizeList);
            sizeListc.add(c);
            this.MATCHNUMBERS.get(Keys.SIZE_NUMBER).put(sizeList, sizeListc);
        }
    }

    public void verifySizeNumber(Integer num) {
        int sizeList = (int) this.MATCHNUMBERS.get(Keys.NUMBER_SIZE).get(num);
        ArrayList<Integer> sizeListObj = (ArrayList<Integer>) this.MATCHNUMBERS.get(Keys.SIZE_NUMBER).get(sizeList);
        sizeListObj.remove(num);
        if (sizeListObj.size() == 0) {
            this.MATCHNUMBERS.get(Keys.SIZE_NUMBER).remove(sizeList);
        } else {
            this.MATCHNUMBERS.get(Keys.SIZE_NUMBER).put(sizeList, sizeListObj);
        }
        this.MATCHNUMBERS.get(Keys.NUMBER_SIZE).put(num, sizeList - 1);
    }

    public void deleteNumberAssociations(Integer num, Integer proxNum) {
        verifySizeNumber(num);
        int sizeList = (int) this.MATCHNUMBERS.get(Keys.NUMBER_SIZE).get(num);
        if (sizeList != 0) {
            if (this.MATCHNUMBERS.get(Keys.SIZE_NUMBER).get(sizeList) == null) {
                this.MATCHNUMBERS.get(Keys.SIZE_NUMBER).put(sizeList, new ArrayList<Integer>());
            }
            ArrayList<Integer> sizeObj = (ArrayList<Integer>) this.MATCHNUMBERS.get(Keys.SIZE_NUMBER).get(sizeList);
            sizeObj.add(num);
            this.MATCHNUMBERS.get(Keys.SIZE_NUMBER).put(sizeList, sizeObj);
            ArrayList<Integer> squareObj = (ArrayList<Integer>) this.MATCHNUMBERS.get(Keys.NUMBER_SQUARE).get(num);
            squareObj.remove(proxNum);
            this.MATCHNUMBERS.get(Keys.NUMBER_SQUARE).put(num, squareObj);
        } else {
            deleteNumber(num);
        }
    }

    public void deleteNumber(Integer num) {
        this.MATCHNUMBERS.get(Keys.NUMBER_SIZE).remove(num);
        this.MATCHNUMBERS.get(Keys.NUMBER_SQUARE).remove(num);
        DELETADOS.add(num);
    }

    public void removeNumbers(Integer num, Integer proxNum) {
        List<Integer> list = Arrays.asList(num, proxNum);
        ArrayList<Boolean> listVerification = new ArrayList<>();
        for (Integer c : list) {
            ArrayList<Integer> direita = this.FINAL_AMOUNT.get(Keys.DIREITA).get(c);
            ArrayList<Integer> esquerda = this.FINAL_AMOUNT.get(Keys.ESQUERDA).get(c);
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
                ArrayList<Integer> squaresc = (ArrayList<Integer>) this.MATCHNUMBERS.get(Keys.NUMBER_SQUARE).get(selected);
                if (squaresc != null) {
                    for (Integer i : squaresc) {
                        ArrayList<Integer> squaresi = (ArrayList<Integer>) this.MATCHNUMBERS.get(Keys.NUMBER_SQUARE).get(i);
                        if (squaresi != null) {
                            if (squaresi.contains(selected)) {
                                deleteNumberAssociations(i, selected);
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

    public ArrayList<Integer> returnResult(Integer num, ArrayList<Integer> proxNums) {
        for (Integer c : proxNums) {
            Amount tmpAmount = new Amount(this.NUM, this.MATCHNUMBERS, this.FINAL_AMOUNT);
            ArrayList<Integer> result = tmpAmount.updateCollection(num, c);
            if (result != null) {
                return result;
            }
        }
        this.updateCollection(num, proxNums.get(0));
        return null;
    }

    public List<Integer> organizeArray() {
        try {
            while (true) {
                List<Integer> keys = this.MATCHNUMBERS.get(Keys.SIZE_NUMBER).keySet().stream().collect(Collectors.toList());
                Integer num = ((ArrayList<Integer>) this.MATCHNUMBERS.get(Keys.SIZE_NUMBER).get(Collections.min(keys))).get(0);
                ArrayList<Integer> list = new ArrayList<>();
                ArrayList<Integer> squareObj = (ArrayList<Integer>) this.MATCHNUMBERS.get(Keys.NUMBER_SQUARE).get(num);
                for (Integer c : squareObj) {
                    Integer number = (Integer) this.MATCHNUMBERS.get(Keys.NUMBER_SIZE).get(c);
                    list.add(number);
                }
                Integer minSize = Collections.min(list);
                ArrayList<Integer> proxNumSelecteds = (ArrayList<Integer>) squareObj.stream().filter(f -> this.MATCHNUMBERS.get(Keys.NUMBER_SIZE).get(f) == minSize).collect(Collectors.toList());
                ArrayList<Integer> result = returnResult(num, proxNumSelecteds);
                if (result != null) {
                    return result;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<Integer> updateCollection(Integer num, Integer proxNum) {
        Integer senseProxNum = -1;
        Integer senseNum = -1;
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
            if (this.FINAL_AMOUNT.get(Keys.DIREITA).get(number) != null) {
                sense = -1;
                sequence = this.FINAL_AMOUNT.get(Keys.DIREITA).get(number);
            } else if (this.FINAL_AMOUNT.get(Keys.ESQUERDA).get(number) != null) {
                sense = 0;
                sequence = this.FINAL_AMOUNT.get(Keys.ESQUERDA).get(number);
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
                    this.FINAL_AMOUNT.get(verificar ? Keys.DIREITA : Keys.ESQUERDA).remove(verificar ? i == 1 ? sequenceProxNum.get(sequenceProxNum.size() - 1) : sequenceNum.get(sequenceNum.size() - 1) : i == 1 ? sequenceProxNum.get(0) : sequenceNum.get(0));
                }
            }
        }
        if (senseNum == senseProxNum) {
            Collections.reverse(sequenceProxNum);
        }
        Stream streamNewSequence = senseNum == -1 ? Stream.concat(sequenceNum.stream(), sequenceProxNum.stream()) : Stream.concat(sequenceProxNum.stream(), sequenceNum.stream());
        newSequence = (ArrayList<Integer>) streamNewSequence.collect(Collectors.toList());
        if (newSequence.size() == this.NUM) {
            return newSequence;
        }
        Integer newSequenceDireita = newSequence.get(newSequence.size() - 1);
        Integer newSequenceEsquerda = newSequence.get(0);
        this.FINAL_AMOUNT.get(Keys.DIREITA).put(newSequenceDireita, newSequence);
        this.FINAL_AMOUNT.get(Keys.ESQUERDA).put(newSequenceEsquerda, newSequence);
        removeNumbers(num, proxNum);
        ArrayList<Integer> squareObjDireita = (ArrayList<Integer>) this.MATCHNUMBERS.get(Keys.NUMBER_SQUARE).get(newSequenceDireita);
        ArrayList<Integer> squareObjEsquerda = (ArrayList<Integer>) this.MATCHNUMBERS.get(Keys.NUMBER_SQUARE).get(newSequenceEsquerda);
        squareObjDireita = squareObjDireita == null ? new ArrayList<>() : squareObjDireita;
        squareObjEsquerda = squareObjEsquerda == null ? new ArrayList<>() : squareObjEsquerda;
        if (squareObjDireita.contains(newSequenceEsquerda)) {
            deleteNumberAssociations(newSequenceDireita, newSequenceEsquerda);
        }
        if (squareObjEsquerda.contains(newSequenceDireita)) {
            deleteNumberAssociations(newSequenceEsquerda, newSequenceDireita);
        }
        return null;
    }
}



