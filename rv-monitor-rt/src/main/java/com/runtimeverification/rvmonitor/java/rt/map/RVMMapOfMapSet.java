package com.runtimeverification.rvmonitor.java.rt.map;

import com.runtimeverification.rvmonitor.java.rt.RVMSet;
import com.runtimeverification.rvmonitor.java.rt.map.hashentry.RVMHashDualEntry;
import com.runtimeverification.rvmonitor.java.rt.ref.RVMWeakReference;

public class RVMMapOfMapSet extends RVMAbstractMapDuo {

	public RVMMapOfMapSet(int idnum) {
		super();
		this.idnum = idnum;
	}

	@Override
	public Object getMap(RVMWeakReference key) {
		return get_1(key);
	}

	@Override
	public Object getSet(RVMWeakReference key) {
		return get_2(key);
	}

	@Override
	public boolean putMap(RVMWeakReference key, Object value) {
		return put_1(key, value);
	}

	@Override
	public boolean putSet(RVMWeakReference key, Object value) {
		return put_2(key, value);
	}

	/* ************************************************************************************ */

	@Override
	protected void endObject(int idnum) {
		this.isDeleted = true;
		for (int i = data.length - 1; i >= 0; i--) {
			RVMHashDualEntry entry = data[i];
			data[i] = null;
			while (entry != null) {
				RVMHashDualEntry next = entry.next;

				RVMAbstractMap map = (RVMAbstractMap) entry.value1;
				RVMSet set = (RVMSet) entry.value2;

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
			RVMHashDualEntry previous = null;
			RVMHashDualEntry entry = data[cleancursor];

			while (entry != null) {
				RVMHashDualEntry next = entry.next;

				RVMAbstractMap map = (RVMAbstractMap) entry.value1;
				RVMSet set = (RVMSet) entry.value2;

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
			RVMHashDualEntry entry = data[i];
			RVMHashDualEntry previous = null;
			while (entry != null) {
				RVMHashDualEntry next = entry.next;

				RVMAbstractMap map = (RVMAbstractMap) entry.value1;
				RVMSet set = (RVMSet) entry.value2;

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
