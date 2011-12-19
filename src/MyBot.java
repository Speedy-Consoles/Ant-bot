import java.util.Iterator;
import java.util.LinkedList;

public class MyBot {

	private static int loadtime;
	private static int turnTime;
	private static int turns;
	private static int viewRadius2;
	private static int attackRadius2;
	private static int spawnRadius2;
	private static int visitRadius2 = 5;

	private static int turn = 0;
	private static long longestThinkTime = 0;
	private static long totalThinkTime = 0;

	private static LinkedList<Strategy> strategies;

	private static LinkedList<MyAnt> myAnts;
	private static LinkedList<EnemyAnt> enemyAnts;

	private static LinkedList<MyHill> myHills;
	private static LinkedList<MyHill> myOldHills;

	private static int enemyHillCount;
	private static LinkedList<Target> targets;
	private static LinkedList<Target> oldTargets;

	public static void main(String[] args) throws Exception {
		try {
			if (args.length > 0)
				Logger.setActivated(Integer.parseInt(args[0]) != 0);
			if (args.length > 1)
				Logger.setLogSuffix(args[1]);
		} catch (NumberFormatException e) {
		}
		try {
			Reader reader = new Reader();
			while (true) {
				reader.read();
			}
		} catch (Exception e) {
			if (Logger.isActivated())
				Logger.printException(e);
			else
				throw e;
		}
	}

	public static void load() {
		Field.initialize();
		Ant.initialize(MyBot.viewRadius2, MyBot.attackRadius2,
				MyBot.visitRadius2);
		MyBot.targets = new LinkedList<Target>();
		MyBot.myHills = new LinkedList<MyHill>();
		MyBot.myOldHills = new LinkedList<MyHill>();
		MyBot.strategies = new LinkedList<Strategy>();
		MyBot.strategies.add(new FightStrategy(7 / 10d));
		MyBot.strategies.add(new DefendStrategy(1 / 20d));
		MyBot.strategies.add(new FoodsHillsStrategy(1 / 10d));
		MyBot.strategies.add(new ExploreStrategy(3 / 40d));
		MyBot.strategies.add(new MoveToFrontStrategy(1 / 20d));
		MyBot.strategies.add(new SurviveStratetgy(1 / 40d));
		MyBot.prepareTurn();
		Logger.printLine("go");
		System.out.println("go");
	}

	private static void prepareTurn() {
		MyBot.myAnts = new LinkedList<MyAnt>();
		MyBot.enemyAnts = new LinkedList<EnemyAnt>();
		MyBot.myOldHills = MyBot.myHills;
		MyBot.myHills = new LinkedList<MyHill>();
		MyBot.oldTargets = MyBot.targets;
		MyBot.targets = new LinkedList<Target>();
		MyBot.enemyHillCount = 0;
		Field.turnReset();
		MyBot.turn++;
	}

	public static void doTurn() {
		MyBot.checkTargetsAndMyHills();

		long turnStartTime = System.currentTimeMillis();
		double ratioSum = 0;
		LinkedList<MyAnt> freeAnts = new LinkedList<MyAnt>(MyBot.myAnts);
		for (Strategy strategy : MyBot.strategies) {
			double ratio = strategy.getTimeRatio();
			long remainingTime = MyBot.turnTime - System.currentTimeMillis()
					+ turnStartTime;
			long taskTime = (long) (ratio / (1 - ratioSum) * remainingTime);
			long taskStartTime = System.currentTimeMillis();
			Logger.printLine("Starting with " + strategy + " with " + taskTime
					+ "ms");
			strategy.controllAnts(freeAnts, taskTime);
			long neededTime = System.currentTimeMillis() - taskStartTime;
			Logger.printLine(strategy + " took " + neededTime + "ms.");
			for (Iterator<MyAnt> iterator = freeAnts.iterator(); iterator
					.hasNext();) {
				MyAnt myAnt = iterator.next();
				if (myAnt.isBusy()) {
					iterator.remove();
				}
			}
			ratioSum += ratio;
		}
		printTurn();
		prepareTurn();
		long turnTime = System.currentTimeMillis() - turnStartTime;
		MyBot.totalThinkTime += turnTime;
		if (MyBot.longestThinkTime < turnTime) {
			MyBot.longestThinkTime = turnTime;
		}
		Logger.printLine("Whole turn took " + turnTime + "ms");
	}

