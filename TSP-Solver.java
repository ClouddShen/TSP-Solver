import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class aims to read file to obtain the information of each city and then
 * to solve a good traverse sequence of cities using a heuristic method.
 * 
 * @author 1821966
 * @Time 2020-6-11:05:01
 *
 */

public class TSPSolver {
	/**
	 * stores the total distance of the best city sequence
	 */
	static double best = Double.MAX_VALUE;
	/**
	 * stores the start time of the program
	 */
	static long start;
	/**
	 * constantly stores the current time to ensure the program not exceeds the time
	 * limit.
	 */
	static long end;

	public static ArrayList<City> readFile(String filename) {
		ArrayList<City> cities = new ArrayList<>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line = null;
			while ((line = in.readLine()) != null) {
				String[] blocks = line.trim().split("\\s+");
				if (blocks.length == 3) {
					City c = new City();
					c.city = Integer.parseInt(blocks[0]);
					c.x = Double.parseDouble(blocks[1]);
					c.y = Double.parseDouble(blocks[2]);
					cities.add(c);
				} else {
					continue;
				}
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		City.distances = new double[cities.size()][cities.size()];
		for (int i = 0; i < cities.size(); i++) {
			City ci = cities.get(i);
			for (int j = i; j < cities.size(); j++) {
				City cj = cities.get(j);
				City.distances[i][j] = City.distances[j][i] = Math
						.sqrt(Math.pow((ci.x - cj.x), 2) + Math.pow((ci.y - cj.y), 2));
			}
		}
		return cities;
	}

	public static ArrayList<City> solveProblem(ArrayList<City> citiesToVisit) {
		ArrayList<City> routine = new ArrayList<City>();
		City start = null;
		City current = null;
		// get city 0;
		for (int i = 0; i < citiesToVisit.size(); i++) {
			if (citiesToVisit.get(i).city == 0) {
				start = current = citiesToVisit.remove(i);
				routine.add(current);
				break;
			}
		}
		if (current == null) {
			System.out.println("Your problem instance is incorrect! Exiting...");
			System.exit(0);
		}
		// visit cities
		while (!citiesToVisit.isEmpty()) {
			double minDist = Double.MAX_VALUE;
			int index = -1;
			for (int i = 0; i < citiesToVisit.size(); i++) {
				double distI = current.distance(citiesToVisit.get(i));
				if (index == -1 || distI < minDist) {
					index = i;
					minDist = distI;
				}
			}
			current = citiesToVisit.remove(index);
			routine.add(current);
		}
		routine.add(start);
		return routine;
	}

	public static double printSolution(ArrayList<City> routine) {
		double totalDistance = 0.0;
		for (int i = 0; i < routine.size(); i++) {
			if (i != routine.size() - 1) {
				System.out.print(routine.get(i).city + "->");
				totalDistance += routine.get(i).distance(routine.get(i + 1));
			} else {
				System.out.println(routine.get(i).city);
			}
		}
		return totalDistance;
	}

	/**
	 * Moves the city at index "from" to index "to" inside the routine
	 *
	 * @param routine a list of city objects produced by an outer file
	 * @param from    position of the city to be move from
	 * @param to      position of the city to be move to
	 */

	private static void moveCity(ArrayList<City> routine, int from, int to) {
		// provide your code here.
		City t = routine.remove(from);
		if (from < to) {
			routine.add(to - 1, t);
		} else {
			routine.add(to, t);
		}
	}

	/**
	 * Evaluate the relocation of city and returns the change in total distance. The
	 * return value is (old total distance - new total distance). As a result, a
	 * positive value means that the relocation of city results in routine
	 * improvement; a negative value means that the relocation leads to worse
	 * routine. A zero value means same quality.
	 *
	 * @param routine a list of city objects produced by an outer file
	 * @param from    position of the city to be move from
	 * @param to      position of the city to be move to
	 * @return the difference of distance
	 */
	public static double evalMove(ArrayList<City> routine, int from, int to) {
		// your implementation goes here
		double oldDistance = routine.get(from).distance(routine.get(from - 1))
				+ routine.get(from).distance(routine.get(from + 1)) + routine.get(to).distance(routine.get(to - 1));
		double newDistance;
		if (from < to) {
			newDistance = routine.get(from - 1).distance(routine.get(from + 1))
					+ routine.get(from).distance(routine.get(to)) + routine.get(from).distance(routine.get(to - 1));
		} else {
			newDistance = routine.get(from - 1).distance(routine.get(from + 1))
					+ routine.get(from).distance(routine.get(to)) + routine.get(from).distance(routine.get(to - 1));
		}
		return oldDistance - newDistance;
	}

	/**
	 * execute a move operation to shorten the distance
	 * 
	 * @param routine a list of city objects produced by an outer file
	 * @return whether there can be a move operation to shorten the distance
	 */
	public static boolean moveFirstImprove(ArrayList<City> routine) {
		// your implementation goes here
		for (int i = 1; i < routine.size() - 1; i++) {
			for (int j = 1; j < i; j++) {
				double diff = evalMove(routine, i, j);
				if (diff - 0.00001 > 0) {
					moveCity(routine, i, j);
					return true;
				}
			}
			for (int j = i + 2; j < routine.size(); j++) {
				double diff = evalMove(routine, i, j);
				if (diff - 0.00001 > 0) {
					moveCity(routine, i, j);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * execute move operations by iterating the city list exhaustively to shorten
	 * the distance
	 * 
	 * @param routine a list of city objects produced by an outer file
	 */
	public static void moveAllImprove(ArrayList<City> routine) {
		// your implementation goes here
		for (int i = 1; i < routine.size() - 1; i++) {
			for (int j = 1; j < i; j++) {
				double diff = evalMove(routine, i, j);
				if (diff - 0.00001 > 0) {
					moveCity(routine, i, j);
				}
			}
			for (int j = i + 2; j < routine.size(); j++) {
				double diff = evalMove(routine, i, j);
				if (diff - 0.00001 > 0) {
					moveCity(routine, i, j);
				}
			}
		}
	}

	/**
	 * swap two cities, one in index1 position and the other one in index2 position
	 * 
	 * @param routine a list of city objects produced by an outer file
	 */
	public static void swapCity(ArrayList<City> routine, int index1, int index2) {
		// your implementation goes here
		Collections.swap(routine, index1, index2);
	}

	/**
	 * evaluate whether a swap operation can be executed
	 * 
	 * @param routine a list of city objects produced by an outer file
	 * @param index1  position of the city to be swapped
	 * @param index2  position of the city to be swapped
	 * @return the difference of distance
	 */
	public static double evalSwap(ArrayList<City> routine, int index1, int index2) {
		double oldDistance = 0;
		double newDistance = 0;
		if (index2 - index1 == 1) {
			oldDistance = routine.get(index1).distance(routine.get(index1 - 1))
					+ routine.get(index2).distance(routine.get(index2 + 1));
			newDistance = routine.get(index2).distance(routine.get(index1 - 1))
					+ routine.get(index1).distance(routine.get(index2 + 1));
		} else {
			oldDistance = routine.get(index1).distance(routine.get(index1 - 1))
					+ routine.get(index1).distance(routine.get(index1 + 1))
					+ routine.get(index2).distance(routine.get(index2 - 1))
					+ routine.get(index2).distance(routine.get(index2 + 1));

			newDistance = routine.get(index2).distance(routine.get(index1 - 1))
					+ routine.get(index2).distance(routine.get(index1 + 1))
					+ routine.get(index1).distance(routine.get(index2 - 1))
					+ routine.get(index1).distance(routine.get(index2 + 1));
		}

		return oldDistance - newDistance;
	}

	/**
	 * This function iterate through all possible swapping positions of cities. if a
	 * city swap is found to lead to shorter travelling distance, that swap action
	 * will be applied and the function will return true. If there is no good city
	 * swap found, it will return false.
	 * 
	 * @param routine a list of city objects produced by an outer file
	 * @return whether there can be a swap operation to shorten the distance
	 */
	public static boolean swapFirstImprove(ArrayList<City> routine) {
		for (int i = 1; i < routine.size() - 1; i++) {
			for (int j = i + 1; j < routine.size() - 1; j++) {
				double diff = evalSwap(routine, i, j);
				if (diff - 0.00001 > 0) { // I really mean diff > 0 here
					swapCity(routine, i, j);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * This function iterate through all possible swapping positions of cities. if a
	 * city swap is found to lead to shorter travelling distance, that swap action
	 * will be applied and the process goes on until the end of the sequence
	 * 
	 * @param routine a list of city objects produced by an outer file
	 */
	public static void swapAllImprove(ArrayList<City> routine) {
		for (int i = 1; i < routine.size() - 1; i++) {
			for (int j = i + 1; j < routine.size() - 1; j++) {
				double diff = evalSwap(routine, i, j);
				if (diff - 0.00001 > 0) { // I really mean diff > 0 here
					swapCity(routine, i, j);
				}
			}
		}
	}

	/**
	 * reverse an interval regulated by index1 and index2
	 * 
	 * @param index1  left position of the interval
	 * @param index2  right side of the interval
	 * @param routine a list of city objects produced by an outer file
	 */
	public static void intervalReverse(int index1, int index2, ArrayList<City> routine) {
		int len = (index2 - index1 - 1) / 2;
		for (int i = 0; i <= len; i++) {
			Collections.swap(routine, index1 + i, index2 - i);
		}
	}

	/**
	 * evaluate whether an interval reverse operation can be executed
	 * 
	 * @param routine a list of city objects produced by an outer file
	 * @param index1  position of the left side of the interval
	 * @param index2  position of the right side of the interval
	 * @return the difference of distance
	 */
	public static double evalReverse(ArrayList<City> routine, int index1, int index2) {
		double oldDistance = routine.get(index1 - 1).distance(routine.get(index1))
				+ routine.get(index2).distance(routine.get(index2 + 1));
		double newDistance = routine.get(index1 - 1).distance(routine.get(index2))
				+ routine.get(index1).distance(routine.get(index2 + 1));
		return oldDistance - newDistance;
	}

	/**
	 * execute the reverse operations by iterating the city list exhaustively to
	 * shorten the distance
	 * 
	 * @param routine a list of city objects produced by an outer file
	 * @return whether there can be at least one reverse operation to shorten the
	 *         distance
	 */
	public static boolean reverseAllImprove(ArrayList<City> routine) {
		boolean flag = false;
		for (int i = 1; i < routine.size() - 1; i++) {
			for (int j = i + 1; j < routine.size() - 1; j++) {
				double diff = evalReverse(routine, i, j);
				if (diff - 0.00001 > 0) { // I really mean diff > 0 here
					intervalReverse(i, j, routine);
					flag = true;
				}
			}
		}
		return flag;
	}

	/**
	 * execute the FP_swap operations by iterating the city list exhaustively to
	 * shorten the distance
	 * 
	 * @param routine a list of city objects produced by an outer file
	 */
	public static void FP_swap(ArrayList<City> routine) {
		for (int i = 1; i < routine.size() - 2; i++) {
			for (int j = i + 3; j < routine.size() - 2; j++) {
				double dis1 = routine.get(i - 1).distance(routine.get(i))
						+ routine.get(i + 1).distance(routine.get(i + 2))
						+ routine.get(j).distance(routine.get(j + 1));
				double dis2 = routine.get(i - 1).distance(routine.get(i + 2))
						+ routine.get(j).distance(routine.get(i + 1)) + routine.get(i).distance(routine.get(j + 1));
				if (dis1 > dis2) {
					moveCity(routine, i + 1, j + 1);
					moveCity(routine, i, j + 1);
				}
			}
		}
	}

	/**
	 * execute the FP_swap2 operations by iterating the city list exhaustively to
	 * shorten the distance(this is a minor change version of FP_swap)
	 * 
	 * @param routine a list of city objects produced by an outer file
	 */
	public static void FP_swap2(ArrayList<City> routine) {
		for (int j = 2; j < routine.size() - 2; j++) {
			for (int j2 = j + 4; j2 < routine.size() - 2; j2++) {
				double dis1 = routine.get(j - 2).distance(routine.get(j - 1))
						+ routine.get(j + 1).distance(routine.get(j + 2))
						+ routine.get(j2).distance(routine.get(j2 + 1));
				double dis2 = routine.get(j - 2).distance(routine.get(j + 2))
						+ routine.get(j2).distance(routine.get(j + 1))
						+ routine.get(j2 + 1).distance(routine.get(j - 1));
				if (dis1 > dis2) {
					moveCity(routine, j + 1, j2 + 1);
					moveCity(routine, j, j2 + 1);
					moveCity(routine, j - 1, j2 + 1);
				}
			}
		}
	}

	/**
	 * execute the FP_swap3 operations by iterating the city list exhaustively to
	 * shorten the distance(this is a minor change version of FP_swap)
	 * 
	 * @param routine a list of city objects produced by an outer file
	 */
	public static void FP_swap3(ArrayList<City> routine) {
		for (int j = 2; j < routine.size() - 2; j++) {
			for (int j2 = j + 4; j2 < routine.size() - 2; j2++) {
				double dis1 = routine.get(j - 2).distance(routine.get(j - 1))
						+ routine.get(j + 2).distance(routine.get(j + 3))
						+ routine.get(j2).distance(routine.get(j2 + 1));
				double dis2 = routine.get(j - 2).distance(routine.get(j + 3))
						+ routine.get(j - 1).distance(routine.get(j2 + 1))
						+ routine.get(j + 2).distance(routine.get(j2));
				if (dis1 > dis2) {
					moveCity(routine, j + 2, j2 + 1);
					moveCity(routine, j + 1, j2 + 1);
					moveCity(routine, j, j2 + 1);
					moveCity(routine, j - 1, j2 + 1);
				}
			}
		}
	}

	/**
	 * execute the 3-opt operations by iterating the city list exhaustively to
	 * shorten the distance
	 * 
	 * @param routine a list of city objects produced by an outer file
	 */
	public static void three_Opt(ArrayList<City> routine) {
		int size = routine.size();
		for (int i = 1; i < size - 3; i++) {
			end = System.currentTimeMillis();
			for (int j = i + 2; j < i + 500; j++) {
				for (int k = j + 2; k < j + 500 && k < size - 2; k++) {
					ArrayList<City> temp = new ArrayList<>(routine);
					City c1 = routine.get(i);
					City c2 = routine.get(i + 1);
					City c3 = routine.get(j);
					City c4 = routine.get(j + 1);
					City c5 = routine.get(k);
					City c6 = routine.get(k + 1);
					// There're 8 possible operation in 3-opt, yet 4 has been contained in 2-opt(in
					// my code, the reverse operator). So dis1 - dis4 represent the other 4, and
					// compared with the original distance(dis).
					double dis = c1.distance(c2) + c3.distance(c4) + c5.distance(c6);
					double dis1 = c1.distance(c5) + c2.distance(c4) + c3.distance(c6);
					double dis2 = c1.distance(c4) + c2.distance(c6) + c3.distance(c5);
					double dis3 = c1.distance(c3) + c2.distance(c5) + c4.distance(c6);
					double dis4 = c1.distance(c4) + c2.distance(c5) + c3.distance(c6);
					double[] store = new double[5];
					store[0] = dis;
					store[1] = dis1;
					store[2] = dis2;
					store[3] = dis3;
					store[4] = dis4;
					double min = dis;
					int index = 0;
					for (int l = 0; l < store.length; l++) {
						if (store[l] < min) {
							min = store[l];
							index = l;
						}
					}
					// choose the best operation from the 4 mentioned above, if all the 4 is worse
					// than the original one, nothing happens
					if (index == 1) {
						int ind = 0;
						for (int l = 0; l <= i; l++) {
							routine.set(ind++, temp.get(l));
						}
						for (int l = k; l >= j + 1; l--) {
							routine.set(ind++, temp.get(l));
						}
						for (int l = i + 1; l <= j; l++) {
							routine.set(ind++, temp.get(l));
						}
						for (int l = k + 1; l <= size - 1; l++) {
							routine.set(ind++, temp.get(l));
						}
					} else if (index == 2) {
						int ind = 0;
						for (int l = 0; l <= i; l++) {
							routine.set(ind++, temp.get(l));
						}
						for (int l = j + 1; l <= k; l++) {
							routine.set(ind++, temp.get(l));
						}
						for (int l = j; l >= i + 1; l--) {
							routine.set(ind++, temp.get(l));
						}
						for (int l = k + 1; l <= size - 1; l++) {
							routine.set(ind++, temp.get(l));
						}
					} else if (index == 3) {
						int ind = 0;
						for (int l = 0; l <= i; l++) {
							routine.set(ind++, temp.get(l));
						}
						for (int l = j; l >= i + 1; l--) {
							routine.set(ind++, temp.get(l));
						}
						for (int l = k; l >= j + 1; l--) {
							routine.set(ind++, temp.get(l));
						}
						for (int l = k + 1; l <= size - 1; l++) {
							routine.set(ind++, temp.get(l));
						}
					} else if (index == 4) {
						int ind = 0;
						for (int l = 0; l <= i; l++) {
							routine.set(ind++, temp.get(l));
						}
						for (int l = j + 1; l <= k; l++) {
							routine.set(ind++, temp.get(l));
						}
						for (int l = i + 1; l <= j; l++) {
							routine.set(ind++, temp.get(l));
						}
						for (int l = k + 1; l <= size - 1; l++) {
							routine.set(ind++, temp.get(l));
						}
					}
				}
			}
			if (end - start >= 297000) {
				break;
			}
		}
	}

	/**
	 * execute one case in 4-opt --- the double bridge operation. This aims to
	 * perturb the sequence.
	 * 
	 * @param routine a list of city objects produced by an outer file
	 */
	public static void double_bridge(ArrayList<City> routine) {
		int size = routine.size();
		ArrayList<City> temp = new ArrayList<>(routine);
		int i = (int) (Math.random() * size / 4 + 1);
		int j = i + (int) (Math.random() * size / 4);
		int k = j + (int) (Math.random() * size / 4);
		int l = k + (int) (Math.random() * size / 4);
		if (l >= size - 1) {
			l = size - 2;
		}

		int ind = 0;
		for (int m = 0; m <= i; m++) {
			routine.set(ind++, temp.get(m));
		}
		for (int m = k + 1; m <= l; m++) {
			routine.set(ind++, temp.get(m));
		}

		for (int m = j + 1; m <= k; m++) {
			routine.set(ind++, temp.get(m));
		}

		for (int m = i + 1; m <= j; m++) {
			routine.set(ind++, temp.get(m));
		}
		for (int m = l + 1; m <= size - 1; m++) {
			routine.set(ind++, temp.get(m));
		}
	}

	/**
	 * execute the reverse function until the distance can't be shorten anymore by
	 * it.
	 * 
	 * @param routine a list of city objects produced by an outer file
	 */
	public static void local_search(ArrayList<City> routine) {
		while (reverseAllImprove(routine))
			;
	}

	/**
	 * calculate the total distance of a city sequence
	 * 
	 * @param routine a list of city objects produced by an outer file
	 * @return the total distance of a city sequence
	 */
	public static double evaluateRoutine(ArrayList<City> routine) {
		double totalDistance = 0.0;
		for (int i = 0; i < routine.size() - 1; i++) {
			totalDistance += routine.get(i).distance(routine.get(i + 1));
		}
		return totalDistance;
	}


	/**
	 * improve the city sequence to ensure a relatively short total distance
	 * 
	 * @param routine a list of city objects produced by an outer file
	 */
	public static ArrayList<City> improveRoutine(ArrayList<City> routine) {
		start = System.currentTimeMillis();
		// use local_search to shorten the distance at first
		while (true) {
			ArrayList<City> temp = new ArrayList<City>(routine);
			double_bridge(routine);
			local_search(routine);
			double diff = evaluateRoutine(routine) - evaluateRoutine(temp);
			if (diff > 0) {
				routine = new ArrayList<City>(temp);
			}
			end = System.currentTimeMillis();
			if (end - start >= 180000) {
				break;
			}
		}
		// then use all the other operators to do the further optimization
		while (true) {
			swapAllImprove(routine);
			moveAllImprove(routine);
			reverseAllImprove(routine);
			FP_swap(routine);
			FP_swap2(routine);
			FP_swap3(routine);
			three_Opt(routine);
			end = System.currentTimeMillis();
			if (end - start >= 297000)
				return routine;
		}
	}
}
