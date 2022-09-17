<template>
  <v-card elevation="0" class="pa-2">
    <v-btn elevation="0" class="top-btn" color="warma-yellow" @click.stop="importDialog.open = true">
      <span class="text--warma-dark">导入文件夹</span>
    </v-btn>
    <v-card class="bottom-list" outlined>
      <v-container fluid class="source-list">
        <v-treeview
            style="user-select: none"
            :items="tree"
            activatable
            hoverable
            dense
            @update:active="onSelect"
            open-on-click
            active-class="animate__animated animate__pulse"
            :active.sync="activeItems"
        >
          <template v-slot:prepend="{ item, open }">
            <v-icon v-if="item.media == null">
              {{ open ? 'mdi-folder-open' : 'mdi-folder' }}
            </v-icon>
            <v-icon v-else>
              mdi-music
            </v-icon>
          </template>
          <template v-slot:append="{ item, open }">
            <div v-if="item.media == null && open" @click.stop="open = true">
              <v-btn icon small @click.stop="openAliasDialog(item.source)">
                <v-icon>mdi-pencil-outline</v-icon>
              </v-btn>
              <v-btn icon small class="ml-2" @click.stop="openDeleteDialog(item.source)">
                <v-icon>mdi-close</v-icon>
              </v-btn>
            </div>
          </template>
        </v-treeview>
      </v-container>
    </v-card>
    <import-source-popup v-model="importDialog.open" @created="reload"/>
    <v-overlay :value="isLoading">
      <v-progress-circular
          indeterminate
      ></v-progress-circular>
    </v-overlay>
    <!--改名确认-->
    <v-dialog v-model="aliasDialog.open" width="300px" persistent>
      <v-card tile outlined class="border--warma-dark bold-border" :loading="aliasDialog.isLoading">
        <v-card-title class="error text--warma-dark">
          修改别名
        </v-card-title>
        <v-container fluid class="pa-3">
          <v-col cols="12" v-if="aliasDialog.source != null">
            <v-text-field
                label="别名"
                dense
                messages="可以不写"
                v-model="aliasDialog.source.alias"
            >
            </v-text-field>
          </v-col>
        </v-container>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="warma-dark" outlined @click="aliasDialog.open = false; aliasDialog.source = null"
                 :disabled="aliasDialog.isLoading">
            取消
          </v-btn>
          <v-btn color="primary" outlined @click.stop="alias" :disabled="aliasDialog.isLoading">
            改名
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!--删除确认-->
    <v-dialog v-model="deleteConfirm.open" width="300px" persistent>
      <v-card tile outlined class="border--warma-dark bold-border" :loading="deleteConfirm.isLoading">
        <v-card-title class="error text--warma-dark">
          确认删除
        </v-card-title>
        <v-container fluid class="pa-3" v-if="deleteConfirm.source != null">
          <span>确认删除文件夹：{{ deleteConfirm.source.alias ?? deleteConfirm.source.filepath }}?</span>
        </v-container>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="warma-dark" outlined @click="deleteConfirm.open = false; deleteConfirm.source = null"
                 :disabled="deleteConfirm.isLoading">
            取消
          </v-btn>
          <v-btn color="error" outlined @click.stop="deleteSource" :disabled="deleteConfirm.isLoading">
            删除
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script lang="ts">
import {Component, Vue} from "vue-property-decorator";
import ImportSourcePopup from "@/components/ImportSourcePopup.vue";
import {Source} from "@/models/source";
import {Message} from "@/components/MessageToast.vue";
import {MediaFile} from "@/models/media-file";
import {$Vue} from "@/main";

export interface SourceTreeItem {
  id: number | string
  name: string
  media: MediaFile | null,
  source: Source | null,
  children: SourceTreeItem[] | null
}

@Component({
  components: {ImportSourcePopup}
})
export default class SourceList extends Vue {
  isLoading = false;
  tree: SourceTreeItem[] = [];
  activeItems: (number | string)[] = [];

  importDialog = {
    open: false
  }

  aliasDialog = {
    open: false,
    source: null as Source | null,
    isLoading: false
  }

  deleteConfirm = {
    open: false,
    source: null as Source | null,
    isLoading: false
  }

  // noinspection JSUnusedGlobalSymbols
  mounted() {
    this.isLoading = true;
    this.reload();
    this.$store.watch(state => state.sources, () => this.reload());
    $Vue.$on('tidied', (file: MediaFile) => {
      this.selectNext();
      setTimeout(() => {
        const rootIdx = this.tree.findIndex(it => file.sourceId === Number(it.id));
        const root = this.tree[rootIdx];
        const strIdx = `${file.sourceId}_${file.id}`;
        const leafIdx = root.children?.findIndex(it => strIdx === String(it.id));
        root.children?.splice(leafIdx!, 1);
        if (root.children?.length === 0) this.tree.splice(rootIdx, 1);
      });
    });
    $Vue.$on('selectNext', () => this.selectNext());
    $Vue.$on('selectPrev', () => this.selectPrev());
  }

