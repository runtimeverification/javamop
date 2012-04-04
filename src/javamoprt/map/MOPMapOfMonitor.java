package javamoprt.map;

import javamoprt.MOPMonitor;
import javamoprt.map.hashentry.MOPHashEntry;
import javamoprt.ref.MOPWeakReference;

public class MOPMapOfMonitor extends MOPAbstractMapSolo {

	public MOPMapOfMonitor(int idnum) {
		super();
		this.idnum = idnum;
	}

	@Override
	public Object getNode(MOPWeakReference key) {
		return get_1(key);
	}

	@Override
	public boolean putNode(MOPWeakReference key, Object value) {
		return put_1(key, value);
	}

	/* ************************************************************************************ */

	@Override
	protected void endObject(int idnum) {
		this.isDeleted = true;
		for (int i = data.length - 1; i >= 0; i--) {
			MOPHashEntry entry = data[i];
			data[i] = null;
			while (entry != null) {
				MOPHashEntry next = entry.next;

				MOPMonitor monitor = (MOPMonitor) entry.value;
				if (!monitor.MOP_terminated)
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
			MOPHashEntry previous = null;
			MOPHashEntry entry = data[cleancursor];

			while (entry != null) {
				MOPHashEntry next = entry.next;

				MOPMonitor monitor = (MOPMonitor) entry.value;

				if (entry.key.get() == null) {
					if (previous == null) {
						data[cleancursor] = entry.next;
					} else {
						previous.next = entry.next;
					}

					if (!monitor.MOP_terminated)
						monitor.endObject(idnum);

					entry.next = null;
					this.deletedMappings++;
				} else if (monitor.MOP_terminated) {
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
				entry = next;
			}
			cleancursor--;
		}
	}

	@Override
	protected void cleanupiter() {
		for (int i = data.length - 1; i >= 0; i--) {
			MOPHashEntry entry = data[i];
			MOPHashEntry previous = null;
			while (entry != null) {
				MOPHashEntry next = entry.next;

				MOPMonitor monitor = (MOPMonitor) entry.value;

				if (entry.key.get() == null) {
					if (previous == null) {
						data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					if (!monitor.MOP_terminated)
						monitor.endObject(idnum);

					entry.next = null;
					this.deletedMappings++;
				} else if (monitor.MOP_terminated) {
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
				entry = next;
			}
		}
	}
}
