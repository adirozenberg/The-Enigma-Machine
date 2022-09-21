package engine;

import enigmaMachine.components.Plug;
import exceptions.InvalidInputException;
import javafx.util.Pair;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.LinkedList;

public interface engineering {
    void createMachineFromFile(String filePath) throws JAXBException, FileNotFoundException, InvalidInputException;
    String getMachineSpecification();
    void initMachine(int selectedReflector, LinkedList<Integer> selectedRotors,String positions, LinkedList<Pair<Character,Character>>selectedPlugs,boolean initToStart);
    String activateMachine(String input) throws InvalidInputException;
    StringBuilder initToStartConfiguration() throws InvalidInputException;
    StringBuilder getHistoryAndStatisticsSB();
}
