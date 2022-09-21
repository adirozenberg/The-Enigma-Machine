package factory;

import bruteForce.Decipher;
import bruteForce.EnigmaDictionary;
import engine.ConfigurationDto;
import enigmaMachine.Configuration;
import enigmaMachine.EnigmaMachine;
import enigmaMachine.components.Reflector;
import enigmaMachine.components.Rotor;
import exceptions.InvalidInputException;
import generated.*;

import java.util.*;

public class Factory {

   public static ConfigurationDto createDtoClassFromConfiguration(Configuration cfi)
   {
     return new ConfigurationDto(cfi.getSelectedReflector(),cfi.getSelectedRotors(),cfi.getPositions(),cfi.getSelectedPlugs());
   }

    public static EnigmaMachine createEnigmaFromGeneratedEnigma(CTEEnigma cteEnigma) throws InvalidInputException{
        List<CTEReflector> cteReflectors=cteEnigma.getCTEMachine().getCTEReflectors().getCTEReflector();
        List<CTERotor> cteRotors =cteEnigma.getCTEMachine().getCTERotors().getCTERotor();
        int rotorsCount =cteEnigma.getCTEMachine().getRotorsCount();

        checkValidRotors(cteRotors,rotorsCount);

        String abc=cteEnigma.getCTEMachine().getABC().replaceAll("\t","");
        abc=abc.replaceAll("\t\r\n","");
        abc=abc.replaceAll("\n","");
        abc=abc.replaceAll("ESC","");
        if(abc.length()%2!=0) {
            throw new InvalidInputException("The abc length should be even!");
        }
        abc=abc.toUpperCase();

        ArrayList<Rotor> rotors=getRotorsFromGeneratedRotors(cteRotors,abc.length());
        for (Rotor r:rotors) {
            r.setRight(toUpperCaseArr(r.getRight()));
            r.setLeft(toUpperCaseArr(r.getLeft()));
            for (int i = 0; i < abc.length(); i++) {
                int numOfAppearance=appearanceOfCharCount(r.getLeft(),abc.charAt(i));
                if(numOfAppearance>1 || numOfAppearance==0)
                {
                    throw new InvalidInputException("The mapping of the rotor should be non-duplicated!");
                }
                numOfAppearance=appearanceOfCharCount(r.getRight(),abc.charAt(i));
                if(numOfAppearance>1 || numOfAppearance==0)
                {
                    throw new InvalidInputException("The mapping of the rotor should be non-duplicated!");
                }
            }
            if(!isRotorCharactersValid(r.getLeft(),abc)){
                throw new InvalidInputException("The character of the rotors should be from the abc which is: "+abc);
            }
            if(!isRotorCharactersValid(r.getRight(),abc)){
                throw new InvalidInputException("The character of the rotors should be from the abc which is: "+abc);

            }
            if(!isInRangeAbc(r.getPositionOfNotch(),abc)){
                throw new InvalidInputException("The notch position in rotor should be in range of abc length!");
            }

        }
        if(cteReflectors.size() > 5)
        {
            throw new InvalidInputException("The maximum size of reflectors is 5!");
        }
        ArrayList<Reflector> reflectors= getReflectorsFromGenerateReflectors(cteReflectors,abc);
        if(!isUniqueIdReflectors(reflectors)){
            throw new InvalidInputException("The reflector id need to be unique!");
        }
        return new EnigmaMachine(rotorsCount, reflectors.size(),rotors,reflectors,abc);
    }

    private static boolean isUniqueIdReflectors(ArrayList<Reflector> reflectors) {
       boolean[] checkValidArr=new boolean[reflectors.size()];
        for (int i = 0; i < checkValidArr.length; i++) {
            checkValidArr[i]=false;
        }
        for (Reflector r:reflectors) {
            if(checkValidArr[r.getId()-1])
                return false;
            checkValidArr[r.getId()-1]=true;
        }
        return true;
    }

    private static boolean isInRangeAbc(int position, String abc) {
       if(position < 0 || position >= abc.length())
           return false;
       return true;
    }
    private static ArrayList<Character> toUpperCaseArr(ArrayList<Character> str){
        ArrayList<Character> upperStr=new ArrayList<>();
        for (Character ch:str) {
            upperStr.add(ch.toString().toUpperCase().charAt(0));
        }
       return upperStr;
    }
    private static boolean isRotorCharactersValid(ArrayList<Character> left, String abc) {
       for (Character ch:left) {
            if(!abc.contains(ch.toString())){
                return false;
            }
        }
        return true;
    }

    private static int appearanceOfCharCount(ArrayList<Character> arr, char charAt) {
        int count=0;
        for (Character ch:arr) {
            if(ch.equals(charAt)){
                count++;
            }
        }
        return count;
    }


    private static void checkValidRotors(List<CTERotor> cteRotors, int rotorsCount) throws InvalidInputException {
        if(rotorsCount> cteRotors.size()) {
            throw new InvalidInputException("The rotors count need to be equals or less than size of the rotors list!");
        }
        if(rotorsCount<2) {
            throw new InvalidInputException("The rotors count should be 2 or bigger!");
        }
        Boolean[] checkRotorId=new Boolean[cteRotors.size()];
        for (int i = 0; i <checkRotorId.length ; i++) {
            checkRotorId[i]=false;
        }
        for (CTERotor rotor:cteRotors) {
            if(rotor.getId()-1>= checkRotorId.length || rotor.getId()<= 0) {
                throw new InvalidInputException("Rotors id should be unique and in the range of 1 to rotors count!");
            }
            checkRotorId[rotor.getId()-1]=true;
        }
        for (int i = 0; i < checkRotorId.length ; i++) {
            if(checkRotorId[i]==false) {
                throw new InvalidInputException("Rotors id should be unique and in the range of 1 to rotors count!");
            }
        }
    }

