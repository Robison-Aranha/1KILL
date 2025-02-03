import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String args[]) {
        long tempoInicial = System.currentTimeMillis();
        System.out.println(organizeArray(1394));
        long tempoFinal = System.currentTimeMillis();
        System.out.println(tempoFinal - tempoInicial);
    }

    public static List<Integer> organizeArray(Integer number) {
        Amount amount = new Amount(number);
        amount.findSquares();
        amount.matchNumbers();
        amount.verifyMatchnumbers(number);
        if (amount.FINAL_ARRAY != null) {
            return amount.FINAL_ARRAY;
        }
        return null;
    }

    public static class Amount {

        private Integer NUM;
        private List<Integer> SQUARES = new ArrayList<>();
        private HashMap<KeysMatchNumbers, HashMap<Integer, Object>> MATCHNUMBERS = new HashMap<>();
        private List<Integer> FINAL_ARRAY = null;

        enum KeysMatchNumbers {
            NUMBER_SQUARE, NUMBER_SIZE
        }

        enum MapTrackKeys {
            TRACKED_VALUES, TRACK_ARRAY, NUMBER
        }

        public Amount(Integer num) {
            this.NUM = num;
            this.MATCHNUMBERS.put(KeysMatchNumbers.NUMBER_SQUARE, new HashMap<>());
            this.MATCHNUMBERS.put(KeysMatchNumbers.NUMBER_SIZE, new HashMap<>());
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
                    if (this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(c) == null) {
                        this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).put(c, sizeList);
                    }
                }
            }
        }

        @SuppressWarnings("unchecked")
        public List<Integer> findProxNum(Integer num, Set<Integer> numbersToAvoid) {
            Object obj = this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(num);
            if (obj != null) {
                List<Integer> squareObj = (ArrayList<Integer>) obj;
                if (numbersToAvoid != null) {
                    squareObj = squareObj.stream().filter(n -> !numbersToAvoid.contains(n))
                            .collect(Collectors.toList());
                }
                if (squareObj.size() > 0) {
                    Map<Integer, Integer> listSizes = new HashMap<Integer, Integer>();
                    for (Integer c : squareObj) {
                        Integer size;
                        if (numbersToAvoid == null) {
                            size = (Integer) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SIZE).get(c);
                        } else {
                            size = ((ArrayList<Integer>) this.MATCHNUMBERS.get(KeysMatchNumbers.NUMBER_SQUARE).get(c))
                                    .stream().filter(n -> !numbersToAvoid.contains(n)).collect(Collectors.toList())
                                    .size();
                        }
                        listSizes.put(c, size);
                    }
                    Integer minSize = Collections.min(listSizes.values());
                    List<Integer> proxNums = listSizes.keySet().stream().filter(n -> listSizes.get(n) == minSize)
                            .collect(Collectors.toList());
                    if (proxNums.size() > 0) {
                        return proxNums;
                    }
                }
            }
            return null;
        }

        public void verifyMatchnumbers(Integer number) {
            List<List<Integer>> tracksToVerify = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                Boolean useAnteriorTrack = false;
                if (tracksToVerify.size() > 0) {
                    useAnteriorTrack = true;
                    tracksToVerify.stream().forEach(a -> Collections.reverse(a));
                }
                if (useAnteriorTrack) {
                    for (List<Integer> anteriorTrack : tracksToVerify) {
                        this.runTrack(number, tracksToVerify, anteriorTrack);
                    }
                } else {
                    tracksToVerify = this.runTrack(number, tracksToVerify, null);
                }
            }
            return;
        }

        @SuppressWarnings("unlikely-arg-type")
        public List<List<Integer>> runTrack(Integer number, List<List<Integer>> tracksToVerify,
                List<Integer> anteriorTrack) {
            List<HashMap<MapTrackKeys, Object>> tracks = new ArrayList<>();
            List<HashMap<MapTrackKeys, Object>> excluidTracks = new ArrayList<>();
            Boolean verifyHashes = false;
            while (true) {
                LinkedHashSet<Integer> track = this.getTrack(number, tracks, anteriorTrack, excluidTracks, verifyHashes);
                verifyHashes = !verifyHashes;
                if (track.size() == this.NUM) {
                    this.FINAL_ARRAY = track.stream().collect(Collectors.toList());
                    return tracksToVerify;
                } else if (tracks.size() == 0) {
                    return tracksToVerify;
                }
                if (anteriorTrack == null) {
                    if (!tracksToVerify.contains(track)) {
                        List<Integer> listMaxTrack = tracksToVerify.stream().max(Comparator.comparingInt(List::size))
                                .orElse(Collections.emptyList());
                        if (track.size() > listMaxTrack.size()) {
                            tracksToVerify = tracksToVerify.stream().filter(a -> a.size() != listMaxTrack.size())
                                    .collect(Collectors.toList());
                            tracksToVerify.add(track.stream().collect(Collectors.toList()));
                        } else if (track.size() == listMaxTrack.size()) {
                            tracksToVerify.add(track.stream().collect(Collectors.toList()));
                        }
                    }
                }
            }
        }

        @SuppressWarnings("unchecked")
        public LinkedHashSet<Integer> getTrack(Integer number, List<HashMap<MapTrackKeys, Object>> tracks,
                List<Integer> anteriorTrack, List<HashMap<MapTrackKeys, Object>> excluidTracks, Boolean verifyHashes) {
            Integer currentNumber = number;
            LinkedHashSet<Integer> track = new LinkedHashSet<>(
                    anteriorTrack != null ? anteriorTrack : new ArrayList<>());
            track.add(currentNumber);
            while (true) {
                List<Integer> nextNums = this.findProxNum(currentNumber, track);
                Integer nextNum = null;
                if (nextNums != null) {
                    final Integer finalCurrentNumber = currentNumber;
                    HashMap<MapTrackKeys, Object> hashExcluid = this.searchHash(excluidTracks, finalCurrentNumber, track);
                    if (hashExcluid == null) {
                        HashMap<MapTrackKeys, Object> hash = this.searchHash(tracks, finalCurrentNumber, track);
                        List<Integer> trackedNumbers = new ArrayList<>();
                        if (hash == null) {
                            hash = new HashMap<>();
                            trackedNumbers = new ArrayList<>();
                        } else {
                            trackedNumbers = (ArrayList<Integer>) hash.get(MapTrackKeys.TRACKED_VALUES);
                        }
                        List<Integer> filteredNums = new ArrayList<>();
                        List<Integer> finalTrackedNumbers = trackedNumbers;
                        filteredNums = nextNums.stream().filter(n -> !finalTrackedNumbers.contains(n))
                                .collect(Collectors.toList());
                        if (filteredNums.size() == 0) {
                            nextNum = nextNums.get(nextNums.size() - 1);
                        } else {
                            nextNum = filteredNums.get(0);
                        }
                        if (nextNums.size() > 1) {
                            if (hash != null && verifyHashes) {
                                Boolean result = this.isChoosedNumbertrack(tracks, currentNumber, excluidTracks);
                                if (result) {
                                    if (!trackedNumbers.contains(nextNum)) {
                                        trackedNumbers.add(nextNum);
                                    }
                                }
                            }
                            Integer index = tracks.indexOf(hash);
                            hash.put(MapTrackKeys.NUMBER, currentNumber);
                            hash.put(MapTrackKeys.TRACKED_VALUES, trackedNumbers);
                            hash.put(MapTrackKeys.TRACK_ARRAY, new LinkedHashSet<>(track));
                            if (!excluidTracks.contains(hash)) {
                                if (tracks.contains(hash)) {
                                    tracks.set(index, hash);
                                } else { 
                                    tracks.add(hash);
                                }
                            }
                        }
                    } else {
                        nextNum = nextNums.get(nextNums.size() - 1);
                    }
                }
                if (nextNum != null) {
                    track.add(nextNum);
                    currentNumber = nextNum;
                } else {
                    return track;
                }
            }
        }

        @SuppressWarnings("unchecked")
        public HashMap<MapTrackKeys, Object> searchHash(List<HashMap<MapTrackKeys, Object>> objectToSearch, Integer number, LinkedHashSet<Integer> track) {
            Optional<HashMap<MapTrackKeys, Object>> hash = objectToSearch.stream().filter(t -> {
                LinkedHashSet<Integer> trackArray = (LinkedHashSet<Integer>) t.get(MapTrackKeys.TRACK_ARRAY);
                if ((Integer) t.get(MapTrackKeys.NUMBER) == number && trackArray.equals(track)) {
                    return true;
                }
                return false;
            }).findFirst();
            if (hash.isPresent()) {
                return hash.get();
            } 
            return null;
        }

        @SuppressWarnings("unchecked")
        public Boolean isChoosedNumbertrack(List<HashMap<MapTrackKeys, Object>> tracks, Integer actualKey,
                List<HashMap<MapTrackKeys, Object>> excluidTracks) {
            while (true) {
                if (tracks.size() == 0) {
                    return false;
                }
                HashMap<MapTrackKeys, Object> hash = tracks.get(tracks.size() - 1);
                Integer lastKey = (Integer) hash.get(MapTrackKeys.NUMBER);
                Set<Integer> trackArray = (HashSet<Integer>) hash.get(MapTrackKeys.TRACK_ARRAY);
                List<Integer> trackedNumbers = (ArrayList<Integer>) hash.get(MapTrackKeys.TRACKED_VALUES);
                List<Integer> filteredArray = this.findProxNum(lastKey, trackArray);
                if (filteredArray != null) {
                    filteredArray = filteredArray.stream().filter(n -> !trackedNumbers.contains(n))
                            .collect(Collectors.toList());
                    if (filteredArray.size() == 0) {
                        tracks.remove(hash);
                        if (!excluidTracks.contains(hash)) {
                            excluidTracks.add(hash);
                        }
                    } else if (actualKey == lastKey) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    tracks.remove(hash);
                    if (!excluidTracks.contains(hash)) {
                        excluidTracks.add(hash);
                    }
                }
            }
        }
    }
}