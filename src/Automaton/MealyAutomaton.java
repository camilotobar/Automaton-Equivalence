package Automaton;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import Exceptions.NonExistentInputSymbolException;
import Exceptions.NonExistentStateException;

/**
 * Created by Camilo Tobar on 14/04/2018.
 */
public class MealyAutomaton extends Automaton {

	private int[][][] StateDiagram;
	private int numPartitions;

	public MealyAutomaton(int statesNumber, int inputSymbolsNumber, int responsesNumber) {
		super(statesNumber, responsesNumber, inputSymbolsNumber, Automaton.MEALY);
		StateDiagram = new int[statesNumber][inputSymbolsNumber][2];
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
					char ady = (char) StateDiagram[stateIndex][i][0];
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
		int[][][] stateDiagram = new int[StateDiagram.length - 1][][];
		char[] states = new char[getStatesNumber() - 1];

		if (stateIndex == (getStatesNumber() - 1)) {
			System.arraycopy(StateDiagram, 0, stateDiagram, 0, StateDiagram.length - 1); // copia la matriz sin incluir
																							// el ultimo

			System.arraycopy(States, 0, states, 0, States.length - 1);

		} else {
			System.arraycopy(StateDiagram, 0, stateDiagram, 0, stateIndex); // copia la primer parte de la matriz sin
																			// incluir el estado a eliminar

			System.arraycopy(StateDiagram, stateIndex + 1, stateDiagram, stateIndex,
					(StateDiagram.length - 1) - stateIndex); // copia parte de la matriz despues del estado a eliminar

			System.arraycopy(States, 0, states, 0, stateIndex);

			System.arraycopy(States, stateIndex + 1, states, stateIndex, (StateDiagram.length - 1) - stateIndex);

		}
		setStatesNumber(getStatesNumber() - 1);
		StateDiagram = stateDiagram;
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
				if (StateDiagram[i][j][0] == state) {
					StateDiagram[i][j][0] = replace;
				}
			}
		}
		setStates(states);
	}

	// Returns the generated partitions of the automaton
	@Override
	public ArrayList<Character>[] partition() throws NonExistentStateException {

		// Step 4a
		int[] partition = new int[getStatesNumber()];
		Arrays.fill(partition, -1);

		int[][] responses = new int[getStatesNumber()][getResponsesNumber()];

		for (int i = 0; i < StateDiagram.length; i++) {
			for (int j = 0; j < StateDiagram[0].length; j++) {
				responses[i][j] = StateDiagram[i][j][1];
			}
		}

		for (int i = 0; i < responses.length - 1; i++) {
			for (int j = i + 1; j < responses.length; j++) {
				if (sameArray(responses[i], responses[j])) {
					if (partition[i] == -1 && partition[j] == -1) {
						partition[i] = numPartitions;
						partition[j] = numPartitions;
						numPartitions++;
					} else if (partition[i] == -1) {
						partition[i] = partition[j];
					} else {
						partition[j] = partition[i];
					}
				}
			}
		}

		for (int i = 0; i < partition.length; i++) {
			if (partition[i] == -1) {
				partition[i] = numPartitions;
				numPartitions++;
			}
		}

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
		int[][] states = new int[getStatesNumber()][getResponsesNumber()];
		for (int j = 0; j < StateDiagram.length; j++) {
			for (int k = 0; k < StateDiagram[0].length; k++) {
				states[j][k] = StateDiagram[j][k][0];
			}
		}
		for (int i = 0; i < partitions; i++) {
			ArrayList<Integer> positions = new ArrayList<>();
			for (int j = 0; j < partition.length; j++) {
				if (partition[j] == i) {
					positions.add(j);
				}
			}
			for (int k = 0; k < positions.size(); k++) {
				int[] a = states[positions.get(k)];

				boolean newPart = false;
				for (int k2 = k + 1; k2 < positions.size(); k2++) {
					int[] b = states[positions.get(k2)];
					for (int l = 0; l < b.length && (parti[positions.get(k)] == parti[positions.get(k2)]); l++) {
						if (partition[indexState((char) a[l])] != partition[indexState((char) b[l])]) {
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

	// Add a transition, from a state and input symbol to another state with a
	// response
	public void addTransition(char stateFrom, char stateTo, String inputSymbol, int response)
			throws NonExistentStateException, NonExistentInputSymbolException {
		int indexFrom = indexState(stateFrom);
		int indexTo = indexSymbol(inputSymbol);
		StateDiagram[indexFrom][indexTo][0] = (int) stateTo;
		StateDiagram[indexFrom][indexTo][1] = response;
	}

	public int getResponse(char stateFrom, String inputSymbol)
			throws NonExistentStateException, NonExistentInputSymbolException {
		return StateDiagram[indexState(stateFrom)][indexSymbol(inputSymbol)][1];
	}

	public char getStateTo(char stateFrom, String inputSymbol)
			throws NonExistentStateException, NonExistentInputSymbolException {
		return (char) StateDiagram[indexState(stateFrom)][indexSymbol(inputSymbol)][0];
	}

	public int[][][] getStateDiagram() {
		return StateDiagram;
	}

	public void setStateDiagram(int[][][] stateDiagram) {
		StateDiagram = stateDiagram;
	}

}