    private static ArrayList<Reflector> getReflectorsFromGenerateReflectors(List<CTEReflector> cteReflectors,String abc) throws InvalidInputException {
     ArrayList<Reflector> reflectors=new ArrayList<>();
        for (CTEReflector cteReflector:cteReflectors) {
            reflectors.add(convertCteReflectorToReflector(cteReflectors.size(),cteReflector,abc));
        }
        return reflectors;
    }

    private static Reflector convertCteReflectorToReflector(int numOfReflector,CTEReflector cteReflector,String abc) throws InvalidInputException  {
        Map<Integer, Integer> reflectorContent=new HashMap<>();
        List<CTEReflect> cteReflects = cteReflector.getCTEReflect();
        boolean[] checkValidArr=new boolean[abc.length()];
        for (int i = 0; i <checkValidArr.length ; i++) {
            checkValidArr[i]=false;
        }
        for (CTEReflect cteReflect:cteReflects) {
            if(cteReflect.getOutput()==cteReflect.getInput())
            {
                throw new InvalidInputException("The reflector mapping should be for different character!");
            }
            if(!(isInRangeAbc(cteReflect.getInput()-1, abc)&&isInRangeAbc(cteReflect.getOutput()-1, abc))){
                throw new InvalidInputException("The reflector positions need to be in range 1-"+abc.length()+"!");
            }
            if(checkValidArr[cteReflect.getInput()-1]||checkValidArr[cteReflect.getInput()-1]){
                throw new InvalidInputException("The mapping of the reflector should be one-to-one function!");
            }
            checkValidArr[cteReflect.getInput()-1]=true;
            checkValidArr[cteReflect.getOutput()-1]=true;
            reflectorContent.put(cteReflect.getInput()-1, cteReflect.getOutput()-1);
            reflectorContent.put(cteReflect.getOutput()-1, cteReflect.getInput()-1);
        }
        for (int i = 0; i <checkValidArr.length ; i++) {
             if(checkValidArr[i]==false)
                 throw  new InvalidInputException("The reflector has a missing mapping!");
        }

        int id=convertRomanToInt(cteReflector.getId());
        if(id>numOfReflector) {
            throw  new InvalidInputException("The reflector ids need to be in ascending order!");
        }
        return new Reflector(id,reflectorContent);
    }

    private static int convertRomanToInt(String id) throws InvalidInputException {
        int res=0;
        switch (id)
        {
            case "I":
                res=1;
                break;
            case "II":
                res=2;
                break;
            case "III":
                res=3;
                break;
            case "IV":
                res=4;
                break;
            case "V":
                res=5;
                break;
            default:
                throw new InvalidInputException("The reflector id needs to be between I-IV");
        }
        return res;
    }

    private static ArrayList<Rotor> getRotorsFromGeneratedRotors(List<CTERotor> cteRotors,int numOfPositions) {
        ArrayList<Rotor> rotors=new ArrayList<>();
        for (CTERotor cteRotor:cteRotors) {
            rotors.add(convertCteRotorToRotor(cteRotor,numOfPositions));
        }
        return rotors;
    }

    private static Rotor convertCteRotorToRotor(CTERotor cteRotor,int numOfPositions) {
        int index=cteRotor.getId();
        ArrayList<Character> right=new ArrayList<>();
        ArrayList<Character> left=new ArrayList<>();
        List<CTEPositioning> ctePositioning=cteRotor.getCTEPositioning();
        for (CTEPositioning ctePosition:ctePositioning) {
            left.add(ctePosition.getLeft().charAt(0));
            right.add(ctePosition.getRight().charAt(0));
        }
        int positionOfNotch=cteRotor.getNotch();
        return new Rotor(index,numOfPositions,positionOfNotch-1,right,left);
    }

    public static Decipher createDecipherFromGeneratedDecipher(CTEDecipher cteDecipher) throws InvalidInputException {
       EnigmaDictionary dictionary=convertCteDictionaryToDictionary(cteDecipher.getCTEDictionary());
       if (!(cteDecipher.getAgents() >= 2 && cteDecipher.getAgents() <= 50)) {
           throw new InvalidInputException("The number of agents needs to be between 2-50");
       }

       return new Decipher(dictionary,cteDecipher.getAgents());
    }

    private static EnigmaDictionary convertCteDictionaryToDictionary(CTEDictionary cteDictionary) {
       String words= cteDictionary.getWords();
        words=words.replaceAll("\t\r\n","");
        words=words.replaceAll("\t","");
        words=words.replaceAll("\r","");
        words=words.replaceAll("\n","");
        words=words.replaceAll("ESC","");
        String excludeChars=cteDictionary.getExcludeChars();
        words=words.replace(excludeChars,"");
        for (int i = 0; i < excludeChars.length(); i++) {
           words=words.replace(Character.toString(excludeChars.charAt(i)),"");
         }

        Set<String>dict=new HashSet<>();
       String[] wordsArr=words.split(" ");
        for (String word:wordsArr) {
            dict.add(word);
        }
        return new EnigmaDictionary(dict,excludeChars);
   }
}
