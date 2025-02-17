import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String args[]) {
        long tempoInicial = System.currentTimeMillis();
        System.out.println(organizeArray(121));
        long tempoFinal = System.currentTimeMillis();
        System.out.println(tempoFinal - tempoInicial);
    }

    public static List<Integer> organizeArray(Integer number) {
        Amount amount = new Amount(number);
        amount.findSquares();
        amount.matchNumbers();
        amount.runTrack(number, null);
        if (amount.FINAL_ARRAY != null) {
            return amount.FINAL_ARRAY;
        }
        return null;
    }

    public static class Amount {

        private Integer NUM;
        private List<Integer> SQUARES = new ArrayList<>();
        private HashMap<Integer, List<Integer>> MATCHNUMBERS = new HashMap<>();
        private List<Integer> FINAL_ARRAY = null;

        enum MapTrackKeys {
            TRACKED_VALUES, TRACK_ARRAY, NUMBER
        }

        public Amount(Integer num) {
            this.NUM = num;
            this.MATCHNUMBERS = new HashMap<>();
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
                    this.MATCHNUMBERS.put(c, list);
                }
            }
        }

        public List<Integer> findProxNum(Integer num, Set<Integer> numbersToAvoid) {
            List<Integer> squareObj = this.MATCHNUMBERS.get(num);
            squareObj = squareObj.stream().filter(n -> !numbersToAvoid.contains(n))
                    .collect(Collectors.toList());
            if (squareObj.size() > 0) {
                Map<Integer, Integer> listSizes = new HashMap<Integer, Integer>();
                for (Integer c : squareObj) {
                    Integer size = this.MATCHNUMBERS.get(c)
                            .stream().filter(n -> !numbersToAvoid.contains(n)).collect(Collectors.toList())
                            .size();
                    listSizes.put(c, size);
                }
                Integer minSize = Collections.min(listSizes.values());
                List<Integer> proxNums = listSizes.keySet().stream().filter(n ->
                listSizes.get(n) == minSize)
                .collect(Collectors.toList());
                // List<Integer> proxNums = listSizes.entrySet()
                //         .stream()
                //         .sorted(Map.Entry.comparingByValue())
                //         .map(Map.Entry::getKey)
                //         .collect(Collectors.toList());
                if (proxNums.size() > 0) {
                    return proxNums;
                }
            }
            return null;
        }

        public Boolean runTrack(Integer number,
                List<Integer> anteriorTrack) {
            List<HashMap<MapTrackKeys, Object>> tracks = new ArrayList<>();
            List<HashMap<MapTrackKeys, Object>> excluidTracks = new ArrayList<>();
            while (true) {
                this.updateTracks(tracks, excluidTracks);
                LinkedHashSet<Integer> track = this.getTrack(number, tracks, anteriorTrack, excluidTracks);
                if (anteriorTrack == null) {
                    List<Integer> reversedTrack = track.stream().collect(Collectors.toList());
                    Collections.reverse(reversedTrack);
                    Boolean result = this.runTrack(number, reversedTrack);
                    if (result) {
                        return true;
                    }
                } else if (tracks.size() == 0) {
                    this.updateTracks(tracks, excluidTracks);
                }
                if (track.size() == this.NUM) {
                    this.FINAL_ARRAY = track.stream().collect(Collectors.toList());
                    return true;
                } else if (tracks.size() == 0) {
                    return false;
                }
            }
        }

        @SuppressWarnings("unchecked")
        public LinkedHashSet<Integer> getTrack(Integer number, List<HashMap<MapTrackKeys, Object>> tracks,
                List<Integer> anteriorTrack, List<HashMap<MapTrackKeys, Object>> excluidTracks) {
            Integer currentNumber = number;
            LinkedHashSet<Integer> track = new LinkedHashSet<>(
                    anteriorTrack != null ? anteriorTrack : new ArrayList<>());
            track.add(currentNumber);
            while (true) {
                List<Integer> nextNums = this.findProxNum(currentNumber, track);
                Integer nextNum = null;
                if (nextNums != null) {
                    final Integer finalCurrentNumber = currentNumber;
                    HashMap<MapTrackKeys, Object> hashExcluid = this.searchHash(excluidTracks, finalCurrentNumber,
                            track);
                    if (hashExcluid == null) {
                        HashMap<MapTrackKeys, Object> hash = this.searchHash(tracks, finalCurrentNumber, track);
                        Boolean isHashInAmount = hash == null ? false : true;
                        List<Integer> trackedNumbers = new ArrayList<>();
                        if (!isHashInAmount) {
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
                            Integer index = tracks.indexOf(hash);
                            hash.put(MapTrackKeys.NUMBER, currentNumber);
                            hash.put(MapTrackKeys.TRACKED_VALUES, trackedNumbers);
                            hash.put(MapTrackKeys.TRACK_ARRAY, new LinkedHashSet<>(track));
                            if (isHashInAmount) {
                                tracks.set(index, hash);
                            } else {
                                tracks.add(hash);
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
        public HashMap<MapTrackKeys, Object> searchHash(List<HashMap<MapTrackKeys, Object>> objectToSearch,
                Integer number, LinkedHashSet<Integer> track) {
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
        public void updateTracks(List<HashMap<MapTrackKeys, Object>> tracks,
                List<HashMap<MapTrackKeys, Object>> excluidTracks) {
            while (true) {
                if (tracks.size() == 0) {
                    return;
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
                    } else {
                        trackedNumbers.add(filteredArray.get(0));
                        return;
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