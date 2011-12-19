import java.util.HashMap;
import java.util.LinkedList;

public abstract class Ant {
	private Field field;
	private static LinkedList<Offset> viewBorder;
	private static LinkedList<Offset> view;
	private static LinkedList<Offset> attackArea;
	private static LinkedList<Offset> attackBorder;
	private static LinkedList<Offset> attackBorder2;
	private static LinkedList<Offset> visitArea;
	private static LinkedList<Offset> visitBorder;
	private static HashMap<Offset, LinkedList<Offset>> attackOffsets;

	public Ant(Field field) {
		this.field = field;
		field.setAnt(this);
	}

	public static void initialize(int viewRadius2, int attackRadius2,
			int visitRadius2) {
		Ant.loadOffsets(viewRadius2, attackRadius2, visitRadius2);
	}

	private static void loadOffsets(int viewRadius2, int attackRadius2,
			int visitRadius2) {
		Ant.view = new LinkedList<Offset>();
		Ant.viewBorder = new LinkedList<Offset>();
		Ant.attackArea = new LinkedList<Offset>();
		Ant.attackBorder = new LinkedList<Offset>();
		Ant.attackBorder2 = new LinkedList<Offset>();
		Ant.visitArea = new LinkedList<Offset>();
		Ant.visitBorder = new LinkedList<Offset>();
		Ant.attackOffsets = new HashMap<Offset, LinkedList<Offset>>();

		LinkedList<Offset> newOffsets = new LinkedList<Offset>();
		newOffsets.add(new Offset(0, 0));

		while (!newOffsets.isEmpty() && newOffsets.size() < 80) {

			Offset nextOffset = newOffsets.getFirst();
			Ant.view.add(nextOffset);
			newOffsets.remove(nextOffset);
			for (int i = 0; i < 4; i++) {
				Offset directionOffset = Offset.getDirectionOffset(i);
				Offset foundOffset = new Offset(directionOffset.getX()
						+ nextOffset.getX(), directionOffset.getY()
						+ nextOffset.getY());
				if (!newOffsets.contains(foundOffset)
						&& !Ant.viewBorder.contains(foundOffset)
						&& !Ant.view.contains(foundOffset)) {
					if (foundOffset.getTwoLengthSquared() <= viewRadius2) {
						newOffsets.add(foundOffset);
					} else {
						Ant.viewBorder.add(foundOffset);
					}
				}
			}
		}

		newOffsets.clear();
		newOffsets.add(new Offset(0, 0));
		Ant.attackArea.add(new Offset(0, 0));
		while (!newOffsets.isEmpty()) {

			Offset nextOffset;
			nextOffset = newOffsets.pop();

			for (int i = 0; i < 4; i++) {
				Offset directionOffset = Offset.getDirectionOffset(i);
				Offset foundOffset = new Offset(directionOffset.getX()
						+ nextOffset.getX(), directionOffset.getY()
						+ nextOffset.getY());
				if (!Ant.attackArea.contains(foundOffset)
						&& foundOffset.getTwoLengthSquared() <= attackRadius2) {
					newOffsets.add(foundOffset);
					Ant.attackArea.add(foundOffset);
				} else if (foundOffset.getTwoLengthSquared() > attackRadius2) {
					if (!Ant.attackBorder.contains(foundOffset))
						Ant.attackBorder.add(foundOffset);
					LinkedList<Offset> ao;
					if (Ant.attackOffsets.containsKey(foundOffset)) {
						ao = Ant.attackOffsets.get(foundOffset);
					} else {
						ao = new LinkedList<Offset>();
						Ant.attackOffsets.put(foundOffset, ao);
					}
					ao.add(directionOffset);
				}
			}
		}

		for (Offset borderOffset : Ant.attackBorder) {
			for (int i = 0; i < 4; i++) {
				Offset directionOffset = Offset.getDirectionOffset(i);
				Offset foundOffset = new Offset(directionOffset.getX()
						+ borderOffset.getX(), directionOffset.getY()
						+ borderOffset.getY());
				if (!Ant.attackArea.contains(foundOffset)
						&& !Ant.attackBorder.contains(foundOffset)) {
					if (!Ant.attackBorder2.contains(foundOffset)) {
						Ant.attackBorder2.add(foundOffset);
					}
					LinkedList<Offset> ao;
					if (Ant.attackOffsets.containsKey(foundOffset)) {
						ao = Ant.attackOffsets.get(foundOffset);
					} else {
						ao = new LinkedList<Offset>();
						Ant.attackOffsets.put(foundOffset, ao);
					}
					ao.add(directionOffset);
				}
			}
		}

		newOffsets.clear();
		newOffsets.add(new Offset(0, 0));

		while (!newOffsets.isEmpty() && newOffsets.size() < 80) {

			Offset nextOffset = newOffsets.getFirst();
			Ant.visitArea.add(nextOffset);
			newOffsets.remove(nextOffset);

			for (int i = 0; i < 4; i++) {
				Offset directionOffset = Offset.getDirectionOffset(i);
				Offset foundOffset = new Offset(directionOffset.getX()
						+ nextOffset.getX(), directionOffset.getY()
						+ nextOffset.getY());
				if (!newOffsets.contains(foundOffset)
						&& !Ant.visitBorder.contains(foundOffset)
						&& !Ant.visitArea.contains(foundOffset)) {
					if (foundOffset.getTwoLengthSquared() <= visitRadius2) {
						newOffsets.add(foundOffset);
					} else {
						Ant.visitBorder.add(foundOffset);
					}
				}
			}
		}
	}

