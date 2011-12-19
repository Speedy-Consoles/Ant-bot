import java.util.LinkedList;

public abstract class Strategy {

	private double timeRatio;

	public Strategy(double ratio) {
		this.timeRatio = ratio;
	}

	public abstract void controllAnts(LinkedList<MyAnt> freeAnts, long time);

	public double getTimeRatio() {
		return this.timeRatio;
	}

	@Override
	public abstract String toString();
}
