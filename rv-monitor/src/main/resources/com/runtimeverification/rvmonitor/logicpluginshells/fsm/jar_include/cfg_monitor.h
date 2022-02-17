typedef struct tag_mon {
  void *key;
  __RV_monitor *monitor;
} __RV_tag_monitor;

typedef struct list{
  int current_index;
  int length;
  __RV_tag_monitor **data;
} __RV_list;

static __RV_list *list = NULL;
static void __RV_delete_RV_list(__RV_list *list){
  if(list == NULL) return;
  free(list->data);
  free(list);
}

static __RV_list *__RV_new_RV_list(int size){
  __RV_list *ret = (__RV_list *) malloc(sizeof(__RV_list));
  ret->current_index = 0;
  ret->length = size;
  ret->data = (__RV_tag_monitor **) malloc(sizeof(__RV_tag_monitor *) * size);
  return ret; 
}

static __RV_tag_monitor  *__RV_new_RV_tag_monitor(void *key, int state){
  __RV_monitor *mon = (__RV_monitor *) malloc(sizeof(__RV_monitor));
  __RV_tag_monitor *ret = (__RV_tag_monitor *) malloc(sizeof(__RV_tag_monitor));
  
  mon->__RVC_state = state;
  ret->monitor = mon;
  ret->key = key;
  return ret;
}

static void __RV_append(__RV_list *list, __RV_tag_monitor *elem){
  if(list->current_index >= list->length) {
    int i;
    __RV_tag_monitor **tmp 
       = (__RV_tag_monitor **) malloc(sizeof(__RV_tag_monitor *) * list->length * 2);
    for(i = 0; i < list->length; ++i){
      tmp[i] = list->data[i];
    } 
    list->length *= 2;
    free(list->data);
    list->data = tmp;
  } 
  list->data[(list->current_index)++] = elem;
}

static __RV_monitor *__RV_find(__RV_list *list, void *tag){
  int i;
  for(i = 0; i < list->current_index; ++i){
    if(list->data[i]->key == tag){
      return list->data[i]->monitor;
    }
  }
  return NULL;
}
;