	private static void checkTargetsAndMyHills() {
		for (Target target : MyBot.oldTargets) {
			if (!target.getField().isVisible()) {
				if (target.getClass().equals(Food.class)) {
					MyBot.targets.add(new Food(target.getField()));
				} else {
					MyBot.targets.add(new EnemyHill(target.getField(),
							((EnemyHill) target).getOwner()));
					MyBot.enemyHillCount++;
				}
			}
		}
		for (MyHill hill : MyBot.myOldHills) {
			if (!hill.getField().isVisible()) {
				MyBot.myHills.add(new MyHill(hill.getField()));
			}
		}

	}

	public static void printTurn() {
		for (MyAnt ant : MyBot.myAnts) {

			int dir = ant.getDirection();
			String direction = "";

			if (dir < 4) {
				switch (dir) {
				case 0:
					direction = "E";
					break;
				case 1:
					direction = "S";
					break;
				case 2:
					direction = "W";
					break;
				case 3:
					direction = "N";
					break;
				default:
					direction = "E";
					break;
				}
				String out = "o " + ant.getY() + " " + ant.getX() + " "
						+ direction;
				Logger.printLine(out);
				System.out.println(out);
			}
		}
		Logger.printLine("go");
		System.out.println("go");
	}

	public static void setLoadTime(int time) {
		MyBot.loadtime = time;
	}

	public static void setTurnTime(int time) {
		// TODO dummy time for tcp
		time = 500;
		MyBot.turnTime = time;
	}

	public static void setTurns(int turns) {
		MyBot.turns = turns;
	}

	public static void setViewRadius2(int radius2) {
		MyBot.viewRadius2 = radius2;
	}

	public static void setAttackRadius2(int radius) {
		MyBot.attackRadius2 = radius;
	}

	public static void setSpawnRadius2(int radius) {
		MyBot.spawnRadius2 = radius;
	}

	public static void setHill(int x, int y, int owner) {
		Field field = Field.getField(x, y);
		if (owner == 0 && !field.hasHill()) {
			MyHill hill = new MyHill(field);
			MyBot.myHills.add(hill);
		} else if (owner != 0 && !field.hasHill()) {
			EnemyHill hill = new EnemyHill(field, owner);
			MyBot.targets.add(hill);
			MyBot.enemyHillCount++;
		}
	}

	public static void setFood(int x, int y) {
		Field field = Field.getField(x, y);
		if (!field.hasFood()) {
			Food food = new Food(field);
			MyBot.targets.add(food);
		}
	}

	public static void setAnt(int x, int y, int owner) {
		Field field = Field.getField(x, y);
		if (owner == 0) {
			MyBot.myAnts.add(new MyAnt(field));
		} else {
			MyBot.enemyAnts.add(new EnemyAnt(field, owner));
		}
	}

	public static void antDead(int x, int y, int owner) {
	}

	public static int getTurns() {
		return MyBot.turns;
	}

	public static int getTurn() {
		return MyBot.turn;
	}

	public static void end() {
		Logger.printLine("exiting...");
		Logger.printLine("Average turn time was "
				+ (MyBot.totalThinkTime / MyBot.turn) + "ms");
		Logger.printLine("Longest turn took " + MyBot.longestThinkTime + "ms");
		System.exit(0);
	}

	public static LinkedList<MyAnt> getMyAnts() {
		return MyBot.myAnts;
	}

	public static LinkedList<Target> getTargets() {
		return MyBot.targets;
	}

	public static LinkedList<EnemyAnt> getEnemyAnts() {
		return MyBot.enemyAnts;
	}

	public static int getEnemyHillCount() {
		return MyBot.enemyHillCount;
	}

	public static int getAttackRadius2() {
		return MyBot.attackRadius2;
	}

	public static int getViewRadius2() {
		return MyBot.viewRadius2;
	}

	public static LinkedList<MyHill> getMyHills() {
		return MyBot.myHills;
	}
}
