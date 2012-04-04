package javamoprt.map;

import javamoprt.MOPMonitor;
import javamoprt.MOPSet;
import javamoprt.map.hashentry.MOPHashDualEntry;
import javamoprt.ref.MOPWeakReference;

public class MOPMapOfSetMon extends MOPAbstractMapDuo {

	public MOPMapOfSetMon(int idnum) {
		super();
		this.idnum = idnum;
	}

	@Override
	public Object getSet(MOPWeakReference key) {
		return get_1(key);
	}

	@Override
	public Object getNode(MOPWeakReference key) {
		return get_2(key);
	}

	@Override
	public boolean putSet(MOPWeakReference key, Object value) {
		return put_1(key, value);
	}

	@Override
	public boolean putNode(MOPWeakReference key, Object value) {
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

				MOPSet set = (MOPSet) entry.value1;
				MOPMonitor monitor = (MOPMonitor) entry.value2;

				if (set != null)
					set.endObjectAndClean(idnum);
				if (monitor != null)
					monitor.endObject(idnum);

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

				MOPSet set = (MOPSet) entry.value1;
				MOPMonitor monitor = (MOPMonitor) entry.value2;

				if (entry.key.get() == null) {
					if (previous == null) {
						data[cleancursor] = entry.next;
					} else {
						previous.next = entry.next;
					}

					if (set != null)
						set.endObjectAndClean(idnum);
					if (monitor != null && !monitor.MOP_terminated)
						monitor.endObject(idnum);

					entry.next = null;
					this.deletedMappings++;
				} else {
					if (set != null && set != lastValue1 && set.size == 0)
						entry.value1 = null;
					if (monitor != null && monitor.MOP_terminated)
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

				MOPSet set = (MOPSet) entry.value1;
				MOPMonitor monitor = (MOPMonitor) entry.value2;

				if (entry.key.get() == null) {
					if (previous == null) {
						data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					if (set != null)
						set.endObjectAndClean(idnum);
					if (monitor != null && !monitor.MOP_terminated)
						monitor.endObject(idnum);

					entry.next = null;
					this.deletedMappings++;
				} else {
					if (set != null && set != lastValue1 && set.size() == 0)
						entry.value1 = null;
					if (monitor != null && monitor.MOP_terminated)
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
