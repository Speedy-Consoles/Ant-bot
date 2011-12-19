

import java.io.IOException;

public class Reader {

	public void read() {
		StringBuilder builder;
		int read;
		builder = new StringBuilder();
		do {
			try {
				read = System.in.read();
			} catch (IOException e) {
				read = 0;
			}
			if (read >= 0 && read != '\r' && read != '\n') {
				builder.append((char) read);
			}
		} while (read != '\r' && read != '\n' && read > 0
				&& !builder.toString().equalsIgnoreCase("end"));

		Logger.printLine(builder.toString());
		this.parseLine(builder.toString());
	}

	private void parseLine(String line) {
		String[] words = line.split(" ");
		String keyWord = words[0];
		if (keyWord.equalsIgnoreCase("loadtime"))
			MyBot.setLoadTime(Integer.parseInt(words[1]));
		else if (keyWord.equalsIgnoreCase("turntime"))
			MyBot.setTurnTime(Integer.parseInt(words[1]));
		else if (keyWord.equalsIgnoreCase("rows"))
			Field.setRows(Integer.parseInt(words[1]));
		else if (keyWord.equalsIgnoreCase("cols"))
			Field.setCols(Integer.parseInt(words[1]));
		else if (keyWord.equalsIgnoreCase("turns"))
			MyBot.setTurns(Integer.parseInt(words[1]));
		else if (keyWord.equalsIgnoreCase("viewradius2"))
			MyBot.setViewRadius2(Integer.parseInt(words[1]));
		else if (keyWord.equalsIgnoreCase("attackradius2"))
			MyBot.setAttackRadius2(Integer.parseInt(words[1]));
		else if (keyWord.equalsIgnoreCase("spawnradius2"))
			MyBot.setSpawnRadius2(Integer.parseInt(words[1]));
		else if (keyWord.equalsIgnoreCase("ready"))
			MyBot.load();
		else if (keyWord.equalsIgnoreCase("w"))
			Field.setWater(Integer.parseInt(words[2]), Integer
					.parseInt(words[1]));
		else if (keyWord.equalsIgnoreCase("f"))
			MyBot.setFood(Integer.parseInt(words[2]), Integer
					.parseInt(words[1]));
		else if (keyWord.equalsIgnoreCase("a"))
			MyBot.setAnt(Integer.parseInt(words[2]),
					Integer.parseInt(words[1]), Integer.parseInt(words[3]));
		else if (keyWord.equalsIgnoreCase("h"))
			MyBot.setHill(Integer.parseInt(words[2]), Integer
					.parseInt(words[1]), Integer.parseInt(words[3]));
		else if (keyWord.equalsIgnoreCase("d"))
			MyBot.antDead(Integer.parseInt(words[2]), Integer
					.parseInt(words[1]), Integer.parseInt(words[3]));
		else if (keyWord.equalsIgnoreCase("go"))
			MyBot.doTurn();
		else if (keyWord.equalsIgnoreCase("end")
				|| keyWord.equalsIgnoreCase(""))
			MyBot.end();
	}
}
