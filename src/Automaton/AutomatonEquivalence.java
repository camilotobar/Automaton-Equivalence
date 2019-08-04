package Automaton;

/**
 * Created by Camilo Tobar on 14/04/2018.
 */
import java.io.*;
import java.util.ArrayList;

import Exceptions.AlreadyInputSymbolExistentException;
import Exceptions.AlreadyResponseExistentException;
import Exceptions.AlreadyStateExistentException;
import Exceptions.NonExistentInputSymbolException;
import Exceptions.NonExistentStateException;

public class AutomatonEquivalence {

	private BufferedReader in;
	private BufferedWriter out;
	private String type;
	private Automaton firstAutomaton;
	private Automaton secondAutomaton;

	public AutomatonEquivalence() {
		in = new BufferedReader(new InputStreamReader(System.in));
		out = new BufferedWriter(new OutputStreamWriter(System.out));
	}

	// Proves the automatons equivalence according to the algorithm
	public void equivalenceProve() throws NonExistentStateException, IOException, AlreadyResponseExistentException,
			AlreadyInputSymbolExistentException, AlreadyStateExistentException, NonExistentInputSymbolException {
		// Step 1
		eliminateInaccessibleStates();

		// Step 2
		renameStates();

		// Step 3
		Automaton directSum = directSum();

		// Steap 4
		ArrayList<Character>[] partition = directSum.partition();

		// Step 5
		boolean equivalent = verifyEquivalence(partition);

		// Message
		out.write("\nANSWER: The automatons " + (equivalent ? "are equivalents.\n " : "do not are equivalents.\n"));
	}

	// Removes the inaccessible states of both automatons
	public void eliminateInaccessibleStates() throws NonExistentStateException {
		firstAutomaton.eliminateInaccessibleStates(); // Elimina los estados del primer automata
		secondAutomaton.eliminateInaccessibleStates(); // Elimina los estados del segundo automata
	}

	// Rename the repeated states
	public void renameStates() {
		char[] statesFirst = firstAutomaton.getStates();
		char[] statesSecond = secondAutomaton.getStates();
		ArrayList<Character> rename = new ArrayList<Character>();
		ArrayList<Character> cont = new ArrayList<Character>();
		ArrayList<Character> cont2 = new ArrayList<Character>();

		// Selects the states that need to be renamed and fill two arraylist to contain they states
		for (char st : statesSecond) {
			cont2.add(st);
			for (char st2 : statesFirst) {
				if (!cont.contains(st2))
					cont.add(st2);
				if (st == st2 && !rename.contains(st)) {
					rename.add(st);
				}
			}
		}

		// Verify that a letter isn't contained in any of the automatons, to replace the marked states
		char letra = 65;
		for (char st : rename) {
			while (cont.contains(letra) || cont2.contains(letra)) {
				letra++;
			}
			cont.add(letra);
			secondAutomaton.replaceState(st, letra);
		}
	}

