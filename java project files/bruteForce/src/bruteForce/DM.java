package bruteForce;

import enigmaMachine.Configuration;
import enigmaMachine.EnigmaMachine;
import enigmaMachine.components.Reflector;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.*;

public class DM {
    private int numOfAgents;
    private int numOfAgentsInUse;
    private int missionExtent;
    private difficultyLevel difficultyLevelOfBruteForce;
    private EnigmaDictionary enigmaDictionary;
    private EnigmaMachine enigmaMachineCopy;
    private String contentToDecrypt;
    private BlockingQueue<Runnable> blockingQueueTasks = new LinkedBlockingQueue<Runnable>();
    private BlockingQueue<CandidateToDecryptionDto> blockingQueueCandidates = new LinkedBlockingQueue<CandidateToDecryptionDto>();
    CustomThreadPoolExecutor executor;

    private double progress = 0;
    private double maxConf;
    private boolean isFinish;
    private boolean pause = false;

    public boolean isPause() {
        return pause;
    }

    public DM() {
    }

    public DM(byte[] machineBytes, int numOfAgents, EnigmaDictionary enigmaDictionary) throws IOException, ClassNotFoundException {
        this.numOfAgents = numOfAgents;
        this.enigmaDictionary = enigmaDictionary;
        this.enigmaMachineCopy = EnigmaMachine.getMachineFromByte(machineBytes);
    }

    public void setDifficultyLevelOfBruteForce(bruteForce.difficultyLevel difficultyLevelOfBruteForce) {
        this.difficultyLevelOfBruteForce = difficultyLevelOfBruteForce;
    }

    public void setContentToDecrypt(String contentToDecrypt) {
        this.contentToDecrypt = contentToDecrypt;
    }

    public EnigmaDictionary getEnigmaDictionary() {
        return enigmaDictionary;
    }

    public void setEnigmaDictionary(EnigmaDictionary dictionary) {
        enigmaDictionary = dictionary;
    }

    public void setNumOfAgents(int agents) {
        numOfAgents = agents;
    }

    public int getNumOfAgents() {
        return numOfAgents;
    }

    public int getMissionExtent() {
        return missionExtent;
    }

    public void setMissionExtent(int missionExtent) {
        this.missionExtent = missionExtent;
    }

