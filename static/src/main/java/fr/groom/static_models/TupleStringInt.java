package fr.groom.static_models;

import java.util.*;

public class TupleStringInt {
	private List<String> strings;
	private int depth;

	public TupleStringInt(List<String> strings, int depth) {
		this.strings = strings;
		this.depth = depth;
	}

	public static TupleStringInt mergeSameDepth(ArrayList<TupleStringInt> tuples) {
		List<String> results = new ArrayList<>();
		int depth = tuples.get(0).getDepth();
		for (TupleStringInt tuple : tuples) {
			ArrayList<TupleStringInt> otherTuples = (ArrayList) tuples.clone();
			otherTuples.remove(tuple);
			for (String currentTupleString : tuple.getStrings()) {
				results.add(currentTupleString);
				for (TupleStringInt otherTuple : otherTuples) {
					for (String otherTupleString : otherTuple.getStrings()) {
						results.add(currentTupleString + otherTupleString);
					}
				}
			}
		}
		return new TupleStringInt(results, depth);
	}

	public static TupleStringInt mergeTwoTuplesDifferentDepth(TupleStringInt t1, TupleStringInt t2) {
		TupleStringInt biggestTuple;
		TupleStringInt smallestTuple;
		if (t1.getDepth() > t2.getDepth()) {
			biggestTuple = t1;
			smallestTuple = t2;
		} else {
			biggestTuple = t2;
			smallestTuple = t1;
		}
		List<String> results = new ArrayList<>();
		int depth = biggestTuple.getDepth();
		for (String biggestString : biggestTuple.getStrings()) {
			results.add(biggestString);
			for (String smallestString : smallestTuple.getStrings()) {
				results.add(biggestString + smallestString);
			}
		}
		return new TupleStringInt(results, depth);
	}

	public static TupleStringInt mergeTwoTuplesSameDepth(TupleStringInt t1, TupleStringInt t2) {
		List<String> results = new ArrayList<>();
		int depth = t1.getDepth();
		for (String t1String : t1.getStrings()) {
			results.add(t1String);
			for (String t2String : t2.getStrings()) {
				results.add(t1String + t2String);
			}
		}
		for (String t2String : t2.getStrings()) {
			results.add(t2String);
			for (String t1String : t1.getStrings()) {
				results.add(t2String + t1String);
			}
		}
		return new TupleStringInt(results, depth);
	}

	public static TupleStringInt merge(ArrayList<TupleStringInt> tuples) {
		tuples.sort(new DepthComparator());
		Stack<TupleStringInt> stack = new Stack<>();
		for (TupleStringInt tuple : tuples) {
			stack.push(tuple);
		}
		while (stack.size() > 1) {
			TupleStringInt t3;
			TupleStringInt t1 = stack.pop();
			TupleStringInt t2 = stack.pop();
			if (t1.getDepth() == t2.getDepth()) {
				t3 = mergeTwoTuplesSameDepth(t1, t2);
			} else {
				t3 = mergeTwoTuplesDifferentDepth(t1, t2);
			}
			stack.push(t3);
		}
		return stack.pop();
	}

	public static TupleStringInt mergeDifferentDepth(ArrayList<TupleStringInt> tuples) {
		tuples.sort(new DepthComparator());
		List<String> results = new ArrayList<>();
		List<String> firstResults = tuples.get(0).getStrings();
		int depth = tuples.get(0).getDepth();
		tuples.remove(0);
		Iterator<TupleStringInt> i = tuples.iterator();
		while (i.hasNext()) {
			TupleStringInt tuple = i.next();
			for (String result : firstResults) {
				for (String stringToMerge : tuple.getStrings()) {
					results.add(stringToMerge + result);
				}
			}
			i.remove();
		}
		results.addAll(firstResults);
		return new TupleStringInt(results, depth);
	}

	public static ArrayList<TupleStringInt> getSameDepthTuples(int depth, ArrayList<TupleStringInt> tuples) {
		tuples.removeIf(tuple -> tuple.getDepth() != depth);
		return tuples;
	}

	public static TupleStringInt mergeTuples(ArrayList<TupleStringInt> tuples) {
		ArrayList<TupleStringInt> differentDepthTuples = new ArrayList<>();

		HashMap<Integer, ArrayList<TupleStringInt>> sameDepthTuplesCluster = new HashMap<>();
		for (TupleStringInt tuple : tuples) {
			if (sameDepthTuplesCluster.containsKey(tuple.getDepth())) {
				ArrayList<TupleStringInt> ts = sameDepthTuplesCluster.get(tuple.getDepth());
				ts.add(tuple);
			} else {
				ArrayList<TupleStringInt> newEntry = new ArrayList<>();
				newEntry.add(tuple);
				sameDepthTuplesCluster.put(tuple.getDepth(), newEntry);
			}
		}

		Set<Map.Entry<Integer, ArrayList<TupleStringInt>>> entries = sameDepthTuplesCluster.entrySet();
		for (Map.Entry<Integer, ArrayList<TupleStringInt>> entry : entries) {
			TupleStringInt mergedTuple = mergeSameDepth(entry.getValue());
			differentDepthTuples.add(mergedTuple);
		}

		TupleStringInt mergedTuple = mergeDifferentDepth(differentDepthTuples);
		return mergedTuple;
	}

	public static int getMaxDepth(List<TupleStringInt> tuples) {
		int maxDepth = tuples.get(0).getDepth();

		for (TupleStringInt tuple : tuples) {
			if (tuple.getDepth() > maxDepth) {
				maxDepth = tuple.getDepth();
			}
		}
		return maxDepth;
	}

	public static ArrayList<TupleStringInt> getMaxDepthTuples(List<TupleStringInt> tuples) {
		int maxDepth = getMaxDepth(tuples);
		ArrayList<TupleStringInt> maxDepthTuples = new ArrayList<>();
		for (TupleStringInt tuple : tuples) {
			if (tuple.getDepth() == maxDepth) {
				maxDepthTuples.add(tuple);
			}
		}
		return maxDepthTuples;
	}

	public List<String> getStrings() {
		return strings;
	}

	public void setStrings(List<String> strings) {
		this.strings = strings;
	}

	public int getDepth() {
		return depth;
	}

	public void addString(String string) {
		strings.add(string);
	}

	public void mergeString(String string) {
		for (String savedString : this.strings) {
			strings.add(savedString + string);
			strings.add(string + savedString);
		}
	}

	public void mergeStringList(List<String> stringsToMerge) {
		for (String stringToMerge : stringsToMerge) {
			mergeString(stringToMerge);
		}
	}
}

class DepthComparator implements Comparator<TupleStringInt> {

	@Override
	public int compare(TupleStringInt o1, TupleStringInt o2) {
		return Integer.compare(o1.getDepth(), o2.getDepth());
	}
}