	// Makes the direct sum of both automatons
	public Automaton directSum()
			throws IOException, AlreadyResponseExistentException, AlreadyInputSymbolExistentException,
			AlreadyStateExistentException, NonExistentInputSymbolException, NonExistentStateException {

		if (type.equals(Automaton.MOORE)) {
			// Sum the states diagrams of both automaton
			char[][] stateDiagram = new char[firstAutomaton.getStatesNumber() + secondAutomaton.getStatesNumber()][];

			System.arraycopy(((MooreAutomaton) firstAutomaton).getStateDiagram(), 0, stateDiagram, 0,
					firstAutomaton.getStatesNumber());

			System.arraycopy(((MooreAutomaton) secondAutomaton).getStateDiagram(), 0, stateDiagram,
					firstAutomaton.getStatesNumber(), secondAutomaton.getStatesNumber());

			// Sum the states container of each automaton
			char[] states = new char[firstAutomaton.getStatesNumber() + secondAutomaton.getStatesNumber()];

			System.arraycopy(firstAutomaton.getStates(), 0, states, 0, firstAutomaton.getStatesNumber());

			System.arraycopy(secondAutomaton.getStates(), 0, states, firstAutomaton.getStatesNumber(),
					secondAutomaton.getStatesNumber());

			// Sum the responses container of each state of the automaton
			int[] statesResponses = new int[firstAutomaton.getStatesNumber() + secondAutomaton.getStatesNumber()];

			System.arraycopy(((MooreAutomaton) firstAutomaton).getStatesResponses(), 0, statesResponses, 0,
					firstAutomaton.getStatesNumber());

			System.arraycopy(((MooreAutomaton) secondAutomaton).getStatesResponses(), 0, statesResponses,
					firstAutomaton.getStatesNumber(), secondAutomaton.getStatesNumber());

			// Creates a new automaton with the sum of the initial automatons
			Automaton aut = new MooreAutomaton(stateDiagram.length, firstAutomaton.getInSymbolsNumber(),
					firstAutomaton.getResponsesNumber());

			// Allocation of the sum of the data in the new automaton
			aut.setResponses(firstAutomaton.getResponses());
			aut.setInSymbols(firstAutomaton.getInSymbols());
			aut.setStates(states);
			((MooreAutomaton) aut).setStateDiagram(stateDiagram);
			((MooreAutomaton) aut).setStatesResponses(statesResponses);

			return aut;

		} else {
			// Sum the states diagrams of both automaton
			int[][][] stateDiagram = new int[firstAutomaton.getStatesNumber() + secondAutomaton.getStatesNumber()][][];

			System.arraycopy(((MealyAutomaton) firstAutomaton).getStateDiagram(), 0, stateDiagram, 0,
					firstAutomaton.getStatesNumber());

			System.arraycopy(((MealyAutomaton) secondAutomaton).getStateDiagram(), 0, stateDiagram,
					firstAutomaton.getStatesNumber(), secondAutomaton.getStatesNumber());

			// Sum the states container of each automaton
			char[] states = new char[firstAutomaton.getStatesNumber() + secondAutomaton.getStatesNumber()];

			System.arraycopy(firstAutomaton.getStates(), 0, states, 0, firstAutomaton.getStatesNumber());

			System.arraycopy(secondAutomaton.getStates(), 0, states, firstAutomaton.getStatesNumber(),
					secondAutomaton.getStatesNumber());

			// Creates a new automaton with the sum of the initial automatons
			Automaton aut = new MealyAutomaton(firstAutomaton.getStatesNumber() + secondAutomaton.getStatesNumber(),
					firstAutomaton.getInSymbolsNumber(), firstAutomaton.getResponsesNumber());

			// Allocation of the sum of the data in the new automaton
			aut.setResponses(firstAutomaton.getResponses());
			aut.setInSymbols(firstAutomaton.getInSymbols());
			((MealyAutomaton) aut).setStateDiagram(stateDiagram);
			aut.setStates(states);

			return aut;
		}
	}

	// Verify that the automatons initial states are in the same partition and, 
	// in each partition exist as least one state of both automatons
	public boolean verifyEquivalence(ArrayList<Character>[] partition) {

		boolean equivalent = false;
		char[] statesFirst = firstAutomaton.getStates();
		char[] statesSecond = secondAutomaton.getStates();
		char q1First = firstAutomaton.getStates()[0];
		char q1Second = secondAutomaton.getStates()[0];
		ArrayList<Character> cont = new ArrayList<Character>();
		ArrayList<Character> cont2 = new ArrayList<Character>();

		// Fill the Collection with the sates of each automaton
		for (char st : statesSecond) {
			cont2.add(st);
		}
		for (char st : statesFirst) {
			cont.add(st);
		}

		// Verify that the initial states are in the same partition
		for (int i = 0; i < partition.length; i++) {
			if (partition[i].contains(q1First) && partition[i].contains(q1Second)) {
				equivalent = true;
			}
		}

		// If the initial states are in the same partition, 
		// it verifies that in each partition exist as least one state of both automatons
		if (equivalent) {
			for (int i = 0; i < partition.length; i++) {
				int first = 0;
				int second = 0;
				for (int j = 0; j < partition[i].size(); j++) {
					if (cont.contains(partition[i].get(j))) {
						first++;
					} else {
						second++;
					}
				}
				if (first == 0 || second == 0) {
					equivalent = false;
				}
			}
		}
		return equivalent;
	}

