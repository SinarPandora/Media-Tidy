<template>
  <v-dialog
      persistent
      v-model="openDialog"
  >
    <template v-slot:activator="{ on, attrs }">
      <v-btn
          small
          icon
          color="warma-dark"
          class="ml-3 border--warma-dark"
          v-bind="attrs"
          v-on="on"
          @click.stop="load"
      >
        <v-icon>mdi-sort</v-icon>
      </v-btn>
    </template>
    <!--    persistent-->
    <v-card tile outlined class="border--warma-dark bold-border" :loading="isLoading">
      <v-card-title class="warma-yellow text--warma-dark">
        拖拽进行排序
      </v-card-title>
      <v-container fluid>
        <div class="list">
          <sortable
              v-for="(item, idx) in items"
              v-model="dragging"
              :key="idx"
              :index="idx"
              replace-direction="horizontal"
              class="dragging border--warma-dark bold-border"
              @sortend="sort($event)"
          >
            {{ item.name }}
          </sortable>
        </div>
      </v-container>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
            color="warma-dark"
            outlined
            @click="openDialog = false"
            :disabled="isLoading"
        >
          取消
        </v-btn>
        <v-btn
            color="primary"
            outlined
            @click="save"
            :disabled="isLoading"
        >
          保存
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import Sortable from 'vue-drag-sortable';
import {Component, Vue} from "vue-property-decorator";
import {Tag} from "@/models/tag";
import {$Vue} from "@/main";
import {Message} from "@/components/MessageToast.vue";

export interface SortableItem {
  id: number
  name: string
}

@Component({
  components: {Sortable}
})
export default class TagSortPopup extends Vue {
  openDialog = false;
  isLoading = false;
  items: SortableItem[] = [];
  dragging: SortableItem = {
    id: -1,
    name: ''
  };

  load() {
    this.isLoading = true;
    this.items = this.$store.state.tags
        .map((it: Tag) => ({id: it.id, name: it.name} as SortableItem));
    this.isLoading = false;
  }

  sort(e: { oldIndex: number, newIndex: number }) {
    const {oldIndex, newIndex} = e
    this.rearrange(oldIndex, newIndex)
  }

  rearrange(oldIndex: number, newIndex: number) {
    if (oldIndex > newIndex) {
      this.items.splice(newIndex, 0, this.items[oldIndex])
      this.items.splice(oldIndex + 1, 1)
    } else {
      this.items.splice(newIndex + 1, 0, this.items[oldIndex])
      this.items.splice(oldIndex, 1)
    }
  }

  async save() {
    this.isLoading = true;
    try {
      await this.$http.put('/tag/order', this.items.map(it => it.id));
      $Vue.$emit('message', {message: '保存成功！'} as Message);
      this.openDialog = false;
      setTimeout(() => {
        this.$store.dispatch('reloadTags');
      });
    } finally {
      this.isLoading = false;
    }
  }
}
</script>

<!--suppress CssUnresolvedCustomProperty -->
<style scoped>
.list {
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  overflow-y: scroll;
}

/* dragging item will be added with a `dragging` class */
/* so you can use this class to define the appearance of it */
.list > *.dragging {
  width: 10%;
  padding: 16px;
  margin: 1%;
  display: inline-block;
  box-sizing: border-box;
  border-radius: 2px;
  cursor: pointer;
  user-select: none;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  text-align: center;
  /*justify-content: center;*/
  /*align-items: center;*/
}

.dragging:active {
  background: var(--v-warma-yellow-base);
}

</style>
