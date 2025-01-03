import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    enum KeysError {
        REMOVE_NUMBERS_ERROR, EDGE_NUMBERS_ERROR
    }

    public static void main(String args[]) {
        long tempoInicial = System.currentTimeMillis();
        System.out.println(organizeArray(23));
        long tempoFinal = System.currentTimeMillis();
        System.out.println(tempoFinal - tempoInicial);
    }

    public static List<Integer> organizeArray(Integer number) {
        Amount amount = new Amount(number);
        amount.findSquares();
        amount.matchNumbers();
        while (true) {
            Integer num = amount.findNextNum();
            Integer proxNum = amount.findProxNum(num, null);
            if (num != null && proxNum != null) {
                if (!amount.verifyMatchnumbers(num, proxNum)) {
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

    public static class Amount {

        private Integer NUM;
        private HashMap<KeysMatchNumbers, HashMap<Integer, List<Integer>>> AMOUNT = new HashMap<>();
        private List<Integer> SQUARES = new ArrayList<>();
        private HashMap<KeysMatchNumbers, HashMap<Integer, Object>> MATCHNUMBERS = new HashMap<>();
        private List<Integer> FINAL_ARRAY = null;
        private List<Integer> EDGE_NUMBERS = new ArrayList<>();

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
                this.EDGE_NUMBERS.add(num);
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
                        for (Integer i : squaresc) {
                            List<Integer> squaresi = (ArrayList<Integer>) this.MATCHNUMBERS
                                    .get(KeysMatchNumbers.NUMBER_SQUARE).get(i);
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

        @SuppressWarnings("unchecked")
        public Integer findNextNum() {
            List<Integer> keys = this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).keySet().stream()
                    .collect(Collectors.toList());
            if (keys.size() > 0) {
                Integer num = ((ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER)
                        .get(Collections.min(keys))).get(0);
                return num;
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        public Integer findProxNum(Integer num, List<Integer> numbersToAvoid) {
            Object obj = this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(num);
            if (obj != null) {
                List<Integer> squareObj = (ArrayList<Integer>) obj;
                List<Integer> listSizes = new ArrayList<Integer>();
                for (Integer c : squareObj) {
                    Integer size;
                    if (numbersToAvoid == null) {
                        size = (Integer) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(c);
                    } else {
                        size = ((ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(c))
                                .stream().filter(n -> !numbersToAvoid.contains(n)).collect(Collectors.toList()).size();
                    }
                    listSizes.add(size);
                }
                Integer minSize = Collections.min(listSizes);
                List<Integer> proxNums = squareObj.stream()
                        .filter(s -> (int) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(s) == minSize)
                        .collect(Collectors.toList());

                return proxNums.get(0);
            }
            return null;
        }

        @SuppressWarnings({ "unchecked", "unused" })
        public Boolean verifyMatchnumbers(Integer num, Integer proxNum) {
            List<Integer> numbersToAvoid = new ArrayList<>();
            Boolean isNumInAmount = (Boolean) this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).get(num);
            Boolean isProxNumInAmount = (Boolean) this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).get(proxNum);
            if (isNumInAmount) {
                numbersToAvoid.add(num);
            }
            if (isProxNumInAmount) {
                numbersToAvoid.add(proxNum);
            }
            List<Integer> squareNum = new ArrayList<>(
                    (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(num));
            List<Integer> squareProxNum = new ArrayList<>(
                    (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(proxNum));
            Set<Integer> numbersToVerify = Stream.concat(squareNum.stream(), squareProxNum.stream())
                    .collect(Collectors.toSet());
            numbersToVerify.remove(proxNum);
            numbersToVerify.remove(num);
            numbersToVerify = numbersToVerify.stream()
                    .filter(n -> (boolean) this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).get(n) == false &&
                            ((ArrayList<Integer>) this.MATCHNUMBERS
                                    .get(KeysMatchNumbers.NUMBER_SQUARE).get(n)).stream()
                                    .filter(c -> !numbersToAvoid.contains(c)).collect(Collectors.toList()).size() == 1)
                    .collect(Collectors.toSet());
            List<Integer> numberSize1 = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(1);
            if (numberSize1 != null) {
                numberSize1 = numberSize1.stream()
                        .filter(n -> (boolean) this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).get(n) == false)
                        .collect(Collectors.toList());
                numbersToVerify.addAll(numberSize1);
            }
            if (this.EDGE_NUMBERS.size() > 0) {
                numbersToVerify.addAll(this.EDGE_NUMBERS);
            }
            if (numbersToVerify.size() > 0) {
                for (Integer c : numbersToVerify) {
                    Boolean principalNumbersHasPassed = false;
                    Integer number = c;
                    if ((boolean) this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).get(c) == true) {
                        number = this.getContrariorNumberInAmount(number);
                    }
                    List<Object> returnObj = this.getTrack(number, numbersToAvoid, num, proxNum,
                            principalNumbersHasPassed);
                    TrackTypes trackType = (TrackTypes) returnObj.get(0);
                    List<Integer> track = (ArrayList<Integer>) returnObj.get(1);
                    Set<Integer> finalTrack = new HashSet<>();
                    if (trackType.equals(TrackTypes.NO_MORE_WAYS)) {
                        for (Integer h : track) {
                            if ((boolean) this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).get(h) == true) {
                                List<Integer> list = this.getAmountRefered(h);
                                finalTrack.addAll(list);
                            } else {
                                finalTrack.add(h);
                            }
                        }
                        if (finalTrack.size() != this.NUM) {
                            return false;
                        }
                    }
                }
            }
            if (num == 36 && proxNum == 45) {
                return false;
            }
            return true;
        }

        @SuppressWarnings("unchecked")
        public List<Object> getTrack(Integer number, List<Integer> numbersToAvoid,
                Integer num, Integer proxNum,
                Boolean principalNumbersHasPassed) {
            Integer currentNumber = number;
            List<Integer> track;
            if (numbersToAvoid != null) {
                track = new ArrayList<>(numbersToAvoid);
            } else {
                track = new ArrayList<>();
            }
            track.add(currentNumber);
            while (true) {
                List<Integer> square = (List<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE)
                        .get(currentNumber);
                if (square != null) {
                    square = square.stream().filter(n -> !track.contains(n)).collect(Collectors.toList());
                    if (currentNumber == num || currentNumber == proxNum) {
                        Integer nextNumber = currentNumber == num ? proxNum : num;
                        if (!principalNumbersHasPassed) {
                            principalNumbersHasPassed = true;
                            square = Arrays.asList(nextNumber);
                        }
                    }
                    Boolean verifyer = false;
                    while (true) {
                        if (square.size() == 1) {
                            Integer numberSquare = square.get(0);
                            Integer amountNumberRefered = this.getContrariorNumberInAmount(numberSquare);
                            if (amountNumberRefered != null) {
                                track.add(amountNumberRefered);
                                if (num != null && proxNum != null && amountNumberRefered == num
                                        || amountNumberRefered == proxNum && !principalNumbersHasPassed) {
                                    principalNumbersHasPassed = true;
                                    Integer nextNumber = amountNumberRefered == num ? proxNum : num;
                                    Integer nextAmountNumber = this.getContrariorNumberInAmount(nextNumber);
                                    if (track.contains(nextAmountNumber)) {
                                        return Arrays.asList(TrackTypes.NO_MORE_WAYS, track);
                                    }
                                    if (nextAmountNumber != null) {
                                        currentNumber = nextAmountNumber;
                                    } else {
                                        currentNumber = nextNumber;
                                    }
                                } else {
                                    currentNumber = amountNumberRefered;
                                }
                            } else {
                                currentNumber = numberSquare;
                            }
                            track.add(currentNumber);
                            verifyer = true;
                            break;
                        } else if (square.size() == 0) {
                            return Arrays.asList(TrackTypes.NO_MORE_WAYS, track);
                        } else {
                            square = square.stream().filter(c -> {
                                thi
                                squares = squares.stream().filter(n -> !track.contains(n))
                                        .collect(Collectors.toList());
                                if (squares.size() <= 1) {
                                    return true;
                                }
                                return false;
                            }).collect(Collectors.toList());
                            if (square.size() == 1) {
                                Integer nextNum = square.get(0);
                                track.add(nextNum);
                                if (nextNum == num || nextNum == proxNum && !principalNumbersHasPassed) {
                                    principalNumbersHasPassed = true;
                                    square = Arrays.asList(nextNum == num ? proxNum : num);
                                }
                                continue;
                            }
                            return Arrays.asList(TrackTypes.MULTIPLE_WAYS, track);
                        }
                    }
                    if (verifyer) {
                        continue;
                    }
                } else {
                    return Arrays.asList(TrackTypes.NO_MORE_WAYS, track);
                }
                break;
            }
            return null;
        }

        public Integer getContrariorNumberInAmount(Integer num) {
            List<Integer> amount = this.getAmountRefered(num);
            if (amount != null) {
                Integer index = amount.indexOf(num);
                if (index == 0 || index == amount.size() - 1) {
                    index = amount.indexOf(num);
                    return amount.get(index == 0 ? amount.size() - 1 : 0);
                }
            }
            return null;
        }

        public List<Integer> getAmountRefered(Integer num) {
            for (List<Integer> amount : this.getAmounts()) {
                Integer index = amount.indexOf(num);
                if (index == 0 || index == amount.size() - 1) {
                    return new ArrayList<>(amount);
                }
            }
            return null;
        }

        public List<List<Integer>> getAmounts() {
            return (List<List<Integer>>) this.AMOUNT.get(KeysMatchNumbers.DIREITA).values().stream()
                    .collect(Collectors.toList());
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

    // @SuppressWarnings("unchecked")
    // public List<Object> getTrack(Integer number, List<Integer> numbersToAvoid,
    // Integer num, Integer proxNum,
    // Boolean principalNumbersHasPassed) {
    // Integer currentNumber = number;
    // List<Integer> track;
    // if (numbersToAvoid != null) {
    // track = new ArrayList<>(numbersToAvoid);
    // } else {
    // track = new ArrayList<>();
    // }
    // track.add(currentNumber);
    // while (true) {
    // List<Integer> square = (List<Integer>)
    // this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE)
    // .get(currentNumber);
    // if (square != null) {
    // square = square.stream().filter(n ->
    // !track.contains(n)).collect(Collectors.toList());
    // if (currentNumber == num || currentNumber == proxNum) {
    // Integer nextNumber = currentNumber == num ? proxNum : num;
    // if (!principalNumbersHasPassed) {
    // principalNumbersHasPassed = true;
    // square = Arrays.asList(nextNumber);
    // }
    // }
    // Boolean verifyer = false;
    // while (true) {
    // if (square.size() == 1) {
    // Integer numberSquare = square.get(0);
    // Integer amountNumberRefered = this.getContrariorNumberInAmount(numberSquare);
    // if (amountNumberRefered != null) {
    // track.add(amountNumberRefered);
    // if (num != null && proxNum != null && amountNumberRefered == num
    // || amountNumberRefered == proxNum && !principalNumbersHasPassed) {
    // principalNumbersHasPassed = true;
    // Integer nextNumber = amountNumberRefered == num ? proxNum : num;
    // Integer nextAmountNumber = this.getContrariorNumberInAmount(nextNumber);
    // if (track.contains(nextAmountNumber)) {
    // return Arrays.asList(TrackTypes.NO_MORE_WAYS, track);
    // }
    // if (nextAmountNumber != null) {
    // currentNumber = nextAmountNumber;
    // } else {
    // currentNumber = nextNumber;
    // track.add(currentNumber);
    // }
    // } else {
    // currentNumber = amountNumberRefered;
    // }
    // } else {
    // currentNumber = numberSquare;
    // track.add(currentNumber);
    // }
    // verifyer = true;
    // break;
    // } else if (square.size() == 0) {
    // return Arrays.asList(TrackTypes.NO_MORE_WAYS, track);
    // } else {
    // square = square.stream().filter(c -> {
    // List<Integer> squares = (List<Integer>) this.MATCHNUMBERS
    // .get(KeysMatchNumbers.NUMBER_SQUARE).get(c);
    // squares = squares.stream().filter(n -> !track.contains(n))
    // .collect(Collectors.toList());
    // if (squares.size() == 0) {
    // return true;
    // } else if (squares.size() == 1 && (Boolean) this.MATCHNUMBERS
    // .get(KeysMatchNumbers.IS_IN_AMOUNT).get(c) == false) {
    // return true;
    // }
    // return false;
    // }).collect(Collectors.toList());
    // if (square.size() == 1) {
    // Integer nextNum = square.get(0);
    // track.add(nextNum);
    // if (nextNum == num || nextNum == proxNum && !principalNumbersHasPassed) {
    // principalNumbersHasPassed = true;
    // square = Arrays.asList(nextNum == num ? proxNum : num);
    // }
    // continue;
    // }
    // return Arrays.asList(TrackTypes.MULTIPLE_WAYS, track);
    // }
    // }
    // if (verifyer) {
    // continue;
    // }
    // } else {
    // return Arrays.asList(TrackTypes.NO_MORE_WAYS, track);
    // }
    // break;
    // }
    // return null;
    // }

    // public Integer getContrariorNumberInAmount(Integer num) {
    // for (List<Integer> amount : this.getAmounts()) {
    // Integer index = amount.indexOf(num);
    // if (index == 0 || index == amount.size() - 1) {
    // index = amount.indexOf(num);
    // return amount.get(index == 0 ? amount.size() - 1 : 0);
    // }
    // }
    // return null;
    // }

    // public List<List<Integer>> getAmounts() {
    // return (List<List<Integer>>)
    // this.AMOUNT.get(KeysMatchNumbers.DIREITA).values().stream()
    // .collect(Collectors.toList());
    // }

    // public List<Integer> getAmountRefered(Integer num) {
    // for (List<Integer> amount : this.getAmounts()) {
    // Integer index = amount.indexOf(num);
    // if (index == 0 || index == amount.size() - 1) {
    // return new ArrayList<>(amount);
    // }
    // }
    // return null;
    // }

    // public List<List<Integer>> getAmounts() {
    // return (List<List<Integer>>)
    // this.AMOUNT.get(KeysMatchNumbers.DIREITA).values().stream()
    // .collect(Collectors.toList());
    // }

    // @SuppressWarnings("unchecked")
    // public Boolean realocateNumber(Integer num) {
    // Integer sizeNum = (Integer)
    // this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(num);
    // if (sizeNum != null) {
    // List<Integer> sizeList = (List<Integer>)
    // this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(sizeNum);
    // sizeList.remove(num);
    // sizeList.add(num);
    // this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).put(sizeNum, sizeList);
    // return true;
    // }
    // return false;
    // }

    // @SuppressWarnings({ "unchecked", "rawtypes" })
    // public void getTrackNumber(Integer number, List<Integer>
    // currentNumbersToAvoid,
    // HashMap<Integer, HashMap<TrackTypes, List<List<Integer>>>> tracks,
    // Integer num,
    // Integer proxNum) {
    // List<Integer> numbersToAvoid = new ArrayList<>(currentNumbersToAvoid);
    // Integer contrariorNumber = this.getContrariorNumberInAmount(number);
    // if (contrariorNumber != null) {
    // numbersToAvoid.add(contrariorNumber);
    // }
    // List<Integer> numbersToAvoidLater = this.updateTracks(number, number, tracks,
    // numbersToAvoid, num, proxNum);
    // numbersToAvoid = new ArrayList(Stream.concat(currentNumbersToAvoid.stream(),
    // numbersToAvoidLater.stream())
    // .collect(Collectors.toSet()));
    // if (contrariorNumber != null) {
    // numbersToAvoid.add(number);
    // }
    // this.updateTracks(contrariorNumber != null ? contrariorNumber : number,
    // number, tracks,
    // numbersToAvoid, num, proxNum);
    // }

    // @SuppressWarnings("unchecked")
    // public List<Integer> updateTracks(Integer number, Integer key,
    // HashMap<Integer, HashMap<TrackTypes, List<List<Integer>>>> tracks,
    // List<Integer> currentNumbersToAvoid, Integer num, Integer proxNum) {
    // if (tracks.get(key) == null) {
    // tracks.put(key, new HashMap<>());
    // }
    // List<Integer> ignore = new ArrayList<>(currentNumbersToAvoid);
    // Boolean principalNumbersHasPassed = false;
    // for (TrackTypes c : tracks.get(key).keySet()) {
    // if (tracks.get(key).get(c).get(0).contains(num)) {
    // principalNumbersHasPassed = true;
    // }
    // }
    // List<Object> returnObj = this.getTrack(number, ignore, num, proxNum,
    // principalNumbersHasPassed);
    // TrackTypes trackType = (TrackTypes) returnObj.get(0);
    // List<Integer> track = (ArrayList<Integer>) returnObj.get(1);
    // if (tracks.get(key).get(trackType) == null) {
    // tracks.get(key).put(trackType, new ArrayList<>());
    // }
    // tracks.get(key).get(trackType).add(track);
    // return (ArrayList<Integer>) returnObj.get(2);
    // }

    // @SuppressWarnings("unchecked")
    // public List<Integer> verifyNexyNumbers(List<Integer> square, List<Integer>
    // numbersToAvoid) {
    // List<Integer> finalArray = new ArrayList<>();
    // for (Integer h : square) {
    // Boolean isHInAmount = (Boolean)
    // this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).get(h);
    // List<Integer> squares = ((ArrayList<Integer>)
    // this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE)
    // .get(h)).stream().filter(c ->
    // !numbersToAvoid.contains(c)).collect(Collectors.toList());
    // List<Integer> numbersSize1H = squares.stream()
    // .filter(n -> ((ArrayList<Integer>)
    // this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(n))
    // .stream().filter(c ->
    // !numbersToAvoid.contains(c)).collect(Collectors.toList())
    // .size() == 1).collect(Collectors.toList());
    // List<Integer> numbersSize1HNotInAmount = numbersSize1H.stream()
    // .filter(n -> (Boolean)
    // this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).get(n) ==
    // false).collect(Collectors.toList());
    // List<Integer> numbersSize1HInAmount = numbersSize1H.stream()
    // .filter(n -> (Boolean)
    // this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).get(n) ==
    // true).collect(Collectors.toList());
    // if (isHInAmount && numbersSize1HNotInAmount.size() == 0 ) {
    // finalArray.add(h);
    // }
    // }
    // return finalArray;
    // }
}