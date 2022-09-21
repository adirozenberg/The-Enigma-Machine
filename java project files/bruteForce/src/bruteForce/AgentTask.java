package bruteForce;

import engine.ConfigurationDto;
import enigmaMachine.Configuration;
import enigmaMachine.EnigmaMachine;
import exceptions.InvalidInputException;
import factory.Factory;
import javafx.util.Pair;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class AgentTask implements Runnable {

    private final String contentToDecrypt;
    private EnigmaMachine enigmaMachine;
    private int missionExtent;
    private Configuration conf;
    private EnigmaDictionary enigmaDictionary;
    private BlockingQueue<CandidateToDecryptionDto> blockingQueueCandidates;
    private int id;

    public AgentTask(byte[] enigmaBytes, int missionExtent, Configuration conf, String contentToDecrypt, EnigmaDictionary enigmaDictionary, BlockingQueue<CandidateToDecryptionDto> blockingQueueCandidates,int id) throws IOException, ClassNotFoundException {
        this.enigmaMachine = EnigmaMachine.getMachineFromByte(enigmaBytes);
        this.missionExtent = missionExtent;
        this.conf = conf;
        this.contentToDecrypt = contentToDecrypt;
        this.enigmaDictionary = enigmaDictionary;
        this.blockingQueueCandidates= blockingQueueCandidates;
        this.id=id;

    }

    @Override
    public void run(){
        String endPosition="";
        boolean needToStop=false;
        for (int i = 0; i < conf.getSelectedRotors().size() ; i++) {
            endPosition+=enigmaMachine.getIoWheel().charAt(enigmaMachine.getIoWheel().length()-1);
        }
        for (int i = 0; i < missionExtent&& !needToStop; i++) {
            Configuration confBeforeInit=new Configuration(conf.getSelectedReflector(),conf.getSelectedRotors(),conf.getPositions(),conf.getSelectedPlugs());
            enigmaMachine.init(conf.getSelectedReflector(), getArrRotorFromCfi(conf), conf.getPositions(), conf.getSelectedPlugs());
            String output = activateMachine(contentToDecrypt);
            if (isCandidateToDecryption(output)) {
                StringBuilder sb =new StringBuilder();
                appendConfiguration(Factory.createDtoClassFromConfiguration(confBeforeInit),sb);
                try {
                    blockingQueueCandidates.put(new CandidateToDecryptionDto(output,sb.toString(),id/*todo*/));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if(conf.getPositions().equals(endPosition)){
                needToStop=true;
                continue;
            }
            incrementConf(conf);
        }
    }
    public String reverseStr(String positions) {
        String reverseStr="";
        for (int i = positions.length()-1; i >=0 ; i--) {
            reverseStr+=positions.charAt(i);
        }
        return reverseStr;
    }
    public void appendConfiguration(ConfigurationDto cfi, StringBuilder sb) {
        LinkedList<Pair<Integer,Integer>> initRotors=cfi.getSelectedRotors();
        sb.append("   <");
        for (int i = initRotors.size()-1; i >=1 ; i--) {
            sb.append(initRotors.get(i).getKey()+",");
        }
        sb.append(initRotors.get(0).getKey()+">");
        sb.append("<");
        String positions = cfi.getPositions();
        for (int i = initRotors.size()-1; i >=1; i--) {
            sb.append(positions.charAt(i)+"("+initRotors.get(i).getValue()+"),");
        }
        sb.append(positions.charAt(0)+"("+initRotors.get(0).getValue()+")>");
        LinkedList<Pair<Character,Character>>plugs=cfi.getSelectedPlugs();
        String reflector=getRoman(cfi.getSelectedReflector());
        sb.append("<"+reflector+">");
        if(plugs!=null&&plugs.size()!=0) {
            sb.append("<");
            for (int i = 0; i < plugs.size() - 1; i++) {
                sb.append(plugs.get(i).getValue() + "|" + plugs.get(i).getKey() + ",");
            }
            sb.append(plugs.get(plugs.size() - 1).getValue() + "|" + plugs.get(plugs.size() - 1).getKey() + ">");
        }
    }
    private  String getRoman(int selectedReflector) {
        switch (selectedReflector)
        {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
        }
        return null;
    }

    private void incrementConf(Configuration confToAgent) {
        String oldStartPosition = reverseStr(confToAgent.getPositions());
        boolean needBreak = false;
        for (int i = confToAgent.getSelectedRotors().size() - 1; i >= 0 && !needBreak; i--) {
            int index = enigmaMachine.getIoWheel().indexOf(oldStartPosition.charAt(i));
            if (index == enigmaMachine.getIoWheel().length() - 1) {
                oldStartPosition = replaceCharAtGivenPosition(i, enigmaMachine.getIoWheel().charAt(0), oldStartPosition);
            } else {
                char newChar = enigmaMachine.getIoWheel().charAt(index + 1);
                oldStartPosition = replaceCharAtGivenPosition(i, newChar, oldStartPosition);
                needBreak = true;
            }
        }
        confToAgent.setPositions(reverseStr(oldStartPosition));
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



    private boolean isCandidateToDecryption(String output) {
        Set<String> words = enigmaDictionary.getWords();
        String excludeChars = enigmaDictionary.getExcludeChars();
        String lala=output.toLowerCase();
        lala= lala.replace(excludeChars, "");
        for (int i = 0; i < excludeChars.length(); i++) {
            lala = lala.replace(Character.toString(excludeChars.charAt(i)), "");
        }
        String[] textArr = lala.split(" ");
        for (String tx : textArr) {
            if (!words.contains(tx))
                return false;
        }
        return true;
    }

    public String activateMachine(String input) {
        String output="";
        for (int i = 0; i < input.length(); i++) {
            output += enigmaMachine.activate(input.charAt(i));
        }
        return output;
    }
    private LinkedList<Integer> getArrRotorFromCfi(Configuration cfi) {
        LinkedList<Integer> rotors=new LinkedList<>();
        for (Pair<Integer,Integer> pair:cfi.getSelectedRotors()) {
            rotors.add(pair.getKey());
        }
        return rotors;
    }
}
