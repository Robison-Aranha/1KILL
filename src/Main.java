import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    enum KeysError {
        REMOVE_NUMBERS_ERROR, EDGE_NUMBERS_ERROR
    }

    public static void main(String args[]) {
        long tempoInicial = System.currentTimeMillis();
        System.out.println(organizeArray(55));
        long tempoFinal = System.currentTimeMillis();
        System.out.println(tempoFinal - tempoInicial);
    }

    public static List<Integer> organizeArray(Integer number) {
        Amount amount = new Amount(number);
        amount.findSquares();
        amount.matchNumbers();
        while (true) {
            Integer num = amount.findNextNum();
            Integer proxNum = amount.findProxNum(num);
            if (num != null && proxNum != null) {
                if (!amount.verifyDeletedNumbers(num, proxNum)) {
                    amount.realocateNumber(num);
                } else if (!amount.verifyMatchnumbers(num, proxNum)) {
                    amount.removeNumbers(num, proxNum);
                } else {
                    amount.updateCollection(num, proxNum);
                }
            } else {
                return null;
            }
            if (amount.FINAL_ARRAY != null) {
                return amount.FINAL_ARRAY;
            }
        }
    }

    public static class Amount  {

        private Integer NUM;
        private HashMap<KeysMatchNumbers, HashMap<Integer, List<Integer>>> AMOUNT = new HashMap<>();
        private List<Integer> SQUARES = new ArrayList<>();
        private HashMap<KeysMatchNumbers, HashMap<Integer, Object>> MATCHNUMBERS = new HashMap<>();
        private List<Integer> FINAL_ARRAY = null;

        enum KeysMatchNumbers {
            NUMBER_SQUARE, NUMBER_SIZE, SIZE_NUMBER, DIREITA, ESQUERDA, IS_IN_AMOUNT, NEXT_NUMBER
        }

        enum TrackTypes {
            MULTIPLE_WAYS, NO_MORE_WAYS
        }

        public Amount(Integer num) {
            this.NUM = num;
            this.MATCHNUMBERS.put(KeysMatchNumbers.NUMBER_SQUARE, new HashMap<>());
            this.MATCHNUMBERS.put(KeysMatchNumbers.NUMBER_SIZE, new HashMap<>());
            this.MATCHNUMBERS.put(KeysMatchNumbers.SIZE_NUMBER, new HashMap<>());
            this.MATCHNUMBERS.put(KeysMatchNumbers.IS_IN_AMOUNT, new HashMap<>());
            this.AMOUNT.put(KeysMatchNumbers.DIREITA, new HashMap<>());
            this.AMOUNT.put(KeysMatchNumbers.ESQUERDA, new HashMap<>());
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
                    List<Integer> sizeListc = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(sizeList);
                    sizeListc.add(c);
                    this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).put(sizeList, sizeListc);
                }
                this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).put(c, false);
            }
        }

        public void verifySizeNumber(Integer num) {
            int sizeList = (int) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(num);
            List<Integer> sizeListObj = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(sizeList);
            sizeListObj.remove(num);
            if (sizeListObj.size() == 0) {
                this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).remove(sizeList);
            } else {
                this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).put(sizeList, sizeListObj);
            }
            this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).put(num, sizeList - 1);
        }

        public void deleteNumberAssociations(Integer num, Integer proxNum) {
            verifySizeNumber(num);
            int sizeList = (int) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(num);
            if (sizeList != 0) {
                if (this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(sizeList) == null) {
                    this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).put(sizeList, new ArrayList<Integer>());
                }
                List<Integer> sizeObj = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(sizeList);
                sizeObj.add(num);
                this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).put(sizeList, sizeObj);
                List<Integer> squareObj = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(num);
                squareObj.remove(proxNum);
                this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).put(num, squareObj);
            } else {
                deleteNumber(num);
            }
        }

        public void deleteNumber(Integer num) {
            this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).remove(num);
            this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).remove(num);
        }

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
                    List<Integer> squaresc = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(selected);
                    if (squaresc != null) {
                        for (Integer i : squaresc) {
                            List<Integer> squaresi = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(i);
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

        public Integer findNextNum() {
            List<Integer> keys = this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).keySet().stream().collect(Collectors.toList());
            if (keys.size() > 0) {
                Integer num = ((ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(Collections.min(keys))).get(0);
                return num;
            }
            return null;
        }

        public Integer findProxNum(Integer num) {
            Object obj = this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(num);
            if (obj != null) {
                List<Integer> squareObj = (ArrayList<Integer>) obj;
                List<Integer> listSizes = new ArrayList<Integer>();
                for (Integer c : squareObj) {
                    Integer size = (Integer) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(c);
                    listSizes.add(size);
                }
                Integer minSize = Collections.min(listSizes);
                List<Integer> proxNums = squareObj.stream().filter(s -> (int) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(s) == minSize).collect(Collectors.toList());
                return proxNums.get(0);
            }
            return null;
        }

        public void realocateNumber(Integer num) {
            Integer sizeNum = (Integer) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(num);
            List<Integer> sizeList = (List<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(sizeNum);
            sizeList.remove(num);
            sizeList.add(num);
            this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).put(sizeNum, sizeList);
        }

        public Boolean verifyMatchnumbers(Integer num, Integer proxNum) {
            HashMap<Integer, HashMap<Integer, List<Integer>>> generations = this.returnGenerations(num, proxNum);
            for (Integer number : generations.keySet()) {
                HashMap<Integer, List<Integer>> hash = (HashMap<Integer, List<Integer>>) generations.get(number);
                HashMap<Integer, List<TrackTypes>> tracks = new HashMap<>();
                for (Integer hashKey : hash.keySet()) {
                    if (hashKey == 9 || hash.get(hashKey).contains(9)) {
                        System.out.println(this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(9));
                        System.out.println("");
                    }
                    List<TrackTypes> arrayc = new ArrayList<>();
                    TrackTypes trackType = this.getTrack(hashKey, number);
                    arrayc.add(trackType);
                    tracks.put(hashKey, arrayc);
                    for (Integer c : hash.get(hashKey)) {
                        List<TrackTypes> arrayh = new ArrayList<>();
                        TrackTypes trackTypeDerivateds = this.getTrack(c, hashKey);
                        arrayh.add(trackTypeDerivateds);
                        tracks.put(c, arrayh);
                    }
                }
                Boolean verift = false;
                for (Integer trackKey : tracks.keySet()) {
                    if (tracks.get(trackKey).contains(TrackTypes.NO_MORE_WAYS)) {
                        verift = true;
                    }
                }
                if (verift) {
                    num = num;
                }
                System.out.println("num: " + num);
                System.out.println("proxNum: " + proxNum);
                System.out.println("track: " + tracks);
                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            }
            return true;
        }

        public TrackTypes getTrack(Integer num, Integer anteriorNumber) {
            Integer currentNumber = num;
            List<Integer> numbersToAvoid = new ArrayList<>();
            numbersToAvoid.add(currentNumber);
            numbersToAvoid.add(anteriorNumber);
            TrackTypes trackType = null;
            while (true) {
                List<Integer> square = (List<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(currentNumber);
                if (square != null) {
                    square = square.stream().filter(n -> !numbersToAvoid.contains(n)).collect(Collectors.toList());
                    if (square.size() == 1) {
                        Integer number = square.get(0);
                        Integer amountNumberRefered = this.getContrariorNumberInAmount(number);
                        if (amountNumberRefered != null) {
                            numbersToAvoid.add(number);
                            currentNumber = amountNumberRefered;
                        } else {
                            currentNumber = number;
                        }
                        numbersToAvoid.add(currentNumber);
                        continue;
                    } else if (square.size() == 0) {
                        trackType = TrackTypes.NO_MORE_WAYS;
                    } else {
                        trackType = TrackTypes.MULTIPLE_WAYS;
                    }
                } else {
                    trackType = TrackTypes.NO_MORE_WAYS;
                }
                break;
            }
            return trackType;
        }

        public Integer getContrariorNumberInAmount(Integer num) {
            for (List<Integer> amount : this.getAmounts()) {
                Integer index = amount.indexOf(num);
                if (index == 0 || index == amount.size() - 1) {
                    index = index == 0 ? amount.size() - 1 : 0;
                    return amount.get(index);
                }
            }
            return null;
        }

        public Boolean verifyDeletedNumbers(Integer num, Integer proxNum) {
            HashMap<Integer, HashMap<Integer, List<Integer>>> generations = this.returnGenerations(num, proxNum);
            for (Integer number : generations.keySet()) {
                HashMap<Integer, List<Integer>> hash = ( HashMap<Integer, List<Integer>>) generations.get(number);
                for (Integer key : hash.keySet()) {
                    if (hash.get(key).size() == 0) {
                        if ((Boolean) this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).get(key) == false) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        public HashMap<Integer, HashMap<Integer, List<Integer>>> returnGenerations(Integer num, Integer proxNum) {
            List<Integer> numSquare = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(num);
            numSquare = numSquare.stream().filter(n -> n != proxNum).collect(Collectors.toList());
            List<Integer> proxNumSquare = (ArrayList<Integer>)this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(proxNum);
            proxNumSquare = proxNumSquare.stream().filter(n -> n != num).collect(Collectors.toList());
            List<List<Integer>> firstGeneratioNumbers = Arrays.asList(numSquare, proxNumSquare);
            HashMap<Integer, HashMap<Integer, List<Integer>>> secondGenerationNumbers = new HashMap<>();
            for (int c = 0; c < 2 ; c++) {
                Integer number = c == 0 ? num : proxNum;
                List<Integer> squares = firstGeneratioNumbers.get(c);
                HashMap<Integer, List<Integer>> hash = new HashMap<>();
                for (Integer i : squares) {
                    List<Integer> squarei = (List<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(i);
                    squarei = squarei.stream().filter(n -> {
                        if (n == i || n == num || n == proxNum) {
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    hash.put(i, squarei);
                }
                secondGenerationNumbers.put(number, hash);
            }
            return secondGenerationNumbers;
        }

        public List<List<Integer>> getAmounts() {
            return (List<List<Integer>>) this.AMOUNT.get(KeysMatchNumbers.DIREITA).values().stream().collect(Collectors.toList());
        }

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
                        this.AMOUNT.get(verificar ? KeysMatchNumbers.DIREITA : KeysMatchNumbers.ESQUERDA).remove(verificar ? i == 1 ? sequenceProxNum.get(sequenceProxNum.size() - 1) : sequenceNum.get(sequenceNum.size() - 1) : i == 1 ? sequenceProxNum.get(0) : sequenceNum.get(0));
                    }
                }
            }
            if (senseNum == senseProxNum) {
                Collections.reverse(sequenceProxNum);
            }
            Stream streamNewSequence = senseNum == -1 ? Stream.concat(sequenceNum.stream(), sequenceProxNum.stream()) : Stream.concat(sequenceProxNum.stream(), sequenceNum.stream());
            newSequence = (ArrayList<Integer>) streamNewSequence.collect(Collectors.toList());
            if (newSequence.size() == this.NUM) {
                this.FINAL_ARRAY = newSequence;
            }
            Integer newSequenceDireita = newSequence.get(newSequence.size() - 1);
            Integer newSequenceEsquerda = newSequence.get(0);
            this.AMOUNT.get(KeysMatchNumbers.DIREITA).put(newSequenceDireita, (ArrayList<Integer>) newSequence);
            this.AMOUNT.get(KeysMatchNumbers.ESQUERDA).put(newSequenceEsquerda, (ArrayList<Integer>) newSequence);
            this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).put(newSequenceDireita, true);
            this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).put(newSequenceEsquerda, true);
            this.removeNumbers(num, proxNum);
            List<Integer> squareObjDireita = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(newSequenceDireita);
            List<Integer> squareObjEsquerda = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(newSequenceEsquerda);
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