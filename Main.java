import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    enum KeysError {
        REMOVE_NUMBERS_ERROR, EDGE_NUMBERS_ERROR
    }
    
    public static void main(String args[]) {
        System.out.println(organizeArray(48));
    }
    
    public static List<Integer> organizeArray(Integer number) {
        Amount amount = new Amount(number);
        amount.findSquares();
        amount.matchNumbers();
        while (true) {
            Integer num = amount.findNextNum();
            Integer proxNum = amount.findProxNum(num);
            if (num != null && proxNum != null) {
            	Amount tmp = new Amount(amount);
            	tmp.updateCollection(num, proxNum);
	            if (!tmp.verifyDeletedNumbers()) { 
	                amount.realocateNumbers(num);
	            } else if (!tmp.verifyEdgeNumbers()) {
	               num = num;
	            } else {
	            	amount = new Amount(tmp);
	            }
	            if (amount.FINAL_ARRAY != null) {
	                return amount.FINAL_ARRAY;
	            }
            } else {
            	return null;
            }
        }
    }
    
    public static class Amount  {

        private Integer NUM;
        private HashMap<KeysMatchNumbers, HashMap<Integer, List<Integer>>> AMOUNT = new HashMap<>();
        private List<Integer> SQUARES = new ArrayList<>();
        private HashMap<KeysMatchNumbers, HashMap<Integer, Object>> MATCHNUMBERS = new HashMap<>();
        private List<Integer> FINAL_ARRAY = null;
        private List<Integer> DELETED = new ArrayList<>();
        private HashMap<Integer, Amount> QT_EDGE_NUMBERS = new HashMap<>();

        enum KeysMatchNumbers {
            NUMBER_SQUARE, NUMBER_SIZE, SIZE_NUMBER, DIREITA, ESQUERDA, IS_IN_AMOUNT
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

        public Amount(Amount amount) {
            this.NUM = amount.NUM;
            this.SQUARES = amount.SQUARES;
            for (HashMap.Entry<KeysMatchNumbers, HashMap<Integer, Object>> entry : amount.MATCHNUMBERS.entrySet()) { this.MATCHNUMBERS.put(entry.getKey(), copyMatchNumber(entry.getValue()));}
            for (Map.Entry<KeysMatchNumbers, HashMap<Integer, List<Integer>>> entry : amount.AMOUNT.entrySet()) { this.AMOUNT.put(entry.getKey(), copyFinalAmount(entry.getValue()));}
            this.FINAL_ARRAY = amount.FINAL_ARRAY;
        }

		@SuppressWarnings({ "removal", "unchecked" })
		public HashMap<Integer, Object> copyMatchNumber(HashMap<Integer, Object> hash) {
            HashMap<Integer, Object> copy = new HashMap<>();
            for (HashMap.Entry<Integer, Object> entry : hash.entrySet()) {
                Object tmp = entry.getValue();
                try {
                    tmp = new Integer((Integer) tmp);
                } catch (Exception e1) {
                	try {
                		tmp = new ArrayList<Integer>((ArrayList<Integer>) tmp);
                	}
                	catch (Exception e2) {
                		tmp = new Boolean((Boolean) tmp);
                	}
                }
                copy.put(entry.getKey(),  tmp);
            }
            return copy;
        }

        public HashMap<Integer, List<Integer>> copyFinalAmount(HashMap<Integer, List<Integer>> hash) {
            HashMap<Integer, List<Integer>> copy = new HashMap<>();
            for (HashMap.Entry<Integer, List<Integer>> entry : hash.entrySet()) { copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));}
            return copy;
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
                    List<Integer> sizeListc = (ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(sizeList);
                    sizeListc.add(c);
                    this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).put(sizeList, sizeListc);
                }
                this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).put(c, false);
            }
        }

        @SuppressWarnings("unchecked")
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

        @SuppressWarnings("unchecked")
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
            this.DELETED.add(num);
        }

        @SuppressWarnings("unchecked")
		public void removeNumbers(Integer num, Integer proxNum) {
            List<Integer> list = Arrays.asList(num, proxNum);
            List<Boolean> listVerification = new ArrayList<>();
            for (Integer c : list) {
                List<Integer> direita = this.AMOUNT.get(KeysMatchNumbers.DIREITA).get(c);
                List<Integer> esquerda = this.AMOUNT.get(KeysMatchNumbers.ESQUERDA).get(c);
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
        
        @SuppressWarnings("unchecked")
        public Integer findNextNum() {
            List<Integer> keys = this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).keySet().stream().collect(Collectors.toList());
            if (keys.size() > 0) {
				Integer num = ((ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(Collections.min(keys))).get(0);
                return num;
            }
            return null;
        }
        
        @SuppressWarnings("unchecked")
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
 				Integer proxNum = squareObj.stream().filter(s -> ((Integer) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(s)) == minSize).collect(Collectors.toList()).get(0);
 				return proxNum;
             }
             return null;
        }
        
        @SuppressWarnings("unchecked")
		public void realocateNumbers(Integer num) {
        	Integer numberSize = (Integer) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(num);
        	List<Integer> listNumberSize = (List<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(numberSize);
        	listNumberSize.remove(num);
        	listNumberSize.add(num);
        	this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).put(numberSize, listNumberSize);
        }
        
        @SuppressWarnings("unchecked")
		public Boolean verifyEdgeNumbers() {
        	List<Integer> sizeList = (List<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.SIZE_NUMBER).get(1);
        	if (sizeList != null) {
	        	for (Integer num : sizeList) {
	        		Integer number = num;
	        		Integer anteriorNumber = 0;
	        		while (true) {
		        		Boolean isInAmount = (Boolean) this.MATCHNUMBERS.get(KeysMatchNumbers.IS_IN_AMOUNT).get(number);
		        		List<Integer> track = new ArrayList<Integer>();
		        		if (!isInAmount) {
			        		List<Integer> square = (List<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(number);
			        		square = (List<Integer>) square.stream().filter(s -> s != anteriorNumber);
			        		if (square.size() == 1) {
			        			Integer nextNumber = square.get(0);
			        			List<Object> list = this.getAmountRefered(nextNumber);
			        			anteriorNumber = number;
			        			if (list != null) {
				        			Integer side = (Integer) list.get(0);
				        			List<Integer> amountNextNumber = (List<Integer>) list.get(1);
				        			if (side == -1) {
				        				Collections.reverse(amountNextNumber);
				        			}
				        			track.addAll(amountNextNumber);
				        			number = amountNextNumber.get(amountNextNumber.size() - 1);
			        			} else {
			        				track.add(nextNumber);
			        				number = nextNumber;
			        			}
			        		}
		        		}
	        		}
	        	}
        	}
        	return true;
        }
        
        public List<Object> getAmountRefered(Integer num) {
        	for (List<Integer> amount : this.getAmounts()) {
        		if (amount.indexOf(num) != -1) {
        			List<Object> list = new ArrayList<>();
        			list.add(amount.indexOf(num) == 0 ? 0 : -1);
        			list.add(amount);
        			return list;
        		}
        	}
        	return null;
        }
        
        public Boolean verifyDeletedNumbers() {
        	for (Integer c : this.DELETED) {
        		Boolean verificator = false;
        		for (List<Integer> amount : this.getAmounts()) {
        			if (amount.indexOf(c) != -1) {
        				verificator = true;
        				break;
        			}
        		}
        		if (!verificator) {
        			return false;
        		}
        	}
        	return true;
        }
        
        public List<List<Integer>> getAmounts() {
        	return (List<List<Integer>>) this.AMOUNT.get(KeysMatchNumbers.DIREITA).values().stream().collect(Collectors.toList());
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