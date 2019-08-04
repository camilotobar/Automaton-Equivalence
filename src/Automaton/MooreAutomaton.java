package Automaton;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import Exceptions.NonExistentInputSymbolException;
import Exceptions.NonExistentStateException;

/**
 * Created by Camilo Tobar on 14/04/2018.
 */
public class MooreAutomaton extends Automaton {

	private char[][] StateDiagram;
	private int[] statesResponses;
	private int numPartitions;

	public MooreAutomaton(int statesNumber, int inputSymbolsNumber, int responsesNumber) {
		super(statesNumber, responsesNumber, inputSymbolsNumber, Automaton.MOORE);
		StateDiagram = new char[statesNumber][inputSymbolsNumber];
		statesResponses = new int[statesNumber];
		numPartitions = 0;
	}

	// Removes the inaccessible states from the initial state
	@Override
	public void eliminateInaccessibleStates() throws NonExistentStateException {
		ArrayList<Character> visit = new ArrayList<Character>(getStatesNumber());
		Queue<Character> queue = new ArrayDeque<Character>();
		queue.add(getStates()[0]);

		while (!queue.isEmpty()) {
			char state = queue.poll();
			if (!visit.contains(state)) {
				visit.add(state);
				int stateIndex = indexState(state);
				int symbolsNumber = getInSymbolsNumber();
				for (int i = 0; i < symbolsNumber; i++) {
					char ady = StateDiagram[stateIndex][i];
					if (!visit.contains(ady))
						queue.add(ady);
				}
			}
		}
		char[] states = getStates();
		int statesNumber = getStatesNumber();
		for (int i = 0; i < statesNumber; i++) {
			if (!visit.contains(states[i])) {
				eliminateInaccessibleState(indexState(states[i]));
			}
		}
	}

	// Removes the input state from the state diagram
	private void eliminateInaccessibleState(int stateIndex) {

		char[] States = getStates();
		char[][] stateDiagram = new char[StateDiagram.length - 1][];
		char[] states = new char[getStatesNumber() - 1];
		int[] responses = new int[statesResponses.length - 1];

		if (stateIndex == (getStatesNumber() - 1)) {
			System.arraycopy(StateDiagram, 0, stateDiagram, 0, StateDiagram.length - 1); // copia la matriz sin incluir
																							// el ultimo

			System.arraycopy(States, 0, states, 0, States.length - 1);

			System.arraycopy(statesResponses, 0, responses, 0, statesResponses.length - 1);

		} else {
			System.arraycopy(StateDiagram, 0, stateDiagram, 0, stateIndex); // copia la primer parte de la matriz sin
																			// incluir el estado a eliminar

			System.arraycopy(StateDiagram, stateIndex + 1, stateDiagram, stateIndex,
					(StateDiagram.length - 1) - stateIndex); // copia parte de la matriz despues del estado a eliminar

			System.arraycopy(States, 0, states, 0, stateIndex);

			System.arraycopy(States, stateIndex + 1, states, stateIndex, (StateDiagram.length - 1) - stateIndex);

			System.arraycopy(statesResponses, 0, responses, 0, stateIndex);

			System.arraycopy(statesResponses, stateIndex + 1, responses, stateIndex,
					(statesResponses.length - 1) - stateIndex);
		}

		setStatesNumber(getStatesNumber() - 1);
		StateDiagram = stateDiagram;
		statesResponses = responses;
		setStates(states);
	}

	// Swaps two states
	@Override
	public void replaceState(char state, char replace) {
		char states[] = getStates();
		for (int i = 0; i < StateDiagram.length; i++) {
			if (states[i] == state) {
				states[i] = replace;
			}
			for (int j = 0; j < StateDiagram[0].length; j++) {
				if (StateDiagram[i][j] == state) {
					StateDiagram[i][j] = replace;
				}
			}
		}
		setStates(states);
	}

	// Returns the generated partitions of the automaton
	@Override
	public ArrayList<Character>[] partition() throws NonExistentStateException {
		// Step 4a
		numPartitions = getResponsesNumber();
		int[] partition = statesResponses;

		// Step 4b
		int[] nextPartition = partition(partition);

		// Step 4c
		while (!sameArray(partition, nextPartition)) {
			partition = nextPartition;
			nextPartition = partition(partition);
		}
		@SuppressWarnings("unchecked")
		ArrayList<Character>[] partitions = new ArrayList[numPartitions];
		for (int i = 0; i < numPartitions; i++) {
			partitions[i] = new ArrayList<Character>();
			for (int j = 0; j < partition.length; j++) {
				if (partition[j] == i) {
					partitions[i].add(getStates()[j]);
				}
			}
		}
		return partitions;
	}

	// Makes a partition according to the partition algorithm
	private int[] partition(int[] partition) throws NonExistentStateException {
		int partitions = numPartitions;
		int[] parti = partition.clone();
		for (int i = 0; i < partitions; i++) {
			ArrayList<Integer> positions = new ArrayList<>();
			for (int j = 0; j < partition.length; j++) {
				if (partition[j] == i) {
					positions.add(j);
				}
			}
			for (int k = 0; k < positions.size(); k++) {
				char[] a = StateDiagram[positions.get(k)];
				boolean newPart = false;
				for (int k2 = k + 1; k2 < positions.size(); k2++) {
					char[] b = StateDiagram[positions.get(k2)];
					for (int l = 0; l < b.length && (parti[positions.get(k)] == parti[positions.get(k2)]); l++) {
						if (partition[indexState(a[l])] != partition[indexState(b[l])]) {
							parti[positions.get(k2)] = numPartitions;
							newPart = true;
						}
					}
				}
				if (newPart) {
					numPartitions++;
				}
			}
		}
		return parti;
	}

	// Add a transition, from a state and input symbol to another state
	public void addStateTransition(char stateFrom, char stateTo, String inputSymbol)
			throws NonExistentStateException, NonExistentInputSymbolException {
		StateDiagram[indexState(stateFrom)][indexSymbol(inputSymbol)] = stateTo;
	}

	// Add a state response
	public void addStateResponse(char state, int value) throws NonExistentStateException {
		statesResponses[indexState(state)] = value;
	}

	public int getStateValue(char state) throws NonExistentStateException {
		return statesResponses[indexState(state)];
	}

	public char getStateTo(char stateFrom, String inputSymbol)
			throws NonExistentInputSymbolException, NonExistentStateException {
		return StateDiagram[indexState(stateFrom)][indexSymbol(inputSymbol)];
	}

	public char[][] getStateDiagram() {
		return StateDiagram;
	}

	public void setStateDiagram(char[][] stateDiagram) {
		StateDiagram = stateDiagram;
	}

	public int[] getStatesResponses() {
		return statesResponses;
	}

	public void setStatesResponses(int[] statesResponses) {
		this.statesResponses = statesResponses;
	}
}