    public int getNumOfAgentsInUse() {
        return numOfAgentsInUse;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public void setNumOfAgentsInUse(int numOfAgentsInUse) {
        this.numOfAgentsInUse = numOfAgentsInUse;
    }

    public void start() throws InterruptedException, IOException, ClassNotFoundException {
        isFinish = false;
        progress = 0;
        pause=false;
        blockingQueueCandidates.clear();
        blockingQueueTasks.clear();
        // Let start all core threads initially
        executor = new CustomThreadPoolExecutor(2, numOfAgentsInUse, 5, TimeUnit.SECONDS,
                blockingQueueTasks, new ThreadPoolExecutor.AbortPolicy());
        executor.prestartAllCoreThreads();
        switch (difficultyLevelOfBruteForce) {
            case EASY:
                executeEasyLevel(enigmaMachineCopy.getInitConfiguration());
                maxConf = (int) Math.pow(enigmaMachineCopy.getIoWheel().length(), enigmaMachineCopy.getNumOfRotorsInUse());
                break;
            case MEDIUM:
                executeMediumLevel(enigmaMachineCopy.getInitConfiguration());
                maxConf = enigmaMachineCopy.getNumOfReflectors() * (int) Math.pow(enigmaMachineCopy.getIoWheel().length(), enigmaMachineCopy.getNumOfRotorsInUse());
                break;
            case HARD:
                int permutation = executeHardLevel(enigmaMachineCopy.getInitConfiguration());
                maxConf = permutation * enigmaMachineCopy.getNumOfReflectors() * (int) Math.pow(enigmaMachineCopy.getIoWheel().length(), enigmaMachineCopy.getNumOfRotorsInUse());
                break;
            case IMPOSSIBLE:
                int ncrPermutations = executeImpossibleLevel(enigmaMachineCopy.getInitConfiguration());
                maxConf = ncrPermutations * enigmaMachineCopy.getNumOfReflectors() * (int) Math.pow(enigmaMachineCopy.getIoWheel().length(), enigmaMachineCopy.getNumOfRotorsInUse());
                break;
        }
        executor.shutdown();
        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public void stopDMWork() throws InterruptedException {
        isFinish = true;
        pause = false;
    }

    private int executeImpossibleLevel(Configuration conf) throws IOException, ClassNotFoundException, InterruptedException {

        ArrayList<int[]> res = generate(enigmaMachineCopy.getRotorsCount(), enigmaMachineCopy.getNumOfRotorsInUse());
        Configuration newConfiguration;
        int permutation = 0;
        for (int[] arr : res) {
            newConfiguration = new Configuration(conf.getSelectedReflector(), getRotorsWithPos(getLinkedListFromArr(arr)), conf.getPositions(), conf.getSelectedPlugs());
            permutation = executeHardLevel(newConfiguration);
        }
        return res.size() * permutation;
    }

    private LinkedList<Integer> getLinkedListFromArr(int[] arr) {
        LinkedList<Integer> res = new LinkedList<>();
        for (int i = 0; i < arr.length; i++) {
            res.add(arr[i]);
        }
        return res;
    }

    public ArrayList<int[]> generate(int n, int r) {
        ArrayList<int[]> combinations = new ArrayList<>();
        int[] combination = new int[r];

        for (int i = 0; i < r; i++) {
            combination[i] = i;
        }

        while (combination[r - 1] < n) {
            combinations.add(combination.clone());
            int t = r - 1;
            while (t != 0 && combination[t] == n - r + t) {
                t--;
            }
            combination[t]++;
            for (int i = t + 1; i < r; i++) {
                combination[i] = combination[i - 1] + 1;
            }
        }
        for (int[] arr : combinations) {
            for (int i = 0; i < arr.length; i++) {
                arr[i]++;
            }
        }

        return combinations;
    }

    private int executeHardLevel(Configuration initConfiguration) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration newConfiguration;
        LinkedList<LinkedList<Integer>> res = new LinkedList<>();
        getAllPermutations(initConfiguration.getSelectedRotors().size(), getArrRotorFromCfi(initConfiguration), res);
        for (LinkedList<Integer> permute : res) {
            LinkedList<Pair<Integer, Integer>> selectedRotors = getRotorsWithPos(permute);
            newConfiguration = new Configuration(initConfiguration.getSelectedReflector(), selectedRotors, initConfiguration.getPositions(), initConfiguration.getSelectedPlugs());
            executeMediumLevel(newConfiguration);
        }
        return res.size();
    }

    private LinkedList<Pair<Integer, Integer>> getRotorsWithPos(LinkedList<Integer> permute) {
        LinkedList<Pair<Integer, Integer>> res = new LinkedList<>();
        for (int i = 0; i < permute.size(); i++) {
            res.add(new Pair<>(permute.get(i), enigmaMachineCopy.getRotorById(permute.get(i)).getPositionOfNotch() + 1));
        }
        return res;
    }

    private LinkedList<Integer> getArrRotorFromCfi(Configuration cfi) {
        LinkedList<Integer> rotors = new LinkedList<>();
        for (Pair<Integer, Integer> pair : cfi.getSelectedRotors()) {
            rotors.add(pair.getKey());
        }
        return rotors;
    }

    public void getAllPermutations(
            int n, LinkedList<Integer> elements, LinkedList<LinkedList<Integer>> res) {

        if (n == 1) {
            res.add(new LinkedList<>(elements));
        } else {
            for (int i = 0; i < n - 1; i++) {
                getAllPermutations(n - 1, elements, res);
                if (n % 2 == 0) {
                    swap(elements, i, n - 1);
                } else {
                    swap(elements, 0, n - 1);
                }
            }
            getAllPermutations(n - 1, elements, res);
        }
    }

    private void swap(LinkedList<Integer> input, int a, int b) {
        int tmp = input.get(a);
        input.set(a, input.get(b));
        input.set(b, tmp);
    }

    private void executeMediumLevel(Configuration initConfiguration) throws IOException, ClassNotFoundException, InterruptedException {
        ArrayList<Reflector> reflectors = enigmaMachineCopy.getReflectors();
        Configuration newConfiguration;
        for (Reflector reflector : reflectors) {
            newConfiguration = new Configuration(reflector.getId(), initConfiguration.getSelectedRotors(), initConfiguration.getPositions(), initConfiguration.getSelectedPlugs());
            executeEasyLevel(newConfiguration);
        }
    }

    private void executeEasyLevel(Configuration conf) throws IOException, ClassNotFoundException, InterruptedException {
        String abc = enigmaMachineCopy.getIoWheel();
        int numOfCong = (int) Math.pow(enigmaMachineCopy.getIoWheel().length(), enigmaMachineCopy.getNumOfRotorsInUse());
        int numOfRotors = conf.getSelectedRotors().size();
        String startPositions = "";
        for (int i = 0; i < numOfRotors; i++) {
            startPositions += abc.charAt(0);
        }
        conf.setPositions(startPositions);
        int id = 0;
        do {
            if(!pause) {
                getNextConf(conf);
                blockingQueueTasks.put(new AgentTask(EnigmaMachine.getMachineByte(enigmaMachineCopy), missionExtent, new Configuration(conf.getSelectedReflector(), conf.getSelectedRotors(), conf.getPositions(), conf.getSelectedPlugs()), contentToDecrypt, enigmaDictionary, blockingQueueCandidates, id % numOfAgentsInUse + 1));
                id++;
                progress += missionExtent;
            }
            if(isFinish==true)
                break;
        } while (getNextConfBoolean(conf));
    }


    private boolean getNextConfBoolean(Configuration confToAgent) {
        String oldStartPosition=reverseStr(confToAgent.getPositions());
        String endPosition=getEndPosition(confToAgent);

        boolean needBreak;
        for (int count = 0; count < missionExtent; count++) {
            needBreak=false;
            for (int i =confToAgent.getSelectedRotors().size()-1 ; i >=0 &&!needBreak; i--) {
                int index= enigmaMachineCopy.getIoWheel().indexOf(oldStartPosition.charAt(i));
                if(index==enigmaMachineCopy.getIoWheel().length()-1) {
                    oldStartPosition=replaceCharAtGivenPosition(i,enigmaMachineCopy.getIoWheel().charAt(0),oldStartPosition);
                    if(oldStartPosition.equals(endPosition)){
                        return false;
                    }
                }
                else
                {
                    char newChar=enigmaMachineCopy.getIoWheel().charAt(index+1);
                    oldStartPosition=replaceCharAtGivenPosition(i,newChar,oldStartPosition);
                    if(oldStartPosition.equals(endPosition)){
                        return false;
                    }
                    needBreak=true;
                }
            }
            if(oldStartPosition.equals(endPosition)){
                return false;
            }
        }
        return true;
    }

    public double getProgress() {
        if(progress>maxConf)
            return 1;
        return progress/maxConf;
    }
    private boolean getNextConf(Configuration confToAgent) {
        String oldStartPosition=reverseStr(confToAgent.getPositions());
        String endPosition=getEndPosition(confToAgent);

        boolean needBreak;
        for (int count = 0; count < missionExtent; count++) {
            needBreak=false;
            for (int i =confToAgent.getSelectedRotors().size()-1 ; i >=0 &&!needBreak; i--) {
                int index= enigmaMachineCopy.getIoWheel().indexOf(oldStartPosition.charAt(i));
                if(index==enigmaMachineCopy.getIoWheel().length()-1) {
                    oldStartPosition=replaceCharAtGivenPosition(i,enigmaMachineCopy.getIoWheel().charAt(0),oldStartPosition);
                    if(oldStartPosition.equals(endPosition)){
                        return false;
                    }
                }
                else
                {
                    char newChar=enigmaMachineCopy.getIoWheel().charAt(index+1);
                    oldStartPosition=replaceCharAtGivenPosition(i,newChar,oldStartPosition);
                    if(oldStartPosition.equals(endPosition)){
                        return false;
                    }
                    needBreak=true;
                }
            }
            if(oldStartPosition.equals(endPosition)){
                confToAgent.setPositions(reverseStr(oldStartPosition));
                return false;
            }
        }
        confToAgent.setPositions(reverseStr(oldStartPosition));
        return true;
    }
    public String reverseStr(String positions) {
        String reverseStr="";
        for (int i = positions.length()-1; i >=0 ; i--) {
            reverseStr+=positions.charAt(i);
        }
        return reverseStr;
    }
    private String getEndPosition(Configuration conf) {
        String endPosition="";
        for (int i = 0; i < conf.getSelectedRotors().size(); i++) {
            endPosition+=enigmaMachineCopy.getIoWheel().charAt(enigmaMachineCopy.getIoWheel().length()-1);
        }
        return endPosition;
    }

    private String replaceCharAtGivenPosition(int i, char newChar,String str) {
        String res="";
        for (int j = 0; j < str.length(); j++) {
            if(j==i)
                res+=newChar;
            else
                res+=str.charAt(j);
        }
        return res;
    }

    public difficultyLevel convertDifficultyLevelToEnum(String value) {

        switch (value) {
            case "Medium":
                return  difficultyLevel.MEDIUM;
            case "Hard":
                return difficultyLevel.HARD;
            case "Impossible":
                return difficultyLevel.IMPOSSIBLE;
            case "Easy":
            default:
                return difficultyLevel.EASY;
        }
    }
    public CandidateToDecryptionDto getAgentsCandidates() throws InterruptedException {
        return  blockingQueueCandidates.take();
    }

    public void setMachine(byte[] machineBytes) throws IOException, ClassNotFoundException {
        this.enigmaMachineCopy = EnigmaMachine.getMachineFromByte(machineBytes);
    }

    public boolean thereISCandidates() {
        return !blockingQueueCandidates.isEmpty();
    }

    public void pauseWork() {
        pause=true;
    }

    public void resumeWork() {
        pause=false;
    }
}