	// Add all the states and transitions for a Moore automaton, according to the
	// alphabet of responses and input symbols.
	public void addMooreData(MooreAutomaton moore, int[] responses, String[] inSymbols)
			throws IOException, AlreadyResponseExistentException, AlreadyInputSymbolExistentException,
			AlreadyStateExistentException, NonExistentInputSymbolException, NonExistentStateException {

		// Adding responses alphabet
		for (int response : responses)
			moore.addResponse(response);

		// Adding input symbols alphabet
		for (String inSym : inSymbols)
			moore.addInputSymbol(inSym);

		// Adding states
		String[] states = in.readLine().split(" ");
		for (String state : states)
			moore.addState(state.toUpperCase().charAt(0));

		// Adding states data
		String[] statesResponse = in.readLine().split(" ");
		for (int i = 0; i < states.length; i++) {
			char currentState = states[i].toUpperCase().charAt(0);
			moore.addStateResponse(currentState, Integer.parseInt(statesResponse[i]));

			// Adding transitions
			String[] stateTransitions = in.readLine().split(" ");
			for (int j = 0; j < inSymbols.length; j++)
				moore.addStateTransition(currentState, stateTransitions[j].toUpperCase().charAt(0), inSymbols[j]);
		}
	}

	// Add all the states and transitions for a Mealy automaton, according to the
	// alphabet of responses and input symbols.
	public void addMealyData(MealyAutomaton mealy, int[] responses, String[] inSymbols)
			throws IOException, AlreadyResponseExistentException, AlreadyInputSymbolExistentException,
			AlreadyStateExistentException, NonExistentInputSymbolException, NonExistentStateException {

		// Adding responses alphabet
		for (int response : responses)
			mealy.addResponse(response);

		// Adding input symbols alphabet
		for (String inSym : inSymbols)
			mealy.addInputSymbol(inSym);

		// Adding states
		String[] states = in.readLine().split(" ");
		for (String state : states)
			mealy.addState(state.toUpperCase().charAt(0));

		// Adding states transitions
		for (int i = 0; i < states.length; i++) {
			char currentState = states[i].toUpperCase().charAt(0);
			String[] stateTransitions = in.readLine().trim().split(" ");
			for (int j = 0; j < inSymbols.length; j++)
				mealy.addTransition(currentState, stateTransitions[j].toUpperCase().charAt(0), inSymbols[j],
						Integer.parseInt(stateTransitions[j].charAt(1) + ""));
		}
	}

	// Print two automatons at the Console
	public void printAutomatons(String type) throws IOException {
		out.write("\n");
		try {
			if (type.equals(Automaton.MOORE)) {
				printMoore((MooreAutomaton) firstAutomaton);
				out.write("\n");
				printMoore((MooreAutomaton) secondAutomaton);
			} else {
				printMealy((MealyAutomaton) firstAutomaton);
				out.write("\n");
				printMealy((MealyAutomaton) secondAutomaton);
			}
		} catch (Exception e) {
		}
	}

	// Print a Moore automaton
	public void printMoore(MooreAutomaton moore)
			throws IOException, NonExistentStateException, NonExistentInputSymbolException {
		char[] states = moore.getStates();
		String[] inSymbols = moore.getInSymbols();

		String str = "  |";
		for (int i = 0; i < inSymbols.length; i++)
			str += " " + inSymbols[i] + " |";
		out.write(str + "\n");

		for (int i = 0; i < states.length; i++) {
			str = states[i] + " |";
			for (int j = 0; j < inSymbols.length; j++)
				str += " " + moore.getStateTo(states[i], inSymbols[j]) + " |";
			str += " " + moore.getStateValue(states[i]);
			out.write(str + "\n");
		}
	}

