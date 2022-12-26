static void monitor(int event) {
    if (__RV_cat != 2) {
        int i,j, old, s; 
        event--; 
        __RV_cat = 1; 
        /* currently ignoring GLR */
        __RV_stack *stack = __RV_get(__RV_stacks_inst, 0); 
        while(stack->current_index >= 0){
            s = __RV_peek(stack);
            if (s >= 0 && __RV_at[s][event][0][0] != 0) { 
                /* not in an error state and something to do? */
                switch (__RV_at[s][event][0][0]) { 
                    case 1:/* Shift */
                        __RV_push(stack, __RV_at[s][event][0][1]); 
                        if (__RV_acc[__RV_at[s][event][0][1]]) __RV_cat = 0; 
                        return;
                    case 2: /* Reduce */ 
                        __RV_pop_n(stack, __RV_at[s][event][0][2]); 
                        old = __RV_peek(stack);
                        __RV_push(stack, __RV_gt[old][__RV_at[s][event][0][1]]); 
                        break; 
                } 
            } 
            else {
                __RV_cat = 2;
                return;
            }
        }
    }
}