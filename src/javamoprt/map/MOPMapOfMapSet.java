package javamoprt.map;

import javamoprt.MOPSet;
import javamoprt.map.hashentry.MOPHashDualEntry;
import javamoprt.map.hashentry.MOPHashEntry;
import javamoprt.ref.MOPWeakReference;

public class MOPMapOfMapSet extends MOPAbstractMapDuo {

	public MOPMapOfMapSet(int idnum) {
		super();
		this.idnum = idnum;
	}

	@Override
	public Object getMap(MOPWeakReference key) {
		return get_1(key);
	}

	@Override
	public Object getSet(MOPWeakReference key) {
		return get_2(key);
	}

	@Override
	public boolean putMap(MOPWeakReference key, Object value) {
		return put_1(key, value);
	}

	@Override
	public boolean putSet(MOPWeakReference key, Object value) {
		return put_2(key, value);
	}

	/* ************************************************************************************ */

	@Override
	protected void endObject(int idnum) {
		this.isDeleted = true;
		for (int i = data.length - 1; i >= 0; i--) {
			MOPHashDualEntry entry = data[i];
			data[i] = null;
			while (entry != null) {
				MOPHashDualEntry next = entry.next;

				MOPAbstractMap map = (MOPAbstractMap) entry.value1;
				MOPSet set = (MOPSet) entry.value2;

				if (map != null)
					map.endObject(idnum);
				if (set != null)
					set.endObjectAndClean(idnum);

				entry.next = null;
				entry = next;
			}
		}

		this.deletedMappings = this.addedMappings;
	}

	@Override
	protected void cleanupchunkiter() {
		if (cleancursor < 0)
			cleancursor = data.length - 1;

		for (int i = 0; i < cleanup_piece && cleancursor >= 0; i++) {
			MOPHashDualEntry previous = null;
			MOPHashDualEntry entry = data[cleancursor];

			while (entry != null) {
				MOPHashDualEntry next = entry.next;

				MOPAbstractMap map = (MOPAbstractMap) entry.value1;
				MOPSet set = (MOPSet) entry.value2;

				if (entry.key.get() == null) {
					if (previous == null) {
						data[cleancursor] = entry.next;
					} else {
						previous.next = entry.next;
					}

					if (map != null)
						map.endObject(idnum);
					if (set != null)
						set.endObjectAndClean(idnum);

					entry.next = null;
					this.deletedMappings++;
				} else {
					if (map != null && map != lastValue1 && map.size() == 0)
						entry.value1 = null;
					if (set != null && set != lastValue2 && set.size == 0)
						entry.value2 = null;

					if (entry.value1 == null && entry.value2 == null) {
						if (previous == null) {
							data[cleancursor] = entry.next;
						} else {
							previous.next = entry.next;
						}
						entry.next = null;
						this.deletedMappings++;
					} else {
						previous = entry;
					}
				}
				entry = next;
			}
			cleancursor--;
		}
	}

	@Override
	protected void cleanupiter() {
		for (int i = data.length - 1; i >= 0; i--) {
			MOPHashDualEntry entry = data[i];
			MOPHashDualEntry previous = null;
			while (entry != null) {
				MOPHashDualEntry next = entry.next;

				MOPAbstractMap map = (MOPAbstractMap) entry.value1;
				MOPSet set = (MOPSet) entry.value2;

				if (entry.key.get() == null) {
					if (previous == null) {
						data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					if (map != null)
						map.endObject(idnum);
					if (set != null)
						set.endObjectAndClean(idnum);

					entry.next = null;
					this.deletedMappings++;
				} else {
					if (map != null && map != lastValue1 && map.size() == 0)
						entry.value1 = null;
					if (set != null && set != lastValue2 && set.size() == 0)
						entry.value2 = null;

					if (entry.value1 == null && entry.value2 == null) {
						if (previous == null) {
							data[i] = entry.next;
						} else {
							previous.next = entry.next;
						}
						entry.next = null;
						this.deletedMappings++;
					} else {
						previous = entry;
					}
				}
				entry = next;
			}
		}
	}
}