	// Print a Mealy automaton
	public void printMealy(MealyAutomaton mealy)
			throws IOException, NonExistentStateException, NonExistentInputSymbolException {
		char[] states = mealy.getStates();
		String[] inSymbols = mealy.getInSymbols();

		String str = "  |";
		for (int i = 0; i < inSymbols.length; i++)
			str += "  " + inSymbols[i] + "  |";
		out.write(str + "\n");

		for (int i = 0; i < states.length; i++) {
			str = states[i] + " |";
			for (int j = 0; j < inSymbols.length; j++)
				str += " " + mealy.getStateTo(states[i], inSymbols[j]) + ","
						+ mealy.getResponse(states[i], inSymbols[j]) + " |";
			out.write(str + "\n");
		}
	}

	// Lee la informacion ingresada por consola //TODO
	public void read() throws NumberFormatException, IOException, AlreadyResponseExistentException,
			AlreadyInputSymbolExistentException, AlreadyStateExistentException, NonExistentInputSymbolException,
			NonExistentStateException {
		String[] data = in.readLine().split(" ");
		String type = data[0].toUpperCase();
		this.type = type;
		int inputSymbolsNumber = Integer.parseInt(data[1]);
		int responsesNumber = Integer.parseInt(data[2]);
		String[] inputSymbolsString = in.readLine().split(" ");
		String[] responsesString = in.readLine().split(" ");
		int[] responses = new int[responsesNumber];
		for (int i = 0; i < responsesNumber; i++)
			responses[i] = Integer.parseInt(responsesString[i]);

		// Creating the first automaton
		int statesNumberFirstAutomaton = Integer.parseInt(in.readLine().trim());
		firstAutomaton = type.equals(Automaton.MOORE)
				? new MooreAutomaton(statesNumberFirstAutomaton, inputSymbolsNumber, responsesNumber)
				: new MealyAutomaton(statesNumberFirstAutomaton, inputSymbolsNumber, responsesNumber);
		if (type.equals(Automaton.MOORE))
			addMooreData((MooreAutomaton) firstAutomaton, responses, inputSymbolsString);
		else
			addMealyData((MealyAutomaton) firstAutomaton, responses, inputSymbolsString);

		// Creating the second automaton
		int statesNumberSecondAutomaton = Integer.parseInt(in.readLine().trim());
		secondAutomaton = type.equals(Automaton.MOORE)
				? new MooreAutomaton(statesNumberSecondAutomaton, inputSymbolsNumber, responsesNumber)
				: new MealyAutomaton(statesNumberSecondAutomaton, inputSymbolsNumber, responsesNumber);
		if (type.equals(Automaton.MOORE))
			addMooreData((MooreAutomaton) secondAutomaton, responses, inputSymbolsString);
		else
			addMealyData((MealyAutomaton) secondAutomaton, responses, inputSymbolsString);
	}

	// Execute the automaton initialization and equivalence methods
	public void run() throws IOException, AlreadyResponseExistentException, AlreadyInputSymbolExistentException,
			AlreadyStateExistentException, NonExistentInputSymbolException, NonExistentStateException {
		read();
		equivalenceProve();
		// --- OPTIONAL: If you desire to print the automatons State Diagrams ---
		printAutomatons(type);
		out.close();
	}

	// Main Method
	public static void main(String[] args) throws IOException {
		AutomatonEquivalence equivalencia = new AutomatonEquivalence();
		try {
			equivalencia.run();
		} catch (NonExistentInputSymbolException NonExInput) {
			NonExInput.getStackTrace();
			System.out.println(NonExInput.getMessage());
		} catch (NonExistentStateException NonSt) {
			NonSt.getStackTrace();
			System.out.println(NonSt.getMessage());
		} catch (AlreadyInputSymbolExistentException AlrInput) {
			AlrInput.getStackTrace();
			System.out.println(AlrInput.getMessage());
		} catch (AlreadyStateExistentException AlrSt) {
			AlrSt.getStackTrace();
			System.out.println(AlrSt.getMessage());
		} catch (AlreadyResponseExistentException AlrRes) {
			AlrRes.getStackTrace();
			System.out.println(AlrRes.getMessage());
		}
	}

}
