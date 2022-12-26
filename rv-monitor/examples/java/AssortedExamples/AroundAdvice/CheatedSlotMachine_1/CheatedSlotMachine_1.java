package CheatedSlotMachine_1;

import rvm.CheatedSlotMachineRuntimeMonitor;

import casino.SlotMachine;

public class CheatedSlotMachine_1 {
	public static void main(String[] args){
		SlotMachine machine = new SlotMachine();
		for (int i = 0; i < 10 ; ++i) {
			System.out.println("Round " + i);
			machine.insertCoin();
			rvm.CheatedSlotMachineRuntimeMonitor.insert_coinEvent(machine);
			machine.push();
			rvm.CheatedSlotMachineRuntimeMonitor.push_buttonEvent(machine);
			rvm.CheatedSlotMachineRuntimeMonitor.resultEvent(machine);
			if (!CheatedSlotMachineRuntimeMonitor.skipEvent)
				System.out.println(machine.getResult());
		}
	}
}
