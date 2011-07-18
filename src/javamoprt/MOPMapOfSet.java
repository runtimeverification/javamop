package javamoprt;

public class MOPMapOfSet extends MOPMap<MOPSet> {

	public MOPMapOfSet(int idnum) {
		super();
		this.idnum = idnum;
	}

	final protected void endObject(int idnum) {
		this.isDeleted = true;
		for (int i = data.length - 1; i >= 0; i--) {
			MOPHashEntry entry = data[i];
			data[i] = null;
			while (entry != null) {
				MOPHashEntry next = entry.next;
				MOPSet set = (MOPSet) entry.getValue();
				set.endObjectAndClean(idnum);
				entry.next = null;
				entry = next;
			}
		}

		this.deletedMappings = this.addedMappings;
	}

	protected void cleanuponeiter() {
		// boolean success = false;
		int skipnum = 0;

		if (cleancursor < 0)
			cleancursor = data.length - 1;

		while (cleancursor >= 0 && skipnum < cleanup_piece) {
			MOPHashEntry previous = null;
			MOPHashEntry entry = data[cleancursor];
			if (entry != null) {
				do {
					MOPHashEntry next = entry.next;
					MOPSet set = (MOPSet) entry.getValue();
					if (entry.key.get() == null) {
						if (previous == null) {
							data[cleancursor] = entry.next;
						} else {
							previous.next = entry.next;
						}
						// set cleanup
						set.endObjectAndClean(idnum);

						entry.next = null;
						this.deletedMappings++;
						// success = true;
						// } else if(set != lastValue && !set.alive()){
					} else if (set != lastValue && set.size() == 0) {
						if (previous == null) {
							data[cleancursor] = entry.next;
						} else {
							previous.next = entry.next;
						}
						entry.next = null;
						this.deletedMappings++;
						// success = true;
					} else {
						previous = entry;
					}
					entry = next;
				} while (entry != null);

				// if(success)
				// MOPStat.success_cleanup_oneiter++;
				// else
				// MOPStat.fail_cleanup_oneiter++;

				// cleancursor--;
				// return;
			}
			cleancursor--;
			skipnum++;
		}
		// if(success)
		// MOPStat.success_cleanup_oneiter++;
		// else
		// MOPStat.fail_cleanup_oneiter++;
	}

	protected void cleanupiter() {
		// boolean success = false;

		for (int i = data.length - 1; i >= 0; i--) {
			MOPHashEntry entry = data[i];
			MOPHashEntry previous = null;
			while (entry != null) {
				MOPHashEntry next = entry.next;
				MOPSet set = (MOPSet) entry.getValue();
				if (entry.key.get() == null) {
					if (previous == null) {
						data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}
					// set cleanup
					set.endObjectAndClean(idnum);

					entry.next = null;
					this.deletedMappings++;

					// success = true;
					// } else if(set != lastValue && !set.alive()){
				} else if (set != lastValue && set.size() == 0) {
					if (previous == null) {
						data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}
					entry.next = null;
					this.deletedMappings++;

					// success = true;
				} else {
					previous = entry;
				}
				entry = next;
			}
		}

		// if(success)
		// MOPStat.success_cleanup++;
		// else
		// MOPStat.fail_cleanup++;

	}
}