  reload() {
    this.isLoading = true;
    this.tree = [];
    this.$store.state.sources.forEach((source: Source) => {
      const root = {
        id: source.id,
        name: source.alias.length == 0 ? source.filepath : source.alias,
        source: source,
        children: new Array<SourceTreeItem>()
      } as SourceTreeItem
      (this.$store.state.untidyFiles.get(source.id) ?? []).forEach((file: MediaFile) => {
        root.children?.push({
          id: `${source.id}_${file.id}`,
          name: file.filename,
          media: file
        } as SourceTreeItem);
      });
      if ((root.children?.length ?? 0) > 0) this.tree.push(root);
    });
    this.isLoading = false;
  }

  onSelect(fileIds: (number | string)[]) {
    if (fileIds.length === 0) return;
    if (typeof fileIds[0] === 'number') {
      this.activeItems = [];
    } else {
      const [sourceId, fileId] = fileIds[0].split('_').map(it => Number(it));
      const selected = this.$store.state.untidyFiles.get(sourceId)!.find((file: MediaFile) => file.id === fileId);
      if (this.activeItems[0] !== fileIds[0]) this.activeItems = [fileIds[0]];
      this.$emit('select', selected);
    }
  }

  selectNext() {
    if (this.tree.length === 0) return
    if (this.activeItems.length === 0) {
      this.onSelect([this.tree[0]!.children![0].id])
    } else {
      const strIdx = this.activeItems[0] as string;
      const sourceId = Number(strIdx.split('_')[0]);
      const currentSourceIdx = this.tree.findIndex(it => it.id === sourceId);
      const mediaList = this.tree[currentSourceIdx].children ?? [];
      const nextIdx = mediaList.findIndex(it => it.id === strIdx) + 1;
      // 如果这下首是当前源最后一首，从下一个源找
      if (nextIdx >= mediaList.length) {
        // 如果这已经是最后一个源，退出
        if (currentSourceIdx + 1 >= this.tree.length) return;
        else {
          // 否则取下一个源第一首
          this.onSelect([this.tree[currentSourceIdx + 1].children![0].id]);
        }
      } else {
        // 如果不是当前源最后一首，直接下一首
        this.onSelect([mediaList[nextIdx].id]);
      }
    }
  }

  selectPrev() {
    if (this.tree.length === 0) return
    if (this.activeItems.length === 0) {
      this.onSelect([this.tree[0]!.children![0].id])
    } else {
      const strIdx = this.activeItems[0] as string;
      const sourceId = Number(strIdx.split('_')[0]);
      const currentSourceIdx = this.tree.findIndex(it => it.id === sourceId);
      const mediaList = this.tree[currentSourceIdx].children ?? [];
      const nextIdx = mediaList.findIndex(it => it.id === strIdx) - 1;
      // 如果这已经是当前源第一首，从上一个源最后一首找
      if (nextIdx < 0) {
        // 如果这已经是第一个源，退出
        if (currentSourceIdx - 1 < 0) return;
        else {
          // 否则取上一个源最后一首
          const lastSource = this.tree[currentSourceIdx - 1].children!;
          this.onSelect([lastSource[lastSource.length - 1].id]);
        }
      } else {
        // 如果不是当前源第一首，直接上一首
        this.onSelect([mediaList[nextIdx].id]);
      }
    }
  }

  alias() {
    const source = this.aliasDialog.source;
    if (source == null) return;
    this.aliasDialog.isLoading = true;
    this.$http.put(`/source/alias/${source.id}?name=${source.alias}`)
        .then(async () => {
          await this.$store.dispatch('reloadSources');
          $Vue.$emit('message', {message: '更新完成'} as Message);
          this.aliasDialog.source = null;
          this.aliasDialog.open = false;
        })
        .finally(() => this.aliasDialog.isLoading = false);
  }

  async deleteSource() {
    try {
      this.deleteConfirm.isLoading = true;
      await this.$http.delete('/source', {data: [this.deleteConfirm.source?.id]})
      this.deleteConfirm.source = null;
      this.deleteConfirm.open = false;
      await this.$store.dispatch('reloadSources');
      $Vue.$emit('message', {message: '删除成功！'} as Message);
      this.activeItems = [];
      this.$emit('select', null);
    } finally {
      this.deleteConfirm.isLoading = false;
    }
  }

  openDeleteDialog(source: Source) {
    this.deleteConfirm.source = source;
    this.deleteConfirm.open = true;
  }

  openAliasDialog(source: Source) {
    this.aliasDialog.source = source;
    this.aliasDialog.open = true;
  }
}
</script>

<!--suppress CssUnresolvedCustomProperty -->
<style scoped>

.source-list {
  height: calc(100vh - 310px);
  overflow-y: scroll;
}

.top-btn {
  border: 3px solid var(--v-warma-dark-base) !important;
  border-bottom: none !important;
  border-radius: 2px 2px 0 0;
}

.bottom-list {
  border: 3px solid var(--v-warma-dark-base);
  border-radius: 0 0 2px 2px;
  height: calc(100% - 36px);
}

</style>