	public int getX() {
		return this.field.getX();
	}

	public int getY() {
		return this.field.getY();
	}

	public Field getField() {
		return this.field;
	}

	public abstract int getOwner();

	public LinkedList<Field> getViewBorder() {
		LinkedList<Field> fields = new LinkedList<Field>();
		for (Offset offset : Ant.viewBorder) {
			fields.add(offset.getField(this.field));
		}

		return fields;
	}

	public LinkedList<Field> getViewField() {
		LinkedList<Field> fields = new LinkedList<Field>();
		for (Offset offset : Ant.view) {
			fields.add(offset.getField(this.field));
		}

		return fields;
	}

	public LinkedList<Field> getAttackArea() {
		LinkedList<Field> fields = new LinkedList<Field>();
		for (Offset offset : Ant.attackArea) {
			fields.add(offset.getField(this.field));
		}

		return fields;
	}

	public LinkedList<Field> getAttackBorder() {
		LinkedList<Field> fields = this.field.getAttackBorder();
		if (fields == null) {
			fields = new LinkedList<Field>();
			for (Offset offset : Ant.attackBorder) {
				boolean found = false;
				if (!offset.getField(this.field).hasWater()) {
					for (Offset o : Ant.attackOffsets.get(offset)) {
						if (!o.getField(this.field).hasWater()) {
							found = true;
							break;
						} else if (!o.getInverse().getField(
								offset.getField(this.field)).hasWater()) {
							found = true;
							break;
						}
					}
				}
				if (found) {
					fields.add(offset.getField(this.field));
				}
			}
			this.field.setAttackBorder(fields);
		}

		return fields;
	}

	public LinkedList<Field> getAttackBorder2() {
		LinkedList<Field> fields = this.field.getAttackBorder2();
		if (fields == null) {
			fields = new LinkedList<Field>();
			for (Offset offset : Ant.attackBorder2) {
				boolean found = false;
				if (!offset.getField(this.field).hasWater()) {
					for (Offset o1 : Ant.attackOffsets.get(offset)) {
						if (!o1.getField(this.field).hasWater()) {
							for (Offset o2 : Ant.attackOffsets.get(new Offset(
									offset.getX() - o1.getX(), offset.getY()
											- o1.getY()))) {
								if (!o2.getInverse().getField(
										offset.getField(this.field)).hasWater()) {
									found = true;
									break;
								}
							}
						}
						if (!found
								&& !o1.getInverse().getField(
										offset.getField(this.field)).hasWater()) {
							for (Offset o2 : Ant.attackOffsets.get(new Offset(
									offset.getX() - o1.getX(), offset.getY()
											- o1.getY()))) {
								if (!o2.getField(this.field).hasWater()) {
									found = true;
									break;
								}
							}
						}
					}
				}
				if (found) {
					fields.add(offset.getField(this.field));
				}
			}
			this.field.setAttackBorder2(fields);
		}

		return fields;
	}

	public LinkedList<Ant> getConflictAnts() {
		LinkedList<Ant> result = new LinkedList<Ant>();
		for (Field field : this.getAttackBorder()) {
			Ant ant = field.getAnt();
			if (ant != null && (ant.getOwner() == 0 ^ this.getOwner() == 0)) {
				result.add(ant);
			}
		}
		for (Field field : this.getAttackBorder2()) {
			Ant ant = field.getAnt();
			if (ant != null && (ant.getOwner() == 0 ^ this.getOwner() == 0)) {
				result.add(ant);
			}
		}
		return result;
	}

	public LinkedList<Field> getVisitArea() {
		LinkedList<Field> fields = new LinkedList<Field>();
		for (Offset offset : Ant.visitArea) {
			fields.add(offset.getField(this.field));
		}

		return fields;
	}

	public LinkedList<Field> getVisitBorder() {
		LinkedList<Field> fields = new LinkedList<Field>();
		for (Offset offset : Ant.visitBorder) {
			fields.add(offset.getField(this.field));
		}

		return fields;
	}

	@Override
	public String toString() {
		return "ant at " + this.field.getX() + "," + this.field.getY();
	}
}
