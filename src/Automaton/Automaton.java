package Automaton;

import java.util.ArrayList;

import Exceptions.AlreadyInputSymbolExistentException;
import Exceptions.AlreadyResponseExistentException;
import Exceptions.AlreadyStateExistentException;
import Exceptions.NonExistentInputSymbolException;
import Exceptions.NonExistentStateException;

/**
 * Created by Camilo Tobar on 14/04/2018.
 */
public abstract class Automaton {

	// Types of automaton
	public final static String MEALY = "MEALY";
	public final static String MOORE = "MOORE";

	private String type;
	private int statesNumber;
	private int inSymbolsNumber;
	private int responsesNumber;

	private String[] inSymbols;
	private int[] responses;
	private char[] states;

	private int counterStates;
	private int counterInputSymbols;
	private int counterResponses;

	public Automaton(int statesNumber, int responsesNumber, int inputSymbolsNumber, String type) {
		this.type = type;
		counterStates = 0;
		counterInputSymbols = 0;
		counterResponses = 0;

		this.statesNumber = statesNumber;
		this.inSymbolsNumber = inputSymbolsNumber;
		this.responsesNumber = responsesNumber;

		this.responses = new int[responsesNumber];
		for (int i = 0; i < responsesNumber; i++)
			responses[i] = Integer.MIN_VALUE;
		this.inSymbols = new String[inputSymbolsNumber];
		this.states = new char[statesNumber];
	}

	// Elimina los estados inaccecibles desde el primer estado
	public abstract void eliminateInaccessibleStates() throws NonExistentStateException;

	// Reemplaza un estado por otro ingresado por parametro
	public abstract void replaceState(char state, char replace);

	// Retorna las particiones generadas por el algotimo de particion
	public abstract ArrayList<Character>[] partition() throws NonExistentStateException;

	// Verifica si dos arreglos son iguales
	public boolean sameArray(int[] array1, int[] array2) {
		for (int i = 0; i < array2.length; i++) {
			if (array1[i] != array2[i]) {
				return false;
			}
		}
		return true;
	}

	// Get the index of an input symbol at the inSymbols collection
	public int indexSymbol(String InputSymbol) throws NonExistentInputSymbolException {
		int index = -1;
		for (int i = 0; i < inSymbols.length && index == -1; i++)
			if (inSymbols[i].equals(InputSymbol))
				index = i;
		if (index != -1)
			return index;

		throw new NonExistentInputSymbolException("The " + InputSymbol + " input symbol doesn´t exist.");
	}

	// Get the index of a state at the states collection
	public int indexState(char InputState) throws NonExistentStateException {
		int index = -1;
		for (int i = 0; i < states.length && index == -1; i++)
			if (states[i] == InputState)
				index = i;
		if (index != -1)
			return index;

		throw new NonExistentStateException("The " + InputState + " state doesn´t exist.");
	}

	// Add a state, verifying if it does not exist yet
	public void addState(char state) throws AlreadyStateExistentException {
		for (char st : states)
			if (st == state)
				throw new AlreadyStateExistentException("The " + state + " state already exist.");
		states[counterStates] = state;
		counterStates++;
	}

	// Add an input symbol, verifying if it does not exist yet
	public void addInputSymbol(String symbol) throws AlreadyInputSymbolExistentException {
		for (String sy : inSymbols)
			if (sy != null && sy.equals(symbol))
				throw new AlreadyInputSymbolExistentException("The " + symbol + " input symbol already exist.");
		inSymbols[counterInputSymbols] = symbol;
		counterInputSymbols++;
	}

	// Add an response symbol, verifying if it does not exist yet
	public void addResponse(int symbol) throws AlreadyResponseExistentException {
		for (int sy : responses)
			if (sy == symbol)
				throw new AlreadyResponseExistentException("The " + symbol + " response symbol already exist.");
		responses[counterResponses] = symbol;
		counterResponses++;
	}
	
	/// Automaton Getters and Setters

	public int getStatesNumber() {
		return statesNumber;
	}

	public void setStatesNumber(int statesNumber) {
		this.statesNumber = statesNumber;
	}

	public int getInSymbolsNumber() {
		return inSymbolsNumber;
	}

	public void setInSymbolsNumber(int inSymbolsNumber) {
		this.inSymbolsNumber = inSymbolsNumber;
	}

	public String[] getInSymbols() {
		return inSymbols;
	}

	public void setInSymbols(String[] inSymbols) {
		this.inSymbols = inSymbols;
	}

	public char[] getStates() {
		return states;
	}

	public void setStates(char[] states) {
		this.states = states;
	}

	public int getResponsesNumber() {
		return responsesNumber;
	}

	public void setResponsesNumber(int responsesNumber) {
		this.responsesNumber = responsesNumber;
	}

	public int[] getResponses() {
		return responses;
	}

	public void setResponses(int[] responses) {
		this.responses = responses;
	}

	public String getType() {
		return type;
	}
}