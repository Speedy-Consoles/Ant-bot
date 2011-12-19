public class Offset {

	private int xOffset;
	private int yOffset;

	public Offset(int x, int y) {
		this.xOffset = x;
		this.yOffset = y;
	}

	public int getX() {
		return this.xOffset;
	}

	public int getY() {
		return this.yOffset;
	}

	public Field getField(Field field) {
		return Field.getField((field.getX() + this.xOffset + Field.getCols())
				% Field.getCols(), (field.getY() + this.yOffset + Field
				.getRows())
				% Field.getRows());
	}

	public static Offset getDirectionOffset(int direction) {
		if (direction <= 3 && direction >= 0) {
			int x = ((direction + 1) % 2) * (1 - direction);
			int y = (direction % 2) * (2 - direction);
			return new Offset(x, y);
		} else
			return new Offset(0, 0);
	}

	public int getDirection() {
		if (this.getOneLength() > 1) {
			Logger
					.printLine("ERROR: Requested direction of Offset longer than 1");
		}
		if (this.xOffset == 1)
			return 0;
		else if (this.yOffset == 1)
			return 1;
		else if (this.xOffset == -1)
			return 2;
		else if (this.yOffset == -1)
			return 3;
		else
			return 4;
	}

	public int getOneLength() {
		return Math.abs(this.xOffset) + Math.abs(this.yOffset);
	}

	public int getTwoLengthSquared() {
		return (int) (Math.pow(this.xOffset, 2) + Math.pow(this.yOffset, 2));
	}

	public Offset getInverse() {
		return new Offset(-this.xOffset, -this.yOffset);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + xOffset;
		result = prime * result + yOffset;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Offset other = (Offset) obj;
		if (xOffset != other.xOffset)
			return false;
		if (yOffset != other.yOffset)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.xOffset + "|" + this.yOffset;
	}
}
